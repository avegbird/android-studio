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
	
	private Context context = null;//�����ģ���ȡһЩȫ�ֱ�����ϵͳ����
	private SurfaceHolder holder;//���ƾ��
	private int speed = 3;//��Ļ������Ļʱ��
    private MyDrawThread randerTherad = null;
	
	public BarrageSurfaceView(Context context) {
		super(context);
		this.context = context;
		holder = this.getHolder();//��ȡ���ƾ��
		holder.addCallback(this);
	}

	public MyDrawThread getRanderTherad(){return randerTherad;}
	/**
	 * surfaceview ��������ʱ����ã���������һЩ��ʼ������
	 * @param holder:���ƾ��
	 * */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		//������Ⱦ�߳�
        if (randerTherad != null)
            return;
		if (D) Log.e(TAG, "surface is created");
		randerTherad = new MyDrawThread(holder);
        randerTherad.setSurfaceMsg(this.getWidth(),this.getHeight(),speed);

        randerTherad.start();
	}

	/**
	 * surfaceview ״̬�����ı�ʱ����ã�����ɻ�õ�ǰ��Ļ��ߵȲ���
	 * @param holder:���ƾ��
	 * @param format:���ظ�ʽ
	 * @param width:surfaceview ���
	 * @param height:surfaceview �߶�
	 * */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * surfaceview ������ʱ����ã����澭����һЩ�ͷ���Դ�Ĳ���
	 * @param holder:���ƾ��
	 * */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
        if (randerTherad == null)
            return;
        randerTherad.softstop();
	}
	
	/**
	 * ��Ҫ������
	 * �滭�߳�
	 * ���а����˻���������Ļ��Ⱦ�����滭����Ⱦ�Ȳ���
	 * */
	public class MyDrawThread extends Thread{
		private SurfaceHolder holder = null;//surfaceholderʵ������������surfaceview����
		private CopyOnWriteArrayList<TaxtTheme> taxtThemes = null;//����δ������ɺ�δ���Ƶ�taxt����Ļ��ʵ��
		private int MaxFrame = 60;//���ˢ��֡��

		private boolean is_run = true;//���ƴ˻�ͼ�߳��Ƿ���ֳ����˳�
		private int showType = 1;//��Ļ��Ⱦ����1��Ļ��������ʾ��2��Ļ������ʾ

		private int[][] is_clean = null; //������Ⱦ����Ϣ���پ�����N�еĵ�Ļ�Ƿ�ɼ�����Ⱦ������Ļ���ڱ�����Ⱦ�и߶�
		private int width = 0;//��Ļ���
		private int heigth = 0;//��Ļ�߶�
        private int speed = 3;//��Ļ�ٶ�
		/**
		 * ���췽��
		 * @param holder surfaceholderʵ������������surfaceview����
		 * @param taxtThemes ����δ������ɺ�δ���Ƶ�taxt����Ļ��ʵ��
		 * */
		public MyDrawThread(SurfaceHolder holder, CopyOnWriteArrayList<TaxtTheme> taxtThemes) {
			this.holder = holder;
			this.taxtThemes = taxtThemes;
		}
		
		public MyDrawThread(SurfaceHolder holder) {
			this(holder, null);
		}

		//��ȡ��Ļ��Ϣ��ȫ����Ϣ
		public void setSurfaceMsg(int width, int height, int speed){
			is_clean = new int[height/5][2];
			this.width = width;
			this.heigth = height;
            this.speed = speed;
			Log.e(TAG,"width="+width+" heigth="+height);
		}
		/**
		 * �̱߳���Ҫʵ�ֵķ���
		 * ��start()�������÷������˷����������ڶ����߳��У������������߳� 
		 * */
		@Override
		public void run() {
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setAntiAlias(true);
			long last_time = System.currentTimeMillis();
			if (D) Log.e(TAG,"draw thread is run");
			while(is_run) {
				long use_time = System.currentTimeMillis() - last_time;
				if (1000/MaxFrame - use_time > 0)//��֤���֡��
				try {
					Thread.sleep((1000/MaxFrame - use_time));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				last_time = System.currentTimeMillis();
				if (holder == null) {
					continue;//���holderΪ�գ���һֱ�ȴ�
				}
				if (taxtThemes == null || taxtThemes.size() < 1) {
					continue;//������е�Ļ�Ѿ�������ɣ��ȴ��µĵ�Ļ����
				}
				synchronized (holder) {
					Canvas lockCanvas = holder.lockCanvas();//��ȡsurfaceview canvas
					if (lockCanvas == null)
						break;
					lockCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//��ջ���
					draw_cache_Canvas(lockCanvas,paint,use_time);
					draw_per_frame(lockCanvas,paint,use_time);
					holder.unlockCanvasAndPost(lockCanvas);
				}
			}
		}
		/**
		 * ����֡�� ֻ����debugʱ�ŻῪ������
		 * @param canvas: ����
		 * @param paint: ����
		 * @param use_time:����һ֡���õ�ʱ��
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
		 * ���Ƶ�Ļ������
		 * */
		private void draw_cache_Canvas(Canvas canvas, Paint paint,long use_time) {
			synchronized (taxtThemes) {//��ֻ֤�д˷��������������е�Ļ
				for(TaxtTheme i : taxtThemes) {
					if (i.is_destroy){//�Ƿ�Ļ�Ѿ�ʧЧ
						taxtThemes.remove(i);
						i = null;
						continue;
					}
					//ѭ��ȡ�õ���taxt����
					Log.e(TAG,"alpha="+i.getTextApth()+" font="+i.getTextFont()+" color="+i.getTextColor());
					paint.setAlpha((int)(255*i.getTextApth()));//�򻭱�����͸����
					paint.setTextSize(i.getTextFont());//��������
					paint.setColor(i.getTextColor());//������ɫ
					Log.e(TAG,"text=" + i.getText() + " head_x=" + i.getHead_x() + " head_y=" + i.getHead_y());
					canvas.drawText(i.getText(),i.getHead_x(),i.getHead_y(),paint);//���Ƶ�Ļ
					i.goMove(width,heigth,speed,use_time);//��Ļ�ƶ�
				}
			}
			return;
		}
		/**
		 * ��ӵ�Ļ
		 * */
		public void setBarrage(String text) {
			if (taxtThemes == null)
				taxtThemes = new CopyOnWriteArrayList<TaxtTheme>();
			synchronized(taxtThemes){
				taxtThemes.add(new TaxtTheme(text));
			}
		}

		public void softstop(){is_run=false;}
	}
	/**
	 * ��Ļ��Ϣ��װģ��
	 * ע�⣡����һ�ֹ������򣬾���Ҫ����һ������
	 * */
	public class TaxtTheme {
		private boolean is_destroy = false;//�Ƿ����� false�����٣�true����
		public static final int ROLLING_NORMAL = 1;//�����������
		public static final int ROLLING_RIGHT2LIFT = 2;//�������ҹ���
		public static final int ROLLING_KEEPING = 0;//��������������ʾ

		private String Text = "";//��������
		private int TextFont = 70;//�����С ��СΪ5
		private int TextColor = Color.WHITE;//������ɫ
		private float TextApth = 255;//͸���� 1��͸����0͸��
		private int TextSpacing = 5;//���Ĭ�ϼ��Ϊ5dxp
		private int TextPosition = 0;//��Ļ��ʾλ�ã�0������Ļ��1��Ļ������֮һ��2��Ļ�м�����֮һ��3��Ļ������֮һ
		private final int TextStayTime = 3000;//��Ļ������ʽΪ0ʱ������Ļ����ʾ��ʱ��
		private long now_time = 0;//��¼��ǰʱ��
		private int RollingType = 1;//��Ļ������ʽ��1�����������������0��������2���������ҹ���
		private int TextPriority = 10;//��Ļ��ʾ���ȼ���Ĭ��ͬ�����ȼ��������£������ȼ�MAX=99�������棬������ȼ�0��������

		private int head_x = Integer.MAX_VALUE - 10000;//������Ļ��ǰͷXλ��
		private int head_y = 0;//������Ļ��ǰͷYλ��
		private int text_length = 0;//��Ļ����
		private int text_heigh = 0;//��Ļ�߶�
		
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
				case 0: //������
					if (now_time == 0)
						now_time = System.currentTimeMillis();
					if (System.currentTimeMillis() - now_time >= TextStayTime){
						toDestroy();
						break;
					}
					break;
				case 1: //���������������
					//�鿴�Ƿ�ɼ�
					Log.e(TAG,"text_length="+text_length+" text_heigh="+text_heigh+" head_x + text_length="+head_x + text_length);
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
				case 2: //�������ҹ���
					//�鿴�Ƿ�ɼ�
					if (head_x - text_length >= width){
						toDestroy();
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
//			�����Ժ����Ϊ�ظ�������׼��
		}
		public int getTextColor() {
			return TextColor;
		}
		public void setTextColor(int textColor) {
			TextColor = textColor;
		}
		public float getTextApth() {
			return TextApth;
		}
		public void setTextApth(float textApth) {
			TextApth = textApth;
		}
		public int getTextSpacing() {
			return TextSpacing;
		}
		public void setTextSpacing(int textSpacing) {
			TextSpacing = textSpacing;
		}
		public int getTextPosition() {
			return TextPosition;
		}
		public void setTextPosition(int textPosition) {
			TextPosition = textPosition;
		}
		public int getRollingType() {
			return RollingType;
		}
		public void setRollingType(int rollingType) {
			RollingType = rollingType;
			if (rollingType == 2)//���������ƶ�
				if (Text != null & !Text.isEmpty())
					Text = new StringBuffer(Text).reverse().toString();//���ַ�������
		}
		public int getTextPriority() {
			return TextPriority;
		}
		public void setTextPriority(int textPriority) {
			TextPriority = textPriority;
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
		public String getText() {
			return Text;
		}
		public void setText(String text) {
			this.Text = text;
		}
		/**
		 * ��ȡtext�ĳ��Ⱥ͸߶�
		 * */
		private void getTextSize(){
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setTextSize(TextFont);
			text_length = (int) paint.measureText(Text);//��ȡ�ܳ�
			Paint.FontMetrics fontMetrics = paint.getFontMetrics();
			text_heigh = (int) (fontMetrics.ascent + fontMetrics.leading + fontMetrics.descent);//��ȡ����߶�
		}
		
	}
	
}
