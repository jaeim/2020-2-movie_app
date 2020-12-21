package ddwu.mobile.finalproject.ma02_20170942;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//  20170942 컴퓨터학과 송재임
//  모바일응용 02분반 최종 프로젝트
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivityTag";
    final static int DETAIL_ACTIVITY_CODE = 100;
    private final static int MY_PERMISSIONS_REQ_LOC = 200;

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    String query;
    String apiAddress;
    EditText et_search;
    ImageView btn_search;

    MyMovieAdapter adapter;

    ArrayList<NaverMovieDto> resultList;
    ListView listView;
    NaverMovieXmlParser parser;
    NaverNetworkManager networkManager;
    ImageFileManager imageFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable drawble = getResources().getDrawable(R.drawable.menu);
        Bitmap bitmap = ((BitmapDrawable)drawble).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 50, true));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(newdrawable); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                // Navigation 메뉴의 이벤트
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                if(id == R.id.menu_box){
                    Toast.makeText(context, "박스오피스", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();

                }
                else if(id == R.id.menu_movies){
                    Toast.makeText(context, "나의 감상 목록", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, WatchedMovieActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "검색", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "즐겨찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "주변 영화관 찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
//                    MainActivity.this.finish();
                }

                return true;
            }
        });

        et_search = findViewById(R.id.et_search);
        btn_search = findViewById(R.id.btn_search);
        listView = findViewById(R.id.listview);

        resultList = new ArrayList();
//        adapter = new MyMovieAdapter(this, R.layout.listview_movie, movieList);
        adapter = new MyMovieAdapter(this, R.layout.listview_movie, resultList);
        listView.setAdapter(adapter);
//        apiAddress = "https://api.themoviedb.org/3/search/movie?api_key=7585d0a9fd60f897846775ad25e13099&language=ko-KR&page=1&query=";
        apiAddress = getResources().getString(R.string.api_url);
        parser = new NaverMovieXmlParser();
        networkManager = new NaverNetworkManager(this);
        networkManager.setClientId(getResources().getString(R.string.client_id));
        networkManager.setClientSecret(getResources().getString(R.string.client_secret));
        imageFileManager = new ImageFileManager(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NaverMovieDto movie = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailMovieActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        checkPermission();
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // FINE을 포함할 경우 COARSE는 기본적으로 허용됨
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없을 경우 권한 요청 메소드 실행(다이얼로그)
                // '나의 요청 코드'와 함께 사용자에게 승인 요청을 하는 다이얼로그를 띄우도록 함
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQ_LOC);
                return false;
            } else
                // 두개의 권한 모두 있을 때
                return true;
        }
        return false;
    }

    /*권한승인 요청에 대한 사용자의 응답 결과에 따른 수행*/
    // 액티비티 상속 메소드
    // 사용자가 승인 또는 거절할 경우 자동으로 호출 됨/요청시 사용했던 req.code 상수값을 전달 받음
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSIONS_REQ_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    /*권한을 승인받았을 때 수행하여야 하는 동작 지정*/
                } else {
                    /*사용자에게 권한 제약에 따른 안내*/
                    Toast.makeText(this, "Permissions are not granted.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 임시 파일 삭제
        imageFileManager.clearTemporaryFiles();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                query = et_search.getText().toString();
                // OpenAPI 주소와 query 조합 후 서버에서 데이터를 가져옴
                // 가져온 데이터는 파싱 수행 후 어댑터에 설정
                if (query.equals("")) {
                    Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;
            // networking
            result = networkManager.downloadContents(address);
            // 온라인이 아닐 경우
            if(result == null) return "Error!";
            Log.d(TAG, result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            resultList = parser.parse(result);
            for (NaverMovieDto dto : resultList) {
                Bitmap bitmap = networkManager.downloadImage(dto.getImageLink());
                if (bitmap != null) {
                    Log.d(TAG, "bitmap is not null");
                    imageFileManager.saveBitmapToTemporary(bitmap, dto.getImageLink());
                }
            }
            // notify까지 수행함
            adapter.setList(resultList);
            progressDlg.dismiss();
        }
    }

    // 툴바 버튼의 클릭 이벤트를 정의
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ // 왼쪽 상단 버튼 눌렀을 때
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}