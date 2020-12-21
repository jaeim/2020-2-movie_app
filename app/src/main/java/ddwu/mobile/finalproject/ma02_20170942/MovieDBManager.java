package ddwu.mobile.finalproject.ma02_20170942;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MovieDBManager {
    MovieDBHelper movieDBHelper = null;
    Cursor cursor = null;

    public MovieDBManager(Context context) {    movieDBHelper = new MovieDBHelper(context);    }

    // WATCHED에 저장된 모든 영화 반환
    public ArrayList<NaverMovieDto> getAllMovieFromWatched() {
        ArrayList<NaverMovieDto> movieList = new ArrayList<NaverMovieDto>();
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + MovieDBHelper.WATCHED_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_ID));
            String hyperLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_HYPER));
            String imgLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_IMGLINK));
            String imgName = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_IMGNAME));
            String title = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_TITLE));
            String subTitle = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_SUBTITLE));
            String pubDate = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_PUB));
            String director = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_DIRECTOR));
            String actors = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_ACTORS));
            float userRating = cursor.getFloat(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_USERRATING));
            String review = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_REVIEW));
            float personalRating = cursor.getFloat(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_PERSRATING));

            movieList.add(new NaverMovieDto(id, hyperLink, imgLink, imgName, title, subTitle, pubDate, director, actors, userRating, review, personalRating));
        }
        cursor.close();
        movieDBHelper.close();
        return  movieList;
    }

    //    // WATCHED에 저장된 영화 문자열 검색
    public ArrayList<NaverMovieDto> searchMovieByTitle(String key) {
        ArrayList<NaverMovieDto> movieList = new ArrayList<NaverMovieDto>();
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + MovieDBHelper.WATCHED_TABLE_NAME + " where " + MovieDBHelper.WATCHED_COL_TITLE + " LIKE '%" + key + "%'" + " or " + MovieDBHelper.WATCHED_COL_SUBTITLE + " LIKE '%" + key + "%'", null);
        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_ID));
            String hyperLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_HYPER));
            String imgLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_IMGLINK));
            String imgName = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_IMGNAME));
            String title = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_TITLE));
            String subTitle = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_SUBTITLE));
            String pubDate = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_PUB));
            String director = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_DIRECTOR));
            String actors = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_ACTORS));
            float userRating = cursor.getFloat(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_USERRATING));
            String review = cursor.getString(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_REVIEW));
            float personalRating = cursor.getFloat(cursor.getColumnIndex(MovieDBHelper.WATCHED_COL_PERSRATING));

            movieList.add(new NaverMovieDto(id, hyperLink, imgLink, imgName, title, subTitle, pubDate, director, actors, userRating, review, personalRating));
        }
        cursor.close();
        movieDBHelper.close();
        return  movieList;
    }

    // WATCHED에 영화 추가
    public boolean insertMovieToWatched(NaverMovieDto newMovie) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(MovieDBHelper.WATCHED_COL_HYPER, newMovie.getHyperLink());
        row.put(MovieDBHelper.WATCHED_COL_IMGLINK, newMovie.getImageLink());
        row.put(MovieDBHelper.WATCHED_COL_IMGNAME, newMovie.getImageFileName());
        row.put(MovieDBHelper.WATCHED_COL_TITLE, newMovie.getTitle());
        row.put(MovieDBHelper.WATCHED_COL_SUBTITLE, newMovie.getSubTitle());
        row.put(MovieDBHelper.WATCHED_COL_PUB, newMovie.getPubDate());
        row.put(MovieDBHelper.WATCHED_COL_DIRECTOR, newMovie.getDirector());
        row.put(MovieDBHelper.WATCHED_COL_ACTORS, newMovie.getActor());
        row.put(MovieDBHelper.WATCHED_COL_USERRATING, newMovie.getUserRating());
        row.put(MovieDBHelper.WATCHED_COL_REVIEW, newMovie.getReview());
        row.put(MovieDBHelper.WATCHED_COL_PERSRATING, newMovie.getPersonalRating());

        long count = db.insert(MovieDBHelper.WATCHED_TABLE_NAME, null, row);
        movieDBHelper.close();

        if (count > 0) return true;
        return false;
    }


    // WATCHED에 저장된 영화의 평점과 감상평 수정(ID 기준)
    public boolean modifyWatchedMovie(NaverMovieDto movie) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(MovieDBHelper.WATCHED_COL_REVIEW, movie.getReview());
        row.put(MovieDBHelper.WATCHED_COL_PERSRATING, movie.getPersonalRating());

        String whereClause = MovieDBHelper.WATCHED_COL_ID + "=?";
        String[] whereArgs = new String[] {    String.valueOf(movie.get_id())   };

        long count = db.update(MovieDBHelper.WATCHED_TABLE_NAME, row, whereClause, whereArgs);
        movieDBHelper.close();

        if(count > 0) return true;
        return false;
    }

    // WATCHED에 저장된 영화 삭제
    public boolean removeMovieFromWatched(long id) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        String whereClause = MovieDBHelper.WATCHED_COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        long count = db.delete(MovieDBHelper.WATCHED_TABLE_NAME, whereClause, whereArgs);
        movieDBHelper.close();

        if (count > 0) return true;
        return false;
    }



    // FAVORITE에 저장된 모든 영화 반환
    public ArrayList<NaverMovieDto> getAllMovieFromFavorite() {
        ArrayList<NaverMovieDto> movieList = new ArrayList<NaverMovieDto>();
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + MovieDBHelper.FAVORITE_TABLE_NAME, null);

        while (cursor.moveToNext()) {
            long id = cursor.getInt(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_ID));
            String hyperLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_HYPER));
            String imgLink = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_IMGLINK));
            String imgName = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_IMGNAME));
            String title = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_TITLE));
            String subTitle = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_SUBTITLE));
            String pubDate = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_PUB));
            String director = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_DIRECTOR));
            String actors = cursor.getString(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_ACTORS));
            float userRating = cursor.getFloat(cursor.getColumnIndex(MovieDBHelper.FAVORITE_COL_USERRATING));

            movieList.add(new NaverMovieDto(id, hyperLink, imgLink, imgName, title, subTitle, pubDate, director, actors, userRating));
        }
        cursor.close();
        movieDBHelper.close();
        return  movieList;
    }

    // FAVORITE에 영화 추가
    public boolean insertMovieToFavorite(NaverMovieDto newMovie) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        ContentValues row = new ContentValues();

        row.put(MovieDBHelper.FAVORITE_COL_HYPER, newMovie.getHyperLink());
        row.put(MovieDBHelper.FAVORITE_COL_IMGLINK, newMovie.getImageLink());
        row.put(MovieDBHelper.FAVORITE_COL_IMGNAME, newMovie.getImageFileName());
        row.put(MovieDBHelper.FAVORITE_COL_TITLE, newMovie.getTitle());
        row.put(MovieDBHelper.FAVORITE_COL_SUBTITLE, newMovie.getSubTitle());
        row.put(MovieDBHelper.FAVORITE_COL_PUB, newMovie.getPubDate());
        row.put(MovieDBHelper.FAVORITE_COL_DIRECTOR, newMovie.getDirector());
        row.put(MovieDBHelper.FAVORITE_COL_ACTORS, newMovie.getActor());
        row.put(MovieDBHelper.FAVORITE_COL_USERRATING, newMovie.getUserRating());

        long count = db.insert(MovieDBHelper.FAVORITE_TABLE_NAME, null, row);
        movieDBHelper.close();

        if (count > 0) return true;
        return false;
    }

    // FAVORITE에 저장된 영화 삭제
    public boolean removeMovieFromFavorite(long id) {
        SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        String whereClause = MovieDBHelper.FAVORITE_COL_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(id) };

        long count = db.delete(MovieDBHelper.FAVORITE_TABLE_NAME, whereClause, whereArgs);
        movieDBHelper.close();

        if (count > 0) return true;
        return false;
    }

    //    close 수행
    public void close() {
        if (movieDBHelper != null) movieDBHelper.close();
        if (cursor != null) cursor.close();
    };
}
