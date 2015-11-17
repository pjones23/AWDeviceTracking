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

import android.os.Build;
import android.os.Bundle;

/**
 * Created by perronj on 11/10/2015.
 */
public class DeviceUtil {

    public static String DEVICE_NAME = "Device Name";
    public static String DEVICE_MODEL = "Device Model";
    public static String DEVICE_OS = "OS Version";
    public static String DEVICE_SERIAL = "Serial";
    public static String DEVICE_OWNER = "Owner";
    public static String DEVICE_TIME = "Time Stamp";

    public static Bundle getDeviceDetails(){
        Bundle deviceInfo = new Bundle();


        deviceInfo.putString(DEVICE_NAME, ((Build.BRAND != null && Build.BRAND != "") ? Build.BRAND
                : Build.MANUFACTURER));
        deviceInfo.putString(DEVICE_MODEL, Build.MODEL);
        deviceInfo.putString(DEVICE_OS, Build.VERSION.RELEASE + " (API: " + Build.VERSION.SDK_INT + ")");
        deviceInfo.putString(DEVICE_SERIAL, ((Build.SERIAL != null && Build.SERIAL != "") ? Build.SERIAL
                : Build.HARDWARE + "-" + Build.DISPLAY));

        return deviceInfo;
    }

}
