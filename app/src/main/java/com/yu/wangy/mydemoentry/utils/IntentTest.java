package com.yu.wangy.mydemoentry.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yu.wangy.mydemoentry.R;

import java.io.FileNotFoundException;

public class IntentTest extends AppCompatActivity {

    private  static final String TAG = "IntentTest";

    private static final int CONTACT_SEL_CODE = 1234;
    private static final int PIC_SEL_CODE = 1235;
    private static final int RING_SEL_CODE = 1236;
    private static final int File_SEL_CODE = 1237;

    private ImageView selPicView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_test);

        selPicView = (ImageView)findViewById(R.id.iv_sel_pic);

        Button contactSelBtn = (Button)findViewById(R.id.btn_sel_contact);
        contactSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK);
                pickIntent.setData(ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(pickIntent, CONTACT_SEL_CODE);
            }
        });

        Button picSelBtn = (Button)findViewById(R.id.btn_sel_pic);
        picSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImgIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImgIntent.setType("image/*");
                startActivityForResult(pickImgIntent, PIC_SEL_CODE);
            }
        });

        Button ringSelBtn = (Button)findViewById(R.id.btn_sel_ringtone);
        ringSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickringIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickringIntent.setType("audio/*");
                startActivityForResult(pickringIntent, RING_SEL_CODE);
            }
        });

        Button fileSelBtn = (Button)findViewById(R.id.btn_sel_file);
        fileSelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickFileIntent.setType("message/*"); //mime类型，如audio,image,message,text,vedio,application
                startActivityForResult(pickFileIntent, File_SEL_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CONTACT_SEL_CODE:
                    Uri contactUri = data.getData();
                    String phoneNum = getPhoneNum(contactUri);
                    Toast.makeText(this, phoneNum, Toast.LENGTH_LONG).show();
                    break;
                case PIC_SEL_CODE:
                    Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
                    showSelectPic(data.getData());
                    break;
                case RING_SEL_CODE:
                    //Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
                    playSelectRing(data.getData());
                    break;
                case File_SEL_CODE:
                    Toast.makeText(this, data.getData().toString(), Toast.LENGTH_LONG).show();
                    break;
                default:
                    ;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intent_test, menu);
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


    private String getPhoneNum(Uri uri) {
        String phoneNum = "";
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {

            int phoneNumCnt = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

            if (phoneNumCnt > 0) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                        null,
                        null);

                if (phoneCursor.moveToFirst()) {
                    int numIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int type = phoneCursor.getInt(typeIndex);
                    if (type == 2) {
                        phoneNum = phoneCursor.getString(numIndex);
                    }
                }
                phoneCursor.close();


            }
        }
        cursor.close();

        return phoneNum;
    }

    void showSelectPic(Uri uri) {
        ContentResolver cr = getContentResolver();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            selPicView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            Toast.makeText(this, uri.toString() + "not fount!", Toast.LENGTH_SHORT).show();
        }
    }

    void playSelectRing(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            Toast.makeText(this, path, Toast.LENGTH_LONG).show();
            //SoundPool sp = new SoundPool(10, AudioManager.STREAM_SYSTEM,5);
            //int soundId = sp.load(path, 1);
            //Log.d(TAG, "soundId="+soundId);
            //sp.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }

        cursor.close();

    }
}
