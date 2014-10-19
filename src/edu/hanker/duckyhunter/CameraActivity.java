package edu.hanker.duckyhunter;


import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
/**The class is created for recording view, which is a customized camera view.
 *(extends {@link Activity}, implements {@link SensorEventListener}, {@link SurfaceHolder.Callback})
 * 
 * @param myCamera private Camera object
 * 
 * @author wang1317
 */
public class CameraActivity extends Activity implements SurfaceHolder.Callback, SensorEventListener{
	/*Activity TAG*/
	private static final String TAG = "mCamera";
	
	private Camera myCamera;
	private SurfaceView previewSurfaceView;
	private SurfaceHolder previewSurfaceHolder;
	private ImageView bloodView;
	Drawable bloodDrawable;
	
	private boolean previewing = false;
	private SensorManager sensorManager;
	private Sensor gSensor;
	//private SurfaceView buttonTakePicture;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       Log.d(TAG, "onCreate");
       /* window configuration*/
       getWindow().setFormat(PixelFormat.TRANSLUCENT);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       /*force a landscape layout*/
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       /*load the camera layout*/
       setContentView(R.layout.shooting_layout);
       /*initialize Sensor Manager*/
       sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
	   gSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);//use orientation sensor to get the viewing angle
	   /* create a SurfaceView to preview the scene*/
       previewSurfaceView = (SurfaceView)findViewById(R.id.previewsurface);
       previewSurfaceHolder = previewSurfaceView.getHolder();
       /* add callback and set parameters*/
       previewSurfaceHolder.addCallback(this);
       previewSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       previewSurfaceHolder.setFormat(ImageFormat.NV21);
       
       bloodView = (ImageView)findViewById(R.id.blood);
       bloodDrawable = getResources().getDrawable(R.drawable.blood);
       
       //set onClickListener on the "Snap it" button 
       previewSurfaceView.setOnClickListener(new Button.OnClickListener(){
 
    	   @Override
    	   public void onClick(View view) {
    		   /* The camera will first auto-focus before taking a photo*/
    		   previewSurfaceView.setEnabled(false);//disable the button when auto-focusing
    		   //myCamera.autoFocus(myAutoFocusCallback);
    		   myCamera.takePicture(null,null,jpegPictureCallback); 
    		  //tryDrawing(previewSurfaceHolder);

    	   }});

       
   }
   	/** call back after auto-focus */
		AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				//buttonTakePicture.setEnabled(true);
				/* unregister the orientation sensor to stop detecting viewing angle*/
				sensorManager.unregisterListener(CameraActivity.this);
				/* processing photo using PictureCallback*/
				myCamera.takePicture(null,null,jpegPictureCallback); 
			}};
			
	   PictureCallback jpegPictureCallback = new PictureCallback(){
 
		   @Override
		   public void onPictureTaken(byte[] data, Camera camera) {
			   try{
				   
				   System.out.println("helloooooo!");
				   Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);//create bitmap to store the captured photo
				   
		           final int w = bitmapPicture.getWidth();  
		           final int h = bitmapPicture.getHeight();
		           System.out.println("width::"+w);
		           System.out.println("width::"+h);
		           
				   /*To broadcast photos in the gallery*/
				   /*galleryAddPic();*/
				   //mBirdTask = new BirdTask(data);
				   //mBirdTask.execute(); 
		            //doSomethingNeeded(bmp);   //自己定义的实时分析预览帧视频的算法
		            int pv = 0;
		            int sumR = 0;
		            int sumG = 0;
		            int sumB = 0;
		            for (int i = Math.round(w/2) - 4; i < Math.round(w/2) + 4; i++) {
						for (int j = Math.round(h/2) - 4; j < Math.round(h/2) + 4; j++) {
							pv = bitmapPicture.getPixel(i, j);
							sumR = sumR + Color.red(pv);
							sumG = sumG + Color.green(pv);
							sumB = sumB + Color.blue(pv);
						}
					}
		            int R = Math.round(sumR/(8*8));
		            int G = sumG/(8*8);
		            int B = sumB/(8*8);
		            System.out.println("A:"+ bitmapPicture.getHeight());
		            System.out.println("R:" + R);
		            System.out.println("G:" + G);
		            System.out.println("B:" + B);
		            
		            
		            int num_bullet = -1;
		            
		            // if color is red
		            if (G > 1.1*R && G > 130 && G > 1.1*B) {
		            	Log.d(TAG, "you win!");
		            	
		        		bloodView.setScaleType(ScaleType.FIT_XY);
		        		bloodView.setImageDrawable(bloodDrawable);
		            	
		            	Handler handler = new Handler();
					    handler.postDelayed(new Runnable() {
					    @Override
					    public void run() {
					    	
				        //TODO show image here!
					        }
					    },550); //adding one sec delay
					    //bloodView.setImageDrawable(null);
					    CameraActivity.this.setResult(Activity.RESULT_OK);
					    Intent intent = new Intent(CameraActivity.this, WinnerPage.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					    startActivity(intent);
		            	//finish();
		            	//TODO show image! -- you win!
					}else {
						Log.d(TAG, "missed!");
						num_bullet = ActivityBridge.getInstance().getBulletNum();
		            	num_bullet--;
		            	ActivityBridge.getInstance().setBulletNum(num_bullet);
					}
		            
		            if (num_bullet == 0) {
		            	CameraActivity.this.setResult(Activity.RESULT_CANCELED);
					    Intent intent = new Intent(CameraActivity.this, LoserPage.class)
						.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					    startActivity(intent);
		            	//finish();
					}else {
//						Handler handler = new Handler();
//					    handler.postDelayed(new Runnable() {
//					    @Override
//					    public void run() {
//					    	
//				        //TODO show image here!
//					        }
//					    },200); //adding one sec delay 
					   
					    myCamera.startPreview();
					    previewSurfaceView.setEnabled(true);
					}
				   
				   
			   }
			   catch(Exception e){
				   e.printStackTrace();
			   }
 
		   }
	   };

	   @Override
		public void onBackPressed() {			
			CameraActivity.this.setResult(RESULT_CANCELED);
			finish();
		}
	   
	   //broadcast photos in gallery
	   /*private void galleryAddPic() {
		   Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		   File f = new File(filePath);
		   Uri contentUri = Uri.fromFile(f);
		   mediaScanIntent.setData(contentUri);
		   this.sendBroadcast(mediaScanIntent);
	   }*/			    


	   @Override
	   public void surfaceCreated(SurfaceHolder holder) {
		   Log.d(TAG,"surfaceCreated Surface is :"+ previewSurfaceHolder.getSurface().getClass().getName());
		   myCamera = Camera.open();
		   
		   try {
			   myCamera.setPreviewDisplay(holder);
			  
		   }catch (IOException e) {
			   e.printStackTrace();
			   myCamera.release();
			   myCamera = null;
		   }
	   }

	   @Override
	   public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		   Log.d(TAG,String.format("surfaceChanged: format = %d, w =%d, h=%d", format, w, h));
		   if (previewing) {
			   myCamera.stopPreview();
		   }
		   try {
			   Camera.Parameters parameters = myCamera.getParameters();
			   parameters.setPictureFormat(PixelFormat.JPEG);
			   parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			   List<Size> sizes = parameters.getSupportedPreviewSizes();
			   List<Size> sizes2 = parameters.getSupportedPictureSizes();
			   Size optimalSize = getOptimalPreviewSize(sizes, w, h);
			   parameters.setPreviewSize(optimalSize.width, optimalSize.height);
			   parameters.setPictureSize(sizes2.get(0).width, sizes2.get(0).height);
			   //tryDrawing(holder);
			   myCamera.setParameters(parameters);
			   myCamera.startPreview();
			   previewing = true;
		  
			   //int bufSize = optimalSize.width * optimalSize.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8;
			   //byte[] cbBuffer = new byte[bufSize];
			   
			   
		   } catch (Exception e) {
			   e.printStackTrace();
		   }

	   }

  private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
	  
		return optimalSize;
	}
  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
	  if (myCamera != null) {
		  myCamera.stopPreview();
		  myCamera.release();
		  myCamera = null;
		  previewing = false;
	  }
  }

  @Override
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(this, gSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
  @Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		int bulletNum = ActivityBridge.getInstance().getBulletNum();
		
		ImageView imageView = (ImageView) findViewById(R.id.bullet1);
		imageView.setScaleType(ScaleType.FIT_XY);
		Drawable image = getResources().getDrawable(R.drawable.bullet1);
		ImageView imageView2 = (ImageView) findViewById(R.id.bullet2);
		imageView2.setScaleType(ScaleType.FIT_XY);
		Drawable image2 = getResources().getDrawable(R.drawable.bullet2);
		ImageView imageView3 = (ImageView) findViewById(R.id.bullet3);
		imageView3.setScaleType(ScaleType.FIT_XY);
		Drawable image3 = getResources().getDrawable(R.drawable.bullet3);
		switch (bulletNum) {
        case 1:  
        	
    		imageView.setImageDrawable(image);
    		imageView2.setImageDrawable(null);
    		imageView3.setImageDrawable(null);
            break;
        case 2:  
    		imageView2.setImageDrawable(image2);
    		imageView.setImageDrawable(null);
    		imageView3.setImageDrawable(null);
            break;
        case 3: 
    		imageView3.setImageDrawable(image3);
    		imageView2.setImageDrawable(null);
    		imageView.setImageDrawable(null);
            break;
        default: 
            break;
    }
//		ImageView imageView = (ImageView) findViewById(R.id.shotgun);
//		imageView.setScaleType(ScaleType.FIT_XY);
//		Drawable image = getResources().getDrawable(R.drawable.gun);
//		imageView.setImageDrawable(image);
	}
  


}