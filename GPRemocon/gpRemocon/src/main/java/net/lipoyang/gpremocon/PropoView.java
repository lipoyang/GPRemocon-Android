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

//import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
//import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.content.res.Resources;
import android.view.MotionEvent;

public class PropoView extends View {
	// screen size of the original design
	private final float W_SCREEN = 1184;
	private final float H_SCREEN = 720;
	// Bluetooth button size
	private float W_BT_BUTTON = 240;
	private float H_BT_BUTTON = 106;
	// Bluetooth button base point (left-top) 
	private float X_BT_BUTTON = W_SCREEN/2 - W_BT_BUTTON/2;
	private float Y_BT_BUTTON = 64;
	
	// Digital Buttons
	final int BUTTON_NUM = 12;
	final int BTN_UP	= 0;
	final int BTN_DOWN	= 1;
	final int BTN_RIGHT	= 2;
	final int BTN_LEFT	= 3;
	final int BTN_TRNGL	= 4;
	final int BTN_CROSS	= 5;
	final int BTN_CIRCLE= 6;
	final int BTN_SQUARE= 7;
	final int BTN_R1	= 8;
	final int BTN_R2	= 9;
	final int BTN_L1	= 10;
	final int BTN_L2	= 11;
	int BIT_BUTTONS[]	= new int[BUTTON_NUM];  // bit mask
	float X_BUTTONS[]	= new float[BUTTON_NUM];// center position x
	float Y_BUTTONS[]	= new float[BUTTON_NUM];// center position y
	float HW_BUTTONS[]	= new float[BUTTON_NUM];// half width of valid range
	float HH_BUTTONS[]	= new float[BUTTON_NUM];// half height of valid range
	float OX_RINGS[]	= new float[BUTTON_NUM];// offset x of effect ring
	float OY_RINGS[]	= new float[BUTTON_NUM];// offset y of effect ring
	int buttonState = 0x0000;
	
	// ID of touch point
	private int idTouchButtons[] = new int[BUTTON_NUM];

    // Bluetooth state
    private WiFiStatus btState = WiFiStatus.DISCONNECTED;
    
    // bitmap objects
	private Bitmap imgRing1, imgRing2, imgDisconnected, imgConnected;
	private Paint paint;
	
	// event listener
	private PropoListener parent;

	// constructors
	public PropoView(Context context, AttributeSet attrs ) {
		super(context,attrs);
		init();
	}
	public PropoView(Context context) {
		super(context);
		init();
	}
	void init(){
		Resources res = this.getContext().getResources();
		imgDisconnected = BitmapFactory.decodeResource(res, R.drawable.disconnected);
		imgConnected    = BitmapFactory.decodeResource(res, R.drawable.connected);
		imgRing1        = BitmapFactory.decodeResource(res, R.drawable.ring1);
		imgRing2        = BitmapFactory.decodeResource(res, R.drawable.ring2);

		paint = new Paint();
	}
	
	// set main activity
	public void setParent(PropoListener listener, Activity activity) {
		
		parent = listener;
		
		// get screen information
		WindowManager wm = (WindowManager)activity.getBaseContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);
		
		// scale factor
		float xScale = displayMetrics.widthPixels / W_SCREEN;
		float yScale = displayMetrics.heightPixels / H_SCREEN;
		
		// resize bitmap images
		Bitmap img1 = imgDisconnected;
		Bitmap img2 = imgConnected;
		Bitmap img3 = imgRing1;
		Bitmap img4 = imgRing2;
		Matrix matrix = new Matrix();
		float rsz_ratio_w = 164.0f / img3.getWidth()  * xScale;
		float rsz_ratio_h = 164.0f / img3.getHeight() * yScale;
		matrix.postScale( rsz_ratio_w, rsz_ratio_h );
		imgDisconnected = Bitmap.createBitmap(img1, 0, 0, img1.getWidth(), img1.getHeight(), matrix, true);
		imgConnected    = Bitmap.createBitmap(img2, 0, 0, img2.getWidth(), img2.getHeight(), matrix, true);
		imgRing1        = Bitmap.createBitmap(img3, 0, 0, img3.getWidth(), img3.getHeight(), matrix, true);
		imgRing2        = Bitmap.createBitmap(img4, 0, 0, img4.getWidth(), img4.getHeight(), matrix, true);
		
		// Bluetooth button size
		W_BT_BUTTON = imgDisconnected.getWidth();
		H_BT_BUTTON = imgDisconnected.getHeight();
		// Bluetooth button base point (left-top) 
		X_BT_BUTTON = displayMetrics.widthPixels/2 - W_BT_BUTTON/2;
		Y_BT_BUTTON *= yScale;
		
		// Digital buttons
		for(int i=0;i<BUTTON_NUM;i++){
			BIT_BUTTONS[i] = 1 << i;
		}
		X_BUTTONS[BTN_UP]     =  305; Y_BUTTONS[BTN_UP]     = 218;
		X_BUTTONS[BTN_DOWN]   =  305; Y_BUTTONS[BTN_DOWN]   = 502;
		X_BUTTONS[BTN_RIGHT]  =  447; Y_BUTTONS[BTN_RIGHT]  = 360;
		X_BUTTONS[BTN_LEFT]   =  163; Y_BUTTONS[BTN_LEFT]   = 360;
		X_BUTTONS[BTN_TRNGL]  =  879; Y_BUTTONS[BTN_TRNGL]  = 218;
		X_BUTTONS[BTN_CROSS]  =  879; Y_BUTTONS[BTN_CROSS]  = 502;
		X_BUTTONS[BTN_CIRCLE] = 1021; Y_BUTTONS[BTN_CIRCLE] = 360;
		X_BUTTONS[BTN_SQUARE] =  737; Y_BUTTONS[BTN_SQUARE] = 360;
		X_BUTTONS[BTN_R1]     =  126; Y_BUTTONS[BTN_R1]     =  83;
		X_BUTTONS[BTN_R2]     =  126; Y_BUTTONS[BTN_R2]     = 637;
		X_BUTTONS[BTN_L1]     = 1058; Y_BUTTONS[BTN_L1]     =  83;
		X_BUTTONS[BTN_L2]     = 1058; Y_BUTTONS[BTN_L2]     = 637;
		for(int i=BTN_UP; i<=BTN_SQUARE; i++){
			HW_BUTTONS[i] = 60;
			HH_BUTTONS[i] = 60;
			OX_RINGS[i]   = -82;
			OY_RINGS[i]   = -82;
		}
		for(int i=BTN_R1; i<=BTN_L2; i++){
			HW_BUTTONS[i] = 100;
			HH_BUTTONS[i] = 60;
			OX_RINGS[i]   = -120;
			OY_RINGS[i]   = -77;
		}
		
