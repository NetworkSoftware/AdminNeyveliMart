package pro.network.jsbroilersadmin;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;

import pro.network.jsbroilersadmin.banner.MainActivityBanner;
import pro.network.jsbroilersadmin.categories.MainActivityCategories;
import pro.network.jsbroilersadmin.news.NewsRegister;
import pro.network.jsbroilersadmin.order.MainActivityOrder;
import pro.network.jsbroilersadmin.product.MainActivityProduct;

public class NaviActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);


        CardView catrgories = (CardView) findViewById(R.id.catrgories);
        catrgories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityCategories.class);
                startActivity(io);
            }
        });

        CardView news = (CardView) findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, NewsRegister.class);
                startActivity(io);
            }
        });
        CardView stock = (CardView) findViewById(R.id.stock);
        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityProduct.class);
                startActivity(io);

            }
        });
        CardView banner = (CardView) findViewById(R.id.banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityBanner.class);
                startActivity(io);

            }
        });
        CardView order = (CardView) findViewById(R.id.orders);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("ordered");
            }
        });
        CardView returned = (CardView) findViewById(R.id.returned);
        returned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("Returned");
            }
        });

        CardView canceled = (CardView) findViewById(R.id.canceled);
        canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("canceled");
            }
        });
        CardView delivered = (CardView) findViewById(R.id.delivered);
        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("Delivered");
            }
        });

    }

    private void navOrderPage(String status){
        Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
        io.putExtra("status",status);
        startActivity(io);
    }
}
