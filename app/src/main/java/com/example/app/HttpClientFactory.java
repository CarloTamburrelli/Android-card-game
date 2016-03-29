package com.example.app;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

/**
 * Created by tamburrelli on 19/08/14.
 */
public class HttpClientFactory {
    /* questa classe è stata necessaria per evitare il sovraccarico di più richieste differenti da parte della classe HttpCLient
     altrimenti ci sarebbero stati frequenti crash all'interno dell'applicazione */
    private static DefaultHttpClient client;

    public synchronized static DefaultHttpClient getThreadSafeClient() {

        if (client != null)
            return client;

        client = new DefaultHttpClient();

        ClientConnectionManager mgr = client.getConnectionManager();

        HttpParams params = client.getParams();
        client = new DefaultHttpClient(
                new ThreadSafeClientConnManager(params,
                        mgr.getSchemeRegistry()), params);

        return client;
    }
}
