package com.example.alexey.usingcontentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayAdapter<String> adapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ((ListView) findViewById(R.id.list)).setAdapter(adapter);

        ((AutoCompleteTextView) findViewById(R.id.text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                ContentValues values = new ContentValues();
                values.put("uri_name", v.getText().toString());
                try {
                    getContentResolver().insert(Uri.parse("content://com.example.alexey.mylauncher/uri/insert"), values);
                    v.setText("");
                } catch (SecurityException e) {
                    if (toast != null)
                        toast.cancel();
                    toast = Toast.makeText(MainActivity.this, "Permission Denial", Toast.LENGTH_SHORT);
                    toast.show();
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.last_records:
                    updateList(getContentResolver().query(Uri.parse("content://com.example.alexey.mylauncher/uri/last"),
                            null, null, null, null));
                    break;
                case R.id.all_records:
                    updateList(getContentResolver().query(Uri.parse("content://com.example.alexey.mylauncher/uri"),
                            null, null, null, null));
                    break;
                case R.id.today_records:
                    updateList(getContentResolver().query(Uri.parse("content://com.example.alexey.mylauncher/uri/today"),
                            null, null, null, null));
                    break;
            }
        } catch (SecurityException e) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(MainActivity.this, "Permission Denial", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void updateList(Cursor cursor) {
        adapter.clear();
        if (cursor == null) {
            adapter.add("empty");
            adapter.notifyDataSetChanged();
            return;
        }
        while (cursor.moveToNext()) {
            adapter.add(cursor.getString(cursor.getColumnIndex("uri_name")));
        }
        adapter.notifyDataSetChanged();
        cursor.close();
    }
}
