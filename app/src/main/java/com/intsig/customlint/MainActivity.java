package com.intsig.customlint;

import android.os.Bundle;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;


/**
 * Log打印必须为工程中自定义的AppLog检查(这个因项目而异)
 * 检测new Message,提醒使用Message.Obtain()/handler.obtainMessage
 * newThread检查,推荐用封装好的线程池
 * layoutId命名检查
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setContentView(R.layout.act_main);
        Test.setContentView(R.layout.act_main); //测试是否在lint的检测范围

        new Thread().run();

        Log.d("1","222");

        new Message();
    }
}
