package com.example.nicolas.androidclient;

/**
 * Created by Nicolas on 26/09/2016.
 */

        import android.os.Message;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.PrintWriter;
        import java.net.Socket;

public class ClientThread extends Thread{

    String dstAddress;
    int dstPort;
    private boolean running;
    MainActivity.ClientHandler handler;

    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;

    public ClientThread(String addr, int port, MainActivity.ClientHandler handler) {
        super();
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        MainActivity.ClientHandler.UPDATE_STATE, state));
    }

    public void txMsg(String msgToSend){
        if(printWriter != null){
            printWriter.println(msgToSend);
        }
    }

    @Override
    public void run() {
        sendState("connecting...");

        running = true;

        try {
            socket = new Socket(dstAddress, dstPort);
            sendState("connected");

            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            while(running){

                //bufferedReader block the code
                String line = bufferedReader.readLine();
                if(line != null){
                    handler.sendMessage(
                            Message.obtain(handler,
                                    MainActivity.ClientHandler.UPDATE_MSG, line));
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(printWriter != null){
                printWriter.close();
            }

            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        handler.sendEmptyMessage(MainActivity.ClientHandler.UPDATE_END);
    }
}
