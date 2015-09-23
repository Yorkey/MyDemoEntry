package com.yu.wangy.mydemoentry.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yu.wangy.mydemoentry.R;

/**
 * Created by wangyu on 2015/9/22.
 */
public class ColorOptionsView extends LinearLayout {

    private String titleText;
    private int valueColor;

    public ColorOptionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Options, 0, 0);
        titleText = a.getString(R.styleable.Options_titleText);
        valueColor = a.getColor(R.styleable.Options_valueColor, getResources().getColor(android.R.color.holo_blue_light));
        a.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_color_options, this, true);

        LinearLayout linearLayout = (LinearLayout)getChildAt(0);

        TextView title = (TextView)linearLayout.getChildAt(0);
        title.setText(titleText);

        View colorView = linearLayout.getChildAt(1);
        colorView.setBackgroundColor(valueColor);
    }


    public ColorOptionsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorOptionsView(Context context) {
        this(context, null, 0);
    }

}
