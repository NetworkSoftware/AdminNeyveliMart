package pro.network.adminneyvelimart.user;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.order.MainActivityOrder;
import pro.network.adminneyvelimart.wallet.WalletActivity;

public class MainActivityUsers extends AppCompatActivity implements OnUsers {

    ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<Users> bannerList;
    private UsersAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Users");

        recyclerView = findViewById(R.id.user_recycler_view);
        bannerList = new ArrayList<>();
        mAdapter = new UsersAdapter(this, bannerList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
getAllUser();;
    }

    private void getAllUser() {

        String tag_string_req = "req_register";
        progressDialog.setMessage("Loading...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                Appconfig.USER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d("Register Response: ", response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    bannerList = new ArrayList<>();
                    if (jObj.getBoolean("success")) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Users blogReview = new Users();
                            blogReview.setId(jsonObject.getString("id"));
                            blogReview.setName(jsonObject.getString("name"));
                            blogReview.setPhone(jsonObject.getString("phone"));
                            blogReview.setWallerAmt(jsonObject.getString("walletAmt"));
                            if (jsonObject.has("email")) {
                                blogReview.setEmail(jsonObject.getString("email"));
                            } else {
                                blogReview.setEmail("NA");
                            }
                            bannerList.add(blogReview);
                        }
                    }
                    mAdapter.notifyData(bannerList);
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
            }
        }) {
            protected Map<String, String> getParams() {
                return new HashMap();
            }
        };
        strReq.setRetryPolicy(Appconfig.getPolicy());
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllUser();
    }


    @Override
    public void onCall(Users position) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + position.phone));
        startActivity(intent);
    }

    @Override
    public void onOrder(Users position) {
        Intent io = new Intent(MainActivityUsers.this, MainActivityOrder.class);
        io.putExtra("id", position.getId());
        startActivity(io);
    }

    @Override
    public void onWallet(Users users) {
        Intent io = new Intent(MainActivityUsers.this, WalletActivity.class);
        io.putExtra("id", users.getId());
        io.putExtra("name", users.getName());
        startActivity(io);
    }

}
