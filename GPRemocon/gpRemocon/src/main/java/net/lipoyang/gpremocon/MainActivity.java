/*
 * Copyright (C) 2018 Bizan Nishimura (@lipoyang)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.lipoyang.gpremocon;

//import android.Manifest;
//import android.annotation.TargetApi;
import android.app.Activity;
//import android.content.Intent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
//import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.Timer;
import java.util.TimerTask;
//import android.widget.Toast;
import android.os.Vibrator;

public class MainActivity extends Activity implements PropoListener, WiFiCommListener{

    // Debugging
    private static final String TAG = "GPRemocon";
    private static final boolean DEBUGGING = true;
    
    // WiFi Communication
    private WiFiComm mWiFiComm;
    
    // Propo View
    private PropoView propoView;
    
    // Bluetooth state
    private WiFiStatus btState = WiFiStatus.DISCONNECTED;
    
    private final Handler handlerDigi = new Handler();

    private Timer timerCommandB;

    private Vibrator vib;
    private int switch_state_old = 0x0000;

    //***** onCreate, onStart, onResume, onPause, onStop, onDestroy
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(DEBUGGING) Log.e(TAG, "++ ON CREATE ++");
        
        // initialize PropoView
        setContentView(R.layout.activity_main);
        propoView = (PropoView)findViewById(R.id.propoView1);
        propoView.setParent(this,this);
        
        // initialize WiFi
        mWiFiComm = WiFiComm.getInstance();
        mWiFiComm.init();

        vib = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(DEBUGGING) Log.e(TAG, "++ ON START ++");
    }
    @Override
    public synchronized void onResume() {
        super.onResume();
        if(DEBUGGING) Log.e(TAG, "+ ON RESUME +");

        // start WiFi
        mWiFiComm.setListener(this);
        mWiFiComm.start();

        btState = mWiFiComm.isConnected() ? WiFiStatus.CONNECTED : WiFiStatus.DISCONNECTED;
        propoView.setBtStatus(btState);

        timerCommandB = new Timer();
        timerCommandB.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(mWiFiComm.isConnected()){
                    timerCommandB.cancel();
                }
                String command = "#B$";
                byte [] bCommand=command.getBytes();
                mWiFiComm.send(bCommand);
            }
        }, 0, 1000);
    }
    @Override
    public synchronized void onPause() {
        if(DEBUGGING) Log.e(TAG, "- ON PAUSE -");

        timerCommandB.cancel();
        timerCommandB = null;
        // stop WiFi
        mWiFiComm.stop();
        mWiFiComm.clearListener();

        super.onPause();
    }
    @Override
    public void onStop() {
        if(DEBUGGING) Log.e(TAG, "-- ON STOP --");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        if(DEBUGGING) Log.e(TAG, "--- ON DESTROY ---");
        super.onDestroy();
    }
    
    // On touch PropoView's Digital Buttons
    public void onTouchDigitalButton(int button_state)
    {
        if((button_state & ~switch_state_old) != 0) {
            vib.vibrate(30);
        }
        switch_state_old = button_state;

        if(!mWiFiComm.isConnected()) return;
        
        // send a message.
        String command
                = "#D" + String.format("%04X", button_state) + "$";
        byte [] bCommand=command.getBytes();
        mWiFiComm.send(bCommand);
    }
    
    /**
     * WiFi Event Listener
     */
    @Override
    public void onConnect() {
        if(DEBUGGING) Log.e(TAG, "onConnect");
        // Connected!
        btState = WiFiStatus.CONNECTED;
        propoView.setBtStatus(btState);
    }
    @Override
    public void onDisconnect() {
        if(DEBUGGING) Log.e(TAG, "onDisconnect");
        // Disconnected!
        btState = WiFiStatus.DISCONNECTED;
        propoView.setBtStatus(btState);
    }
    @Override
    public void onReceive(byte[] value) {
        // mResultText.setText(new String(value));
    }
}
