/*
 * ******************************************************************************
 *  *
 *  * Copyright (c) ${year} AirWatch, LLC. All rights reserved.
 *  * This product is protected by copyright and intellectual property laws in
 *  * the United States and other countries as well as by international treaties.
 *  * AirWatch products may be covered by one or more patents listed at
 *  * http://www.vmware.com/go/patents.
 *  *
 * *****************************************************************************
 */

package awdevicecheckin.com.airwatchdevicecheck_in;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;


/**
 * Created by perronj on 11/9/2015.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {

    private String TAG = "AWCHECKIN:MakeRequestTask";
    public static String scriptId = "M2qEmMm6IfxrGpH40n0YCf1zPU65frwET";
    private static AssetManager assetMgr;
    private static File fileDir;

    public MakeRequestTask(AssetManager assetManager, File fileDirectory) {
        assetMgr = assetManager;
        fileDir = fileDirectory;
      }

    /**
     * Background task to call Google Apps Script Execution API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {

        try {
            // ID of the script to call. Acquire this from the Apps Script editor,
            // under Publish > Deploy as API executable.

            Script service = getScriptService();

            // Create an execution request object.
            /*
            ExecutionRequest request = new ExecutionRequest()
                    .setFunction("addOwner")
                    .setParameters(Arrays.asList((Object)"Perron Jones"));
            */
            ExecutionRequest request = new ExecutionRequest()
                    .setFunction("getDeviceRecord")
                    .setParameters(Arrays.asList((Object)"VS986de8c3d8"));

            // Make the API request.
            Operation op = service.scripts().run(scriptId, request).execute();

            // Print results of request.
            if (op.getError() != null) {
                // The API executed, but the script returned an error.
                System.out.println(getScriptError(op));
            } else {
                // The result provided by the API needs to be cast into
                // the correct type, based upon what types the Apps
                // Script function returns. Here, the function returns
                // an Apps Script Object with String keys and values,
                // so must be cast into a Java Map (folderSet).
                Map<String, String> resultSet = (Map<String, String>)(op.getResponse().get("result"));
                if (resultSet == null || resultSet.size() == 0) {
                    Log.d(TAG, "No result returned!");
                } else {
                    Log.d(TAG, "Folders under your root folder:");
                    for (String id: resultSet.keySet()) {
                        Log.d(TAG, "\t" + resultSet.get(id) + " (" + id + ")\n");
                    }
                }
            }
        } catch (GoogleJsonResponseException e) {
            // The API encountered a problem before the script was called.
            Log.e(TAG, e.getMessage());
            e.printStackTrace(System.out);
        } catch (IOException e) {
            // The API encountered a problem before the script was called.
            Log.e(TAG, e.getMessage());
            e.printStackTrace(System.out);
        }

        return null;
    }

    /**
     * Create a HttpRequestInitializer from the given one, except set
     * the HTTP read timeout to be longer than the default (to allow
     * called scripts time to execute).
     *
     * @param {HttpRequestInitializer} requestInitializer the initializer
     *     to copy and adjust; typically a Credential object.
     * @return an initializer with an extended read timeout.
     */
    private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                requestInitializer.initialize(httpRequest);
                // This allows the API to call (and avoid timing out on)
                // functions that take up to 6 minutes to complete (the maximum
                // allowed script run time), plus a little overhead.
                httpRequest.setReadTimeout(380000);
            }
        };
    }

    /**
     * Build and return an authorized Script client service.
     *
     * @param {Credential} credential an authorized Credential object
     * @return an authorized Script client service
     */
    public static Script getScriptService() throws IOException {
        GoogleCredential credential = AWCheckinOAuth.authorize(assetMgr, fileDir);
        return new Script.Builder(
                AWCheckinOAuth.HTTP_TRANSPORT, AWCheckinOAuth.JSON_FACTORY, setHttpTimeout(credential))
                .setApplicationName(AWCheckinOAuth.APPLICATION_NAME)
                .build();
    }

    /**
     * Interpret an error response returned by the API and return a String
     * summary.
     *
     * @param {Operation} op the Operation returning an error response
     * @return summary of error response, or null if Operation returned no
     *     error
     */
    public static String getScriptError(Operation op) {
        if (op.getError() == null) {
            return null;
        }

        // Extract the first (and only) set of error details and cast as a Map.
        // The values of this map are the script's 'errorMessage' and
        // 'errorType', and an array of stack trace elements (which also need to
        // be cast as Maps).
        Map<String, Object> detail = op.getError().getDetails().get(0);
        List<Map<String, Object>> stacktrace =
                (List<Map<String, Object>>)detail.get("scriptStackTraceElements");

        java.lang.StringBuilder sb =
                new StringBuilder("\nScript error message: ");
        sb.append(detail.get("errorMessage"));
        sb.append("\nScript error type: ");
        sb.append(detail.get("errorType"));

        if (stacktrace != null) {
            // There may not be a stacktrace if the script didn't start
            // executing.
            sb.append("\nScript error stacktrace:");
            for (Map<String, Object> elem : stacktrace) {
                sb.append("\n  ");
                sb.append(elem.get("function"));
                sb.append(":");
                sb.append(elem.get("lineNumber"));
            }
        }
        sb.append("\n");
        return sb.toString();
    }

}
