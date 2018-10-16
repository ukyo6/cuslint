package com.james.customlint;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Log打印必须为工程中自定义的AppLog检查(这个因项目而异)
 * 检测new Message,提醒使用Message.Obtain()/handler.obtainMessage
 * newThread检查,推荐用封装好的线程池
 * layoutId命名检查
 */
public class MainActivity extends AppCompatActivity {

    TextView tv_hana;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setContentView(R.layout.act_main);
        Test.setContentView(R.layout.act_main); //测试是否在lint的检测范围

        tv_hana = new TextView(this);

        Toast.makeText(this,"11",Toast.LENGTH_SHORT);

        new Thread().run();

        Log.d("1","222");

        new Message();
    }
}
