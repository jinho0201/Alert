package com.management.location.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.management.location.R;

public class FunctionActivity extends Activity {
    //기능 등록 페이지.

    String bell;
    String wifi;
    RadioGroup group1, group2;

    RadioButton set1,set2 ,set3, wset1, wset2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        set1 = (RadioButton) findViewById(R.id.set1);
        set2 = (RadioButton) findViewById(R.id.set2);
        set3 = (RadioButton) findViewById(R.id.set3);
        wset1 = (RadioButton) findViewById(R.id.wset1);
        wset2 = (RadioButton) findViewById(R.id.wset2);


        group1 = (RadioGroup) findViewById(R.id.group1);
        group2 = (RadioGroup) findViewById(R.id.group2);

        if(getIntent().hasExtra("bell") && getIntent().hasExtra("wifi")){
            //설정 값이 존재하면.

            if(getIntent().getStringExtra("bell").equals("SILENT")){

                group1.check(set1.getId());


            }else if(getIntent().getStringExtra("bell").equals("VIBRATE")){

                group1.check(set2.getId());

            }else if(getIntent().getStringExtra("bell").equals("NORMAL")){

                group1.check(set3.getId());

            }

            if(getIntent().getStringExtra("wifi").equals("ON")){

                group2.check(wset1.getId());


            }else if(getIntent().getStringExtra("wifi").equals("OFF")){

                group2.check(wset2.getId());

            }



        }


        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //라디오 버튼 선택
                switch (group1.getCheckedRadioButtonId()) {

                    case R.id.set1:

                        bell = "SILENT";

                        break;

                    case R.id.set2:

                        bell = "VIBRATE";

                        break;

                    case R.id.set3:

                        bell = "NORMAL";

                        break;

                    default:

                        break;

                }

                switch (group2.getCheckedRadioButtonId()) {

                    case R.id.wset1:

                        wifi = "ON";

                        break;

                    case R.id.wset2:

                        wifi = "OFF";

                        break;

                    default:

                        break;

                }

                if(bell ==null){

                    bell = "NOT";

                }

                if(wifi == null){

                    wifi = "NOT";

                }




                Intent intent = new Intent();
                intent.putExtra("bell" , bell);
                intent.putExtra("wifi" , wifi);

                backData(intent);



            }
        });
    }


    public void resetButton(View v) {
        group1.clearCheck();
        group2.clearCheck();
}

    private void backData(Intent intent){

        this.setResult(RESULT_OK,intent);
        finish();

    }
}
