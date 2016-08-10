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

    public static final String mNoQueryURL = "http://dev.markitondemand.com/MODApis/Api/v2/Lookup/json?input=";
    public static final Uri CONTENT_URI = StockContract.Stocks.CONTENT_URI;
    ContentResolver mContentResolver;
    String mURI;
    private EditText mInputQuery;
    private TextView mStock_text1, mStock_text2, mStock_text3, mStock_text4;
    ListView mListView;

    String mQuantity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mStock_text1 = (TextView) findViewById(R.id.stock_text1);
        mStock_text2 = (TextView) findViewById(R.id.stock_text2);
        mStock_text3 = (TextView) findViewById(R.id.stock_text3);
        mStock_text4 = (TextView) findViewById(R.id.stock_text4);

//        SimpleAdapter adapter = new SimpleAdapter()

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        fab.setOnClickListener(new View.OnClickListener() {
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
                                EditText rawQuery2 = (EditText) dialog.findViewById(R.id.query_quantity);


                                String query = rawQuery.getText().toString().trim();

                                String URL = mNoQueryURL + query;
                                mURI = URL;
                                if (URL != null) {
                                    if (networkInfo != null && networkInfo.isConnected()) {
                                        mQuantity = rawQuery2.toString();
                                        Toast.makeText(MainActivity.this, "Network is active", Toast.LENGTH_SHORT).show();
                                        new DownloadTask().execute(URL);
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

    private JSONArray parseJson3(String contentAsString) throws JSONException {
        ArrayList<String> repoList = new ArrayList<>();
        JSONArray array = new JSONArray(contentAsString);
//    for (int i = 0; i < array.length(); i++)
//    {
//      JSONObject repo = array.getJSONObject(i);
//      String repoName = repo.getString("Exchange");
//      String repoName2 = repo.getString("Name");
//      String repoName3 = repo.getString("Symbol");
//    }
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

                ArrayList<String> array = new ArrayList<>();
                for (int i = 0; i < test.length(); i++) {
                    JSONObject repo = test.getJSONObject(i);
                    String repoName = repo.getString("Exchange");
                    String repoName2 = repo.getString("Name");
                    String repoName3 = repo.getString("Symbol");
                    array.add(repoName);
                    array.add(repoName2);
                    array.add(repoName3);
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
            String result1 = result.get(0);
            String result2 = result.get(1);
            String result3 = result.get(2);
//            mStock_text1.setText(result1);
//            mStock_text2.setText(result2);
//            mStock_text3.setText(result3);
            StockContentProvider sTc = new StockContentProvider();
            ContentValues values = new ContentValues();
            values.put(StockContract.Stocks.COMP_NAME, result2);
            values.put(StockContract.Stocks.EXC_NAME, result1);
            values.put(StockContract.Stocks.QUANITIY, mQuantity);
            values.put(StockContract.Stocks.STOCK_SYMBOL, result3);

            sTc.insert(CONTENT_URI, values);
            mListView = (ListView) findViewById(R.id.listView);

            Cursor cursor = DBHelper.getInstance(MainActivity.this).getAllCartItems();

            CursorAdapter cursorAdapter = new CursorAdapter(MainActivity.this, cursor, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    Toast.makeText(MainActivity.this, "Success... inside adapter", Toast.LENGTH_SHORT).show();
                    return LayoutInflater.from(MainActivity.this).inflate(R.layout.list_view_items, parent, false);
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    mStock_text1.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COMP_NAME)));
                    mStock_text2.setText(cursor.getString(cursor.getColumnIndex(DBHelper.EXC_NAME)));
                    mStock_text3.setText(cursor.getString(cursor.getColumnIndex(DBHelper.QUANITIY)));
                    mStock_text4.setText(cursor.getString(cursor.getColumnIndex(DBHelper.STOCK_SYMBOL)));

                    Toast.makeText(MainActivity.this, "Success... set the view", Toast.LENGTH_SHORT).show();
                }
            };

            mListView.setAdapter(cursorAdapter);

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
