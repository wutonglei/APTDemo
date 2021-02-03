package com.example.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.annotation.BindRb;
import com.example.aptdemo.radiobutton.MainActivity_RG_FastRb;
import com.example.aptdemo.radiobutton.RadioButtonListener;

/**
 * 最终任务 手写  ButterKnife
 * 和自己想的RadioButton
 * 1.解析xml文件  （先不做）
 * 2.  声明的参数  group   id
 * 3.
 *
 *
 */
public class MainActivity extends AppCompatActivity {


    @BindRb(groupId=R.id.rg,viewId = R.id.rb_1)
   public RadioButton button1;
    @BindRb(groupId=R.id.rg,viewId = R.id.rb_2)
    RadioButton button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity_RG_FastRb.mainActivityRGFastRb.bind(this);

        MainActivity_RG_FastRb.mainActivityRGFastRb.set_rg_radioButton1Listener(new RadioButtonListener() {
            @Override
            public void selected(View v) {
                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}