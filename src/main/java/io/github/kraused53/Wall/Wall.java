package io.github.kraused53.Wall;

public class Wall {
    private int lineHeight;
    private int drawStart;
    private int drawEnd;
    private int wallID;
    private int side;
    private double wallX;

    public Wall() {
        this.lineHeight = 0;
        this.drawStart = 0;
        this.drawEnd = 0;
        this.wallID = 0;
        this.side = 0;
        this.wallX = 0;
    }

    public int getLineHeight() { return lineHeight; }
    public int getDrawStart() { return drawStart; }
    public int getDrawEnd() { return drawEnd; }
    public int getWallID() { return wallID; }
    public int getSide() { return side; }
    public double getWallX() { return wallX; }

    public void setLineHeight( int lineHeight ) { this.lineHeight = lineHeight; }
    public void setDrawStart( int drawStart ) { this.drawStart = drawStart; }
    public void setDrawEnd( int drawEnd ) { this.drawEnd = drawEnd; }
    public void setWallID( int wallID ) { this.wallID = wallID; }
    public void setSide( int side ) { this.side = side; }
    public void setWallX( double wallX ) { this.wallX = wallX; }
}
