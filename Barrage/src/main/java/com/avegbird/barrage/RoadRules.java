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

//    private int text_height;//弹幕高度
//    private int text_length;//弹幕长度

    //自身属性
    private float barrage_clearance_width = 0.04f;//同行弹幕间隔，屏幕宽度*barrage_clearance_width
    private float barrage_clearance_heigth = 0.04f;//相邻行弹幕间隔，屏幕高度*barrage_clearance_heigth

    //横向弹幕控制行 [][0]弹幕上位置，[][1]弹幕下位置 如果[][0]==[][1]==0 该行可以继续添加弹幕
    private int[][] transverse_Ballistic;
    //纵向弹幕控制列
    private int[] portrait_Ballistic;


    //构造函数，必须需要屏幕数据
    public RoadRules(int width, int height) {
        scr_height = height;
        scr_width = width;
        //初始化弹道参数
        transverse_Ballistic = new int[(int) (1/barrage_clearance_width)][2];
        portrait_Ballistic = new int[(int) (1/barrage_clearance_heigth)];
    }
    //返回可用head_y高度，并设置该行为占用 -1 没有可用行

    /**
     * 返回可用head_y高度,并设置该行高度
     * null 未找到合适行
     */
    public MyPoint auto_get_set(int text_height) {
        for (int i = 0; i < transverse_Ballistic.length; i++) {
            if (transverse_Ballistic[i][0] == 0 && transverse_Ballistic[i][0] == 0) {//该行未被占用
                //检测上下行高是否大于等于自身字体高度
                int[] spacing = check_transverse_height(i);
                if (spacing[0] < text_height)
                    continue;
                //占用此行
                int head_y = transverse_Ballistic[i][0] = (int) (spacing[1] + barrage_clearance_heigth * scr_height);
                transverse_Ballistic[i][1] = head_y + text_height;
                //返回弹幕行和y值
                return new MyPoint(0, head_y, i, -1);
            }
        }
        return null;
    }

    /**
     * 返回x行间隙和上止点高度
     */
    private int[] check_transverse_height(int x) {
        int after_line = x + 1;
        int before_line = x - 1;
        int bottom = scr_height;
        int top = 0;
        //寻找下面最近一行弹幕高度
        for (int i = after_line; i < transverse_Ballistic.length; i++) {
            if (transverse_Ballistic[i][0] != 0) {
                bottom = transverse_Ballistic[i][0];
                break;
            }
        }
        bottom = bottom > scr_height ? scr_height : bottom;
        //寻找上面最近一行弹幕高度
        for (int i = before_line; i >= 0; i--) {
            if (transverse_Ballistic[i][1] != 0) {
                top = transverse_Ballistic[i][1];
                before_line = i;
                break;
            }
        }
        top = top < 0 ? 0 : top;
        int[] a = new int[2];
        a[0] = bottom - top;
        a[1] = top;
        return a;
    }

    /**
     * 设置弹幕尾
     *
     * @param tail_x 弹幕尾x坐标 模式为ROLLING_KEEPING是 -1为销毁
     * @param xline  弹幕所属行
     * @param MODE   弹幕模式
     */
    public void set_xline_tail(int tail_x, int xline, int MODE, TaxtTheme taxtTheme) {
        if (xline < 0 || xline >= scr_height)//防止数组越界
            return;
        switch (MODE) {
            case TaxtTheme.ROLLING_KEEPING://保持在屏幕中间
                if (tail_x == -1) {
                    transverse_Ballistic[xline][0] = 0;
                    transverse_Ballistic[xline][1] = 0;
                    taxtTheme.setX_line(-1);
                }
                break;
            case TaxtTheme.ROLLING_NORMAL://右->左
                if (tail_x + barrage_clearance_width * scr_width <= scr_width)//此行可用
                {
                    transverse_Ballistic[xline][0] = 0;
                    transverse_Ballistic[xline][1] = 0;
                    taxtTheme.setX_line(-1);
                }
                break;
            case TaxtTheme.ROLLING_RIGHT2LIFT://左->右
                if (tail_x - barrage_clearance_width * scr_width >= 0)//此行可用
                {
                    transverse_Ballistic[xline][0] = 0;
                    transverse_Ballistic[xline][1] = 0;
                    taxtTheme.setX_line(-1);
                }
                break;
            default://额外处理措施
        }
    }

    /**
     * 设置弹幕尾
     *
     * @param tail_y 弹幕尾y坐标
     * @param yline  弹幕所属列
     * @param MODE   弹幕模式
     */
    public void set_yline_tail(int tail_y, int yline, int MODE) {
        if (tail_y < 0 || tail_y >= scr_width)//防止数组越界
            return;
        switch (MODE) {
        }
    }

    //横向是否不能添加弹幕，false 可以继续添加，true 不能继续添加弹幕
    public boolean x_is_full() {
        for (int i = 0; i < transverse_Ballistic.length; i++) {
            if (transverse_Ballistic[i][0] == 0) return false;
        }
        return true;
    }

    //返回一个横向可用位置 >0可用位置 <0 不可用
    public int x_can_use() {
        for (int i = 0; i < transverse_Ballistic.length; i++) {
            if (i == 0) return i;
        }
        return -1;
    }

    //纵向是否不能添加弹幕，false 可以继续添加，true 不能继续添加弹幕
    public boolean y_is_full() {
        for (int i : portrait_Ballistic) {
            if (i == 0) return false;
        }
        return true;
    }

    //返回一个纵向可用位置 >0可用位置 <0 不可用
    public int y_can_use() {
        for (int i = 0; i < portrait_Ballistic.length; i++) {
            if (i == 0) return i;
        }
        return -1;
    }
}
