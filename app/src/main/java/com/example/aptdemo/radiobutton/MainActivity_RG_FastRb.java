package com.example.aptdemo.radiobutton;

import android.app.Activity;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 最佳的方式还是扩展
 */
public class MainActivity_RG_FastRb {
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    int rb1;
    int rb2;
    int rg;
    RadioButtonListener radioButtonListener1;
    RadioButtonListener radioButtonListener2;
    public static MainActivity_RG_FastRb mainActivityRGFastRb = new MainActivity_RG_FastRb();

    private MainActivity_RG_FastRb() {

    }

    public void bind(Activity activity) {
        radioButton1 = activity.findViewById(rb1);
        radioButton2 = activity.findViewById(rb2);
        radioGroup = activity.findViewById(rg);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rb1 && radioButtonListener1 != null) {
                    radioButtonListener1.selected(radioButton1);
                }
                if (checkedId == rb2 && radioButtonListener2 != null) {
                    radioButtonListener1.selected(radioButton2);
                }


            }
        });
    }

    public void set_rg_radioButton1Listener(RadioButtonListener radioButtonListener) {
        radioButtonListener1 = radioButtonListener;
    }

    public void set_rg_radioButton2Listener(RadioButtonListener radioButtonListener) {
        radioButtonListener2 = radioButtonListener;
    }


}
