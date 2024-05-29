package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String autocomplete;
    private final TextView results;

    private Socket socket;

    public ClientThread(String clientAddress, String clientPort, String string, TextView results) {
        this.address = clientAddress;
        this.port = Integer.parseInt(clientPort);
        this.autocomplete = string;
        this.results = results;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(autocomplete);
            printWriter.flush();
            results.post(() -> results.setText(""));
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                final String finalizedResult = result;
                results.post(() -> results.setText(results.getText().toString() + finalizedResult));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}