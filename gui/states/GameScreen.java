package gui.states;

import assets.Assets;
import assets.definitions.Definitions;
import gui.GUI;
import gui.GUIAnchor;
import gui.elements.*;
import gui.menus.Journal;
import gui.menus.SpellcraftingMenu;
import misc.Location;
import misc.MiscMath;
import misc.Window;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import world.Camera;
import world.Chunk;
import world.Region;
import world.World;
import world.entities.Entity;
import world.entities.types.humanoids.Zombie;
import world.entities.types.humanoids.npcs.LostCivilian;
import world.generators.region.DungeonGenerator;

import java.util.ArrayList;
import java.util.Random;

public class GameScreen extends BasicGameState {

    static StateBasedGame game;
    private static Input input;
    private static boolean initialized, paused;
    private Graphics graphics;

    private static GUI gui;
    private static Modal spellbook;
    private static boolean debugMode, showTopLayer;

    private static MiniMap miniMap;
    private static TextLabel deathMessage;

    public GameScreen(int state) {
        this.initialized = false;
    }

    @Override
    public int getID() {
        return Assets.GAME_SCREEN;
    }

    public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
        if (initialized) return;
        showTopLayer = true;
        game = sbg;
        Assets.load();
        Definitions.load();

        initialized = true;
    }

    public static void initializeGUI() {
        gui = new GUI();
        SpellcraftingMenu spellcasting = new SpellcraftingMenu(World.getLocalPlayer());
        spellbook = new Journal(World.getLocalPlayer(), spellcasting);

        gui.addElement(new CameraViewport(), 0, 0, GUIAnchor.TOP_LEFT);
        gui.addElement(new Statusbar(World.getLocalPlayer()), 2, 2, GUIAnchor.TOP_LEFT);
        gui.addElement(new Hotbar(World.getLocalPlayer()), 2, 38, GUIAnchor.TOP_LEFT);
        gui.addElement(new Button(null, 16, 16, "spellbook.png", false) {
            @Override
            public boolean onKeyDown(int key) {
                if (key == Input.KEY_TAB) {
                    gui.stackModal(spellbook);
                    World.setPaused(true);
                    return true;
                }
                return false;
            }
            @Override
            public boolean onClick(int button) {
                gui.stackModal(spellbook);
                World.setPaused(true);
                return true;
            }
        }, 4, 94, GUIAnchor.TOP_LEFT);

        miniMap = new MiniMap();
        gui.addElement(miniMap, -2, 2, GUIAnchor.TOP_RIGHT);

        deathMessage = new TextLabel("Press R to continue", 4, Color.white, true);
        gui.addElement(deathMessage, 0, 16, GUIAnchor.CENTER);

        gui.addElement(spellbook, 0, 0, GUIAnchor.CENTER);
        gui.addElement(spellcasting, 0, 0, GUIAnchor.CENTER);
        spellbook.hide();
        spellcasting.hide();

        gui.setSpeechBubble();
    }

    public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
        graphics = g;
        input = gc.getInput();
        g.setFont(Assets.getFont(14));
        if (World.getLocalPlayer() == null) return;
        gui.draw(g);

        g.setFont(Assets.getFont(14));

    }

    public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {

        World.setTimeMultiplier(gc.getInput().isKeyDown(Input.KEY_LSHIFT) ? 0.5 : 1);
        MiscMath.DELTA_TIME = (int)(delta * World.getTimeMultiplier());
        input = gc.getInput();

        if (World.getLocalPlayer().isDead()) deathMessage.show(); else deathMessage.hide();
        if (!paused) World.update();
        if (gc.getInput().isKeyDown(Input.KEY_ESCAPE)) sbg.enterState(Assets.MAIN_MENU_SCREEN);

    }

    @Override
    public void keyReleased(int key, char c) {

        if (key == Input.KEY_Q)
            GameScreen.getGUI().floatText(World.getLocalPlayer().getLocation(), "-1", Color.red, 4, 500, 0, true);
        if (key == Input.KEY_F5) miniMap.setRegion(new Region("test_dungeon", 16, new DungeonGenerator(1, 16)));
        if (key == Input.KEY_F6) miniMap.setRegion(null);

        if (key == Input.KEY_F11) {
            try {
                Window.toggleFullScreen();
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

        gui.onKeyUp(key);

    }

    @Override
    public void mousePressed(int button, int x, int y) {
        gui.handleMousePressed(x, y, button);
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        gui.handleMouseRelease(x, y, button);
    }

    @Override
    public void keyPressed(int key, char c) {
        gui.onKeyDown(key);
        if (key == Input.KEY_F2)
            Window.takeScreenshot(graphics);
    }

    @Override
    public void mouseWheelMoved(int newValue) {
        gui.handleMouseScroll(newValue > 0 ? -1 : 1);
    }

    public static Input getInput() { return input; }
    public static GUI getGUI() {
        return gui;
    }
    public static void setPaused(boolean p) { paused = p; }

}
