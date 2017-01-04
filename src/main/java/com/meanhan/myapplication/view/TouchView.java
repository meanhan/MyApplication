package com.meanhan.myapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.meanhan.myapplication.R;
import com.meanhan.myapplication.utils.DisplayUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TouchView extends View {

	private final String TAG = getClass().getSimpleName();

	private int width;
	private int height;

	//拖动球画笔
	private Paint thumbPaint;
	//文字 画笔
	private Paint textPaint;

	private Paint tempPaint;
	//温度显示大圆 宽度
	private  int TEMP_CIECLE_WIDTH = DisplayUtil.dip2Pix(getContext(), 24);
	//温度显示 与 拖动球之间距离
	private int TEMP_CIRCLE_SPACE = DisplayUtil.dip2Pix(getContext(), 48);
	private int textPaintSize = DisplayUtil.dip2Pix(getContext(), 11);
	// X 最大值 和最小值
	private int maxX ;
	private int minX ;
	// Y 最大值 和最小值
	private int maxY = 515;
	private int minY = 75;
	private Paint timePaint;
	private Bitmap timeBitmap;

	private boolean isTempShow = false;
	private boolean isTimeShow = false;

	private List<TouchBallModel> thumbList;

	private TouchBallModel touchBall;

	private int maxTemp = 32;
	private int minTemp = 10;
	private boolean isTempUnitC = true;


	public TouchView(Context context) {
		super(context);
		initDatas();
	}

	public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initDatas();
	}

	public TouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDatas();
	}

	public void setThumbList(List<TouchBallModel> mthumbList) {
		if(this.thumbList==null){
			this.thumbList=new ArrayList<TouchBallModel>();
		}
		this.thumbList.clear();
		if(mthumbList!=null && !mthumbList.isEmpty()){
			for(TouchBallModel pball:mthumbList){
				TouchBallModel pbCopy=null;
				try {
					pbCopy = (TouchBallModel)pball.clone();
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				if(pbCopy!=null){
					this.thumbList.add(pbCopy);
				}
			}

		}
		setXbyTime(thumbList);
		setYbyTemp(thumbList);
		setTimeValue(thumbList);
		postInvalidate();

	}

	public List<TouchBallModel> getThumbList() {
		return thumbList;
	}

	public void setMaxTemp(int maxTemp) {
		this.maxTemp = maxTemp;
	}

	public void setMinTemp(int minTemp) {
		this.minTemp = minTemp;
	}

	private void initDatas(){
		thumbPaint = new Paint();
		thumbPaint.setAntiAlias(true);
		thumbPaint.setColor(Color.parseColor("#ec7016"));
		thumbPaint.setStrokeWidth(1);

		tempPaint = new Paint();
		tempPaint.setAntiAlias(true);
		tempPaint.setColor(Color.parseColor("#ec7016"));
		tempPaint.setStrokeWidth(1);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.parseColor("#FFFFFF"));
		textPaint.setTextSize(textPaintSize);
		textPaint.setTextAlign(Paint.Align.CENTER);

		timePaint = new Paint();
		timePaint.setAntiAlias(true);
		timePaint.setColor(Color.parseColor("#000000"));
		timePaint.setTextSize(textPaintSize);
		timePaint.setTextAlign(Paint.Align.CENTER);

		timeBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.thermost_program_frame);
	}

	private void initMaxXY(){
		minY = height/10;
		maxY = height - height/6;
		Log.i(TAG, "minY:"+minY+"-maxY:"+maxY);

		minX = width/26;
		maxX = width - width/26;
		Log.i(TAG, "minX:"+minX+"-maxX:"+maxX);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		setXbyTime(thumbList);
		setYbyTemp(thumbList);
		//setThumbTempF(thumbList);
		setTimeValue(thumbList);

		drawThumb(canvas);
		drawText(canvas);
		drawTime(canvas);

	}

	private void drawThumb(Canvas canvas){
		if(thumbList != null && !thumbList.isEmpty()){

			for (TouchBallModel ball : thumbList) {

				canvas.drawCircle(ball.x, ball.y, ball.width, thumbPaint);
			}
		}

		if(isTempShow){
			canvas.drawCircle(touchBall.x, touchBall.y-TEMP_CIRCLE_SPACE, TEMP_CIECLE_WIDTH, tempPaint);
		}

	}

	private void drawText(Canvas canvas){

		if(thumbList != null && !thumbList.isEmpty()){

			for (TouchBallModel ball : thumbList) {
				canvas.drawText(ball.temp, ball.x, ball.y+ball.width/4, textPaint);
			}
		}

		if(isTempShow){
			canvas.drawText(touchBall.temp, touchBall.x, touchBall.y-TEMP_CIRCLE_SPACE+TEMP_CIECLE_WIDTH/4, textPaint);
		}

	}

	private void drawTime(Canvas canvas){

		if(thumbList != null && !thumbList.isEmpty()){
			for (TouchBallModel ball : thumbList){
				canvas.drawText(ball.timeValue+"", ball.x, ball.y-ball.width, textPaint);
			}
			if(isTimeShow){
				canvas.drawBitmap(timeBitmap, touchBall.x - timeBitmap.getWidth()/2, touchBall.y-TEMP_CIRCLE_SPACE, null);

				canvas.drawText(touchBall.timeValue+"", touchBall.x, touchBall.y-TEMP_CIRCLE_SPACE+timeBitmap.getHeight()/2, timePaint);
			}
		}


	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		width = MeasureSpec.getSize(widthMeasureSpec);
		height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		Log.i(TAG, "width:"+width+"-height:"+height);
		initMaxXY();

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	private TouchBallModel getCurBall(float x, float y){
		TouchBallModel curBall = null;
		if(thumbList != null && !thumbList.isEmpty()){
			for (TouchBallModel ball : thumbList) {

				if (x > (ball.x -  ball.width) && y > (ball.y -  ball.width) && x < (ball.x + ball.width*2)
						&& y < (ball.y + ball.width*2)) {

					curBall =  ball;
				}
			}

		}
		return curBall;
	}


	public void setThumbTemp(List<TouchBallModel> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){

			for (TouchBallModel ball : mThumbList) {
				double d =(double)((maxY - ball.y)/(maxY - minY)*(maxTemp - minTemp)+minTemp);
				d = formatTemp (d);
				ball.temp = Math.round(d) + "";
			}
		}

	}

	private double formatTemp(double dTemp){
		BigDecimal bd = new BigDecimal(dTemp);
		bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
		String temp = tempFormat(bd.toString());
		double tempInt = Double.valueOf(temp);
		if(tempInt < 10){
			tempInt = 10;
		}
		return  tempInt;
	}

	public void setThumbTime(List<TouchBallModel> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){

			for (TouchBallModel ball : mThumbList) {
				ball.time = Math.round((ball.x - minX)/(maxX - minX)*96) +"";
			}
		}
	}

	public void setYbyTemp(List<TouchBallModel> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){

			for (TouchBallModel ball : mThumbList) {

				float temp = Float.parseFloat(ball.temp);
				ball.y = maxY - (temp - minTemp)/(maxTemp - minTemp)*(maxY - minY);
			}
		}
	}

	public void setXbyTime(List<TouchBallModel> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){

			for (TouchBallModel ball : mThumbList) {
				float time = Float.parseFloat(ball.time);
				ball.x = (time/96) * (maxX - minX) + minX;
			}
		}
	}

	public void setTimeValue(List<TouchBallModel> mThumbList){
		if(mThumbList != null && !mThumbList.isEmpty()){

			for (TouchBallModel ball : mThumbList) {
				String hours = (int)(Float.parseFloat(ball.time)/4)+"";
				String mins =  (Math.round(Float.parseFloat(ball.time))%4)*15 +"";
				ball.timeValue = hours + " : " +mins;
			}
		}
		postInvalidate();
	}

	//屏幕按下时 点的X Y 值
	float dX ;
	float dY ;
	//防止单向拖动时 方向发生变化
	float dX1 ;
	float dY1 ;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		float tX = event.getX();
		float tY = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				dX = event.getX();
				dY = event.getY();
				touchBall = getCurBall(tX, tY);
				dX1 = dX;
				dY1 = dY;
				break;

			case MotionEvent.ACTION_MOVE:

				if (tX > (maxX) || tY > (maxY) || tX < minX  || tY < minY) {
					break;
				}
				if(touchBall != null){
					float mX = event.getX();
					float mY = event.getY();

					float disX = mX - dX1;
					float disY = mY - dY1;

					if(Math.abs(disY) > Math.abs(disX)){
						isTempShow = true;
						isTimeShow = false;
						dX1 = mX;
						touchBall.x = dX;
						touchBall.y = mY;

					}else{
						isTempShow = false;
						isTimeShow = true;
						dY1 = mY;
						touchBall.x = mX;
						touchBall.y = dY;

					}

					int position = thumbList.indexOf(touchBall);

					// 设置当前拖动球 不能超过前一个
					if(position != 0){
						TouchBallModel ball1 = thumbList.get(position-1);
						if(touchBall.x <= (ball1.x + 40)){
							touchBall.x = ball1.x + 40;
						}
					}
					// 设置当前拖动球不能超过后一个
					if(position != (thumbList.size()-1)){
						TouchBallModel ball1 = thumbList.get(position+1);
						if(touchBall.x >= (ball1.x- 40)){
							touchBall.x = ball1.x - 40;
						}
					}

					setThumbTemp(thumbList);
					setThumbTime(thumbList);
					setTimeValue(thumbList);

					Log.i(TAG, "x:"+touchBall.x +"y:"+touchBall.y);
					Log.i(TAG, "time:"+touchBall.time);
					Log.i(TAG, "timeValue:"+touchBall.timeValue);
				}

				break;

			case MotionEvent.ACTION_UP:
				isTempShow = false;
				isTimeShow = false;
				touchBall = null;
				break;

		}

		invalidate();
		return true;
	}

	//小数部分大于0.5设为0.5，小于0.5设为0
	private String tempFormat(String temp){
		double d = Double.valueOf(temp);
		if((d-0.5) < (int)d){
			d = (int)d;
		}
		if((d-0.5) > (int)d){
			d = (int)d + 0.5;
		}
		temp = d+"";
		return temp;
	}

}
