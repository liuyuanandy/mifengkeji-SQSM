package com.beeinc.mylibrary.bean;

public class Line {
    //假设直线为一元一次方程 ：AX+By+C = 0
    public boolean isY;//y值固定  直线 y = NY
    private float Y;//Y值固定时Y的值
    public boolean isX;//x值固定  直线 x = N;
    private float X;//X值固定时X的值
    public float A;
    public float B;
    public float C;

    public Line(float x1, float y1, float x2, float y2){
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
