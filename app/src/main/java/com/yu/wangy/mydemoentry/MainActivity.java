package com.yu.wangy.mydemoentry;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加入口步骤:
 * 1.activity中添加category com.yu.wangy.mydemoentry.DEMO_CODE
 * 2.activity中android:label设置为 相对路径/activity名称
 */
public class MainActivity extends AppCompatActivity {


    public static final String CATEGORY_PATH = "com.yu.wangy.mydemoentry.path";
    public static final String CATEGORY_DEMO = "com.yu.wangy.mydemoentry.DEMO_CODE";

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView)findViewById(R.id.main_listview);

        String listPath = getIntent().getStringExtra(CATEGORY_PATH);

        listview.setAdapter(new SimpleAdapter(this, getListData(listPath), android.R.layout.simple_list_item_1,
                new String[]{"title"}, new int[]{android.R.id.text1}));
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Map<String, Object> map = (Map<String, Object>) adapterView.getItemAtPosition(i);
                Intent intent = (Intent) map.get("intent");
                startActivity(intent);
            }
        });
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


    private List<Map<String,Object>> getListData(String listPath) {
        List<Map<String,Object>> listData
                = new ArrayList<Map<String, Object>>();
        PackageManager pm = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(CATEGORY_DEMO);
        List<ResolveInfo> listInfo = pm.queryIntentActivities(intent, 0);
        String[] listPathArray = (listPath==null ? null : listPath.split("/"));
        int index = (listPath==null ? 0 : listPathArray.length);

        Map<String, Boolean> listItemFlag = new HashMap<String, Boolean>();
        for(int i=0; i<listInfo.size(); ++i) {
            ResolveInfo info = listInfo.get(i);
            CharSequence lableSeq = info.loadLabel(pm);
            String infoLabel = (lableSeq!=null ? lableSeq.toString() : info.activityInfo.name);
            Log.d("MainActivity", "infoLabel:" + infoLabel);
            if(index==0 || infoLabel.startsWith(listPath)) {
                String[] labelPathArray = infoLabel.split("/");
                Log.d("MainActivity", "labelPathArray:"+labelPathArray.length);
                String listLabel = labelPathArray[index];
                if(index == labelPathArray.length-1) {
                    addItem(listData, listLabel,
                            activityIntent(info.activityInfo.applicationInfo.packageName,
                                    info.activityInfo.name));

                } else if(listItemFlag.get(listLabel) == null) {
                    listItemFlag.put(listLabel, Boolean.TRUE);
                    String nextPath = listPath==null ? listLabel : listPath+"/"+listLabel;
                    addItem(listData, listLabel,
                            browseIntent(nextPath));
                }
            }
        }

        return listData;
    }

    private Intent activityIntent(String pkg, String name) {
        Intent result = new Intent();
        result.setClassName(pkg, name);
        return result;
    }

    private Intent browseIntent(String listPath) {
        Intent result = new Intent();
        result.setClass(this, MainActivity.class);
        result.putExtra(CATEGORY_PATH, listPath);
        return result;
    }

    private void addItem(List<Map<String,Object>> mData, String label, Intent intent) {
        Map<String,Object> tmp = new HashMap<String, Object>();
        tmp.put("title", label);
        tmp.put("intent", intent);
        mData.add(tmp);
    }
}
