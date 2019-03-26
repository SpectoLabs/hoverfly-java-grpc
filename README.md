Following these steps to start using Hoverfly-Java-Grpc: 

Pre-requisites:
1. Install Hoverfly self-signed certificate. 
   gRPC client must be able to trust Hoverfly certificate for Hoverfly to perform MITM proxying. This is usually taken care of 
   by Hoverfly-Java, and most of the HTTP clients respects the default JAVA SslContext. However this is not the case for GCP Java Client.
   
   First download the certificate from Hoverfly Git repo: 
   
   `wget https://raw.githubusercontent.com/SpectoLabs/hoverfly/master/core/cert.pem`
   
   Then manually add the hoverfly self-signed certificate to the global java keystore with the following command: 
   `sudo $JAVA_HOME/bin/keytool -import -alias hoverfly -keystore $JAVA_HOME/jre/lib/security/cacerts -file cert.pem`
   
2. Add the jar file as your project dependency

3. You still need to include `hoverfly-java` in your Maven or Gradle build file. 
   You need to use `0.11.4-SNAPSHOT` version:
   
   For Gradle: 
   ```

   repositories {
       maven {
           url 'https://oss.sonatype.org/content/repositories/snapshots'
       }
   }

    dependencies {
        compile 'io.specto:hoverfly-java:0.11.4-SNAPSHOT'
    }
    ```


Usage:

Whenever you need to capture or simulate the GCP APIs, just create a `HoverflyRule` as you would do normally. The only difference is you pass a `new GrpcConfig()` to the static factory method instead of the `localConfigs()` which will run the Hoverfly with gRPC support. 

```$java
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("simulation.json", new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));
```

Side note: I set a `GcpApiSimulationPreprocessor` to remove GCP authentication request body from matching. The GCP OAuth request body contains time-sensitive data, and does not exactly match on previous captured data. This class is also available from hoverfly-java-grpc library.
