package com.example.nicolas.androidclient;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText editTextAddress, editTextPort, editTextMsg;
    Button buttonConnect, buttonDisconnect, buttonSend, buttonGPIO1, buttonGPIO2;
    TextView textViewState, textViewRx;

    ClientHandler clientHandler;
    ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextAddress = (EditText) findViewById(R.id.address);
        editTextPort = (EditText) findViewById(R.id.port);
        editTextMsg = (EditText) findViewById(R.id.msgtosend);
        buttonConnect = (Button) findViewById(R.id.connect);
        buttonDisconnect = (Button) findViewById(R.id.disconnect);
        buttonSend = (Button)findViewById(R.id.send);
        textViewState = (TextView)findViewById(R.id.state);
        textViewRx = (TextView)findViewById(R.id.received);
        buttonGPIO1 = (Button) findViewById(R.id.buttonGPIO1);
        buttonGPIO2 = (Button) findViewById(R.id.buttonGPIO2);

        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonGPIO1.setEnabled(false);
        buttonGPIO2.setEnabled(false);

        buttonConnect.setOnClickListener(buttonConnectOnClickListener);
        buttonDisconnect.setOnClickListener(buttonDisConnectOnClickListener);
        buttonSend.setOnClickListener(buttonSendOnClickListener);
        buttonGPIO1.setOnClickListener(buttonGPIO1OnClickListener);
        buttonGPIO2.setOnClickListener(buttonGPIO2OnClickListener);

        clientHandler = new ClientHandler(this);
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    clientThread = new ClientThread(
                            editTextAddress.getText().toString(),
                            Integer.parseInt(editTextPort.getText().toString()),
                            clientHandler);
                    clientThread.start();

                    buttonConnect.setEnabled(false);
                    buttonDisconnect.setEnabled(true);
                    buttonSend.setEnabled(true);
                    buttonGPIO1.setEnabled(true);
                    buttonGPIO2.setEnabled(true);
                }
            };

    View.OnClickListener buttonDisConnectOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(clientThread != null){
                clientThread.setRunning(false);
            }

        }
    };

    View.OnClickListener buttonSendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(clientThread != null){
                String msgToSend = editTextMsg.getText().toString();
                clientThread.txMsg(msgToSend);
            }
        }
    };

    View.OnClickListener buttonGPIO1OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(clientThread != null){
                String msgToSend = "GPIO 1";
                clientThread.txMsg(msgToSend);
            }
        }
    };

    View.OnClickListener buttonGPIO2OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(clientThread != null){
                String msgToSend = "GPIO 2";
                clientThread.txMsg(msgToSend);
            }
        }
    };

    private void updateState(String state){
        textViewState.setText(state);
    }

    private void updateRxMsg(String rxmsg){
        //textViewRx.append(rxmsg + "\n");
        textViewRx.setText(rxmsg + "\n");
    }

    private void clientEnd(){
        clientThread = null;
        textViewState.setText("clientEnd()");
        buttonConnect.setEnabled(true);
        buttonDisconnect.setEnabled(false);
        buttonSend.setEnabled(false);
        buttonGPIO1.setEnabled(false);
        buttonGPIO2.setEnabled(false);

    }

    public static class ClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private MainActivity parent;

        public ClientHandler(MainActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }
}