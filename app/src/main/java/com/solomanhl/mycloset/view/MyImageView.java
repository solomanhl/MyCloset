package com.solomanhl.mycloset.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.solomanhl.mycloset.utils.LogUtil;

public class MyImageView extends ImageView {
	/** 左上角的x初始坐标 **/
	private float preX;
	/** 左上角的y初始坐标 **/
	private float preY;
	/** 图片宽 **/
	private float mWidth;
	/** 图片高 **/
	private float mHeight;
	private Matrix mMatrix=new Matrix();
	private Matrix savedMatrix=new Matrix();
	private Matrix scaleMatrix=new Matrix();

	/** 优先级 **/
	private int piority;
	boolean isDrawBorder= false;
	boolean isInit = false;
	private int mood = 0;
	// 手指按下
	public static final int MOOD_ACTION_DOWN=1;
	// 副手指按下
	public static final int MOOD_ACTION_POINTERDOWN=2;
	// 副手指离开屏幕
	public static final int MOOD_ACTION_POINTERUP=3;
	// 手指离开屏幕
	public static final int MOOD_ACTION_UP=4;
	// 手指离在屏幕上滑动
	public static final int MOOD_ACTION_MOVE=5;

	/**
	 * 存储边框各个点的坐标,依次为左上、右上、右下、左下
	 */
	private float[] mFrame = new float[8];
	public MyImageView(Context context) {
		super(context);
		//设置ScaleType为ScaleType.MATRIX，这一步很重要
		this.setScaleType(ScaleType.MATRIX);
	}
	//将图片加灰色的边框
	private int color ;
	private boolean setColor = false;

	public void setColor(int color) {
		this.color = color;
	}


	public void setSetColor(boolean setColor) {
		this.setColor = setColor;
	}


