package ro.pub.cs.systems.eim.practicaltest02v1.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.pub.cs.systems.eim.practicaltest02v1.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02v1.general.Utilities;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e("PracticalTest02v1", "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            // ******* TODO: read new data ********
            String autocomplete = bufferedReader.readLine();
            if (autocomplete == null || autocomplete.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            // ******* TODO: check if data was already requested *******
            HashMap<String, List<String>> data = serverThread.getData();
            List<String> results;
            if (data.containsKey(autocomplete)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                results = data.get(autocomplete);
            } else {
                results = new ArrayList<>();
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");

                // ******* TODO: change server URL ******
//                String url = Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&APPID=" + Constants.WEB_SERVICE_API_KEY + "&units=" + Constants.UNITS;
                String url = "https://www.google.com/complete/search?client=chrome&q=" + autocomplete;

                URL urlAddress = new URL(url);
                URLConnection urlConnection = urlAddress.openConnection();
                BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String pageSourceCode;
                StringBuilder stringBuilder = new StringBuilder();
                String currentLine;
                while ((currentLine = bufferedReader1.readLine()) != null) {
                    stringBuilder.append(currentLine);
                }

                pageSourceCode = stringBuilder.toString();
//                JSONObject content = new JSONObject(pageSourceCode);
                // Parse the JSON string
                JSONArray jsonArray = new JSONArray(pageSourceCode);

                // Extract the second array from the JSON
                JSONArray suggestionsArray = jsonArray.getJSONArray(1);

                // Print out the suggestions
                for (int i = 0; i < suggestionsArray.length(); i++) {
                    results.add(suggestionsArray.getString(i));
                }
                System.out.print(suggestionsArray.toString());
                serverThread.setData(autocomplete, results);
//
//            // ******* TODO: Update result in UI ******

                printWriter.println(results.toString());
                printWriter.flush();
//            } finally{
//                try {
                    socket.close();
//                } catch (IOException ioException) {
//                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
//                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
//    } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }