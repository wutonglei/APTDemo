package com.example.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.annotation.BindRb;
import com.example.aptdemo.radiobutton.MainActivity_RG_FastRb;
import com.example.aptdemo.radiobutton.RBbind;
import com.example.aptdemo.radiobutton.RadioButtonListener;

/**
 * 最终任务 手写  ButterKnife
 * 和自己想的RadioButton
 * 1.解析xml文件  （先不做）
 * 2.  声明的参数  group   id
 * 3.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Deprecated
    @BindRb(groupId = R.id.rg, viewId = R.id.rb_1)
    public RadioButton button1;
    @BindRb(groupId = R.id.rg, viewId = R.id.rb_1)
    public RadioButton button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new RBbind(this);
//        MainActivityRG2131230923FastRb mainActivityRG2131230923FastRb=new MainActivityRG2131230923FastRb();
//        mainActivityRG2131230923FastRb.bind(this);
////        mainActivityRG2131230923FastRb.setRgbutton1Listener(new );
//        mainActivityRG2131230923FastRb.setSelectRgbutton1Listener(new RadioButtonSelectedListener() {
//            @Override
//            public void selected() {
//                Toast.makeText(MainActivity.this, "被选中了1", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//           mainActivityRG2131230923FastRb.setSelectRgbutton2Listener(new RadioButtonSelectedListener() {
//            @Override
//            public void selected() {
//                Toast.makeText(MainActivity.this, "被选中了2", Toast.LENGTH_SHORT).show();
//            }
//        });


//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        });
//        MainActivity_RG_FastRb.mainActivityRGFastRb.bind(this);
//
//        MainActivity_RG_FastRb.mainActivityRGFastRb.set_rg_radioButton1Listener(new RadioButtonListener() {
//            @Override
//            public void selected(View v) {
//                Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
//            }
//        });


    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this, "被点击了", Toast.LENGTH_SHORT).show();
    }
}