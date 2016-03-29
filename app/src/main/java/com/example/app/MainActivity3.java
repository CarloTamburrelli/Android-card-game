package com.example.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.PushService;

import org.apache.http.client.HttpClient;

public class MainActivity3 extends ActionBarActivity {
    HttpClient httpcl = HttpClientFactory.getThreadSafeClient();
    AlertDialog.Builder builder;
    AlertDialog alert;
    Button b;
    TextView tv1;
    TextView tv2;
    TextView tv3;
    String last_ch="";
    String enemy="";
    public int flieg=0;
    Handler handler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main3);
        Parse.initialize(MainActivity3.this, getString(R.string.parseAppID), getString(R.string.parseClientID));
        //avvio del thread che ricerca
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        ricerca thr = new ricerca(handler,httpcl,0,"server.php");
        thr.start();
    }else {
        AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity3.this);
        miaAlert.setTitle("Connessione");
        miaAlert.setMessage("Il tuo dispositivo non risulta connesso a internet.");
        AlertDialog alert = miaAlert.create();
        alert.setIcon(R.drawable.no);
        alert.show();
    }

    }
    @Override
    protected void onStop() {
        Handler handler = new MyHandler();
        if(flieg==0){ //se = 1 non c'e' bisogno di fermare un thread gia' fermo
        ricerca thr = new ricerca(handler,httpcl,1,"");
        thr.start();
        }
        super.onPause();
        finish();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.containsKey("sfida")) {
                String value = bundle.getString("sfida");
                if(value=="0"){
                    Toast toast = Toast.makeText(MainActivity3.this, "si e' verificato un problema.", Toast.LENGTH_LONG);
                    toast.show();
                }else {
                    enemy = value;
                    //avversario trovato
                    /*
                    ProgressBar el = (ProgressBar) findViewById(R.id.load);
                    el.setVisibility(View.GONE);
                    */

                    flieg=1; //per evitare di fermare un thread gia fermo

                    ProgressBar ll1 = (ProgressBar) findViewById(R.id.progressB);
                    ll1.setVisibility(View.INVISIBLE);
                    TextView trov = (TextView)findViewById(R.id.tit);
                    trov.setText("Giocatore trovato!");

                    LinearLayout ll = (LinearLayout)findViewById(R.id.linearlp);

                    tv1 = new TextView(MainActivity3.this);
                    tv1.setText("Utente trovato! Vuoi sfidare ");
                    tv1.setTextSize(15);
                    tv1.setTextColor(Color.WHITE);
                    ll.addView(tv1);

                    tv2 = new TextView(MainActivity3.this);
                    tv2.setText(value+" ?");
                    tv2.setTextSize(15);
                    tv2.setTextColor(Color.WHITE);
                    tv2.setTypeface(null, Typeface.BOLD);
                    ll.addView(tv2);
                    tv3 = new TextView(MainActivity3.this);
                    tv3.setTextColor(Color.WHITE);
                    tv3.setTextSize(15);
                    tv3.setText("(Hai 1 minuto di tempo per decidere)");
                    tv3.setTypeface(null, Typeface.ITALIC);
                    ll.addView(tv3);
                    b = new Button(MainActivity3.this);
                    b.setText("Sfida "+value+" in una partita!");
                    ll.addView(b);

                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv1.setVisibility(View.INVISIBLE);
                            tv2.setVisibility(View.INVISIBLE);
                            tv3.setVisibility(View.INVISIBLE);
                            builder = new AlertDialog.Builder(MainActivity3.this);
                            builder.setTitle("Attesa");
                            builder.setMessage("In attesa dell'altro giocatore");
                            alert = builder.create();
                            alert.show();
                            //avvio del thread che cercherà ogni 5 secondi risposta
                            ricerca thr = new ricerca(handler,httpcl,0,"controllo.php");
                            thr.start();
                            b.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }else if(bundle.containsKey("infoo")) {
                if(MainActivity3.this.isFinishing()!= true){
                    String value = bundle.getString("infoo");
                    alert.hide();
                    builder.setMessage(value);
                    alert = builder.create();
                    alert.show();
                }

            } if(bundle.containsKey("ini")) {
                String value = bundle.getString("ini");

               /* alert.hide();
                builder.setMessage("la partita sta per iniziare... " + value);
                alert = builder.create();
                alert.show();*/
               // alert.cancel();

                    Intent nuovaPagina = new Intent(MainActivity3.this, MainActivity4.class);
                    Bundle datipassati = getIntent().getExtras();
                    String uu = datipassati.getString("nick");
                    String pp = datipassati.getString("pass");
                    nuovaPagina.putExtra("nick",uu);
                    nuovaPagina.putExtra("pass",pp);
                    nuovaPagina.putExtra("canale",value);
                    nuovaPagina.putExtra("nemico",enemy);

                Parse.initialize(MainActivity3.this, getString(R.string.parseAppID), getString(R.string.parseClientID));
                //da vedere value
                    PushService.subscribe(MainActivity3.this, value, MainActivity4.class);
                    PushService.unsubscribe(MainActivity3.this,last_ch);
                    PushService.setDefaultPushCallback(MainActivity3.this, MainActivity4.class);
                    startActivity(nuovaPagina);
                    finish();
            }else if(bundle.containsKey("prendi_canale")) {
                last_ch = bundle.getString("prendi_canale");
            }
        }
    }


}
