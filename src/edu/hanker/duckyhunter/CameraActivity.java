package edu.hanker.duckyhunter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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
import android.widget.Toast;
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
//		            //int sumR = 0;
//		            //int sumG = 0;
//		            //int sumB = 0;
		            int flag = 0;
		            for (int i = Math.round(w/2) - 50; i < Math.round(w/2) + 50; i++) {
						for (int j = Math.round(h/2) - 50; j < Math.round(h/2) + 50; j++) {
							pv = bitmapPicture.getPixel(i, j);
							//sumR = sumR + Color.red(pv);
							//sumG = sumG + Color.green(pv);
							//sumB = sumB + Color.blue(pv);
							if (Color.red(pv) > 140 && Color.green(pv) > 140 && Color.green(pv) > 1.3 * Color.blue(pv)) {
								flag++;
							}
							if (flag > 10) {
								break;
							}
						}
						if (flag > 10) {
							System.out.println("R:" + Color.red(pv));
							System.out.println("G:" + Color.green(pv));
							System.out.println("B:" + Color.blue(pv));
							break;
						}
					}
//		            int R = Math.round(sumR/(8*8));
//		            int G = sumG/(8*8);
//		            int B = sumB/(8*8);
//		            System.out.println("A:"+ bitmapPicture.getHeight());
//		            System.out.println("R:" + R);
//		            System.out.println("G:" + G);
//		            System.out.println("B:" + B);
		            
		            
		            int num_bullet = -1;
		            
		            // if color is red
		            if (flag > 10) {
		            	Log.d(TAG, "you win!");
		            	new HttpAsyncTask().execute("https://agent.electricimp.com/ytNQ5QZ5SzCe?led=0");
		    			new HttpAsyncTask().execute("https://agent.electricimp.com/putVqm5RC6EY?led=1");
		        		bloodView.setScaleType(ScaleType.FIT_XY);
		        		bloodView.setImageDrawable(bloodDrawable);
		            	
		            	/*Handler handler = new Handler();
					    handler.postDelayed(new Runnable() {
					    @Override
					    public void run() {
					    	
				        //TODO show image here!
					        }
					    },150);*/ //adding one sec delay
					    //bloodView.setImageDrawable(null);
					    //new HttpAsyncTask().execute("https://agent.electricimp.com/putVqm5RC6EY?led=0");
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
		            	new HttpAsyncTask().execute("https://agent.electricimp.com/ytNQ5QZ5SzCe?led=1");
		    			new HttpAsyncTask().execute("https://agent.electricimp.com/putVqm5RC6EY?led=0");
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
			   //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
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
	public static String GET(String url){
		InputStream inputStream = null;
		String result = "";
		try {
			
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			
			// convert inputstream to string
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";
		
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		
		return result;
	}
	
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        
        inputStream.close();
        return result;
        
    }
	
    public boolean isConnected(){
    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	    if (networkInfo != null && networkInfo.isConnected()) 
    	    	return true;
    	    else
    	    	return false;	
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
       }
    }


}