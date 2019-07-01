package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static ArrayList<String> registered = new ArrayList<String>();
    private final static int MSG_RESULT = 1234;
    private final FetchHandler handler = new FetchHandler(this);

    private Button button_dict;
    private TextView content_text;
    private EditText input_url;
    private Button button_fetch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_dict = findViewById(R.id.button_dict);
        button_dict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DictActivity.class);
                intent.putExtra("registered", registered);
                registered = new ArrayList<String>();
                startActivity(intent);
            }
        });

        content_text = findViewById(R.id.content_text);
        content_text.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_edittext, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_weblio) {
                    TextView textView1 = findViewById(R.id.content_text);
                    int selectionStart = textView1.getSelectionStart();
                    int selectionEnd = textView1.getSelectionEnd();
                    CharSequence selectedText = textView1.getText().subSequence(selectionStart, selectionEnd);
                    Uri uri = Uri.parse("https://ejje.weblio.jp/content/" + selectedText.toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    mode.finish();
                    return true;
                }
                if (item.getItemId() == R.id.action_register) {
                    TextView textView1 = findViewById(R.id.content_text);
                    int selectionStart = textView1.getSelectionStart();
                    int selectionEnd = textView1.getSelectionEnd();
                    CharSequence selectedText = textView1.getText().subSequence(selectionStart, selectionEnd);
                    registered.add(selectedText.toString());
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(TAG, "onDestroyActionMode");
            }
        });

        input_url = findViewById(R.id.input_url);
        button_fetch = findViewById(R.id.button_fetch);
        button_fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = input_url.getText().toString().trim();
                if (!url.isEmpty()) {
                    new FetchThread(handler, url).start();
                }
            }
        });

    }

    private static class FetchHandler extends Handler {
        private WeakReference<MainActivity> activityRef;
        FetchHandler(MainActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (msg.what == MSG_RESULT) {
                activity.showResult((String) msg.obj);
            }
        }
    }

    private void showResult(String s) {
        content_text.setText(s);
    }

    private static class FetchThread extends Thread {
        private final FetchHandler handler;
        private final String url;

        FetchThread(FetchHandler handler, String url) {
            this.handler = handler;
            this.url = url;
        }

        @Override
        public void run() {
            HtmlFetcher fetcher = new HtmlFetcher();
            JResult res = null;
            try {
                res = fetcher.fetchAndExtract(url, 10000, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            assert res != null;
            String text = res.getText();
            handler.sendMessage(handler.obtainMessage(MSG_RESULT, text));
        }
    }
}
