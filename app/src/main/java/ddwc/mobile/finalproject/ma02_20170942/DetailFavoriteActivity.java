package ddwc.mobile.finalproject.ma02_20170942;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DetailFavoriteActivity extends AppCompatActivity {
    ImageView poster;
    TextView title;
    TextView subTitle;
    TextView pubDate;
    TextView director;
    TextView actor;
    RatingBar ratingBar;
    Button hyperBtn;
    Button saveBtn;
    Button closeBtn;
    Button shareBtn;
    Button searchBtn;

    NaverMovieDto movie;

    private NaverNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    AlertDialog.Builder builder;
    AlertDialog add_dialog;
    View custom_add_dialog;

    MovieDBManager movieDBManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_favorite);

        imageFileManager = new ImageFileManager(this);
        networkManager = new NaverNetworkManager(this);
        movieDBManager = new MovieDBManager(this);

        movie = (NaverMovieDto)getIntent().getSerializableExtra("movie");

        poster = findViewById(R.id.favorite_poster);
        title = findViewById(R.id.favorite_title);
        subTitle = findViewById(R.id.favorite_subtitle);
        pubDate = findViewById(R.id.favorite_pub);
        director = findViewById(R.id.favorite_director);
        actor = findViewById(R.id.favorite_actor);
        ratingBar = findViewById(R.id.favorite_rating);
        hyperBtn = findViewById(R.id.favorite_hyper);
        saveBtn = findViewById(R.id.favorite_savebtn);
        closeBtn = findViewById(R.id.favorite_closebtn);
        shareBtn = findViewById(R.id.favorite_sharebtn);
        searchBtn = findViewById(R.id.favorite_searchBtn);

        if(movie.getImageLink() == null) {
            poster.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(movie.getImageLink());
            if (savedBitmap != null) {
                poster.setImageBitmap(savedBitmap);
            } else { //null이면 네트워크를 통해 이미지를 다운로드 하고 파일로 저장!
                //초기화 (네트워크가 느리면 일단 이미지를 기본 이미지로 설정)
                poster.setImageResource(R.mipmap.ic_launcher);
                poster.setImageBitmap(savedBitmap);
                //현재 이 뷰홀더에서 가져온 네트워크 정보를 넣어줘야하니까 뷰홀더를 생성자에 전달받게한다!
                //안그러면 지금 다운로드한 이미지가 어느 뷰홀더의 이미지뷰인지 뒤죽박죽 헷갈려서 엉뚱한 이미지가 엉뚱한 책의 표지로 들어갈 수 있곘지
                new DetailFavoriteActivity.GetImageAsyncTask().execute(movie.getImageLink());
            }
        }

        title.setText(movie.getTitle());
        subTitle.setText(movie.getSubTitle());
        pubDate.setText(String.valueOf(movie.getPubDate()));
        director.setText(movie.getDirector());
        actor.setText(movie.getActor());
        ratingBar.setRating(movie.getUserRating());

    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.favorite_savebtn:
                builder = new AlertDialog.Builder(DetailFavoriteActivity.this);
                custom_add_dialog = View.inflate(DetailFavoriteActivity.this, R.layout.dialog_review_movie, null);

                EditText et_rating = custom_add_dialog.findViewById(R.id.add_rating);
                EditText et_review = custom_add_dialog.findViewById(R.id.add_review);
                Button btn_save = custom_add_dialog.findViewById(R.id.add_savebtn);
                Button btn_close = custom_add_dialog.findViewById(R.id.add_closebtn);

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_rating.getText().toString().equals("")) {
                            Toast.makeText(DetailFavoriteActivity.this, "평점을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                        else if (et_review.getText().toString().equals("")) {
                            Toast.makeText(DetailFavoriteActivity.this, "감상평을 입력하세요.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            movie.setPersonalRating(Float.valueOf(et_rating.getText().toString()));
                            movie.setReview(et_review.getText().toString());
                            // WATCHED에 추가
                            boolean result = movieDBManager.insertMovieToWatched(movie);
                            if (result) {
                                Toast.makeText(DetailFavoriteActivity.this,"영화를 추가 하였습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                            else {
                                Toast.makeText(DetailFavoriteActivity.this, "영화를 추가하지 못했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_dialog.dismiss();
                    }
                });

                builder.setView(custom_add_dialog);
                add_dialog = builder.create();
                add_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                add_dialog.show();

                break;
            case R.id.favorite_closebtn:
                finish();
                break;
            case R.id.favorite_sharebtn:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                String shareText = "영화 " + movie.getTitle() + " 같이 보실래요? \n\n" + "영화 상세정보 바로가기 : " +
                        movie.getHyperLink();
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

                Intent Sharing = Intent.createChooser(shareIntent, "공유하기");
                startActivity(Sharing);
                break;
            case R.id.favorite_hyper:
                Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse(movie.getHyperLink()));
                startActivity(intent);
                break;
            case R.id.favorite_searchBtn:
                Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                searchIntent.putExtra(SearchManager.QUERY, "영화 " + movie.getTitle());

                if (searchIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(searchIntent);
                } else {
                    String msg = "웹 브라우저를 실행할 수 없습니다.";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
        String imageAddress;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageAddress = params[0];
            Bitmap result = null;
            //1번 동작 수행
            result = networkManager.downloadImage(imageAddress);

            return result;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            /*네트워크에서 다운 받은 이미지 파일을 (null이 아닐경우) ImageFileManager 를 사용하여 내부저장소에 저장
             * 다운받은 bitmap 을 이미지뷰에 지정*/
            if (bitmap != null) {
                poster.setImageBitmap(bitmap);
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }
    }

}
