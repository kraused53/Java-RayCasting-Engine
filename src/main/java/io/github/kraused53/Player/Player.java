package io.github.kraused53.Player;

import io.github.kraused53.Entity.Entity;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Player extends Entity {

    // Camera
    private float dirX;
    private float dirY;
    private float planeX;
    private float planeY;

    // Used in rotation
    private float oldDirX;
    private float oldPlaneX;

    public Player( float x, float y, float dirX, float dirY, float planeX, float planeY ) {
        super( x, y );
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;
    }

    public float getDirX() { return dirX; }
    public float getDirY() { return dirY; }

    public float getPlaneX() { return planeX; }
    public float getPlaneY() { return planeY; }

    private void normalize() {
        float len = (float) Math.sqrt( dirX * dirX + dirY * dirY );
        dirX /= len;
        dirY /= len;
    }

    public void rotateRight( float speed ) {
        // Rotate Direction vector
        oldDirX = dirX;
        dirX = (float) (dirX * cos(speed) - dirY * sin(speed));
        dirY = (float) (oldDirX * sin(speed) + dirY * cos(speed));

        // Rotate camera plane
        oldPlaneX = planeX;
        planeX = (float) (planeX * cos(speed) - planeY * sin(speed));
        planeY = (float) (oldPlaneX * sin(speed) + planeY * cos(speed));

        normalize();
    }

    public void rotateLeft( float speed ) {
        // Rotate Direction vector
        oldDirX = dirX;
        dirX = (float) (dirX * cos(-speed) - dirY * sin(-speed));
        dirY = (float) (oldDirX * sin(-speed) + dirY * cos(-speed));

        // Rotate camera plane
        oldPlaneX = planeX;
        planeX = (float) (planeX * cos(-speed) - planeY * sin(-speed));
        planeY = (float) (oldPlaneX * sin(-speed) + planeY * cos(-speed));

        normalize();
    }
}
