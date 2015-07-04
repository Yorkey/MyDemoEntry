package com.yu.wangy.mydemoentry.cp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by wangyu on 15-6-29.
 */
public class ContactProvider extends ContentProvider {

    private static final String TAG = "ContactProvider";
    private static final HashMap<String, String> sContactProjectMap = new HashMap<String, String>();
    static {
        sContactProjectMap.put(ContactTableMetaData._ID,
                ContactTableMetaData._ID);
        sContactProjectMap.put(ContactTableMetaData.CONTACT_ACCOUNT,
                ContactTableMetaData.CONTACT_ACCOUNT);
        sContactProjectMap.put(ContactTableMetaData.CONTACT_NICKNAME,
                ContactTableMetaData.CONTACT_NICKNAME);
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int INCOMING_CONTACT_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SIGNLE_CONTACT_URI_INDICATOR = 2;
    static {
        sUriMatcher.addURI(ContactProviderMetaData.AUTHORITY,
                "mycontacts",
                INCOMING_CONTACT_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(ContactProviderMetaData.AUTHORITY,
                "mycontacts/#",
                INCOMING_SIGNLE_CONTACT_URI_INDICATOR);
    }

    private DatabaseHelper mOpenHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, ContactTableMetaData.TABLE_NAME, null,
                    ContactProviderMetaData.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "CREATE TABLE " + ContactTableMetaData.TABLE_NAME + " ("
                    + ContactTableMetaData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + ContactTableMetaData.CONTACT_ACCOUNT + " TEXT,"
                    + ContactTableMetaData.CONTACT_NICKNAME + " TEXT);";
            Log.d("MYDatabaseHelper", sql);
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DatabaseHelper", "Updgrading database form version " + oldVersion + " to " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + ContactTableMetaData.TABLE_NAME);
            onCreate(db);
        }
    }



    @Override
    public boolean onCreate() {
        Log.d("MYDatabaseHelper", "onCreate()");
        mOpenHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case INCOMING_CONTACT_COLLECTION_URI_INDICATOR:
                qb.setTables(ContactTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sContactProjectMap);
                break;
            case INCOMING_SIGNLE_CONTACT_URI_INDICATOR:
                qb.setTables(ContactTableMetaData.TABLE_NAME);
                qb.setProjectionMap(sContactProjectMap);
                qb.appendWhere(ContactTableMetaData.CONTACT_ACCOUNT
                            + "="+uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknow URI "+uri);
        }

        String orderBy = TextUtils.isEmpty(sortOrder) ? ContactTableMetaData.DEFAULT_SORT_ORDER : sortOrder;

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case INCOMING_CONTACT_COLLECTION_URI_INDICATOR:
                return ContactTableMetaData.CONTENT_TYPE;
            case INCOMING_SIGNLE_CONTACT_URI_INDICATOR:
                return ContactTableMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknow URI "+uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (sUriMatcher.match(uri) != INCOMING_CONTACT_COLLECTION_URI_INDICATOR) {
            throw new IllegalArgumentException("Unknow URI "+uri);
        }

        Log.d(TAG, "Enter insert");

        if (!values.containsKey(ContactTableMetaData.CONTACT_ACCOUNT)) {
            throw new IllegalArgumentException("Failed to insert row, account is needed " + uri);
        }

        if (!values.containsKey(ContactTableMetaData.CONTACT_NICKNAME)) {
            values.put(ContactTableMetaData.CONTACT_NICKNAME,
                    (String)values.get(ContactTableMetaData.CONTACT_ACCOUNT));
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(ContactTableMetaData.TABLE_NAME,
                ContactTableMetaData.CONTACT_ACCOUNT, values);
        if (rowId > 0) {
            Uri insertContactUri = ContentUris.withAppendedId(ContactTableMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertContactUri, null);

            Log.d(TAG, "insert uri=% " + insertContactUri.toString());
            return insertContactUri;
        }

        throw new SQLiteException("Failed to insert row int " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_CONTACT_COLLECTION_URI_INDICATOR:
                count = db.delete(ContactTableMetaData.TABLE_NAME, selection, selectionArgs);
                break;
            case INCOMING_SIGNLE_CONTACT_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(ContactTableMetaData.TABLE_NAME,
                        ContactTableMetaData._ID + "=" + rowId
                                + ((!TextUtils.isEmpty(selection)) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int count;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_CONTACT_COLLECTION_URI_INDICATOR:
                count = db.update(ContactTableMetaData.TABLE_NAME, values, selection, selectionArgs);
                break;
            case INCOMING_SIGNLE_CONTACT_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(ContactTableMetaData.TABLE_NAME,
                        values,
                        ContactTableMetaData._ID + "=" + rowId
                                + ((!TextUtils.isEmpty(selection)) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI "+uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
