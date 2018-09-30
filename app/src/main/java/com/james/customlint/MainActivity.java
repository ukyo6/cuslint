package com.james.customlint;

import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


/**
 * Log、System.out.println打印必须为工程中自定义的AppLog检查(这个因项目而异)
 * 检测new Message,提醒使用Message.Obtain()/handler.obtainMessage
 * newThread检查,推荐用封装好的线程池
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this,"11",Toast.LENGTH_SHORT);

        new Thread().run();

        Log.d("1","222");

        new Message();
    }
}
