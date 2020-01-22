package com.example.socketrocket.appengine.networking;

public class NetworkError {

    public final Type type;

    enum Type {
        notReachable, connectionClosed, httpStatus, badResponse;
    }

    public NetworkError(Type type) {
        this.type = type;
    }
}
