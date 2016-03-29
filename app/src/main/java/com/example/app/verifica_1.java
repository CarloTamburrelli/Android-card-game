package com.example.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 06/08/14.
 */
class verifica_1 extends Thread {
    private Handler handler;
    private HttpClient httpclient;
    private HttpResponse response = null;
    int plus=0;
    int flus=0;
    int poi=0;

    public verifica_1(Handler handler ,HttpClient ht,int fin) {
        this.handler = handler;
        this.httpclient = ht;
        this.poi = fin;
    }


    public void run() {
        while(true){

            if(flus==1){
                break;
            }
            flus=1;
        JSONObject obj = null;
        try {
        String finale ="http://giocodicarte.altervista.org/play.php";
        HttpPost httppost = new HttpPost(finale);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("no_canc","b"));

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


            if(stato==0){
                notifyMessage("Accesso negato.");
            }else { //tutto ok
                JSONArray box = obj.getJSONArray("box");
                int i=0;
                int x=0;
                if(poi == 5){
                    i=4;
                    x=4;
                    poi=9;
                }else if(stato==1) {
                    JSONArray fub = obj.getJSONArray("your_cards");
                    String all[]=new String[5];
                    all[0]=fub.getString(0);
                    all[1]=fub.getString(1);
                    all[2]=fub.getString(2);
                    all[3]=fub.getString(3);
                    all[4]=fub.getString(4);
                    notifyMessage4(all);
                }

                String arr[]= new String[9];
                for(;i < poi;i=i+1){
                    arr[i] = box.getString(i);
                }

                for(;x< poi;x=x+1){
                    if(arr[x].length()==0){ //in quella x-esima casella non e' inserita nessuna carta
                        notifyMessage3("no");
                    }else {
                        URL url = new URL("http://giocodicarte.altervista.org/cards/"+arr[x]+".png");
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        notifyMessage2(byteArray); //si salva tutte le 9 carte sul tavolo da gioco*/
                    }

                }
                notifyMessage5("ok");

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

    private void notifyMessage(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("avviso", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage2(byte [] ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putByteArray("salva", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage3(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("un_save", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage4(String [] arr) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putStringArray("allay",arr);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage5(String ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("cambio", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }

}
