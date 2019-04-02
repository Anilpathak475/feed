/*
 * Copyright (C) 2012-2013 Neo Visionaries Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.feed;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/*
*//**//**//**//**//**//**//**//**
 * A {@link WebView} subclass dedicated to Twitter OAuth on Android,
 * using <a href="http://twitter4j.org/">twitter4j</a>.
 *
 * <p>
 * As this class is implemented as a subclass of {@code View}, it can be
 * integrated into the Android layout system seamlessly. This fact
 * makes this class an easily-reusable UI component.
 * </p>
 *
 * <p>
 * To use this class, it is not necessary to review the flow of
 * OAuth handshake. Just implement {@link TwitterOAuthView.Listener}
 * and call {@link #start(String, String, String, boolean, Listener)
 * start()} method. The result of OAuth handshake is reported via
 * either of the listener's methods, {@link
 * Listener#onSuccess(TwitterOAuthView, AccessToken) onSuccess()} or
 * {@link Listener#onFailure(TwitterOAuthView, TwitterOAuthView.Result)
 * onFailure()}.
 * </p>
 *
 * <p>
 * Below is an example {@code Activity} implementation using
 * {@code TwitterOAuthView}.
 * </p>
 *
 * <pre style="border: 1px solid black; margin: 1em; padding: 0.5em;">
 *
 * package twitteroauthview.sample;
 *
 *
 * import twitter4j.auth.AccessToken;
 * import com.neovisionaries.android.twitter.{@link TwitterOAuthView};
 * import com.neovisionaries.android.twitter.{@link TwitterOAuthView.Result};
 * import android.app.Activity;
 * import android.os.Bundle;
 * import android.widget.Toast;
 *
 *
 * public class TwitterOAuthActivity extends Activity implements {@link TwitterOAuthView.Listener}
 * {
 *     <span style="color: darkgreen;">// Replace values of the parameters below with your own.</span>
 *     private static final String CONSUMER_KEY = "YOUR CONSUMER KEY HERE";
 *     private static final String CONSUMER_SECRET = "YOUR CONSUMER SECRET HERE";
 *     private static final String CALLBACK_URL = "YOUR CALLBACK URL HERE";
 *     private static final boolean DUMMY_CALLBACK_URL = true;
 *
 *
 *     private {@link TwitterOAuthView} view;
 *     private boolean oauthStarted;
 *
 *
 *     &#x0040;Override
 *     public void onCreate(Bundle savedInstanceState)
 *     {
 *         super.onCreate(savedInstanceState);
 *
 *         <span style="color: darkgreen;">// Create an instance of TwitterOAuthView.</span>
 *         view = new {@link TwitterOAuthView#TwitterOAuthView(Context) TwitterOAuthView}(this);
 *
 *         setContentView(view);
 *
 *         oauthStarted = false;
 *     }
 *
 *
 *     &#x0040;Override
 *     protected void onResume()
 *     {
 *         super.onResume();
 *
 *         if (oauthStarted)
 *         {
 *             return;
 *         }
 *
 *         oauthStarted = true;
 *
 *         <span style="color: darkgreen;">// Start Twitter OAuth process. Its result will be notified via
 *         // TwitterOAuthView.Listener interface.</span>
 *         view.{@link #start(String, String, String, boolean, Listener)
 *         start}(CONSUMER_KEY, CONSUMER_SECRET, CALLBACK_URL, DUMMY_CALLBACK_URL, this);
 *     }
 *
 *
 *     public void {@link TwitterOAuthView.Listener#onSuccess(TwitterOAuthView, AccessToken)
 *     onSuccess}({@link TwitterOAuthView} view, {@link AccessToken} accessToken)
 *     {
 *         <span style="color: darkgreen;">// The application has been authorized and an access token
 *         // has been obtained successfully. Save the access token
 *         // for later use.</span>
 *         showMessage("Authorized by " + accessToken.{@link AccessToken#getScreenName() getScreenName()});
 *     }
 *
 *
 *     public void {@link TwitterOAuthView.Listener#onFailure(TwitterOAuthView, TwitterOAuthView.Result)
 *     onFailure}({@link TwitterOAuthView} view, {@link TwitterOAuthView.Result Result} result)
 *     {
 *         <span style="color: darkgreen;">// Failed to get an access token.</span>
 *         showMessage("Failed due to " + result);
 *     }
 *
 *
 *     private void showMessage(String message)
 *     {
 *         <span style="color: darkgreen;">// Show a popup message.</span>
 *         Toast.makeText(this, message, Toast.LENGTH_LONG).show();
 *     }
 * }
 * </pre>
 *
 * @author Takahiko Kawasaki
 */
