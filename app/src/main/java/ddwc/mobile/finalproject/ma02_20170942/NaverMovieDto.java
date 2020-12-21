package ddwc.mobile.finalproject.ma02_20170942;

import android.text.Html;
import android.text.Spanned;

import java.io.Serializable;

public class NaverMovieDto implements Serializable {
    private long _id;
    private String hyperLink;
    private String imageLink;
    private String imageFileName;
    private String title;
    private String subTitle;
    private String pubDate; // 제작년도
    private String director;
    private String actor;
    private float userRating;

    private String review;
    private float personalRating;

    public NaverMovieDto() {
        hyperLink = null;
        imageLink = null;
        imageFileName = null;
        title = null;
        subTitle = null;
        pubDate = null;
        director = null;
        actor = null;
        userRating = 0;

        review = null;
        personalRating = 0;
    }

    public NaverMovieDto(long _id, String hyperLink, String imageLink, String imageFileName, String title, String subTitle, String pubDate, String director, String actor, float userRating, String review, float personalRating) {
        this._id = _id;
        this.hyperLink = hyperLink;
        this.imageLink = imageLink;
        this.imageFileName = imageFileName;
        this.title = title;
        this.subTitle = subTitle;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.userRating = userRating;
        this.review = review;
        this.personalRating = personalRating;
    }

    public NaverMovieDto(long _id, String hyperLink, String imageLink, String imageFileName, String title, String subTitle, String pubDate, String director, String actor, float userRating) {
        this._id = _id;
        this.hyperLink = hyperLink;
        this.imageLink = imageLink;
        this.imageFileName = imageFileName;
        this.title = title;
        this.subTitle = subTitle;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.userRating = userRating;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getHyperLink() {
        return hyperLink;
    }

    public void setHyperLink(String hyperLink) {
        this.hyperLink = hyperLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getTitle() {
        //html 태그를 없애고 순수한 String으로 변환!
        Spanned spanned = Html.fromHtml(title);
        return spanned.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public float getUserRating() {
        return userRating;
    }

    public void setUserRating(float userRating) {
//        this.userRating = Math.round((userRating * 100) / 100.0);
        this.userRating = userRating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getPersonalRating() {
        return personalRating;
    }

    public void setPersonalRating(float personalRating) {
//        this.personalRating = Math.round((personalRating * 100) / 100.0);
        this.personalRating = personalRating;
    }
}
