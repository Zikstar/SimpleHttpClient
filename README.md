First build the project in the IDE of your choice preferably IntelliJ IDEA.
This is equivalent to javac. Since we need to compile our code in Java before 
execution. Afterwards you can run the following commands to test

Also my curl like statement is 'com.mpdev.httpc' instead of 'httpc' because I have 
to write the fully qualified name of the java file including the package names

## GET Testing

#### 1. GET with query parameters

```java com.mpdev.httpc get "http://httpbin.org/get?course=networking&assignment=1"```

```java com.mpdev.httpc get "http://httpbin.org/get?course=networking&assignment=1" -v```

#### 2. GET with headers

```java com.mpdev.httpc get -h "Authorization:Basic YWxhZGRpbjpvcGVuc2VzYW1l" "http://httpbin.org/get?course=networking&assignment=1"```

```java com.mpdev.httpc get -v -h "Authorization:Basic YWxhZGRpbjpvcGVuc2VzYW1l" "http://httpbin.org/get?course=networking&assignment=1"```

#### 3. GET with output file [-o]

```java com.mpdev.httpc -h "Authorization:Basic YWxhZGRpbjpvcGVuc2VzYW1l" "http://httpbin.org/get?course=networking&assignment=1" -v -o get.json```

```java com.mpdev.httpc get -o outputTxtFile.txt "http://httpbin.org/status/418"```

#### 4. GET with Redirection

```java com.mpdev.httpc get -r "http://httpbin.org/redirect-to?url=http://httpbin.org/get?course=networking&assignment=1"```

#### 5. Multiple Header Support [-h]*

Note: You have to separate each header via comma ',' and Quote all headers together.

```java com.mpdev.httpc get -h "Authorization:Basic YWxhZGRpbjpvcGVuc2VzYW1l,Name: Isaac" "http://httpbin.org/get?course=networking&assignment=1" -v -o get.json```

## POST Testing

#### 1. POST with inline data [-d]

```java com.mpdev.httpc post -h Content-Type:application/json -d "{\"Assignment\": 1}" "http://httpbin.org/post"```

```java com.mpdev.httpc post -h Content-Type:application/json -d "{\"Assignment\": 1}" "http://httpbin.org/post" -v```

#### 2. POST with File [-f] (Enter full path of file)

```java com.mpdev.httpc post http://httpbin.org/post -f "/Users/isaac/Downloads/Comp6461_LA1/src/com/mpdev/post.json"```

```java com.mpdev.httpc post http://httpbin.org/post -f "/Users/isaac/Downloads/Comp6461_LA1/src/com/mpdev/post.json" -v``` 
