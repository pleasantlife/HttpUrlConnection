package com.kimjinhwan.android.httpurlconnection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by XPS on 2017-06-12.
 */

public class Remote {


    public static void newTask(final TaskInterface taskInterface){        //void로 넣어서 return을 하지 않도록 해야함.


        new AsyncTask<String, Void, String>(){
            //백그라운드 처리를 할 함수인 doInBackground를 오버라이드 해준다.


            @Override
            protected String doInBackground(String... params) {
                String result = "";
                try {
                    //getData 함수로 데이터를 가져온다.
                    result = getData(params[0]);
                    //Result를 화면에 출력하세요.

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                //결과처리
                taskInterface.postExecute(result);
            }
        }.execute(taskInterface.getUrl());      //주소값을 String으로 넘기기 때문에!
    }








    //인자로 받은 url로 네트웍을 통해 데이터를 가져오는 함수!
    //처음에 가져올땐 무조건 String!
    public static String getData(String url) throws Exception{     //<- 요청한 곳에서 Exception 처리를 해준다. (try/catch 아님!)
        String result = "";

        //네트웍 처리
        //1. 요청처리 request(서버 url 연결!)
        //1-1. URL 객체 만들기
        URL serverUrl = new URL(url);

        //1-2. 연결객체 생성 (DBHelper처럼 HttpURLConnection이 네트워크 데이터를 연결해줌)
        //Https를 사용하는 네트워크와 연결하려면 HttpsURLConnection을 사용해야 함
        HttpURLConnection con = (HttpURLConnection) serverUrl.openConnection();             //url 객체에서 연결을 꺼낸다.

        //1-3. http Method 결정
        con.setRequestMethod("GET");    //가져와!


        //2. 응답처리 (Response)
        //2-1. 응답코드 분석
        int responseCode = con.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK){  //정상적인 코드일 시 처리!

            String temp = null;

            BufferedReader br = new BufferedReader( new InputStreamReader( con.getInputStream() ) );
            while((temp = br.readLine()) != null){
                result += temp;
            }
            //2-3. 오류에 대한 응답처리
        } else {
            // 각자 호출측으로 Exception을 만들어서 넘겨줄 것!(오류처리)
            Log.e("Network", "error_code = " + responseCode);
        }

        return result;
    }
}
