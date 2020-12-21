package ddwc.mobile.finalproject.ma02_20170942;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyMovieAdapter extends BaseAdapter {
    public static final String TAG = "MyMovieAdapter";

    LayoutInflater inflater;
    Context context;
    int layout;
    private ArrayList<NaverMovieDto> list;
    private NaverNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;

    public MyMovieAdapter(Context context, int resource, ArrayList<NaverMovieDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        imageFileManager = new ImageFileManager(context);
        networkManager = new NaverNetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public NaverMovieDto getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_title = view.findViewById(R.id.tv_movieTitle);
            viewHolder.tv_director = view.findViewById(R.id.tv_director);
            viewHolder.tv_pubDate = view.findViewById(R.id.tv_pubDate);
            viewHolder.poster = view.findViewById(R.id.poster);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        NaverMovieDto dto = list.get(position);

        viewHolder.tv_title.setText(dto.getTitle());
        viewHolder.tv_pubDate.setText(String.valueOf(dto.getPubDate()));
        viewHolder.tv_director.setText(dto.getDirector());

        // 강의자료 2번 방법 사용) 왜 1번 방법은 안될까? 그냥 흰 이미지만 나오고, 임시저장소에 저장을 못함..
        // 파일에 있는지 확인 (파일을 읽어 오는게 네트워크에서 다운로드하는 것 보다 빠르지!)
        // 한번이라도 읽은 적이 있는 이미지는 파일에 저장!
        // dto 의 이미지 주소 정보로 이미지 파일 읽기 (반환 값: null or Bitmap)
        if (dto.getImageLink() == null) {
            viewHolder.poster.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink()); // 파일의 이름을 이미지 링크의 일부로 사용 (xxx.jpg)
            if (savedBitmap != null) {
                viewHolder.poster.setImageBitmap(savedBitmap);
                Log.d(TAG, "Image loading from file");
            } else { //null이면 네트워크를 통해 이미지를 다운로드 하고 파일로 저장!
                //초기화 (네트워크가 느리면 일단 이미지를 기본 이미지로 설정)
                viewHolder.poster.setImageResource(R.mipmap.ic_launcher);
//                viewHolder.poster.setImageBitmap(savedBitmap);
                //현재 이 뷰홀더에서 가져온 네트워크 정보를 넣어줘야하니까 뷰홀더를 생성자에 전달받게한다!
                //안그러면 지금 다운로드한 이미지가 어느 뷰홀더의 이미지뷰인지 뒤죽박죽 헷갈려서 엉뚱한 이미지가 엉뚱한 책의 표지로 들어갈 수 있곘지
                new GetImageAsyncTask(viewHolder).execute(dto.getImageLink());
                Log.d(TAG, "Image loading from network");
            }
        }

        return view;
    }
    static class ViewHolder {
        ImageView poster;
        TextView tv_title;
        TextView tv_director;
        TextView tv_pubDate;

        public ViewHolder() {
            poster = null;
            tv_pubDate = null;
            tv_title = null;
            tv_director = null;
        }
    }
    public void setList(ArrayList<NaverMovieDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

        ViewHolder viewHolder;
        String imageAddress;

        //내가 지금 저장하려는 뷰홀더의 정보 저장!
        public GetImageAsyncTask(ViewHolder holder) {
            viewHolder = holder;
        }

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
            /*작성할 부분*/
            /*네트워크에서 다운 받은 이미지 파일을 (null이 아닐경우) ImageFileManager 를 사용하여 내부저장소에 저장
             * 다운받은 bitmap 을 이미지뷰에 지정*/
            if (bitmap != null) {
                //2번 동작 수행
                viewHolder.poster.setImageBitmap(bitmap);
                //3번 동작 수행
                imageFileManager.saveBitmapToTemporary(bitmap, imageAddress);
            }
        }



    }
}
