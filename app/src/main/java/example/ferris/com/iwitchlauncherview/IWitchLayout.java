package example.ferris.com.iwitchlauncherview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 苹果启动器 环形界面
 * Created by ferris on 2016/7/7.
 */
public class IWitchLayout extends ViewGroup {


    Paint mPaint=new Paint();

    LauncherScroller mScroller;
    Paint mPaintCircle=new Paint();
    public IWitchLayout(Context context) {
        super(context);
        init();
    }

    public IWitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IWitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private static class ScrollInterpolator implements Interpolator {
        public ScrollInterpolator() {
        }

        public float getInterpolation(float t) {
            t -= 1.0f;
            return t*t*t*t*t + 1;
        }
    }
    ScrollInterpolator mScrollInterpolator= new ScrollInterpolator();
    DecelerateInterpolator mDecelerateInterpolator= new DecelerateInterpolator();
    public void init(){
        mPaint.setColor(Color.BLACK);
        mPaintCircle.setColor(Color.WHITE);
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.STROKE);
        mPaintCircle.setStrokeWidth(10);
        mScroller = new LauncherScroller(getContext(), mScrollInterpolator);

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = 3000;
        mTouchState = TOUCH_STATE_REST;
        initIcons();
    }

    public void initIcons(){
        for(int i=0;i<20;i++){
            IconView mIconView=new IconView(getContext());
            mIconView.setImageResource(R.drawable.icon0);
            addView(mIconView,new LayoutParams(85,85));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
//
//        // measure child
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v != null) {
//                int childWidthSpec = MeasureSpec.makeMeasureSpec(mChildRect.width(), widthMode);
//                int childHeightSpec = MeasureSpec.makeMeasureSpec(mChildRect.height(), heightMode);
//                v.measure(childWidthSpec, childHeightSpec);
                measureChild(v,widthMeasureSpec,heightMeasureSpec);
            }
        }
        refreshCirclePaht(width,height);
        setMeasuredDimension(width, height);
    }

    //布局icon的位置
    //根据path
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left=l;
        int top=t;
        for(int i=0;i<getChildCount();i++){
            View mView=getChildAt(i);
            CircleBean mCircleBean=null;

            int width=mView.getMeasuredWidth();
            int height=mView.getMeasuredHeight();

            if(i==0){
                mCircleBean=  mCircleBeans.get(0);
                mView.layout(pointCenter[0]-width/2,pointCenter[1]-height/2,pointCenter[0]-width/2+width,pointCenter[1]-height/2+height);
            }else if(i<7){
                mCircleBean=  mCircleBeans.get(1);
                //1-6

                PathMeasure measure2 = new PathMeasure(mCircleBean.getmPath(),true);
                float lengthRadio = measure2.getLength()/6*(i-1);
                float[] pos=new float[2];
                measure2.getPosTan(lengthRadio,pos,null);
                mView.layout(((int)pos[0])-width/2,((int)pos[1])-height/2,((int)pos[0])-width/2+width,((int)pos[1])-height/2+height);
            }else{
                mCircleBean=  mCircleBeans.get(2);


                PathMeasure measure2 = new PathMeasure(mCircleBean.getmPath(),true);
                float lengthRadio = measure2.getLength()/12*(i-8);
                float[] pos=new float[2];
                measure2.getPosTan(lengthRadio,pos,null);
                mView.layout(((int)pos[0])-width/2,((int)pos[1])-height/2,((int)pos[0])-width/2+width,((int)pos[1])-height/2+height);

            }

        }

    }

    List<CircleBean> mCircleBeans=new ArrayList<>();

    //网格数目为 5x5
    int[] grid=new int[]{3,3};
    int[] radiusArray=new int[3];
    //网格中心点
    int[] pointCenter=new int[2];

    //第一个圆的半径

    //只需要管 宽高
    public void refreshCirclePaht(int width,int height){
        if(width!=height){
            return;
        }

        int pathcount=grid[0];
        pointCenter[0]=width/2;
        pointCenter[1]=height/2;

        //划分网格
        float  radiusFirstCircle= (int) ((height/2)/Math.sin(45*Math.PI/180));
        //划分圆圈的半径
        radiusArray[0]= 0;
        radiusArray[1]= (int) (radiusFirstCircle*0.3f);
        radiusArray[2]= (int) (radiusFirstCircle*0.55f);
        for(int i=0;i<pathcount;i++  ){
            //绘制出球的矩阵

            int delayRadiius=radiusArray[i];
            CircleBean mCircleBean=new CircleBean();
            RectF mRect=new RectF(pointCenter[0]-delayRadiius,pointCenter[1]-delayRadiius,pointCenter[0]+delayRadiius,pointCenter[1]+delayRadiius);
            mCircleBean.setmRectF(mRect);

            Path mPaht=new Path();
            mPaht.addCircle(mRect.centerX(),mRect.centerY(),delayRadiius, Path.Direction.CW);
            mCircleBean.setmPath(mPaht);
            mCircleBean.setIndex(i);

            if(i==0){
                mCircleBean.setIconCounts(1);
            }else if(i==1){
                mCircleBean.setIconCounts(6);
            }else if(i==2){
                mCircleBean.setIconCounts(12);
            }
            mCircleBeans.add(mCircleBean);
        }
    }


    private void b()
    {
        for (int i1 = 0; ; i1++)
        {
            if (i1 >= getChildCount())
                return;
//            View localView = getChildAt(i1);
//            IconScaleXYAttributes locale = (IconScaleXYAttributes)this.s.get(i1);
//            int i2 = localView.getMeasuredWidth();
//            int i3 = localView.getMeasuredHeight();
//            int i4 = this.p - this.y;
//            int i5 = this.q - this.y;
//            localView.layout(i4 + (int)locale.a, i5 + (int)locale.b, i2 + (i4 + (int)locale.a), i3 + (i5 + (int)locale.b));
//            localView.setScaleX(locale.c);
//            localView.setScaleY(locale.c);
//            localView.setAlpha(this.z);
        }
    }
    private void d()
    {
        ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
//        localValueAnimator.addUpdateListener(new b(this));
        localValueAnimator.setDuration(1000L);
        localValueAnimator.start();
    }
    public void drawCiclePahts(Canvas canvas){
        for(int i=0;i<mCircleBeans.size();i++){
            drawCiclePaht(canvas,mCircleBeans.get(i).getmPath());
        }

    }
    public void drawCiclePaht(Canvas canvas,Path mPaht){
        canvas.save();
        int scrollX=getScrollX();
        int scrollY=getScrollY();
        canvas.translate(scrollX, scrollY);
        canvas.drawPath(mPaht, mPaintCircle);
        canvas.restore();
    }

    public void drawBackgroud(Canvas canvas){
        canvas.save();
        int scrollX=getScrollX();
        int scrollY=getScrollY();
        canvas.translate(scrollX, scrollY);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mPaint);
        canvas.restore();
    }




    int translationX=0;
    int translationY=0;

    float mLastMotionX=0;
    float mLastMotionY=0;

    float mDownMotionX=0;
    float mDownMotionY=0;
    @Override
    protected void dispatchDraw(Canvas canvas) {
        //画背景
        drawBackgroud(canvas);
        //画圆
        canvas.save();
        canvas.translate(translationX,translationY);
        //drawCiclePahts(canvas);
        super.dispatchDraw(canvas);
        canvas.restore();
    }


    private VelocityTracker mVelocityTracker;
    private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
    }
    private void resetTouchState() {
        mTouchState = TOUCH_STATE_REST;
    }
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public int mTouchSlop;
    private int mMaximumVelocity;
    public static final int MIN_SNAP_VELOCITY = 300;
    public static final int MIN_MOVE_SPACE = 8;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        acquireVelocityTrackerAndAddMovement(event);


        if ((event.getAction() == MotionEvent.ACTION_MOVE) &&
                (mTouchState == TOUCH_STATE_SCROLLING)) {
            return true;
        }
        float xDiff=0;
        float yDiff=0;
        float x=  event.getX();
        float y=  event.getY();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (mScroller.isFinished()) {
                    mTouchState = TOUCH_STATE_REST;
                    mScroller.abortAnimation();
                }

                mDownMotionX=x;
                mDownMotionY=y;
                mLastMotionX=x;
                mLastMotionY=y;
                break;
            case MotionEvent.ACTION_MOVE:

                xDiff=x - mLastMotionX;
                yDiff=y - mLastMotionY;
                if(Math.abs(xDiff)>MIN_MOVE_SPACE||Math.abs(yDiff)>MIN_MOVE_SPACE){
                    mTouchState = TOUCH_STATE_SCROLLING;
                    translationX+=xDiff*SCROLL_RATIO;
                    translationY+=yDiff*SCROLL_RATIO;
                    invalidate();
                }



                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                invalidate();
                resetTouchState();
                releaseVelocityTracker();
                mDownMotionY=0;
                mDownMotionY=0;
                mLastMotionX=0;
                mLastMotionY=0;

                break;


        }
        return mTouchState==TOUCH_STATE_SCROLLING;

    }
    private static final float SCROLL_RATIO = 0.6f;// 阻尼系数
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        acquireVelocityTrackerAndAddMovement(event);
        float x=  event.getX();
        float y=  event.getY();
        float xDiff=0;
        float yDiff=0;
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                if (mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mDownMotionX=x;
                mDownMotionY=y;
                mLastMotionX=x;
                mLastMotionY=y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:


                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                float velocityY =  Math.abs(mVelocityTracker.getYVelocity());
                float velocityX= Math.abs(mVelocityTracker.getXVelocity());


                float [] xy=getRayAndRadius(x,y);
                Log.d("ray","ray:x="+xy[0]+",y="+xy[1]);
                xDiff=x - mDownMotionX;
                yDiff=y - mDownMotionY;

                if (velocityY > MIN_SNAP_VELOCITY||velocityX>MIN_SNAP_VELOCITY) {
                    //160000

                    float maxVelocity=Math.max(velocityY,velocityX);
                    int duoTime= (int) ((maxVelocity/3000f)*300);
                    mScroller.setInterpolator(mScrollInterpolator);
                    mScroller.startScroll(translationX, translationY, (int)xDiff,(int)yDiff, duoTime);
                    invalidate();
                }

                invalidate();
                mLastMotionX=0;
                mLastMotionY=0;

                resetTouchState();
                releaseVelocityTracker();

                break;

            case MotionEvent.ACTION_MOVE:
                 mTouchState = TOUCH_STATE_SCROLLING;
                 x= event.getX();
                 y=  event.getY();

                xDiff=x - mLastMotionX;
                yDiff=y - mLastMotionY;
//                float xDiffabs=Math.abs(xDiff);
//                float yDiffabs=Math.abs(yDiff);
                //if(xDiffabs>MIN_MOVE_SPACE||yDiffabs>MIN_MOVE_SPACE){
                    translationX+=xDiff*SCROLL_RATIO;
                    translationY+=yDiff*SCROLL_RATIO;
                    invalidate();
               // }


                mLastMotionY = y;
                mLastMotionX = x;

                break;


        }
        return true;
    }
    protected int mTouchState;
    protected final static int TOUCH_STATE_REST = 0;
    protected final static int TOUCH_STATE_SCROLLING = 1;
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            translationY=mScroller.getCurrY();
            translationX=mScroller.getCurrX();
            invalidate();


        }else{//滚动结束
            //判断滚动到哪里，应该反弹回来
            //速度根据滚动的距离

            if(mTouchState!=TOUCH_STATE_SCROLLING&&(translationX!=0||translationY!=0)){
                int xDiff=-translationX;
                int yDiff=-translationY;
                double distance= Math.sqrt(translationX*translationX+translationY*translationY);
                mScroller.setInterpolator(mDecelerateInterpolator);
                int  duration = (int) (Math.round(300 * Math.abs(Math.max(getHeight(),distance) / getHeight())));
                mScroller.startScroll(translationX, translationY, xDiff,yDiff,duration);
                invalidate();
            }


        }
    }


    private float[] getRayAndRadius(float paramFloat1, float paramFloat2)
    {
        float f1 = (float)Math.sqrt(Math.pow(paramFloat1, 2.0D) + Math.pow(paramFloat2, 2.0D));
        float f2 = (float)Math.atan2(paramFloat2, paramFloat1);
        float[] locald = new float[2];
        locald[0] = f1;
        locald[1] = f2;
        return locald;
    }
}
