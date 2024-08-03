package pro.network.adminneyvelimart.product;

import static org.apache.poi.ss.util.CellUtil.createCell;
import static pro.network.adminneyvelimart.app.Appconfig.STOCK;
import static pro.network.adminneyvelimart.app.Appconfig.UPLOAD_STOCK;
import static pro.network.adminneyvelimart.app.Appconfig.mypreference;
import static pro.network.adminneyvelimart.app.Appconfig.shopId;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import pro.network.adminneyvelimart.R;
import pro.network.adminneyvelimart.app.AndroidMultiPartEntity;
import pro.network.adminneyvelimart.app.AppController;
import pro.network.adminneyvelimart.app.Appconfig;
import pro.network.adminneyvelimart.product.shopfilter.OnShop;
import pro.network.adminneyvelimart.product.shopfilter.ShopFilterAdapter;
import pro.network.adminneyvelimart.product.shopfilter.ShopFilterBean;

public class MainActivityProduct extends AppCompatActivity implements ProductAdapter.ContactsAdapterListener, OnShop {
    private static final int PICKFILE_REQUEST_CODE = 100;
    ProgressDialog progressDialog;
    int offset = 0;
    boolean isAlreadyLoading;
    ProgressBar productRecycle;
    String shopID = null, shoName = null;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView, restaurantChip;
    private List<Product> contactList;
    private ProductAdapter mAdapter;
    private SearchView searchView;
    private ArrayList<ShopFilterBean> shopFilterBeans = new ArrayList<>();
    private Set<String> subShopName = new HashSet<>();
    ShopFilterAdapter shopFilterAdapter;
    private String selectedShop = "All";
    RoundedBottomSheetDialog mBottomSheetDialog;
    TextView fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_mainstok);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        sharedPreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        shopID = getIntent().getStringExtra("shopId");
        shoName = getIntent().getStringExtra("shopName");
        // toolbar fancy stuff
        recyclerView = findViewById(R.id.recycler_view);
        contactList = new ArrayList<>();
        mAdapter = new ProductAdapter(this, contactList, this, this);
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
                            fetchContacts(searchView.getQuery().toString(), false, selectedShop);
                        } else {
                            fetchContacts("", false, selectedShop);
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


        FloatingActionButton stockUpload = findViewById(R.id.stockUpload);
        stockUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockUploadBottom();
            }
        });
        filterShopCate();
    }

    private void filterShopCate() {
        shopFilterBeans = new ArrayList<>();
        restaurantChip = findViewById(R.id.restaurantChip);
        if (!sharedPreferences.getString(Appconfig.role, "").equalsIgnoreCase("isClient")) {
            restaurantChip.setVisibility(View.VISIBLE);
        } else {
            restaurantChip.setVisibility(View.GONE);
        }
        shopFilterAdapter = new ShopFilterAdapter(MainActivityProduct.this, shopFilterBeans, this, selectedShop);
        final LinearLayoutManager addManager2 =
                new LinearLayoutManager(MainActivityProduct.this,
                        LinearLayoutManager.HORIZONTAL, false);
        restaurantChip.setLayoutManager(addManager2);
        restaurantChip.setAdapter(shopFilterAdapter);
    }

    private void fetchContacts(final String searchkey, final boolean sheet, String shopName) {
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
        String url;
        if (sheet) {
            url = STOCK + "?user=true" + "&shopid=" + shopId
                    + "&selectShop=" + shopName;
        } else {
            url = STOCK + "?offset=" + offset * 10 + "" + "&shopid=" + shopId + "&searchKey=" + searchkey
                    + "&selectShop=" + shopName;
        }
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                productRecycle.setVisibility(View.GONE);
                Log.d("Register Response: ", response);
                if (offset == 0) {
                    contactList = new ArrayList<>();
                    subShopName = new HashSet<>();
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
                            product.setMrp(jsonObject.getString("mrp"));
                            product.setNmPrice(jsonObject.getString("nmPrice"));
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
                            JSONArray jsonShop = jObj.getJSONArray("shopname");
                            if (jsonShop != null) {
                                for (int b = 0; b < jsonShop.length(); b++) {
                                    String string = jsonShop.getString(b);
                                    if (string.length() > 0) {
                                        subShopName.add(string.toUpperCase());
                                    }
                                }
                            }
                            contactList.add(product);
                        }
                        if (sheet) {
                            // printXLSheet(contactList);
                        }
                        mAdapter.notifyData(contactList);
                        if (shopFilterBeans.size() <= 0) {
                            shopFilterBeans = new ArrayList<>();
                            shopFilterBeans.add(new ShopFilterBean("All"));
                            for (String e : subShopName) {
                                shopFilterBeans.add(new ShopFilterBean(e));
                            }
                            shopFilterAdapter.notifyData(shopFilterBeans);
                            shopFilterAdapter.notifyData(selectedShop);
                        }

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

    public void printXLSheet(List<Product> contactList) {

        try {
            String path = getApplication().getExternalCacheDir().getPath() + "/PDF";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File uriFile = new File(dir, System.currentTimeMillis() + ".xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName("report"));

            int columns = 8;
            for (int i = 0; i < columns; i++) {
                sheet.setColumnWidth(i, 15 * 256);
            }

            Row row = sheet.createRow(0);
            createCell(row, 0, "S.NO");
            createCell(row, 1, "ID");
            createCell(row, 2, "Product");
            createCell(row, 3, "MRP");
            createCell(row, 4, "NM Price");
            createCell(row, 5, "Image");
            createCell(row, 6, "Description");
            createCell(row, 7, "Category");
            createCell(row, 8, "Stock Update");
            createCell(row, 9, "Sub_category");
            createCell(row, 10, "Shop name");


            for (int i = 0; i < contactList.size(); i++) {
                Row row1 = sheet.createRow(i + 1);
                Product userBean = contactList.get(i);
                createCell(row1, 0, String.valueOf(i + 1));
                createCell(row1, 1, userBean.getId());
                createCell(row1, 2, userBean.getBrand() + " - " + userBean.getModel());
                createCell(row1, 3, userBean.getMrp());
                createCell(row1, 4, userBean.getNmPrice());
                createCell(row1, 5, userBean.getImage());
                createCell(row1, 6, userBean.getDescription());
                createCell(row1, 7, userBean.getCategory());
                createCell(row1, 8, userBean.getStock_update());
                createCell(row1, 9, userBean.getSub_category());
                createCell(row1, 10, userBean.getShopname());
            }
            Uri fileUri = Uri.fromFile(uriFile);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileUri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplication().getPackageName() + ".provider",
                        uriFile);
            }

            OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
            Log.i("TAG", "writeExcel: export successful");
            Toast.makeText(getApplicationContext(), "export successful", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(fileUri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } else {
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(fileUri, "application/xlsx");
                Intent intent = Intent.createChooser(target, "Open File");
                startActivity(intent);
            }

            hideDialog();
        } catch (Exception e) {
            Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            hideDialog();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_sheet);
        item.setVisible(!sharedPreferences.getString(Appconfig.role, "").equalsIgnoreCase("isClient"));
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
                fetchContacts("", false, selectedShop);
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
                    fetchContacts(query, false, selectedShop);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("", false, selectedShop);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if (query.length() > 3) {
                    offset = 0;
                    fetchContacts(query, false, selectedShop);
                } else if (query.length() == 0) {
                    offset = 0;
                    fetchContacts("", false, selectedShop);
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

        if (id == R.id.action_sheet) {
            fetchContacts("", true, selectedShop);

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
        fetchContacts("", false, selectedShop);
    }


    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onShop(String status) {
        selectedShop = status;
        shopFilterAdapter.notifyData(selectedShop);
        offset = 0;
        fetchContacts("", false, status);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICKFILE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    String[] nameVal = filePath.split("/");
                    fileName.setText(nameVal[nameVal.length - 1]);
                    new UploadFileToServer().execute(filePath);
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void stockUploadBottom() {
        mBottomSheetDialog = new RoundedBottomSheetDialog(MainActivityProduct.this);
        LayoutInflater inflater = MainActivityProduct.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.mart_excel_stock, null);


        final ImageView clear = dialogView.findViewById(R.id.clear);
        final Button selectFile = dialogView.findViewById(R.id.selectFile);
//        amount = dialogView.findViewById(R.id.amount);

        fileName = dialogView.findViewById(R.id.fileName);
        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(MainActivityProduct.this)
                        .withCloseMenu(true)
                        .withHiddenFiles(false)
                       // .withFilter(Pattern.compile(".*\\.(xlsx)$"))
                        .withFilterDirectories(false)
                        .withTitle("Select file")
                        .withRequestCode(PICKFILE_REQUEST_CODE)
                        .start();
            }
        });


        mBottomSheetDialog.setContentView(dialogView);
        mBottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RoundedBottomSheetDialog d = (RoundedBottomSheetDialog) dialog;
                        FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }, 0);
            }
        });
        mBottomSheetDialog.show();

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.hide();
            }
        });
    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {
        public long totalSize = 0;
        String filepathUri;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setMessage("Uploading..." + (progress[0]));
        }

        @Override
        protected String doInBackground(String... params) {
            filepathUri = params[0];
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(UPLOAD_STOCK);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                try {
                    File sourceFile = new File(filepathUri.contains(":") ? filepathUri.split(":")[1].trim() : filepathUri.trim());
                    entity.addPart("file", new FileBody(sourceFile));
                    totalSize = entity.getContentLength();
                    httppost.setEntity(entity);
                } catch (Exception e) {
                    Log.e("xxxxdata", e.toString());
                }
                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Response from server: ", result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getBoolean("success")) {
                    Toast.makeText(getApplicationContext(), "Stock upload successfully", Toast.LENGTH_SHORT).show();
                    fetchContacts("", false, selectedShop);
                    fileName.setText("");
                    if (mBottomSheetDialog != null) {
                        mBottomSheetDialog.cancel();
                    }

                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Excel not uploaded", Toast.LENGTH_SHORT).show();
            }
            hideDialog();
            super.onPostExecute(result);
        }

    }

}
