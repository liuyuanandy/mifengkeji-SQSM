package com.beeinc.SQSB.views;

import static org.opencv.imgproc.Imgproc.COLOR_RGBA2BGRA;

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
import android.widget.EditText;
import android.widget.TextView;

import com.beeinc.SQSB.activity.CutPictureActivity;
import com.beeinc.SQSB.activity.DuiHuaActivity;
import com.beeinc.SQSB.activity.MirroPictureActivity;
import com.beeinc.SQSB.scale.ScreenUtil;
import com.beeinc.SQSB.util.common;
import com.test.RemapHelper;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.ArrayList;

public class ImageCutView extends View {

    private Context context;
    private Bitmap bitmap;//bitmap
    private Bitmap previewBitmap;//预览bitmap
    private float bigImagescaleSize;//放大部分缩放区间的尺寸
    private float bigImageScale = 4;//放大部分缩放倍数

    private int width;
    private int height;
    private int bitmapWidth;//图片实际宽度
    private int bitmapHeight;//图片实际高度
    private int imageWidth;//图片显示宽度
    private int imageHeight;//图片显示高度
    private int imageBigWidth;
    private int imageBigHeight;
    private Paint paintForImage;
    private Paint paintForLine;
    private Paint paintForBig;

    private int topPadding=100;//图片显示区上边距
    private int bottomPadding=100;//图片显示区下边距
    private int leftPadding = 100;//图片显示区左边距
    private int rightPadding = 100;//图片显示区右边距


    private int imageLeftPadding;//图片左边距
    private int imageTopPadding;//图片上边距

    private  float[] points = new float[8];
    private PointF[] circles = new PointF[8];
    private int position;//点击的拖动点位置

    private float initX;//按下时x坐标
    private float initY;//按下时y坐标

    private float x;//移动时候的坐标
    private float y;//移动时候的坐标
    private boolean isOnTouch;//是否响应触摸事件
    private int circleR;//触摸圆点半径
    private float scale;//图片显示尺寸与原图尺寸的比例
    private int type = 0;// 0 未操作 ;1 操作中 2 已点击预览
    private boolean isDown;//手指是否按下

    private TextView mBindView;
    private EditText et_width;
    private EditText et_heihgt;

    private float onTouchX;
    private float onTouchY;

    private final int[] sizes= new int[]{500,600,800,1000,1200};

    private boolean isDenyCut;//不能裁剪
    private int DEFAULT_MAX_WITH = 2600;//默认最大边长
    public ImageCutView(Context context) {
        super(context);
    }

    public ImageCutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context =context;

