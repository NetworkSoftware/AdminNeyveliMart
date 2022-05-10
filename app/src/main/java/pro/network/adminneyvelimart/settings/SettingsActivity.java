package pro.network.adminneyvelimart.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;

import static pro.network.adminneyvelimart.app.Appconfig.SETTING;
import static pro.network.adminneyvelimart.app.Appconfig.mypreference;


public class SettingsActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    Button save;
    TextInputEditText packingCost;
    TextInputEditText shippingCost;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_settings);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        save = findViewById(R.id.save);
        shippingCost = findViewById(R.id.shippingCost);
        packingCost = findViewById(R.id.packingCost);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shippingCost.getText().toString().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "Enter ShippingCost", Toast.LENGTH_SHORT).show();
                }else if(packingCost.getText().toString().length() <=0){
                    Toast.makeText(getApplicationContext(), "Enter PackingCost", Toast.LENGTH_SHORT).show();
                } else {
                    updateSettings();
                }
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_round_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fetchContacts();
    }

    private void fetchContacts() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                SETTING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    if (success == 1) {
                        JSONArray data = jObj.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject = data.getJSONObject(i);
                            if (jsonObject.getString("name").equalsIgnoreCase("config")) {
                                Setting settings = new Gson().fromJson(jsonObject.getString("value"), Setting.class);
                                shippingCost.setText(settings.shippingCost + "");
                                packingCost.setText(settings.packingCost + "");
                            }
                        }
                    }
                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateSettings() {
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                SETTING, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register Response: ", response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                HashMap localHashMap = new HashMap();
                Setting setting = new Setting(
                        Integer.parseInt(shippingCost.getText().toString()),
                        Integer.parseInt(packingCost.getText().toString()));
                localHashMap.put("config", new Gson().toJson(setting));
                return localHashMap;
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
