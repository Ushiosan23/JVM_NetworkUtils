# JVM Network Utils


Utilities used to manage network actions in JVM (Java Virtual Machine).


## Features

- Make Http request
    - [x] GET
        - [x] Sync
        - [x] Async
    - [ ] POST
        - [ ] Sync
        - [ ] Async
    - [ ] PUT
        - [ ] Sync
        - [ ] Async
    - [ ] PATCH
        - [ ] Sync
        - [ ] Async
    - [ ] DELETE
        - [ ] Sync
        - [ ] Async
- Download resources from server
    - [ ] Get percentage of download
    - [ ] Calculate file size
    - [ ] Real time download status
- Upload files to server
    - [ ] Get percentage of upload
    - [ ] Calculate time
    - [ ] Real time download status


## Download

You can download jar file from [Release](#Release) section or
put in your gradle project the next code:


### Groovy DSL
```groovy
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation "com.github.ushiosan23:networkutils:0.0.1"
    }
```

### Kotlin DSL
```kotlin
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.github.ushiosan23:networkutils:0.0.1")
    }
```

### Maven POM File
```xml 
    <dependencies>
        <dependency>
            <groupId>com.github.ushiosan23</groupId>
            <artifactId>networkutils</artifactId>
            <version>0.0.1</version>
        </dependency>
    </dependencies>
```
