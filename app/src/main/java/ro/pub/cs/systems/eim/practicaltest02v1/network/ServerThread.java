package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

public class ServerThread extends Thread {

    private ServerSocket serverSocket = null;

    private final HashMap<String, List<String>> data;

    public ServerThread(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException ioException) {
            Log.e("PracticalTest02v1", "An exception has occurred: " + ioException.getMessage());
        }
        this.data = new HashMap<>();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public synchronized void setData(String city, List<String> autocomplete) {
        this.data.put(city, autocomplete);
    }

    public synchronized HashMap<String, List<String>> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Log.i("PracticalTest02v1", "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i("PracticalTest02v1", "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this, socket);
                communicationThread.start();
            }
        } catch (IOException ioException) {
            Log.e("PracticalTest02v1", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
        }
    }

    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e("PracticalTest02v1", "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

}