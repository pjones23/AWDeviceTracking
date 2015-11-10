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

/**
 * Created by perronj on 11/10/2015.
 */

import android.content.res.AssetManager;
import android.nfc.Tag;
import android.util.Log;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.script.model.ExecutionRequest;

import org.mortbay.jetty.servlet.Context;

public class AWCheckinOAuth {

    private static String TAG = "AWCheckinOAuth";
    /** Application name. */
    public static final String APPLICATION_NAME =
            "Google Apps Script Execution API Java Quickstart";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/script-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    //private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    public static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    public static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart. */
    private static final List<String> SCOPES =
            Arrays.asList("https://www.googleapis.com/auth/spreadsheets");

    static {
        try {
            HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static GoogleCredential authorize(AssetManager assetMgr, File fileDirectory) throws IOException {
        GoogleCredential credential = null;
        try {
            InputStream p12InputStream = assetMgr.open("AirWatchDeviceTracking-d2d4b34b1e92.p12");
            File key = createFileFromInputStream(p12InputStream, fileDirectory);

            String emailAddress = "909354235047-j1iqjr69dmbutavi1evjmpahc2rbof67@developer.gserviceaccount.com";
            HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
            credential = new GoogleCredential.Builder()
                    .setTransport(HTTP_TRANSPORT)
                    .setJsonFactory(JSON_FACTORY)
                    .setServiceAccountId(emailAddress)
                    .setServiceAccountPrivateKeyFromP12File(key)
                    .setServiceAccountScopes(SCOPES)
                    .setServiceAccountUser("perronjones.aw@gmail.com")
                    .build();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return credential;
    }

    private static File createFileFromInputStream(InputStream inputStream, File fileDirectory) {

        try{
            File f = new File(fileDirectory, "clientSecret.p12");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
            Log.e(TAG, e.getMessage());
        }
        return null;
    }
}
