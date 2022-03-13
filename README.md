Android library containing a precompiled version of `sqlite3`.

![latest version on maven central](https://maven-badges.herokuapp.com/maven-central/eu.simonbinder/sqlite3-native-library/badge.svg)

It does not contain JNI bindings or any other code, just the native libraries for `dlopen`.

To depend on this, just add the dependency:

```groovy
dependencies {
    implementation 'eu.simonbinder:sqlite3-native-library:3.38.1'
}
```
