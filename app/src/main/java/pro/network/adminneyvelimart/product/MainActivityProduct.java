package pro.network.adminneyvelimart.product;

import static pro.network.adminneyvelimart.app.Appconfig.STOCK;
import static pro.network.adminneyvelimart.app.Appconfig.mypreference;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;


public class MainActivityProduct extends AppCompatActivity implements ProductAdapter.ContactsAdapterListener {
    private static final String TAG = MainActivityProduct.class.getSimpleName();
    ProgressDialog progressDialog;
    int offset = 0;
    boolean isAlreadyLoading;
    ProgressBar productRecycle;
    String shopID = null, shoName = null;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private List<Product> contactList;
    private ProductAdapter mAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainstock);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        shopID = getIntent().getStringExtra("shopId");
        shoName = getIntent().getStringExtra("shopName");
        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new ProductAdapter(this, contactList, this, this);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager addManager1 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(addManager1);
        recyclerView.setAdapter(mAdapter);

        productRecycle = findViewById(R.id.productRecycle);
        NestedScrollView nestedScrollview = findViewById(R.id.nestedScrollview);
        nestedScrollview.setNestedScrollingEnabled(false);
        nestedScrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) { //scrollY is the sliding distance
                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    if (!isAlreadyLoading) {
                        if (searchView != null && !searchView.isIconified() && searchView.getQuery().toString().length() > 0) {
                            fetchContacts(searchView.getQuery().toString());
                        } else {
                            fetchContacts("");
                        }
                    }
                }
            }
        });


        FloatingActionButton addStock = findViewById(R.id.addStock);
        addStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityProduct.this, ProductUpdate.class);
                intent.putExtra("shopId", shopID);
                intent.putExtra("shopName", shoName);
                startActivity(intent);
            }
        });
    }


    private void fetchContacts(final String searchKey) {
        isAlreadyLoading = true;
        String tag_string_req = "req_register";
        progressDialog.setMessage("Processing ...");
        productRecycle.setVisibility(View.VISIBLE);
        String shopId = null;
        if ("Admin".equalsIgnoreCase(sharedPreferences.getString(Appconfig.role, ""))) {
            shopId = "Admin";
        } else {
            shopId = shopID;
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                STOCK + "?searchKey=" + searchKey + "&offset=" + offset * 10 + "" + "&shopid=" + shopId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                productRecycle.setVisibility(View.GONE);
                Log.d("Register Response: ", response);
                if (offset == 0) {
                    contactList = new ArrayList<>();
                }
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");
                    if (jObj.has("offset")) {
                        int offSet = jObj.getInt("offset") / 10;
                        offset = offSet;
                        if (offset == 0) {
                            contactList = new ArrayList<>();
                        }
                    }
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray("data");
                        offset = offset + 1;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Product product = new Product();
                            product.setId(jsonObject.getString("id"));
                            product.setBrand(jsonObject.getString("brand"));
                            product.setCategory(jsonObject.getString("category"));
                            product.setSub_category(jsonObject.getString("sub_category"));
                            product.setShopname(jsonObject.getString("shopname"));
                            product.setStock_update(jsonObject.getString("stock_update"));
                            product.setDescription(jsonObject.getString("description"));
                            product.setPrice(jsonObject.getString("price"));
                            product.setModel(jsonObject.getString("model"));
                            product.setImage(jsonObject.getString("image"));

                            try {
                                JSONObject shopObject = jsonObject.getJSONObject("shop");
                                product.setShopid(shopObject.getString("id"));
                                product.setShopname(shopObject.getString("shop_name"));
                                product.setLatlong(shopObject.getString("latlong"));
                            } catch (Exception e) {
                                Log.e("xxxxxxxxx", e.toString());
                            }

                            contactList.add(product);
                        }
                        mAdapter.notifyData(contactList);
                        isAlreadyLoading = false;
                        getSupportActionBar().setSubtitle(contactList.size() + "  Nos");

                    } else {
                        Toast.makeText(getApplication(), jObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.e("xxxxxxxxxxx", e.toString());
                    Toast.makeText(getApplication(), "Some Network Error.Try after some time", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                productRecycle.setVisibility(View.GONE);
                isAlreadyLoading = false;
                Log.e("Registration Error: ", error.getMessage());
                Toast.makeText(getApplication(),
                        "Some Network Error.Try after some time", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                offset = 0;
                fetchContacts("");
                return false;
            }
        });
        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                if (query.length() > 3) {
                    offset = 0;
                    fetchContacts(query);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (query.length() > 3) {
                    offset = 0;
                    fetchContacts(query);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("");
                }
                return false;
            }
        });
        if (getIntent() != null && getIntent().getBooleanExtra("isSearch", false)) {
            searchView.setIconified(false);
        }
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
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    public void onContactSelected(Product contact) {
        Intent intent = new Intent(MainActivityProduct.this, ProductUpdate.class);
        intent.putExtra("data", contact);
        intent.putExtra("shopId", shopID);
        intent.putExtra("shopName", shoName);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        offset = 0;
        fetchContacts("");
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
