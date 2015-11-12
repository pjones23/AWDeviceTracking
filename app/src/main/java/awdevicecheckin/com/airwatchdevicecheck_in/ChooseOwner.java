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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by perronj on 11/8/2015.
 */

public class ChooseOwner extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private ListView ownerListView;
    private SearchView ownerSearchView;
    private MenuItem searchMenuItem;
    private Button okBtn;
    private Button cancelBtn;
    private String TAG = "AWCHECKIN:ChooseOwner";
    private ArrayAdapter ownerArrayAdapter;
    private Filter searchFilter;
    private ArrayList<String> ownerArray;
    private String chosenOwner;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message handlerMessage){
            // Get Device Owner from message data
            Bundle msgData = handlerMessage.getData();
            if(msgData != null){
                int task = msgData.getInt(MakeRequestTask.TASK_HEADER, -1);
                switch (task) {
                    case MakeRequestTask.TASK_GET_ALL_OWNERS:
                        // populate the owner list view with owners
                        String[] owners = msgData.getStringArray(DeviceUtil.DEVICE_OWNER);
                        if (owners != null && owners.length > 0) {
                            if (ownerListView == null)
                                initiateListView();
                            ownerArray.clear();
                            ownerArray.addAll(Arrays.asList(owners));
                            ownerArrayAdapter.notifyDataSetChanged();
                        }
                        break;
                    case MakeRequestTask.TASK_ADD_UPDATE_DEVICE_RECORD:
                        finish();
                        break;
                    case MakeRequestTask.TASK_ADD_OWNER:
                        // refresh the list of owners
                        executeTask(MakeRequestTask.TASK_GET_ALL_OWNERS, DeviceUtil.getDeviceDetails());

                        // reset adapter because adapter does not update after filtering (common Android issue)
                        ownerArray.clear();
                        ownerArray = null;
                        ownerArrayAdapter.clear();
                        ownerArrayAdapter = null;
                        searchFilter = null;
                        ownerSearchView.setQuery("", false);
                        searchMenuItem.collapseActionView();
                        initiateListView();
                        break;
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

        Toolbar chooserToolbar = (Toolbar) findViewById(R.id.chooserToolbar);
        setSupportActionBar(chooserToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setIcon(R.drawable.ic_title_airwatch);

        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        okBtn = (Button) findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set new owner
                if(chosenOwner != null) {
                    Bundle deviceInfo = DeviceUtil.getDeviceDetails();
                    deviceInfo.putString(DeviceUtil.DEVICE_OWNER, chosenOwner);
                    executeTask(MakeRequestTask.TASK_ADD_UPDATE_DEVICE_RECORD, deviceInfo);
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
        executeTask(MakeRequestTask.TASK_GET_ALL_OWNERS, DeviceUtil.getDeviceDetails());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chooser, menu);

        searchMenuItem = menu.findItem(R.id.action_search);
        ownerSearchView = (SearchView) searchMenuItem.getActionView();
        ownerSearchView.setOnQueryTextListener(this);
        ownerSearchView.setQueryHint(getString(R.string.search_hint));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Settings" item, show the app settings UI...
                Log.d(TAG, "selected search");
                return true;
            case R.id.action_add_owner:
                Log.d(TAG, "selected add owner");
                showAddOwnerDialog();
                return true;
            case R.id.action_sync_owners:
                // refresh the list of owners
                executeTask(MakeRequestTask.TASK_GET_ALL_OWNERS, DeviceUtil.getDeviceDetails());
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void initiateListView(){

        ownerArray = new ArrayList<String>();
        ownerArrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.owner_list_item, ownerArray);
        searchFilter = ownerArrayAdapter.getFilter();
        ownerListView = (ListView) findViewById(R.id.ownerListView);
        ownerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ownerListView.setAdapter(ownerArrayAdapter);

        ownerListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.d(TAG, "Clicked on List Item");
                chosenOwner =((TextView)view).getText().toString();
            }
        });
    }

    protected void executeTask(int task, Bundle params){
        new MakeRequestTask(this, getAssets(), getFilesDir(), mHandler,
               task, params).execute();
    }

    private void showAddOwnerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Owner");
        builder.setMessage("Enter new owner name.");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, input.getText().toString());
                Bundle newOwnerBundle = new Bundle();
                newOwnerBundle.putString(DeviceUtil.DEVICE_OWNER, input.getText().toString().trim());
                executeTask(MakeRequestTask.TASK_ADD_OWNER, newOwnerBundle);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public boolean onQueryTextChange(String newText) {
        if(searchFilter != null)
            searchFilter.filter(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String newText) {
        return false;
    }

}
