# Ubuntu #

  * Extract "seventhsense-`*`.zip" (where "`*`" is the current build no.) to your hard disc, if you're not sure where to put it extract the files to _/home/**yourName**/Documents/7thSense_. You may do so by double clicking the zip-file.
  * Move to the folder you just extracted the files to, right click seventhsense.jar and choose **"Properties"**.
  * Open the **"Permissions"** tab and check **"Execute"**.
  * Open the **"Open with"** tab and choose **"OpenJDK Java 6 Runtime"**. If there is no such entry you need to install Java first - see the appropriate entry in this wiki.
Now you can run the program by double clicking **"seventhsense.jar"**.

If there are issues with sound playback you need to install libopenal. See step 1 and 2 at **Other Linux**.


# Other Linux #
  * Open up a terminal.
  * Execute _sudo apt-get install libopenal1_
  * Execute _cd **`*`directory`*`**_ (eg. _cd /home/**yourName**/Documents/7thSense/_).
  * Execute _unzip -d seventhsense-`*`.zip_ (where "`*`" is the current build no.).
  * Execute _chmod +x seventhsense.jar_.
  * Right click **"seventhsense.jar"** and choose **"Open with OpenJDK Java 6 Runtime"**.

# Installing Java #

## Ubuntu ##
  * Open up **Ubuntu Software Center** and search for **"Java"**.
  * Choose **"OpenJDK Java 6 Runtime"** and click on **"Install"**.

## Other Linux ##
  * See [Java Homepage](http://www.java.com/en/download/help/linux_install.xml).