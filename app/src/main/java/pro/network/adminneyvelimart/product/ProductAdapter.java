package pro.network.adminneyvelimart.product;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.app.GlideApp;

/**
 * Created by ravi on 16/11/17.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Product> productList;
    private List<Product> productListFiltered;
    private ContactsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView model,price,brand,stock_update,description,sub_category,shopname;
        public ImageView thumbnail;
        public MyViewHolder(View view) {
            super(view);
            model = view.findViewById(R.id.model);
            sub_category = view.findViewById(R.id.sub_category);
            shopname = view.findViewById(R.id.shopname);
            brand = view.findViewById(R.id.brand);
            price = view.findViewById(R.id.price);
            stock_update = view.findViewById(R.id.stock_update_row);
            thumbnail = view.findViewById(R.id.thumbnail);
            description=view.findViewById(R.id.description);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onContactSelected(productListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ProductAdapter(Context context, List<Product> productList, ContactsAdapterListener listener, MainActivityProduct mainActivityProduct) {
        this.context = context;
        this.listener = listener;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Product product = productListFiltered.get(position);
        holder.brand.setText(product.getBrand());
        holder.model.setText(product.getModel());
        holder.price.setText("₹ "+product.getNmPrice());
        holder.shopname.setText(product.getShopname());
        holder.shopname.setVisibility(View.VISIBLE);
        if(product.getShopname().equalsIgnoreCase("NA")){
            holder.shopname.setVisibility(View.GONE);
        }
        holder.sub_category.setText(product.getSub_category());
        holder.stock_update.setText(product.getStock_update());
        ArrayList<String> urls = new Gson().fromJson(product.image, (Type) List.class);
        new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

//if(urls.size()>0){
        GlideApp.with(context)
                .load(Appconfig.getResizedImage(urls.get(0), true))
                .into(holder.thumbnail);
//}
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<Product> filteredList = new ArrayList<>();
                    for (Product row : productList) {

                        String val=row.getBrand();
                        if (val.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }else if (row.getModel().contains(charString.toLowerCase())) {
                            filteredList.add(row);

                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                productListFiltered = (ArrayList<Product>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
    public void notifyData(List<Product> productList) {
        this.productListFiltered=productList;
        this.productList=productList;
        notifyDataSetChanged();
    }
    public interface ContactsAdapterListener {
        void onContactSelected(Product product);
    }
}
