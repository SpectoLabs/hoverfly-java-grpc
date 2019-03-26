
# Hoverfly Java gRPC (beta)

hoverfly-java-grpc is an extension to hoverfly-java which provides additional configurations for enabling gRPC capture and simulation. 

Following these steps to start using the beta version of hoverfly-java-grpc:

## Pre-requisites:
1. Install Hoverfly self-signed certificate. 
   A client must be configured to trust Hoverfly certificate for Hoverfly to perform MITM proxying. This is usually taken care of 
   by Hoverfly Java via configuring the default SSLContext. However GCP Java Client neither use default SSLContext or allow setting a
   custom SSLContext. So here is what you need to do instead:
   
   - First download the certificate from Hoverfly Git repo: 
   
     `wget https://raw.githubusercontent.com/SpectoLabs/hoverfly/master/core/cert.pem`
   
   - Then manually add the Hoverfly self-signed cert to the global Java keystore with the following command: 
   
     `sudo $JAVA_HOME/bin/keytool -import -alias hoverfly -keystore $JAVA_HOME/jre/lib/security/cacerts -file cert.pem`
   
2. Add the jar file as your project dependency. (The release version will be available from Maven Central)

3. You still need to include `hoverfly-java` in your Maven or Gradle build file. 
   The latest `0.11.4` version is not released, so for now you should use the SNAPSHOT version by adding the following to your build file: 
   
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
    
    For Maven: 
    ```xml
    <repositories>
        <repository>
            <id>oss-snapshots</id>
                <name>OSS Snapshots</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
            <snapshots>
                <enabled>true</enabled>
                </snapshots>
        </repository>
    </repositories>
 
    <dependency>
         <groupId>io.specto</groupId>
         <artifactId>hoverfly-java</artifactId>
         <version>0.11.4-SNAPSHOT</version>
         <scope>test</scope>
    </dependency>
    ```


## Usage:

Whenever you need to capture or simulate the GCP APIs, just create a `HoverflyRule` as you would do normally. The only difference is that you pass a `new GrpcConfig()` to the static factory method instead of the `localConfigs()`. It will configure `HoverflyRule` to run Hoverfly with gRPC support. 

```$java
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode("simulation.json", new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));
```

Note: The GCP OAuth request body contains time-sensitive data, and does not exactly match on previous captured data. The code above set a `GcpApiSimulationPreprocessor` to remove GCP authentication request body from matching.


(c) SpectoLabs 2019.