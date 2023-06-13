## COS 460/540 - Computer Networks
# Project 3: Octothorpe (#), The Game

# Environment and Language
This program was written on a Windows machine using the Eclipse IDE for Java. The program itself is written in Java.

## How to compile

Through an IDE:

Create a new Java Project in Eclipse and put the .java files from the repository in the "src" folder.

## How to run

Run as a java application through eclipse. The following command can also be adapted to your system to run it from the command line:

%userprofile%\.p2\pool\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_16.0.2.v20210721-1149\jre\bin\java.exe -classpath "%userprofile%\eclipse-workspace\project-2-http-server-Casey-USM\bin" Server

To start the server on a different port or with a different map, use the following syntax:

java [JVM arguments, must include -classpath] Server [port number] [map file]

Or adjust the run configurations in Eclipse.

# Connecting to the server
Windows version of telnet works fine, but the logon text is obscured. For best results use telnet in a bash shell.

## My experience with this project
This took a lot longer than I expected it to, but overall it was pretty straight forward. I feel like the majority of the work was done in setting up the supporting objects for the game. Then again, getting all the commands implemented and running took a fair amount of time too, so it's probably closer to a 50/50 split.
Thankfully I didn't have any major hang ups like I did with the last project. For the most part once I got the code done it just needed some minor fixes to work the way I wanted it to.
Somewhat unrelated, but I discovered along the way that Windows PowerShell has a version of ssh on it (maybe because I have git and putty installed) so that made it easier to get to a bash shell and do testing.
I designed a lot of the commands and responses with the idea that it would be tested via telnet, so I added a help command to show the things available for the player to be able to do. An incorrect command will send a response telling the player about the help command. Hopefully that doesn't break anything when we start working with the client.
Overall it was pretty fun to do!