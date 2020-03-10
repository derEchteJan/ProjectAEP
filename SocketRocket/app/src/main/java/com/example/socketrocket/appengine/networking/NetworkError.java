package com.example.socketrocket.appengine.networking;

public class NetworkError {

    public final Type type;
    public final int statusCode;
    public final String message;

    public enum Type {
        notReachable, connectionClosed, httpStatus, badResponse;
    }

    public NetworkError(Type type, String message, int statusCode) {
        this.type = type;
        this.message = message;
        this.statusCode = statusCode;
    }

    public NetworkError(Type type, String message) {
        this(type, message, -1);
    }

    public NetworkError(Type type) {
        this(type, null, -1);
    }

    @Override public String toString() {
        String result = "NetworkError(" + this.type;
        if(this.statusCode != -1) result += ", httpStatus: " + this.statusCode;
        if(this.message != null) result += ", message: " + this.message;
        result += ")";
        return result;
    }
}
