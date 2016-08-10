package com.example.ander.stockapilab;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by ander on 8/9/2016.
 */
public class StockContentProvider extends ContentProvider {

    private DBHelper mDBHelper;

    public static final int PRODUCTS = 1;
    public static final int PRODUCTS_ID = 2;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(StockContract.AUTHORITY, DBHelper.TABLE_STOCK, PRODUCTS);
        sURIMatcher.addURI(StockContract.AUTHORITY, DBHelper.TABLE_STOCK + "/#", PRODUCTS_ID);
    }

    @Override
    public boolean onCreate() {
        mDBHelper = DBHelper.getInstance(getContext());
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        switch (uriType) {
            case PRODUCTS:
                id = mDBHelper.addItemsFromClick(contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(StockContract.Stocks.CONTENT_URI, id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        Cursor cursor = null;

        switch (uriType) {
            case PRODUCTS:
                cursor = mDBHelper.findStocks(selection, selectionArgs, sortOrder, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//
//        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
//
//        switch (uriType) {
//            case PRODUCTS:
//                rowsUpdated = mDBHelper.updateProduct(values,selection,selectionArgs,null);
//                break;
//            case PRODUCTS_ID:
//                String id = uri.getLastPathSegment();
//                rowsUpdated = mDBHelper.updateProduct(values,null,null,id);
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

//        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
//
//        switch (uriType) {
//            case PRODUCTS:
//                rowsDeleted = mDBHelper.deleteProduct(selection,selectionArgs,null);
//                break;
//            case PRODUCTS_ID:
//                String id = uri.getLastPathSegment();
//                rowsDeleted = mDBHelper.deleteProduct(null,null,id);
//                break;
//            default:
//                throw new IllegalArgumentException("Unknown URI: " + uri);
//        }
//        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
//    }
    }
}
