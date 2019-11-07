package com.example.customview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView kanhao;
    TextView bukanhao;
    VoteButton voteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RunText runText = (RunText) findViewById(R.id.run_text);
        runText.setRunTextString("这是一个文字跑马灯控件，当文字的长度超过宽度的时候，就会开始跑动，可以设置是否循环，速度，滚动方向，文字颜色，背景颜色等等。并且设定了一个滚动完毕的回调，便于滚动完毕后通知环境");
        runText.startScroll();

        voteButton = (VoteButton) findViewById(R.id.voteButton);

        voteButton.setmLeftNo(5);
        voteButton.setmRightNo(3);
        voteButton.setmSlashUnderWidth(100);

        kanhao = (TextView) findViewById(R.id.kanhao);
        kanhao.setText(voteButton.getmLeftNo()+"");

        bukanhao = (TextView) findViewById(R.id.bukanhao);
        bukanhao.setText(voteButton.getmRightNo()+"");

        voteButton.setOnVoteClickListener(new VoteButton.VoteClickListener() {
            @Override
            public void onClickLeft() {
                int k = voteButton.getmLeftNo();
                kanhao.setText(voteButton.setmLeftNo(k+1)+"");
            }

            @Override
            public void onClickRight() {
                int k =voteButton.getmRightNo();
                bukanhao.setText(voteButton.setmRightNo(k+1)+"");
            }
        });
    }
}
