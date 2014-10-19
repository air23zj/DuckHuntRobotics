package edu.hanker.duckyhunter;


import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CameraFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View cameraLayout = inflater.inflate(R.layout.camera_layout, container, false);
		
		Button btn_play = (Button) cameraLayout.findViewById(R.id.btn_play);
		btn_play.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity().getApplication(), CameraActivity.class);
				startActivity(intent);
			}
		});
		return cameraLayout;
	}

}