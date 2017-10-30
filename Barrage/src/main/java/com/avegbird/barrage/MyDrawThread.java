package com.avegbird.barrage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 主要工作类
 * 绘画线程
 * 其中包含了画布处理，弹幕渲染，缓存画布渲染等操作
 * */
public class MyDrawThread extends Thread{
    private static final boolean D = true;
    private static final String TAG = "MyDrawThread";
    private SurfaceHolder holder = null;//surfaceholder实例，用来控制surfaceview绘制
    private CopyOnWriteArrayList<TaxtTheme> taxtThemes = null;//保存未绘制完成和未绘制的taxt（弹幕）实例
    private int MaxFrame = 60;//最大刷新帧率

    private boolean is_run = true;//控制此绘图线程是否存活，又称软退出
    private int showType = 1;//弹幕渲染规则，1弹幕不覆盖显示，2弹幕覆盖显示
    private RoadRules myrules;//控制弹幕行

    private int[][] is_clean = null; //保存渲染行信息，①决定第N行的弹幕是否可继续渲染其他弹幕；②保存渲染行高度
    private int width = 0;//屏幕宽度
    private int heigth = 0;//屏幕高度
    private int speed = 3;//弹幕速度
    /**
     * 构造方法
     * @param holder surfaceholder实例，用来控制surfaceview绘制
     * @param taxtThemes 保存未绘制完成和未绘制的taxt（弹幕）实例
     * */
    public MyDrawThread(SurfaceHolder holder, CopyOnWriteArrayList<TaxtTheme> taxtThemes) {
        this.holder = holder;
        this.taxtThemes = taxtThemes;
    }

    public MyDrawThread(SurfaceHolder holder) {
        this(holder, null);
    }

    //获取屏幕信息和全局信息
    public void setSurfaceMsg(int width, int height, int speed){
        is_clean = new int[height/5][2];
        this.width = width;
        this.heigth = height;
        this.speed = speed;
        Log.e(TAG,"width="+width+" heigth="+height);
        myrules = new RoadRules(width,height);
    }
    /**
     * 线程必须要实现的方法
     * 用start()来启动该方法，此方法将运行在独立线程中，不会阻塞主线程
     * */
    @Override
    public void run() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        long last_time = System.currentTimeMillis();
        if (D) Log.e(TAG,"draw thread is run");
        while(is_run) {
            long use_time = System.currentTimeMillis() - last_time;
            if (use_time > 100)
                Log.e(TAG,"used time="+use_time);
            if (1000/MaxFrame - use_time > 0)//保证最大帧率
                try {
                    Thread.sleep((1000/MaxFrame - use_time));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            last_time = System.currentTimeMillis();
            if (holder == null) {
                continue;//如果holder为空，则一直等待
            }
            if (taxtThemes == null || taxtThemes.size() < 1) {
                Log.e(TAG,"taxtThemes is null");
                continue;//如果所有弹幕已经绘制完成，等待新的弹幕进入
            }
            synchronized (holder) {
                Canvas lockCanvas = holder.lockCanvas();//获取surfaceview canvas
                if (lockCanvas == null)
                    continue;
                lockCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//清空画布
                draw_cache_Canvas(lockCanvas,paint,use_time);//绘制弹幕到画布
                draw_per_frame(lockCanvas,paint,use_time);//绘制帧率到画布
                holder.unlockCanvasAndPost(lockCanvas);//将绘制内容显示到屏幕上
            }
        }
        holder = null;
        taxtThemes = null;
    }
    /**
     * 绘制帧率 只有在debug时才会开启绘制
     * @param canvas: 画布
     * @param paint: 画笔
     * @param use_time:更新一帧所用的时间
     * */
    private void draw_per_frame(Canvas canvas, Paint paint, long use_time){
        if (!D) return;
        double l = 1000.0 / (use_time>=1?use_time:1);
        paint.setColor(Color.RED);
        paint.setAlpha(255);
        paint.setTextSize(75);
        canvas.drawText(String.format("%.2f", l),width-230,100,paint);
    }
    /**
     * 绘制弹幕到画布
     * */
    private void draw_cache_Canvas(Canvas canvas, Paint paint,long use_time) {
        synchronized (taxtThemes) {//保证只有此方法单独操作所有弹幕
            MyPoint point = null;
            //循环取得单个taxt对象
            for(TaxtTheme i : taxtThemes) {
                if (i.is_destroy()){//是否弹幕已经失效
                    taxtThemes.remove(i);
                    i = null;
                    continue;
                }
                paint.setAlpha((int)(255*i.getTextApth()));//向画笔设置透明度
                paint.setTextSize(i.getTextFont());//设置字体
                paint.setColor(i.getTextColor());//设置颜色
                long k = System.currentTimeMillis();
                if (i.need_to_init()){//如果弹幕需要被初始化，初始化其行高
                    //获取当前弹幕可以所在的行
                    point = myrules.auto_get_set(i.getText_heigh());
                    if (point != null){
                        //获取到了可以渲染的行
                        int head_y = point.getHead_y();
                        if (head_y > 0){
                            i.setHead_y(head_y);
                            i.setX_line(point.getLine_x());
                        }
                    }else{
                        Log.e(TAG,"there have no size to put barrage");
                        continue;
                    }
                }
                int tail = i.goMove(width,heigth,speed,use_time);//弹幕移动
                canvas.drawText(i.getText(),i.getHead_x(),i.getHead_y(),paint);//绘制弹幕
                //重置每行弹幕是否可以继续添加
                myrules.set_xline_tail(tail,i.getX_line(),i.getRollingType(),i);
                point = null;
            }
        }
        return;
    }
    /**
     * 添加弹幕
     * */
    public void setBarrage(String text) {
        if (taxtThemes == null)
            taxtThemes = new CopyOnWriteArrayList<TaxtTheme>();
        if (taxtThemes.size() > 100)//如果输入弹幕过于频繁，应当存在另一个地方，不参加循环
            return;
        synchronized(taxtThemes){
            taxtThemes.add(new TaxtTheme(text).setRollingType(2));
        }
    }

    public void softstop(){is_run=false;}
}