public class TwitterOAuthView extends WebView
{
    /**
     * Tag for logging.
     */
    private static final String TAG = "TwitterOAuthView";


    /**
     * Internal flag for debug logging. Change the value to {@code true}
     * to turn on debug logging.
     */
    private static final boolean DEBUG = false;


    /**
     * Result code of Twitter OAuth process.
     *
     * @author Takahiko Kawasaki
     */
    public enum Result
    {
        /**
         * The application has been authorized by the user and
         * got an access token successfully.
         */
        SUCCESS,


        /**
         * Twitter OAuth process was cancelled. This result code
         * is generated when the internal {@link AsyncTask}
         * subclass was cancelled for some reasons.
         */
        CANCELLATION,


        /**
         * Twitter OAuth process was not even started due to
         * failure of getting a request token. The pair of
         * consumer key and consumer secret was wrong or some
         * kind of network error occurred.
         */
        REQUEST_TOKEN_ERROR,


        /**
         * The application has not been authorized by the user,
         * or a network error occurred during the OAuth handshake.
         */
        AUTHORIZATION_ERROR,


        /**
         * The application has been authorized by the user but
         * failed to get an access token.
         */
        ACCESS_TOKEN_ERROR
    }


    /**
     * Listener to be notified of Twitter OAuth process result.
     *
     * <p>
     * The methods of this listener are called on the UI thread.
     * </p>
     *
     * @author Takahiko Kawasaki
     */
    public interface Listener
    {
        /**
         * Called when the application has been authorized by the user
         * and got an access token successfully.
         *
         * @param view
         * @param accessToken
         */
        void onSuccess(TwitterOAuthView view, AccessToken accessToken);


        /**
         * Called when the OAuth process was not completed successfully.
         *
         * @param view
         * @param result
         */
        void onFailure(TwitterOAuthView view, Result result);
    }


    /**
     * Twitter OAuth task that has been invoked by {@link
     * #start(String, String, String, boolean, Listener) start} method.
     */
    private TwitterOAuthTask twitterOAuthTask;


    /**
     * Flag to call cancel() from within onDetachedFromWindow().
     */
    private boolean cancelOnDetachedFromWindow = true;


    /**
     * Flag for debug logging.
     */
    private boolean isDebugEnabled = DEBUG;


