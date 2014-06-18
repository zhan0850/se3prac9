se3prac9
========
The test subject is a program developed by some Flinders students as the assignment for the previous version of se3 in 
semester 1, 2013.

The program is a movie ticket booking application that allows a user to choose a movie session and book tickets. For more 
information about the functionalities, please read SE3ProjectA/docs/UserGuide

This is how it works:<br>
  -All data are loaded from the xml files in SE3ProjectA/data/<br>
  -First the user needs to choose a session.<br>
  -Movie, time, and theatre are attributes of session and can all be chosen by the user to decide a session.<br>
  -Then the user can do a transaction.<br>
  -Ticket type, seat type, and number of tickets are attributes of transaction and together decide the cost.<br>
  -After that, the user can select seats and book tickets.<br>
  -When a seat is selected (by clicking on the seat icon or clicking random select botton), the state of the seat changes
   from "Empty" to "Held". Once book botton is clicked, the state of the seat becomes "Occupied" and is written to the file 
   data/TheatreSessions.xml

Test:<br>
  -The test file is in SE3ProjectA/test/se3projecta/Model/<br>
  -There is only one test file, but there are many tests, each of which has many test cases<br>
  -The test is focused on the functionality of random seat allocation. As a result, a bug is spotted.<br>
  -For more information, please read bugreport.txt<br>
