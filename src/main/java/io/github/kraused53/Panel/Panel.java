package io.github.kraused53.Panel;

import io.github.kraused53.Wall.Wall;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Panel extends JPanel {

    private final int screenWidth;
    private final int screenHeight;
    private final Wall[] walls;
    private final BufferedImage[] textures;
    private final BufferedImage frameBuffer;
    private int[] pixels;

    public Panel( Wall[] walls, int screenWidth, int screenHeight, BufferedImage[] textures ) {
        this.walls = walls;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.textures = textures;
        frameBuffer = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) frameBuffer
                .getRaster()
                .getDataBuffer())
                .getData();
    }

    private void rayCast( Graphics g ) {
        int texW = textures[0].getWidth();
        int texH = textures[0].getHeight();

        for( int x = 0; x < walls.length; x++ ) {
            int texX = (int)(walls[x].getWallX() * texW);
            boolean dark = walls[x].getSide() == 1;

            for (int y = walls[x].getDrawStart(); y <= walls[x].getDrawEnd(); y++) {
                // Map screen pixel → texture row
                int d = y - screenHeight / 2 + walls[x].getLineHeight() / 2;
                int texY = Math.max(0, Math.min(texH - 1, (d * texH) / walls[x].getLineHeight()));

                int rgb = textures[walls[x].getWallID()-1].getRGB(texX, texY);  // already packed ARGB
                if (dark) {
                    // Halve each channel by shifting — faster than Color.darker()
                    rgb = ((rgb & 0xFEFEFE) >> 1);
                }
                pixels[y * screenWidth + x] = rgb;  // write directly — no pipeline
            }

        }
    }

    private void clearPanel( Graphics g ) {
        int sky   = 0x202030;   // dark blue-gray
        int floor = 0x383838;   // dark gray
        int half  = screenHeight / 2;
        for (int i = 0; i < half * screenWidth; i++)             pixels[i] = sky;
        for (int i = half * screenWidth; i < pixels.length; i++) pixels[i] = floor;
    }

    @Override
    public void paintComponent( Graphics g  ) {
        clearPanel( g );

        rayCast( g );
        g.drawImage(frameBuffer, 0, 0, null);
    }
}
