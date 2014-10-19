package edu.hanker.duckyhunter;


import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
	private static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
	/*random number*/
	private static final int SHOW_PREVIEW = 47;
	
	private Camera myCamera;
	private SurfaceView previewSurfaceView;
	private SurfaceHolder previewSurfaceHolder;
	
	private boolean previewing = false;
	private SensorManager sensorManager;
	private Sensor gSensor;
	private Button buttonTakePicture;

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
       
	   
       
       
       //set onClickListener on the "Snap it" button 
       buttonTakePicture = (Button)findViewById(R.id.takebutton);
       buttonTakePicture.setOnClickListener(new Button.OnClickListener(){
 
    	   @Override
    	   public void onClick(View view) {
    		   /* The camera will first auto-focus before taking a photo*/
    		   buttonTakePicture.setEnabled(false);//disable the button when auto-focusing
    		   myCamera.autoFocus(myAutoFocusCallback);
    	   }});
       
       //set onClickListener on the "cancel" button 
       Button buttonCancel = (Button)findViewById(R.id.cancelbutton);
       buttonCancel.setOnClickListener(new Button.OnClickListener(){
		@Override
		public void onClick(View view) {
			/*back to TadaActivity*/
			CameraActivity.this.setResult(Activity.RESULT_CANCELED);
			finish();
		}});
       
   }
   	/** call back after auto-focus */
		AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				buttonTakePicture.setEnabled(true);
				/* unregister the orientation sensor to stop detecting viewing angle*/
				sensorManager.unregisterListener(CameraActivity.this);
				/* processing photo using PictureCallback*/
				myCamera.takePicture(null,null,jpegPictureCallback); 
			}};
			
	   PictureCallback jpegPictureCallback = new PictureCallback(){
 
		   @Override
		   public void onPictureTaken(byte[] data, Camera camera) {
			   try{
				   //Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);//create bitmap to store the captured photo
				   
				   DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);//reset date format
				   String timeStamp = dateFormat.format(new Date()).toString();//generate date string to be used as filename 
				   String filepath = getBaseContext().getFilesDir().getPath() + "/"+ timeStamp + ".jpg";//filename
				   				   				   				   
				   
				   FileOutputStream b = new FileOutputStream(filepath);
				   //bitmapPicture.compress(Bitmap.CompressFormat.JPEG,100,b);
				   //write out image file on SDcard
				   b.write(data);
				   b.flush();
				   b.close();
				   
				   /*To broadcast photos in the gallery*/
				   /*galleryAddPic();*/
				   
			   }
			   catch(Exception e){
				   e.printStackTrace();
			   }
 
		   }
	   };
	   /* Called when an activity you launched exits, giving you the requestCode you started it with, the 
 		* resultCode it returned, and any additional data from it*/
	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		   super.onActivityResult(requestCode, resultCode, data);
		   Log.d(TAG, "onActivityResult" + resultCode);
		   if (requestCode == SHOW_PREVIEW && resultCode == Activity.RESULT_OK) {
				System.out.println("successfully return to camera");
				CameraActivity.this.setResult(RESULT_OK);
				this.finish();
			}
		   if (requestCode == SHOW_PREVIEW && resultCode == Activity.RESULT_CANCELED) {
				System.out.println("successfully return to camera");
			}
		}
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
			   //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			   List<Size> sizes = parameters.getSupportedPreviewSizes();
			   List<Size> sizes2 = parameters.getSupportedPictureSizes();
			   Size optimalSize = getOptimalPreviewSize(sizes, w, h);
			   parameters.setPreviewSize(optimalSize.width, optimalSize.height);
			   parameters.setPictureSize(sizes2.get(0).width, sizes2.get(0).height);

			   myCamera.setParameters(parameters);
			   myCamera.startPreview();
			   previewing = true;
		  
			   //int bufSize = optimalSize.width * optimalSize.height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat())/8;
			   //byte[] cbBuffer = new byte[bufSize];
			   //myCamera.setPreviewCallbackWithBuffer(this);
			   //myCamera.addCallbackBuffer(cbBuffer);
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
//		ImageView imageView = (ImageView) findViewById(R.id.shotgun);
//		imageView.setScaleType(ScaleType.FIT_XY);
//		Drawable image = getResources().getDrawable(R.drawable.gun);
//		imageView.setImageDrawable(image);
	}
}