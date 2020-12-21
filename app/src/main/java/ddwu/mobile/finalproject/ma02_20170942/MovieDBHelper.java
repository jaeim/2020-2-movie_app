package ddwu.mobile.finalproject.ma02_20170942;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MovieDBHelper extends SQLiteOpenHelper {
    final static String TAG = "MovieDBHelper";

    final static String DB_NAME = "movies.db";

    public final static String WATCHED_TABLE_NAME = "watched_movie_table";
    public final static String FAVORITE_TABLE_NAME = "favorite_movie_table";

    // watched table columns
    public final static String WATCHED_COL_ID = "_id";
    public final static String WATCHED_COL_HYPER = "hyperLink";
    public final static String WATCHED_COL_IMGLINK = "imageLink";
    public final static String WATCHED_COL_IMGNAME = "imageName";
    public final static String WATCHED_COL_TITLE = "movieTitle";
    public final static String WATCHED_COL_SUBTITLE = "subTitle";
    public final static String WATCHED_COL_PUB = "publishDate";
    public final static String WATCHED_COL_DIRECTOR = "director";
    public final static String WATCHED_COL_ACTORS = "actors";
    public final static String WATCHED_COL_USERRATING = "userRating";
    public final static String WATCHED_COL_REVIEW = "review";
    public final static String WATCHED_COL_PERSRATING = "personalRating";

    // favorite table columns
    public final static String FAVORITE_COL_ID = "_id";
    public final static String FAVORITE_COL_HYPER = "hyperLink";
    public final static String FAVORITE_COL_IMGLINK = "imageLink";
    public final static String FAVORITE_COL_IMGNAME = "imageName";
    public final static String FAVORITE_COL_TITLE = "movieTitle";
    public final static String FAVORITE_COL_SUBTITLE = "subTitle";
    public final static String FAVORITE_COL_PUB = "publishDate";
    public final static String FAVORITE_COL_DIRECTOR = "director";
    public final static String FAVORITE_COL_ACTORS = "actors";
    public final static String FAVORITE_COL_USERRATING = "userRating";

    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE " + WATCHED_TABLE_NAME + " (" + WATCHED_COL_ID + " integer primary key autoincrement, " +
                WATCHED_COL_HYPER + " TEXT, " + WATCHED_COL_IMGLINK + " TEXT, " + WATCHED_COL_IMGNAME + " TEXT, " + WATCHED_COL_TITLE +
                " TEXT, " + WATCHED_COL_SUBTITLE + " TEXT, " + WATCHED_COL_PUB + " TEXT, " + WATCHED_COL_DIRECTOR + " TEXT, " + WATCHED_COL_ACTORS +
                " TEXT, " + WATCHED_COL_USERRATING + " REAL, " + WATCHED_COL_REVIEW + " TEXT, " + WATCHED_COL_PERSRATING + " REAL)";

        String sql2 = "CREATE TABLE " + FAVORITE_TABLE_NAME + " (" + FAVORITE_COL_ID + " integer primary key autoincrement, " +
                FAVORITE_COL_HYPER + " TEXT, " + FAVORITE_COL_IMGLINK + " TEXT, " + FAVORITE_COL_IMGNAME + " TEXT, " + FAVORITE_COL_TITLE +
                " TEXT, " + FAVORITE_COL_SUBTITLE + " TEXT, " + FAVORITE_COL_PUB + " TEXT, " + FAVORITE_COL_DIRECTOR + " TEXT, " + FAVORITE_COL_ACTORS +
                " TEXT, " + FAVORITE_COL_USERRATING + " REAL)";


        Log.d(TAG, sql1);
        Log.d(TAG, sql2);
        db.execSQL(sql1);
        db.execSQL(sql2);

        db.execSQL("insert into " + WATCHED_TABLE_NAME + " values (null, 'https://movie.naver.com/movie/bi/mi/basic.nhn?code=92075'" +
                ", 'https://ssl.pstatic.net/imgmovie/mdi/mit110/0920/92075_P31_154949.jpg', '92075_P31_154949.jpg', '어바웃 타임', 'About Time'" +
                ", '2013', '리차드 커티스|', '도널 글리슨|레이첼 맥아담스|', '4.64'" +
                ", '단순히 로맨틱 코미디를 넘어 무언가 가슴속에서 올라오는 찡함이 있는 영화.', '5.0')");

        db.execSQL("insert into " + FAVORITE_TABLE_NAME + " values (null, 'https://movie.naver.com/movie/bi/mi/basic.nhn?code=92075'" +
                ", 'https://ssl.pstatic.net/imgmovie/mdi/mit110/0920/92075_P31_154949.jpg', '92075_P31_154949.jpg', '어바웃 타임', 'About Time'" +
                ", '2013', '리차드 커티스|', '도널 글리슨|레이첼 맥아담스|', '4.64')");

//        db.execSQL("insert into " + FAVORITE_TABLE_NAME + " values (null, 'http://openapi.naver.com/l?AAADWLQQvCIBzFP83f48h0zh08uK1B0S2IOm7mUEIts0F9+vQQPN77vQfv+dbxI2DXgyTQ9QV4B+2ATNSLMCk9gEjYjlkurFZXflp1rFRw/yXnbEspNk8vqypvPJBRhZsGMrSMY4ySwLSpN5RT3NSYIScO5nxhdzc18cjq0958w8LneKUy8fz6AdRxjD6YAAAA'" +
//                ", 'http://imgmovie.naver.com/mdi/mit110/0968/96811_P01_142155.jpg', '96811_P01_142155.jpg', '주마등 주식 회사', '走馬&amp;amp;#28783;株式&amp;amp;#20250;社'" +
//                ", '2012', '미키 코이치로|', '카시이 유우|쿠보타 마사타카|카지와라 히카리|치요 쇼타|요코야마 메구미|카시와바라 슈지|', '4.50')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WATCHED_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE_NAME);
        onCreate(db);
    }
}
