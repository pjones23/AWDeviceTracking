function testAddOwner() {
  addOwner("Joe No");
}

function testGetOwners() {
  var owners = getOwners();
  Logger.log(owners)
}

function testInsertOrUpdateDeviceRecord() {
  insertOrUpdateDeviceRecord("Samsung", "SM-T230NU", "5.0.1", "VS986de8c3d8", "Perron Jones");
}

function testGetDeviceRecord() {
  var device = getDeviceRecord("VS986de38c3d8");
  Logger.log(device)
}

function addOwner(ownerName) {
  ownerName = ownerName.trim();

  // The code below opens a spreadsheet using its id and logs the name for it.
  // Note that the spreadsheet is NOT physically opened on the client side.
  // It is opened on the server only (for modification by the script).
  var ss = SpreadsheetApp.openById('1PcjTdM1OGyqVA9ePNm-7dMqlI246xuLH4tik4EvX6tc');
  SpreadsheetApp.setActiveSpreadsheet(ss);
  // Set the 2nd sheet (Owners) active in the active spreadsheet
  var ownersSheet = SpreadsheetApp.setActiveSheet(ss.getSheets()[1]);

  var lastRow = ownersSheet.getLastRow();
  if(lastRow > 0) {
    var owners = ownersSheet.getSheetValues(1, 1, lastRow, 1);

    var add = true;
    if(owners != null && owners.length > 0){
      for(i = 0; i < owners.length; i++){
        if(owners[i].indexOf(ownerName) > -1) {
          add = false;
          break;
        }
      }
    }

    if(add){
      Logger.log("Adding new owner, " + ownerName);
      ownersSheet.appendRow([ownerName]);
      ownersSheet.sort(1);
      SpreadsheetApp.flush();
    }
    else {
      Logger.log("Owner already exists");
    }
  }
  else {
    Logger.log("Adding new owner, " + ownerName);
    ownersSheet.appendRow([ownerName]);
    ownersSheet.sort(1);
    SpreadsheetApp.flush();
  }
}

function getOwners() {
  // The code below opens a spreadsheet using its id and logs the name for it.
  // Note that the spreadsheet is NOT physically opened on the client side.
  // It is opened on the server only (for modification by the script).
  var ss = SpreadsheetApp.openById('1PcjTdM1OGyqVA9ePNm-7dMqlI246xuLH4tik4EvX6tc');
  SpreadsheetApp.setActiveSpreadsheet(ss);
  // Set the 2nd sheet (Owners) active in the active spreadsheet
  var ownersSheet = SpreadsheetApp.setActiveSheet(ss.getSheets()[1]);

  var lastRow = ownersSheet.getLastRow();
  if(lastRow > 0) {
    var owners = ownersSheet.getSheetValues(1, 1, lastRow, 1);
    return owners;
  }
  else {
    return null;
  }

}

function insertOrUpdateDeviceRecord(deviceName, deviceModel, OSVersion, serial, ownerName) {
  // The code below opens a spreadsheet using its id and logs the name for it.
  // Note that the spreadsheet is NOT physically opened on the client side.
  // It is opened on the server only (for modification by the script).
  var ss = SpreadsheetApp.openById('1PcjTdM1OGyqVA9ePNm-7dMqlI246xuLH4tik4EvX6tc');
  SpreadsheetApp.setActiveSpreadsheet(ss);
  // Set the 1st sheet (Devices) active in the active spreadsheet
  var devicesSheet = SpreadsheetApp.setActiveSheet(ss.getSheets()[0]);

  var deviceInfo = [deviceName.trim(), deviceModel.trim(), OSVersion.trim(), serial.trim(), ownerName.trim(), new Date().toLocaleString()]


  var lastRow = devicesSheet.getLastRow();
  var updateRow = -1;
  if(lastRow > 0) {
    var devices = devicesSheet.getSheetValues(1, 1, lastRow, 6);

    var add = true;
    Logger.log(devices);
    if(devices != null && devices.length > 0){
      for(i = 0; i < devices.length; i++){
        if(devices[i][3].indexOf(serial.trim()) > -1) {
          add = false;
          updateRow = i + 1;
          break;
        }
      }
    }

    if(add){
      Logger.log("Adding new device, " + deviceName);
      devicesSheet.appendRow(deviceInfo);
      devicesSheet.autoResizeColumn(1);
      devicesSheet.autoResizeColumn(2);
      devicesSheet.autoResizeColumn(3);
      devicesSheet.autoResizeColumn(4);
      devicesSheet.autoResizeColumn(5);
      devicesSheet.autoResizeColumn(6);
      SpreadsheetApp.flush();
    }
    else {
      Logger.log("Updating Device");
      var range = devicesSheet.getRange(updateRow, 1, 1, 6);
      range.setValues([deviceInfo]);
      devicesSheet.autoResizeColumn(1);
      devicesSheet.autoResizeColumn(2);
      devicesSheet.autoResizeColumn(3);
      devicesSheet.autoResizeColumn(4);
      devicesSheet.autoResizeColumn(5);
      devicesSheet.autoResizeColumn(6);
      SpreadsheetApp.flush();
    }
  }
  else {
    Logger.log("Adding new device, " + deviceName);
    devicesSheet.appendRow(deviceInfo);
    devicesSheet.autoResizeColumn(1);
    devicesSheet.autoResizeColumn(2);
    devicesSheet.autoResizeColumn(3);
    devicesSheet.autoResizeColumn(4);
    devicesSheet.autoResizeColumn(5);
    devicesSheet.autoResizeColumn(6);
    SpreadsheetApp.flush();
  }
}

function getDeviceRecord(serial){
  // The code below opens a spreadsheet using its id and logs the name for it.
  // Note that the spreadsheet is NOT physically opened on the client side.
  // It is opened on the server only (for modification by the script).
  var ss = SpreadsheetApp.openById('1PcjTdM1OGyqVA9ePNm-7dMqlI246xuLH4tik4EvX6tc');
  SpreadsheetApp.setActiveSpreadsheet(ss);
  // Set the 1st sheet (Devices) active in the active spreadsheet
  var devicesSheet = SpreadsheetApp.setActiveSheet(ss.getSheets()[0]);



  var lastRow = devicesSheet.getLastRow();
  var device = null;
  if(lastRow > 1) {
    var devices = devicesSheet.getSheetValues(1, 1, lastRow, 6);

    if(devices != null && devices.length > 0){
      for(i = 0; i < devices.length; i++){
        if(devices[i][3].indexOf(serial.trim()) > -1) {
          device = devices[i];
          break;
        }
      }
    }
  }
  return device;

}
