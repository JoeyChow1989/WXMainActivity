package com.hb.ui;



import com.hb.qx.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

@SuppressLint("NewApi")
public class GradientView extends View {
	

	private static final  String  TAG = "GradientView";
	private static final boolean DEBUG = false;
	private float 	mIndex = 0;
	private Shader	mShader;
	private int 	mTextSize;	
	private static final int mUpdateStep = 15;
	private static final int mMaxWidth = 40 * mUpdateStep; // 26*25 
	private static final int mMinWidth = 6 * mUpdateStep;  // 5*25
	int 			mDefaultColor;
	int             mSlideColor;
	private ValueAnimator animator;
	private int mWidth,mHeight;
	private String mStringToShow;
	private Paint mTextPaint;
	private float mTextHeight;
	private Drawable mSlideIcon;
	private int mSlideIconHeight;
	private static final int mSlideIconOffSetTop = 2;

	@SuppressLint("NewApi")
	private AnimatorUpdateListener mAnimatorUpdateListener = new AnimatorUpdateListener() {
		
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			mIndex =Float.parseFloat(animation.getAnimatedValue().toString());
			// RadialGradient SweepGradient
			mShader = new LinearGradient(mIndex - 20 * mUpdateStep, 100,
					mIndex, 100, new int[] { mDefaultColor, mDefaultColor, mDefaultColor,mSlideColor,
					mSlideColor, mDefaultColor, mDefaultColor, mDefaultColor }, null,
					Shader.TileMode.CLAMP);

			postInvalidate();
			
		}
	};
	
	public GradientView(Context context) {
		super(context);
	}

	
	public GradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientView);
		mStringToShow = a.getString(R.styleable.GradientView_StringToShow) ;
		mTextSize = (int)a.getDimension(R.styleable.GradientView_TextSize, 40);
		mDefaultColor = a.getColor(R.styleable.GradientView_TextColor, Color.GRAY);
		mSlideColor = a.getColor(R.styleable.GradientView_SlideColor, Color.WHITE);
		mSlideIcon = context.getResources().getDrawable(R.drawable.slide_icon);
		
		mSlideIconHeight = mSlideIcon.getMinimumHeight();
		a.recycle();
		
		animator = ValueAnimator.ofFloat(mMinWidth,mMaxWidth);
		animator.setDuration(1800);
		animator.addUpdateListener(mAnimatorUpdateListener);
		animator.setRepeatCount(Animation.INFINITE);//repeat animation
		animator.start();
		
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(mSlideColor);
		mTextPaint.setTextSize(mTextSize); 
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		
		mTextHeight = mTextPaint.ascent();
		
		setFocusable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if(DEBUG)
		Log.w(TAG, "b onDraw()");
		mTextPaint.setShader(mShader);
		//canvas.drawBitmap(((BitmapDrawable) mSlideIcon).getBitmap(), 10, mHeight
				/// 2 - mSlideIconHeight / 2 + mSlideIconOffSetTop, null);
		canvas.drawText(mStringToShow, mWidth/3, mHeight / 2 - mTextHeight
				/ 2 - mSlideIconOffSetTop, mTextPaint); // slide_unlock
	} 

	

	public void stopAnimatorAndChangeColor() {
		//if(DEBUG)
		Log.w(TAG, "stopGradient");
		animator.cancel();
		//reset
		mShader = new LinearGradient(0, 100, mIndex, 100,
				new int[] {mSlideColor, mSlideColor},
				null, Shader.TileMode.CLAMP);
		invalidate();
	}
	
	public void startAnimator() {
		if(DEBUG)
		Log.w(TAG, "startGradient");
		animator.start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		mHeight = MeasureSpec.getSize(heightMeasureSpec);
	}
	
	public void resetControl(){
		animator.start();
		this.setX(0);
		invalidate();
	}
}

