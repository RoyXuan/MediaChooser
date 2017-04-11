package com.vest.album;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> data = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.chooser_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaChooserActivity.startMediaChooser(MainActivity.this, data);
            }
        });
        findViewById(R.id.clear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.clear();
                adapter.clear();
                adapter.notifyDataSetChanged();
            }
        });
        ListView recyclerView = (ListView) findViewById(R.id.chooser_list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        recyclerView.setAdapter(adapter);
    }

    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MediaChooserConstants.ACTION_MEDIA);
        registerReceiver(mediaReceiver, intentFilter);
    }

    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MediaChooserConstants.ACTION_MEDIA.equals(action)) {
                Bundle bundle = intent.getExtras();
                data = bundle.getStringArrayList(MediaChooserConstants.RESULT_LIST);
                adapter.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mediaReceiver);
    }
}
