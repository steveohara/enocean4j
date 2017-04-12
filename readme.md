# EnOcean4J Java Library
enocean4j is a fork of the [EnJ-Library](https://github.com/dog-gateway/enj-library) that takes a more generalised view of EnOcean protocol and how to work with it.
The codebase has been re-factored considerably to bring it into line with common patterns.

Some of the changes are listed below;

* Introduction of wide-scale logging using SL4J
* Large number of re-factors to improve code performance and bring it into line with standard Maven structure
* Hundreds of bug-fixes for static code issues and code correctness
* Replacement of the RxTxComm library with JSerialComm
* Changes to the way in which the attribute notifications are handled

The main driver for taking this on is the same as that for j2mod - we at 4NG have a need for an industrial grade  protocol library for the SMARTset product and 
although EnJ-Library gets you a very long way forward, it doesn't meet our requirements.

## Note
One of the things that this library does not support at this point in time is OSGi bundling. This is for two reasons; 

1. JSerialComm is not packaged for OSGi
2. enocean4j uses package scanning to find all EEPs

When we've worked with the library a bit more, we will fix these issues.

## Acknowledgements
Thanks to Dario Bonino and Andrea Biasi for all of their work on the original library at [e-lite](https://elite.polito.it/).