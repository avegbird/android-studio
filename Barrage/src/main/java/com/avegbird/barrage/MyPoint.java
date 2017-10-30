package com.avegbird.barrage;

/**
 * Created by wangjunfu on 2017/10/24.
 */

public class MyPoint {
//    默认-1为不可用
    private int head_x;//弹幕x位置
    private int head_y;//弹幕y位子
    private int line_x;//x方向行id
    private int line_y;//y方向列id

    public MyPoint(){
        head_x = head_y = line_x = line_y = -1;
    }

    public MyPoint(int head_x,int head_y,int line_x,int line_y){
        this.head_x = head_x;
        this.head_y = head_y;
        this.line_x = line_x;
        this.line_y = line_y;
    }

    public int getHead_x() {
        return head_x;
    }

    public void setHead_x(int head_x) {
        this.head_x = head_x;
    }

    public int getHead_y() {
        return head_y;
    }

    public void setHead_y(int head_y) {
        this.head_y = head_y;
    }

    public int getLine_x() {
        return line_x;
    }

    public void setLine_x(int line_x) {
        this.line_x = line_x;
    }

    public int getLine_y() {
        return line_y;
    }

    public void setLine_y(int line_y) {
        this.line_y = line_y;
    }
}
