The trueffect-tpasapi-client-vXXX.jar is the main library.  The other
jars are dependencies.  The "SampleGetCrativeGroup.java" class is
provided as a VERY simple example of using the SDK.

We do not yet have a manual for the SDK, but it is fairly easy to 
navigate the methods of each "Proxy" class.  From the factory
class (see SampleGetCrativeGroup.java), you can see which proxies
it produes, and which methods are on each proxy.  The method names
are fairly consistent across the proxies.

The basic usage pattern is to create an instance of the factory
(TpasapiServiceFactory) with the appropriate URLs and credentials.
Then you ask the factory for a specific service proxy, and execute
any of the proxy's methods to invoke the services.


