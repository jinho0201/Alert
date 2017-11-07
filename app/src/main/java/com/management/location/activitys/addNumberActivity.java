package com.management.location.activitys;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.management.location.R;

public class addNumberActivity extends Activity {
// 번호를 등록하는 엑티비티.


    EditText edit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_number);


        Button button = (Button) findViewById(R.id.button);
        edit = (EditText) findViewById(R.id.editText);





    }
}
