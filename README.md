# HttpURLConnection

## HttpURLConnection 이란?
 - Http의 고유한 기능을 지원하는 URLConnection
 - URLConnection은 어플리케이션과 URL 사이의 통신 링크를 나타내는 클래스.

## HttpURLConnection의 쓰임?
 - URL로 웹에 접속하여, 데이터베이스에 접근한 후 데이터를 다시 가져와야 할 때 사용.
 - 여기서 웹(Web)은 HTML, JSP, PHP 등을 포함한다.

## HttpURLConnection 사용시 제약 조건
 - 반드시 메인스레드가 아닌 서브 스레드를 구성하여 실행해야 한다.
 > 수 초 정도 걸리는 작업을 메인스레드에서 수행하게 되면, 앱 전체가 다운되는 ANR-Android Not Responding- 상태에 빠지기 때문이다.

 - 따라서 AsyncTask라는 별도의 스레드를 이용하여 실행해야 함.

## 본격적으로 구현하기에 앞서 준비할 것.
 - AndroidManifest.xml에서 인터넷 접속 권한 부여
 - String 변수로 접속할 주소 설정해 놓기

## 코드 구현

 - 한 개의 클래스에 AsyncTask를 이용하는 함수를 생성하여 함수에서 httpURLConnection 실행


```java

public class MainActivity extends AppCompatActivity {

    TextView txtResult; // 웹에서 받아온 정보를 나타낼 텍스트뷰 선언
    final String url = "http://13.124.140.9:3456/exhibition_list"; // 서버 주소를 String으로 미리 정의.
    String complete = ""; // 웹에서 받아온 정보를 넣어올 String 객체 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = (TextView) findViewById(R.id.txtResult); // 텍스트뷰 객체 등록
        httpUrlConnection(url); // httpURLConnection을 구현하는 함수 실행(실행 시 서버 주소를 괄호 안에 넣음)
    }

    /*
    * httpURLConnection 구현하는 함수
    */
    public void httpUrlConnection(final String url){
        /*
        * AsyncTask로 별도의 Thread를 생성하여 httpURLConnection을 구현한다.
        * AsyncTask의 제네릭 (꺽쇠 안에 들어가는 세 개의 타입)의 의미는 각각 아래와 같다.
        * <doInBackground 함수안에 들어가는 매개변수타입, onProgressUpdate 함수안에 들어가는 매개변수타입, doInBackground의 리턴타입>
        */
        new AsyncTask<String, Void, String>(){
            StringBuilder stringBuilder = new StringBuilder();
            // 여기서는 text를 받아오는만큼, String으로 처리함.
            @Override
            protected String doInBackground(String... params) {
                try {
                    //URL 객체 생성 (URL : 네트워크 상에서 자원이 어디있는지를 알려주는 규약. 안드로이드에서는 객체로 사용하여 주소를 구분함.)
                    URL serverUrl = new URL(url);
                    //HttpURLConnection 객체 생성  : URL 객체에 openConnection함수를 붙여서 통신을 한다.
                    HttpURLConnection urlConnection = (HttpURLConnection) serverUrl.openConnection();
                    //현재의 통신을 "GET"메소드로 정의하여 웹에서 내용을 받아올 수 있도록 한다.
                    urlConnection.setRequestMethod("GET");  

                    /*시스템 버퍼로 미리 문자를 읽어들이기 위해 BufferedReader 객체를 생성함.
                    * BufferedInputStream / BufferedOutputStream은 바이트 단위로 처리.
                    * BufferedReader / BufferedWriter는 문자(Char) 단위 처리
                    *
                    * 지금은 내용을 받아오는 상황이기 때문에 BufferedReader안에 InputStreamReader를 사용했다.
                    * HttpURLConnection 객체에 getInputStream 함수를 사용하여 '받아오는'스트림을 활성화했다.
                    */
                    BufferedReader bfReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                    /*
                    * 스트림이라는 데이터의 강에서 건져올린 것들을 담아낼 곳이 필요하다.
                    * 여기서는 텍스트 데이터들을 받아왔으니, String 변수 'test'를 사용한다.
                    */
                    String test = "";

                    //BufferedReader에 담은 내용이 더 이상 존재하지 않을때까지 BufferedReader를 읽어내려가며, 그 내용을 test 변수에 담는다.
                    while((test = bfReader.readLine()) != null){
                        /*
                        * 왜 굳이 String에 있던걸 StringBuilder에 담나요?
                        * 자바에서 String을 담는 3형제 :  StringBuilder >> StringBuffer >> String (성능순.)
                        * 셋의 차이점에 대해서는 ? : http://ooz.co.kr/298 에서 확인할 것!
                        * String 객체는 한 번 생성되면 할당된 메모리 공간이 변하지 않기 때문이며, 연산이 많을 경우 StringBuilder가 성능이 가장 좋음!
                        * 그래서 StringBuilder객체에 담아줌.
                        */
                        stringBuilder.append(test);
                    }
                    //Buffer에 있던 내용을 StringBuilder에 담는 작업이 끝나면, 활용도를 높이기 위해 다시 toString()으로 적용.
                    complete = stringBuilder.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //서브 스레드에서 일 하고 나온 결과를 처리하기 위해 onPostExecute에 결과값이 담겨있는 complete 변수를 보냄.
                return complete;
            }

            /*
            * onPostExecute : onPostExecute의 리턴값을 매개변수로 받아 처리한다.
            */
            @Override
            protected void onPostExecute(String complete) {
                super.onPostExecute(complete);
                Log.e("Result", complete);
                txtResult.setText(complete);
            }
        }.execute(url);
    }
```     

