package json.a8;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import jforsythe.Message;
import jforsythe.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    private EditText TextInput;
    private EditText TextOutput;
    private String name;
    private Socket socket;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
    private ServerListener serverListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        this.TextInput = findViewById(R.id.TextInput);
        this.TextInput.setOnEditorActionListener(this);
        this.TextOutput = findViewById(R.id.TextOutput);

        getUserName();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverListener.running = false;
        try{
            objectOutputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        try {
            socket = new Socket("odin.cs.csub.edu" , 3390);
            outputStream = socket.getOutputStream();
            outputStream.flush();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.flush();

             serverListener = new ServerListener(socket , TextOutput);
            serverListener.start();

            Message connect = new Message(MessageType.CONNECT , name , "Hi from Android ");
            objectOutputStream.writeObject(connect);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getUserName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("User Name");
        final EditText userNameInput  = new EditText(this);
        userNameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(userNameInput);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name = userNameInput.getText().toString();
                Log.d("USERNAME" , name);
                if(name.equals(""))getUserName();
                else connect();
            }
        });
        builder.show();
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(event  == null || event.getAction() == KeyEvent.ACTION_UP) {
            Message temp = new Message(MessageType.MESSAGE, name, TextInput.getText().toString());
            try {
                objectOutputStream.writeObject(temp);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            TextInput.setText("");
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return true;

    }
}