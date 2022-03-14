package pro.network.adminneyvelimart.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pro.network.adminneyvelimart.R;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> implements Filterable {


    private final Context context;
    private final OnUsers onUsers;
    private ArrayList<Users> blogArrayList;
    private ArrayList<Users> blogListFiltered;

    public UsersAdapter(Context context, ArrayList<Users> blogArrayList, OnUsers onBlock) {
        this.blogArrayList = blogArrayList;
        this.blogListFiltered = blogArrayList;
        this.context = context;
        this.onUsers = onBlock;

    }

    public void notifyData(ArrayList<Users> myList) {
        this.blogArrayList = myList;
        this.blogListFiltered = myList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_users_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Users bean = blogListFiltered.get(position);
        holder.name.setText(bean.getName());
        holder.phone.setText(bean.getPhone());
        holder.walletAmt.setText("Wallet : " + bean.wallerAmt);
        holder.callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUsers.onCall(bean);
            }
        });
        holder.orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUsers.onOrder(bean);
            }
        });
        holder.walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUsers.onWallet(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    blogListFiltered = blogArrayList;
                } else {
                    ArrayList<Users> filteredList = new ArrayList<>();
                    for (Users row : blogArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPhone().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getId().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    blogListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = blogListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                blogListFiltered = (ArrayList<Users>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, walletAmt;
        ImageView callBtn, orderBtn, walletBtn;

        public MyViewHolder(View view) {
            super(view);
            phone = view.findViewById(R.id.phone);
            walletAmt = view.findViewById(R.id.walletAmt);
            name = view.findViewById(R.id.name);
            callBtn = view.findViewById(R.id.callBtn);
            orderBtn = view.findViewById(R.id.orderBtn);
            walletBtn = view.findViewById(R.id.walletBtn);

        }
    }

}
