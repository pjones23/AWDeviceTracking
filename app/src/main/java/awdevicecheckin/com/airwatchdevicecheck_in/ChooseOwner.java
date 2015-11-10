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

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

/**
 * Created by perronj on 11/8/2015.
 */

public class ChooseOwner extends Activity {

    private SearchView ownerSearchView;
    private ListView ownerListView;
    private Button okBtn;
    private Button cancelBtn;
    private String TAG = "AWCHECKIN:ChooseOwner";

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message handlerMessage){
            // Get Device Owner from message data
            Bundle msgData = handlerMessage.getData();
            if(msgData != null){

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser);

        Button cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Set new owner
                MakeRequestTask mrt = new MakeRequestTask(getAssets(), getFilesDir(), mHandler, -1, null);
                mrt.execute();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

}
