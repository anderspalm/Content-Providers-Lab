package com.example.ander.stockapilab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ander on 8/9/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "stockDB.db";

    public static final String TABLE_STOCK = StockContract.Stocks.TABLE_STOCK;
    public static final String _ID = "_id";
    public static final String COMP_NAME = StockContract.Stocks.COMP_NAME;
    public static final String QUANITIY = StockContract.Stocks.QUANITIY;
    public static final String EXC_NAME = StockContract.Stocks.EXC_NAME;
    public static final String STOCK_SYMBOL = StockContract.Stocks.STOCK_SYMBOL;

    private DBHelper(Context context) {
        super(context, "db", null, DATABASE_VERSION);
    }

    private static DBHelper INSTANCE;

    public static synchronized DBHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper(context.getApplicationContext());
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "
                + TABLE_STOCK + "("
                + _ID + " INTEGER PRIMARY KEY,"
                + COMP_NAME + " TEXT,"
                + QUANITIY + " INTEGER,"
                + EXC_NAME + " INTEGER,"
                + STOCK_SYMBOL + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    public Cursor getAllCartItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCK, null, null, null, null, null, null, null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        onCreate(sqLiteDatabase);
    }

    public Cursor findStocks(String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TABLE_STOCK,null,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

//    public void addItemsFromClick(String name, String quantity, String exchange_name) {
//        SQLiteDatabase db = getWritableDatabase();
//        String name1 = "'" + name + "'";
//        String quantity1 = "'" + quantity + "'";
//        String exchange_name1 = "'" + exchange_name + "'";
//
//        db.execSQL("INSERT INTO " + TABLE_STOCK + "(" + COMP_NAME + ", " +
//                QUANITIY + ", " +
//                EXC_NAME + ")" +
//                " VALUES " + "( " + name1 + ", " + quantity1 + ", " + exchange_name1 + " )");
//        db.close();
//    }

    public long addItemsFromClick(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        long insertedRow = db.insert(TABLE_STOCK, null, values);
        db.close();
        return insertedRow;
    }


}
