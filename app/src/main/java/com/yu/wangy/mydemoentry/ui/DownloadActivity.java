package com.yu.wangy.mydemoentry.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yu.wangy.mydemoentry.R;
import com.yu.wangy.mydemoentry.event.DownloadEvent;
import com.yu.wangy.mydemoentry.service.DownloadService;

import de.greenrobot.event.EventBus;

public class DownloadActivity extends AppCompatActivity {

    protected Button startStopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        startStopBtn = (Button)findViewById(R.id.startStopBtn);
        startStopBtn.setText("Start");
        startStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(DownloadActivity.this, DownloadService.class));
                startStopBtn.setEnabled(false);
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download, menu);
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


    public void onEventMainThread(DownloadEvent event) {

        Toast.makeText(DownloadActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
        startStopBtn.setEnabled(true);
    }

}
