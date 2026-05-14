package io.github.kraused53.Entity;

public class Entity {

    // Position
    private float x;
    private float y;

    public Entity( float x, float y ) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public void moveX( float dx ) { x += dx; }
    public void moveY( float dy ) { y += dy; }

}
