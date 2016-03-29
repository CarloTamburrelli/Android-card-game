package com.example.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 02/08/14.
 */
class prendi_collez extends Thread {
    private Handler handler;
    private int flag = 0;
    private String user="";
    private String password="";
    private HttpClient httpclient;
    private String [] arr;
    public prendi_collez(Handler handler,String usr, String psw,HttpClient http,String [] arr,int fl) {
        this.handler = handler;
        this.user=usr;
        this.password=psw;
        this.httpclient = http;
        this.flag = fl;
        this.arr = arr;
    }
    public void run() {
        URL url = null;
        if(flag==0){ //devo solamente prelevare le carte dell'utente
        int i=0;


        try {
            for (;i<arr.length;i=i+1){
            url = new URL("http://giocodicarte.altervista.org/cards/"+arr[i]+"g.png");
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        notifyMessage(byteArray);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else if(flag==1){
            String devo = "!"+arr[0]+"!"+arr[1]+"!"+arr[2]+"!"+arr[3]+"!"+arr[4]+"!)";
            try{
                String finale ="http://giocodicarte.altervista.org/profilo.php";
                HttpPost httppost = new HttpPost(finale);
                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("cartez", devo));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String responseAsString = EntityUtils.toString(response.getEntity());
                try {
                    String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    JSONObject obj = null;
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                    if(stato==1){
                        notifyMessage2("Aggiornamento eseguito");
                    }else if(stato==0){
                        notifyMessage2("Errore");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private void notifyMessage(byte [] ar) {
        Message msg = handler.obtainMessage(); //SI USA PER OTTENERE UN'ISTANZA DELLA CLASSE MESSAGE
        Bundle b = new Bundle();
        b.putByteArray("give", ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage2(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("avviso", ""+str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}