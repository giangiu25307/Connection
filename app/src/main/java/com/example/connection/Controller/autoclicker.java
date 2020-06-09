package com.example.connection.Controller;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityService.GestureResultCallback;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class autoclicker{
    boolean result;
    float x=0;
    float y=0;
    GestureResultCallback callback;
    AccessibilityService accessibilityService;
    public autoclicker() {
        accessibilityService= new AccessibilityService() {
            @Override
            public void onAccessibilityEvent(AccessibilityEvent event) {

            }

            @Override
            public void onInterrupt() {

            }
        };
        callback = new AccessibilityService.GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                System.out.println("gesture completed");
            }

            @Override
            public void onCancelled(GestureDescription gestureDescription) {
                super.onCancelled(gestureDescription);
                System.out.println("gesture cancelled");
            }
        };
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
    public boolean clicker(){
        return result = accessibilityService.dispatchGesture(createClick(), callback, null);
    }
// callback invoked either when the gesture has been completed or cancelled
   

    // accessibilityService: contains a reference to an accessibility service
// callback: can be null if you don't care about gesture termination


}