		for(int i=0;i<BUTTON_NUM;i++){
			X_BUTTONS[i]  *= xScale;
			Y_BUTTONS[i]  *= yScale;
			HW_BUTTONS[i] *= xScale;
			HH_BUTTONS[i] *= yScale;
			OX_RINGS[i]   *= xScale;
			OY_RINGS[i]   *= yScale;
		}
		for(int i=0;i<BUTTON_NUM;i++){
			idTouchButtons[i] = -1;
		}
	}
	
	// set Bluetooth State
	public void setBtStatus(WiFiStatus state)
	{
		btState = state;
		invalidate();	// redraw
	}
	
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		// draw bitmap objects
		
		// Bluetooth button
		switch(btState){
		case CONNECTED:
			c.drawBitmap(imgConnected,X_BT_BUTTON,Y_BT_BUTTON,paint);
			break;
		case DISCONNECTED:
		default:
			c.drawBitmap(imgDisconnected,X_BT_BUTTON,Y_BT_BUTTON,paint);
			break;
		}
		
		// Digital button's effect ring
		for(int i=0; i<BUTTON_NUM; i++){
			if((buttonState & BIT_BUTTONS[i]) != 0){
				if(i<BTN_R1){
					c.drawBitmap(
						imgRing1,
						X_BUTTONS[i] + OX_RINGS[i],
						Y_BUTTONS[i] + OY_RINGS[i],
						paint);
				}else{
					c.drawBitmap(
						imgRing2,
						X_BUTTONS[i] + OX_RINGS[i],
						Y_BUTTONS[i] + OY_RINGS[i],
						paint);
				}
			}
		}
	}
	
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	
    	// get touch informations
        int action = event.getAction();
        // int index = (action & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
        int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int eventID = event.getPointerId(index);
        int touchCount = event.getPointerCount();
 
        switch ( action & MotionEvent.ACTION_MASK ) {
 
        // (1) on touch-down
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_POINTER_DOWN:
        	
        	// get the touch point
            int tx =(int)event.getX(index);
            int ty =(int)event.getY(index);
            
            for(int i=0; i<BUTTON_NUM; i++){
				if(idTouchButtons[i] == -1){
					if( (tx >= (X_BUTTONS[i] - HW_BUTTONS[i])) &&
						(tx <= (X_BUTTONS[i] + HW_BUTTONS[i])) &&
						(ty >= (Y_BUTTONS[i] - HH_BUTTONS[i])) &&
						(ty <= (Y_BUTTONS[i] + HH_BUTTONS[i])))
					{
						idTouchButtons[i] = eventID;
						buttonState |= BIT_BUTTONS[i];
						parent.onTouchDigitalButton(buttonState);
					}
				}
			}
            break;
        
        // (2) on touch-move
        case MotionEvent.ACTION_MOVE:
 
            for(index = 0; index < touchCount; index++) {
 
            	// get the touch point
                eventID = event.getPointerId(index);
                tx =(int)event.getX(index);
                ty =(int)event.getY(index);
				
				for(int i=0; i<BUTTON_NUM; i++){
					if(eventID == idTouchButtons[i]){
						if( (tx < (X_BUTTONS[i] - HW_BUTTONS[i])) ||
							(tx > (X_BUTTONS[i] + HW_BUTTONS[i])) ||
							(ty < (Y_BUTTONS[i] - HH_BUTTONS[i])) ||
							(ty > (Y_BUTTONS[i] + HH_BUTTONS[i])))
						{
							idTouchButtons[i] = -1;
							buttonState &= ~BIT_BUTTONS[i];
							parent.onTouchDigitalButton(buttonState);
						}
					}
				}
	            for(int i=0; i<BUTTON_NUM; i++){
					if(idTouchButtons[i] == -1){
						if( (tx >= (X_BUTTONS[i] - HW_BUTTONS[i])) &&
							(tx <= (X_BUTTONS[i] + HW_BUTTONS[i])) &&
							(ty >= (Y_BUTTONS[i] - HH_BUTTONS[i])) &&
							(ty <= (Y_BUTTONS[i] + HH_BUTTONS[i])))
						{
							idTouchButtons[i] = eventID;
							buttonState |= BIT_BUTTONS[i];
							parent.onTouchDigitalButton(buttonState);
						}
					}
				}
            }
            break;
 
        // (3) on touch-up
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
			for(int i=0; i<BUTTON_NUM; i++){
				if(eventID == idTouchButtons[i]){
					idTouchButtons[i] = -1;
					buttonState &= ~BIT_BUTTONS[i];
					parent.onTouchDigitalButton(buttonState);
				}
			}
            break;
        }
        invalidate(); // redraw
        return true;
    }
}
