package com.example.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {
    //public static HttpClient httpcl = new DefaultHttpClient();
    public static HttpClient httpcl = HttpClientFactory.getThreadSafeClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
        // add text view
      //  TextView tv = new TextView(this);
       // tv.setText("Dynamic Text!");
        Button ciao = (Button) findViewById(R.id.button);
        ciao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usr = (EditText) findViewById(R.id.editText1);
                EditText psw = (EditText) findViewById(R.id.editText2);
                String uu = usr.getText().toString();
                String pp= psw.getText().toString();
                String url =  "system.php";
                Handler handler = new MyHandler();
                login thr = new login(handler,uu,pp,httpcl,url);
                thr.start();
            }
        });
        }else {
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity.this);
            miaAlert.setTitle("Connessione");
            miaAlert.setMessage("Il tuo dispositivo non risulta connesso a internet.");
            AlertDialog alert = miaAlert.create();
            alert.setIcon(R.drawable.no);
            alert.show();
        }

    }
    @Override
    public void onRestart(){
        super.onRestart();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnected())) {
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity.this);
            miaAlert.setTitle("Connessione");
            miaAlert.setMessage("Il tuo dispositivo non risulta connesso a internet.");
            AlertDialog alert = miaAlert.create();
            alert.setIcon(R.drawable.no);
            alert.show();
        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }



    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.containsKey("login")) {
                try {
                String value = bundle.getString("login");
                String vv = value.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                JSONObject obj = new JSONObject(vv);
                int stato = obj.getInt("stato");
                if (stato == 1) {
                    Toast toast = Toast.makeText(MainActivity.this, "Login effettuato!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent nuovaPagina = new Intent(MainActivity.this, MainActivity2.class);
                    EditText usr = (EditText) findViewById(R.id.editText1);
                    EditText psw = (EditText) findViewById(R.id.editText2);
                    String uu = usr.getText().toString();
                    String pp= psw.getText().toString();
                    nuovaPagina.putExtra("nick",uu);
                    nuovaPagina.putExtra("pass",pp);
                    startActivity(nuovaPagina);
                } else if (stato == 0) {
                    Toast toast = Toast.makeText(MainActivity.this, "Login errato", Toast.LENGTH_LONG);
                    toast.show();
                } else if (stato == 2) {
                    Toast toast = Toast.makeText(MainActivity.this, "LOGIN GIa' eseguito!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent nuovaPagina = new Intent(MainActivity.this, MainActivity2.class);
                    EditText usr = (EditText) findViewById(R.id.editText1);
                    EditText psw = (EditText) findViewById(R.id.editText2);
                    String uu = usr.getText().toString();
                    String pp= psw.getText().toString();
                    nuovaPagina.putExtra("nick",uu);
                    nuovaPagina.putExtra("pass",pp);
                    startActivity(nuovaPagina);
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
