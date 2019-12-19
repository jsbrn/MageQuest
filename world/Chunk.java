package world;

import assets.Assets;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import world.generators.chunk.ChunkGenerator;
import world.generators.chunk.ChunkType;

public class Chunk {

    public static final int TILE_SIZE = 16;
    public static final int CHUNK_SIZE = 13;

    private byte base[][];
    private byte top[][];

    public Chunk(ChunkType type) {
        this.base = new byte[CHUNK_SIZE][CHUNK_SIZE];
        this.top = new byte[CHUNK_SIZE][CHUNK_SIZE];
        ChunkGenerator generator = ChunkGenerator.get(type);
        base = generator.generateBase(CHUNK_SIZE);
        top = generator.generateObjects(CHUNK_SIZE);
    }

    public void set(int x, int y, byte base, byte top) {
        this.base[x][y] = base;
        this.top[x][y] = top;
    }

    public byte[] get(int x, int y) {
        return new byte[]{this.base[x][y], this.top[x][y]};
    }

    public void draw(float sx, float sy, float scale, Color filter) {
        Assets.TILES.startUse();
        for (int j = 0; j < CHUNK_SIZE; j++) {
            for (int i = 0; i < CHUNK_SIZE; i++) {
                //Assets.TILES.setFilter(Image.FILTER_NEAREST);
                float ox = sx + (i * TILE_SIZE * scale);
                float oy = sy + ((j - ((Assets.TILES.getHeight() / TILE_SIZE) - 1)) * TILE_SIZE * scale);
                float btx = base[i][j] * TILE_SIZE;
                float ttx = top[i][j] * TILE_SIZE;
                Assets.TILES.drawEmbedded(
                        ox,
                        oy,
                        ox + (TILE_SIZE * scale),
                        oy + (Assets.TILES.getHeight() * scale),
                        btx,
                        0,
                        btx + TILE_SIZE,
                        Assets.TILES.getHeight(), filter);
                Assets.TILES.drawEmbedded(
                        ox,
                        oy,
                        ox + (TILE_SIZE * scale),
                        oy + (Assets.TILES.getHeight() * scale),
                        ttx,
                        0,
                        ttx + TILE_SIZE,
                        Assets.TILES.getHeight(), filter);
                //Assets.TILES.draw(sx + (i * TILE_SIZE * scale), sy + (j * TILE_SIZE * scale), TILE_SIZE * scale, TILE_SIZE * scale, Color.white);

            }
        }
        Assets.TILES.endUse();
    }

}
