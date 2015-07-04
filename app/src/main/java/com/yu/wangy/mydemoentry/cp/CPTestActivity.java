package com.yu.wangy.mydemoentry.cp;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.yu.wangy.mydemoentry.R;

public class CPTestActivity extends AppCompatActivity {

    private static final String TAG = "CPTestActivity";
    private ListView contactListView;
    private EditText editText;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cp_test);

        Cursor cursor = getContentResolver().query(ContactTableMetaData.CONTENT_URI,
                null, null, null, null);

       adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2, cursor, new String[]{ContactTableMetaData.CONTACT_NICKNAME, ContactTableMetaData.CONTACT_ACCOUNT},
                new int[]{android.R.id.text1, android.R.id.text2}, 0);

        contactListView = (ListView)findViewById(R.id.contact_list);
        contactListView.setAdapter(adapter);


        editText = (EditText)findViewById(R.id.et_content);

        Button insertBtn = (Button)findViewById(R.id.btn_add);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Editable editable = editText.getText();
                if (editable == null) {
                    Toast.makeText(CPTestActivity.this, "输入插入数据，以空格分割", Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = editable.toString();
                String[] valuesList = content.split(" ");
                if (valuesList.length < 2) {
                    Toast.makeText(CPTestActivity.this, "输入插入数据，以空格分割", Toast.LENGTH_SHORT).show();
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(ContactTableMetaData.CONTACT_ACCOUNT, valuesList[0]);
                values.put(ContactTableMetaData.CONTACT_NICKNAME, valuesList[1]);
                getContentResolver().insert(ContactTableMetaData.CONTENT_URI, values);
                refreshList();
            }
        });

        Button viewBtn = (Button)findViewById(R.id.btn_view);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = editText.getText();
                if (editable == null) {
                    return;
                }
                String nikename = editable.toString();
                if (nikename != null) {
                    Cursor c = getContentResolver().query(ContactTableMetaData.CONTENT_URI, null,
                            ContactTableMetaData.CONTACT_NICKNAME + "=?", new String[]{nikename}, null);

                    editText.setText("");
                    for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                        String name = c.getString(c.getColumnIndex(ContactTableMetaData.CONTACT_NICKNAME));
                        String account = c.getString(c.getColumnIndex(ContactTableMetaData.CONTACT_ACCOUNT));
                        String content = String.format("%s(%s)", name, account);
                        editText.append(content);
                        editText.append("\n");
                    }
                    c.close();
                    refreshList();
                }
            }
        });

        Button updateBtn = (Button)findViewById(R.id.btn_update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = editText.getText();
                if (editable == null) {
                    return;
                }
                String content = editable.toString();
                String[] valuesList;
                if (content == null || (valuesList = content.split(" ")).length < 3) {
                    Toast.makeText(CPTestActivity.this, "输入条件 更新数据，以空格分割", Toast.LENGTH_SHORT).show();
                    return;
                }

                ContentValues values = new ContentValues();
                values.put(ContactTableMetaData.CONTACT_ACCOUNT, valuesList[1]);
                values.put(ContactTableMetaData.CONTACT_NICKNAME, valuesList[2]);

                int count = getContentResolver().update(ContactTableMetaData.CONTENT_URI, values,
                        ContactTableMetaData.CONTACT_NICKNAME + "=?", new String[]{valuesList[0]});
                refreshList();
                Toast.makeText(CPTestActivity.this, "更新了" + count + "条数据", Toast.LENGTH_SHORT).show();
            }
        });

        Button deleteBtn = (Button)findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = editText.getText();
                if (editable == null) {
                    return;
                }
                String nikename = editable.toString();
                if (nikename != null) {
                    int count = getContentResolver().delete(ContactTableMetaData.CONTENT_URI,
                            ContactTableMetaData.CONTACT_NICKNAME + "=?", new String[]{nikename});
                    refreshList();
                    Toast.makeText(CPTestActivity.this, "删除了" + count + "条数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void refreshList() {
        Cursor newCursor = getContentResolver().query(ContactTableMetaData.CONTENT_URI,
                null, null, null, null);
        Cursor oldCursor = adapter.swapCursor(newCursor);
        if(oldCursor != null && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cp_test, menu);
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

    @Override
    protected void onDestroy() {
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
        super.onDestroy();
    }
}
