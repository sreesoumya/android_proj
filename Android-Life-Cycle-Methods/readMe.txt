Some Observations:
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
 
2)Save the values of your ﬁeld that count the times each method is called using onRestoreInstanceState and onSaveInstanceStat How does this affect the displayed values when you rotate the device/emulator? 

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
