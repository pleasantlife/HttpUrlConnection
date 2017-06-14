package com.kimjinhwan.android.httpurlconnection;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
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



    //한 페이지에 불러오는 데이터 수
    static final int PAGE_OFFSET = 10;
    int page = 0;

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

        setViews();
        setListener();
        setMap();
    }

    private void setViews(){
        listView = (ListView) findViewById(R.id.listView);
        textView = (TextView) findViewById(R.id.textView);


        //데이터 - 위에서 공간 할당 됨
        //어댑터
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);

    }

    private void setListener(){
        //스크롤의 상태값을 체크해주는 리스너
        //리스트의 마지막 아이템이 보이는지 여부 확인
        listView.setOnScrollListener(scrollListener);
    }

    private void setMap(){
        //맵을 세팅
        FragmentManager manager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) manager.findFragmentById(R.id.mapView);
        //로드되면 onReady 호출하도록
        mapFragment.getMapAsync(this);
    }

        //리스트의 마지막 아이템이 보이는지 여부
        boolean lastItemVisible = false;
        //스크롤 리스너
        AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //IDLE = 스크롤바가 동작하지 않는 상태
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && lastItemVisible) {
                    loadPage();
                }
            }

            // firstVisibleItem = 현재 화면에 보여지는 첫번째 아이템의 번호
            // visibleItemCount = 현재 화면에 보여지는 아이템의 개수(1px라도 보이면 개수에 포함)
            // totalItemCount = 리스트에 담겨있는 전체 아이템의 개수
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount <= firstVisibleItem + visibleItemCount) {
                    lastItemVisible = true;
                } else {
                    lastItemVisible = false;
                }
            }
        };


        //페이지를 로드한다.
        private void loadPage(){
            nextPage();
            setUrl();
            Remote.newTask(MainActivity.this);
        }


         private void nextPage(){
            page = page + 1;

        }

        private void setUrl() {

            int end = page * PAGE_OFFSET;
            int begin = end - PAGE_OFFSET + 1;
            url = URL_PREFIX + URL_CERT + URL_MID + begin + "/" + end + "/" ;

         /*
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
        */

        }


        @Override
         public String getUrl() {
        return url;
    }

        @Override
        public void postExecute(String jsonString){
           //json 스트링을 Data 오브젝트로 변환
            Data data = convertJson(jsonString);

            //사용해야하는 데이터만 꺼내서 담아둔다.
            int totalCount = data.getSearchPublicToiletPOIService().getList_total_count();
            Row items[] = data.getSearchPublicToiletPOIService().getRow();

            //총 개수를 화면에 출력
            setItemCount(totalCount);

            //네트웍에서 가져온 데이터를 꺼내서 datas에 담아준다.
            addDatas(items);

            addMarkers(items);



            // 지도 컨트롤
            LatLng sinsa = new LatLng(37.516066, 127.019361);
            moveMapPosition(sinsa);

            //adapter를 갱신해준다.
            adapter.notifyDataSetChanged();

    }

    //지도 이동
    private void moveMapPosition(LatLng position) {
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
    }

    // datas에 데이터 더하기
    private void addDatas(Row[] items){
        for(Row item : items){
            datas.add(item.getFNAME());
        }
    }

    //지도에 마커 생성
    private void addMarkers(Row[] items) {
        for (Row row : items) {
            //row를 돌면서 좌표의 마커를 생성한다.
            MarkerOptions marker = new MarkerOptions();
            LatLng tempCoord = new LatLng(row.getY_WGS84(), row.getX_WGS84());
            marker.position(tempCoord);
            marker.title(row.getFNAME());

            myMap.addMarker(marker);
        }
    }

    //총 개수를 화면에 출력
    private void setItemCount(int totalCount){
        textView.setText("총 개수 : " + totalCount);
    }

    //json 스트링을 Data 오브젝트로 변환
    public Data convertJson(String jsonString) {
        Gson gson = new Gson();

        // Gson의 역할
        // 1. json String -> class로 변환
        return gson.fromJson(jsonString, Data.class);
        // 2. Class를 json String으로 변환
    }


    GoogleMap myMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        loadPage();
    }
}
