package ddwc.mobile.finalProject.movie;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class KobisMovieParser {

    //    xml에서 읽어들일 태그를 구분한 enum  → 정수값 등으로 구분하지 않고 가독성 높은 방식을 사용
    private enum TagType { NONE, MOVIE_CD, MOVIE_NM, MOVIE_NM_EN, OPEN_DT, NATIONS, DIRECTORS };
    //    parsing 대상인 tag를 상수로 선언
    private final static String FAULT_RESULT = "faultResult";
    private final static String START_ITEM_TAG = "movieInfoResult";
    private final static String START_NATION_TAG = "nations";
    private final static String NATION_TAG = "nationNm";
    private final static String START_DIRECTOR_TAG = "directors";
    private final static String DIRECTOR_TAG = "peopleNm";
    private final static String MOVIE_NAME_EN_TAG = "movieNmEn";
    private final static String MOVIE_NAME_TAG = "movieNm";
    private final static String OPEN_DATE_TAG = "openDt";
    private final static String MOVIE_CODE_TAG = "movieCd";

    private XmlPullParser parser;

    public KobisMovieParser() {
//        xml 파서 관련 변수들은 필요에 따라 멤버변수로 선언 후 생성자에서 초기화
//        파서 준비
        XmlPullParserFactory factory = null;

//        파서 생성
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }


    public KobisMovieDto parse(String xml) {
//        Log.d("parserLog", xml);
        KobisMovieDto dto = null;
        ArrayList<String> nations = null;
        ArrayList<String> directors = null;
        TagType tagType = TagType.NONE;    //  태그를 구분하기 위한 enum 변수 초기화

        try {
            // 파싱 대상 지정

            parser.setInput(new StringReader(xml));
            // 태그 유형 구분 변수 준비
            int eventType = parser.getEventType();
            // parsing 수행 - for 문 또는 while 문으로 구성
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        Log.d("parserLog", parser.getName());
                        if (tag.equals(START_ITEM_TAG)) {
                            dto = new KobisMovieDto();
                        } else if (tag.equals(MOVIE_CODE_TAG)) {
                            if(dto != null) tagType = TagType.MOVIE_CD;
                        } else if (tag.equals(MOVIE_NAME_TAG)) {
                            if(dto != null) tagType = TagType.MOVIE_NM;
                        } else if (tag.equals(MOVIE_NAME_EN_TAG)) {
                            if(dto != null) tagType = TagType.MOVIE_NM_EN;
                        } else if (tag.equals(OPEN_DATE_TAG)) {
                            if(dto != null) tagType = TagType.OPEN_DT;
                        } else if (tag.equals(FAULT_RESULT)) {
                            return null;//네트워크 상에는 문제 없었지만 입력값 등의 오류로 xml상에서 값의 오류가 생길때
                        } else if(tag.equals(START_NATION_TAG)) {
                            nations = new ArrayList<>();
                        } else if(tag.equals(START_DIRECTOR_TAG)) {
                            directors = new ArrayList<>();
                        } else if(tag.equals(NATION_TAG)) {
                            if(nations != null) tagType = TagType.NATIONS;
                        } else if(tag.equals(DIRECTOR_TAG)) {
                            if(directors != null) tagType = TagType.DIRECTORS;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(START_NATION_TAG)) {
                            dto.setNations(nations);
                            nations = null;
                        } else if (parser.getName().equals(START_DIRECTOR_TAG)) {
                            dto.setDirectors(directors);
                            directors = null;
                        }

                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case MOVIE_CD:
                                dto.setMovieCd(parser.getText());
                                break;
                            case MOVIE_NM:
                                dto.setMovieNm(parser.getText());
                                break;
                            case OPEN_DT:
                                dto.setOpenDt(parser.getText());
                                break;
                            case MOVIE_NM_EN:
                                dto.setMovieNmEn(parser.getText());
                                break;
                            case NATIONS:
                                nations.add(parser.getText());
                                break;
                            case DIRECTORS:
                                directors.add(parser.getText());
                                break;
                        }
                        //해당 태그에 저장되어있던 text를 활용했으니 다시 태그타입 초기화
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }
}
