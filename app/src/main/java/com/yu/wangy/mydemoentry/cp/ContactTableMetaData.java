package com.yu.wangy.mydemoentry.cp;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by wangyu on 15-7-1.
 */
public class ContactTableMetaData implements BaseColumns {
    private ContactTableMetaData() {}

    public static final String TABLE_NAME = "mycontacts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + ContactProviderMetaData.AUTHORITY + "/" + TABLE_NAME);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.tkcontact.contact";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.tkcontact.contact";



    public static final String CONTACT_ACCOUNT = "account";
    public static final String CONTACT_NICKNAME = "nickname";

    public static final String DEFAULT_SORT_ORDER = CONTACT_NICKNAME + " ASC";
}