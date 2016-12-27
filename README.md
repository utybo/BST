# BST [![Codacy Badge](https://api.codacy.com/project/badge/Grade/8416a09095454d3983c78e64eb796429)](https://www.codacy.com/app/utybo/BST?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=utybo/BST&amp;utm_campaign=Badge_Grade) ![Version](https://img.shields.io/badge/version-N%2FA-lightgrey.svg) [![Build Status](https://travis-ci.org/utybo/BST.svg?branch=master)](https://travis-ci.org/utybo/BST) [![Github All Releases](https://img.shields.io/github/downloads/utybo/BST/total.svg)](https://github.com/utybo/BST/releases/latest)

Branching Story Tree, or BST for short, is a Turing complete language that allows you to create branching stories easily, with scripting capabilities.

This repository contains :

* The **official BST tutorial and documentation** (specification TBD)
* The **OpenBST Player**, a simple program for playing .bst files easily
* The **BST-Java library**, a simple library that reads and understand .bst files.

## Components
### BST Tutorial
*/bst*

This tutorial will get you started for everything BST : basic layout, scripting...

### OpenBST
*/openbst*

A simplistic player for any BST file.

### BST-Java
*/bst-java*

Java implementation of the BST language. Very easy to use. Do note that it tends to be very laxist regarding syntax, and not descriptive for crashes. It is up to the application built upon it to provide more details.


## Building OpenBST

To build OpenBST :
- Download and install the Java JDK. Both the Oracle JDK and the OpenJDK will work. Once this is done :
- Either clone the repository if Git is installed on your computer, or download and unzip this : https://github.com/utybo/BST/archive/master.zip
- Open a command prompt inside the BST folder (you should see folders like "openbst", "bst", "bst-java"...)
- If you **do not have** Gradle installed on your computer :
  - Type `gradlew build` on Windows or `./gradlew build` on Linux/Mac OS and press Enter
- If you **have** Gradle installed on your computer :
  - Type `gradle build` and press Enter
- Wait for a bit until the process finishes - this can take quite a long time. If you see a BUILD FAILED information, the devs are probably already aware of this and are working on it. Otherwise, you should have a BUILD SUCCESFUL line.
- Once this is done, go to `openbst/build/distributions`, then :
  - If you're on Mac OS or Linux, untar the .tar flie
  - If you're on Windows, unzip the .zip file
- The folder `openbst-` followed by a version number contains all the necessary files for running OpenBST. You can copy it somewhere else safe.
- Inside the folder, navigate to the `bin` folder, and then :
  - If you're on Mac OS or Linux, run `openbst`
  - If you're on Widnows, run `openbst.bat`
- Done!

Short version (assuming you have git, gradle, tar and a JDK installed) :
```shell
git clone https://github.com/utybo/BST
cd BST
gradle build
```
