package json.a8;

import android.widget.EditText;
import jforsythe.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ServerListener extends Thread{

    private Socket socket;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private EditText output;
    public boolean running  = true;

    public ServerListener(Socket socket , EditText output){
        this.socket =socket;
        this.output = output;
        try{
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try{
                while(running){
                    Message temp = (Message) objectInputStream.readObject();
                    output.append(String.format("%s: %s%n" , temp.getName() , temp.getMessage()));
                }
        } catch (IOException  | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try{
                objectInputStream.close();
                inputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
