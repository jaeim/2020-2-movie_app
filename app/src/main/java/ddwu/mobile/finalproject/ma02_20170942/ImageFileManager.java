package ddwu.mobile.finalproject.ma02_20170942;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFileManager {

    final static String TAG = "ImageFileManager";
    final static String IMG_EXT = ".jpg";

    private Context context;

    public ImageFileManager (Context context) {
        this.context = context;
    }

    /* Bitmap 과 Bitmap 다운로드에 사용한 URL 을 전달받아 내부저장소에 JPG 파일로 저장 후
    파일 이름을 반환, 파일 저장 실패 시 null 반환 */
    public String saveBitmapToTemporary(Bitmap bitmap, String url) {
        String fileName = null;
        try {
            // xxxx.jpg의 형식으로 파일이름을 가져오는 메소드 호출
            fileName = getFileNameFromUrl(url);

            // 내부 저장소에 파일 생성
            File saveFile = new File(context.getFilesDir(), fileName);
            // file용 output 스트림 생성
            FileOutputStream fos = new FileOutputStream(saveFile);
            // 비트맵 이미지를 파일에 기록, Bitmap의 크기가 클 경우 이 부분에서 조정
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();
            Log.d(TAG, "IMAGE SAVED!!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fileName = null;
        } catch (IOException e) {
            e.printStackTrace();
            fileName = null;
        }
        return fileName;
    }


    /* 이미지 다운로드에 사용하는 url 을 전달받아
     url 에 해당하는 내부 저장소에 이미지 파일이 있는지 확인 후 있으면 Bitmap 반환, 없을 경우 null 반환 */
    public Bitmap getBitmapFromTemporary(String url) {
        //file 이름 꺼내는 별도의 메소드를 정의 -> 해당 이름을 가진 이미지파일이 있는지 확인!
        String fileName = getFileNameFromUrl(url);
        String path = context.getFilesDir().getPath() + "/" + fileName;

        // 비트맵일 경우의 읽기
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(path);
        Log.i(TAG, path);

        return bitmap;
    }


    /* 이미지 파일명을 전달받아 외부저장소에 이미지 파일이 있는지 확인 후 있으면 Bitmap 반환, 없을 경우 null 반환 */
    public Bitmap getBitmapFromExternal(String fileName) {
        if (!isExtStorageWritable()) return null;
        String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + fileName;
        Log.i(TAG, path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }


    /* 내부저장소에 저장한 모든 임시 파일을 제거 */
    public void clearTemporaryFiles() {
        File internalStoragefiles = context.getFilesDir();
        File[] files = internalStoragefiles.listFiles();
        for (File target : files) {
            target.delete();
        }

    }


    /* Bitmap 과 Bitmap 다운로드에 사용한 URL 을 전달받아
    내부저장소에 저장한 임시파일을 외부저장소로 옮긴 후 현재시간으로 지정한 파일명을 반환 */
    public String moveFileToExt(String url) {
        if (!isExtStorageWritable()) return null;

//        내부 저장소에 저장하고 있는 원본 파일 준비
        if(url != null) {
            //파일 이름 알아내는 메소드
            String fileName = getFileNameFromUrl(url);
            File internalFile = new File(context.getFilesDir(), fileName);

//        외부 저장소에 저장할 복사본 파일 준비
            String newfileName = getCurrentTime() + IMG_EXT;     // 현재시간.jpg 로 파일이름 지정 (영구적으로 저장시->유일하게 식별 가능)
            File externalFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), newfileName);
//        복사 수행
            boolean result = copyFile(internalFile, externalFile);

//        정상적으로 복사 완료 시 내부저장소의 파일 삭제 및 외부저장소에 저장한 파일명 반환
            if (result) {
                internalFile.delete();
                return newfileName;
            }
        }

        return null;
    }


    /* 외부저장소에 보관 중인 이미지 파일을 제거 */
    public boolean removeImageFromExt(String fileName) {
        if (!isExtStorageWritable()) return false;
        File targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
        return targetFile.delete();
    }


    /* URL에서 파일명에 해당하는 부분을 추출 (예: http://www.dongduk.ac.kr/main/main.jpg → main.jpg)
     사용하는 URL에 따라 달라질 수 있으므로 사용 시 확인 필요 */
    public String getFileNameFromUrl(String url) {
        String fileName = Uri.parse(url).getLastPathSegment();
        return fileName.replace("\n", "");
    }


    /* 현재 시간(초단위)을 문자열로 단위   */
    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss"); //SSS가 밀리세컨드 표시
        return dateFormat.format(new Date());
    }


    /* target을 dest 로 복사, 실패할 경우 false 반환 */
    private boolean copyFile(File targetFile, File destFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        byte[] buffer = null;
        int length = 0;
        try {
            fis = new FileInputStream(targetFile);
            fos = new FileOutputStream(destFile) ;
            buffer = new byte[1024];
            while((length=fis.read(buffer)) != -1){
                fos.write(buffer, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally{
            try {
                fis.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    /* 외부 저장소가 읽기/쓰기가 가능한지 확인 */
    private boolean isExtStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}