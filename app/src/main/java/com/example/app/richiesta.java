package com.example.app;

/**
 * Created by tamburrelli on 19/08/14.
 */

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 06/08/14.
 */
class richiesta extends Thread {
    private Handler handler;
    private HttpClient httpclient;
    private HttpResponse response = null;
    private String card;
    private int posizione;
    int flus=0;

    public richiesta(Handler handler ,HttpClient ht,int psz,String carta) {
        this.handler = handler;
        this.httpclient = ht;
        this.card=carta;
        this.posizione=psz;
    }

    public void run() {
        while(true){

            if(flus==1){
                break;
            }
            flus=1;
            JSONObject obj = null;
            if(posizione==10){


                try {
                    String finale ="http://giocodicarte.altervista.org/play.php";
                    HttpPost httppost = new HttpPost(finale);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("abbandona", "yes"));
                    nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                    response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    if(entity == null){//richiesta fallita
                        flus=0; //ripeti while
                    }else {
                        String responseAsString = EntityUtils.toString(response.getEntity());

                        String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                        obj = new JSONObject(vv);
                        int stato = obj.getInt("stato");//0 = accesso negato,1 = il mio turno , 2 = attesa , 3 = vittoria del giocatore ,4 = tempo scaduto
                        if(stato==4){ //tutto ok
                            notifyMessage3("lefft");
                        }else {
                            flus=0;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    flus=0;
                } catch (JSONException e) {
                    e.printStackTrace();
                    flus=0;
                }




            }else {
            try {
                String finale ="http://giocodicarte.altervista.org/play.php";
                HttpPost httppost = new HttpPost(finale);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("carta",card));
                nameValuePairs.add(new BasicNameValuePair("box", ""+posizione));
                nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();

                if(entity == null){//richiesta fallita
                    flus=0; //ripeti while
                }else {
                    String responseAsString = EntityUtils.toString(response.getEntity());

                    String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");//0 = accesso negato,1 = il mio turno , 2 = attesa , 3 = vittoria del giocatore ,4 = tempo scaduto
                    if(stato==2){ //tutto ok
                    notifyMessage("next activity");
                    }else if(stato==-1){ //carta gia' inserita o altri errori
                    notifyMessage2("errore");
                    }else if(stato==4){ //vittoria di uno dei due giocatori
                    notifyMessage2("vittoria");
                    }


                }

            } catch (IOException e) {
                e.printStackTrace();
                flus=0;
            } catch (JSONException e) {
                e.printStackTrace();
                flus=0;
            }
            }

        }
    }

    private void notifyMessage(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("next_activity", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage2(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("avviso", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage3(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("lefft", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }

}

