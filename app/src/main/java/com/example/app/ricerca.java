package com.example.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 06/08/14.
 */
class ricerca extends Thread {
    private Handler handler;
    private HttpClient httpclient;
    private HttpResponse response = null;
    private int flag=0;
    private int avanti=0;
    private String paag;

    public ricerca(Handler handler ,HttpClient ht,int fl,String pag) {
        this.handler = handler;
        this.httpclient = ht;
        this.avanti = fl;
        this.paag = pag;
    }
    public void run() {

if(avanti == 1) { //devo stoppare
    try {
    String finale2 ="http://giocodicarte.altervista.org/break_while.php";
    HttpPost httppost = new HttpPost(finale2);
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
    nameValuePairs.add(new BasicNameValuePair("del", "tele"));
    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    response = httpclient.execute(httppost);
    } catch (IOException e) {
        e.printStackTrace();
    }

}else {
        while(true){



        if(flag==1){

            try {
            //prima che esce risetta il valore a 0
            String finale3 ="http://giocodicarte.altervista.org/break_finish.php";
            HttpPost httppost = new HttpPost(finale3);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httppost);

            } catch (IOException e) {
                e.printStackTrace();
            }
            break; //esci dal while, non stai piu' giocando
        }
            if(paag != "") {

        try { //questo thread continuera' anche ad activity spenta

            Thread.sleep(10000);
            String finale ="http://giocodicarte.altervista.org/"+paag;
            HttpPost httppost = new HttpPost(finale);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            String responseAsString = EntityUtils.toString(response.getEntity());


            try {

                String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                JSONObject obj = null;
                obj = new JSONObject(vv);

                if(paag == "server.php"){
                int stato = obj.getInt("found");

                if((stato==1)){ //si inizia a giocare
                    String ult ="http://giocodicarte.altervista.org/your_chan.php";
                    HttpPost httppostt = new HttpPost(ult);
                    List<NameValuePair> nameValuePairss = new ArrayList<NameValuePair>(1);
                    httppostt.setEntity(new UrlEncodedFormEntity(nameValuePairss));
                    // Execute HTTP Post Request
                    response = httpclient.execute(httppostt);
                    String responseAs = EntityUtils.toString(response.getEntity());
                    JSONObject obj2 = null;
                    String ade = responseAs.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    obj2 = new JSONObject(ade);
                    String ci = obj2.getString("your_ch");//prendi l'ultimo canale registrato per l'utente ed eliminalo da parse.com
                    notifyMessage4(ci);
                    String nick = obj.getString("nick");
                    flag=1; // per fermare il while
                    notifyMessage(nick);
                    //altro
                }else if(stato==-1){ //errore
                    flag=1;
                    notifyMessage("0");
                    //forse non bisogna aggiornare nulla

                }
                }else {
                    int stato = obj.getInt("stato");

                if(stato == 1 ){ //si inizia a giocare
                    String cann = obj.getString("canale");
                    flag=1; // per fermare il while
                    notifyMessage2(cann);
                    //altro
                }else if(stato == 2){ //in attesa
                    String inf = obj.getString("informazione");
                    notifyMessage3(inf);
                }else if(stato == 0) { //time_out
                    flag=1;
                    String inf2 = obj.getString("informazione");
                    notifyMessage3(inf2);
                }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

            //devo ancora continuare? o fare un break?
            try {
            String finale2 ="http://giocodicarte.altervista.org/break_while.php";
            HttpPost httppost = new HttpPost(finale2);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            response = httpclient.execute(httppost);
            String responseAsString = EntityUtils.toString(response.getEntity());

                try {
                    String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    JSONObject obj = null;
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                    if((stato == 0) || (stato == -1)){ //ferma while
                       flag=1;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } //fine while(true)

}
    }

    private void notifyMessage(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("sfida", "" + str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage2(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("ini", "" + str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage3(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("infoo", "" + str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage4(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("prendi_canale", "" + str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}
