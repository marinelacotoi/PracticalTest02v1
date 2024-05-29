package ro.pub.cs.systems.eim.practicaltest02v1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import ro.pub.cs.systems.eim.practicaltest02v1.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02v1.network.ServerThread;

public class PracticalTest02v1MainActivity extends AppCompatActivity {
    private Button connectButton = null;
    private EditText portServer = null;
    private EditText portClient = null;

    private EditText clientAddress = null;

    private EditText autocomplete = null;
    private Button getResults = null;

    private TextView results = null;

    private ServerThread serverThread = null;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String serverPort = portServer.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                return;
            }
            serverThread.start();
        }
    }

    private final ServerDataButtonClickListener getResultsButtonClickListener= new ServerDataButtonClickListener();

    private class ServerDataButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String address = clientAddress.getText().toString();
            String clientPort = portClient.getText().toString();
            if (address == null || address.isEmpty() || clientPort == null || clientPort.isEmpty()) {
                return;
            }

            getResults.setText("");
            ClientThread clientThread = new ClientThread(
                    address,
                    clientPort,
                    autocomplete.getText().toString(),
                    results
            );
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test02v1_main);

        connectButton = findViewById(R.id.connectButton);
        portServer = findViewById(R.id.serverPortTextEdit);
        portClient = findViewById(R.id.clientPortTextEdit);
        clientAddress = (EditText) findViewById(R.id.addressTextEdit);
        autocomplete = findViewById(R.id.autocompleteTextEdit);
        getResults = findViewById(R.id.getResultsButton);
        results = findViewById(R.id.resultsTextView);

        connectButton.setOnClickListener(connectButtonClickListener);
        getResults.setOnClickListener(getResultsButtonClickListener);
    }
}