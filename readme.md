# EnOcean4J Java Library #
enocean4j is a fork of the EnJ-Library that takes a more generalised view of EnOcean protocol and how to work with it.
The codebase has been re-factored considerably to bring it into line with common patterns.

Some of the changes are listed below;

* Introduction of wide-scale logging using SL4J
* Large number of re-factors to improve code performance and bring it into line with standard Maven structure
* Hundreds of bug-fixes for static code issues and bad-practise
* Replacement of the RxTxComm library with JSerialComm
* Changes to the way in which the attribute notifications are handled

The main driver for taking this on is the same as that for j2mod - we at 4NG have a need for a robust and well tested protocol library for the SMARTset product and 
although EnJ-Library get's you a long way forward, it doesn't meet our rather exacting requirements.

One of things that this library does not support at this point in time is OSGi bundling. This s for two reasons; a) the JSerialComm is not packaged for OSGi and b) enocean4j uses package scanning to find all EEPs
When we've worked with the library a bit more, we will fix these issues.

Thanks to Dario Bonino and Andrea Biasi for all of their work on the original library.