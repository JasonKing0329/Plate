package com.king.app.plate.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/1/22 14:03
 */
public class DrawableUtil {

    public static void setRippleBackground(View view, int colorContent, int colorRipple) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(colorContent);
        // param1:ripple color, param2:background of normal status, param3:the limit of ripple
        RippleDrawable drawable = new RippleDrawable(
                ColorStateList.valueOf(colorRipple), gd, new ShapeDrawable(
                        new RectShape()
                )
        );
        view.setBackground(drawable);
    }

    public static void setGradientColor(View view, int color) {
        try {
            GradientDrawable drawable = (GradientDrawable) view.getBackground();
            drawable.setColor(color);
            view.setBackground(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
