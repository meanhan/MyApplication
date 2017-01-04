package com.meanhan.myapplication.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.meanhan.myapplication.R;
import com.meanhan.myapplication.utils.DisplayUtil;
import com.meanhan.myapplication.view.TouchBallModel;
import com.meanhan.myapplication.view.TouchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private TouchView mTouchView;
    private Button mButton;

    private List<TouchBallModel> initialDataList;
    private int ballWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mTouchView = (TouchView) findViewById(R.id.touchView);
        mButton = (Button) findViewById(R.id.button);
        ballWidth = DisplayUtil.dip2Pix(this, 13);
        initViewData();
       mTouchView.setThumbList(initialDataList);
    }

    private void initViewData(){
        if(initialDataList==null) {
            initialDataList = new ArrayList<TouchBallModel>();
            initialDataList.add(new TouchBallModel("21", "24", ballWidth));
            initialDataList.add(new TouchBallModel("16", "32", ballWidth));
            initialDataList.add(new TouchBallModel("16", "48", ballWidth));
            initialDataList.add(new TouchBallModel("16", "56", ballWidth));
            initialDataList.add(new TouchBallModel("21", "72", ballWidth));
            initialDataList.add(new TouchBallModel("16", "88", ballWidth));
        }
    }
}
