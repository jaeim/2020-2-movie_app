package ddwc.mobile.finalproject.ma02_20170942;

import java.io.Serializable;
import java.util.ArrayList;

public class KobisMovieDto implements Serializable {
    private long _id;
    private String movieCd;
    private String movieNm;
    private String movieNmEn;
    private String openDt;
    private ArrayList<String> nations;
    private ArrayList<String> directors;

    public KobisMovieDto() {
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getMovieCd() {
        return movieCd;
    }

    public void setMovieCd(String movieCd) {
        this.movieCd = movieCd;
    }

    public String getMovieNm() {
        return movieNm;
    }

    public void setMovieNm(String movieNm) {
        this.movieNm = movieNm;
    }

    public String getMovieNmEn() {
        return movieNmEn;
    }

    public void setMovieNmEn(String movieNmEn) {
        this.movieNmEn = movieNmEn;
    }

    public String getOpenDt() {
        return openDt;
    }

    public void setOpenDt(String openDt) {
        this.openDt = openDt;
    }

    public String getNations() {
        StringBuilder result = new StringBuilder();
        for(String nation : nations) {
            result.append(nation + " ");
        }
        return result.toString();
    }

    public void setNations(ArrayList<String> nations) {
        this.nations = nations;
    }

    public String getDirectors() {
        StringBuilder result = new StringBuilder();
        for(String director : directors) {
            result.append(director + " ");
        }
        return result.toString();
    }

    public void setDirectors(ArrayList<String> directors) {
        this.directors = directors;
    }
}
