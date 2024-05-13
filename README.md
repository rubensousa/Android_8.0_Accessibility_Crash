# Android 8.0 Accessibility Crash in Compose

## Reproduction steps

1. Run the app in this repo in an Android 8.0 device
2. Long press the home button to trigger Google Assistant
3. Wait for a few seconds
4. Observe

Crash stacktrace:

```
Process: com.rubensousa.android8composecrash, PID: 10712
java.lang.StackOverflowError: stack size 8MB
at androidx.core.view.accessibility.AccessibilityNodeInfoCompat.setCheckable(AccessibilityNodeInfoCompat.java:2902)
at androidx.compose.ui.platform.AndroidComposeViewAccessibilityDelegateCompat.setIsCheckable(AndroidComposeViewAccessibilityDelegateCompat.android.kt:1388)
at androidx.compose.ui.platform.AndroidComposeViewAccessibilityDelegateCompat.populateAccessibilityNodeInfoProperties(AndroidComposeViewAccessibilityDelegateCompat.android.kt:826)
at androidx.compose.ui.platform.AndroidComposeViewAccessibilityDelegateCompat.createNodeInfo(AndroidComposeViewAccessibilityDelegateCompat.android.kt:554)
at androidx.compose.ui.platform.AndroidComposeViewAccessibilityDelegateCompat.access$createNodeInfo(AndroidComposeViewAccessibilityDelegateCompat.android.kt:193)
at androidx.compose.ui.platform.AndroidComposeViewAccessibilityDelegateCompat$MyNodeProvider.createAccessibilityNodeInfo(AndroidComposeViewAccessibilityDelegateCompat.android.kt:3180)
at android.view.View.populateVirtualStructure(View.java:8019)
at android.view.View.populateVirtualStructure(View.java:8022)
at android.view.View.populateVirtualStructure(View.java:8022)
at android.view.View.onProvideVirtualStructure(View.java:7591)
at android.view.View.dispatchProvideStructureForAssistOrAutofill(View.java:8091)
at android.view.View.dispatchProvideStructure(View.java:8034)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3395)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3459)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3459)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3459)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3459)
at android.view.ViewGroup.dispatchProvideStructure(ViewGroup.java:3459)
at android.app.assist.AssistStructure$WindowNode.<init>(AssistStructure.java:512)
at android.app.assist.AssistStructure.<init>(AssistStructure.java:1908)
at android.app.ActivityThread.handleRequestAssistContextExtras(ActivityThread.java:3035)
at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1807)
at android.os.Handler.dispatchMessage(Handler.java:105)
at android.os.Looper.loop(Looper.java:164)
at android.app.ActivityThread.main(ActivityThread.java:6541)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:240)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:767)
```


The workaround however doesn't work for the following Compose implementations:

1. Dialogs
2. Popups
3. Modal Bottom Sheets

All of the above create their own `AbstractComposeView` so developers can't modify its internal code to include the workaround

## Internal changes in Android 8.0

Android 8.0 introduced the Assist and Autofill feature inside `View` and `ViewGroup`: https://github.com/AndroidSDKSources/android-sdk-sources-for-api-level-26/blob/master/android/view/View.java#L8079

From Android 8.1 on, `dispatchProvideStructure` has an extra `isLaidOut` check that Android 8.0 does not have: https://github.com/AndroidSDKSources/android-sdk-sources-for-api-level-27/blob/master/android/view/ViewGroup.java#L3411

```kotlin
if (!isLaidOut()) {
    Log.v(VIEW_LOG_TAG, "dispatchProvideStructure(): not laid out, ignoring "
        + childrenCount + " children of " + getAccessibilityViewId());
    return;
}
```

## Probable source of the bug

- Compose could be adding a child self-reference when creating the accessibility nodes: a parent could be defining itself as its own children, which would cause an infinite traversal of the view tree.
https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/ui/ui/src/androidMain/kotlin/androidx/compose/ui/platform/AndroidComposeViewAccessibilityDelegateCompat.android.kt;l=434;bpv=1

## Documentation

- https://developer.android.com/training/articles/assistant