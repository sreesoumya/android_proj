package edu.sdsu.cse.assignment4.flappybird;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnTouchListener{
	
	DrawView drawView;
	View rectangle;
	boolean isInMotion;
	boolean game_over = false;
	Canvas canvas;
	int screenWidth;
	int screenHeight;
	ImageView flappy_bird;
	DrawView drawview;
	private AnimationDrawable fly;
	Rect top_rect,bottom_rect;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        Point screensize = new Point();
        display.getSize(screensize);
        screenHeight= screensize.y;
        screenWidth = screensize.x;
        
        drawview = (DrawView)findViewById(R.id.drawView1);
        drawview.setOnTouchListener(this);
        
        flappy_bird = (ImageView) findViewById(R.id.imageview_flappybird);
        flappy_bird.setBackgroundResource(R.anim.flappybird_motion);
        this.fly = (AnimationDrawable) flappy_bird.getBackground();
        
        //start flapping
        this.fly.start();
        
        //start the game
        flappy_down_gravity();
	}
	
	
	protected void detect_crash(){
		//get rectangle position
		top_rect = drawview.get_top_rect_position();
		bottom_rect = drawview.get_bottom_rect_position();
		
		//detect crash, fine tune to get contact with bird
			
		//crash against top_rect
		if((top_rect.right >0) && (top_rect.left < flappy_bird.getX()+100) ){
			if(flappy_bird.getY()+25 < top_rect.bottom){
				game_over();
			}	
		}
		//crash against bottom_rect
		if((bottom_rect.right >0) && (bottom_rect.left < flappy_bird.getX()+100) ){
			if(flappy_bird.getY()+120 > bottom_rect.top){
				game_over();
			}	
		}
		
		//crash against top and bottom of screen edge
		if((flappy_bird.getY()+25 < 0) || (flappy_bird.getY()+250 > screenHeight)){
			game_over();
		}
		
		//rectangle reaches the left end
		if(top_rect.right < 0){
			game_over();
		}
		

	}
	
	protected void game_over(){
		
		flappy_bird.removeCallbacks(r);
		flappy_bird.clearAnimation();
		drawview.drawview_gameover();
		game_over = true;
		SystemClock.sleep(2000);
		setContentView(R.layout.game_over_view);
		
		
	}
	
	private final Runnable r = new Runnable() {
		@Override
		public void run() {
			flappy_down_gravity();
		}
	};
	
	protected void flappy_down_gravity(){
		ViewPropertyAnimator flappy_bird_property = flappy_bird.animate();
		//move flappy_bird down constantly (20 selected based on playing conditions on Nexus 7 and HTC OneX)
		flappy_bird_property.translationY(flappy_bird.getY()+20);
		flappy_bird_property.start();
		
		detect_crash();
		
		if(!game_over)
			flappy_bird.postDelayed(r,300);
	}

	protected void handle_action(MotionEvent event){
		ViewPropertyAnimator flappy_bird_property = flappy_bird.animate();
		//decide the push per tap based on gameplay (150 selected to provide stability in play conditions based on Nexus 7 and HTC OneX).
		flappy_bird_property.translationY(flappy_bird.getY()-150);
		flappy_bird_property.start();
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		int action = event.getAction();
		int action_code = action & MotionEvent.ACTION_MASK;
		switch(action_code){
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_MOVE:
			 handle_action(event);
			 return true;
		}
		return false;
	}
}