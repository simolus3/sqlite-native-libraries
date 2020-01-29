Android library containing a precompiled version of `sqlite3`.

It does not contain JNI bindings or any other code, just the native libraries for `dlopen`.

To use, first add the repository:
```groovy
maven {
    url 'https://dl.bintray.com/sbinder/sqlite3-native-library/'
}
```

Next:
```grovy
dependencies {
    implementation 'eu.simonbinder:sqlite3-native-library:3.30.1'
}
```