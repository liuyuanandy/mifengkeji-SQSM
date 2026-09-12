package com.beeinc.mylibrary.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.beeinc.mylibrary.R;
import com.beeinc.mylibrary.bean.Line;
import com.beeinc.mylibrary.util.common;

import org.opencv.core.Point;

import java.util.ArrayList;

public class JiaozhenView extends View {
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
    private Paint paintForLine;
    private Paint paintForBig;

    private PointF[] points = new PointF[4];//图片初始4个点的位置
    private PointF[] circles = new PointF[4];//矫正四个点的位置

    private int position;//点击的拖动点位置

    private float initX;//按下时x坐标
    private float initY;//按下时y坐标

    private float x;//移动时候的坐标
    private float y;//移动时候的坐标
    private boolean isOnTouch;//是否响应触摸事件
    private int circleR;//触摸圆点半径
    private float scale;//图片显示尺寸与原图尺寸的比例
    private boolean isDown;//手指是否按下

    private float onTouchX;
    private float onTouchY;

    private JiaoZhenListener jiaoZhenListener;

    public final int JIAOZHENG_UNSTART = 0 ;//尚未拖动矫正的点；
    public final int JIAOZHENG_MOVED_CIRCLES = 1 ;// 已经拖动矫正的点；
    public final int JIAOZHENG_IS_DOING = 2 ;//正在矫正；
    public final int JIAOZHENG_ENDED = 3 ;//矫正结束；
    public int JIAOZHENG_STATUS = JIAOZHENG_UNSTART;

    private int initBoardSpace;//初始距离图片边缘距离
    public JiaozhenView(Context context) {
        super(context);
        init();
    }

