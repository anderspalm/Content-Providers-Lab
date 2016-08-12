package com.example.ander.stockapilab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by ander on 8/9/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHelper";

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "stockDB";

    public static final String TABLE_STOCK = StockContract.Stocks.TABLE_STOCK;
    public static final String _ID = BaseColumns._ID;
    public static final String COMP_NAME = StockContract.Stocks.COMP_NAME;
    public static final String QUANITIY = StockContract.Stocks.QUANITIY;
    public static final String EXC_NAME = StockContract.Stocks.EXC_NAME;
    public static final String STOCK_SYMBOL = StockContract.Stocks.STOCK_SYMBOL;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DBHelper INSTANCE;

//    public DBHelper(Context context) {
//        super(context, "stockDB", null,  DATABASE_VERSION);
//    }

    public static synchronized DBHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DBHelper(context);
//            Log.d(TAG, "getInstance: creating new instance");
        }
        return INSTANCE;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "CREATE TABLE "
                + TABLE_STOCK + "("
                + _ID + " INTEGER PRIMARY KEY NOT NULL,"
                + COMP_NAME + " TEXT,"
                + QUANITIY + " INTEGER,"
                + EXC_NAME + " TEXT,"
                + STOCK_SYMBOL + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        onCreate(sqLiteDatabase);
    }


/*

    Queries below

*/



    public Cursor getAllCartItems() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_STOCK, null, null, null, null, null, null, null);
        return cursor;
    }

    public Cursor findStocks(String selection, String[] selectionArgs, String sortOrder, String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        cursor = db.query(TABLE_STOCK,null,selection,selectionArgs,null,null,sortOrder);
        return cursor;
    }

    public long addItemsFromClick(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        long insertedRow = db.insert(TABLE_STOCK, null, values);
        db.close();
        return insertedRow;
    }

    public int deleteProduct(String selection, String[] selectionArgs, String id) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = 0;

        if(id == null)
            rowsDeleted = db.delete(TABLE_STOCK,selection,selectionArgs);
        else
            rowsDeleted = db.delete(TABLE_STOCK,_ID+" = ?",new String[]{id});

        db.close();
        return rowsDeleted;
    }

    public int updateProduct(ContentValues values, String selection, String[] selectionArgs, String id){
        SQLiteDatabase db = getWritableDatabase();

        int updatedRows = 0;

        if(id == null)
            updatedRows = db.update(TABLE_STOCK,values,selection,selectionArgs);
        else
            updatedRows = db.update(TABLE_STOCK,values,_ID+" = ?",new String[]{id});

        db.close();
        return updatedRows;
    }


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
