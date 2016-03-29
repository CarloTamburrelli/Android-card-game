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
class verifica_2 extends Thread {
    private Handler handler;
    private HttpClient httpclient;
    private HttpResponse response = null;
    int flus=0;

    public verifica_2(Handler handler ,HttpClient ht) {
        this.handler = handler;
        this.httpclient = ht;
    }
    public void run() {
        while(true) {
            if(flus==1){
                break;
            }
            flus=1;
        try {
            String finale ="http://giocodicarte.altervista.org/play.php";
            HttpPost httppost = new HttpPost(finale);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request

             response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            if(entity == null){//richiesta fallita
                flus=0; //ripeti while
            }else {
            String responseAsString = EntityUtils.toString(response.getEntity());

            String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
            JSONObject obj = null;
            obj = new JSONObject(vv);
            int stato = obj.getInt("stato");
            if(stato==0){
                notifyMessage("Accesso negato.");
            }else if(stato==1){ //e' il tuo turno

                JSONArray mazzo = obj.getJSONArray("your_cards"); //le 5 carte
                int j=0;
                String arr2[]= new String[5];
                for(;j<5;j=j+1){
                    arr2[j]= mazzo.getString(j);
                }
                int y=0;
                for(;y<5;y=y+1){
                    if(arr2[y].length()==0){ //in quella x-esima casella non e' inserita nessuna carta
                        notifyMessage3("no");
                    }else {

                        URL url = new URL("http://giocodicarte.altervista.org/cards/"+arr2[y]+"g.png");
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        notifyMessage2(byteArray);
                    }
                }
                try {
                    URL url = new URL("http://giocodicarte.altervista.org/cards/vuoto.png");
                    Bitmap bmp = null;
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    notifyMessage4(byteArray);
                } catch (IOException e) {
                    flus=0;
                    e.printStackTrace();
                }


            }else if((stato==2) || (stato==3) || (stato ==4)){
                try {
                    URL url = new URL("http://giocodicarte.altervista.org/cards/vuoto.png");
                    Bitmap bmp = null;
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    if(stato==3 || stato==4){
                        String ecco[] = new String[3];
                               ecco[0] = obj.getString("win");
                               ecco[1] = obj.getString("lose");
                               ecco[2] = obj.getString("ioo");
                        notifyMessage5(ecco);
                    }

                    notifyMessage4(byteArray);
                } catch (IOException e) {
                    flus=0;
                    e.printStackTrace();
                }
            }

            }
        } catch (IOException e) {
            e.printStackTrace();
            flus=0;
        } catch (JSONException e) {
            e.printStackTrace();
            flus=0;
        }

        }//fine del while

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
        b.putByteArray("salva",ar);
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
    private void notifyMessage4(byte [] ar) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putByteArray("vuot",ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage5(String ar []) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putStringArray("finalz",ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}