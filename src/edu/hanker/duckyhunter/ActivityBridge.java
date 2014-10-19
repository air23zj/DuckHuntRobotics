package edu.hanker.duckyhunter;


/**This class is created for sharing data among activities.
 * 
 * @author wang1317
 * @param  bulletNum # of bullets
 * 
 *
 */
public class ActivityBridge {
	
	private static final ActivityBridge INSTANCE = new ActivityBridge();
	private int bulletNum;

	
	private ActivityBridge(){
		
	}
	public static ActivityBridge getInstance(){
		return INSTANCE;
	}
	public int getBulletNum() {
		return bulletNum;
	}
	public void setBulletNum(int bulletNum) {
		this.bulletNum = bulletNum;
	}

	
	
	
}