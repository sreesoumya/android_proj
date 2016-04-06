
App Objectives:

Use some basic UI widgets
Use Intents to call other Activities
Handling the Keyboard

This app consisitd of different UI elements. The combination of elements does giev the  experience with more UI elements.

This app has number of different activities. 
The first (main) activity contains a spinner, a button and an EditText field. The spinner contains a list of activities one can select.
When the user selects the option and then clicks on the button the selected activity becomes
the active activity. The contents of the text field are sent to the next activity. 
Each section below describes an activity that the user can go to.

Keyboard Activity
This activity's view has three EditText fields. One at the top of the screen, one in the middle of
the screen and one at the bottom of the screen. The top EditText field should contain the text
sent from the first (main) activity. There is also two buttons. One labeled "Hide" and the other
labeled "Back". The "Back" button when tapped goes back to the first (main) activity. 
When the user taps on one of the EditText fields the soft keyboard appears.
When the user taps on the "Hide" button the soft keyboard disappears. One problem with the
soft keyboard is that it can cover up part of the screen. In this case it will hide the field on the
bottom of the screen. You activity should have the view pan up when the soft keyboard is
shown and the focus is in the bottom EditText field.

Web Activity
This view contains a EditText field, a button and a web view. When the user enters a valid url in
the text field and then taps on the button the web view displays the indicated web page.

List Activity
This activity has a list and a back button. The list is either a List Activity or a list view. The list
contains all the versions of Android. The user can select one item in the list. When the user
taps on the back button the app goes back to the first (main) activity. The first (main) activity
will display the selected Android version in its text field.
1.5 Cupcake
1.6 Donut
2.0 Eclair
2.1 Eclair
2.2 Froyo
2.3 Gingerbread
3 Honeycomb
4.0 Ice Cream Sandwich
4.1 Jelly Bean
4.2 Jelly Bean
4.3 Jelly Bean
4.4 KitKat

Addintional Features :
1. Enabled the application icon in the action bar to take the user back to the first (main) activity.
Makes sure that the app goes back to the first activity and not create a new instance of the first
activity. If the app is displaying a one of the other activities, that activity is been destroyed
when you go back to the first activity.
2. Added items to the Action Bar to allow the user to select which activity to go to.
3. Enabled the split action bar.
4.Uses a List fragment to display The list of Android versions.
