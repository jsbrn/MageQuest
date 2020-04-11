package gui;

import gui.elements.Modal;
import gui.elements.PositionalTextLabel;
import gui.elements.SpeechBubble;
import gui.states.GameScreen;
import misc.Location;
import misc.MiscMath;
import misc.Window;
import org.newdawn.slick.Color;
import org.newdawn.slick.Game;
import org.newdawn.slick.Graphics;
import world.Camera;
import world.World;
import world.entities.Entity;
import world.events.Event;
import world.events.EventDispatcher;
import world.events.EventHandler;
import world.events.EventListener;
import world.events.event.*;

import java.util.ArrayList;
import java.util.Stack;

public class GUI {

    private ArrayList<GUIElement> elements;
    private Stack<Modal> modals;
    private SpeechBubble speechBubble;
    private float darkness;
    private int[] lastMousePosition;

    public GUI() {
        this.elements = new ArrayList<>();
        this.modals = new Stack<>();
        this.lastMousePosition = new int[]{0, 0};
    }

    public void setSpeechBubble() {
        speechBubble = new SpeechBubble();
        this.addElement(speechBubble, 0, -10, GUIAnchor.BOTTOM_MIDDLE);
        speechBubble.hide();

        EventDispatcher.register(new EventListener()
                .on(NPCSpeakEvent.class.toString(), new EventHandler() {
                    @Override
                    public void handle(Event e) {
                        NPCSpeakEvent cse = (NPCSpeakEvent)e;
                        if (cse.getPlayer().equals(World.getLocalPlayer())) {
                            speechBubble.setSpeaker(cse.getNPC());
                            speechBubble.setDialogue(cse.getDialogue());
                            speechBubble.show();
                        }
                    }
                })
                .on(ConversationEndedEvent.class.toString(), new EventHandler() {
                    @Override
                    public void handle(Event e) {
                        ConversationEndedEvent cse = (ConversationEndedEvent)e;
                        if (cse.getPlayer().equals(World.getLocalPlayer())) {
                            speechBubble.hide();
                        }
                    }
                })
        );
    }

    public SpeechBubble getSpeechBubble() {
        return speechBubble;
    }

    public boolean handleMouseMoved(int osx, int osy) {
        int ogx = (int)(osx / Window.getScale()), ogy = (int)(osy / Window.getScale());
        if (!modals.isEmpty()) return modals.peek().handleMouseMoved(ogx, ogy);
        for (int i = elements.size() - 1; i >= 0; i--) {
            GUIElement e = elements.get(i);
            if (e.handleMouseMoved(ogx, ogy)) return true;
        }
        //double[] wc = Camera.getWorldCoordinates(osx, osy, Window.getScale());
        //EventDispatcher.invoke(new MousePressedEvent(wc[0], wc[1]));
        return false;
    }

    public boolean handleMousePressed(int osx, int osy, int button) {
        int ogx = (int)(osx / Window.getScale()), ogy = (int)(osy / Window.getScale());
        if (!modals.isEmpty()) return modals.peek().handleMousePressed(ogx, ogy, button);
        for (int i = elements.size() - 1; i >= 0; i--) {
            GUIElement e = elements.get(i);
            if (e.handleMousePressed(ogx, ogy, button)) return true;
        }
        double[] wc = Camera.getWorldCoordinates(osx, osy, Window.getScale());
        EventDispatcher.invoke(new MousePressedEvent(wc[0], wc[1], button));
        return false;
    }

    public boolean handleMouseRelease(int osx, int osy, int button) {
        int ogx = (int)(osx / Window.getScale()), ogy = (int)(osy / Window.getScale());
        if (!modals.isEmpty()) return modals.peek().handleMouseRelease(ogx, ogy, button);
        for (int i = elements.size() - 1; i >= 0; i--) {
            GUIElement e = elements.get(i);
            if (e.handleMouseRelease(ogx, ogy, button)) return true;
        }
        double[] wc = Camera.getWorldCoordinates(osx, osy, Window.getScale());
        EventDispatcher.invoke(new MouseReleaseEvent(wc[0], wc[1], button));
        return false;
    }

    public boolean onKeyDown(int key) {
        if (!modals.isEmpty()) return modals.peek().handleKeyDown(key);
        for (int i = elements.size() - 1; i >= 0; i--) {
            GUIElement e = elements.get(i);
            if (e.handleKeyDown(key)) return true;
        }
        EventDispatcher.invoke(new KeyDownEvent(key));
        return false;
    }

    public boolean onKeyUp(int key) {
        if (!modals.isEmpty()) return modals.peek().handleKeyUp(key);
        for (int i = elements.size() - 1; i >= 0; i--) {
            GUIElement e = elements.get(i);
            if (e.handleKeyUp(key)) return true;
        }
        EventDispatcher.invoke(new KeyUpEvent(key));
        return false;
    }

    public void stackModal(Modal element) {
        if (element != null) {
            modals.add(element);
            element.show();
        }
    }

    public void popModal() {
        if (!modals.isEmpty()) modals.peek().hide();
        modals.pop();
        if (!modals.isEmpty()) modals.peek().onShow();
    }

    public void setFade(float alpha) {
        darkness = alpha;
    }

    public void addElement(GUIElement element, int ogx, int ogy, GUIAnchor anchor) {
        element.setOffset(ogx, ogy);
        element.setAnchor(anchor);
        element.setGUI(this);
        this.elements.add(element);
    }

    public void removeElement(GUIElement element) {
        if (elements.remove(element)) element.onHide();
    }

    public void floatText(Location location, String text, Color color, int speed, int lifespan) {
        if (location.getRegion().equals(World.getRegion()))
            GameScreen.getGUI().addElement(
                    new PositionalTextLabel(location, text, color, MiscMath.random(-45, 45), speed, lifespan),
                    -Integer.MAX_VALUE,
                    -Integer.MAX_VALUE,
                    GUIAnchor.TOP_LEFT);
    }

    public void draw(Graphics g, boolean debug) {

        int mouseGX = (int)(GameScreen.getInput().getMouseX() / Window.getScale()), mouseGY = (int)(GameScreen.getInput().getMouseY() / Window.getScale());
        if (lastMousePosition[0] != mouseGX || lastMousePosition[1] != mouseGY) {
            handleMouseMoved(GameScreen.getInput().getMouseX(), GameScreen.getInput().getMouseY());
            lastMousePosition[0] = mouseGX;
            lastMousePosition[1] = mouseGY;
        }

        darkness = (float)MiscMath.tween(1f, darkness, 0f, 1f, 0.6f);
        if (darkness > 0) {
            g.setColor(new Color(0, 0, 0, darkness));
            g.fillRect(0, 0, Window.getWidth(), Window.getHeight());
            g.setColor(Color.white);
        }

        for (int i = 0; i < elements.size(); i++) {
            if (i >= elements.size()) break;
            GUIElement element = elements.get(i);
            if (element.isActive() && !element.equals(modals)) {
                element.draw(g);
                if (debug) element.drawDebug(g);
            }
        }

        for (Modal modal: modals) {
            g.setColor(new Color(0, 0, 0, 0.5f));
            g.fillRect(0, 0, Window.getWidth(), Window.getHeight());
            modal.draw(g);
            if (debug) modal.drawDebug(g);
        }

    }

}