    /**
     * A constructor that calls {@link WebView#WebView(Context, AttributeSet, int)
     * super}(context, attrs, defStyle).
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public TwitterOAuthView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        // Additional initialization.
        init();
    }


    /**
     * A constructor that calls {@link WebView#WebView(Context, AttributeSet)
     * super}(context, attrs).
     *
     * @param context
     * @param attrs
     */
    public TwitterOAuthView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        // Additional initialization.
        init();
    }


    /**
     * A constructor that calls {@link WebView#WebView(Context) super}(context).
     *
     * @param context
     */
    public TwitterOAuthView(Context context)
    {
        super(context);

        // Additional initialization.
        init();
    }


    /**
     * Initialization common for all constructors.
     */
    private void init()
    {
        WebSettings settings = getSettings();

        // Not use cache.
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Enable JavaScript.
        settings.setJavaScriptEnabled(true);
        // Enable zoom control.
        settings.setBuiltInZoomControls(true);

        // Scroll bar
        setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
    }


    public void start(String consumerKey, String consumerSecret,
            String callbackUrl, boolean dummyCallbackUrl,
            Listener listener)
    {
        // Check the given arguments.
        if (consumerKey == null || consumerSecret == null || callbackUrl == null || listener == null)
        {
            throw new IllegalArgumentException();
        }

        // Convert the boolean parameter to a Boolean object to pass it
        // as an argument of AsyncTask.execute().
        Boolean dummy = dummyCallbackUrl;

        TwitterOAuthTask oldTask;
        TwitterOAuthTask newTask;

        synchronized (this)
        {
            // Renew Twitter OAuth task.
            oldTask = twitterOAuthTask;
            newTask = new TwitterOAuthTask();
            twitterOAuthTask = newTask;
        }

        // Cancel an old running task, if not null.
        cancelTask(oldTask);

        // Execute the new task.
        newTask.execute(consumerKey, consumerSecret, callbackUrl, dummy, listener);
    }


    public void cancel()
    {
        TwitterOAuthTask task;

        synchronized (this)
        {
            // Get the reference of the running task.
            task = twitterOAuthTask;
            twitterOAuthTask = null;
        }

        // Cancel a task, if not null.
        cancelTask(task);
    }


    private void cancelTask(TwitterOAuthTask task)
    {
        // If the given argument is null.
        if (task == null)
        {
            // No task to cancel. Nothing to do.
            return;
        }

        // If the task has not been cancelled yet.
        if (!task.isCancelled())
        {
            if (isDebugEnabled())
            {
                Log.d(TAG, "Cancelling a task.");
            }

            // Cancel the task.
            task.cancel(true);
        }

        synchronized (task)
        {
            if (isDebugEnabled())
            {
                Log.d(TAG, "Notifying a task of cancellation.");
            }

            // Notify to interrupt the loop of waitForAuthorization().
            task.notify();
        }
    }


    public boolean isDebugEnabled()
    {
        return isDebugEnabled;
    }


    public void setDebugEnabled(boolean enabled)
    {
        isDebugEnabled = enabled;
    }

    public boolean isCancelOnDetachedFromWindow()
    {
        return cancelOnDetachedFromWindow;
    }


    public void setCancelOnDetachedFromWindow(boolean enabled)
    {
        cancelOnDetachedFromWindow = enabled;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        if (isCancelOnDetachedFromWindow())
        {
            cancel();
        }
    }


    private class TwitterOAuthTask extends AsyncTask<Object, Void, Result>
    {
        private String callbackUrl;
        private boolean dummyCallbackUrl;
        private Listener listener;
        private Twitter twitter;
        private RequestToken requestToken;
        private volatile boolean authorizationDone;
        private volatile String verifier;
        private AccessToken accessToken;


        /**
         * Check whether this task has been cancelled or not.
         *
         * @return
         *         {@code true} if this task has been cancelled.
         */
        private boolean checkCancellation(String context)
        {
            if (!isCancelled())
            {
                return false;
            }

            if (isDebugEnabled())
            {
                Log.d(TAG, "Cancellation was detected in the context of " + context);
            }

            return true;
        }


        @Override
        protected void onPreExecute()
        {
            // Set up a WebViewClient on the UI thread.
            TwitterOAuthView.this.setWebViewClient(new LocalWebViewClient());
        }


        @Override
        protected Result doInBackground(Object... args)
        {
            // Check if this task has been cancelled.
            if (checkCancellation("doInBackground() [on entry]"))
            {
                return Result.CANCELLATION;
            }

            // Name the arguments.
            String consumerKey    = (String)  args[0];
            String consumerSecret = (String)  args[1];
            callbackUrl           = (String)  args[2];
            dummyCallbackUrl      = (Boolean) args[3];
            listener              = (Listener)args[4];

            if (isDebugEnabled())
            {
                debugDoInBackground(args);
            }

            // Create a Twitter instance with the given pair of
            // consumer key and consumer secret.
            twitter = new TwitterFactory().getInstance();
            twitter.setOAuthConsumer(consumerKey, consumerSecret);

            // Get a request token. This triggers network access.
            requestToken = getRequestToken();
            if (requestToken == null)
            {
                // Failed to get a request token.
                return Result.REQUEST_TOKEN_ERROR;
            }

            // Access Twitter's authorization page. After the user's
            // operation, this web view is redirected to the callback
            // URL, which is caught by shouldOverrideUrlLoading() of
            // LocalWebViewClient.
            authorize();

            // Wait until the authorization step is done.
            boolean cancelled = waitForAuthorization();
            if (cancelled)
            {
                // Cancellation was detected while waiting.
                return Result.CANCELLATION;
            }

            // If the authorization has succeeded, 'verifier' is not null.
            if (verifier == null)
            {
                // The authorization failed.
                return Result.AUTHORIZATION_ERROR;
            }

            // Check if this task has been cancelled.
            if (checkCancellation("doInBackground() [before getAccessToken()]"))
            {
                return Result.CANCELLATION;
            }

            // The authorization succeeded. The last step is to get
            // an access token using the verifier.
            accessToken = getAccessToken();
            if (accessToken == null)
            {
                // Failed to get an access token.
                return Result.ACCESS_TOKEN_ERROR;
            }

            // All the steps were done successfully.
            return Result.SUCCESS;
        }


        private void debugDoInBackground(Object... args)
        {
            Log.d(TAG, "CONSUMER KEY = " + (String)args[0]);
            Log.d(TAG, "CONSUMER SECRET = " + (String)args[1]);
            Log.d(TAG, "CALLBACK URL = " + (String)args[2]);
            Log.d(TAG, "DUMMY CALLBACK URL = " + (Boolean)args[3]);

            System.setProperty("twitter4j.debug", "true");
        }


        @Override
        protected void onProgressUpdate(Void... values)
        {
            // Check if this task has been cancelled.
            if (checkCancellation("onProgressUpdate()"))
            {
                // Not load the authorization URL.
                return;
            }

            // In this implementation, onProgressUpdate() is called
            // only from authorize().

            // The authorization URL.
            String url = requestToken.getAuthorizationURL();

            if (isDebugEnabled())
            {
                Log.d(TAG, "Loading the authorization URL: " + url);
            }

            // Load the authorization URL on the UI thread.
            TwitterOAuthView.this.loadUrl(url);
        }


        @Override
        protected void onPostExecute(Result result)
        {
            if (isDebugEnabled())
            {
                Log.d(TAG, "onPostExecute: result = " + result);
            }

            if (result == null)
            {
                // Probably cancelled.
                result = Result.CANCELLATION;
            }

            if (result == Result.SUCCESS)
            {
                // Call onSuccess() method of the listener.
                fireOnSuccess();
            }
            else
            {
                // Call onFailure() method of the listener.
                fireOnFailure(result);
            }

            // Set null to TwitterOAuthView.this.twitterOAuthTask if appropriate.
            clearTaskReference();
        }


        @Override
        protected void onCancelled()
        {
            super.onCancelled();

            // Call onFailure() method of the listener.
            fireOnFailure(Result.CANCELLATION);

            // Set null to TwitterOAuthView.this.twitterOAuthTask if appropriate.
            clearTaskReference();
        }


        private void fireOnSuccess()
        {
            if (isDebugEnabled())
            {
                Log.d(TAG, "Calling Listener.onSuccess");
            }

            // Call onSuccess() method of the listener.
            listener.onSuccess(TwitterOAuthView.this, accessToken);
        }


        private void fireOnFailure(Result result)
        {
            if (isDebugEnabled())
            {
                Log.d(TAG, "Calling Listener.onFailure, result = " + result);
            }

            // Call onFailure() method of the listener.
            listener.onFailure(TwitterOAuthView.this, result);
        }


        private void clearTaskReference()
        {
            synchronized (TwitterOAuthView.this)
            {
                if (TwitterOAuthView.this.twitterOAuthTask == this)
                {
                    TwitterOAuthView.this.twitterOAuthTask = null;
                }
            }
        }


        private RequestToken getRequestToken()
        {
            try
            {
                // Get a request token. This triggers network access.
                RequestToken token = twitter.getOAuthRequestToken();

                if (isDebugEnabled())
                {
                    Log.d(TAG, "Got a request token.");
                }

                return token;
            }
            catch (TwitterException e)
            {
                // Failed to get a request token.
                e.printStackTrace();
                Log.e(TAG, "Failed to get a request token.", e);

                // No request token.
                return null;
            }
        }


        private void authorize()
        {
            // WebView.loadUrl() needs to be called on the UI thread,
            // so trigger onProgressUpdate().
            publishProgress();
        }


        private boolean waitForAuthorization()
        {
            while (authorizationDone == false)
            {
                // Check if this task has been cancelled.
                if (checkCancellation("waitForAuthorization()"))
                {
                    // Cancelled.
                    return true;
                }

                synchronized (this)
                {
                    try
                    {
                        if (isDebugEnabled())
                        {
                            Log.d(TAG, "Waiting for the authorization step to be done.");
                        }

                        // Wait until interrupted.
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        // Interrupted.
                        if (isDebugEnabled())
                        {
                            Log.d(TAG, "Interrupted while waiting for the authorization step to be done.");
                        }
                    }
                }
            }

            if (isDebugEnabled())
            {
                Log.d(TAG, "Finished waiting for the authorization step to be done.");
            }

            // Not cancelled.
            return false;
        }


        private void notifyAuthorization()
        {
            // The authorization step was done.
            authorizationDone = true;

            synchronized (this)
            {
                if (isDebugEnabled())
                {
                    Log.d(TAG, "Notifying that the authorization step was done.");
                }

                // Notify to interrupt the loop in waitForAuthorization().
                this.notify();
            }
        }


        private class LocalWebViewClient extends WebViewClient
        {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                // Something wrong happened during the authorization step.
                Log.e(TAG, "onReceivedError: [" + errorCode + "] " + description);

                // Stop the authorization step.
                notifyAuthorization();
            }


            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
            {
                handler.proceed();
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                super.onPageStarted(view, url, favicon);

                // 11 = Build.VERSION_CODES.HONEYCOMB (Android 3.0)
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                // Check if the given URL is the callback URL.
                if (!url.startsWith(callbackUrl))
                {
                    // The URL is not the callback URL.
                    return false;
                }

                // This web view is about to be redirected to the callback URL.
                if (isDebugEnabled())
                {
                    Log.d(TAG, "Detected the callback URL: " + url);
                }

                // Convert String to Uri.
                Uri uri = Uri.parse(url);

                // Get the value of the query parameter "oauth_verifier".
                // A successful response should contain the parameter.
                verifier = uri.getQueryParameter("oauth_verifier");

                if (isDebugEnabled())
                {
                    Log.d(TAG, "oauth_verifier = " + verifier);
                }

                // Notify that the the authorization step was done.
                notifyAuthorization();

                // Whether the callback URL is actually accessed or not
                // depends on the value of dummyCallbackUrl. If the
                // value of dummyCallbackUrl is true, the callback URL
                // is not accessed.
                return dummyCallbackUrl;
            }
        }


        private AccessToken getAccessToken()
        {
            try
            {
                // Get an access token. This triggers network access.
                AccessToken token = twitter.getOAuthAccessToken(requestToken, verifier);

                if (isDebugEnabled())
                {
                    Log.d(TAG, "Got an access token for " + token.getScreenName());
                }

                return token;
            }
            catch (TwitterException e)
            {
                // Failed to get an access token.
                e.printStackTrace();
                Log.e(TAG, "Failed to get an access token.", e);

                // No access token.
                return null;
            }
        }
    }
}