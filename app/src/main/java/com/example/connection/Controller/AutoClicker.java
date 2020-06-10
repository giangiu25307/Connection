package com.example.connection.Controller;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;


public class AutoClicker extends AccessibilityService {
        boolean result;
        float x;
        float y;
    GestureResultCallback callback;
    AccessibilityServiceInfo info;
    public AutoClicker() {
        callback = new GestureResultCallback() {
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
    }

    public void setX(float x) {
        this.x = x/100*72;
    }

    public void setY(float y) {
        this.y = y/100*86;
    }

    @Override
        public void onServiceConnected() {
        info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes=AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.packageNames = null;
        setServiceInfo(info);
        clicker();
    }
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }








    private GestureDescription createClick() {
            // for a single tap a duration of 1 ms is enough
            final int DURATION = 1;

            Path clickPath = new Path();
            clickPath.moveTo(x, y);
            GestureDescription.StrokeDescription clickStroke =
                    new GestureDescription.StrokeDescription(clickPath, 0, DURATION);
            GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
            clickBuilder.addStroke(clickStroke);
            return clickBuilder.build();
        }
        public void clicker(){
             result = this.dispatchGesture(createClick(), callback, null);
            System.out.println(result);
        }

// callback invoked either when the gesture has been completed or cancelled


        // accessibilityService: contains a reference to an accessibility service
// callback: can be null if you don't care about gesture termination


    }


