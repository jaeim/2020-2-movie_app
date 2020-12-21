package ddwc.mobile.finalproject.ma02_20170942;

import java.io.Serializable;

public class DailyBoxOfficeDto implements Serializable {
    private long _id;
    private String rank;
    private String movieNm;
    private String openDt;
    private String movieCD;
    // 누적 매출액
    private String salesAcc;
    // 누적 관객수
    private String audiAcc;

    public DailyBoxOfficeDto() {}

    public DailyBoxOfficeDto(long _id, String rank, String movieNm, String openDt, String movieCD, String salesAcc, String audiAcc) {
        this._id = _id;
        this.rank = rank;
        this.movieNm = movieNm;
        this.openDt = openDt;
        this.movieCD = movieCD;
        this.salesAcc = salesAcc;
        this.audiAcc = audiAcc;
    }

    public long get_id() { return _id; }
    public void set_id(long _id) { this._id = _id;  }
    public String getMovieNm() {
        return movieNm;
    }
    public void setMovieNm(String movieNm) {
        this.movieNm = movieNm;
    }
    public String getOpenDt() {
        return openDt;
    }
    public void setOpenDt(String openDt) {
        this.openDt = openDt;
    }
    public String getRank() {
        return rank;
    }
    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getMovieCD() { return movieCD; }
    public void setMovieCD(String movieCD) { this.movieCD = movieCD; }
    public String getAudiAcc() {
        return audiAcc;
    }
    public void setAudiAcc(String audiAcc) {
        this.audiAcc = audiAcc;
    }
    public String getSalesAcc() {
        return salesAcc;
    }
    public void setSalesAcc(String salesAcc) {
        this.salesAcc = salesAcc;
    }
}
