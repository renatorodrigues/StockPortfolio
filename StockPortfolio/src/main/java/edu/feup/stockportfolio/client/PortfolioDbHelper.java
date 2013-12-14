package edu.feup.stockportfolio.client;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.provider.BaseColumns;

import java.util.ArrayList;

import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class PortfolioDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Portfolio.db";

    public PortfolioDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(PortfolioContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL(PortfolioContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public static final class PortfolioContract {
        public PortfolioContract() {}

        public static abstract class StockEntry implements BaseColumns {
            public static final String TABLE_NAME = "stock";
            public static final String COLUMN_NAME_TICK = "tick";
            public static final String COLUMN_NAME_SHARES = "shares";
        }

        private static final String TEXT_TYPE = " TEXT";
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " +    StockEntry.TABLE_NAME + " (" +
                        StockEntry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                        StockEntry.COLUMN_NAME_TICK + TEXT_TYPE + COMMA_SEP +
                        StockEntry.COLUMN_NAME_SHARES + INTEGER_TYPE +
                        ")";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + StockEntry.TABLE_NAME;
    }

    public void addStock(final StockData stock) {
        SQLiteDatabase db = getWritableDatabase();ContentValues values = new ContentValues();
        values.put(PortfolioContract.StockEntry.COLUMN_NAME_TICK, stock.get_company());
        values.put(PortfolioContract.StockEntry.COLUMN_NAME_SHARES, stock.get_quantity());

        db.insert(
                PortfolioContract.StockEntry.TABLE_NAME,
                null,
                values);

        db.close();
    }

    public void updateStock(final StockData stock) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PortfolioContract.StockEntry.COLUMN_NAME_SHARES, stock.get_quantity());

        String selection = PortfolioContract.StockEntry.COLUMN_NAME_TICK + " = ?";
        String[] selection_args = {
                stock.get_company()
        };

        db.update(
                PortfolioDbHelper.PortfolioContract.StockEntry.TABLE_NAME,
                values,
                selection,
                selection_args);

        db.close();
    }

    public void deleteStock(String company) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = PortfolioContract.StockEntry.COLUMN_NAME_TICK + " = ?";
        String[] selection_args = {
                company
        };

        db.delete(PortfolioContract.StockEntry.TABLE_NAME, selection, selection_args);

        db.close();
    }

    public ArrayList<StockData> getAllStocks() {
        final ArrayList<StockData> stocks = new ArrayList<StockData>();

        String query = "SELECT * FROM " + PortfolioContract.StockEntry.TABLE_NAME;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        StockData stock = null;
        if (cursor.moveToFirst()) {
            do {
                String company = cursor.getString(1);
                int shares = Integer.parseInt(cursor.getString(2));
                stock = new StockData(company, shares);
                stocks.add(stock);
            } while (cursor.moveToNext());
        }

        return stocks;
    }

}
