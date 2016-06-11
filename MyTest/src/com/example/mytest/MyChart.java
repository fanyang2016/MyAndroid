package com.example.mytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyChart extends View{
    private String [] Xscale ;//X轴刻度值
    private String [] Yscale ;//Y轴刻度值
    private float [] Yvalue ;//具体到折线图中的纵坐标
    private float [] Y ;//后台传递过来的每个点对于的Y轴的值
    public   float [] Yclone;//克隆Y
    private int width =this.getResources().getDisplayMetrics().widthPixels;// 屏幕的宽度
    private int Xpx = width/8;//Y轴的单位间距
    private int Ypx = width/8;//Y轴的单位间距
    private int Xmin = width/8;//X轴最小值
    private int Ymin = width/16;//Y轴最小值
//    private int Xmax;
//    private int Ymax;
    private int lineColor = Color.BLACK ;//横竖交互的线的颜色
    private int textColor = Color.BLACK;//文字的颜色
    private int textRectColor = Color.WHITE;//文字的颜色
    private int pointColor = Color.RED;//点的颜色
    private int XYlineColor = Color.RED;//点点之间连接线的颜色
    private int areaColor   = Color.RED;//x轴y轴,还有连接线所圈定面积的颜色
    private  float[] xpts ;//存放每条横线的坐标数组
    private  float[] ypts;//存放每条纵线的坐标数组
    private  float[] pointpts;//存放点的坐标数组
    private  float[] p2pLinepts;//存放点点之间线段的坐标数组
    public MyChart(Context context){
    	this(context, null);
    }
	public MyChart(Context context,AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public MyChart(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	public void initData(String[] Xscale,float[] Y){
		Xpx = width/8;//重置一次。
		Yclone = new float[Y.length];
		Yclone = Y;
		this.Y = new float[Y.length];
		System.arraycopy(Y,0,this.Y,0,Y.length) ;
		this.Xscale = Xscale; 
		Yvalue = new float[this.Y.length];
		sort(this.Y, 0, this.Y.length-1);//给Y轴刻度排序,排序之后Y的顺序就是增序
		Yscale = getYscale(this.Y);
	  	 if(2 == Y.length ){
	  		Xpx = Xpx + 4*Xpx;
    	}else if(3 == Y.length){
    		Xpx = Xpx + Xpx;
    	}else if(4 == Y.length){
    		Xpx = Xpx + Xpx/2;
    	}else if(5 == Y.length){
    		Xpx = Xpx + Xpx/3;
    	}else if(6 == Y.length){
    		Xpx = Xpx + Xpx/5;
    	}else{
    		//如果处于 1 或者7 这两个端点，则不做处理。
    	}
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();//画笔
		canvas.scale(1, 1);
		canvas.drawColor(Color.WHITE);//设置画笔的颜色为白色
		DrawLine(canvas, paint, treatXline(), lineColor);//画横向的线
		DrawLine(canvas, paint, treatYline(), lineColor);//画纵向的线
		DrawText(canvas, paint, Xscale, Yscale, textColor);//画坐标轴上的值
		DrawPoint(canvas, paint,treatPoint(), pointColor);//画点
		Drawp2pLine(canvas, paint, treatp2pLine(),XYlineColor);//画点点之间的线
		DrawPath(canvas, paint,areaColor);//给指定的区域着色
		DrawRound(canvas,paint,XYlineColor);//画圆角矩形和圆角矩形中的文字
	}
	/**
	 * 画横纵交互的线,参数依次是,画布，画笔，线段的坐标数组，对应的颜色值
	 * @param canvas
	 * @param paint
	 * @param pts
	 * @param color
	 */
	public void DrawLine(Canvas canvas ,Paint paint,float [] pts,int color){
		paint.setColor(color);
		canvas.drawLines(pts, paint);
	}
	/**
	 * 画点点之间的线
	 * @param canvas
	 * @param paint
	 * @param pts
	 * @param color
	 */
	public void Drawp2pLine(Canvas canvas ,Paint paint,float [] pts,int color){
		paint.setColor(color);
		paint.setStrokeWidth(width/120);
		canvas.drawLines(pts, paint);
	}
	/**
	 * 画坐标的刻度值
	 * @param canvas
	 * @param paint
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public void DrawText(Canvas canvas ,Paint paint,String [] xvalue,String[] yvalue,int color){
		paint.setColor(color);
		paint.setTextSize(Xmin/4);
		//画X轴的文字
		for(int i=0;i<Xscale.length;i++){
			 canvas.drawText(xvalue[i], ypts[i*4+2]-paint.measureText(xvalue[i])/2, ypts[i*4+3]+Xmin/4, paint);
		}
		//画Y轴的文字
		for(int i=0;i<Yscale.length;i++){
			canvas.drawText(yvalue[i], xpts[i*4]-paint.measureText(yvalue[i])/4, xpts[i*4+1], paint);
		}
	}
	/**
	 * 画坐标中的点,其实就是画圆
	 * @param canvas
	 * @param paint
	 * @param pts
	 * @param color
	 */
	public void DrawPoint(Canvas canvas ,Paint paint ,float [] pts,int color){
		paint.setColor(color);
		paint.setAntiAlias(true);
		for(int i=0;i<pts.length;i=i+2){
			canvas.drawCircle(pts[i], pts[i+1], width/90, paint);
		}
	}
	/**
	 * 画圈定的区域背景
	 * @param canvas
	 * @param paint
	 * @param color
	 */
	public void DrawPath(Canvas canvas,Paint paint,int color){
		paint.setColor(color);
		paint.setAlpha(50);
		Path path = new Path();
		path.moveTo(ypts[2], ypts[3]);
		for(int i=0;i<pointpts.length;i=i+2){
			path.lineTo(pointpts[i], pointpts[i+1]);
		}
		path.lineTo(pointpts[pointpts.length-2], ypts[ypts.length-1]);
//		LinearGradient shader = new LinearGradient(0, 0, pointpts[pointpts.length-4], pointpts[pointpts.length-3], Color.WHITE, Color.RED, Shader.TileMode.MIRROR);
//		paint.setShader(shader);
		canvas.drawPath(path, paint);
	}
	/**
	 * 画圆角矩形和圆角矩形中的文字
	 * @param canvas
	 * @param paint
	 * @param color
	 */
	public void DrawRound(Canvas canvas,Paint paint,int color){
	     paint.setColor(color);
		 paint.setStyle(Paint.Style.FILL);//充满   
		 paint.setAntiAlias(true);// 设置画笔的锯齿效果   
		 //画圆角矩形中的文字,就是最有一个坐标的Y轴对应的值,取出来就OK
		 RectF oval3 = new RectF(pointpts[pointpts.length-2]-width/16, pointpts[pointpts.length-1]-Ypx*2/3, pointpts[pointpts.length-2]+width/16, pointpts[pointpts.length-1]);// 设置个新的长方形   
		 canvas.drawRoundRect(oval3, Xmin/4, Xmin/4, paint);//第二个参数是x半径，第三个参数是y半径 
		 //然后就是在椭圆中画一个文字，是最后一个点的纵坐标的值
		 paint.setColor(textRectColor);
		 //矩形中的数字要显示四位小数，保留五位，然后再把第五位去掉
		 canvas.drawText(String.format("%.5f", Yclone[Yclone.length-1]).substring(0,String.format("%.5f", Yclone[Yclone.length-1]).length()-1), oval3.centerX()-paint.measureText(String.format("%.5f", Yclone[Yclone.length-1]).substring(0, String.format("%.5f", Yclone[Yclone.length-1]).length()-1))/2, oval3.centerY()+9, paint);
	}
	/**
	 * 组织横向的线必须的数组，按顺序，从头开始，每四个元素代表一条线的坐标。
	 * @return
	 */
    public float [] treatXline(){
    	 xpts = new float[Yscale.length*4];
    	//给所有横向的线，起点添加横坐标
    	for(int i=0;i<xpts.length;i=i+4){
    		xpts[i] = Xmin/4;
    	}
    	//给所有横向的线，起点添加纵坐标
    	for(int i=1;i<xpts.length;i=i+4){
    		xpts[i] = Ymin + (i/4)*Ypx;
    	}
    	//给所有横向的线，终点添加横坐标
    	for(int i=2;i<xpts.length;i=i+4){
    		xpts[i] = Xmin + 13*width/16;
    	}
    	//给所有横向的线，终点添加纵坐标
    	for(int i=3;i<xpts.length;i=i+4){
    		xpts[i] = Ymin + ((i/4))*Ypx; 
    	}
    	return xpts;
    }
	/**
	 * 组织纵向的线必须的数组，按顺序，从头开始，每四个代表一条线的坐标。
	 * @return
	 */
    public float [] treatYline(){
        ypts = new float[Xscale.length*4];
    	//给所有纵向的线，起点添加横坐标
    	for(int i=0;i<ypts.length;i=i+4){
    		ypts[i] = Xmin*7/8 + (i/4)*Xpx;
    	}
    	//给所有纵向的线，起点添加纵坐标
    	for(int i=1;i<ypts.length;i=i+4){
    		ypts[i] = Ymin ;
    	}
    	//给所有纵向的线，终点添加横坐标
    	for(int i=2;i<ypts.length;i=i+4){
    		ypts[i] = Xmin*7/8 + (i/4)*Xpx;
    	}
    	//给所有纵向的线，终点添加纵坐标
    	for(int i=3;i<ypts.length;i=i+4){
    		ypts[i] = Ymin + 3*Ypx; 
    	}
    	return ypts;
    }
    /**
     * 组织点与点之间的坐标数组,按顺序,从头开始,两个一组
     * @return
     */
    public float [] treatPoint(){
    	pointpts = new float[Yvalue.length*2];
    	for(int i=0;i<pointpts.length;i=i+2){
    		pointpts[i] = ypts[2*i];
    	}
    	for(int i=1;i<pointpts.length;i=i+2){
    		pointpts[i] = Yvalue[i/2];
    	}
    	return pointpts;
    }
    /**
     * 组织点点之间的坐标数组,按顺序,从头开始,四个一组
     * 如果是7个点,那么就只有6条线段,但还是要用到7个点才能凑够6条线段的坐标
     * @return
     */
    public float [] treatp2pLine(){
    	p2pLinepts = new float[(pointpts.length/2-1)*4];
    	for(int i=0;i<p2pLinepts.length;i=i+4){
    		p2pLinepts[i] = pointpts[i/2];
    	}
    	for(int i=1;i<p2pLinepts.length;i=i+4){
    		p2pLinepts[i] = pointpts[i/2+1];
    	}
    	for(int i=2;i<p2pLinepts.length;i=i+4){
    		p2pLinepts[i] = pointpts[i/2+1];
    	}
    	for(int i=3;i<p2pLinepts.length;i=i+4){
    		p2pLinepts[i] = pointpts[i/2+2];
    	}
    	return p2pLinepts;
    }
	 /**
	  * 快速排序
	  * @param a
	  * @param left
	  * @param right
	  */
	 public  void sort(float[] a, int left, int right) {
			if (right > left) {
				int i = left;
				float p = a[left];
				for (int j = 1; j <= right - left; j++) {
					if (a[j + left] <= p) {
						float tmp_j = a[left + j];
						for (int k = left + j; k > i; k--) {
							a[k] = a[k - 1];
						}
						a[i] = tmp_j;
						i++;
					}
				}
				if (left + 1 < i) {
					sort(a, left, i - 1);
				}
				if (i < right - 1) {
					sort(a, i + 1, right);
				}
			}
		}
	 
	 public String [] getYscale(float [] y){
		 float minScale = y[0];
		 float maxScale = y[y.length-1];
		 float gap = maxScale - minScale;//最大的刻度减去最小的刻度，先得到这个值
		 float startScale;
		 float secondScale;
		 float thirdScale;
		 float endScale;
		 if(gap>=0.04){
			 startScale = maxScale - gap*3;//得到Y轴开始的刻度,也就是第一个刻度
			 endScale   = maxScale + gap/3;//得到Y轴结束的刻度,也就是第四个刻度
			  thirdScale  = endScale-(endScale - startScale)/3;//得到第三个刻度
			  secondScale = thirdScale-(endScale - startScale)/3;//得到第二个刻度 
			 //在得到刻度之后,开始绑定值
			 for(int i=0;i<Yclone.length;i++){
		      Yvalue[i] = Ymin +3*Ypx -((Yclone[i]-startScale)*3/(endScale-startScale))*Ypx;
			 }
		 }else {
			 gap = minScale;
			 startScale = maxScale - gap /3;
			 endScale = maxScale +gap/3;
			  thirdScale  = endScale-(endScale - startScale)/3;//得到第三个刻度
			  secondScale = thirdScale-(endScale - startScale)/3;//得到第二个刻度 			
			 //在得到刻度之后,开始绑定值			 
			 for(int i=0;i<Yclone.length;i++){		     
				 Yvalue[i] = Ymin +3*Ypx -((Yclone[i]-startScale)*3/(endScale-startScale))*Ypx;			
		     }	
			 }
		  //返回值中倒序，这样画的时候就是从上往下，对应从大到小
		 return new String[]{String.format("%.4f", endScale).substring(0, String.format("%.4f", endScale).length()-1),
				             String.format("%.4f",thirdScale).substring(0, String.format("%.4f",thirdScale).length()-1),
				             String.format("%.4f",secondScale).substring(0, String.format("%.4f",secondScale).length()-1),
				             String.format("%.4f",startScale).substring(0, String.format("%.4f",startScale).length()-1)};
	 }
}
