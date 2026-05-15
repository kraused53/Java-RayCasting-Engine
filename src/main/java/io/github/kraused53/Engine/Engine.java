package io.github.kraused53.Engine;

import io.github.kraused53.Map.Map;
import io.github.kraused53.Panel.Panel;
import io.github.kraused53.Player.Player;
import io.github.kraused53.RayCast.RayCast;
import io.github.kraused53.Wall.Wall;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static java.lang.Math.*;

public class Engine implements KeyListener {

    // Graphics settings
    private final int screenWidth;
    private final int screenHeight;
    private final JFrame screen;

    // Engine settings
    private boolean running;

    // Map
    private final Map map;

    // Walls
    private final Wall[] walls;

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

    private final BufferedImage[] textures;

    public Engine( int screenWidth, int screenHeight ) {
        textures = new BufferedImage[8];
        try{
            textures[0] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/redbrick.png" ) ) );
            textures[1] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/eagle.png" ) ) );
            textures[2] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/colorstone.png" ) ) );
            textures[3] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/bluestone.png" ) ) );
            textures[4] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/greystone.png" ) ) );
            textures[5] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/mossy.png" ) ) );
            textures[6] = ImageIO.read( Objects.requireNonNull(getClass().getResource( "/textures/wood.png" ) ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        // Each pixel across the map will get one wall slice
        walls = new Wall[ screenWidth ];
        for ( int index = 0; index < screenWidth; index++ ) {
            walls[ index ] = new Wall();
        }

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
        Panel panel = new Panel( walls, screenWidth, screenHeight, textures );
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

        //screen.setContentPane( new Panel() );
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
            player.rotateRight( rotSpeed );
        } else if ( rotLeft ) {
            player.rotateLeft( rotSpeed );
        }

        RayCast.rayCast( walls, player, screenWidth, screenHeight, map );
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
}