	private String tag= this.getClass().getSimpleName();

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		LogUtil.i(tag, "onDraw() -- isDrawBorder:"+isDrawBorder);
		//根据MyImageView来获取bitmap对象
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd!=null&&!isInit)
		{
			isInit = true;
			this.mWidth = bd.getBitmap().getWidth();
			this.mHeight = bd.getBitmap().getHeight();
			setDefaultFrame(mWidth/2, mHeight/2);
		}
		dra(canvas);
	}


	public float getPreX() {
		return preX;
	}


	public void setPreX(float preX) {
		this.preX = preX;
	}


	public float getPreY() {
		return preY;
	}


	public void setPreY(float preY) {
		this.preY = preY;
	}


	public int getColor() {
		return color;
	}


	public float getmWidth() {
		//根据MyImageView来获取bitmap对象
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd!=null)
		{
			this.mWidth = bd.getBitmap().getWidth();
		}
		return mWidth;
	}


	public void setmWidth(float mWidth) {
		this.mWidth = mWidth;
	}


	public float getmHeight() {
		//根据MyImageView来获取bitmap对象
		BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
		if(bd!=null)
		{
			this.mHeight = bd.getBitmap().getHeight();
		}
		return mHeight;
	}


	public void setmHeight(float mHeight) {
		this.mHeight = mHeight;
	}


	public int getPiority() {
		return piority;
	}


	public void setPiority(int piority) {
		this.piority = piority;
	}

	/**
	 * 设置默认边框,基于中心点的未经旋转的边框
	 * @param desX 中心点距左右边界的距离
	 * @param desY 中心点距上下边界的距离
	 */
	public void setDefaultFrame(float desX, float desY) {
		mFrame[0] = this.preX - desX;
		mFrame[1] = this.preY - desY;
		mFrame[2] = this.preX + desX;
		mFrame[3] = this.preY - desY;
		mFrame[4] = this.preX + desX;
		mFrame[5] = this.preY + desY;
		mFrame[6] = this.preX - desX;
		mFrame[7] = this.preY + desY;
	}

	/**
	 * 移动边框
	 * @param offsetX X坐标移动的距离
	 * @param offsetY Y坐标移动的距离
	 */
	public void transFrame(float offsetX, float offsetY) {
		mFrame[0] += offsetX;
		mFrame[1] += offsetY;
		mFrame[2] += offsetX;
		mFrame[3] += offsetY;
		mFrame[4] += offsetX;
		mFrame[5] += offsetY;
		mFrame[6] += offsetX;
		mFrame[7] += offsetY;
	}

	public void scalFrame(float scale){
		rotateFrame(mWidth, mHeight);
	}

	/**
	 * 旋转边框,旋转前先先重置为正常未经旋转的边框,然后根据matrix的旋转值进行旋转
	 * x = x0*cosα + y0*sinα
	 * y = y0*cosα + x0*sinα
	 *
	 * 矩阵表示如下：
	 * x   cosα  -sinα  0     x0
	 * y = sinα   cosα  0     y0
	 * 1   0      0     1     1
	 */
	public void rotateFrame(float width,float height) {
		//设置未经旋转的边框各点坐标值
		setDefaultFrame(width / 2f, height / 2f);

		float[] temp = new float[mFrame.length];
		System.arraycopy(mFrame, 0, temp, 0, mFrame.length);
		//根据旋转后的matrix值,设置旋转后的边框各点坐标值
		float[] matrixArray = new float[9];
		this.mMatrix.getValues(matrixArray);
		mFrame[0] = temp[0]*matrixArray[0] + temp[1]*matrixArray[1];
		mFrame[1] = temp[1]*matrixArray[4] + temp[0]*matrixArray[3];
		mFrame[2] = temp[2]*matrixArray[0] + temp[3]*matrixArray[1];
		mFrame[3] = temp[3]*matrixArray[4] + temp[2]*matrixArray[3];
		mFrame[4] = temp[4]*matrixArray[0] + temp[5]*matrixArray[1];
		mFrame[5] = temp[5]*matrixArray[4] + temp[4]*matrixArray[3];
		mFrame[6] = temp[6]*matrixArray[0] + temp[7]*matrixArray[1];
		mFrame[7] = temp[7]*matrixArray[4] + temp[6]*matrixArray[3];
		//根据matrix的偏移值,将边框旋转后产生的偏移再重置回去
		if(matrixArray[2] > mFrame[0]) {
			float offsetX = matrixArray[2] - mFrame[0];
			float offsetY = mFrame[1] - matrixArray[5];
			mFrame[0] += offsetX;
			mFrame[1] -= offsetY;
			mFrame[2] += offsetX;
			mFrame[3] -= offsetY;
			mFrame[4] += offsetX;
			mFrame[5] -= offsetY;
			mFrame[6] += offsetX;
			mFrame[7] -= offsetY;
		} else {
			float offsetX = mFrame[0] - matrixArray[2];
			float offsetY = matrixArray[5] - mFrame[1];
			mFrame[0] -= offsetX;
			mFrame[1] += offsetY;
			mFrame[2] -= offsetX;
			mFrame[3] += offsetY;
			mFrame[4] -= offsetX;
			mFrame[5] += offsetY;
			mFrame[6] -= offsetX;
			mFrame[7] += offsetY;
		}

	}

	/**
	 * 根据传入的x、y判断是否在控件里边
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isOnView(float x,float y){
		Matrix inMatrix = new Matrix();
//		inMatrix.set(mMatrix);
		mMatrix.invert(inMatrix);
		float[] xy = new float[2];
		inMatrix.mapPoints(xy,new float[]{x,y});
		if(xy[0] > 0 && xy[0] < mWidth && xy[1] > 0 && xy[1] < mHeight)
		{
			LogUtil.i(tag, "isOnView() -- 在区域内...");
			return true;
		}else{
			LogUtil.i(tag, "isOnView() -- 不在区域内...");
		}
		return false;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();

		LogUtil.i(tag, "onAttachedToWindow() -- dw:"+getDrawable());
	}


	public float[] getmFrame() {
		return mFrame;
	}


	public Matrix getmMatrix() {
		return mMatrix;
	}


	public void setmMatrix(Matrix mMatrix) {
		this.mMatrix = mMatrix;
	}
	class Point {
		float x0, y0, x1, y1, x2, y2, x3, y3;
	}

	private void drawAl(Point point, Canvas canvas, int color, int alpha) {
		LogUtil.i(tag, "drawAl()");
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4);
		//if (alpha >= 0)
		//paint.setAlpha(alpha);
		Log.i(tag, "drawAl() -- 基本点：["+point.x0+","+point.y0+"]   "
				+ "右边点：["+point.x1+","+point.y1+"]  右下点：["+point.x2+","+point.y2+"]  "
				+ "左下点：["+point.x3+","+point.y3+"]");
		Path p = new Path();
		// 基本点
		p.moveTo(point.x0, point.y0);
		// 右边点
		p.lineTo(point.x1, point.y1);
		// 右边下角
		p.lineTo(point.x2, point.y2);
		// 左下角
		p.lineTo(point.x3, point.y3);
		p.close();
		canvas.drawPath(p, paint);
	}

	private void dra(Canvas canvas){
		LogUtil.i(tag, "dra()");
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
//		canvas.drawBitmap(canvasBitmap, 0, 0, null);
		Point point = new Point();
		point.x0 = mFrame[0];
		point.y0 = mFrame[1];
		point.x1 = mFrame[2];
		point.y1 = mFrame[3];
		point.x2 = mFrame[4];
		point.y2 = mFrame[5];
		point.x3 = mFrame[6];
		point.y3 = mFrame[7];
		int color = Color.parseColor("#f977a7");
		if(!isDrawBorder){
			color = Color.parseColor("#00000000");
		}
		drawAl(point, canvas, color, 0);
	}


	public void setDrawBorder(boolean isDrawBorder) {
		this.isDrawBorder = isDrawBorder;
	}


	public Matrix getSavedMatrix() {
		return savedMatrix;
	}


	public void setSavedMatrix(Matrix savedMatrix) {
		this.savedMatrix = savedMatrix;
	}


	public int getMood() {
		return mood;
	}


	public void setMood(int mood) {
		this.mood = mood;
	}


	public Matrix getScaleMatrix() {
		return scaleMatrix;
	}


	public void setScaleMatrix(Matrix scaleMatrix) {
		this.scaleMatrix = scaleMatrix;
	}



}
