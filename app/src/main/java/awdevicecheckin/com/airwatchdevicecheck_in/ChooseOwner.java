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
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by perronj on 11/8/2015.
 */

public class ChooseOwner extends Activity {

    private SearchView ownerSearchView;
    private ListView ownerListView;
    private Button okBtn;
    private Button cancelBtn;
    private String TAG = "AWCHECKIN:ChooseOwner";
    private ArrayAdapter ownerArrayAdapter;
    private ArrayList<String> ownerArray;
    private String chosenOwner;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message handlerMessage){
            // Get Device Owner from message data
            Bundle msgData = handlerMessage.getData();
            if(msgData != null){
                // populate the owner list view with owners
                String[] owners = msgData.getStringArray(DeviceUtil.DEVICE_OWNER);
                if(owners != null && owners.length >0){
                    if(ownerListView ==null)
                        initiateListView();
                    ownerArray.clear();
                    ownerArray.addAll(Arrays.asList(owners));
                    ownerArrayAdapter.notifyDataSetChanged();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooser);

        chosenOwner = null;
        initiateListView();

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
                // set new owner
                if(chosenOwner != null) {
                    Bundle deviceInfo = DeviceUtil.getDeviceDetails();
                    deviceInfo.putString(DeviceUtil.DEVICE_OWNER, chosenOwner);
                    new MakeRequestTask(getAssets(), getFilesDir(), mHandler,
                            MakeRequestTask.TASK_ADD_UPDATE_DEVICE_RECORD, deviceInfo).
                            execute();
                }
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");

        // Initial call to populate the device owner
        // The callback listener will update the ui and notification
        new MakeRequestTask(getAssets(), getFilesDir(), mHandler,
                MakeRequestTask.TASK_GET_ALL_OWNERS, DeviceUtil.getDeviceDetails()).execute();
    }

    public void initiateListView(){

        ownerArray = new ArrayList<String>();
        ownerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.owner_list_item, ownerArray);
        ownerListView = (ListView) findViewById(R.id.ownerListView);
        ownerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ownerListView.setAdapter(ownerArrayAdapter);

        ownerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.d(TAG, "Clicked on List Item");
                chosenOwner = ownerArray.get(position);
            }
        });
    }

    private class OwnerArrayAdapter extends ArrayAdapter<String> {

        public OwnerArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }
    }
}
