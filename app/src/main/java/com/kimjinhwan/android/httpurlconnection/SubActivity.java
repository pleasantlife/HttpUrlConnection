package com.kimjinhwan.android.httpurlconnection;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SubActivity extends AppCompatActivity {

    TextView txtResult;
    final String url = "http://13.124.140.9:3456/exhibition_list";
    String complete = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = (TextView) findViewById(R.id.txtResult);
        inputStreamConnection(url);
    }

    public void inputStreamConnection(final String url){

        new AsyncTask<String, Void, String>(){
            StringBuilder stringBuilder = new StringBuilder();
            @Override
            protected String doInBackground(String... params) {
                try {
                    URL serverUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
                    urlConnection.setRequestMethod("GET");
                    BufferedReader bfReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    String test = "";

                    while((test = bfReader.readLine()) != null){
                        stringBuilder.append(test);
                    }
                    complete = stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return complete;
            }

            @Override
            protected void onPostExecute(String complete) {
                super.onPostExecute(complete);
                Log.e("Result", complete);
                txtResult.setText(complete);
            }
        }.execute(url);
    }
