package ddwc.mobile.finalProject.movie;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BoxOfficeActivity extends AppCompatActivity {
    public static final String TAG = "BoxOfficeActivityTag";
    private DrawerLayout mDrawerLayout;
    private Context context = this;

    BoxOfficeAdapter adapter;
    ArrayList<DailyBoxOfficeDto> resultList;
    ListView listView;

    String query;
    String apiAddress;

    KobisXmlParser parser;
    NaverNetworkManager networkManager;
    ImageFileManager imageFileManager;

    KobisMovieDto detailMovie;
    KobisMovieParser movieParser;
    String movieApiAddress;

    AlertDialog.Builder builder;
    AlertDialog kobis_dialog;
    View custom_kobis_dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_office);

        Toolbar toolbar = (Toolbar)findViewById(R.id.boxOffice_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.boxOfficeLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.boxOffice_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Navigation 메뉴의 이벤트
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.menu_box){
                    Toast.makeText(context, "박스오피스", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.menu_movies){
                    Toast.makeText(context, "나의 감상 목록", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BoxOfficeActivity.this, WatchedMovieActivity.class);
                    startActivity(intent);
                    BoxOfficeActivity.this.finish();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "검색", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BoxOfficeActivity.this, MainActivity.class);
                    startActivity(intent);
                    BoxOfficeActivity.this.finish();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "즐겨찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BoxOfficeActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
                    BoxOfficeActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "주변 영화관 찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BoxOfficeActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
                    BoxOfficeActivity.this.finish();
                }

                return true;
            }
        });

        listView = findViewById(R.id.boxOffice_listview);

        resultList = new ArrayList<DailyBoxOfficeDto>();
        adapter = new BoxOfficeAdapter(this, R.layout.listview_box_office, resultList);
        listView.setAdapter(adapter);

        parser = new KobisXmlParser();
        movieParser = new KobisMovieParser();
        networkManager = new NaverNetworkManager(this);
        imageFileManager = new ImageFileManager(this);

        apiAddress = getResources().getString(R.string.box_office_url);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        Date time = new Date();
        Calendar time = Calendar.getInstance();
        time.add(Calendar.DATE, -1); 

        query = format.format(time.getTime());

        new BoxOfficeActivity.NetworkAsyncTask().execute(apiAddress + query);

        movieApiAddress = getResources().getString(R.string.kobis_movie_url);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieCD = resultList.get(position).getMovieCD();
                new NetworkDetailMovieAsyncTask().execute(movieApiAddress + movieCD);
            }
        });

    }
    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(BoxOfficeActivity.this, "Wait", "Downloading...");
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
            progressDlg.dismiss();
            resultList = (ArrayList<DailyBoxOfficeDto>) parser.parse(result);
            // notify까지 수행함
            adapter.setList(resultList);

        }
    }
    // 영화 진흥 위원회 영화 상세 정보 API
    class NetworkDetailMovieAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDlg = ProgressDialog.show(BoxOfficeActivity.this, "Wait", "Downloading...");
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
            // 영화 진흥 위원회 api를 통해 영화 정보를 가져오고 다이얼로그로 띄움
            detailMovie = (KobisMovieDto) movieParser.parse(result);

            builder = new AlertDialog.Builder(BoxOfficeActivity.this);
            custom_kobis_dialog = View.inflate(BoxOfficeActivity.this, R.layout.dialog_kobis_movie, null);

            TextView tv_title = custom_kobis_dialog.findViewById(R.id.dialog_kobis_title);
            TextView tv_enTitle = custom_kobis_dialog.findViewById(R.id.dialog_kobis_en);
            TextView tv_openDt = custom_kobis_dialog.findViewById(R.id.dialog_kobis_date);
            TextView tv_cn = custom_kobis_dialog.findViewById(R.id.dialog_kobis_cn);
            TextView tv_director = custom_kobis_dialog.findViewById(R.id.dialog_kobis_director);
            Button btn_close = custom_kobis_dialog.findViewById(R.id.dialog_kobis_closebtn);

            if (detailMovie.getMovieNm() == null || detailMovie.getMovieNm().equals("")) {
                tv_title.setText("영화제목 정보 없음");
            } else {
                tv_title.setText(detailMovie.getMovieNm());
            }
            if (detailMovie.getMovieNmEn() == null || detailMovie.getMovieNmEn().equals("")) {
                tv_enTitle.setText("영문제목 정보 없음");
            } else {
                tv_enTitle.setText("(" + detailMovie.getMovieNmEn() + ")");
            }
            if (detailMovie.getOpenDt() == null || detailMovie.getOpenDt().equals("")) {
                tv_openDt.setText("개봉일 정보 없음");
            } else {
                tv_openDt.setText("개봉일 : " + detailMovie.getOpenDt());
            }
            if (detailMovie.getNations() == null || detailMovie.getNations().equals("")) {
                tv_cn.setText("제작국가 정보 없음");
            } else {
                tv_cn.setText("제작국가 : " + detailMovie.getNations());
            }
            if (detailMovie.getDirectors() == null || detailMovie.getDirectors().equals("")) {
                tv_director.setText("감독 정보 없음");
            } else {
                tv_director.setText("감독 : " + detailMovie.getDirectors());
            }

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    kobis_dialog.dismiss();
                }
            });

            builder.setView(custom_kobis_dialog);
            kobis_dialog = builder.create();
            kobis_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            kobis_dialog.show();
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
