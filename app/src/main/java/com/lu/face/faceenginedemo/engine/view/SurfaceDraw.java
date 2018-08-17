package com.lu.face.faceenginedemo.engine.view;

import com.THID.FaceSDK.THIDFaceRect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
* @author liulv
* @version create：2017年4月24日 上午11:17:58
* @company 北京海鑫科金高科技股份有限公司
* 
*/
public class SurfaceDraw extends SurfaceView {
	
	private static final String TAG = "SurfaceDraw";
	protected SurfaceHolder surfacehldr_;

	public SurfaceDraw(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		 init();
	}

	public SurfaceDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		 init();
	}

	public SurfaceDraw(Context context) {
		super(context);
        init();
	}
	
	private void init(){
		surfacehldr_ = getHolder();
		surfacehldr_.setFormat(PixelFormat.TRANSPARENT);
		setZOrderOnTop(true);
	}
	
	private void clearDraw() {
		Canvas canvas = surfacehldr_.lockCanvas();
		// 清屏
		Paint p = new Paint();
		p.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		canvas.drawPaint(p);
		p.setXfermode(new PorterDuffXfermode(Mode.SRC));
		surfacehldr_.unlockCanvasAndPost(canvas);
	}
	
	
	public void drawFaceRect(int faceNum,THIDFaceRect[] faceRect,int preWidth,int preHeight,boolean isMirror){
		Canvas canvas=null;
		try {
			canvas=surfacehldr_.lockCanvas();
			if(canvas!=null){
				float ration=canvas.getWidth()/preWidth;
				//设置画布，画笔
				canvas.drawColor(Color.TRANSPARENT);
				Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
				
				//清屏
				paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				canvas.drawPaint(paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
				
				paint.setColor(Color.GREEN);
				paint.setStrokeWidth(2);
				paint.setStyle(Paint.Style.STROKE);
				paint.setAntiAlias(true);
				
				for(int i=0;i<faceNum;i++){
					int left=0;
					if (!isMirror) {// 后置
						left = faceRect[i].left;
					} else {// 前置，需要处理镜像
						left = preWidth- faceRect[i].right;
					}
					int right=0;
					if (!isMirror) {// 后置
						right = faceRect[i].right;
					} else {// 前置，需要处理镜像
						right = preWidth- faceRect[i].left;
					}
					
					canvas.drawRect(left*ration,faceRect[i].top*ration, right*ration,faceRect[i].bottom*ration, paint);
				}
			}
			surfacehldr_.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			if(canvas!=null){
				surfacehldr_.unlockCanvasAndPost(canvas);
			}
			e.printStackTrace();
		}
		
	}
	
	
	public void drawFaceCircle(int faceNum,THIDFaceRect[] faceRect,int preWidth,int preHeight,boolean isMirror){
		Canvas canvas=null;
		try {
			canvas=surfacehldr_.lockCanvas();
			if(canvas!=null){
				float ration=canvas.getWidth()/preWidth*1.5f;
				//设置画布，画笔
				canvas.drawColor(Color.TRANSPARENT);
				Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
				
				//清屏
				paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				canvas.drawPaint(paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
				
				paint.setColor(Color.GREEN);
				paint.setStrokeWidth(4);
				paint.setStyle(Paint.Style.FILL);
				paint.setAntiAlias(true);
				
				if(faceNum>0){
					canvas.drawCircle(ration*130, ration*100, 10, paint);
					paint.setColor(Color.RED);
					paint.setStrokeWidth(2);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawCircle(ration*130, ration*100, 14, paint);
				}
			}
			surfacehldr_.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			if(canvas!=null){
				surfacehldr_.unlockCanvasAndPost(canvas);
			}
			e.printStackTrace();
		}
		
	}
	
	

}
