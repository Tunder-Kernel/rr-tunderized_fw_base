package com.android.mtp;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestListener;

public class TestResultInstrumentation extends InstrumentationTestRunner implements TestListener {
    private boolean mHasError = false;

    @Override
    public void onCreate(Bundle arguments) {
        if (arguments == null) {
            arguments = new Bundle();
        }
        final boolean includeRealDeviceTest =
                Boolean.parseBoolean(arguments.getString("realDeviceTest", "false"));
        if (!includeRealDeviceTest) {
            arguments.putString("notAnnotation", "com.android.mtp.RealDeviceTest");
        }
        super.onCreate(arguments);
        if (includeRealDeviceTest) {
            // Show the test result by using activity because we need to disconnect USB cable
            // from adb host while testing with real MTP device.
            addTestListener(this);
        }
    }

    @Override
    public void addError(Test test, Throwable t) {
        mHasError = true;
        show("ERROR", test, t);
    }

    @Override
    public void addFailure(Test test, AssertionFailedError t) {
        mHasError = true;
        show("FAIL", test, t);
    }

    @Override
    public void endTest(Test test) {
        if (!mHasError) {
            show("PASS", test, null);
        }
    }

    @Override
    public void startTest(Test test) {
        mHasError = false;
    }

    void show(String message) {
        TestResultActivity.show(getContext(), "    " + message);
    }

    private void show(String tag, Test test, Throwable t) {
        String message = "";
        if (t != null && t.getMessage() != null) {
            message = t.getMessage();
        }
        TestResultActivity.show(
                getContext(), String.format("[%s] %s %s", tag, test.toString(), message));
    }
}