    public JiaozhenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;
        init();
    }

    public JiaozhenView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        paintForBig = new Paint();
        paintForBig.setAntiAlias(true);
        paintForBig.setStrokeWidth(common.Dp2Px(getContext(), 2));
        paintForBig.setStyle(Paint.Style.STROKE);
        paintForBig.setColor(Color.parseColor("#60000000"));
        circleR = common.Dp2Px(context,5 );
        bigImagescaleSize = 4*circleR;
        imageBigHeight =imageBigWidth = (int)(bigImagescaleSize*bigImageScale);

    }
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(width!=0&&height!=0&&bitmap!=null){
            if(JIAOZHENG_STATUS!=JIAOZHENG_ENDED){
                //绘制线段
                drawLines(canvas);
                //绘制圆
                drawCircles(canvas);
                if(isDown){
                    drawScalePart(canvas);
                }
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
        leftPadding = common.Dp2Px(getContext(),0);
        topPadding = common.Dp2Px(getContext(),0);
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
        for(int i=0;i<circles.length;i++){
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
        for(int i=0;i<circles.length;i++){
            circles[i] = new PointF();
        }
        circles[0].x = points[0].x+initBoardSpace;
        circles[0].y = points[0].y+initBoardSpace;

        circles[1].x = points[1].x-initBoardSpace;
        circles[1].y = points[1].y+initBoardSpace;

        circles[2].x = points[2].x+initBoardSpace;
        circles[2].y = points[2].y-initBoardSpace;

        circles[3].x = points[3].x-initBoardSpace;
        circles[3].y = points[3].y-initBoardSpace;
    }
    public void drawLines(Canvas canvas){
        canvas.drawLine(circles[0].x,circles[0].y ,circles[1].x,circles[1].y, paintForLine);
        canvas.drawLine(circles[1].x,circles[1].y ,circles[3].x,circles[3].y, paintForLine);
        canvas.drawLine(circles[2].x,circles[2].y ,circles[3].x,circles[3].y, paintForLine);
        canvas.drawLine(circles[0].x,circles[0].y ,circles[2].x,circles[2].y, paintForLine);
    }
    public void drawCircles(Canvas canvas){
        for(int i=0;i<circles.length;i++){
            canvas.drawCircle(circles[i].x,circles[i].y , circleR,paintForLine );
        }
    }
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDown = true;
                x = initX = event.getX();
                y = initY = event.getY();
                onTouchX = x;
                onTouchY = y;
                isOnTouch = false;
                position = getOnTouchCirclePosition();
                if(position!=-1){
                    isOnTouch = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float xNow = event.getX();
                float yNow = event.getY();
                float offsetX = xNow-x;
                float offsetY = yNow-y;
                if(isOnTouch){
                    switch(position){
                        case 0:
                            move0(offsetX,offsetY);
                            break;
                        case 1:
                            move1(offsetX,offsetY);
                            break;
                        case 2:
                            move2(offsetX,offsetY);
                            break;
                        case 3:
                            move3(offsetX,offsetY);
                            break;
                    }
                    postInvalidate();
                }
                x = xNow;
                y = yNow;
                if(position!=-1){
                    onTouchX = circles[position].x;
                    onTouchY = circles[position].y;
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
            boolean isJiaoZheng = checkIsJiaozhenging();//是否显示矫正
            if(isJiaoZheng){
                JIAOZHENG_STATUS = JIAOZHENG_MOVED_CIRCLES;
                if(jiaoZhenListener!=null){
                    jiaoZhenListener.isMovedJiaoZhenCircles(true);
                }
            }else{
                JIAOZHENG_STATUS = JIAOZHENG_UNSTART;
                if(jiaoZhenListener!=null){
                    jiaoZhenListener.isMovedJiaoZhenCircles(false);
                }
            }
        }
        return true;
    }
    //获取被按下的圆的位置
    private int getOnTouchCirclePosition(){
        int position = -1;
        float distance = 0;//
        for(int i=0;i<circles.length;i++){
            float offsetX = circles[i].x-initX;
            float offsetY = circles[i].y-initY;
            float distanceCurrent = (float)Math.sqrt(offsetX*offsetX+offsetY*offsetY);
            if(distanceCurrent<= common.Dp2Px(context, 20)){
                if(position==-1){
                    distance = distanceCurrent;
                    position = i;
                }else{
                    if(distanceCurrent<=distance){
                        position = i;
                        distance = distanceCurrent;
                    }
                }
            }
        }
        return position;
    }
    private int getTwoPointsDistance(PointF a,PointF b){
        return (int)Math.sqrt((b.x-a.x)*(b.x-a.x)+(b.y-a.y)*(b.y-a.y));

    }
    //移动点的位置0
    private void move0(float offsetX, float offsetY){
        Line line_1_2 = new Line(circles[1].x,circles[1].y ,circles[2].x ,circles[2].y);
        Line line_2_3 = new Line(circles[2].x,circles[2].y, circles[3].x ,circles[3].y);
        Line line_1_3 = new Line(circles[1].x ,circles[1].y,circles[2].x ,circles[2].y);

        float endX;
        float endY;
        endX = circles[0].x+offsetX;
        endY = circles[0].y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0].x&&endX<=points[1].x&&endY>=points[0].y&&endY<=points[2].y){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX<=circles[1].x-4*circleR&&endY<=circles[2].y-4*circleR){
            isInPoint = true;
        }
        //第三步 判断是否越过对角线和是否越过对角边
        boolean isInLine = false;
        if(line_1_2.calculate(endX, endY)>=4*circleR&&line_2_3.calculate(endX, endY)>4*circleR&&line_1_3.calculate(endX, endY)>4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circles[0].x = endX;
            circles[0].y = endY;
        }
    }
    //移动点的位置1
    private void move1( float offsetX, float offsetY){
        Line line_0_3 = new Line(circles[0].x,circles[0].y ,circles[3].x ,circles[3].y);
        Line line_0_2 = new Line(circles[0].x,circles[0].y, circles[2].x ,circles[2].y);
        Line line_2_3 = new Line(circles[2].x ,circles[2].y,circles[3].x ,circles[3].y);
        float endX;
        float endY;
        endX = circles[1].x+offsetX;
        endY = circles[1].y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0].x&&endX<=points[1].x&&endY>=points[0].y&&endY<=points[2].y){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX>circles[0].x+4*circleR&&endY<circles[3].y-4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
        if(line_0_3.calculate(endX, endY)>=4*circleR&&line_0_2.calculate(endX, endY)>=4*circleR&&line_2_3.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circles[1].x = endX;
            circles[1].y = endY;
        }
    }
    //移动点的位置2
    private void move2(float offsetX, float offsetY){
        Line line_0_3 = new Line(circles[0].x,circles[0].y ,circles[3].x ,circles[3].y);
        Line line_1_3 = new Line(circles[1].x,circles[1].y, circles[3].x ,circles[3].y);
        Line line_0_1 = new Line(circles[0].x ,circles[0].y,circles[1].x ,circles[1].y);
        float endX;
        float endY;
        endX = circles[2].x+offsetX;
        endY = circles[2].y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0].x&&endX<=points[1].x&&endY>=points[0].y&&endY<=points[2].y){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX<=circles[3].x-4*circleR&&endY>=circles[0].y+4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
        if(line_0_3.calculate(endX, endY)>=4*circleR&&line_1_3.calculate(endX, endY)>=4*circleR&&line_0_1.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circles[2].x = endX;
            circles[2].y = endY;
        }
    }
    //移动点的位置3
    private void move3(float offsetX, float offsetY){
        Line line_1_2 = new Line(circles[1].x,circles[1].y ,circles[2].x ,circles[2].y);
        Line line_0_1 = new Line(circles[0].x,circles[0].y, circles[1].x ,circles[1].y);
        Line line_0_2 = new Line(circles[0].x ,circles[0].y,circles[2].x ,circles[2].y);
        float endX;
        float endY;
        endX = circles[3].x+offsetX;
        endY = circles[3].y+offsetY;
        boolean isRightLine1 = false;
        boolean isBootomLine2 = false;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0].x&&endX<=points[1].x&&endY>=points[0].y&&endY<=points[2].y){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX>=circles[0].x+4*circleR&&endY>=circles[0].y+4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
        if(line_1_2.calculate(endX, endY)>=4*circleR&&line_0_1.calculate(endX, endY)>=4*circleR&&line_0_2.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circles[3].x = endX;
            circles[3].y = endY;

        }
    }

    //获取映射前坐标
    public ArrayList<Point> getPointsBeforeJiaozheng(){
        ArrayList<Point> srcPoints = new ArrayList<Point>();
        srcPoints.add(new Point((circles[0].x-imageLeftPadding)/scale,(circles[0].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[1].x-imageLeftPadding)/scale,(circles[1].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[2].x-imageLeftPadding)/scale,(circles[2].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[3].x-imageLeftPadding)/scale,(circles[3].y-imageTopPadding)/scale));
        return srcPoints;
    }
    //获取映射后的坐标
    public ArrayList<Point> getPointsAfterJiaozhen(){
        //获取映射后的坐标
        float h;
        float w;
        w = (circles[1].x-circles[0].x)/scale;
        h = (circles[1].y-circles[0].y)/scale;
        int widthTop = (int)Math.sqrt(w*w+h*h);
        w = (circles[3].x-circles[2].x)/scale;
        h = (circles[3].y-circles[2].y)/scale;
        int widthbottom = (int)Math.sqrt(w*w+h*h);
        int width = widthTop>=widthbottom?widthTop:widthbottom;
        w = (circles[2].x-circles[0].x)/scale;
        h = (circles[2].y-circles[0].y)/scale;
        int heightLeft =(int)Math.sqrt(w*w+h*h);
        w = (circles[3].x-circles[1].x)/scale;
        h = (circles[3].y-circles[1].y)/scale;
        int heightRight = (int)Math.sqrt(w*w+h*h);
        int height = heightLeft>=heightRight?heightLeft:heightRight;
        ArrayList<Point> dstPoints = new ArrayList<Point>();
        dstPoints.add(new Point(0,0));
        dstPoints.add(new Point(width,0 ));
        dstPoints.add(new Point(0,height));
        dstPoints.add(new Point(width,height));
        return dstPoints;
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
    //放大部分
    private void drawScalePart(Canvas canvas){
        float offsetX;
        if(onTouchX+ common.Dp2Px(getContext(), -60)>0){
            offsetX = common.Dp2Px(getContext(), -60);
        }else{
            offsetX = 0;
        }
        float offsetY;
        if(onTouchY+ common.Dp2Px(getContext(), -60)>0){
            offsetY = common.Dp2Px(getContext(), -60);
        }else{
            offsetY = 0;
        }
        Bitmap bt =getScaleBitmap(bitmap);
        Bitmap big = Bitmap.createScaledBitmap(bt, imageBigWidth,imageBigHeight,true);
        canvas.drawBitmap(big, onTouchX+offsetX-imageBigWidth/2, onTouchY+offsetY-imageBigHeight/2, paintForImage);
        canvas.drawCircle(onTouchX+offsetX, onTouchY+offsetY , imageBigWidth/2, paintForBig);
        canvas.drawCircle(onTouchX+offsetX, onTouchY+offsetY,circleR,paintForLine);
        switch(position){
            case 0:
                PointF point_circle_0_1 = getCirePoint(circles[0],circles[1]);
                canvas.drawLine(circles[0].x+offsetX,circles[0].y+offsetY ,point_circle_0_1.x+offsetX ,point_circle_0_1.y+offsetY,paintForLine);
                PointF point_circle_0_2 = getCirePoint(circles[0],circles[2]);
                canvas.drawLine(circles[0].x+offsetX,circles[0].y +offsetY,point_circle_0_2.x +offsetX,point_circle_0_2.y+offsetY,paintForLine);
                break;
            case 1:
                PointF point_circle_1_0 = getCirePoint(circles[1],circles[0]);
                canvas.drawLine(circles[1].x+offsetX,circles[1].y +offsetY,point_circle_1_0.x+offsetX ,point_circle_1_0.y+offsetY,paintForLine);
                PointF point_circle_1_3 = getCirePoint(circles[1],circles[3]);
                canvas.drawLine(circles[1].x+offsetX,circles[1].y+offsetY ,point_circle_1_3.x +offsetX,point_circle_1_3.y+offsetY,paintForLine);
                break;
            case 2:
                PointF point_circle_2_0 = getCirePoint(circles[2],circles[0]);
                canvas.drawLine(circles[2].x+offsetX,circles[2].y+offsetY ,point_circle_2_0.x+offsetX ,point_circle_2_0.y+offsetY,paintForLine);
                PointF point_circle_2_3 = getCirePoint(circles[2],circles[3]);
                canvas.drawLine(circles[2].x+offsetX,circles[2].y +offsetY,point_circle_2_3.x+offsetX ,point_circle_2_3.y+offsetY,paintForLine);
                break;
            case 3:
                PointF point_circle_3_1 = getCirePoint(circles[3],circles[1]);
                canvas.drawLine(circles[3].x+offsetX,circles[3].y+offsetY ,point_circle_3_1.x+offsetX ,point_circle_3_1.y+offsetY,paintForLine);
                PointF point_circle_3_2 = getCirePoint(circles[3],circles[2]);
                canvas.drawLine(circles[3].x+offsetX,circles[3].y+offsetY ,point_circle_3_2.x+offsetX,point_circle_3_2.y+offsetY,paintForLine);
                break;
        }
    }
    //找到 point1 为圆心的圆与线段的交点
    private PointF getCirePoint(PointF point1 , PointF point2){
        PointF point = new PointF();
        Line line = new Line(point1.x,point1.y ,point2.x ,point2.y);
        if(line.isX){
            point.x = point1.x;
            if(point1.y<point2.y){
                point.y = point1.y+imageBigWidth/2;
            }else{
                point.y = point1.y-imageBigWidth/2;
            }
        }else if(line.isY){
            point.y = point1.y;
            if(point1.x<point2.x){
                point.x = x+imageBigWidth/2;
            }else{
                point.x = x-imageBigWidth/2;
            }
        }else{
            float bigR = imageBigWidth/2;
            float dx = (float)Math.sqrt(bigR*bigR*line.B*line.B/(line.B*line.B+line.A*line.A));
            if(point1.x<point2.x){
                point.x = point1.x+dx;
            }else{
                point.x = point1.x-dx;
            }
            point.y = -line.C/line.B-line.A*point.x/line.B;
        }
        return point;
    }
    private Bitmap getScaleBitmap(Bitmap bitmap) {
        int left = (int)((onTouchX-imageLeftPadding-bigImagescaleSize/2)/scale);
        int top = (int)((onTouchY-imageTopPadding -bigImagescaleSize/2)/scale);
        //依据原有的图片丶创建一个新的图片   格式是：Config.ARGB_4444
        Bitmap bt = Bitmap.createBitmap((int)(bigImagescaleSize/scale),(int)(bigImagescaleSize/scale), Bitmap.Config.ARGB_4444);
        //创建一个画布
        Canvas canvas = new Canvas(bt);
        //创建一个画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //画笔的颜色
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //求得圆的半径
        canvas.drawCircle((int)(bigImagescaleSize/(scale*2)), (int)(bigImagescaleSize/(scale*2)), (int)(bigImagescaleSize/(scale*2)), paint);
        //重置画笔
        paint.reset();
        //调用截图图层的方法
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //画图片
        canvas.drawBitmap(bitmap, -left, -top, paint);
        return bt;
    }
    private boolean checkIsJiaozhenging(){
        if(circles==null){
            return false;
        }
        for(int i=0;i<circles.length;i++){
            if(circles[i].x!=points[i].x||circles[i].y!=points[i].y){
                return true;
            }
        }
        return false;
    }

    public void setImageBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
        JIAOZHENG_STATUS = JIAOZHENG_UNSTART;
        setData();
    }
    public void setJiaozhenStart(){
        if(jiaoZhenListener!=null){
            jiaoZhenListener.jiaoZhengStart();
        }
        JIAOZHENG_STATUS = JIAOZHENG_IS_DOING;
    }
    public void setJiaozhengEnd(){
        JIAOZHENG_STATUS = JIAOZHENG_ENDED;
        if(jiaoZhenListener!=null){
            jiaoZhenListener.jiaoZhengEnd();
        }
        invalidate();
    }
    public void setJiaoZhenListener(JiaoZhenListener jiaoZhenListener) {
        this.jiaoZhenListener = jiaoZhenListener;
    }

    public interface JiaoZhenListener{
        public void isMovedJiaoZhenCircles(boolean isMoved);//矫正四个点是否移动过。移动过后，可以执行矫正
        public void jiaoZhengStart();//矫正开始
        public void jiaoZhengEnd();//矫正结束
    }
}
