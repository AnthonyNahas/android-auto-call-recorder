# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

## Pull SQLite DB from Android Device 

1.  Connect your device and launch the application in debug mode.
2.  You may want to use adb -d shell "run-as com.yourpackge.name ls /data/data/com.yourpackge.name/databases/" to see what the database filename is.
    Notice: com.yourpackge.name is your application package name. You can get it from the manifest file.
    
3.  Copy the database file from your application folder to your SD card.
    
    adb -d shell "run-as anthonynahas.com.autocallrecorder cat /data/data/anthonynahas.com.autocallrecorder/databases/filename.sqlite > /sdcard/filename.sqlite"
    
    Notice: filename.sqlite is your database name you used when you created the database
    
4.  Pull the database files to your machine:
    
    adb pull /sdcard/filename.sqlite
    This will copy the database from the SD card to the place where your ADB exist.
    
    Install Firefox SQLite Manager: https://addons.mozilla.org/en-US/firefox/addon/sqlite-manager/
    Open Firefox SQLite Manager (Tools->SQLite Manager) and open your database file from step 3 above.
    Enjoy!