package edu.hanker.duckyhunter;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MessageFragment extends Fragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View messageLayout = inflater.inflate(R.layout.settings_layout, container, false);
		Button about_us = (Button) messageLayout.findViewById(R.id.settings_button3);
		about_us.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity().getApplication(), AboutUs.class);
				startActivity(intent);
				
			}
		});
		
		return messageLayout;
		
	}

}