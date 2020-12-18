package ddwc.mobile.finalProject.movie;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivityTag";
    final static int DETAIL_ACTIVITY_CODE = 100;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground); //뒤로가기 버튼 이미지 지정

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
                try {
                    new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
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
            // 어댑터에 넘기기전 미리 이미지를 모두 다운받는 방식
            // 왜 이 방법을 해도 안될까??
            // 디버깅하면 stream = getNetworkConnection(conn); 이 라인을 수행안하는데..??
            // 예외도 발생한다.. NetworkOnMainThreadException
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