# JVM Network Utils


Utilities used to manage network actions in JVM (Java Virtual Machine).


## Feature status
- :ballot_box_with_check: Complete
- :white_square_button: Partial complete
- :black_square_button: Incomplete

## Features

- Make Http request
    - :ballot_box_with_check: GET
        - :ballot_box_with_check: Sync
        - :ballot_box_with_check: Async
        - :ballot_box_with_check: Coroutines
    - :ballot_box_with_check: POST
        - :ballot_box_with_check: Sync
        - :ballot_box_with_check: Async
        - [ ] Coroutines
    - :ballot_box_with_check: PUT
        - :ballot_box_with_check: Sync
        - :ballot_box_with_check: Async
        - [ ] Coroutines
    - [ ] PATCH
        - [ ] Sync
        - [ ] Async
        - [ ] Coroutines
    - :ballot_box_with_check: DELETE
        - :ballot_box_with_check: Sync
        - :ballot_box_with_check: Async
        - [ ] Coroutines
- Download resources from server
    - [ ] Get percentage of download
    - [ ] Calculate file size
    - [ ] Real time download status
- :white_square_button: Upload files to server
    - [ ] Get percentage of upload
    - [ ] Calculate time
    - [ ] Real time download status


## Problems

It's not possible to upload large files. I'm working to fix this problem.
If you know how to upload large files by chunks you can fork this repo and make a pull request. 

#### Any help is good. 👌😁

## Download

You can download jar file from [Release](#Release) section or
put in your gradle project the next code:


### Groovy DSL
```groovy
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation "com.github.ushiosan23:networkutils:0.0.2"
    }
```

### Kotlin DSL
```kotlin
    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("com.github.ushiosan23:networkutils:0.0.2")
    }
```

### Maven POM File
```xml 
    <dependencies>
        <dependency>
            <groupId>com.github.ushiosan23</groupId>
            <artifactId>networkutils</artifactId>
            <version>0.0.2</version>
        </dependency>
    </dependencies>
```


## How to use


### Simple http request

- Java
```java
import com.github.ushiosan23.networkutils.http.HttpRequestAction;

class SimpleHttpRequest {

    HttpRequestAction action = new HttpRequestAction("https://api.github.com/users/Ushiosan23");

    // Create asynchronous request
    public void makeSyncRequest() throws Exception {
        System.out.println(action.get().body());
    }

    // Create asynchronous request
    public void makeAsyncRequest() {
        // Action always return the same action
        action.getAsync(action -> {
            System.out.println(action.body());
            return action;
        });
    }

    public static void main(String[] args) throws Exception {
        SimpleHttpRequest request = new SimpleHttpRequest();
        request.makeAsyncRequest();
        request.makeSyncRequest();

        Thread.sleep(5000);
    }
}
```

- Kotlin
```kotlin
import com.github.ushiosan23.networkutils.http.HttpRequestAction


fun main() {
    val request = HttpRequestAction("https://api.github.com/users/Ushiosan23")
    
    // Asynchronous request
    request.getAsync { action ->
    	println(action.body())
        return@getAsync action
    }

    // Synchronous request
    println(request.get().body())
    Thread.sleep(5000)
}
```

- Kotlin Coroutines
```kotlin
import com.github.ushiosan23.networkutils.http.HttpRequestAction
import com.github.ushiosan23.networkutils.http.getAsyncC

suspend fun main() {
    val request = HttpRequestAction("https://api.github.com/users/Ushiosan23")
    
    // Asynchronous request with coroutines
    println(request.getAsyncC().body())

    // Synchronous request
    println(request.get().body())
}
```
