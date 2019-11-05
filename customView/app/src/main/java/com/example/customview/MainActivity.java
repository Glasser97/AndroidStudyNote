package com.example.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RunText runText = (RunText) findViewById(R.id.run_text);
        runText.setRunTextString("这是一个文字跑马灯控件，当文字的长度超过宽度的时候，就会开始跑动，可以设置是否循环，速度，滚动方向，文字颜色，背景颜色等等。并且设定了一个滚动完毕的回调，便于滚动完毕后通知环境");
        runText.startScroll();


    }
}
