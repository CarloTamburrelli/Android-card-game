package com.example.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamburrelli on 02/08/14.
 */
class login extends Thread {
    private Handler handler;
    private int i = 0;
    private int flag = 0;
    private String user="";
    private String password="";
    private HttpClient httpclient;
    private String url;
    public login(Handler handler,String usr,String psw,HttpClient http,String urr) {
        this.handler = handler;
        this.user=usr;
        this.password=psw;
        this.httpclient = http;
        this.url = urr;
    }
    public void run() {
        if(url.equals("system.php")){
        String finale ="http://giocodicarte.altervista.org/"+url;
            HttpPost httppost = new HttpPost(finale);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("nick",user));
            nameValuePairs.add(new BasicNameValuePair("pass", password));
            nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            String responseAsString = EntityUtils.toString(response.getEntity());
            notifyMessage(responseAsString); // <--- Passa l'output della pagina all'handler
    } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }else if(url.equals("profilo.php")) {

            String finale ="http://giocodicarte.altervista.org/"+url;
            HttpPost httppost = new HttpPost(finale);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("andr", "tele"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String responseAsString = EntityUtils.toString(response.getEntity());
                notifyMessage2(responseAsString); // <--- Passa l'output della pagina all'handler
                /*allora */
                try {
                    String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    JSONObject obj = null;
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                    if(stato==1){
                        JSONArray ob = obj.getJSONArray("carte");
                        JSONArray bul = obj.getJSONArray("collezione");
                        String col [] = new String[bul.length()];
                        int kk=0;
                        while(kk< bul.length()){
                            col[kk]=bul.getString(kk);
                            kk=kk+1;
                        }
                        notifyMessage4(col);
                        if((!user.equals("")) && (!password.equals(""))){
                        int i=0;
                        String arr[]= new String[ob.length()];
                        for(;i<ob.length();i=i+1){
                            arr[i] = ob.getString(i);
                        }
                        int x=0;
                        for(;x<5;x=x+1){

                            URL url = new URL("http://giocodicarte.altervista.org/cards/"+arr[x]+"g.png");
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            notifyMessage3(byteArray);
                        }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*allora fine */

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(url.equals("vedi.php")){



            String finale ="http://giocodicarte.altervista.org/"+url;
            HttpPost httppost = new HttpPost(finale);
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String responseAsString = EntityUtils.toString(response.getEntity());
                /*allora */
                try {
                    String vv = responseAsString.replace('\"','\''); //il json in php utilizza " e non ' <--- un problema per android
                    JSONObject obj = null;
                    obj = new JSONObject(vv);
                    int stato = obj.getInt("stato");
                    if(stato==1){ //c'e' una partita da giocare
                        notifyMessage5("1");
                    }else if(stato==0){
                        notifyMessage5("0");
                    }else if(stato==2){ //nessuna partita da giocare
                        notifyMessage5("2");
                    }


    } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

            private void notifyMessage(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("login", ""+str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage2(String str) {
        Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("profilo", ""+str);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage3(byte [] ar) {
        Message msg = handler.obtainMessage(); //SI USA PER OTTENERE UN'ISTANZA DELLA CLASSE MESSAGE
        Bundle b = new Bundle();
        b.putByteArray("cards",ar);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage4(String [] arr) {
        Message msg = handler.obtainMessage(); //SI USA PER OTTENERE UN'ISTANZA DELLA CLASSE MESSAGE
        Bundle b = new Bundle();
        b.putStringArray("collezzz", arr);
        msg.setData(b);
        handler.sendMessage(msg);
    }
    private void notifyMessage5(String arr) {
        Message msg = handler.obtainMessage(); //SI USA PER OTTENERE UN'ISTANZA DELLA CLASSE MESSAGE
        Bundle b = new Bundle();
        b.putString("innuendo", arr);
        msg.setData(b);
        handler.sendMessage(msg);
    }
}