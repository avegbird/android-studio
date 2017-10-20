package com.avegbird.barrage;

import java.util.concurrent.CopyOnWriteArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BarrageSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	public static final String TAG = "BarrageSurfaceView";
	public static final boolean D = true;
	
	private Context context = null;//上下文，用以获取全局变量或者系统服务
	private SurfaceHolder holder;//surfaceview 句柄，可间接控制surfaceview所有活动
	private int speed = 3;//弹幕在屏幕存在时间，控制着弹幕速度
    private MyDrawThread randerTherad = null;//绘图线程，扮演着导演角色，控制弹幕在屏幕的显示
	
	public BarrageSurfaceView(Context context) {
		super(context);
		this.context = context;
		holder = this.getHolder();//获取surfaceview 的控制句柄
		holder.addCallback(this);
	}

	public MyDrawThread getRanderTherad(){return randerTherad;}

	/**
	 * surfaceview surfaceview创建完成时调用，一般用来初始化操作
	 * @param holder:surfaceview句柄
	 * */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//如果绘图线程不为空，不作操作
        if (randerTherad != null)
            return;
		if (D) Log.e(TAG, "surface is created");
		randerTherad = new MyDrawThread(holder);
        randerTherad.setSurfaceMsg(this.getWidth(),this.getHeight(),speed);

        randerTherad.start();
	}

	/**
	 * surfaceview 界面发生更改时调用
	 * @param holder:surfaceview句柄
	 * @param format:
	 * @param width:surfaceview 宽度
	 * @param height:surfaceview 高度
	 * */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * surfaceview 销毁时调用，一般在此释放资源
	 * @param holder:surfaceview句柄
	 * */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
        if (randerTherad == null)
            return;
        randerTherad.softstop();
	}
}
