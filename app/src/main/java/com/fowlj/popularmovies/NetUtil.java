package com.fowlj.popularmovies;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * NetUtil houses all the network utility methods unique to this app.
 */

public class NetUtil {

    final static String LOG_TAG = NetUtil.class.getSimpleName();
    final static String BASE_ERROR_MSG = "Error ";
    final static String NAMESERVER_PING_EXEC = "/system/bin/ping -c 1 8.8.8.8";

    /**
     * This method will try and retrieve a String object from the given URL.
     */
    public static String getStringFromURL(URL url) {
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String outputStr = null;

        try {
            //connect to the URL
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //set up the InputStream
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if(inputStream == null) {
                //stop doing work if the InputStream is null
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            //read in the data from the BufferedReader
            String line;
            while((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

            if(builder.length() == 0) {
                //if there is no data then return null
                return null;
            }

            //String output to be returned
            outputStr = builder.toString();

        } catch (ProtocolException e) {
            Log.e(LOG_TAG, BASE_ERROR_MSG, e);
        } catch (IOException e) {
            Log.e(LOG_TAG, BASE_ERROR_MSG, e);
        }

        return outputStr;
    }

    /**
     * Adapted from user @Letiv on StackOverflow
     * http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * The unique feature of this connectivity check compared to others is that it actually looks
     * for packets from a name server instead of just checking with the system if there is
     * a connection. There are instances where you may be connected to wi-fi or mobile data where
     * the device technically has a connection, but there is no actual internet available to the
     * device.
     *
     * @return whether internet connection is available
     */
    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec(NAMESERVER_PING_EXEC);
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { Log.e(LOG_TAG, BASE_ERROR_MSG, e); }
        catch (InterruptedException e) { Log.e(LOG_TAG, BASE_ERROR_MSG, e); }

        return false;
    }
}
