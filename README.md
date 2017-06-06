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
    
    
# Test #
### Mockito vs Robolectric

**`Mockito`** http://site.mockito.org/ https://github.com/mockito/mockito

is used for making mocks of your classes.

When you are testing a particular class you mock all of its dependencies with Mockito.

Where possible most of your tests should use mockito. To make this possible most people split their
 code up into MVP, etc where the business logic is separated from the View logic. This way your
  business logic (Presenter) has no knowledge (or dependencies) on the Android library and has no 
  need to have mocks of them.

**`Robolectric`**

is a library which contains many mocks of Android classes.

The Robolectric test runner injects these 'shadow objects' in place of the actual Android classes 
when the tests are run. This is what allows the tests to run on the JVM without booting up an 
instance of Android.

When using MVP your View layer tends to be implemented by the Activity/Fragment and this is where
 you can use Robolectric to mock these.

Notes

Use Robolectric only where necessary. It basically re-implements parts of the Android framework but
 not always in exactly the same way.

You may also need another library such as PowerMock. This allows the mocking of static classes such 
as Math or can be used to mock static Android classes such as TextUtils.

#### @see
- http://www.vogella.com/tutorials/JUnit/article.html#junit_performancetests
- http://www.vogella.com/tutorials/Mockito/article.html

#### NB
+ Small: this test doesn't interact with any file system or network.
+ Medium: Accesses file systems on box which is running tests.
+ Large: Accesses external file systems, networks, etc.
