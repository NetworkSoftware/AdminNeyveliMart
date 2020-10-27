package smart.network.patasuadmin.stock;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
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
import smart.network.patasuadmin.app.GlideApp;
import smart.network.patasuadmin.app.Imageutils;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.network.patasuadmin.shop.Shop;

import static smart.network.patasuadmin.app.Appconfig.ALL_SHOP;
import static smart.network.patasuadmin.app.Appconfig.STACK_DELETE;
import static smart.network.patasuadmin.app.Appconfig.STACK_GET_ALL;
import static smart.network.patasuadmin.app.Appconfig.STACK_UPDATE;

/**
 * Created by user_1 on 11-07-2018.
 */

public class StockUpdate extends AppCompatActivity {


    MaterialBetterSpinner title;
    EditText items;
    EditText price;
    EditText itemNo;
    private ProgressDialog pDialog;


   Contact contact;
    String shopId = null;
    MaterialBetterSpinner shopid;

    TextView submit;
    Map<String, String> storecodeMap = new HashMap<>();
    Map<String, String> storeNameMap = new HashMap<>();
    private String[] STOREID = new String[]{
            "Loading",
    };
    private String[] TITLE = new String[]{
            "Sparklers","Ground Chakkars","Flower Pots","Twinkling Star","Pencil","Atom Bombs","One Sound Crakers","Bijili","Chorsa","Giant",
            "Deluxe","Lar Crackers","Rockets","Fancy Comets","Repeating Shots","Matches","Festival Repeating Shot","New Items","Gift Boxes","Guns",
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_register);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        shopid = (MaterialBetterSpinner) findViewById(R.id.storeid);
        title = (MaterialBetterSpinner) findViewById(R.id.title);
        items = (EditText) findViewById(R.id.items);
        price = (EditText) findViewById(R.id.price);
        itemNo = (EditText) findViewById(R.id.itemsNo);


        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().length() > 0 &&
                        price.getText().toString().length() > 0 &&
                        items.getText().toString().length() > 0 &&
                        itemNo.getText().toString().length() > 0 &&
                        shopid.getText().toString().length() > 0
                ) {
                    registerUser(contact);

                }
            }
        });
        shopid=findViewById(R.id.storeid);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, STOREID);
        shopid.setAdapter(stateAdapter);
        shopid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        fetchstoreid();
        ArrayAdapter<String> titleAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, TITLE);
        title.setAdapter(titleAdapter);
        title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        try {

            Contact contact = (Contact) getIntent().getSerializableExtra("object");
            shopId = contact.id;
            items.setText(contact.items);
            itemNo.setText(contact.itemNo);
            title.setText(contact.title);
            price.setText(contact.price);
            shopid.setText(contact.shopname);
            submit.setText("Update");


        } catch (Exception e) {
            Log.e("xxxxxxxxxxx", e.toString());

        }

    }

    private void registerUser(final Contact contact) {
        String tag_string_req = "req_register";
        pDialog.setMessage("Processing ...");
        showDialog();
        // showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                STACK_UPDATE, new Response.Listener<String>() {
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
                localHashMap.put("id", shopId);
                localHashMap.put("title", title.getText().toString());
                localHashMap.put("items", items.getText().toString());
                localHashMap.put("nos", itemNo.getText().toString());
                localHashMap.put("price", price.getText().toString());
                localHashMap.put("shopid", storecodeMap.get(shopid.getText().toString()));
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
    private void fetchstoreid() {
        this.pDialog.setMessage("fetching...");
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest local16 = new JsonObjectRequest(1, ALL_SHOP, jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject localJSONObject1) {
                        hideDialog();
                        try {
                            if (localJSONObject1.getInt("success") == 1) {
                                storecodeMap = new HashMap<>();
                                storeNameMap = new HashMap<>();
                                JSONArray jsonArray = localJSONObject1.getJSONArray("shops");
                                STOREID = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    STOREID[i] = jsonObject1.getString("storename");
                                    storecodeMap.put(jsonObject1.getString("storename"), jsonObject1.getString("id"));
                                    storeNameMap.put(jsonObject1.getString("id"), jsonObject1.getString("storename"));
                                }
                                ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(StockUpdate.this,
                                        android.R.layout.simple_dropdown_item_1line, STOREID);
                                shopid.setAdapter(districtAdapter);


                                return;
                            }
                        } catch (JSONException localJSONException) {
                            localJSONException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                Log.e("tag", "Fetch Error: " + paramVolleyError.getMessage());
                Toast.makeText(getApplicationContext(), paramVolleyError.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {

                HashMap<String, String> localHashMap = new HashMap<String, String>();

                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(local16, "");
    }
    private void fetchtitle() {
        this.pDialog.setMessage("fetching...");
        showDialog();
        JSONObject jsonObject = new JSONObject();

        JsonObjectRequest local16 = new JsonObjectRequest(1, STACK_GET_ALL, jsonObject,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject localJSONObject1) {
                        hideDialog();
                        try {
                            if (localJSONObject1.getInt("success") == 1) {
                                JSONArray jsonArray = localJSONObject1.getJSONArray("staff");
                                TITLE = new String[jsonArray.length()];
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                    TITLE[i] = jsonObject1.getString("title");
                                }
                                ArrayAdapter<String> Adapter = new ArrayAdapter<String>(StockUpdate.this,
                                        android.R.layout.simple_dropdown_item_1line, TITLE);
                                title.setAdapter(Adapter);


                                return;
                            }
                        } catch (JSONException localJSONException) {
                            localJSONException.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError paramVolleyError) {
                Log.e("tag", "Fetch Error: " + paramVolleyError.getMessage());
                Toast.makeText(getApplicationContext(), paramVolleyError.getMessage(), Toast.LENGTH_SHORT).show();
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {

                HashMap<String, String> localHashMap = new HashMap<String, String>();

                return localHashMap;
            }
        };
        AppController.getInstance().addToRequestQueue(local16, "");
    }

}
