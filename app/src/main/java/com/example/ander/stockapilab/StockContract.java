package com.example.ander.stockapilab;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ander on 8/9/2016.
 */
public final class StockContract {
    public static final String AUTHORITY = "com.example.ander.stockapilab.ProductsContentProvider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Stocks implements BaseColumns {
        public static final String TABLE_STOCK = "stock_table";
        public static final String COMP_NAME = "company_name";
        public static final String QUANITIY = "stock_quantity";
        public static final String EXC_NAME = "exchange_name";
        public static final String STOCK_SYMBOL = "stock_symbol";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, "stock_table");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/com.example.ander.stock_table";

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/com.example.ander.stock_table";
    }

}
