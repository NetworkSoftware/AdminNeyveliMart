package pro.network.adminneyvelimart;

import static pro.network.adminneyvelimart.app.Appconfig.mypreference;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.banner.MainActivityBanner;
import pro.network.adminneyvelimart.categories.MainActivityCategories;
import pro.network.adminneyvelimart.coupon.MainActivityCoupon;
import pro.network.adminneyvelimart.deliveryboy.MainActivityDelivery;
import pro.network.adminneyvelimart.news.NewsRegister;
import pro.network.adminneyvelimart.order.MainActivityOrder;
import pro.network.adminneyvelimart.product.MainActivityProduct;
import pro.network.adminneyvelimart.settings.SettingsActivity;
import pro.network.adminneyvelimart.shopreg.MainActivityShop;
import pro.network.adminneyvelimart.user.MainActivityUsers;
import pro.network.adminneyvelimart.videos.MainActivityVideo;

public class

NaviActivity extends AppCompatActivity {
    String role = "Admin";
    String shopId = "shopId";
    String shopName = "shopName";
    private SharedPreferences sharedpreferences;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        role = getIntent().getStringExtra("role");
        shopId = getIntent().getStringExtra("shopId");
        shopName = getIntent().getStringExtra("shopName");
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)  {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
        }
        CardView setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, SettingsActivity.class);
                startActivity(io);
            }
        });
        CardView users = findViewById(R.id.users);
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityUsers.class);
                startActivity(io);
            }
        });
        CardView coupon = findViewById(R.id.coupon);
        coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityCoupon.class);
                startActivity(io);
            }
        });
        CardView news = findViewById(R.id.news);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, NewsRegister.class);
                startActivity(io);
            }
        });
        CardView catrgories = findViewById(R.id.catrgories);
        catrgories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityCategories.class);
                startActivity(io);
            }
        });
        CardView video = findViewById(R.id.video);
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityVideo.class);
                startActivity(io);
            }
        });

        CardView dboy = findViewById(R.id.dboy);
        dboy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityDelivery.class);
                startActivity(io);
            }
        });


        CardView stock = findViewById(R.id.stock);
        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityProduct.class);
                io.putExtra("shopId", shopId);
                io.putExtra("shopName", shopName);
                io.putExtra("role", role);
                startActivity(io);

            }
        });

        CardView shop = findViewById(R.id.shop);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityShop.class);
                startActivity(io);

            }
        });
        CardView banner = findViewById(R.id.banner);
        banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent io = new Intent(NaviActivity.this, MainActivityBanner.class);
                startActivity(io);

            }
        });
        CardView order = findViewById(R.id.orders);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("ordered");
            }
        });

        CardView returned = findViewById(R.id.returned);
        returned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("Returned");
            }
        });

        CardView canceled = findViewById(R.id.canceled);
        canceled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("canceled");
            }
        });
        CardView delivered = findViewById(R.id.delivered);
        delivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navOrderPage("Delivered");
            }
        });

        if ("Admin".equalsIgnoreCase(role)) {
            stock.setVisibility(View.VISIBLE);
            banner.setVisibility(View.VISIBLE);
            catrgories.setVisibility(View.VISIBLE);
            shop.setVisibility(View.VISIBLE);
            news.setVisibility(View.VISIBLE);
            video.setVisibility(View.VISIBLE);
            order.setVisibility(View.VISIBLE);
            canceled.setVisibility(View.VISIBLE);
            delivered.setVisibility(View.VISIBLE);
            dboy.setVisibility(View.VISIBLE);
            returned.setVisibility(View.VISIBLE);
            coupon.setVisibility(View.VISIBLE);
        } else {
            banner.setVisibility(View.GONE);
            catrgories.setVisibility(View.GONE);
            shop.setVisibility(View.GONE);
            news.setVisibility(View.GONE);
            video.setVisibility(View.GONE);
            dboy.setVisibility(View.GONE);
            returned.setVisibility(View.GONE);
            coupon.setVisibility(View.GONE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)  {

            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void navOrderPage(String status) {
        Intent io = new Intent(NaviActivity.this, MainActivityOrder.class);
        io.putExtra("shopId", shopId);
        io.putExtra("shopName", shopName);
        io.putExtra("role", role);
        io.putExtra("status", status);
        startActivity(io);
    }

}
