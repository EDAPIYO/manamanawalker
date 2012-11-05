package com.example.manamanawalker;

import java.util.List;

import android.R.id;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WalkerActivity extends Activity implements SensorEventListener {
	String TAG = "manamanawalker";

	private int[] soundResouces = {
			R.raw.countup,//00
			R.raw.m01,
			R.raw.m02,
			R.raw.m03,
			R.raw.m04,
			R.raw.m05,
			R.raw.m06,
			R.raw.m07,
			R.raw.m08,
			R.raw.m09,
			R.raw.m10,//10
			R.raw.m20,//11
			R.raw.m30,//12
			R.raw.m40,//13
			R.raw.m50,//14
			R.raw.m60,//15
			R.raw.m70,//16
			R.raw.m80,//17
			R.raw.m90,//18
			R.raw.m100,//19
			R.raw.thankyou,
			R.raw.toofast,//21
	};
	private static int STARTVOICE = 0;
	private static int STOPVOICE = 20;
	private static int TOOFASTVOICE = 21;

	private int soundIds[];
	private SoundPool mSoundPool = null;
	private int mRingVol;
	private Boolean mButtonStatus = false;
	private int cnt = 0;
	private int mStopCounter = 0;
	public TextView mText;
	SensorManager mSensorManager = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walker);
        final TextView mText = (TextView)findViewById(R.id.textView1);
        mText.setText("");
        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        mRingVol = audio.getStreamVolume(AudioManager.STREAM_RING);
    	Button button = (Button) findViewById(R.id.toggleButton1);

        mSoundPool = new SoundPool(soundResouces.length, AudioManager.STREAM_RING, 0);
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {

    		public void onLoadComplete(SoundPool soundPool, int sampleId,
    				int status) {
    			// TODO 自動生成されたメソッド・スタブ
    			if (0 == status) {
    				Log.v(TAG,"mSoundPool.setOnLoadCompleteListener LoadComplete" + sampleId);
    	        }
    		}
        });

    	soundIds = new int[soundResouces.length];

    	for (int i=0; i<soundResouces.length; i++) {
    		soundIds[i] = mSoundPool.load(this, soundResouces[i], 1);
    	}

        // ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ボタンがクリックされた時に呼び出されます
                Button button = (Button) v;
                Log.i(TAG,"on Clicked.");
                if(mButtonStatus == false){
                	Log.v(TAG,"START VOICE");
                	mSoundPool.play(soundIds[STARTVOICE], 0.5f, 0.5f, 1, 0, 1.0f);
                	mText.setText("かぞえまーす");
                	mButtonStatus = true;
                	startManamanaCounter();
                }else{
                	Log.v(TAG,"STOP VOICE");
                	mText.setText("おつかれさまでした\n" + cnt + "歩でした");
                	cnt = 0;
                	stopManamanaCounter();

                	mButtonStatus = false;
                	mSoundPool.play(soundIds[STOPVOICE], 0.5f, 0.5f, 1, 0, 1.0f);
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.activity_walker, menu);
        return true;
    }

    public void startManamanaCounter(){
    	//regist Lisnter
    	Log.v(TAG,"startManamanaCounter");
    	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
    	cnt = 0;
    	for (Sensor sensor : sensors) {
            if( sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
            }
        }


    }
    public void stopManamanaCounter(){
    	Log.v(TAG,"stopManamanaCounter");
    	mSensorManager.unregisterListener(this);
        //unregist Listener
    }
    public void onDestroy(){
    	stopManamanaCounter();
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO 自動生成されたメソッド・スタブ

	}


	public void onSensorChanged(SensorEvent event) {
		// TODO 自動生成されたメソッド・スタブ
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];

		//Log.i(TAG,"x =" + x + ";y=" + y + ";z = " + z);
		//Log.i(TAG,"!!" + Math.sqrt(x*x + y*y + z*z));
		if(225 < (x*x + y*y + z*z) && mStopCounter == 0){
			cnt++;
			playVoice(cnt);
			mStopCounter = 5;
		}else if(mStopCounter > 0){
			Log.v(TAG,"wait...");
			mStopCounter--;
		}
	}

	public void playVoice(int cnt){
		Log.v(TAG,"playVoice:" + cnt);
		if(cnt%100 != 0){
			cnt = cnt%100;
		}else{
			cnt = 100;
		}

		if(cnt == 100){
			mSoundPool.play(soundIds[19], 0.5f, 0.5f, 1, 0, 1.0f);
		}else if(cnt%10 != 0){
			mSoundPool.play(soundIds[cnt%10], 0.5f, 0.5f, 1, 0, 1.0f);
		}else{
			int num = 9 + cnt/10;
			mSoundPool.play(soundIds[num], 0.5f, 0.5f, 1, 0, 1.0f);
		}
	}
}
