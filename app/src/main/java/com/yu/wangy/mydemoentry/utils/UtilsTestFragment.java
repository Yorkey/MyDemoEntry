package com.yu.wangy.mydemoentry.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.yu.wangy.mydemoentry.R;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class UtilsTestFragment extends Fragment {

    private static final String TAG = "UtilsTestFragment";
    private static final int TAKE_PIC_CODE = 1238;

    private ListView testList;


    public UtilsTestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_utils_test, container, false);

        testList = (ListView)view.findViewById(R.id.testlv);
        String[] titles = new String[]{"test01", "test02"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, titles);
        testList.setAdapter(adapter);
        testList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) parent.getAdapter();
                Toast.makeText(getActivity(), adapter.getItem(position), Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, TAKE_PIC_CODE);
                        break;
                    case 1:
                        break;
                    case 3:
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PIC_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    return;
                }
                String sdState = Environment.getExternalStorageState();
                if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                    Log.i(TAG, "SD card is not avaiable/writable right now!");
                    return;
                }

                String picname = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()) + ".jpg";
                String dir = Environment.getExternalStorageDirectory() + "/myImg/";
                Log.d(TAG, "dir path=" + dir);
                File imgdir = new File(dir);
                if (!imgdir.exists())
                {
                    Log.d(TAG, "imgdir doesn't exists, mkdirs = " + imgdir.mkdirs());
                }

                String filename = dir + picname;
                Log.d(TAG, "take pic store path=" + filename);

                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap)bundle.get("data");
                ImageHelper.save(bitmap, filename);
                break;
        }
    }
}
