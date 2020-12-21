package ddwc.mobile.finalproject.ma02_20170942;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class KobisXmlParser {

    //    xml에서 읽어들일 태그를 구분한 enum  → 정수값 등으로 구분하지 않고 가독성 높은 방식을 사용
    private enum TagType { NONE, RANK, MOVIE_NM, OPEN_DT, MOVIE_CD, SALES_ACC, AUDI_ACC };     // 해당없음, rank, movieNm, openDt, movieCd, salesAcc, audiAcc

    //    parsing 대상인 tag를 상수로 선언
    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM_TAG = "dailyBoxOffice";
    private final static String RANK_TAG = "rank";
    private final static String MOVIE_NAME_TAG = "movieNm";
    private final static String OPEN_DATE_TAG = "openDt";
    private final static String MOVIE_CODE = "movieCd";
    private final static String SALES_TAG = "salesAcc";
    private final static String AUDI_TAG = "audiAcc";

    private XmlPullParser parser;

    public KobisXmlParser() {
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


    public ArrayList<DailyBoxOfficeDto> parse(String xml) {
//        Log.d("parserLog", xml);
        ArrayList<DailyBoxOfficeDto> resultList = new ArrayList();
        DailyBoxOfficeDto dbo = null;
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
                        //번외) 네이버 오픈 api의 경우 ITEM_TAG밖에도 예를 들어 ITEM_TAG안의 태그와 동일한 이름의 태그가 존재한다.
                        //이럴 경우 ITEM_TAG의 안인지 밖인지를 구분해야 하는데..ITEM_TAG의 밖일 경우 아직 dbo가 만들어지지 않았으므로,
                        //즉 dbo == null이므로 이를 이용하도록 한다.
                        if (tag.equals(ITEM_TAG)) {
                            dbo = new DailyBoxOfficeDto();
                        } else if (tag.equals(RANK_TAG)) {
                            if(dbo != null) tagType = TagType.RANK;
                        } else if (tag.equals(MOVIE_NAME_TAG)) {
                            if(dbo != null) tagType = TagType.MOVIE_NM;
                        } else if (tag.equals(OPEN_DATE_TAG)) {
                            if(dbo != null) tagType = TagType.OPEN_DT;
                        } else if (tag.equals(MOVIE_CODE)) {
                            if(dbo != null) tagType = TagType.MOVIE_CD;
                        } else if (tag.equals(FAULT_RESULT)) {
                            return null;//네트워크 상에는 문제 없었지만 입력값 등의 오류로 xml상에서 값의 오류가 생길때
                        } else if(tag.equals(SALES_TAG)) {
                            if(dbo != null) tagType = TagType.SALES_ACC;
                        } else if(tag.equals(AUDI_TAG)) {
                            if(dbo != null) tagType = TagType.AUDI_ACC;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(ITEM_TAG)) {
                            resultList.add(dbo);
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch (tagType) {
                            case RANK:
                                dbo.setRank(parser.getText());
                                break;
                            case MOVIE_NM:
                                dbo.setMovieNm(parser.getText());
                                break;
                            case OPEN_DT:
                                dbo.setOpenDt(parser.getText());
                                break;
                            case MOVIE_CD:
                                dbo.setMovieCD(parser.getText());
                                break;
                            case SALES_ACC:
                                dbo.setSalesAcc(parser.getText());
                                break;
                            case AUDI_ACC:
                                dbo.setAudiAcc(parser.getText());
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

        return resultList;
    }
}
