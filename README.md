# Project 3: Regular Expressions

* Author: Trevor Smith, Brandon Mattaini, Berto Cisneros
* Class: CS 361, Section 1 (Trevor & Berto), Section 2 (Brandon)
* Semester: Spring 2021


## Overview

This program is an implementation of a regular expression to NFA converter. This program creates an NFA from the given regular expression and evaluates whether the given strings are accepted or not.

## Compiling and Using

In order to compile this program, make sure you are in the parent directory containing this README file. Once you are at this directory, run the command shown below to compile the whole package:

javac -cp ".:./CS361FA.jar" re/REDriver.java

Then to run the program, run the following command, where \<input file> is the file (and filepath) that the program will run.

java -cp ".:./CS361FA.jar" re.REDriver \<input file>


## Discussion

This project overall was not too bad. Most of the project went very smoothly without any problems, but we had few issues that took more time to solve which are outlined below:

We would say that initially working with the jar files were a bit conusing as it looked like we always had errors.  

We struggled to figure out how to initially implement these methods we were given from the link. While it made some sense conceptually, it wasn't totally obvious how it would all work together when
we were writing the methods. To figure out how these methods would work, we basically referred to previous homework assignments and used the provided link to make it clearer to us. Writing this out on paper helped as we could then kind of convert those steps into code. This project was also a bit tougher with its concepts, being that the structure was largely recursive, and that can be a lot harder to picture sometimes. Overall though, we thought this project wasn't as bad as we were expecting, but it still presented challenges.

## Testing

For testing this project, we compiled and ran the REDriver class with the 3 given test files to be able to compare our results. We didn't add anymore tests this time, as we felt fairly confident that the given tests covered most cases. If we were to change one thing in our development process, it would have been to do more testing on smaller methods to ensure they were working correctly.