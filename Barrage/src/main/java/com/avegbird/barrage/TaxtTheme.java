package com.avegbird.barrage;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 * 弹幕信息封装模块
 * 注意！增加一种滚动方向，就需要增加一个画布
 * */
public class TaxtTheme {
    private static final String TAG= "TaxtTheme";

    private boolean is_destroy = false;//是否销毁 false不销毁，true销毁
    public static final int ROLLING_NORMAL = 1;//自右向左滚动
    public static final int ROLLING_RIGHT2LIFT = 2;//自左向右滚动
    public static final int ROLLING_KEEPING = 0;//不滚动，居中显示

    private String Text = "";//所发文字
    private int TextFont = 70;//字体大小 最小为5
    private int TextColor = Color.WHITE;//字体颜色
    private float TextApth = 255;//透明度 1不透明，0透明
    private int TextSpacing = 5;//间距默认间距为5dxp
    private int TextPosition = 0;//弹幕显示位置，0整个屏幕，1屏幕上三分之一，2屏幕中间三分之一，3屏幕下三分之一
    private final int TextStayTime = 3000;//弹幕滚动方式为0时，在屏幕上显示的时间
    private long now_time = 0;//记录当前时间
    private int RollingType = 1;//弹幕滚动方式，1正常自右向左滚动，0不滚动，2，自左往右滚动
    private int TextPriority = 10;//弹幕显示优先级，默认同等优先级先来在下，高优先级MAX=99在最上面，最低优先级0在最下面

    private int head_x = Integer.MAX_VALUE - 10000;//此条弹幕当前头X位置
    private int head_y = 0;//此条弹幕当前头Y位置
    private int text_length = 0;//弹幕长度
    private int text_heigh = 0;//弹幕高度

    public TaxtTheme(String text) {
        if (text == null){
            Text = "";
        }else {
            Text = text;
        }
        getTextSize();
        head_x = Integer.MAX_VALUE - 10000;
        head_y = text_heigh + 200;
    }
    public int getTextFont() {
        return TextFont;
    }
    public void setTextFont(int textFont) {
        if (textFont < 5)
            textFont = 5;
        TextFont = textFont;
        getTextSize();
    }
    public void goMove(int width,int heigth, int speed,long use_time){
        int move = (int) ((width+text_length)/(speed*1000.0/use_time));
        switch (RollingType){
            case 0: //不滚动
                if (now_time == 0)
                    now_time = System.currentTimeMillis();
                    head_x = (width - text_length)/2;
                if (System.currentTimeMillis() - now_time >= TextStayTime){
                    toDestroy();
                    break;
                }
                break;
            case 1: //正常自右向左滚动
                //查看是否可见
                if (head_x + text_length <= 0){
                    Log.e(TAG,"textTheme destroy");
                    toDestroy();
                    break;
                }
                if (head_x >= Integer.MAX_VALUE-15000){
                    head_x = width;
                    Log.e(TAG,"head_x set to width"+head_x);
                }
                head_x = head_x - (move>1?move:1);
                Log.e(TAG, "gomove="+head_x);
                break;
            case 2: //自左往右滚动
                //查看是否可见
                if (head_x >= Integer.MAX_VALUE-15000){
                    head_x = 0 - text_length;
                    Log.e(TAG,"head_x set to zero"+head_x);
                }
                if (head_x - text_length >= width){
                    toDestroy();
                    Log.e(TAG,"textTheme destroy");
                    break;
                }
                head_x = head_x + (move>1?move:1);
                break;
            default:

        }
    }
    public void toDestroy(){
        is_destroy = true;
        now_time = 0;
//			这里以后可以为重复利用做准备
    }
    public boolean is_destroy(){return is_destroy;}
    public int getTextColor() {
        return TextColor;
    }
    public TaxtTheme setTextColor(int textColor) {
        TextColor = textColor;
        return this;
    }
    public float getTextApth() {
        return TextApth;
    }
    public TaxtTheme setTextApth(float textApth) {
        TextApth = textApth;
        return this;
    }
    public int getTextSpacing() {
        return TextSpacing;
    }
    public TaxtTheme setTextSpacing(int textSpacing) {
        TextSpacing = textSpacing;
        return this;
    }
    public int getTextPosition() {
        return TextPosition;
    }
    public TaxtTheme setTextPosition(int textPosition) {
        TextPosition = textPosition;
        return this;
    }
    public int getRollingType() {
        return RollingType;
    }
    public TaxtTheme setRollingType(int rollingType) {
        RollingType = rollingType;
        if (rollingType == 2)//自左向右移动
            if (Text != null & !Text.isEmpty())
                Text = new StringBuffer(Text).reverse().toString();//将字符串倒叙
        return this;
    }
    public int getTextPriority() {
        return TextPriority;
    }
    public TaxtTheme setTextPriority(int textPriority) {
        TextPriority = textPriority;
        return this;
    }
    public int getHead_x() {
        return head_x;
    }
    public TaxtTheme setHead_x(int head_x) {
        this.head_x = head_x;
        return this;
    }
    public int getHead_y() {
        return head_y;
    }
    public TaxtTheme setHead_y(int head_y) {
        this.head_y = head_y;
        return this;
    }
    public String getText() {
        return Text;
    }
    public TaxtTheme setText(String text) {
        this.Text = text;
        return this;
    }
    /**
     * 获取text的长度和高度
     * */
    private void getTextSize(){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(TextFont);
        text_length = (int) paint.measureText(Text);//获取总长
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        text_heigh = (int) (fontMetrics.ascent + fontMetrics.leading + fontMetrics.descent);//获取字体高度
    }

}