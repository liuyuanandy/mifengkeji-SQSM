package com.beeinc.SQSB.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.beeinc.SQSB.R;
import com.beeinc.SQSB.util.common;

import org.opencv.core.Point;

import java.util.ArrayList;

public class CaiJianView extends View {
    private Context context;

    private Bitmap bitmap;
    private float bigImagescaleSize;//放大部分缩放区间的尺寸
    private float bigImageScale = 4;//放大部分缩放倍数

    private int width;//控件宽度
    private int height;//控件高度
    private int leftPadding;//控件左边距
    private int topPadding;//控件上边距

    private int bitmapWidth;//图片实际像素宽度
    private int bitmapHeight;//图片实际像素高度
    private int imageViewWidth;//图片控件宽度
    private int imageViewHeight;//图片控件高度
    private int imageWidth;//图片显示宽度
    private int imageHeight;//图片显示高度
    private int imageLeftPadding;//图片左边距
    private int imageTopPadding;//图片上边距

    private int imageBigWidth;//缩放区域宽
    private int imageBigHeight;//缩放区域高

    private Paint paintForImage;
    private Paint paintForLine;//细线
    private Paint paintForLineBlod;//粗线

    private Paint paintForBig;

    private PointF[] points = new PointF[4];//图片初始4个点的位置
    private PointF[] caijianPoints = new PointF[4];//裁剪的四个顶点
    private PointF[] caijianLineCenterPoints = new PointF[4];//裁剪四个边中心点的位置
    private float initX;//按下时x坐标
    private float initY;//按下时y坐标

    private float x;//移动时候的坐标
    private float y;//移动时候的坐标
    private boolean isOnTouch;//是否响应触摸事件
    private int caijianWidth ;//裁剪线长度
    private int circleR;//触摸圆点半径
    private float scale;//图片显示尺寸与原图尺寸的比例
    int position;//拖动的位置  0；1；2；3；1000  1000表示整体拖动裁剪框
    private boolean isDown;//手指是否按下

    private float onTouchX;
    private float onTouchY;

    private CaijianListener caijianListener;

    public final int CAIJIAN_UNSTART = 0 ;//尚未拖动矫正的点；
    public final int CAIJIAN_MOVED_CIRCLES = 1 ;// 已经拖动矫正的点；
    public final int CAIJIAN_START = 2 ;// 裁剪开始；
    public final int CAIJIAN_END = 3 ;// 结束；
    public int CAIJIAN_STATUS = CAIJIAN_UNSTART;
    private int initBoardSpace;//初始距离图片边缘距离

    public CaiJianView(Context context) {
        super(context);
        init();
    }

