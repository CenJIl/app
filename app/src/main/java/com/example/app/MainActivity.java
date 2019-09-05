package com.example.app;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.centerText)
    public TextView textView;

    public WebSocketResponse webSocketResponse;

    public Handler handler;

    Runnable runnable = () -> {
        try {
            Thread.sleep(500);
            switch (webSocketResponse.type) {
                case 1:
                    textView.setBackgroundColor(getColor(R.color.colorAccent));
                    break;
                case 2:
                    textView.setBackgroundColor(getColor(R.color.colorUser));
                    break;
                case 3:
                    textView.setBackgroundColor(getColor(R.color.colorPrimary));
                    break;
            }
            textView.setText(webSocketResponse.text);
        } catch (Exception e) {
            e.printStackTrace();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        handler = new Handler();


        fab.setOnClickListener(view -> {
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
            WebSocketClient webSocketClient = new WebSocketClient();
            webSocketClient.init("ws://192.168.206.116/websocket", this);

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
}
