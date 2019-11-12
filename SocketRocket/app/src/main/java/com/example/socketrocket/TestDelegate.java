package com.example.socketrocket.networking;

public class TestDelegate implements NetworkRequestDelegate {

    public void runTest() {
        System.out.println("start sending test request");
        NetworkRequestHandler.sendTestRequest(this);
    }

    @Override
    public void didRecieveNetworkResponse(int requestId, int statusCode, String payload) {
        System.out.println("did recieve network response: ");
        System.out.println("status: " + statusCode);
        System.out.println("data: " + payload);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkErrorType errorType, int statusCode) {
        String status = (statusCode > 0) ? (" HTTP Status: "+statusCode) : ("");
        System.out.println("did recieve network error: " + errorType + status);
    }
}
