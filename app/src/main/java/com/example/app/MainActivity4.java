package com.example.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.Parse;

import org.apache.http.client.HttpClient;

public class MainActivity4 extends ActionBarActivity {
    HttpClient httpcl = HttpClientFactory.getThreadSafeClient();
    Bitmap bmp3[]= new Bitmap[16];// (9 carte ) + (1 carta casella vuota)
    int cont=0;
    int cco=0;
    int fl=0;
    int is[] = new int[16]; //questo vettore conterra' 1 se la carta c'e' e 0 se non c'e'.
    String my_card[];
    String prendi="";
    AlertDialog alert=null;
    AlertDialog.Builder builder;
    int no_alert=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main4);
        Parse.initialize(MainActivity4.this, getString(R.string.parseAppID), getString(R.string.parseClientID)); //necessario per l'anti-crash
        //dialog = ProgressDialog.show(MainActivity4.this, "", "Caricamento in corso...", true);
        setTitle("Partita in corso...");
        if(MainActivity4.this.isFinishing()!= true){
            builder=new AlertDialog.Builder(MainActivity4.this);
            builder.setMessage("Caricamento in corso...");
            builder.setCancelable(true);
            alert=builder.create();
            alert.show();
        }
        Handler handler = new MyHandler();
        verifica_1 thr = new verifica_1(handler,httpcl,4); //resetta tutti i bottoni, inizializzatore
        thr.start();
        Button biton = (Button) findViewById(R.id.abb);
        biton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity4.this);

                builder.setTitle("Conferma");
                builder.setMessage("Stai per abbandonare la partita, sei sicuro?");

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        dialog.dismiss();
                        Handler handler = new MyHandler();
                        richiesta thr = new richiesta(handler,httpcl,10,""); //invia la richiesta post con la carta scelta
                        thr.start();
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
        });

    }

    @Override
    protected void onStop() {
        if (alert!=null) {
            if (alert.isShowing()) {
                alert.dismiss();
            }
        }
        super.onPause();
        finish();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if(bundle.containsKey("salva")) { //qui arriveranno tutte le immagini e verranno salvate pian piano.
                ImageView im = (ImageView) findViewById(R.id.bot1);
                Button im2 = (Button) findViewById(R.id.bot10);

                int he =im.getHeight();
                int wi =im.getWidth();
                int he2 =im2.getHeight();
                int wi2 =im2.getWidth();


                byte[] value = bundle.getByteArray("salva");
                //in questo if si entra solamente per salvare le 9 carte sul campo
                Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length); //DA BYTE A BITMAP
                int larg;
                int alt;
                if(cont<9){
                    larg=wi;
                    alt=he;
                }else {
                    larg=wi2;
                    alt=he2;
                }
                bmp3[cont] = Bitmap.createScaledBitmap(bmp, larg,alt, false);
                is[cont]=1;//la carta nella cont-esima casella esiste !
                cont=cont+1;

                }else if(bundle.containsKey("vuot")) { //e' l'ultima drawable caricata ovvero quella della casella vuota ;D

                //qui bisogna inserire tutte le carte nelle loro posizioni
                ImageView im = (ImageView) findViewById(R.id.bot1); //dimensione piu' grande della casella vuota
                Button im2 =(Button) findViewById(R.id.bot10); //dimensione piu' piccola della casella vuota
                int he =im.getHeight();
                int wi =im.getWidth();
                int he2 =im2.getHeight();
                int wi2 =im2.getWidth();

                byte[] value = bundle.getByteArray("vuot");
                //in questo if si entra solamente per caricare le 9 carte sul campo
                Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length); //DA BYTE A BITMAP
                bmp3[cont] = Bitmap.createScaledBitmap(bmp, wi, he, false);
                is[cont]=1;
                cont=cont+1;
                bmp3[cont] = Bitmap.createScaledBitmap(bmp, wi2, he2, false);
                is[cont]=1;
                cont=cont+1;
                int i=0;
                for(;i<cont;i=i+1){ //completa l'array bmp3 con le immagini delle caselle vuote
                    if(i<9){
                    if(is[i]==0)  {
                        bmp3[i]=bmp3[cont-2];
                    }
                    }else { //nel caso e' il nostro turno e si stanno inserendo anche le nostre 5 carte
                        if(is[i]==0)  {
                            bmp3[i]=bmp3[cont-1];
                        }
                    }
                }
                /*if((dialog!=null && dialog.isShowing())){
                    dialog.dismiss();
                }*/
                if (alert!=null) {
                    if (alert.isShowing()) {
                        alert.dismiss();
                    }
                }

                ImageView b0 = (ImageView) findViewById(R.id.bot0);
                b0.setImageBitmap(bmp3[0]);
                ImageView b1 = (ImageView) findViewById(R.id.bot1);
                b1.setImageBitmap(bmp3[1]);
                ImageView b2 = (ImageView) findViewById(R.id.bot2);
                b2.setImageBitmap(bmp3[2]);
                ImageView b3 = (ImageView) findViewById(R.id.bot3);
                b3.setImageBitmap(bmp3[3]);
                ImageView b4 = (ImageView) findViewById(R.id.bot4);
                b4.setImageBitmap(bmp3[4]);
                ImageView b5 = (ImageView) findViewById(R.id.bot5);
                b5.setImageBitmap(bmp3[5]);
                ImageView b6 = (ImageView) findViewById(R.id.bot6);
                b6.setImageBitmap(bmp3[6]);
                ImageView b7 = (ImageView) findViewById(R.id.bot7);
                b7.setImageBitmap(bmp3[7]);
                ImageView b8 = (ImageView) findViewById(R.id.bot8);
                b8.setImageBitmap(bmp3[8]);
                fl=0;
                if(cont==16){ //significa che e' il nostro turno quindi fa vedere anche le nostre carte
                    final Button b9 = (Button) findViewById(R.id.bot9);
                    final Button b10 = (Button) findViewById(R.id.bot10);
                    final Button b11 = (Button) findViewById(R.id.bot11);
                    final Button b12 = (Button) findViewById(R.id.bot12);
                    final Button b13 = (Button) findViewById(R.id.bot13);
                    Drawable dra9= new BitmapDrawable(getResources(),bmp3[9]);
                    b9.setBackgroundDrawable(dra9);
                    b9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(my_card[0].length()!=0){
                            b9.setText("x");
                            b10.setText("");
                            b11.setText("");
                            b12.setText("");
                            b13.setText("");

                            prendi= my_card[0];
                            }

                        }
                    });

                    Drawable dra10= new BitmapDrawable(getResources(),bmp3[10]);
                    b10.setBackgroundDrawable(dra10);
                    b10.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(my_card[1].length()!=0){
                            b10.setText("x");
                            b9.setText("");
                            b11.setText("");
                            b12.setText("");
                            b13.setText("");
                            prendi= my_card[1];
                            }

                        }
                    });

                    Drawable dra11= new BitmapDrawable(getResources(),bmp3[11]);
                    b11.setBackgroundDrawable(dra11);
                    b11.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(my_card[2].length()!=0){
                            b11.setText("x");
                            b10.setText("");
                            b9.setText("");
                            b12.setText("");
                            b13.setText("");
                            prendi= my_card[2];
                            }

                        }
                    });

                    Drawable dra12= new BitmapDrawable(getResources(),bmp3[12]);
                    b12.setBackgroundDrawable(dra12);
                    b12.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(my_card[3].length()!=0){
                            b12.setText("x");
                            b10.setText("");
                            b11.setText("");
                            b9.setText("");
                            b13.setText("");
                            prendi= my_card[3];
                            }

                        }
                    });


                    Drawable dra13= new BitmapDrawable(getResources(),bmp3[13]);
                    b13.setBackgroundDrawable(dra13);
                    b13.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(my_card[4].length()!=0){
                            b13.setText("x");
                            b10.setText("");
                            b11.setText("");
                            b12.setText("");
                            b9.setText("");
                            prendi= my_card[4];
                            }

                        }
                    });
                    if(is[0]==0){ //non c'e' nessuna carta nella prima casella
                        b0.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                Handler handler = new MyHandler();
                                richiesta thr = new richiesta(handler,httpcl,0,prendi); //invia la richiesta post con la carta scelta
                                thr.start();
                                }

                            }
                        });
                    }
                    if(is[1]==0){
                        b1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,1,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[2]==0){
                        b2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,2,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[3]==0){
                        b3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,3,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[4]==0){
                        b4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,4,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[5]==0){
                        b5.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,5,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[6]==0){
                        b6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,6,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[7]==0){
                        b7.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,7,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }
                    if(is[8]==0){
                        b8.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(prendi!=""){
                                    Handler handler = new MyHandler();
                                    richiesta thr = new richiesta(handler,httpcl,8,prendi); //invia la richiesta post con la carta scelta
                                    thr.start();
                                }

                            }
                        });
                    }

                }else {
                    Button b9 = (Button) findViewById(R.id.bot9);

                    Button b10 = (Button) findViewById(R.id.bot10);

                    Button b11 = (Button) findViewById(R.id.bot11);

                    Button b12 = (Button) findViewById(R.id.bot12);

                    Button b13 = (Button) findViewById(R.id.bot13);

                    //rimuovi 5 bottoni e inserisci testo
                    b9.setVisibility(View.INVISIBLE);
                    b10.setVisibility(View.INVISIBLE);
                    b11.setVisibility(View.INVISIBLE);
                    b12.setVisibility(View.INVISIBLE);
                    b13.setVisibility(View.INVISIBLE);
                    if(MainActivity4.this.isFinishing()!= true && no_alert == 0){
                        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity4.this);
                        builder.setTitle("In attesa");
                        builder.setMessage("E' il turno dell'altro giocatore.");
                        builder.setCancelable(true);
                        AlertDialog alert=builder.create();
                        alert.show();
                    }
                }

            }else if(bundle.containsKey("un_save")) {
            is[cont]=0; // Non c'e' nessuna carta nella cont-esima casella.
            cont =cont+1;
        }else if(bundle.containsKey("avviso")) {
                String value = bundle.getString("avviso");
                Toast toast= Toast.makeText(MainActivity4.this, value,Toast.LENGTH_SHORT);
                toast.show();
            }else if(bundle.containsKey("cambio")) {
                //passa all'altro thread per allegerire la richiesta
                if(cco == 1){
                   Handler handler = new MyHandler();
                    verifica_2 thr = new verifica_2(handler,httpcl); //resetta tutti i bottoni, inizializzatore
                    thr.start();
                }else {

                    Handler handler = new MyHandler();
                    verifica_1 thr = new verifica_1(handler,httpcl,5); //resetta tutti i bottoni, inizializzatore
                    thr.start();
                }
                cco=cco+1;
            }else if(bundle.containsKey("allay")) {
                 my_card =bundle.getStringArray("allay");
            }else if(bundle.containsKey("next_activity")){
                //cambio di activity da 4 a 4
                Intent nuovaPagina = new Intent(MainActivity4.this, MainActivity4.class);
                Bundle datipassati = getIntent().getExtras();
                String canal = datipassati.getString("canale");
                String uu = datipassati.getString("nick");
                String pp = datipassati.getString("pass");
                nuovaPagina.putExtra("nick",uu);
                nuovaPagina.putExtra("pass",pp);
                nuovaPagina.putExtra("canale",canal);
                startActivity(nuovaPagina);
                finish();
            }else if(bundle.containsKey("finalz")){
                no_alert=1;
                String [] win_lose = bundle.getStringArray("finalz");
                String vincitore = win_lose[0];
                String io = win_lose[2];
                String perdente = win_lose[1];

                if(vincitore.equals(io)) {
                    AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity4.this);
                    miaAlert.setTitle("Sei il vincitore!");
                    miaAlert.setMessage("Hai appena vinto una nuova carta!");
                    AlertDialog alert = miaAlert.create();
                    alert.setIcon(R.drawable.belin);
                    alert.show();
                }else {
                    AlertDialog.Builder miaAlert = new AlertDialog.Builder(MainActivity4.this);
                    miaAlert.setTitle("Partita terminata");
                    miaAlert.setMessage("Il vincitore e' " + win_lose[0]);
                    AlertDialog alert = miaAlert.create();
                    alert.show();
                }
            }else if(bundle.containsKey("lefft")){
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity4.this);
                builder.setTitle("Partita terminata");
                builder.setMessage("Hai abbandonato la partita!");
                builder.setCancelable(true);
                AlertDialog alert=builder.create();
                alert.show();
            }
        }
    }


}
