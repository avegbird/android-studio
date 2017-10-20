package com.avegbird.barrage;

/**
 * Created by wangjunfu on 2017/10/20.
 * 用于弹幕弹道管理
 */

public class RoadRules {
    private static final boolean D = true;
    private static final String TAG = "RoadRules";

    private int scr_width;//屏幕宽度
    private int scr_height;//屏幕高度

    private int text_height;//弹幕高度
    private int text_length;//弹幕长度

    //自身属性
    private float barrage_clearance_width = 0.07f;//同行弹幕间隔，屏幕宽度*barrage_clearance_width
    private float barrage_clearance_heigth = 0.07f;//相邻行弹幕间隔，屏幕高度*barrage_clearance_heigth


    public RoadRules(int width,int height){
        scr_height = height;
        scr_width = width;
    }
}
