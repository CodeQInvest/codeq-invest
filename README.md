# CodeQ Invest

CodeQ Invest is the new way to deal with inner software quality in your projects.

## Overview

CodeQ Invest implements a new quality model for the management of internal software quality. For this, a new metaphor for dealing with internal software quality was invented: the quality investment. Quality investments are the reversal of technical debt. Instead of calculating the amount of remediation costs for paying off the technical debt, a return of investment is calculated which tells you how much time you save when investing in the internal software quality.

Please visit [codeq-invest.org](http://codeq-invest.de) for more information.

## Build Management

[Maven](http://maven.apache.org) is used to build and test the software. It is mandatory that you have [Firefox](http://www.mozilla.org/firefox) installed on your machine for acceptance and integration tests in the [web-ui module](https://github.com/CodeQInvest/codeq-invest/tree/master/web-ui). Firefox has to be accessible from the command line.

With the following command you build and test the whole software and deploy all artefacts in your local Maven repository:

```
mvn clean install
```

This build command is mandatory to start CodeQ Invest (see next command) or for setting up your development environment.

Deploy CodeQ Invest in a locally running Tomcat instance for debugging purpose (has to be executed in the [web-ui module](https://github.com/CodeQInvest/codeq-invest/tree/master/web-ui) directory):

```
mvn clean package org.codehaus.cargo:cargo-maven2-plugin:run -Pdebug-web-app
```

Afterwards the application is accessible under localhost:8080.

You can skip the integration tests by providing the environment variable ```skipITs```:

```
mvn clean install -DskipITs
```

## Contribution

If you want to start to create a new feature or have any other questions regarding CodeQ Invest, [file an issue](https://github.com/CodeQInvest/codeq-invest/issues/new).
I'll try to answer as soon as I find the time.

### Pull requests welcome

Feel free to contribute to CodeQ Invest.

Either you found a bug or have created a new and awesome feature, just create a pull request.

### Formatting

For contributors using Eclipse there's a [formatter](https://raw.github.com/CodeQInvest/codeq-invest/master/etc/eclipse/formatter.xml) available.

For IntelliJ IDEA please use this [formatter](https://raw.github.com/CodeQInvest/codeq-invest/master/etc/idea/formatter.xml).

In order to reduce merging pains on my end, please use this formatter or format your commit in a way similar to it's example.

## License

CodeQ Invest is licensed under [GPLv3](http://www.gnu.org/licenses/gpl-3.0.html).