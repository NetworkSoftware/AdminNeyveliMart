package smart.network.patasuadmin.shop;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import smart.network.patasuadmin.R;
import smart.network.patasuadmin.app.AndroidMultiPartEntity;
import smart.network.patasuadmin.app.AppController;
import smart.network.patasuadmin.app.Appconfig;
import smart.network.patasuadmin.app.BaseActivity;
import smart.network.patasuadmin.app.GlideApp;
import smart.network.patasuadmin.app.Imageutils;
import de.hdodenhof.circleimageview.CircleImageView;


import static smart.network.patasuadmin.app.Appconfig.mypreference;

/**
 * Created by user_1 on 11-07-2018.
 */

public class ShopRegister extends BaseActivity {




    EditText contact;
    EditText shopname;
    EditText address;
    private TextView submit;
    private ProgressDialog pDialog;
    SharedPreferences sharedpreferences;

    @Override
    protected void startDemo() {
        setContentView(R.layout.shop_register);
        getSupportActionBar().setTitle("Shop Register");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);




        contact = (EditText) findViewById(R.id.contact);
        shopname = (EditText) findViewById(R.id.shopName);
        address = (EditText) findViewById(R.id.address);
       submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (    contact.getText().toString().length() > 0 &&
                        shopname.getText().toString().length() > 0
                        && address.getText().toString().length() > 0) {

                   if (contact.getText().toString().length() != 10 || contact.getText().toString().matches(".*[a-zA-Z]+.*")) {
                        Toast.makeText(getApplicationContext(), "Enter a valid Contact", Toast.LENGTH_SHORT).show();
                    } else {
                        Shop shop = new Shop(
                                contact.getText().toString(),
                                shopname.getText().toString(),
                                address.getText().toString());

                        registerUser(shop);
                    }
                }
            }
        });

    }

    private void registerUser(final Shop shop) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Appconfig.CREATE_SHOP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.length()));
                    int success = jObj.getInt("success");
                    String msg = jObj.getString("message");
                    if (success == 1) {
                        finish();
                    }
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                localHashMap.put("storename", shopname.getText().toString());
                localHashMap.put("phone", contact.getText().toString());
                localHashMap.put("address", address.getText().toString());
                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }






    @Override
    protected void onPause() {
        super.onPause();
        hideDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



}
