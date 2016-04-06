1)	When you run your app and rotate the device/emulator are the method counts consistent with the number of times
    you see the methods called in the log?

Yes, the method counts are consistent with the number of times it is called in the log.
When the Orientation is changed, the following entries were found in the log and it is 
consistent with the counter values of respective methods on screen.
01-29 20:45:03.571: D/LifeCycle(16735): onPause()
01-29 20:45:03.631: D/LifeCycle(16735): onSaveInstanceState()
01-29 20:45:03.691: D/LifeCycle(16735): onCreate()
01-29 20:45:03.701: D/LifeCycle (16735): onStart ()
01-29 20:45:03.711: D/LifeCycle(16735): onRestoreInstanceState()
01-29 20:45:03.711: D/LifeCycle(16735): onResume()
  Few other scenarios tested are mentioned at the end of this document.

2)	 Save the values of your ﬁeld that count the times each method is called using onRestoreIn- stanceState and onSaveInstanceState.
     See below for an example of saving and restoring a ﬁeld. How does this affect the displayed values when you rotate the device/emulator? 

When the methods onRestoreInstanceState() and onSaveInstanceState() are not called, the values reset when onCreate()
 is called after device orientation change.
For example, if the value was 3, then it changes to 0 after the orientation is changed.
When the methods onRestoreInstanceState and onSaveInstanceState are called, the values continue to update from where 
it was before changing the orientation.
For example ,if the value was 3 ,then it changes to 4 if it was incremented ,or else it remains 3.



3)	Change one or all of the EditViews to a TextView. After all the user does not need to edit the values of the count. 
    How does this affect the displayed values when you rotate the device/emulator?

The text displayed on the EditView is preserved on device orientation change ,whereas the text displayed 
in the TestView is not preserved  automatically. To preserve text in the Text View one has to explicitly 
save and restore the value and set the value to the TextView after device orientation change.

 

Below are the few other scenarios to verify Question no 1.

Test Case1:
// App is launched 
01-29 20:36:24.074: W/ActivityThread(16208): Application edu.sdsu.projects.lifecyclemethods can be debugged on port 8100...
01-29 20:36:24.154: D/LifeCycle(16208): onCreate()
01-29 20:36:24.154: D/LifeCycle(16208): savedInstanceState is NULL
01-29 20:36:24.154: D/LifeCycle(16208): onStart()
01-29 20:36:24.154: D/LifeCycle(16208): onResume()

//Device orientation changed
01-29 20:36:39.969: D/LifeCycle(16208): onPause()
01-29 20:36:39.979: D/LifeCycle(16208): onSaveInstanceState()
01-29 20:36:40.059: D/LifeCycle(16208): onCreate()
01-29 20:36:40.059: D/LifeCycle(16208): savedInstanceState is not null
01-29 20:36:40.059: D/LifeCycle(16208): onStart()
01-29 20:36:40.069: D/LifeCycle(16208): onRestoreInstanceState()
01-29 20:36:40.069: D/LifeCycle(16208): onResume()

Screen Result:
onCreate() =2 
onRestart()=0
onStart()=2
onResume()=2
onPause()=1

Test Case2:
//App launched 
01-29 20:41:49.431: W/ActivityThread(16585): Application edu.sdsu.projects.lifecyclemethods can be debugged on port 8100...
01-29 20:41:49.481: D/LifeCycle(16585): onCreate()
01-29 20:41:49.481: D/LifeCycle(16585): savedInstanceState is NULL
01-29 20:41:49.491: D/LifeCycle(16585): onStart()
01-29 20:41:49.491: D/LifeCycle(16585): onResume()

//Home button pressed
01-29 20:41:54.426: D/LifeCycle(16585): onPause()
01-29 20:41:54.947: D/LifeCycle(16585): onSaveInstanceState()

//Activity brought back from Recent app list
01-29 20:42:02.504: D/LifeCycle(16585): onRestart()
01-29 20:42:02.524: D/LifeCycle(16585): onStart()
01-29 20:42:02.534: D/LifeCycle(16585): onResume()

Screen Result:
onCreate =2 
onRestart=1
onStart=2
onResume=2
onPause=1

Test Case 3:
//App launched 
01-29 20:44:45.893: W/ActivityThread(16735): Application edu.sdsu.projects.lifecyclemethods can be debugged on port 8100...
01-29 20:44:45.964: D/LifeCycle(16735): onCreate()
01-29 20:44:45.964: D/LifeCycle(16735): savedInstanceState is NULL
01-29 20:44:45.964: D/LifeCycle(16735): onStart()
01-29 20:44:45.964: D/LifeCycle(16735): onResume()

//Reset Button clicked
01-29 20:44:59.917: D/LifeCycle(16735): ResetButton.OnClick()

//Device Orientation changed
01-29 20:45:03.571: D/LifeCycle(16735): onPause()
01-29 20:45:03.631: D/LifeCycle(16735): onSaveInstanceState()
01-29 20:45:03.691: D/LifeCycle(16735): onCreate()
01-29 20:45:03.691: D/LifeCycle(16735): savedInstanceState is not null
01-29 20:45:03.701: D/LifeCycle(16735): onStart()
01-29 20:45:03.711: D/LifeCycle(16735): onRestoreInstanceState()
01-29 20:45:03.711: D/LifeCycle(16735): onResume()

Screen Result:
onCreate =1
onRestart=0
onStart=1
onResume=1
onPause=1

