package com.example.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import static com.example.app.R.layout;

public class MainActivity2 extends ActionBarActivity {
    int gl=0;
    Drawable immm[]= new Drawable[5];
    static int collez_leng=0;
    HttpClient httpcl = HttpClientFactory.getThreadSafeClient();
    String arr[]= new String[5];
    String [] collezione;
    int pww[]= new int[5];
    String name_u;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.fragment_main2);
        Parse.initialize(MainActivity2.this, getString(R.string.parseAppID), getString(R.string.parseClientID));
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        String url = "profilo.php";
        Handler handler = new MyHandler();
        Bundle datipassati = getIntent().getExtras();
        String nn = datipassati.getString("nick");
        setTitle("Il tuo profilo: "+ nn);
        String pp = datipassati.getString("pass");
        login thr = new login(handler,nn,pp,httpcl,url);
        thr.start();
        Button gioca = (Button) findViewById(R.id.bot1); //tasto join
        gioca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new MyHandler();
                login thr = new login(handler,"","",httpcl,"vedi.php");
                thr.start();

        }
        });

        Button imposta = (Button) findViewById(R.id.bot2); //tasto join
        imposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent cambia activity
                Intent nuovaPagina = new Intent(MainActivity2.this, MainActivity5.class);
                Bundle datipassati = getIntent().getExtras();
                String nn = datipassati.getString("nick");
                String pp = datipassati.getString("pass");
                nuovaPagina.putExtra("nick", nn);
                nuovaPagina.putExtra("pass", pp);
                startActivity(nuovaPagina);
            }

        });
        }else {
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity2.this);
            miaAlert.setTitle("Connessione");
            miaAlert.setMessage("Il tuo dispositivo non risulta connesso a internet.");
            AlertDialog alert = miaAlert.create();
            alert.setIcon(R.drawable.no);
            alert.show();
        }

    }
    @Override
    public void onBackPressed(){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);

        builder.setTitle("Log out");
        builder.setMessage("Clicca continua per effettuare il logout, altrimenti clicca su annulla e premi il tasto home del telefono per uscire momentaneamente dall'applicazione.");

        builder.setPositiveButton("continua", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                dialog.dismiss();
                finish();
            }

        });

        builder.setNegativeButton("annulla", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }



    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.containsKey("profilo")) {
                String value = bundle.getString("profilo");
                String vv = value.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                JSONObject obj = null;
                    try {
                        obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                if(stato==1){
                    name_u = obj.getString("nick");
                    JSONArray ob = obj.getJSONArray("carte");
                    int i=0;

                    for(;i<ob.length();i=i+1){
                    arr[i] = ob.getString(i);
                        String nom= arr[i];
                        int conta = Integer.parseInt(nom.substring(0,1)); // 1_1_1_3
                        conta =conta + Integer.parseInt(nom.substring(2,3));
                        conta = conta + Integer.parseInt(nom.substring(4,5));
                        conta = conta + Integer.parseInt(nom.substring(6));
                    pww[i] = conta;
                    }
                    //arr adesso contiene i valori delle carte


                }else {
                    Toast toast = Toast.makeText(MainActivity2.this, "si e' verificato un problema.", Toast.LENGTH_LONG);
                    toast.show();
                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }else if (bundle.containsKey("cards")){

                //in questo if si entra solamente per caricare le 5 carte
                byte [] value = bundle.getByteArray("cards");
                Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length); //DA BYTE A BITMAP
                Drawable ab=new BitmapDrawable(getResources(),bmp); //DA  BITMAP A DRAWABLE
                Bitmap bitmap = ((BitmapDrawable) ab).getBitmap();
                immm[gl] = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                if(gl!=4) {
                gl=gl+1;
                }else { //e' l'ultima drawable caricata
                    ListView listView = (ListView)findViewById(R.id.listViewp);
                    List list = new LinkedList();

                    list.add(new carte(arr[0],immm[0],pww[0]));
                    list.add(new carte(arr[1],immm[1],pww[1]));
                    list.add(new carte(arr[2],immm[2],pww[2]));
                    list.add(new carte(arr[3],immm[3],pww[3]));
                    list.add(new carte(arr[4],immm[4],pww[4]));

                    CustomAdapter adapter = new CustomAdapter(MainActivity2.this, layout.elenco, list,0);
                    listView.setAdapter(adapter);
                    ViewGroup.LayoutParams lp =  listView.getLayoutParams();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int height = displaymetrics.heightPixels;
                    lp.height = height/2;
                    listView.setLayoutParams(lp);
                }
            }else if(bundle.containsKey("collezzz")){
                collezione = bundle.getStringArray("collezzz");//collezione contiene l'array delle carte collezionabili
                collez_leng =collezione.length;
            }else if(bundle.containsKey("innuendo")){
                String mes = bundle.getString("innuendo");
                if(mes.equals("0")){ //non sei collegato
                    Toast toast = Toast.makeText(MainActivity2.this, "ERRORE: NON SEI COLLEGATO.", Toast.LENGTH_LONG);
                    toast.show();

                }else if(mes.equals("1")){ //una partita da giocare
                    Intent nuovaPagina = new Intent(MainActivity2.this, MainActivity4.class);
                    Bundle datipassati = getIntent().getExtras();
                    String uu = datipassati.getString("nick");
                    String pp = datipassati.getString("pass");
                    nuovaPagina.putExtra("nick",uu);
                    nuovaPagina.putExtra("pass",pp);
                    startActivity(nuovaPagina);

                }else if(mes.equals("2")){ //nessuna partita da giocare
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity2.this);

                    builder.setTitle("Conferma");
                    builder.setMessage("Stai per fare una richiesta di gioco, sei sicuro?");

                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing but close the dialog

                            dialog.dismiss();
                            //intent cambia activity
                            Intent nuovaPagina = new Intent(MainActivity2.this, MainActivity3.class);
                            Bundle datipassati = getIntent().getExtras();
                            String nn = datipassati.getString("nick");
                            String pp = datipassati.getString("pass");
                            nuovaPagina.putExtra("nick",nn);
                            nuovaPagina.putExtra("pass",pp);
                            startActivity(nuovaPagina);
                        }

                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
    }
    }

}