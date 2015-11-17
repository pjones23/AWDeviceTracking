package awdevicecheckin.com.airwatchdevicecheck_in;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class Checkin extends AppCompatActivity {

    private Button changeOwnerBtn;
    private TextView ownerTxt;
    private TextView deviceInfoTxt;
    private String TAG = "AWCHECKIN:Checkin";
    private String currentOwner = "None";
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int notificationID = 2003;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message handlerMessage){
            // Get Device Owner from message data
            Bundle msgData = handlerMessage.getData();
            if(msgData != null){
                String owner = msgData.getString(DeviceUtil.DEVICE_OWNER, null);
                String checkInDate = msgData.getString(DeviceUtil.DEVICE_TIME, "");
                if(owner != null) {
                    if (ownerTxt == null)
                        ownerTxt = (TextView) findViewById(R.id.currentOwnerTxt);
                    ownerTxt.setText(owner + "\n" + checkInDate);
                    // set current owner on Notification
                    if(mBuilder != null && mNotifyManager != null) {
                        mBuilder.setContentText("Owner: " + owner);
                        mNotifyManager.notify(notificationID, mBuilder.build());
                    }
                }
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);

        Toolbar checkinToolbar = (Toolbar) findViewById(R.id.checkinToolbar);
        checkinToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(checkinToolbar);

        deviceInfoTxt = (TextView) findViewById(R.id.deviceInfoTxt);

        ownerTxt = (TextView) findViewById(R.id.currentOwnerTxt);

        changeOwnerBtn = (Button) findViewById(R.id.changeOwnerBtn);
        changeOwnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ChooseOwner.class));
            }
        });
        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("AirWatch Device Check-in");
        mBuilder.setSmallIcon(R.drawable.ic_title_airwatch);
        mBuilder.setOngoing(true);

        Intent checkinIntent = new Intent();
        checkinIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName(getApplicationContext(), "awdevicecheckin.com.airwatchdevicecheck_in.Checkin");
        checkinIntent.setComponent(cn);

        // Gets a PendingIntent
        PendingIntent checkinPendingIntent = PendingIntent.getActivity(this, 0, checkinIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(checkinPendingIntent);

        // Displays the progress bar for the first time.
        mNotifyManager.notify(notificationID, mBuilder.build());


    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "onResume");

        Bundle deviceInfo = DeviceUtil.getDeviceDetails();
        deviceInfoTxt.setText(DeviceUtil.DEVICE_NAME + ": " + deviceInfo.getString(DeviceUtil.DEVICE_NAME, "")
            + "\n" + DeviceUtil.DEVICE_MODEL + ": " + deviceInfo.getString(DeviceUtil.DEVICE_MODEL, "")
            + "\n" + DeviceUtil.DEVICE_OS + ": " + deviceInfo.getString(DeviceUtil.DEVICE_OS, "")
            + "\n" + DeviceUtil.DEVICE_SERIAL + ": " + deviceInfo.getString(DeviceUtil.DEVICE_SERIAL, "")
        );


        // Initial call to get the current device owner.
        // The callback listener will update the ui and notification
        new MakeRequestTask(this, getAssets(), getFilesDir(), mHandler,
                MakeRequestTask.TASK_GET_DEVICE_OWNER, deviceInfo).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_checkin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync_device_info:
                // refresh the list of owners
                new MakeRequestTask(this, getAssets(), getFilesDir(), mHandler,
                        MakeRequestTask.TASK_GET_DEVICE_OWNER, DeviceUtil.getDeviceDetails()).execute();
            case R.id.action_about:
                Log.d(TAG, "About");
                showAboutDialog();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAboutDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);

        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            builder.setMessage("Version: " + version
            + "\n\nDeveloper: Perron Jones"
            + "\nEmail: perronjones@air-watch.com"
            + "\n\nClick the link below for device tracking page.\n");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Name Not Found Exception occurred showing about dialog. e: " + e.getMessage());
        }

        TextView spreadsheetLink = new TextView(this);
        spreadsheetLink.setClickable(true);
        spreadsheetLink.setGravity(Gravity.CENTER_HORIZONTAL);
        spreadsheetLink.setMovementMethod(LinkMovementMethod.getInstance());
        String link = "<a href='https://docs.google.com/spreadsheets/d/1PcjTdM1OGyqVA9ePNm-7dMqlI246xuLH4tik4EvX6tc/edit?usp=sharing'>Device Tracking Spreadsheet</a>";
        spreadsheetLink.setText(Html.fromHtml(link));
        builder.setView(spreadsheetLink);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

}
