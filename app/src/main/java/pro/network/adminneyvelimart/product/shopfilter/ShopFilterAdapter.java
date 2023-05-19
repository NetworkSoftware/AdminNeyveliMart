package pro.network.neyvelimart.shopfilter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pro.network.neyvelimart.R;
import pro.network.neyvelimart.chip.ShopBean;


public class ShopFilterAdapter extends RecyclerView.Adapter<ShopFilterAdapter.MyViewHolder> {


    private final Context context;
    private final OnShop onBlock;
    private ArrayList<ShopFilterBean> blogArrayList;
    private String selectedPosition;

    public ShopFilterAdapter(Context context, ArrayList<ShopFilterBean> blogArrayList, OnShop onBlock, String selectedPosition) {
        this.blogArrayList = blogArrayList;
        this.context = context;
        this.onBlock = onBlock;
        this.selectedPosition = selectedPosition;
    }

    public void notifyData(ArrayList<ShopFilterBean> myList) {
        this.blogArrayList = myList;
        notifyDataSetChanged();
    }

    public void notifyData(String selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.status_filter, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ShopFilterBean bean = blogArrayList.get(position);
        holder.action_chip.setText(bean.getShopName());
        if (bean.getShopName().equalsIgnoreCase(selectedPosition)) {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipSelected)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
        } else {
            holder.action_chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.chipUnSelect)));
            holder.action_chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray)));
        }

        holder.action_chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBlock.onShop(bean.getShopName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        com.google.android.material.chip.Chip action_chip;

        public MyViewHolder(View view) {
            super(view);
            action_chip = view.findViewById(R.id.status_filter);
        }
    }
}