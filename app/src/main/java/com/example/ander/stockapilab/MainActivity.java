package com.example.ander.stockapilab;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    CursorAdapter mAdapter;

    public static final String mNoQueryURL = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";
    public static Uri mCONTENT_URI_Var;
    String mURI;
    ListView mListView;
    FloatingActionButton mFab;
    NetworkInfo mNetworkInfo;

    String mQuantity;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCONTENT_URI_Var = StockContract.Stocks.CONTENT_URI;
        mListView = (ListView) findViewById(R.id.listView);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        // populating listView on Create
        setInitialListView();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = connMgr.getActiveNetworkInfo();

        // calling the fab click event
        fabClickListener();


    }

    public void fabClickListener() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setView(R.layout.alert_dialog);
                builder.setTitle("Here is my title")
                        .setMessage("Here is my message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Dialog dialog = (Dialog) dialogInterface;

                                Toast.makeText(MainActivity.this, "you clicked OK", Toast.LENGTH_SHORT).show();
                                EditText rawQuery = (EditText) dialog.findViewById(R.id.query_symbol);
                                EditText quantity = (EditText) dialog.findViewById(R.id.query_quantity);


                                String query = rawQuery.getText().toString().trim();
                                String quant = quantity.getText().toString().trim();

                                String URL = mNoQueryURL + query;
                                mURI = URL;
                                if (URL != null) {
                                    if (mNetworkInfo != null && mNetworkInfo.isConnected()) {
                                        mQuantity = quant;
                                        Log.i(TAG, "onClick: num " + mQuantity);
                                        Toast.makeText(MainActivity.this, "Network is active", Toast.LENGTH_SHORT).show();
                                        DownloadTask downloadTask = new DownloadTask();
                                        downloadTask.execute(URL);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Check Network Connection", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(MainActivity.this, "you clicked Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setInitialListView() {
        Cursor cursor = DBHelper.getInstance(MainActivity.this).getAllCartItems();

        if (cursor != null) {

            mAdapter = new CursorAdapter(MainActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    return LayoutInflater.from(MainActivity.this).inflate(R.layout.list_view_items, parent, false);
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    TextView companyInfo = (TextView) view.findViewById(R.id.stock_text1);
                    TextView quantity = (TextView) view.findViewById(R.id.stock_text2);

                    companyInfo.setText((cursor.getString(cursor.getColumnIndex(DBHelper.COMP_NAME)))
                            + ",  " + cursor.getString(cursor.getColumnIndex(DBHelper.EXC_NAME))
                            // the following returned values are swapped but will fix soon
                            + ",  " + cursor.getString(cursor.getColumnIndex(DBHelper.QUANITIY)));
                    quantity.setText(cursor.getString(cursor.getColumnIndex(DBHelper.STOCK_SYMBOL)));
                }
            };
            mListView.setAdapter(mAdapter);
        }
    }

    private JSONArray parseJson3(String contentAsString) throws JSONException {
        ArrayList<String> repoList = new ArrayList<>();
        JSONArray array = new JSONArray(contentAsString);
        return array;
    }

    public String readIt(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String read;

        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        return sb.toString();
    }

    private class DownloadTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            JSONArray test = null;
            try {
                test = downloadUrl3(urls[0]);

                // create array of objects
                ArrayList<String> array = new ArrayList<>();
                for (int i = 0; i < test.length(); i++) {
                    JSONObject repo = test.getJSONObject(i);
                    String repoName = repo.getString("Exchange");
                    String repoName2 = repo.getString("Name");
                    String repoName3 = repo.getString("Symbol");
                    String quantity = mQuantity.toString();
                    Log.i(TAG, "onClick: Async num" + mQuantity);
                    array.add(repoName);
                    array.add(repoName2);
                    array.add(repoName3);
                    array.add(quantity);
                }
                return array;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            String result1 = result.get(1);
            String result2 = result.get(2);
            String result3 = result.get(3);
            String result4 = result.get(4);

            // inserting into the SQLITE Table
            ContentValues values = new ContentValues();
            values.put(StockContract.Stocks.COMP_NAME, result2);
            values.put(StockContract.Stocks.EXC_NAME, result1);
            values.put(StockContract.Stocks.STOCK_SYMBOL, result3);
            values.put(StockContract.Stocks.QUANITIY, result4);
            getContentResolver().insert(StockContract.Stocks.CONTENT_URI, values);

            // retrieve from table and swap adapter
            Cursor cursor = DBHelper.getInstance(MainActivity.this).getAllCartItems();
            mAdapter.changeCursor(cursor);
            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        }
    }

    private JSONArray downloadUrl3(String myUrl) throws IOException, JSONException {
        InputStream inputStream = null;
        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn3 = (HttpURLConnection) url.openConnection();
            conn3.setRequestMethod("GET");
            conn3.connect();
            inputStream = conn3.getInputStream();
            String contentAsString = readIt(inputStream);
            JSONArray processedJson = parseJson3(contentAsString);
            return processedJson;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
