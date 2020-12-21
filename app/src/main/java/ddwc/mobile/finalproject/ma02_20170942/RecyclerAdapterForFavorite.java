package ddwc.mobile.finalproject.ma02_20170942;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapterForFavorite extends RecyclerView.Adapter<RecyclerAdapterForFavorite.ViewHolder> {
    public static final String TAG = "RecyclerAdapter";
    LayoutInflater inflater;
    Context context;
    int layout;
    private ArrayList<NaverMovieDto> list;
    private NaverNetworkManager networkManager = null;
    private ImageFileManager imageFileManager = null;
    MovieDBManager movieDBManager;

    public RecyclerAdapterForFavorite(Context context, ArrayList<NaverMovieDto> data) {
        list = data;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageFileManager = new ImageFileManager(context);
        networkManager = new NaverNetworkManager(context);
        movieDBManager = new MovieDBManager(context);
    }
    // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos = position;
        NaverMovieDto dto = list.get(position);

        holder.tv_title.setText(dto.getTitle());
        holder.tv_en_title.setText(dto.getSubTitle());

        if (dto.getImageLink() == null) {
            holder.poster.setImageResource(R.mipmap.ic_launcher);
        } else {
            Bitmap savedBitmap = imageFileManager.getBitmapFromTemporary(dto.getImageLink()); // 파일의 이름을 이미지 링크의 일부로 사용 (xxx.jpg)
            if (savedBitmap != null) {
                holder.poster.setImageBitmap(savedBitmap);
                Log.d(TAG, "Image loading from file");
            } else { //null이면 네트워크를 통해 이미지를 다운로드 하고 파일로 저장!
                //초기화 (네트워크가 느리면 일단 이미지를 기본 이미지로 설정)
                holder.poster.setImageResource(R.mipmap.ic_launcher);
//                holder.poster.setImageBitmap(savedBitmap);
                new GetImageAsyncTask(holder).execute(dto.getImageLink());
                Log.d(TAG, "Image loading from network");
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailFavoriteActivity.class);
                intent.putExtra("movie", list.get(pos));
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("목록에서 삭제")
                        .setMessage("영화 " + dto.getTitle() + "를(을) 삭제하시겠습니까?")
                        .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(movieDBManager.removeMovieFromFavorite(dto.get_id())) {
                                    Toast.makeText(context, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    list.remove(pos);
                                    notifyItemRemoved(pos);
                                    notifyItemRangeChanged(pos, list.size());
                                    // notifydatasetChanged..?
                                } else {
                                    Toast.makeText(context, "삭제하지 못했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public void setList(ArrayList<NaverMovieDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView tv_title;
        TextView tv_en_title;
        ViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.recycle_poster);
            tv_title = itemView.findViewById(R.id.recycle_title);
            tv_en_title = itemView.findViewById(R.id.recycle_en_title);
        }
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
