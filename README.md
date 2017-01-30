# cancellable-task
A cancellable task to be run asynchronously.

[![Build Status](https://travis-ci.org/NthPortal/cancellable-task.svg?branch=master)](https://travis-ci.org/NthPortal/cancellable-task)
[![Coverage Status](https://coveralls.io/repos/github/NthPortal/cancellable-task/badge.svg?branch=master)](https://coveralls.io/github/NthPortal/cancellable-task?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/com.nthportal/cancellable-task_2.12.svg)](https://mvnrepository.com/artifact/com.nthportal/cancellable-task_2.12)

## Add as a Dependency

### SBT (Scala 2.11 and 2.12)
```
"com.nthportal" %% "cancellable-task" % "1.0.0"
```

### Maven

**Scala 2.12**

```
<dependency>
  <groupId>com.nthportal</groupId>
  <artifactId>cancellable-task_2.12</artifactId>
  <version>1.0.0</version>
</dependency>
```

**Scala 2.11**

```
<dependency>
  <groupId>com.nthportal</groupId>
  <artifactId>cancellable-task_2.11</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Authorship

The core of this library was originally written by Stack Overflow user [Pablo Francisco Pérez Hidalgo](https://stackoverflow.com/users/1893995/pablo-francisco-p%C3%A9rez-hidalgo) ([Twitter](https://twitter.com/pfcoperez), [GitHub](https://github.com/pfcoperez)). That implementation can be found on GitHub [here](https://github.com/Stratio/common-utils/blob/b9195e3a2b206bb65bf61b412371cf07858d5450/src/main/scala/com/stratio/common/utils/concurrent/Cancellable.scala), and in [this Stack Overflow answer](https://stackoverflow.com/a/35724035/5101123).

Stack Overflow user [nightingale](https://stackoverflow.com/users/133062/nightingale) improved on the implementation by utilizing Java [`FutureTask`'s `done()` method](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html#done--); the improved implementation can be found in [this Stack Overflow answer](https://stackoverflow.com/a/39986418/5101123).

This author ([NthPortal](https://github.com/NthPortal)) tweaked the implementation by unwrapping [`ExecutionException`s](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutionException.html) thrown by the [`FutureTask`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html), and forwarding the [`cancel(boolean)`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html#cancel-boolean-) and [`isCancelled()`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/FutureTask.html#isCancelled--) methods from `FutureTask`.
