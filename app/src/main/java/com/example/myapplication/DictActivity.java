package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DictActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {
    private final static String TAG = DictActivity.class.getSimpleName();

    private ArrayAdapter<String> adapter;
    private final static ArrayList<String> vocab = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        ArrayList<String> registered;
        registered = (ArrayList<String>)getIntent().getSerializableExtra("registered");
        vocab.addAll(registered);
        ListView list_dict = findViewById(R.id.list_dict);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, vocab);
        list_dict.setAdapter(adapter);
        list_dict.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, final int position, long id) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        String[] alert_menu = {"Weblioで検索", "削除"};
        alert.setItems(alert_menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                if (idx == 0) {
                    Uri uri = Uri.parse("https://ejje.weblio.jp/content/" + vocab.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                else if (idx == 1) {
                    adapter.remove(vocab.get(position));
                }
            }
        });
        alert.show();
    }

}
