# KoukBot
KoukBot was created as a joke for a private discord server. It started when due to the pandemic of COVID-19 we had online school. It was meant to announce the upcoming lesson for everyone on the server. After online school ended I decided to add more functionality into it and put in more useful features.

This features include:
* Program announcer
* Chat leveling system
* To-do task manager
* Music player
* Movie polls (not complete)
* Utility commands

## Program announcer
Program announcer is the part of the bot that announces the upcoming subject for everyone. It posts a message on a channel (announcement channel) 5 minutes before a class starts and 5 minutes before it ends to announce the upcoming break. It divides my class into **4 sub-classes** depending on the subjects the mainly focus on. Before each class the bot announces 4 subjects one for each sub-class. Before each break there is a global announcement regardless sub-class. It gets the next subject based on the `program.json` file which contains the weekly program for every sub-class.

### Structure of program.json
The file contains 4 `class` objects which represent the 4 sub-classes. The keys of `class` objects are the numbers from 0 to 3. Inside every `class` object there are 35 `subject` elements (7 hours a day x 5 days a week) representing a subject at a specific day of the week and time. The keys of `subject` objects have values from 0 to 34. Now a subject is represented by a list of 2 integers, the first being for getting the subject from the Enum and the second for getting the correct professor. A `subject` object usually contain just one list (= one subject).
  * Some subjects are taught to everyone regardless their subclass. To handle those subjects the bot uses the first `class` object and when there is a global subject instead of its `subject` object containing just 1 list it contains 2 for the 2 sub-classes created by alphabetical order.

### Examples:
The `class` object which contains 35 `subject` objects (not showing all of them):
```javascript
"0": { //First sub-class
  "0": [10, 0], //The first subject of the week. Monday, first hour of the day.
  "1": [9, 0], //The first integer is the index of the subject Enum. ('9' -> 'LATIN')
  "2": [8, 0], //The second integer is the index of the professor list for the subject ('8' -> 'HISTORY', '0' -> first teacher)  
  .
  .
  "33": [15, 0], //Day: Friday, Time of the day: 6th hour
  "34": [15, 0],
}
```
A class object which also contains `global` subjects:
```javascript
"0": { 
  "0": [10, 0], 
  "1": [9, 0], //Day: Monday, Time of the day: 2nd hour
  "2": { //The lists represent a subject for each sub-class divided alphabetically  
    "0": [0, 0], //First sub-class: ('0' -> 'GREEK', '0' -> first teacher)
    "1": [12, 0] //Second sub-class: ('12' -> 'PHYSICAL_EDUCATION', '0' -> first teacher)
  }, 
  .
  .
  "34": [15, 0],
}
```
