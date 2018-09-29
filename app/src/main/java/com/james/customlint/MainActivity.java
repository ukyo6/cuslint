package com.james.customlint;

import android.arch.core.executor.DefaultTaskExecutor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lintrules.detectors.NewThreadDetector;
import com.lintrules.detectors.SelfLogDetector;


/**
 * Actvity、Fragment布局文件名称前缀为actvity_、fragment_的检查
 * Log、System.out.println打印必须为工程中自定义的AppLog检查(这个因项目而异)
 * 检测new Message,提醒使用Message.Obtain()/handler.obtainMessage
 * xml文件中各种控件命名规范化，如Button前缀为btn，适合规范化编程
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("1","111");

        new Thread().run();
    }
}
