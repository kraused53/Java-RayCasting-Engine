package io.github.kraused53;

import io.github.kraused53.Engine.Engine;

public class Main {
    static void main() {
        Jogger.enableColor();
        Jogger.Info( "Welcome to my ray-casting engine" );

        Engine game = new Engine( 700, 700 );
        game.run();

        Jogger.Info( "Thanks for playing!" );
    }
}
