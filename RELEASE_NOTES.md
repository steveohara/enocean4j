## Version 1.0.0
* Refactoring of codebase from original enj-library
* Removed redundant code and fixed 100+ static bugs
* Applied a common coding standard and naming convention
* Replaced RxTx with JSerialComm and removed all dependencies on external serial libraries
* Added logging using slf4j
* Changed the handling of EnOcoean devices via a central, statis DeviceManager
* Added new events and added the payload/message to all events for specialised handling

## Version 1.1.0
* Added support for A5-09-04 CO2/Temp/Humidity devices
* Added support for A5-04-02 and A5-04-03 Temp/Humidity device
* Refactored the teach-in notifications
* Refactored the DeviceManager to make it useable across multiple connections
* Made the EEPRegistry static which makes sense
* Tidied up the teach-in logic so that the system will only respond to teach-in requests when it is set to teach-in mode
