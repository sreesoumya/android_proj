package edu.sdsu.cs.assignment3.photosharing;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    int reqwidth;
    int reqheight;

    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        reqwidth = size.x;
        reqheight = size.y;
    }

    @Override
    public int getCount() {
        return this._imagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Button btnClose;
        ImageView imgDisplay;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_full_screen_image, container,
                false);
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        String image_path = _imagePaths.get(position);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = true; 
    	BitmapFactory.decodeFile(image_path, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        Log.d("Full screen","Original Image height "+imageHeight+" x "+imageWidth );
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inJustDecodeBounds = false;
        Log.d("FullScreenAdapter","getting image of size "+reqheight+"x"+reqwidth);
        if(imageHeight >=3000 || imageWidth >=3000){
        	options.inSampleSize =8;
        }else{
            int inSampleSize = 1;
            if (imageHeight > reqheight || imageWidth > reqwidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;
            while ((halfHeight / inSampleSize) > reqheight
                && (halfWidth / inSampleSize) > reqwidth) {
                	inSampleSize *= 2;
            }
            }
        }
	    Log.d("Full screen","sample size "+ options.inSampleSize);
        Bitmap bitmap = BitmapFactory.decodeFile(image_path, options);
        imgDisplay.setImageBitmap(bitmap);
        
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View v) {
                _activity.finish();
            }
        }); 

        ((ViewPager) container).addView(viewLayout);
 
        return viewLayout;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
 
    }

}