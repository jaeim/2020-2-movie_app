package ddwc.mobile.finalProject.movie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BoxOfficeAdapter extends BaseAdapter {
    public static final String TAG = "MyMovieAdapter";

    LayoutInflater inflater;
    Context context;
    int layout;
    private ArrayList<DailyBoxOfficeDto> list;
    private NaverNetworkManager networkManager = null;

    public BoxOfficeAdapter(Context context, int resource, ArrayList<DailyBoxOfficeDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        networkManager = new NaverNetworkManager(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public DailyBoxOfficeDto getItem(int position) {
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

        if(view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv_rank = view.findViewById(R.id.list_box_rank);
            viewHolder.tv_title = view.findViewById(R.id.list_box_title);
            viewHolder.tv_sales = view.findViewById(R.id.list_box_sales);
            viewHolder.tv_audience = view.findViewById(R.id.list_box_audi);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }
        DailyBoxOfficeDto dto = list.get(position);

        viewHolder.tv_rank.setText(dto.getRank() + " 위");
        viewHolder.tv_title.setText(dto.getMovieNm());

        long sales = Long.valueOf(dto.getSalesAcc());
        long audience = Long.valueOf(dto.getAudiAcc());

        viewHolder.tv_sales.setText("누적 매출액: " + String.format("%,d", sales) + " 원");
        viewHolder.tv_audience.setText("누적 관객수: " + String.format("%,d", audience) + " 명");

        return view;
    }
    static class ViewHolder {
        TextView tv_rank;
        TextView tv_title;
        TextView tv_sales;
        TextView tv_audience;

        public ViewHolder() {
            this.tv_rank = null;
            this.tv_title = null;
            this.tv_sales = null;
            this.tv_audience = null;
        }
    }
    public void setList(ArrayList<DailyBoxOfficeDto> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
