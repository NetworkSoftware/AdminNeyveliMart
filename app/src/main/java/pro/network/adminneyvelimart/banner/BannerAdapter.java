package pro.network.adminneyvelimart.banner;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.app.GlideApp;

/**
 * Created by ravi on 16/11/17.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
    private final Context context;
    private List<Banner> bannerList;
    private final BannerClick bannerClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail, cancel;
        public TextView description, type;

        public MyViewHolder(View view) {
            super(view);


            thumbnail = view.findViewById(R.id.thumbnail);
            cancel = view.findViewById(R.id.cancel);
            type = view.findViewById(R.id.type);
            description = view.findViewById(R.id.description);

        }
    }


    public BannerAdapter(Context context, List<Banner> bannerList, BannerClick bannerClick) {
        this.context = context;
        this.bannerList = bannerList;
        this.bannerClick = bannerClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Banner banner = bannerList.get(position);

        GlideApp.with(context)
                .load(Appconfig.getResizedImage(banner.getBanner(), true))
                .placeholder(R.drawable.vivo)
                .into(holder.thumbnail);
        holder.description.setText(banner.getDescription());
        holder.type.setText(banner.getType());
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onDeleteClick(position);
            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bannerClick.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }


    public void notifyData(List<Banner> bannerList) {
        this.bannerList = bannerList;
        notifyDataSetChanged();
    }
}
