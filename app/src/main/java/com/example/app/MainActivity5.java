package com.example.app;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.Parse;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity5 extends ActionBarActivity {
    Drawable [] immm;
    int gl=0;
    String arr[]= new String[5];
    String [] collezione;
    int s=0; //iteratore globale
    HttpClient httpcl = HttpClientFactory.getThreadSafeClient();
    CustomAdapter dataAdapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        setContentView(R.layout.fragment_main5);
            Parse.initialize(MainActivity5.this, getString(R.string.parseAppID), getString(R.string.parseClientID));
        setTitle("La tua collezione:");
        Handler handler = new MyHandler();
        login thr = new login(handler,"","",httpcl,"profilo.php");
        thr.start();

        Button myButton = (Button) findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String [] gro = new String [collezione.length];
                int j=0;
                ArrayList<carte> countryList = dataAdapter.countryList;
                for(int i=0;i<countryList.size();i++){
                    carte c = countryList.get(i);
                    if(c.isSelected()){
                        gro[j] = c.getNome();
                        j=j+1;
                    }
                }
                if(j==5){
                    Handler handler = new MyHandler();
                    prendi_collez thr = new prendi_collez(handler,"","",httpcl,gro,1); //il flag 0 indica il caricamento delle carte
                    thr.start();

                }else {
                    Toast toast = Toast.makeText(MainActivity5.this, "Devono essere selezionate 5 carte!", Toast.LENGTH_LONG);
                    toast.show();
                }



            }
        });

        }else{
            AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity5.this);
            miaAlert.setTitle("Connessione");
            miaAlert.setMessage("Il tuo dispositivo non risulta connesso a internet.");
            AlertDialog alert = miaAlert.create();
            alert.setIcon(R.drawable.no);
            alert.show();
        }
    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.containsKey("give")) {


                byte [] value = bundle.getByteArray("give");

                Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length); //DA BYTE A BITMAP

                Drawable ab=new BitmapDrawable(getResources(),bmp); //DA  BITMAP A DRAWABLE

                Bitmap bitmap = ((BitmapDrawable) ab).getBitmap();
                immm[s] = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
                if(s< (collezione.length-1)) {
                    s=s+1;
                }else { //e' l'ultima drawable caricata


                   ListView listView = (ListView)findViewById(R.id.listView1);
                    int i=0;
                    ArrayList<carte> lista = new ArrayList<carte>();
                    for (;i<collezione.length; i=i+1){
                        String nom= collezione[i];
                        int conta = Integer.parseInt(nom.substring(0,1)); // 1_1_1_3
                        conta =conta + Integer.parseInt(nom.substring(2,3));
                        conta = conta + Integer.parseInt(nom.substring(4,5));
                        conta = conta + Integer.parseInt(nom.substring(6));
                        int j=0;
                        int fla=0; // se fla sara' uguale a 1 dopo il for significa che possiediamo quella carta
                        for (;j<arr.length;j=j+1){
                            if(nom.equals(arr[j])){
                                fla=1;
                                break;
                            }
                        }
                        boolean f= false;
                        if(fla==1){
                            f=true;
                        }
                        carte riga = new carte(collezione[i],immm[i],conta,f);
                        lista.add(riga);
                    }
                    dataAdapter = new CustomAdapter(MainActivity5.this, R.layout.elenco2, lista,1,'c');
                    listView.setAdapter(dataAdapter);

            }
            }else  if(bundle.containsKey("profilo")) {
                String value = bundle.getString("profilo");
                String vv = value.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                JSONObject obj = null;
                try {
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                    if(stato==1){

                        JSONArray ob = obj.getJSONArray("carte");
                        int i=0;

                        for(;i<ob.length();i=i+1){
                            arr[i] = ob.getString(i);
                        }
                        //arr adesso contiene i valori delle carte


                    }else {
                        Toast toast = Toast.makeText(MainActivity5.this, "si e' verificato un problema.", Toast.LENGTH_LONG);
                        toast.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }   else if(bundle.containsKey("collezzz")){

                    collezione = bundle.getStringArray("collezzz");//collezione contiene l'array delle carte collezionabili
                    immm = new Drawable[collezione.length];
                    Bundle datipassati = getIntent().getExtras();
                    String nick= datipassati.getString("nick");
                    String pass= datipassati.getString("pass");
                    Handler handler = new MyHandler();
                    prendi_collez thr = new prendi_collez(handler,nick,pass,httpcl,collezione,0); //il flag 0 indica il caricamento delle carte
                    thr.start();
                }else if(bundle.containsKey("avviso")){
                String al = bundle.getString("avviso");
                Toast toast = Toast.makeText(MainActivity5.this, al, Toast.LENGTH_LONG);
                toast.show();
            }
            }
        }
    }




