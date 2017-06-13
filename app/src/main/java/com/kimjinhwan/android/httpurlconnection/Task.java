package com.kimjinhwan.android.httpurlconnection;

import android.os.AsyncTask;

import static com.kimjinhwan.android.httpurlconnection.R.id.textView;
import static com.kimjinhwan.android.httpurlconnection.Remote.getData;

/**
 * Created by XPS on 2017-06-12.
 */

public class Task {

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



}
