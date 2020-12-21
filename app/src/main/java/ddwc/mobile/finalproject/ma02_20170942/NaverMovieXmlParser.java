package ddwc.mobile.finalproject.ma02_20170942;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class NaverMovieXmlParser {
    public enum TagType {NONE, TITLE, HYPER, IMAGE, SUBTITLE, PUB, DIRECTOR, ACTOR, RATING};

    final static String TAG_ITEM = "item";
    final static String TAG_TITLE = "title";
    final static String TAG_HYPER = "link";
    final static String TAG_SUBTITLE = "subtitle";
    final static String TAG_PUB = "pubDate";
    final static String TAG_DIRECTOR = "director";
    final static String TAG_ACTOR = "actor";
    final static String TAG_RATING = "userRating";
    final static String TAG_IMAGE = "image";

    public NaverMovieXmlParser() {
    }

    public ArrayList<NaverMovieDto> parse(String xml) {
        ArrayList<NaverMovieDto> resultList = new ArrayList();
        NaverMovieDto dto = null;

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            dto = new NaverMovieDto();
                        } else if (parser.getName().equals(TAG_TITLE)) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals(TAG_HYPER)) {
                            if (dto != null) tagType = TagType.HYPER;
                        } else if (parser.getName().equals(TAG_SUBTITLE)) {
                            if (dto != null) tagType = TagType.SUBTITLE;
                        } else if (parser.getName().equals(TAG_PUB)) {
                            if (dto != null) tagType = TagType.PUB;
                        } else if (parser.getName().equals(TAG_DIRECTOR)) {
                            if (dto != null) tagType = TagType.DIRECTOR;
                        } else if (parser.getName().equals(TAG_ACTOR)) {
                            if (dto != null) tagType = TagType.ACTOR;
                        } else if (parser.getName().equals(TAG_RATING)) {
                            if (dto != null) tagType = TagType.RATING;
                        } else if (parser.getName().equals(TAG_IMAGE)) {
                            if (dto != null) tagType = TagType.IMAGE;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ITEM)) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case HYPER:
                                dto.setHyperLink(parser.getText());
                                break;
                            case SUBTITLE:
                                dto.setSubTitle(parser.getText());
                                break;
                            case PUB:
                                dto.setPubDate((parser.getText()));
                                break;
                            case DIRECTOR:
                                dto.setDirector(parser.getText());
                                break;
                            case ACTOR:
                                dto.setActor(parser.getText());
                                break;
                            case RATING:
                                // 10점 만점을 5점 만점 기준으로 변환
                                float rating = (float) (Float.parseFloat(parser.getText()) / 2.0);
                                String format = String.format("%.2f", rating);
//                                dto.setUserRating((Float.parseFloat(parser.getText())) / 2);
                                dto.setUserRating(Float.parseFloat(format));
                                break;
                            case IMAGE:
                                dto.setImageLink(parser.getText());
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}
