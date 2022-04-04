package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    private Socket client;
    private PrintWriter printwriter;
    private EditText textField;
    private Button button;
    private Button button2;
    private Button button3;
    private String message;
    private BufferedReader inFromUser;
    private DataOutputStream outToServer;
    private String sentence;
    private TextView textView;
    private boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textField = (EditText) findViewById(R.id.editText1);
        textView = (TextView) findViewById(R.id.textView2);


        button = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);


        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread((new ClientClose(client,flag))).start();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new ClientConnect(client,flag)).start();

            }
        });
        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {


             if(flag){
                message = textField.getText().toString();


                new Thread(new ClientThread(message,client)).start();}


            }
        });
    }
    public void toastmsg(String msg){
        Toast toast = Toast.makeText(this,msg,Toast.LENGTH_LONG);
        toast.show();
    }
    public void display(View x,String s){
        toastmsg(s);
    }


    class ClientThread implements Runnable {
        private final String message;
        private final Socket Client;
        ClientThread(String message,Socket Client) {
            this.message = message;
            this.Client = Client;
        }
        @Override
        public void run() {
            try {

                outToServer =
                        new DataOutputStream(client.getOutputStream());
                outToServer.writeBytes(message+ '\n');
                BufferedReader inFromServer =
                        new BufferedReader(new
                                InputStreamReader(client.getInputStream()));

                sentence = inFromServer.readLine();
                textView.setText(sentence);
                textView.setTextColor(Color.RED);
            } catch (IOException e) {
                e.printStackTrace();
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    textField.setText("");
                }
            });
        }
    }
    class ClientConnect implements Runnable{

        private final Socket Client;
        private final boolean Flag;
        ClientConnect(Socket Client,boolean Flag) {

            this.Client = Client;
            this.Flag = Flag;
        }
        @Override
        public void run() {
            try {
                if(!flag){
                client = new Socket("10.0.2.2", 11224);
                flag = true;}


            } catch (IOException e) {
                e.printStackTrace();
            }

        }}
        class ClientClose implements Runnable{
            private final Socket Client;
            private final boolean Flag;

            public ClientClose(Socket client,boolean Flag) {
                this.Client = client;
                this.Flag = Flag;
            }

            @Override
            public void run() {
                try {
                    outToServer =
                            new DataOutputStream(client.getOutputStream());
                    outToServer.writeBytes("over"+ '\n');
                    flag = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
    }
}