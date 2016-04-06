package edu.sdsu.cse.assignment4.flappybird;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

public class DrawView extends View  {
	Paint paint = new Paint();
	Rect outrect = new Rect();
	Rect flappy_rect = new Rect();
	int screenHeight;
	int screenWidth;
	Context mcontext;
	boolean isInMotion = false;
	Canvas canvas;
	Display display;
	Rect rect_top = new Rect();
	Rect rect_bottom = new Rect();
	int i= 0;
	

	public DrawView(Context context) {
		super(context);
		this.mcontext = context;
	}
	public DrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public Rect get_top_rect_position(){
		return rect_top;
	}
	public Rect get_bottom_rect_position(){
		return rect_bottom;
	}
	public void drawview_gameover(){
		this.removeCallbacks(r);
	}
	protected void move_invalidate(){
		this.invalidate();
	}
	
	private final Runnable r = new Runnable() {
		@Override
		public void run() {
			move_invalidate();	
		}
	};
	
	
	@Override
	protected void onDraw(Canvas canvas) {	
		super.onDraw(canvas);
		this.canvas = canvas;
		//do one time assignments inside to avoid load on multiple draws.
		if(!isInMotion){
			this.getWindowVisibleDisplayFrame(outrect);
			this.screenHeight = outrect.bottom;
			this.screenWidth = outrect.right;
			paint.setColor(0xff2EFE2E);
			isInMotion = true;
		}
		//move the retangle (10 selected to provde slow and smooth animation based on 300ms refresh)
		i+=10;
		
		rect_top.set(screenWidth-100-i,0,screenWidth-i,500);
		rect_bottom.set(screenWidth-100-i,screenHeight-500,screenWidth-i,screenHeight);
		canvas.drawRect(rect_top,paint);
		canvas.drawRect(rect_bottom,paint);
		
		//redreaw the canvas every 300ms
		this.postDelayed(r, 300);
		
	}
			
}

	

	

