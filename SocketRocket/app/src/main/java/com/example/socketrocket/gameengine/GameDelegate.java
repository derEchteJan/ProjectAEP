package com.example.socketrocket.gameengine;

import com.example.socketrocket.gameengine.entity.Player;

public interface GameDelegate {
    public abstract void openMenu();
    public abstract void hideMenu();
    public abstract void quitToMainMenu();
    public abstract Player getPlayer();
}
