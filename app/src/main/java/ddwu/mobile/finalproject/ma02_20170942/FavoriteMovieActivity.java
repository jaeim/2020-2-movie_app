package ddwu.mobile.finalproject.ma02_20170942;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class FavoriteMovieActivity extends AppCompatActivity {
    public static final String TAG = "FavoriteMovieActivityTag";

    private DrawerLayout mDrawerLayout;
    private Context context = this;

    MyMovieAdapter adapter;
    RecyclerAdapterForFavorite recyclerAdapter;
    RecyclerView recyclerView;
    ArrayList<NaverMovieDto> resultList;
    ListView listView;

    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_movie);

        movieDBManager = new MovieDBManager(this);
        Drawable drawble = getResources().getDrawable(R.drawable.menu);
        Bitmap bitmap = ((BitmapDrawable)drawble).getBitmap();
        Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 40, 50, true));

        Toolbar toolbar = (Toolbar)findViewById(R.id.favorite_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기
        actionBar.setHomeAsUpIndicator(newdrawable); //뒤로가기 버튼 이미지 지정

        mDrawerLayout = (DrawerLayout) findViewById(R.id.favoriteLayout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.favorite_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Navigation 메뉴의 이벤트
                item.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.menu_box){
                    Toast.makeText(context, "박스오피스", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FavoriteMovieActivity.this, BoxOfficeActivity.class);
                    startActivity(intent);
                    FavoriteMovieActivity.this.finish();
                }
                else if(id == R.id.menu_movies){
                    Toast.makeText(context, "나의 감상 목록", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FavoriteMovieActivity.this, WatchedMovieActivity.class);
                    startActivity(intent);
                    FavoriteMovieActivity.this.finish();
                }
                else if(id == R.id.menu_search){
                    Toast.makeText(context, "검색", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FavoriteMovieActivity.this, MainActivity.class);
                    startActivity(intent);
                    FavoriteMovieActivity.this.finish();
                }
                else if(id == R.id.menu_favorite){
                    Toast.makeText(context, "즐겨찾기", Toast.LENGTH_SHORT).show();
                    FavoriteMovieActivity.this.finish();
                }
                else if(id == R.id.menu_theater) {
                    Toast.makeText(context, "주변 영화관 찾기", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FavoriteMovieActivity.this, NearByTheaterActivity.class);
                    startActivity(intent);
                    FavoriteMovieActivity.this.finish();
                }

                return true;
            }
        });

//        listView = findViewById(R.id.favorite_listview);
        resultList = new ArrayList<NaverMovieDto>();
//        adapter = new MyMovieAdapter(this, R.layout.listview_movie, resultList);
//        listView.setAdapter(adapter);
/*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteMovieActivity.this, DetailFavoriteActivity.class);
                intent.putExtra("movie", resultList.get(position));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteMovieActivity.this);
                builder.setTitle("목록에서 삭제")
                        .setMessage("영화 " + resultList.get(pos).getTitle() + "를(을) 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(movieDBManager.removeMovieFromFavorite(resultList.get(pos).get_id())) {
                                    Toast.makeText(FavoriteMovieActivity.this, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    resultList.clear();
                                    resultList.addAll(movieDBManager.getAllMovieFromFavorite());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(FavoriteMovieActivity.this, "삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });
 */
        recyclerView = findViewById(R.id.favorite_recyclerview);
        recyclerAdapter = new RecyclerAdapterForFavorite(this, resultList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

    }

    @Override
    protected void onResume() {
        super.onResume();
        resultList.clear();
        resultList.addAll(movieDBManager.getAllMovieFromFavorite());
//        adapter.notifyDataSetChanged();
        recyclerAdapter.notifyDataSetChanged();
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
