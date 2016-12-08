package com.example.giris.luxtest;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity {

    boolean syncTestStart=false;
    long syncTestPrevTime;

    long syncTime;

    String output="0";
    String preface="";
    // declare variable

    ProgressBar lightMeter;
    TextView tvMaxValue, tvReader;

    ScrollView scrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load control
        lightMeter = (ProgressBar) findViewById(R.id.lightmeter);
        tvMaxValue = (TextView) findViewById(R.id.max);
        tvReader = (TextView) findViewById(R.id.reading);


        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        // implement sensor manager
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        // check sensor available in devise. if available then get reading
        if (lightSensor == null) {
            Toast.makeText(getApplicationContext(), "No Sensor",
                    Toast.LENGTH_SHORT).show();
            // Toast.makeText(AndroidLightSensorActivity.this,
            // "No Light Sensor! quit-", Toast.LENGTH_LONG).show();
        } else {
            float max = lightSensor.getMaximumRange();
            lightMeter.setMax((int) max);
            tvMaxValue.setText("Max Reading: " + String.valueOf(max));

            sensorManager.registerListener(lightSensorEventListener,
                    lightSensor, SensorManager.SENSOR_DELAY_FASTEST);

        }



        /*
        mLinearLayout = (LinearLayout) findViewById(R.id.strobe);
        startStrobe();
        */


        /*
        Camera cam = Camera.open();
        final Camera.Parameters params = cam.getParameters();
        String myString = "0101010101";
        long blinkDelay = 50; //Delay in ms
        for (int i = 0; i < myString.length(); i++) {
            if (myString.charAt(i) == '0') {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }

    // implement sensor event listener

    SensorEventListener lightSensorEventListener = new SensorEventListener() {

        int prevValue=0;
        long prevDuration;

        long syncTestCurrentTime;

        long currentTime;
        long prevTime;
        long noOfOnes;
        long noOfZeroes;
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        // get sensor update and reading
        @Override
        public void onSensorChanged(SensorEvent event) {
            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float currentReading = event.values[0];
                lightMeter.setProgress((int) currentReading);
                //tvReader.setText("Current Reading: "
                //      + String.valueOf(currentReading) + " Lux");

                if(!syncTestStart)
                {
                    syncTestPrevTime = System.nanoTime();
                    syncTestStart = true;
                }

                syncTestCurrentTime = System.nanoTime();
                long cycleTime = syncTestCurrentTime-syncTestPrevTime;
                Log.e("cycle time", (syncTestCurrentTime-syncTestPrevTime)+"");
                syncTestPrevTime = syncTestCurrentTime;


                Log.e("lux", currentReading+"");
                DateFormat df = new SimpleDateFormat("HH:mm:ss.SSSSSS");
                String date = df.format(Calendar.getInstance().getTime());

                if (prevTime == 0)
                {
                    prevTime = System.nanoTime();
                    syncTime = prevTime;
                }
//45000000*3*2*2
                if(currentReading == 0) {
                    Log.e("prev duration", prevDuration+"");
                    currentTime = System.nanoTime();
                    noOfOnes = (int)((prevDuration)/700000000);
                    prevDuration = 0;
                    prevValue = 0;

                    //Log.e("noOfOnes before while", currentTime + "");
                    while(noOfOnes > 0){
                        output += '1';
                        noOfOnes--;
                    }
                    prevTime = currentTime;
                    //output += '0';
                }
                else{
                    currentTime = System.nanoTime();
                    if(prevValue==1)
                    {
                        prevDuration+=currentTime - prevTime;
                    }

                    else {
                        prevValue = 1;
                        noOfZeroes = (int) ((currentTime - prevTime) / 700000000);

                        //Log.e("noOfZeroes before while", currentTime + "");
                        while (noOfZeroes > 0) {
                            output += '0';
                            noOfZeroes--;
                        }
                    }
                    prevTime = currentTime;
                    //output += '1';
                }

                tvReader.setText(output);
                //tvReader.setText(tvReader.getText().toString()+"\n"+date+":  "+String.valueOf(currentReading)+" Lux");
                scrollView.fullScroll(View.FOCUS_DOWN);

                Log.e("output", output);
                Log.e("string output", new String(new BigInteger(output, 2).toByteArray())+"");

            }
        }

    };

    private LinearLayout mLinearLayout;

    private Handler mHander = new Handler();

    private boolean mActive = false;
    private boolean mSwap = true;

    private final Runnable mRunnable = new Runnable() {

        public void run() {
            if (mActive) {
                if (mSwap) {
                    mLinearLayout.setBackgroundColor(Color.WHITE);
                    mSwap = false;
                    mHander.postDelayed(mRunnable, 1);
                } else {
                    mLinearLayout.setBackgroundColor(Color.BLACK);
                    mSwap = true;
                    mHander.postDelayed(mRunnable, 1);
                }
            }
        }
    };
    private void startStrobe() {
        mActive = true;
        mHander.post(mRunnable);
    }

}