        init();
    }

    public ImageCutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void init(){
        paintForImage = new Paint();
        paintForImage.setAntiAlias(true);

        paintForLine = new Paint();
        paintForLine.setAntiAlias(true);
        paintForLine.setStrokeWidth(common.Dp2Px(getContext(), 2));
        paintForLine.setColor(Color.parseColor("#ffffff"));

        paintForBig = new Paint();
        paintForBig.setAntiAlias(true);
        paintForBig.setStrokeWidth(common.Dp2Px(getContext(), 2));
        paintForBig.setStyle(Paint.Style.STROKE);
        paintForBig.setColor(Color.parseColor("#60000000"));
        circleR = common.Dp2Px(context,5 );
        bigImagescaleSize = 4*circleR;
        imageBigHeight =imageBigWidth = (int)(bigImagescaleSize*bigImageScale);
    }
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //图片显示区域宽高
        if(bitmap==null){
            return;
        }
        //绘制图片
        drawBitmap(canvas);
        if(type!=2){//预览时不绘制线段和圆
            //绘制线段
            drawLines(canvas);
            //绘制圆
            drawCircles(canvas);
        }
        if(isDown&&type==1){
            drawScalePart(canvas);
        }
    }
    public void onMeasure(int widthMeasureSpect,int heightMeasureSpec){
        super.onMeasure(widthMeasureSpect, heightMeasureSpec);
    }


    //圆顺序：从左到右，从上到下
    private void initCirclesPosition(){
        for(int i=0;i<circles.length;i++){
            circles[i] = new PointF();
        }
        circles[0].x = points[0];
        circles[0].y = points[1];

        circles[1].x = (points[0]+points[2])/2;
        circles[1].y = (points[1]+points[3])/2;

        circles[2].x = points[2];
        circles[2].y = points[3];

        circles[3].x = (points[0]+points[4])/2;
        circles[3].y = (points[1]+points[5])/2;

        circles[4].x = (points[2]+points[6])/2;
        circles[4].y = (points[3]+points[7])/2;

        circles[5].x = points[4];
        circles[5].y = points[5];

        circles[6].x = (points[4]+points[6])/2;
        circles[6].y = (points[5]+points[7])/2;

        circles[7].x = points[6];
        circles[7].y = points[7];
    }
    public void drawBitmap(Canvas canvas){
        if(type==2){
            canvas.drawBitmapMesh(previewBitmap, 1, 1, points, 0, null, 0, paintForImage);
        }else{
            canvas.drawBitmapMesh(bitmap, 1, 1, points, 0, null, 0, paintForImage);
        }
    }
    public void drawLines(Canvas canvas){
        canvas.drawLine(circles[0].x,circles[0].y ,circles[2].x,circles[2].y, paintForLine);
        canvas.drawLine(circles[2].x,circles[2].y ,circles[7].x,circles[7].y, paintForLine);
        canvas.drawLine(circles[7].x,circles[7].y ,circles[5].x,circles[5].y, paintForLine);
        canvas.drawLine(circles[5].x,circles[5].y ,circles[0].x,circles[0].y, paintForLine);
    }
    public void drawCircles(Canvas canvas){
        if(bitmap!=null){
            for(int i=0;i<circles.length;i++){
                canvas.drawCircle(circles[i].x,circles[i].y , circleR,paintForLine );
            }
        }
    }
    public boolean onTouchEvent(MotionEvent event){
//        super.onTouchEvent(event);
        if(isDenyCut){//禁止裁剪
            return true;
        }
        if(type==2){//预览中不能操错
            return true;
        }
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
                    type=1;
                    if(mBindView!=null){
                        mBindView.setVisibility(View.VISIBLE);
                    }
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
                            move2(circles[2],offsetX,offsetY);
                            break;
                        case 3:
                            move3(offsetX,offsetY);
                            break;
                        case 4:
                            move4(offsetX,offsetY);
                            break;
                        case 5:
                            move5(circles[5],offsetX,offsetY);
                            break;
                        case 6:
                            move6(offsetX,offsetY);
                            break;
                        case 7:
                            move7(circles[7],offsetX,offsetY);
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
                if(et_width!=null&&et_heihgt!=null){
                    setWidthAndHeight();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDown = false;
                invalidate();
                break;
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
        Line line_2_5 = new Line(circles[2].x,circles[2].y ,circles[5].x ,circles[5].y);
        Line line_2_7 = new Line(circles[2].x,circles[2].y, circles[7].x ,circles[7].y);
        Line line_5_7 = new Line(circles[5].x ,circles[5].y,circles[7].x ,circles[7].y);

        float endX;
        float endY;
        endX = circles[0].x+offsetX;
        endY = circles[0].y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0]&&endX<=points[2]&&endY>=points[1]&&endY<=points[5]){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX<=circles[2].x-4*circleR&&endY<=circles[5].y-4*circleR){
            isInPoint = true;
        }
        //第三步 判断是否越过对角线和是否越过对角边
        boolean isInLine = false;
        if(line_2_5.calculate(endX, endY)>=4*circleR&&line_2_7.calculate(endX, endY)>4*circleR&&line_5_7.calculate(endX, endY)>4*circleR){
            isInLine = true;
        }

        if(isInDisplayArea&&isInPoint&&isInLine){
            circles[0].x = endX;
            circles[0].y = endY;
            circles[1].x = (circles[0].x+circles[2].x)/2;
            circles[1].y = (circles[0].y+circles[2].y)/2;
            circles[3].x = (circles[0].x+circles[5].x)/2;
            circles[3].y = (circles[0].y+circles[5].y)/2;
        }
    }
    //移动点的位置1
    private void move1(float offsetX, float offsetY){
        if(offsetY>=0){//向下拖动
            PointF a= new PointF();
            a.x = circles[0].x;
            a.y = circles[0].y+offsetY;

            PointF b =new PointF();
            b.x = circles[2].x;
            b.y = circles[2].y+offsetY;

            int distance_0_5 = getTwoPointsDistance(a,circles[5]);
            int distance_2_7 = getTwoPointsDistance(b,circles[7]);
            Line line_5_7 = new Line(circles[5].x,circles[5].y,circles[7].x,circles[7].y);
            Line line_2_5 = new Line(circles[2].x,circles[2].y+offsetY,circles[5].x,circles[5].y);
            Line line_0_7 = new Line(circles[0].x,circles[0].y+offsetY,circles[7].x,circles[7].y);

            if(distance_0_5>=4*circleR&&distance_2_7>=4*circleR
                    &&line_5_7.calculate(a.x,a.y)>=4*circleR
                    &&line_5_7.calculate(b.x,b.y)>=4*circleR
                    &&line_2_5.calculate(a.x,a.y)>=4*circleR
                    &&line_0_7.calculate(b.x,b.y)>=4*circleR){
                circles[0].y= circles[0].y+offsetY;
                circles[1].y=circles[1].y+offsetY;
                circles[2].y = circles[2].y+offsetY;
            }
        }else{//向上拖动
            if(circles[0].y+offsetY<points[1]||circles[2].y+offsetY<points[1]){
                if(circles[0].y<=circles[2].y){
                    offsetY = points[1]-circles[0].y;
                }else{
                    offsetY = points[1]-circles[2].y;
                }
            }
            circles[0].y= circles[0].y+offsetY;
            circles[1].y= circles[1].y+offsetY;
            circles[2].y= circles[2].y+offsetY;
        }
        circles[3].y = (circles[0].y+circles[5].y)/2;
        circles[4].y = (circles[2].y+circles[7].y)/2;

    }
    //移动点的位置2
    private void move2(PointF circle, float offsetX, float offsetY){
        Line line_0_7 = new Line(circles[0].x,circles[0].y ,circles[7].x ,circles[7].y);
        Line line_0_5 = new Line(circles[0].x,circles[0].y, circles[5].x ,circles[5].y);
        Line line_5_7 = new Line(circles[5].x ,circles[5].y,circles[7].x ,circles[7].y);
        float endX;
        float endY;
        endX = circle.x+offsetX;
        endY = circle.y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0]&&endX<=points[2]&&endY>=points[1]&&endY<=points[5]){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX>circles[0].x+4*circleR&&endY<circles[7].y-4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
       if(line_0_7.calculate(endX, endY)>=4*circleR&&line_0_5.calculate(endX, endY)>=4*circleR&&line_5_7.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circle.x = endX;
            circle.y = endY;
            circles[1].x = (circles[0].x+circles[2].x)/2;
            circles[1].y = (circles[0].y+circles[2].y)/2;
            circles[4].x = (circles[2].x+circles[7].x)/2;
            circles[4].y = (circles[2].y+circles[7].y)/2;
        }
    }
    //移动点的位置3
    private void move3(float offsetX, float offsetY){
        if(offsetX>=0){//向右拖动
            PointF a= new PointF();
            a.x = circles[0].x+offsetX;
            a.y = circles[0].y;

            PointF b =new PointF();
            b.x = circles[5].x+offsetX;
            b.y = circles[5].y;

            int distance_0_2 = getTwoPointsDistance(a,circles[2]);
            int distance_5_7 = getTwoPointsDistance(b,circles[7]);
            Line line_2_7 = new Line(circles[2].x,circles[2].y,circles[7].x,circles[7].y);
            Line line_2_5 = new Line(circles[2].x,circles[2].y+offsetY,circles[5].x,circles[5].y);
            Line line_0_7 = new Line(circles[0].x,circles[0].y+offsetY,circles[7].x,circles[7].y);

            if(distance_0_2>=4*circleR
                    &&distance_5_7>=4*circleR
                    &&line_2_7.calculate(a.x,a.y)>=4*circleR
                    &&line_2_7.calculate(b.x,b.y)>=4*circleR
                    &&line_2_5.calculate(circles[0].x+offsetX,circles[0].y)>=4*circleR
                    &&line_0_7.calculate(circles[5].x+offsetX,circles[5].y)>=4*circleR
                    ){
                circles[0].x= circles[0].x+offsetX;
                circles[3].x=circles[3].x+offsetX;
                circles[5].x = circles[5].x+offsetX;
            }
        }else{//向左拖动
            if(circles[0].x+offsetX<points[0]||circles[5].x+offsetX<points[0]){
                if(circles[0].x<=circles[5].x){
                    offsetX = points[0]-circles[0].x;
                }else{
                    offsetX = points[0]-circles[5].x;
                }
            }
            circles[0].x= circles[0].x+offsetX;
            circles[3].x=circles[3].x+offsetX;
            circles[5].x = circles[5].x+offsetX;
        }
        circles[1].x = (circles[0].x+circles[2].x)/2;
        circles[6].x = (circles[5].x+circles[7].x)/2;

    }
    //移动点的位置4
    private void move4(float offsetX, float offsetY){
        if(offsetX>=0){//向右拖动
            if(circles[2].x+offsetX>points[2]||circles[7].x+offsetX>points[2]){
                if(circles[2].x>=circles[7].x){
                    offsetX = points[2]-circles[2].x;
                }else{
                    offsetX = points[2]-circles[7].x;
                }
            }
            circles[2].x= circles[2].x+offsetX;
            circles[4].x=circles[4].x+offsetX;
            circles[7].x = circles[7].x+offsetX;
        }else{//向左拖动
            PointF a= new PointF();
            a.x = circles[2].x+offsetX;
            a.y = circles[2].y;

            PointF b =new PointF();
            b.x = circles[7].x+offsetX;
            b.y = circles[7].y;

            int distance_0_2 = getTwoPointsDistance(a,circles[0]);
            int distance_5_7 = getTwoPointsDistance(b,circles[5]);
            Line line_0_5 = new Line(circles[0].x,circles[0].y,circles[5].x,circles[5].y);
            Line line_2_5 = new Line(circles[2].x,circles[2].y+offsetY,circles[5].x,circles[5].y);
            Line line_0_7 = new Line(circles[0].x,circles[0].y+offsetY,circles[7].x,circles[7].y);
            if(distance_0_2>=4*circleR
                    &&distance_5_7>=4*circleR
                    &&line_0_5.calculate(a.x,a.y)>=4*circleR
                    &&line_0_5.calculate(b.x,b.y)>=4*circleR
                    &&line_2_5.calculate(circles[7].x+offsetX,circles[7].y)>=4*circleR
                    &&line_0_7.calculate(circles[5].x+offsetX,circles[5].y)>=4*circleR
                ){
                circles[2].x= circles[2].x+offsetX;
                circles[4].x=circles[4].x+offsetX;
                circles[7].x = circles[7].x+offsetX;
            }
        }
        circles[1].x = (circles[0].x+circles[2].x)/2;
        circles[6].x = (circles[5].x+circles[7].x)/2;

    }
    //移动点的位置5
    private void move5(PointF circle, float offsetX, float offsetY){
        Line line_0_7 = new Line(circles[0].x,circles[0].y ,circles[7].x ,circles[7].y);
        Line line_0_2 = new Line(circles[0].x,circles[0].y, circles[2].x ,circles[2].y);
        Line line_2_7 = new Line(circles[2].x ,circles[2].y,circles[7].x ,circles[7].y);
        float endX;
        float endY;
        endX = circle.x+offsetX;
        endY = circle.y+offsetY;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0]&&endX<=points[2]&&endY>=points[1]&&endY<=points[5]){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX<=circles[7].x-4*circleR&&endY>=circles[0].y+4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
       if(line_0_7.calculate(endX, endY)>=4*circleR&&line_0_2.calculate(endX, endY)>=4*circleR&&line_2_7.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circle.x = endX;
            circle.y = endY;
            circles[3].x = (circles[0].x+circles[5].x)/2;
            circles[3].y = (circles[0].y+circles[5].y)/2;
            circles[6].x = (circles[5].x+circles[7].x)/2;
            circles[6].y = (circles[5].y+circles[7].y)/2;
        }
    }
    //移动点的位置6
    private void move6(float offsetX, float offsetY){
        if(offsetY>=0){//向下拖动
            if(circles[5].y+offsetY>points[5]||circles[7].y+offsetY>points[5]){
                if(circles[5].y>=circles[7].y){
                    offsetY = points[5]-circles[5].y;
                }else{
                    offsetY = points[5]-circles[7].y;
                }
            }
            circles[5].y= circles[5].y+offsetY;
            circles[6].y=circles[6].y+offsetY;
            circles[7].y = circles[7].y+offsetY;
        }else{//向上拖动
            PointF a= new PointF();
            a.x = circles[5].x;
            a.y = circles[5].y+offsetY;

            PointF b =new PointF();
            b.x = circles[7].x;
            b.y = circles[7].y+offsetY;

            int distance_0_5 = getTwoPointsDistance(a,circles[0]);
            int distance_2_7 = getTwoPointsDistance(b,circles[2]);
            Line line_0_2 = new Line(circles[0].x,circles[0].y,circles[2].x,circles[2].y);
            Line line_2_5 = new Line(circles[2].x,circles[2].y+offsetY,circles[5].x,circles[5].y);
            Line line_0_7 = new Line(circles[0].x,circles[0].y+offsetY,circles[7].x,circles[7].y);

            if(distance_0_5>=4*circleR
                    &&distance_2_7>=4*circleR
                    &&line_0_2.calculate(circles[5].x,circles[5].y)>=4*circleR
                    &&line_0_2.calculate(circles[7].x,circles[7].y)>=4*circleR
                    &&line_2_5.calculate(circles[7].x,circles[7].y+offsetY)>=4*circleR
                    &&line_0_7.calculate(circles[5].x,circles[5].y+offsetY)>=4*circleR){
                circles[5].y= circles[5].y+offsetY;
                circles[6].y=circles[6].y+offsetY;
                circles[7].y = circles[7].y+offsetY;
            }
        }
        circles[3].y = (circles[0].y+circles[5].y)/2;
        circles[4].y = (circles[2].y+circles[7].y)/2;
    }
    //移动点的位置7
    private void move7(PointF circle, float offsetX, float offsetY){
        Line line_2_5 = new Line(circles[2].x,circles[2].y ,circles[5].x ,circles[5].y);
        Line line_0_2 = new Line(circles[0].x,circles[0].y, circles[2].x ,circles[2].y);
        Line line_0_5 = new Line(circles[0].x ,circles[0].y,circles[5].x ,circles[5].y);
        float endX;
        float endY;
        endX = circle.x+offsetX;
        endY = circle.y+offsetY;
        boolean isRightLine1 = false;
        boolean isBootomLine2 = false;
        //第一步 判断是否离开图片显示区
        boolean isInDisplayArea = false;
        if(endX>=points[0]&&endX<=points[2]&&endY>=points[1]&&endY<=points[5]){
            isInDisplayArea = true;
        }
        //第二步 判断相邻点是否越界
        boolean isInPoint = false;
        if(endX>=circles[5].x+4*circleR&&endY>=circles[2].y+4*circleR){
            isInPoint = true;
        }
        //第三部 判断是否越过对角线
        boolean isInLine = false;
       if(line_2_5.calculate(endX, endY)>=4*circleR&&line_0_2.calculate(endX, endY)>=4*circleR&&line_0_5.calculate(endX, endY)>=4*circleR){
            isInLine = true;
        }
        if(isInDisplayArea&&isInPoint&&isInLine){
            circle.x = endX;
            circle.y = endY;
            circles[4].x = (circles[7].x+circles[2].x)/2;
            circles[4].y = (circles[7].y+circles[2].y)/2;
            circles[6].x = (circles[5].x+circles[7].x)/2;
            circles[6].y = (circles[5].y+circles[7].y)/2;
        }
    }
    private class Line{
        //假设直线为一元一次方程 ：AX+By+C = 0
        private boolean isY;//y值固定  直线 y = NY
        private float Y;//Y值固定时Y的值
        private boolean isX;//x值固定  直线 x = N;
        private float X;//X值固定时X的值
        private float A;
        private float B;
        private float C;
        public Line(float x1,float y1,float x2,float y2){
            if(y1==y2){
                isY = true;
                Y = y1;
                A =0;
                B = 1;
                C = -Y;
            }else if(x1==x2){
                isX = true;
                X = x1;
                A=1;
                B=0;
                C = -X;
            }else{
                //假设 y = ax +n;
                float a = (y1-y2)/(x1-x2);
                float n = y1-a*x1;
                A = a;
                B = -1;
                C = n;
            }
        }
        public float calculate(float x,float y){
            float distance = (float) Math.abs((A*x+B*y+C)/Math.sqrt(A*A+B*B));
            return distance ;
        }
    }
    public void setBitmap(Bitmap bitmap, int leftPadding, int topPadding, int rightPadding, int bottomPadding){
        width = ScreenUtil.SCREEN_THIS_W;
        height = ScreenUtil.SCREEN_THIS_H;
        this.bitmap = bitmap;
        this.leftPadding = leftPadding;
        this.topPadding = topPadding;
        this.rightPadding = rightPadding;
        this.bottomPadding = bottomPadding;
        setInitInfo();
    }
    private void setInitInfo(){
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        setImagePosition();
        initCirclesPosition();
        if(et_width!=null&&et_heihgt!=null){
            setWidthAndHeight();
        }
        invalidate();
    }
    private void setRotateInfo(){
        if(type==2){//已点击预览
            bitmapWidth = previewBitmap.getWidth();
            bitmapHeight = previewBitmap.getHeight();
        }else{
            bitmapWidth = bitmap.getWidth();
            bitmapHeight = bitmap.getHeight();
        }
        setImagePosition();
        initCirclesPosition();
        if(et_width!=null&&et_heihgt!=null){
            setWidthAndHeight();
        }
        invalidate();
    }
    private int getRemapedWidth(){
        float h;
        float w;
        w = (circles[2].x-circles[0].x)/scale;
        h = (circles[2].y-circles[0].y)/scale;
        int widthTop = (int)Math.sqrt(w*w+h*h);
        w = (circles[7].x-circles[5].x)/scale;
        h = (circles[7].y-circles[5].y)/scale;
        int widthbottom = (int)Math.sqrt(w*w+h*h);
        int width = widthTop>=widthbottom?widthTop:widthbottom;
        return width;
    }
    private int getRemapedHeight(){
        float h;
        float w;
        w = (circles[5].x-circles[0].x)/scale;
        h = (circles[5].y-circles[0].y)/scale;
        int heightLeft =(int)Math.sqrt(w*w+h*h);
        w = (circles[7].x-circles[2].x)/scale;
        h = (circles[7].y-circles[2].y)/scale;
        int heightRight = (int)Math.sqrt(w*w+h*h);
        int height = heightLeft>=heightRight?heightLeft:heightRight;
        return height;
    }
    private void  setPreviewInfo(){
        bitmapWidth = previewBitmap.getWidth();
        bitmapHeight = previewBitmap.getHeight();
        setImagePosition();
        initCirclesPosition();
        invalidate();
    }
    private void setImagePosition(){
        int width_ = width - leftPadding-rightPadding;
        int height_ = height - topPadding-bottomPadding;
        if((float)bitmapWidth/bitmapHeight>=(float)width_/height_){//图片的宽度比例大于图片显示区域的宽度比例，图片上下居中
            scale = (float) width_/bitmapWidth;
            imageWidth = width_;
            imageHeight = (int)(bitmapHeight*scale);
            imageLeftPadding = leftPadding;
            imageTopPadding = topPadding+(height_-imageHeight)/2;
        }else{//图片左右居中
            scale = (float) height_/bitmapHeight;
            imageHeight = height_;
            imageWidth = (int)(bitmapWidth*scale);
            imageLeftPadding = leftPadding+(width_-imageWidth)/2;
            imageTopPadding = topPadding;
        }
        //图片四个顶点
        points[0] = imageLeftPadding;
        points[1] = imageTopPadding;

        points[2] = imageLeftPadding+imageWidth;
        points[3] = imageTopPadding;

        points[4] = imageLeftPadding;
        points[5] = imageTopPadding+imageHeight;

        points[6] = imageLeftPadding+imageWidth;
        points[7] = imageTopPadding+imageHeight;
    }
    public void bindView(TextView mBindView, EditText et_width, EditText et_height){
        this.mBindView = mBindView;
        this.et_width = et_width;
        this.et_heihgt = et_height;
    }
    public void preViewClick(){
        switch (type){
            case 1:
                mBindView.setText("再次矫正");
                type = 2;
                getJiaozhenBitmap();
                if(et_width!=null&&et_heihgt!=null){
                    setWidthAndHeight();
                }
                setPreviewInfo();
                break;
            case 2:
                mBindView.setText("预览");
                mBindView.setVisibility(View.GONE);
                type = 0;
                setInitInfo();
                break;
        }
    }
    private void getJiaozhenBitmap(){
        Mat mat = new Mat();
        Utils.bitmapToMat(bitmap,mat);
        //获取映射前坐标
        ArrayList<Point> srcPoints = new ArrayList<Point>();
        srcPoints.add(new Point((circles[0].x-imageLeftPadding)/scale,(circles[0].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[2].x-imageLeftPadding)/scale,(circles[2].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[5].x-imageLeftPadding)/scale,(circles[5].y-imageTopPadding)/scale));
        srcPoints.add(new Point((circles[7].x-imageLeftPadding)/scale,(circles[7].y-imageTopPadding)/scale));
        //获取映射后的坐标
        float h;
        float w;
        w = (circles[2].x-circles[0].x)/scale;
        h = (circles[2].y-circles[0].y)/scale;
        int widthTop = (int)Math.sqrt(w*w+h*h);
        w = (circles[7].x-circles[5].x)/scale;
        h = (circles[7].y-circles[5].y)/scale;
        int widthbottom = (int)Math.sqrt(w*w+h*h);
        int width = widthTop>=widthbottom?widthTop:widthbottom;
        w = (circles[5].x-circles[0].x)/scale;
        h = (circles[5].y-circles[0].y)/scale;
        int heightLeft =(int)Math.sqrt(w*w+h*h);
        w = (circles[7].x-circles[2].x)/scale;
        h = (circles[7].y-circles[2].y)/scale;
        int heightRight = (int)Math.sqrt(w*w+h*h);
        int height = heightLeft>=heightRight?heightLeft:heightRight;
        ArrayList<Point> dstPoints = new ArrayList<Point>();
//        dstPoints.add(new Point(0,0));
//        dstPoints.add(new Point(mat.cols(),0 ));
//        dstPoints.add(new Point(0,mat.rows()));
//        dstPoints.add(new Point(mat.cols(),mat.rows()));

        dstPoints.add(new Point(0,0));
        dstPoints.add(new Point(width,0 ));
        dstPoints.add(new Point(0,height));
        dstPoints.add(new Point(width,height));
        Mat warp_dst = new Mat();
        Mat warp_x = Converters.vector_Point_to_Mat(srcPoints, CvType.CV_32F);
        Mat warp_y = Converters.vector_Point_to_Mat(dstPoints, CvType.CV_32F);
        Mat warp_mat = Imgproc.getPerspectiveTransform(warp_x,warp_y);
        Imgproc.warpPerspective(mat, warp_dst ,warp_mat, new Size(new Point(width,height)));
//        Imgproc.warpPerspective(mat, warp_dst ,warp_mat, mat.size());
        if(previewBitmap!=null){
            previewBitmap.recycle();
            previewBitmap = null;
        }
        previewBitmap = Bitmap.createBitmap(warp_dst.width(), warp_dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(warp_dst, previewBitmap);
        mat.release();
        warp_dst.release();
        warp_x.release();
        warp_y.release();
        mat = null;
        warp_dst = null;
        warp_x = null;
        warp_y = null;
    }

    /**
     * 旋转 每次逆时针旋转90度。
     */
    public void rotate(){
        if(type==0){
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            Mat dst = new Mat();
            RemapHelper.setRotate(mat.getNativeObjAddr(), dst.getNativeObjAddr(),90);
            bitmap.recycle();
            bitmap = null;
            bitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, bitmap);
            mat.release();
            dst.release();
            mat=null;
            setRotateInfo();
        }
        if(type==2){
            Mat mat = new Mat();
            Utils.bitmapToMat(previewBitmap, mat);
            Mat dst = new Mat();
            RemapHelper.setRotate(mat.getNativeObjAddr(), dst.getNativeObjAddr(),90);
            previewBitmap.recycle();
            previewBitmap = null;
            previewBitmap = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(dst, previewBitmap);
            mat.release();
            dst.release();
            mat=null;
            setRotateInfo();
        }
    }
    public void saveBitmap(String path){
        Bitmap b=bitmap;
        if(getContext() instanceof MirroPictureActivity){
            b = bitmap;
        }else if(getContext() instanceof CutPictureActivity){
            if(type==0){//未操作
                b= bitmap;
            }else if(type==1){//操作中
                preViewClick();
                b = previewBitmap;
            }else if(type==2){//正在预览
                b =previewBitmap;
            }
        }else if(getContext() instanceof MirroPictureActivity){
            if(type==0){//未操作
                b= bitmap;
            }else if(type==1){//操作中
                preViewClick();
                b = previewBitmap;
            }else if(type==2){//正在预览
                b =previewBitmap;
            }
        }else if(getContext() instanceof DuiHuaActivity){
            if(type==0){//未操作
                b= bitmap;
            }else if(type==1){//操作中
                preViewClick();
                b = previewBitmap;
            }else if(type==2){//正在预览
                b =previewBitmap;
            }
        }
        Mat mat= new Mat();
        Utils.bitmapToMat(b,mat);
        Mat temp = new Mat();
        Imgproc.cvtColor(mat, temp, COLOR_RGBA2BGRA);
        Imgcodecs.imwrite(path,temp);
        temp.release();
        mat.release();
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
//        float offsetX = common.Dp2Px(getContext(), -60);
//        float offsetY = common.Dp2Px(getContext(), -60);
//        if(onTouchX-offsetX<0){
//            offsetX=-onTouchX;
//        }
//        if(onTouchY-offsetY<0){
//            offsetY=-onTouchY;
//        }

        Bitmap bt =getBitmap();
        Bitmap big = Bitmap.createScaledBitmap(bt, imageBigWidth,imageBigHeight,true);
        canvas.drawBitmap(big, onTouchX+offsetX-imageBigWidth/2, onTouchY+offsetY-imageBigHeight/2, paintForImage);
        canvas.drawCircle(onTouchX+offsetX, onTouchY+offsetY , imageBigWidth/2, paintForBig);
        canvas.drawCircle(onTouchX+offsetX, onTouchY+offsetY,circleR,paintForLine);
        switch(position){
            case 0:
                PointF point_circle_0_2 = getCirePoint(circles[0],circles[2]);
                canvas.drawLine(circles[0].x+offsetX,circles[0].y+offsetY ,point_circle_0_2.x+offsetX ,point_circle_0_2.y+offsetY,paintForLine);
                PointF point_circle_0_5 = getCirePoint(circles[0],circles[5]);
                canvas.drawLine(circles[0].x+offsetX,circles[0].y +offsetY,point_circle_0_5.x +offsetX,point_circle_0_5.y+offsetY,paintForLine);
                break;
            case 2:
                PointF point_circle_2_0 = getCirePoint(circles[2],circles[0]);
                canvas.drawLine(circles[2].x+offsetX,circles[2].y +offsetY,point_circle_2_0.x+offsetX ,point_circle_2_0.y+offsetY,paintForLine);
                PointF point_circle_2_7 = getCirePoint(circles[2],circles[7]);
                canvas.drawLine(circles[2].x+offsetX,circles[2].y+offsetY ,point_circle_2_7.x +offsetX,point_circle_2_7.y+offsetY,paintForLine);
                break;
            case 5:
                PointF point_circle_5_0 = getCirePoint(circles[5],circles[0]);
                canvas.drawLine(circles[5].x+offsetX,circles[5].y+offsetY ,point_circle_5_0.x+offsetX ,point_circle_5_0.y+offsetY,paintForLine);
                PointF point_circle_5_7 = getCirePoint(circles[5],circles[7]);
                canvas.drawLine(circles[5].x+offsetX,circles[5].y +offsetY,point_circle_5_7.x+offsetX ,point_circle_5_7.y+offsetY,paintForLine);
                break;
            case 7:
                PointF point_circle_7_2 = getCirePoint(circles[7],circles[2]);
                canvas.drawLine(circles[7].x+offsetX,circles[7].y+offsetY ,point_circle_7_2.x+offsetX ,point_circle_7_2.y+offsetY,paintForLine);
                PointF point_circle_7_5 = getCirePoint(circles[7],circles[5]);
                canvas.drawLine(circles[7].x+offsetX,circles[7].y+offsetY ,point_circle_7_5.x+offsetX,point_circle_7_5.y+offsetY,paintForLine);
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
    private Bitmap getBitmap() {
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
    private void setWidthAndHeight(){
        int remapWidth = getRemapedWidth();
        int remapHeight = getRemapedHeight();

        if(remapWidth>remapHeight){
            et_width.setText(""+DEFAULT_MAX_WITH);
            et_heihgt.setText(""+remapHeight*DEFAULT_MAX_WITH/remapWidth);
        }else{
            et_width.setText(""+remapWidth*DEFAULT_MAX_WITH/remapHeight);
            et_heihgt.setText(""+DEFAULT_MAX_WITH);
        }
    }
    public void setDenyCut(boolean denyCut) {
        isDenyCut = denyCut;
    }
}
