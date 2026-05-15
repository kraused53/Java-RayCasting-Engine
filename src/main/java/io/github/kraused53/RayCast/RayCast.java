package io.github.kraused53.RayCast;

import io.github.kraused53.Player.Player;
import io.github.kraused53.Wall.Wall;
import io.github.kraused53.Map.Map;

public class RayCast {

    public static void rayCast( Wall[] walls, Player player, int screenWidth, int screenHeight, Map map ) {
        // Cast all player values to double immediately — prevents float precision
        // from contaminating the double DDA math and causing gap artifacts
        double playerDirX  = player.getDirX();
        double playerDirY  = player.getDirY();
        double playerPlaneX = player.getPlaneX();
        double playerPlaneY = player.getPlaneY();
        double playerPosX  = player.getX();
        double playerPosY  = player.getY();

        for( int x = 0; x < screenWidth; x++ ) {
            double cameraX = 2.0 * x / (double) screenWidth - 1.0;
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
                if (map.get(mapX, mapY) > 0) hit = 1;
            }

            if (side == 0) {
                perpWallDist = sideDistX - deltaDistX;
            } else {
                perpWallDist = sideDistY - deltaDistY;
            }

            double wallX;
            if( side == 0 ) {
                wallX = playerPosY + perpWallDist * rayDirY;
            } else {
                wallX = playerPosX + perpWallDist * rayDirX;
            }

            wallX -= Math.floor(wallX);
            walls[x].setWallX(wallX);

            int lineHeight = (int)(screenHeight / perpWallDist);

            int drawStart = Math.max(0, screenHeight / 2 - lineHeight / 2);
            int drawEnd   = Math.min(screenHeight - 1, screenHeight / 2 + lineHeight / 2);

            walls[x].setLineHeight( lineHeight );
            walls[x].setDrawStart( drawStart );
            walls[x].setDrawEnd( drawEnd );
            walls[x].setWallID( map.get(mapX, mapY) );
            walls[x].setSide( side );
        }
    }
}
