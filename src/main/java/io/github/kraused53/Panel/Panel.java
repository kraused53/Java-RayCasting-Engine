package io.github.kraused53.Panel;

import io.github.kraused53.Engine.Engine;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static java.lang.Math.*;

public class Panel extends JPanel {
    private final Engine e;

    Random rand;

    public Panel( Engine e ) {
        this.rand = new Random();
        this.e = e;
    }

    private int mapToScreen( double data ) {
        return (int) ( data * 60 );
    }

    private Color getColorFromMap( int val ) {
        return switch (val) {
            case 1 -> Color.RED; //red
            case 2 -> Color.GREEN; //green
            case 3 -> Color.BLUE; //blue
            case 4 -> Color.WHITE; //white
            case 0 -> Color.BLACK;
            default -> Color.YELLOW; //yellow
        };
    }

    private void renderCone( Graphics g ) {
        g.setColor( Color.GREEN );

        g.drawLine(
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() ),
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() - e.getPlayerPlnX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() - e.getPlayerPlnY() )
        );
        g.drawLine(
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() ),
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() + e.getPlayerPlnX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() + e.getPlayerPlnY() )
        );

        g.drawLine(
                e.mapToScreen( e.getPlayerX() ),
                e.mapToScreen( e.getPlayerY() ),
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() - e.getPlayerPlnX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() - e.getPlayerPlnY() )
        );

        g.drawLine(
                e.mapToScreen( e.getPlayerX() ),
                e.mapToScreen( e.getPlayerY() ),
                e.mapToScreen( e.getPlayerX() + e.getPlayerDirX() + e.getPlayerPlnX() ),
                e.mapToScreen( e.getPlayerY() + e.getPlayerDirY() + e.getPlayerPlnY() )
        );
    }

    private void renderPlayer( Graphics g ) {
        int player_size = 10;
        int half_player_size = player_size / 2;
        renderCone( g );

        g.setColor( Color.blue );
        g.fillOval(
                e.mapToScreen( e.getPlayerX() ) - half_player_size,
                e.mapToScreen( e.getPlayerY() ) - half_player_size,
                player_size,
                player_size
        );
    }

    private void rayCastMiniMap( Graphics g ) {
        // Cast all player values to double immediately — prevents float precision
        // from contaminating the double DDA math and causing gap artifacts
        double playerDirX  = e.getPlayerDirX();
        double playerDirY  = e.getPlayerDirY();
        double playerPlaneX = e.getPlayerPlnX();
        double playerPlaneY = e.getPlayerPlnY();
        double playerPosX  = e.getPlayerX();
        double playerPosY  = e.getPlayerY();

        for( int x = 0; x < e.getW(); x++ ) {
            double cameraX = 2.0 * x / (double) e.getW() - 1.0;
            double rayDirX = playerDirX + playerPlaneX * cameraX;
            double rayDirY = playerDirY + playerPlaneY * cameraX;

            int mapX = (int) playerPosX;
            int mapY = (int) playerPosY;

            double sideDistX;
            double sideDistY;

            double deltaDistX = (rayDirX == 0) ? 1e30 : Math.abs(1.0 / rayDirX);
            double deltaDistY = (rayDirY == 0) ? 1e30 : Math.abs(1.0 / rayDirY);
            double perpWallDist;

            int stepX, stepY;
            int hit = 0, side = 0;

            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (playerPosX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - playerPosX) * deltaDistX;
            }
            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (playerPosY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - playerPosY) * deltaDistY;
            }

            while (hit == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                if (e.mapGet(mapX, mapY) > 0) hit = 1;
            }

            if (side == 0) {
                perpWallDist = sideDistX - deltaDistX;
            } else {
                perpWallDist = sideDistY - deltaDistY;
            }

            int lineHeight = (int)(e.getH() / perpWallDist);

            int drawStart = Math.max(0, e.getH() / 2 - lineHeight / 2);
            int drawEnd   = Math.min(e.getH() - 1, e.getH() / 2 + lineHeight / 2);

            Color wallColor = switch (e.mapGet(mapX, mapY)) {
                case 1 -> Color.RED;
                case 2 -> Color.GREEN;
                case 3 -> Color.BLUE;
                case 4 -> Color.WHITE;
                default -> Color.YELLOW;
            };
            wallColor = (side != 0) ? wallColor.darker() : wallColor.brighter();

            g.setColor(wallColor);
            // fillRect instead of drawLine — fills the full pixel column with no gaps
            g.fillRect(x, drawStart, 1, drawEnd - drawStart + 1);
        }
    }

    private void clearPanel( Graphics g ) {
        g.setColor( Color.DARK_GRAY );
        g.fillRect( 0, (int)(e.getH()/2), e.getW(), (int)(e.getH()/2) );
        g.setColor( Color.CYAN );
        g.fillRect( 0, 0, e.getW(), (int)(e.getH()/2) );
    }

    private void renderMap( Graphics g ) {
        clearPanel( g );
/*
        for( int mx = 0; mx < e.getMapWidth(); mx++ ) {
            for( int my = 0; my < e.getMapHeight(); my++ ) {
                g.setColor(getColorFromMap( e.mapGet( mx, my ) ) );
                g.fillRect(
                        mx * e.getMapScale(), my * e.getMapScale(),
                        e.getMapScale(), e.getMapScale()
                );
            }
        }
*/
        rayCastMiniMap( g );

//        renderPlayer( g );
    }

    @Override
    public void paintComponent( Graphics g ) {
        g.setColor( Color.DARK_GRAY );
        g.fillRect( 0, (int)(e.getH()/2), e.getW(), (int)(e.getH()/2) );
        g.setColor( Color.CYAN );
        g.fillRect( 0, 0, e.getW(), (int)(e.getH()/2) );

        renderMap( g );
    }
}
