package pro.network.adminneyvelimart.order;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.Appconfig;

/**
 * Created by ravi on 16/11/17.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>
        implements Filterable {
    private final Context context;
    private final ContactsAdapterListener listener;
    StatusListener statusListener;
    private List<Order> orderList;
    private List<Order> orderListFiltered;
    SharedPreferences sharedPreferences;

    public OrderAdapter(Context context, List<Order> orderList, ContactsAdapterListener listener,
                        StatusListener statusListener, SharedPreferences sharedPreferences) {
        this.context = context;
        this.listener = listener;
        this.orderList = orderList;
        this.orderListFiltered = orderList;
        this.statusListener = statusListener;
        this.sharedPreferences = context.getSharedPreferences(Appconfig.mypreference, Context.MODE_PRIVATE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Order order = orderListFiltered.get(position);

        if ("Admin".equalsIgnoreCase(sharedPreferences.getString(Appconfig.role, ""))) {
            holder.phoneLi.setVisibility(View.VISIBLE);
            holder.nameLi.setVisibility(View.VISIBLE);
            holder.reason.setVisibility(View.VISIBLE);
            holder.desTxt.setVisibility(View.VISIBLE);
        } else {
            holder.phoneLi.setVisibility(View.GONE);
            holder.nameLi.setVisibility(View.GONE);
            holder.reason.setVisibility(View.GONE);
            holder.desTxt.setVisibility(View.GONE);
        }
        holder.order_id.setText("#" + order.getId());
        holder.price.setText(order.getPrice());
        holder.quantity.setText(order.getQuantity());
        holder.status.setText(order.getStatus());
        holder.phone.setText(order.getPhone());
        holder.name.setText(order.getName());
        holder.dtime.setText(order.getDtime());
        holder.address.setText(order.getAddress());
        holder.addressUser.setText(order.getLiveLocation());

        holder.reason.setText(order.getReson());
        //holder.orderedOn.setText(Appconfig.convertTimeToLocal(order.createdOn));
        holder.dCharge.setText(order.dcharge);
        if (order.getStatus().equalsIgnoreCase("ordered")) {
            holder.assignDboy.setVisibility(View.VISIBLE);
            holder.cancalOrder.setVisibility(View.VISIBLE);
            holder.inprogress.setVisibility(View.GONE);
            holder.deliveredBtn.setVisibility(View.GONE);
            holder.completed.setVisibility(View.GONE);

        } else if (order.getStatus().equalsIgnoreCase("Delivery Boy Assigned") ||
                order.getStatus().equalsIgnoreCase("Delivery Boy Picked")) {
            holder.assignDboy.setVisibility(View.GONE);
            holder.inprogress.setVisibility(View.VISIBLE);
            holder.cancalOrder.setVisibility(View.VISIBLE);
            holder.deliveredBtn.setVisibility(View.GONE);
            holder.completed.setVisibility(View.GONE);
        } else if (order.getStatus().equalsIgnoreCase("InProgress")) {
            holder.assignDboy.setVisibility(View.GONE);
            holder.inprogress.setVisibility(View.GONE);
            holder.cancalOrder.setVisibility(View.GONE);
            holder.deliveredBtn.setVisibility(View.VISIBLE);
            holder.completed.setVisibility(View.GONE);
        } else if (order.getStatus().equalsIgnoreCase("Delivered") ||
                (order.getStatus().equalsIgnoreCase("canceled"))) {
            holder.assignDboy.setVisibility(View.GONE);
            holder.inprogress.setVisibility(View.GONE);
            holder.deliveredBtn.setVisibility(View.GONE);
            holder.cancalOrder.setVisibility(View.GONE);
            holder.completed.setVisibility(View.GONE);
        } else {
            holder.assignDboy.setVisibility(View.VISIBLE);
            holder.inprogress.setVisibility(View.VISIBLE);
            holder.cancalOrder.setVisibility(View.VISIBLE);
            holder.deliveredBtn.setVisibility(View.VISIBLE);
            holder.completed.setVisibility(View.VISIBLE);
        }


        OrderListSubAdapter OrderListAdapter = new OrderListSubAdapter(context, order.productBeans);
        final LinearLayoutManager addManager1 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        holder.cart_sub_list.setLayoutManager(addManager1);
        holder.cart_sub_list.setAdapter(OrderListAdapter);
        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onDeliveredClick(order.id);
            }
        });
        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onWhatsAppClick(order.phone);
            }
        });
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCallClick(order.phone);
            }
        });
        holder.cancalOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onCancelClick(order.id);
            }
        });
        holder.inprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.InProgress(order);
            }
        });
        holder.trackOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusListener.onTrackOrder(order.id);
            }
        });
        holder.assignDboy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.onAssignDboy(order);
            }
        });
        holder.cashback.setText(order.cashback);
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusListener.bill(order);
            }
        });
        holder.wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                statusListener.wallet(order);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    orderListFiltered = orderList;
                } else {
                    List<Order> filteredList = new ArrayList<>();
                    for (Order row : orderList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        String val = row.getName();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        } else if (row.getPhone().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    orderListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = orderListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                orderListFiltered = (ArrayList<Order>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void notifyData(List<Order> orderList) {
        this.orderListFiltered = orderList;
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public interface ContactsAdapterListener {
        void onContactSelected(Order order);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, price, status, quantity, dCharge,
                phone, desTxt, nameTxt, phoneTxt, orderedOn,addressUser, address, dtime, reason, cashback, order_id;
        public ImageView thumbnail;
        public RecyclerView cart_sub_list;
        LinearLayout nameLi, phoneLi;
        Button deliveredBtn, whatsapp, call, cancalOrder,
                assignDboy, trackOrder, wallet, inprogress, completed, bill;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            // orderedOn = view.findViewById(R.id.orderedOn);
            nameLi = view.findViewById(R.id.nameLi);
            phoneLi = view.findViewById(R.id.phoneLi);
            desTxt = view.findViewById(R.id.desTxt);
            phoneTxt = view.findViewById(R.id.phoneTxt);
            nameTxt = view.findViewById(R.id.nameTxt);
            price = view.findViewById(R.id.price);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            status = view.findViewById(R.id.status);
            dCharge = view.findViewById(R.id.dCharge);
            quantity = view.findViewById(R.id.quantity);
            thumbnail = view.findViewById(R.id.thumbnail);
            cart_sub_list = view.findViewById(R.id.cart_sub_list);
            deliveredBtn = view.findViewById(R.id.deliveredBtn);
            whatsapp = view.findViewById(R.id.whatsapp);
            cancalOrder = view.findViewById(R.id.cancalOrder);
            call = view.findViewById(R.id.call);
            address = view.findViewById(R.id.address);
            reason = view.findViewById(R.id.reason);
            assignDboy = view.findViewById(R.id.assignDboy);
            inprogress = view.findViewById(R.id.inprogress);
            completed = view.findViewById(R.id.completed);
            trackOrder = view.findViewById(R.id.trackOrder);
            order_id = view.findViewById(R.id.order_id);
            bill = view.findViewById(R.id.bill);
            dtime = view.findViewById(R.id.dtime);
            wallet = view.findViewById(R.id.wallet);
            cashback = view.findViewById(R.id.cashback);
            addressUser =view.findViewById(R.id.addressUser);

        }
    }
}
