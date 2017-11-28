# Node-Status-Reporting

## Description

Node Status Reporting is a program that reads notifications send from nodes and prints the status of each node.
The program prints the name of the node, followed by the status of the node, the timestamp of the last notification and the message from the notification.
The notifications are read from `Messages.txt`

## Assumptions

* The nodes, along with required information, are to be printed in no particular order.

## How To Run
  1. Navigate to the directory on cmd/terminal
  2. Compile using `javac NodeReport.java`
  3. Run program with `java NodeReport Messages.txt`
