package edu.hanker.duckyhunter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class CameraFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View cameraLayout = inflater.inflate(R.layout.camera_layout, container, false);
		ImageView flyingDuck = (ImageView) cameraLayout.findViewById(R.id.flying_duck);
		flyingDuck.setScaleType(ScaleType.FIT_XY);
		flyingDuck.setBackgroundResource(R.anim.flyingduck);
		final AnimationDrawable animationDrawable=(AnimationDrawable)flyingDuck.getBackground();
		flyingDuck.post(new Runnable(){
		@Override
		public void run(){
			animationDrawable.start();
			}
		});
		
		ImageButton btn_play = (ImageButton) cameraLayout.findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new HttpAsyncTask().execute("https://agent.electricimp.com/ytNQ5QZ5SzCe?led=1");
				new HttpAsyncTask().execute("https://agent.electricimp.com/putVqm5RC6EY?led=0");
				Intent intent = new Intent(getActivity().getApplication(), CameraActivity.class);
				ActivityBridge.getInstance().setBulletNum(3);
				startActivity(intent);
			}
		});
		return cameraLayout;
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
    	ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
        	Toast.makeText(getActivity().getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
       }
    }
}