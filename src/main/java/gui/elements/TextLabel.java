package gui.elements;

import assets.Assets;
import gui.GUIElement;
import misc.Window;
import com.github.mathiewz.slick.Color;
import com.github.mathiewz.slick.Graphics;
import com.github.mathiewz.slick.TrueTypeFont;

import java.util.ArrayList;

public class TextLabel extends GUIElement {

    private String text;
    private Color color, backgroundColor;
    private boolean shadow, background;
    private int maxWidth, maxLines, actualWidth;
    private float lineHeight;
    private ArrayList<String> lines;

    public TextLabel(String text, int lineHeight, Color color, boolean shadow, boolean background) {
        this(text, lineHeight, Integer.MAX_VALUE, 16, color, shadow, background);
    }

    public TextLabel(String text, int lineHeight, int maxWidth, int maxLines, Color color, boolean shadow, boolean background) {
        this.text = text;
        this.color = color;
        this.maxWidth = maxWidth;
        this.maxLines = maxLines;
        this.lineHeight = lineHeight;
        this.shadow = shadow;
        this.background = background;
        this.backgroundColor = new Color(0, 0, 0, 0.75f);
        this.setBuffered(false);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    private ArrayList<String> computeLines(String text) {
        String[] words = text.replace("\n", " \n ").split(" +");
        ArrayList<String> rows = new ArrayList<String>();
        rows.add("");
        int longestRowSize = 0;
        int currentRowSize = 0;
        int currentRowIndex = 0;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.equals("\n")) { //forced new line
                currentRowIndex += 1;
                rows.add("");
                currentRowSize = 0;
                continue;
            }
            int size = (int)(getFont().getWidth(" "+word) / Window.getScale());
            if (currentRowSize + size < maxWidth) {
                rows.set(currentRowIndex, rows.get(currentRowIndex) + " " + word);
                currentRowSize += size;
            } else {
                i--;
                if (currentRowIndex + 1 < maxLines) {
                    currentRowIndex += 1;
                    currentRowSize = 0;
                    rows.add("");
                } else {
                    rows.set(currentRowIndex, rows.get(currentRowIndex) + "...");
                    break;
                }
            }
        }
        //compute actual width
        for (String row: rows) {
            int width = (int)(getFont().getWidth(row) / Window.getScale());
            if (width > longestRowSize) longestRowSize = width;
        }
        actualWidth = longestRowSize - 1;
        return rows;
    }

    private ArrayList<String> getLines() {
        if (lines == null) lines = computeLines(text);
        return lines;
    }

    public void setText(String newtext) {
        newtext = newtext.replaceAll("’", "'");
        if (!newtext.equals(text)) {
            lines = computeLines(newtext);
            text = newtext;
        }
    }

    private TrueTypeFont getFont() { return Assets.getFont(lineHeight * Window.getScale()); }

    @Override
    public int[] getDimensions() {
        return new int[]{
                actualWidth,
                getLines().size() * (int)lineHeight
        };
    }

    @Override
    public boolean onMouseMoved(int ogx, int ogy) {
        return false;
    }

    @Override
    public boolean onMouseRelease(int ogx, int ogy, int button) {
        return false;
    }

    @Override
    public boolean onMousePressed(int ogx, int ogy, int button) { return false; }

    @Override
    public boolean onMouseScroll(int direction) {
        return false;
    }

    @Override
    public boolean onKeyDown(int key) {
        return false;
    }

    @Override
    public boolean onKeyUp(int key) {
        return false;
    }

    @Override
    public void drawUnder(Graphics g) {
        if (!background || text.length() == 0) return;
        float[] coords = getOnscreenCoordinates();
        g.setColor(backgroundColor);
        g.fillRect(coords[0], coords[1], (getDimensions()[0] + 1) * Window.getScale(), (getDimensions()[1] + 2) * Window.getScale());
    }

    @Override
    public void drawOver(Graphics g) {
        float[] coords = getOnscreenCoordinates();
        g.setFont(Assets.getFont(lineHeight * Window.getScale()));
        for (int i = 0; i < getLines().size(); i++) {
            float x = (coords[0] + (background ? 1 : 0)) - Window.getScale();
            float y = coords[1] + (i * lineHeight * Window.getScale());
            if (shadow) {
                g.setColor(color.darker().darker());
                g.drawString(getLines().get(i), (int)x + 1, (int)y + 1);
            }
            g.setColor(color);
            g.drawString(getLines().get(i), (int)x, (int)y);
        }
    }

    @Override
    protected void drawBuffered(Graphics b, boolean mouseHovering, boolean mouseDown) {}

}
