
# Hoverfly Java gRPC

![CircleCI](https://img.shields.io/circleci/build/github/SpectoLabs/hoverfly-java-grpc/master)
![Maven Central](https://img.shields.io/maven-central/v/io.specto/hoverfly-java-grpc)

hoverfly-java-grpc is an extension to [hoverfly-java](https://github.com/SpectoLabs/hoverfly-java) which provides additional configurations for enabling gRPC capture and simulation. 

Following these steps to start using hoverfly-java-grpc:

## Pre-requisites

1. Install Hoverfly self-signed certificate. 
   A client must be configured to trust Hoverfly certificate for Hoverfly to perform MITM proxying. This is usually taken care of 
   by Hoverfly Java via configuring the default `SSLContext`. However GCP Java Client neither use default `SSLContext` or allow setting a
   custom `SSLContext`. So here is what you need to do instead:
   
   - First download the certificate from Hoverfly Git repo: 
   
     `wget https://raw.githubusercontent.com/SpectoLabs/hoverfly/master/core/cert.pem`
     
   
   - Then manually add the Hoverfly self-signed cert to the global Java keystore with the following command:
   
     `sudo $JAVA_HOME/bin/keytool -import -alias hoverfly -keystore $JAVA_HOME/jre/lib/security/cacerts -file cert.pem` 
   
2. Import both `hoverfly-java-grpc` and `hoverfly-java` (version `0.12.0` or above) into your Maven or Gradle build file.
   
   For Gradle: 
   ```groovy
    dependencies {
        testCompile 'io.specto:hoverfly-java-grpc:0.12.0'
        testCompile 'io.specto:hoverfly-java:0.12.0'
    }
    ```
    
    For Maven: 
    ```xml
    <dependencies>
        <dependency>
              <groupId>io.specto</groupId>
              <artifactId>hoverfly-java-grpc</artifactId>
              <version>0.12.0</version>
              <scope>test</scope>
        </dependency>
        <dependency>
             <groupId>io.specto</groupId>
             <artifactId>hoverfly-java</artifactId>
             <version>0.12.0</version>
             <scope>test</scope>
       </dependency>
    </dependencies>
    ```

   
## Usage

Whenever you need to capture or simulate the GCP APIs, just create a `HoverflyRule` as you would do normally. The only difference is that you pass a `new GrpcConfig()` to the static factory method instead of the `localConfigs()`. It will configure `HoverflyRule` to run Hoverfly with gRPC support. 

```java
    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureOrSimulationMode(
            "simulation.json", 
            new GrpcConfig().simulationPreprocessor(new GcpApiSimulationPreprocessor()));
```

Note: The GCP OAuth request body contains time-sensitive data, and does not exactly match on previous captured data. The code above set a `GcpApiSimulationPreprocessor` to remove GCP authentication request body from matching.


## CI/CD

Before you run your tests that use hoverfly-java-grpc in your build pipeline, you need to install Hoverfly self-signed cert to the default JAVA keystore. 

You can use the following command in your CI configs:

`sudo $JAVA_HOME/bin/keytool -import -alias hoverfly -keystore $JAVA_HOME/jre/lib/security/cacerts -storepass ${KEY_STORE_PASS} -noprompt -file cert.pem`

You can either download the certificate from [here](https://raw.githubusercontent.com/SpectoLabs/hoverfly/master/core/cert.pem) or store it in your project. 

The default JAVA keystore pass is `changeit`


## Licensing
Use hoverfly-java-grpc requires a valid license. You need to make sure your license file is named `hoverfly_license` and can be found on classpath. 

Putting the license file under `test/resources` folder is usually sufficient. 

Please contact [SpectoLabs](https://specto.io/contact/) if you require a license.


(c) SpectoLabs 2019.
