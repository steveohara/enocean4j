## Version 1.0.0
* Refactoring of codebase from original enj-library
* Removed redundant code and fixed 100+ static bugs
* Applied a common coding standard and naming convention
* Replaced RxTx with JSerialComm and removed all dependencies on external serial libraries
* Added logging using slf4j
* Changed the handling of EnOcean devices via a central, static DeviceManager
* Added new events and added the payload/message to all events for specialised handling

## Version 1.1.0
* Added support for A5-09-04 CO2/Temp/Humidity devices
* Added support for A5-04-02 and A5-04-03 Temp/Humidity device
* Refactored the teach-in notifications
* Refactored the DeviceManager to make it usable across multiple connections
* Made the EEPRegistry static which makes sense
* Tidied up the teach-in logic so that the system will only respond to teach-in requests when it is set to teach-in mode

## Version 1.1.1
* Added support for D2-32-XX CT Clamp devices

## Version 1.2.0
* fix humidity for EEP A5-04-01/-02 (wrong offset)
* Code hardening and javadoc cleanup
* Upgraded to jSerialComm v2.6.3
