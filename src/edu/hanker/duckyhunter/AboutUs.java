package edu.hanker.duckyhunter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**This activity shows a brief introduction about 4Hackers.
 * 
 * @author wang1317
 *
 */
public class AboutUs extends Activity{
	/* AboutTada display an "about" page, which contains a short description of TADA project.
	 * */

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/* Load the layout */
		setContentView(R.layout.about_layout);

		Button backButton = (Button)findViewById(R.id.settings_button0);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}