    public CaiJianView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        init();
    }

    public CaiJianView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        initBoardSpace = common.Dp2Px(getContext(),10);

        paintForImage = new Paint();
        paintForImage.setAntiAlias(true);

        paintForLine = new Paint();
        paintForLine.setAntiAlias(true);
        paintForLine.setStrokeWidth(common.Dp2Px(getContext(), 2));
        paintForLine.setColor(this.getResources().getColor(R.color.color00BABA));

        paintForLineBlod = new Paint();
        paintForLineBlod.setAntiAlias(true);
        paintForLineBlod.setStrokeWidth(common.Dp2Px(getContext(), 6));
        paintForLineBlod.setColor(this.getResources().getColor(R.color.color00BABA));

        paintForBig = new Paint();
        paintForBig.setAntiAlias(true);
        paintForBig.setStrokeWidth(common.Dp2Px(getContext(), 2));
        paintForBig.setStyle(Paint.Style.STROKE);
        paintForBig.setColor(Color.parseColor("#60000000"));
        caijianWidth = common.Dp2Px(context,25 );
        circleR = common.Dp2Px(context,5);
        bigImagescaleSize = 4*circleR;
        imageBigHeight =imageBigWidth = (int)(bigImagescaleSize*bigImageScale);

    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(width!=0&&height!=0&&bitmap!=null){
            if(CAIJIAN_STATUS!=CAIJIAN_END){
                //绘制线段
                drawLines(canvas);
                //绘制裁剪线
                drawShortLine(canvas);
            }
        }
    }
    @Override
    public void onMeasure(int widthMeasureSpect,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpect, heightMeasureSpec);
        if(width==0||height==0){
            initWidthAndHeight();
            setData();
        }else{
            if(width!=getMeasuredWidth()||height!=getMeasuredHeight()){
                initWidthAndHeight();
                setData();
            }
        }
    }
    private void initWidthAndHeight(){
        width = getMeasuredWidth();
        height = getMeasuredHeight();
//        leftPadding = common.Dp2Px(getContext(),60);
//        topPadding = common.Dp2Px(getContext(),25);
        imageViewWidth = width -leftPadding*2;
        imageViewHeight = height -topPadding*2;
    }
    private void setData(){
        if(width!=0&&height!=0&&bitmap!=null){
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
            initImagePosition();
            initCirclesPosition();
        }
        invalidate();
    }
    private void initImagePosition(){
        if((float)bitmapWidth/bitmapHeight>=(float)imageViewWidth/imageViewHeight){//图片的宽度比例大于图片显示区域的宽度比例，图片上下居中
            scale = (float) imageViewWidth/bitmapWidth;
            imageWidth = imageViewWidth;
            imageHeight = (int)(bitmapHeight*scale);
            imageLeftPadding =leftPadding;
            imageTopPadding = topPadding+(imageViewHeight-imageHeight)/2;
        }else{//图片左右居中
            scale = (float) imageViewHeight/bitmapHeight;
            imageHeight = imageViewHeight;
            imageWidth = (int)(bitmapWidth*scale);
            imageLeftPadding = leftPadding+(imageViewWidth-imageWidth)/2;
            imageTopPadding = topPadding;
        }

        //图片四个顶点
        for(int i=0;i<points.length;i++){
            points[i] = new PointF();
        }
        points[0].x = imageLeftPadding;
        points[0].y = imageTopPadding;

        points[1].x = imageLeftPadding+imageWidth;
        points[1].y = imageTopPadding;

        points[2].x = imageLeftPadding;
        points[2].y = imageTopPadding+imageHeight;

        points[3].x = imageLeftPadding+imageWidth;
        points[3].y = imageTopPadding+imageHeight;


    }
    //圆顺序：从左到右，从上到下
    private void initCirclesPosition(){
        for(int i=0;i<caijianPoints.length;i++){
            caijianPoints[i] = new PointF();
        }
        caijianPoints[0].x = points[0].x+initBoardSpace;
        caijianPoints[0].y = points[0].y+initBoardSpace;

        caijianPoints[1].x = points[1].x-initBoardSpace;
        caijianPoints[1].y = points[1].y+initBoardSpace;

        caijianPoints[2].x = points[2].x+initBoardSpace;
        caijianPoints[2].y = points[2].y-initBoardSpace;

        caijianPoints[3].x = points[3].x-initBoardSpace;
        caijianPoints[3].y = points[3].y-initBoardSpace;


        for(int i=0;i<caijianLineCenterPoints.length;i++){
            caijianLineCenterPoints[i] = new PointF();
        }
        caijianLineCenterPoints[0].x = (caijianPoints[0].x+caijianPoints[1].x)/2;
        caijianLineCenterPoints[0].y = (caijianPoints[0].y+caijianPoints[1].y)/2;

        caijianLineCenterPoints[1].x = (caijianPoints[0].x+caijianPoints[2].x)/2;
        caijianLineCenterPoints[1].y = (caijianPoints[0].y+caijianPoints[2].y)/2;

        caijianLineCenterPoints[2].x = (caijianPoints[1].x+caijianPoints[3].x)/2;
        caijianLineCenterPoints[2].y = (caijianPoints[1].y+caijianPoints[3].y)/2;

        caijianLineCenterPoints[3].x = (caijianPoints[2].x+caijianPoints[3].x)/2;
        caijianLineCenterPoints[3].y = (caijianPoints[2].y+caijianPoints[3].y)/2;
    }
    public void drawLines(Canvas canvas){
        canvas.drawLine(caijianPoints[0].x,caijianPoints[0].y ,caijianPoints[1].x,caijianPoints[1].y, paintForLine);
        canvas.drawLine(caijianPoints[1].x,caijianPoints[1].y ,caijianPoints[3].x,caijianPoints[3].y, paintForLine);
        canvas.drawLine(caijianPoints[2].x,caijianPoints[2].y ,caijianPoints[3].x,caijianPoints[3].y, paintForLine);
        canvas.drawLine(caijianPoints[0].x,caijianPoints[0].y ,caijianPoints[2].x,caijianPoints[2].y, paintForLine);
    }
    public void drawShortLine(Canvas canvas){
        canvas.drawLine(caijianLineCenterPoints[0].x-caijianWidth/2,caijianLineCenterPoints[0].y ,caijianLineCenterPoints[0].x+caijianWidth/2,caijianLineCenterPoints[0].y, paintForLineBlod);
        canvas.drawLine(caijianLineCenterPoints[1].x,caijianLineCenterPoints[1].y -caijianWidth/2,caijianLineCenterPoints[1].x,caijianLineCenterPoints[1].y+caijianWidth/2, paintForLineBlod);
        canvas.drawLine(caijianLineCenterPoints[2].x,caijianLineCenterPoints[2].y -caijianWidth/2,caijianLineCenterPoints[2].x,caijianLineCenterPoints[2].y+caijianWidth/2, paintForLineBlod);
        canvas.drawLine(caijianLineCenterPoints[3].x-caijianWidth/2,caijianLineCenterPoints[3].y ,caijianLineCenterPoints[3].x+caijianWidth/2,caijianLineCenterPoints[3].y, paintForLineBlod);
    }
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                x = initX = event.getX();
                y = initY = event.getY();
                onTouchX = x;
                onTouchY = y;
                position = getOnTouchCirclePosition();
                Log.e("------------>","position = "+position);
                if(position!=-1){
                    isOnTouch = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float xNow = event.getX();
                float yNow = event.getY();
                float offsetX = xNow-x;
                float offsetY = yNow-y;
                x = xNow;
                y = yNow;
                if(isOnTouch){
                    move(offsetX,offsetY);
                    postInvalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDown = false;
                isOnTouch =false;
                invalidate();
                break;
        }
        if(isOnTouch){
            boolean isJiaoZheng = checkIsCaiJian();//是否显示矫正
            if(isJiaoZheng){
                CAIJIAN_STATUS = CAIJIAN_MOVED_CIRCLES;
                if(caijianListener!=null){
                    caijianListener.isMovedCaijianCircles(true);
                }
            }else{
                CAIJIAN_STATUS = CAIJIAN_UNSTART;
                if(caijianListener!=null){
                    caijianListener.isMovedCaijianCircles(false);
                }
            }
        }
        return true;
    }
    //获取被按下的圆的位置
    private int getOnTouchCirclePosition(){
        int position = -1;
        for(int i=0;i<caijianLineCenterPoints.length;i++){
            if(initX>caijianLineCenterPoints[i].x-caijianWidth/2&&initX<caijianLineCenterPoints[i].x+caijianWidth/2
                    &&initY>caijianLineCenterPoints[i].y-caijianWidth/2&&initY<caijianLineCenterPoints[i].y+caijianWidth/2
            ){
                return i;
            }
        }
        //裁剪框整体拖动
        if(initX>caijianPoints[0].x+caijianWidth/2&&initX<caijianPoints[3].x&&initY>caijianPoints[0].y+caijianWidth/2&&initY<caijianPoints[3].y){
            return 1000;
        }
        return position;
    }
    private void move(float offsetX,float offsetY){
        float xLocation = x+offsetX;
        float yLocation = y+offsetY;
        switch (position){
            case 0:
                if(yLocation<points[0].y){
                    caijianLineCenterPoints[0].y = points[0].y;
                }else if(yLocation<caijianLineCenterPoints[3].y-caijianWidth){
                    caijianLineCenterPoints[0].y = yLocation;
                }else{
                    caijianLineCenterPoints[0].y = caijianLineCenterPoints[3].y-caijianWidth;
                }
                caijianPoints[0].y = caijianLineCenterPoints[0].y;
                caijianPoints[1].y = caijianLineCenterPoints[0].y;
                caijianLineCenterPoints[1].y = (caijianLineCenterPoints[0].y+caijianLineCenterPoints[3].y)/2;
                caijianLineCenterPoints[2].y = (caijianLineCenterPoints[0].y+caijianLineCenterPoints[3].y)/2;
                break;
            case 3:
                if(yLocation<caijianLineCenterPoints[0].y+caijianWidth){
                    caijianLineCenterPoints[3].y = caijianLineCenterPoints[0].y+caijianWidth;
                }else if(yLocation<points[3].y){
                    caijianLineCenterPoints[3].y = yLocation;
                }else{
                    caijianLineCenterPoints[3].y = points[3].y;
                }
                caijianPoints[2].y = caijianLineCenterPoints[3].y;
                caijianPoints[3].y = caijianLineCenterPoints[3].y;
                caijianLineCenterPoints[1].y = (caijianLineCenterPoints[0].y+caijianLineCenterPoints[3].y)/2;
                caijianLineCenterPoints[2].y = (caijianLineCenterPoints[0].y+caijianLineCenterPoints[3].y)/2;
                break;
            case 1:
                if(xLocation<points[0].x){
                    caijianLineCenterPoints[1].x = points[0].x;
                }else if(xLocation<caijianLineCenterPoints[2].x-caijianWidth){
                    caijianLineCenterPoints[1].x = xLocation;
                }else{
                    caijianLineCenterPoints[1].x = caijianLineCenterPoints[2].x-caijianWidth;
                }
                caijianPoints[0].x = caijianLineCenterPoints[1].x;
                caijianPoints[2].x = caijianLineCenterPoints[1].x;
                caijianLineCenterPoints[0].x = (caijianLineCenterPoints[1].x+caijianLineCenterPoints[2].x)/2;
                caijianLineCenterPoints[3].x = (caijianLineCenterPoints[1].x+caijianLineCenterPoints[2].x)/2;
                break;
            case 2:
                if(xLocation<caijianLineCenterPoints[1].x+caijianWidth){
                    caijianLineCenterPoints[2].x = caijianLineCenterPoints[1].x+caijianWidth;
                }else if(xLocation<points[3].x){
                    caijianLineCenterPoints[2].x = xLocation;
                }else{
                    caijianLineCenterPoints[2].x = points[3].x;
                }
                caijianPoints[1].x = caijianLineCenterPoints[2].x;
                caijianPoints[3].x = caijianLineCenterPoints[2].x;
                caijianLineCenterPoints[0].x = (caijianLineCenterPoints[1].x+caijianLineCenterPoints[2].x)/2;
                caijianLineCenterPoints[3].x = (caijianLineCenterPoints[1].x+caijianLineCenterPoints[2].x)/2;
                break;
            case 1000:

                if(offsetX<0){
                    if(caijianLineCenterPoints[1].x+offsetX<points[0].x){
                        offsetX = points[0].x -caijianLineCenterPoints[1].x;
                    }
                }else{
                    if(caijianLineCenterPoints[2].x>points[3].x){
                        offsetX = points[3].x-caijianLineCenterPoints[2].x;
                    }
                }
                if(offsetY<0){
                    if(caijianLineCenterPoints[0].y+offsetY<points[0].y){
                        offsetY = points[0].y -caijianLineCenterPoints[0].y;
                    }
                }else{
                    if(caijianLineCenterPoints[3].y>points[3].y){
                        offsetY = points[3].y-caijianLineCenterPoints[3].y;
                    }
                }
                caijianPoints[0].x = caijianPoints[0].x+offsetX;
                caijianPoints[0].y = caijianPoints[0].y+offsetY;
                caijianPoints[1].x = caijianPoints[1].x+offsetX;
                caijianPoints[1].y = caijianPoints[1].y+offsetY;
                caijianPoints[2].x = caijianPoints[2].x+offsetX;
                caijianPoints[2].y = caijianPoints[2].y+offsetY;
                caijianPoints[3].x = caijianPoints[3].x+offsetX;
                caijianPoints[3].y = caijianPoints[3].y+offsetY;

                caijianLineCenterPoints[0].x = caijianLineCenterPoints[0].x+offsetX;
                caijianLineCenterPoints[0].y = caijianLineCenterPoints[0].y+offsetY;
                caijianLineCenterPoints[1].x = caijianLineCenterPoints[1].x+offsetX;
                caijianLineCenterPoints[1].y = caijianLineCenterPoints[1].y+offsetY;
                caijianLineCenterPoints[2].x = caijianLineCenterPoints[2].x+offsetX;
                caijianLineCenterPoints[2].y = caijianLineCenterPoints[2].y+offsetY;
                caijianLineCenterPoints[3].x = caijianLineCenterPoints[3].x+offsetX;
                caijianLineCenterPoints[3].y = caijianLineCenterPoints[3].y+offsetY;
                break;

        }
    }
    public void saveBitmap(String path){
//        Bitmap b=bitmap;
//        if(getContext() instanceof MirroPictureActivity){
//            b = bitmap;
//        }else if(getContext() instanceof CutPictureActivity){
//            if(type==0){//未操作
//                b= bitmap;
//            }else if(type==1){//操作中
//                preViewClick();
//                b = previewBitmap;
//            }else if(type==2){//正在预览
//                b =previewBitmap;
//            }
//        }else if(getContext() instanceof MirroPictureActivity){
//            if(type==0){//未操作
//                b= bitmap;
//            }else if(type==1){//操作中
//                preViewClick();
//                b = previewBitmap;
//            }else if(type==2){//正在预览
//                b =previewBitmap;
//            }
//        }else if(getContext() instanceof DuiHuaActivity){
//            if(type==0){//未操作
//                b= bitmap;
//            }else if(type==1){//操作中
//                preViewClick();
//                b = previewBitmap;
//            }else if(type==2){//正在预览
//                b =previewBitmap;
//            }
//        }
//        Mat mat= new Mat();
//        Utils.bitmapToMat(b,mat);
//        Mat temp = new Mat();
//        Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
//        Imgcodecs.imwrite(path,temp);
//        temp.release();
//        mat.release();
    }
    //获取映射前坐标
    public ArrayList<Point> getPointsBeforeCaijian(){
        ArrayList<Point> srcPoints = new ArrayList<Point>();
        srcPoints.add(new Point((caijianPoints[0].x-imageLeftPadding)/scale,(caijianPoints[0].y-imageTopPadding)/scale));
        srcPoints.add(new Point((caijianPoints[1].x-imageLeftPadding)/scale,(caijianPoints[1].y-imageTopPadding)/scale));
        srcPoints.add(new Point((caijianPoints[2].x-imageLeftPadding)/scale,(caijianPoints[2].y-imageTopPadding)/scale));
        srcPoints.add(new Point((caijianPoints[3].x-imageLeftPadding)/scale,(caijianPoints[3].y-imageTopPadding)/scale));
        return srcPoints;
    }
    //获取映射后的坐标
    public ArrayList<Point> getPointsAfterCaijian(){
        //获取映射后的坐标
        float h;
        float w;
        w = (caijianPoints[1].x-caijianPoints[0].x)/scale;
        h = (caijianPoints[1].y-caijianPoints[0].y)/scale;
        int widthTop = (int)Math.sqrt(w*w+h*h);
        w = (caijianPoints[3].x-caijianPoints[2].x)/scale;
        h = (caijianPoints[3].y-caijianPoints[2].y)/scale;
        int widthbottom = (int)Math.sqrt(w*w+h*h);
        int width = widthTop>=widthbottom?widthTop:widthbottom;
        w = (caijianPoints[2].x-caijianPoints[0].x)/scale;
        h = (caijianPoints[2].y-caijianPoints[0].y)/scale;
        int heightLeft =(int)Math.sqrt(w*w+h*h);
        w = (caijianPoints[3].x-caijianPoints[1].x)/scale;
        h = (caijianPoints[3].y-caijianPoints[1].y)/scale;
        int heightRight = (int)Math.sqrt(w*w+h*h);
        int height = heightLeft>=heightRight?heightLeft:heightRight;
        ArrayList<Point> dstPoints = new ArrayList<Point>();
        dstPoints.add(new Point(0,0));
        dstPoints.add(new Point(width,0 ));
        dstPoints.add(new Point(0,height));
        dstPoints.add(new Point(width,height));
        return dstPoints;
    }
    private boolean checkIsCaiJian(){
        if(caijianPoints==null){
            return false;
        }
        for(int i=0;i<caijianPoints.length;i++){
            if(caijianPoints[i].x!=points[i].x||caijianPoints[i].y!=points[i].y){
                return true;
            }
        }
        return false;
    }

    public void setImageBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        CAIJIAN_STATUS = CAIJIAN_UNSTART;
        setData();
    }
    public void setCaiJianStart(){
        if(caijianListener!=null){
            caijianListener.caiJianStart();
        }
        CAIJIAN_STATUS = CAIJIAN_START;
    }
    public void setCaiJianEnd(){
        CAIJIAN_STATUS = CAIJIAN_END;
        if(caijianListener!=null){
            caijianListener.caiJianEnd();
        }
        invalidate();
    }

    public void setCaijianListener(CaijianListener caijianListener) {
        this.caijianListener = caijianListener;
    }

    public interface CaijianListener{
        public void isMovedCaijianCircles(boolean isMoved);//裁剪四个点是否移动过。移动过后，可以执行矫正
        public void caiJianStart();//裁剪开始
        public void caiJianEnd();//裁剪结束
    }
}
