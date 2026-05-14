package io.github.kraused53.Engine;

import io.github.kraused53.Map.Map;
import io.github.kraused53.Panel.Panel;
import io.github.kraused53.Player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;

import static java.lang.Math.*;

public class Engine implements KeyListener {

    // Graphics settings
    private final int screenWidth;
    private final int screenHeight;
    private final JFrame screen;

    // Engine settings
    boolean running;
    int miniMapScale = 20;

    // Map
    private final Map map;

    // Player
    private final Player player;

    // Movement trackers
    private boolean movUp = false;
    private boolean movDown = false;
    private boolean rotLeft = false;
    private boolean rotRight = false;

    // Timing
    private static final int FPS = 120;
    private static final double FIXED_DT = 1.0 / FPS;

    public Engine( int screenWidth, int screenHeight ) {
        this.map = new Map();
        this.player = new Player(
                ( float ) map.getMapWidth()  / 2,
                ( float ) map.getMapHeight() / 2,
                1.0f, 0.0f,
                0.0f, 0.6f
        );

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        screen = new JFrame( "Ray-Caster!" );
        Panel panel = new Panel( this );
        panel.setPreferredSize( new Dimension( this.screenWidth, this.screenHeight ) );
        panel.setBackground( Color.BLACK );

        screen.add( panel );
        screen.pack();

        screen.setLocationRelativeTo( null );
        screen.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

        screen.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        exitFrame( screen );
                    }
                }
        );

        screen.setContentPane( new Panel( this ) );
        screen.setVisible( true );

        running = false;

        screen.addKeyListener(this);
    }

    private void exitFrame(JFrame frame) {
        frame.dispose();
        running = false;
    }

    public void run() {
        setup();
        long interval = 1_000_000_000L / FPS;

        while( running ) {
            long frameStart = System.nanoTime();
            update();
            render();
            long elapsed = System.nanoTime() - frameStart;
            if( elapsed < interval ) {
                try {
                    Thread.sleep( (interval - elapsed) / 1_000_000 );
                } catch( InterruptedException ex ) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void setup() {
        running = true;
    }

    private void update() {
        float rotSpeed  = (float) ( 3.0 * FIXED_DT );
        float moveSpeed = (float) ( 5.0 * FIXED_DT );

        //System.out.println( 1.0 / dt );

        // Move Player
        if( movUp ) {
            if( map.get( (int)(floor( player.getX() + player.getDirX() * (moveSpeed + 0.5f) )), (int)(floor( player.getY() )) ) == 0 ) {
                player.moveX( moveSpeed * player.getDirX() );
            }
            if( map.get( (int)(floor( player.getX() )), (int)(floor( player.getY() + player.getDirY() * (moveSpeed + 0.5f) )) ) == 0 ) {
                player.moveY( moveSpeed * player.getDirY() );
            }
        } else if( movDown ) {
            if( map.get( (int)(floor( player.getX() - player.getDirX() * (moveSpeed + 0.5f) )), (int)(floor( player.getY() )) ) == 0 ) {
                player.moveX( -moveSpeed * player.getDirX() );
            }
            if( map.get( (int)(floor( player.getX() )), (int)(floor( player.getY() - player.getDirY() * (moveSpeed + 0.5f) )) ) == 0 ) {
                player.moveY( -moveSpeed * player.getDirY() );
            }
        }

        // Rotate player
        if( rotRight ) {
            player.rotateLeft( rotSpeed );
        } else if ( rotLeft ) {
            player.rotateRight( rotSpeed );
        }


    }

    private void render() {
        screen.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: exitFrame( screen ); break;
            case KeyEvent.VK_W: movUp = true; break;
            case KeyEvent.VK_A: rotLeft = true; break;
            case KeyEvent.VK_S: movDown = true; break;
            case KeyEvent.VK_D: rotRight = true; break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_W: movUp = false; break;
            case KeyEvent.VK_A: rotLeft = false; break;
            case KeyEvent.VK_S: movDown = false; break;
            case KeyEvent.VK_D: rotRight = false; break;
        }
    }
    @Override public void keyTyped(KeyEvent e) {}

    // Getters
    public int getH() { return this.screenHeight; }
    public int getW() { return this.screenWidth; }
    public int getMapWidth() { return map.getMapWidth(); }
    public int getMapHeight() { return map.getMapHeight(); }
    public int getMapScale() { return miniMapScale; }
    public int mapGet( int x, int y ) { return map.get( x, y ); }
    public float getPlayerX() { return player.getX(); }
    public float getPlayerY() { return player.getY(); }
    public float getPlayerDirX() { return player.getDirX(); }
    public float getPlayerDirY() { return player.getDirY(); }
    public float getPlayerPlnX() { return player.getPlaneX(); }
    public float getPlayerPlnY() { return player.getPlaneY(); }
    public int mapToScreen( float data ) { return ( int ) ( data * miniMapScale ); }
}
