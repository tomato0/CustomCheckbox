package design.wang.com.customcheckbox;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ThreeStatusCheckBox.OnThreeStatusCheckBoxChangeListener {

    private static final String TAG = "MainActivity";
    private ThreeStatusCheckBox viewById;
    private int selectStatus = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = (ThreeStatusCheckBox) findViewById(R.id.view_test);
        viewById.setOnClickListener(this);
        viewById.setCheckBoxListener(this);
    }

    @Override
    public void onClick(View view) {
        viewById.setChecked(selectStatus);
        switch (selectStatus){
            case 0:
                selectStatus = 2;
                break;
            case 1:
                selectStatus = 0;
                break;
            case 2:
                selectStatus = 0;
                break;
        }
    }

    @Override
    public void CheckBoxStatusChange(int checkStatus) {
        Log.d(TAG, "CheckBoxStatusChange: " + checkStatus+"");
    }
}
