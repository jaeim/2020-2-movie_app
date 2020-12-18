package ddwc.mobile.finalProject.movie;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class WatchedMovieActivity extends AppCompatActivity {
    public static final String TAG = "WatchedMovieActivityTag";

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    MyMovieAdapter adapter;
    ArrayList<NaverMovieDto> resultList;
    ListView listView;

    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watched_movie);

        movieDBManager = new MovieDBManager(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.watched_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(R.drawable.ic_launcher_foreground); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.watchedLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.watched_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Navigation 메뉴의 이벤트
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.menu_box){
                    Toast.makeText(context, "박스오피스", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }
                else if(id == R.id.menu_movies){
                    Toast.makeText(context, "나의 감상 목록", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "검색", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, MainActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "즐겨찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, FavoriteMovieActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "주변 영화관 찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(WatchedMovieActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
//                    WatchedMovieActivity.this.finish();
                }

                return true;
            }
        });

        listView = findViewById(R.id.watached_listview);
        resultList = new ArrayList<NaverMovieDto>();
        adapter = new MyMovieAdapter(this, R.layout.listview_movie, resultList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(WatchedMovieActivity.this, DetailWatchedMovieActivity.class);
                intent.putExtra("movie", resultList.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(WatchedMovieActivity.this);
                builder.setTitle("목록에서 삭제")
                        .setMessage("영화 " + resultList.get(pos).getTitle() + "를(을) 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(movieDBManager.removeMovieFromWatched(resultList.get(pos).get_id())) {
                                    Toast.makeText(WatchedMovieActivity.this, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    resultList.clear();
                                    resultList.addAll(movieDBManager.getAllMovieFromWatched());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(WatchedMovieActivity.this, "삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        resultList.clear();
        resultList.addAll(movieDBManager.getAllMovieFromWatched());
        adapter.notifyDataSetChanged();
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
