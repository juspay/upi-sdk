package com.example.sdk_demo_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SDKResponse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdkresponse);

        TextView tv = (TextView) findViewById(R.id.finalStatus);
        StringBuilder sb = new StringBuilder();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.getString(key);
                String row = String.format("\n%s : %s\n", key, value);
                sb.append(row);
            }
        }
        tv.setText(sb.toString());
    }
}
