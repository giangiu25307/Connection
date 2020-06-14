package com.example.connection.Controller;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;

import android.view.accessibility.AccessibilityEvent;

import com.example.connection.View.Connection;


public class AutoClicker extends AccessibilityService {
    private static AutoClicker mAutoClicker;
        boolean result;

    AccessibilityServiceInfo info;
    GestureResultCallback callback = new GestureResultCallback() {
        @Override
        public void onCompleted(GestureDescription gestureDescription) {
            System.out.println("gesture completed");
            super.onCompleted(gestureDescription);

        }

        @Override
        public void onCancelled(GestureDescription gestureDescription) {
            System.out.println("gesture cancelled");
            super.onCancelled(gestureDescription);

        }
    };
    public AutoClicker() {
    }


    @Override
        public void onServiceConnected() {

        super.onServiceConnected();
        info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.packageNames = null;
        setServiceInfo(info);
        mAutoClicker=this;

    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


    }

    @Override
    public void onInterrupt() {

    }








    private GestureDescription createClick() {
            Path clickPath = new Path();
            clickPath.moveTo(Connection.touchX,Connection.touchY);
            GestureDescription.StrokeDescription clickStroke =
                    new GestureDescription.StrokeDescription(clickPath, 0, 1);
            GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
            clickBuilder.addStroke(clickStroke);
            return clickBuilder.build();
        }
        public void clicker() {

             result = dispatchGesture(createClick(), callback, null);
            System.out.println(result);
        }

public static AutoClicker getInstance(){
     return mAutoClicker;
}
    }


