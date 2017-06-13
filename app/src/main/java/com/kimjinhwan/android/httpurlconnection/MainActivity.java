package com.kimjinhwan.android.httpurlconnection;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.kimjinhwan.android.httpurlconnection.domain.Data;
import com.kimjinhwan.android.httpurlconnection.domain.Row;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import static com.kimjinhwan.android.httpurlconnection.Remote.getData;

public class MainActivity extends AppCompatActivity
        implements TaskInterface, OnMapReadyCallback {

    /* 기초정보
       url : http://openAPI.seoul.go.kr:8088/486a78476d706c653833457479494a/json/SearchPublicToiletPOIService/1/5/
       인증키 : 486a78476d706c653833457479494a
    */

    static final String URL_PREFIX = "http://openAPI.seoul.go.kr:8088/";
    static final String URL_CERT = "486a78476d706c653833457479494a";
    static final String URL_MID = "/json/SearchPublicToiletPOIService/";

    int pageBegin = 1;
    int pageEnd = 10;

    //한 페이지에 불러오는 데이터 수
    static final int PAGE_OFFSET = 10;

    TextView textView;
    ListView listView;
    String url = "";

    ArrayAdapter<String> adapter;

    //아답터에서 사용할 데이터 공간
    final List<String> datas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);


        //데이터 - 위에서 공간 할당 됨
        //어댑터
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);


        //맵을 세팅
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFragment =  (SupportMapFragment) manager.findFragmentById(R.id.mapView);
        //로드되면 onReady 호출하도록
        mapFragment.getMapAsync(this);










    }

    private void setPage(int page){
        pageEnd = page * PAGE_OFFSET;
        pageBegin = pageEnd - PAGE_OFFSET + 1;

    }

    private void setUrl(int begin, int end) {
        //String
        //String Buffer
        //String Builder


        // String 연산...?!
        // String result = "문자열" + "문자열";
        // result = "문자열문자열";
        // String result = "문자열" + "문자열" + "문자열";
        //                  -----------------
        //                  메모리공간 할당
        //                  -----------------------------
        //                  메모리공간 할당
        // 특별히 복잡하지 않은 스트링 배열은 스트링빌더가 자동으로 변환함.
        // (if문이 들어간다거나...)

        StringBuffer sb = new StringBuffer();           // 동기화를 지원함.
        sb.append("문자열");
        sb.append("문자열");

        StringBuilder sbl = new StringBuilder();        // 동기화를 미지원. (속도가 더 빠름)
        sb.append("문자열");
        sb.append("문자열");

        url = URL_PREFIX + URL_CERT + URL_MID + begin + "/" + end + "/" ;
    }


    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void postExecute(String jsonString){
        Gson gson = new Gson();

        // Gson의 역할
        // 1. json String -> class로 변환
        Data data = gson.fromJson(jsonString, Data.class);
        // 2. Class를 json String으로 변환


        //총 개수를 화면에 세팅
        textView.setText("총 개수 : " + data.getSearchPublicToiletPOIService().getList_total_count());

        //건물의 이름을 listView에 세팅

        Row rows[] = data.getSearchPublicToiletPOIService().getRow();

        //네트웍에서 가져온 데이터를 꺼내서 datas에 담아준다.
        for(Row row : rows){
            datas.add(row.getFNAME());

            //row를 돌면서 좌표의 마커를 생성한다.
            MarkerOptions marker = new MarkerOptions();
            LatLng tempCoord = new LatLng(row.getY_WGS84(), row.getX_WGS84());
            marker.position(tempCoord);
            marker.title(row.getFNAME());

            myMap.addMarker(marker);



        }
        //adapter를 갱신해준다.
        adapter.notifyDataSetChanged();

        // 지도 컨트롤
        LatLng sinsa = new LatLng(37.516066, 127.019361);
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sinsa, 10));
    }

    GoogleMap myMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        //최초 호출시, 첫 번째 집합을 불러온다.
        setPage(1);
        setUrl(pageBegin, pageEnd);

        Remote.newTask(this);






    }
}
