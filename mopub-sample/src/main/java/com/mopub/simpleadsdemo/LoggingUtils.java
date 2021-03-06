package com.mopub.simpleadsdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MoPubErrorCode;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Used to intercept logs so that we can view logs at a lower level
 * than Verbose (ie. Level.FINEST). This will show a toast when we
 * receive a matching error from the mopub sdk.
 */
public class LoggingUtils {
    private LoggingUtils() {
    }

    /**
     * The name of the custom logger we're looking for
     */
    private static final String LOGGER_NAME = "com.mopub";

    private static boolean sEnabled;

    /**
     * Makes it so that this app can intercept Level.FINEST log messages.
     * This is not thread safe.
     *
     * @param context Needs a context to send toasts.
     */
    static void enableCanaryLogging(@NonNull final Context context) {
        if (sEnabled) {
            return;
        }

        final Handler handler = new SampleAppLogHandler(context.getApplicationContext());
        final Logger logger = getLogger();

        logger.setLevel(Level.ALL);
        logger.addHandler(handler);

        sEnabled = true;
    }

    private static Logger getLogger() {
        // This makes sure the static block in MoPubLog is executed before
        // LogManager#getLogManager is called.
        MoPubLog.c("Canary level logging enabled");

        return LogManager.getLogManager().getLogger(LOGGER_NAME);
    }

    private static class SampleAppLogHandler extends Handler {

        @NonNull
        private final Context mContext;

        protected SampleAppLogHandler(@NonNull final Context context) {
            super();
            mContext = context;
        }

        @Override
        public void publish(final LogRecord logRecord) {
            // Toasts the warmup message if X-Warmup flag is set to 1
            if (logRecord != null && MoPubErrorCode.WARMUP.toString().equals(logRecord.getMessage())) {
                Utils.logToast(mContext, MoPubErrorCode.WARMUP.toString());
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
}

