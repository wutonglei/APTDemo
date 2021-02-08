package com.example.aptdemo.radiobutton;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;

import com.example.annotation.BindRb;
import com.example.aptdemo.R;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RBbind {


    private static final String TAG = "RBbind";

    Map<Integer, Integer> groupIdMap = new HashMap<>();

    int groupID;

    public RBbind(Activity activity) {

        Log.i(TAG, "RBbind: " + activity.getClass().toString());
        String packName = activity.getClass().getPackage().getName();
        String activityName = activity.getClass().getSimpleName();
        Field[] fields = activity.getClass().getFields();

        for (Field field : fields) {

            if (!field.getType().toString().contains("android.widget.RadioButton"))
                continue;
            Log.i(TAG, "RBbind: field" + field.toString() + field.getType());
            field.setAccessible(true);

            Annotation[] annotations = field.getDeclaredAnnotations();
            Log.i(TAG, "RBbind: annotations:"+annotations.length);
            for (Annotation a :
                    annotations) {
                Log.i(TAG, "RBbind: annotation" + a.toString());
                if (a instanceof BindRb) {
                    BindRb bindRb = (BindRb) a;
                    groupIdMap.put(bindRb.groupId(), bindRb.groupId());
                    Log.i(TAG, "RBbind: " + bindRb.groupId());
                    groupID = bindRb.groupId();
                }
            }

        }


        String generateFileName = packName + "." + activityName + "RG" + groupID + "FastRb";
        Log.i(TAG, "RBbind: " + generateFileName);
        try {
            Class<?> aClass = Class.forName(generateFileName);
            Object o = aClass.newInstance();
//            如何获取类的方法
            Method bind = aClass.getDeclaredMethod("bind", activity.getClass());
            bind.setAccessible(true);
//            java.lang.NoSuchMethodException: bind [class android.app.Activity]
            bind.invoke(o,activity);
            Log.i(TAG, "RBbind: 成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        /**
         * MainActivityRG2131230923FastRb
         * 1.获取全部属性
         * 2.属性上有特定声明的
         * 3.分类
         * 4.绑定
         * */

    }

    public static View findViewById(Activity a, int id) {
        Log.i(TAG, "findViewById: 1");
        View.OnClickListener onClickListener = null;
        if (a instanceof View.OnClickListener) {
            onClickListener = (View.OnClickListener) a;
            Log.i(TAG, "findViewById: 2");
        }
        View view = a.findViewById(id);
        if (onClickListener == null) {
            Log.i(TAG, "findViewById: 3");
            System.out.println("onClickListener没有实现");
            return view;
        }
        view.setOnClickListener(onClickListener);
        return view;
    }
}
