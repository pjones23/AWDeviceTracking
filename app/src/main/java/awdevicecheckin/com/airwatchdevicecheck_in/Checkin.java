package awdevicecheckin.com.airwatchdevicecheck_in;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class Checkin extends Activity {

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
                if(owner != null) {
                    if (ownerTxt == null) {
                        ownerTxt = (TextView) findViewById(R.id.currentOwnerTxt);
                    }
                    ownerTxt.setText(owner);
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
        mBuilder.setSmallIcon(R.drawable.notification_icon);
        mBuilder.setOngoing(true);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(notificationID, mBuilder.build());


    }

    @Override
    protected void onResume(){
        super.onResume();

        Log.d(TAG, "onResume");


        // Initial call to get the current device owner.
        // The callback listener will update the ui and notification
        new MakeRequestTask(getAssets(), getFilesDir(), mHandler,
                MakeRequestTask.TASK_GET_DEVICE_OWNER, DeviceUtil.getDeviceDetails()).execute();
    }

}
