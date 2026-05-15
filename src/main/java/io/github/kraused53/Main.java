package io.github.kraused53;

import io.github.kraused53.Engine.Engine;

public class Main {
    public static void main(String[] args) {
        Engine game = new Engine(1440, 1080);
        game.run();
    }
}
