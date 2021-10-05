# KoukBot
KoukBot is a discord bot that was created as a joke for a private discord server. It started when due to the pandemic of COVID-19 we had online school. It was meant to announce the upcoming lesson for everyone on the server. After online school ended I decided to add more functionality into it and put in more useful features.

This features include:
* [Program announcer](https://github.com/ACrispyCookie/KoukBot#program-announcer)
* [Program creator](https://github.com/ACrispyCookie/KoukBot#program-creator)
* [Music player](https://github.com/ACrispyCookie/KoukBot#music-player)
* [Chat leveling system](https://github.com/ACrispyCookie/KoukBot#chat-leveling)
* [To-do task manager](https://github.com/ACrispyCookie/KoukBot#to-do-task-manager)
* [Utility commands](https://github.com/ACrispyCookie/KoukBot#utility-commands)
* Movie polls (not complete)


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
An announcement for 2 sub-classes:

![image](https://user-images.githubusercontent.com/30019341/136095376-b33f3698-6a5e-4b03-8d1a-59e020c56f2f.png)


## Program creator
Program creator is a system created to make the process of saving a program on their phone very easy. It is an very user friendly process which uses message reactions to gather input from the users. There are 2 types of programs, morning and afternoon. The morning programs are used for school while the afternoon for after-school lessons. To start the creation of a program you first have to react on a message to start the process. After that you will be directed to your own channel in which you will have to input your program by clicking the corresponding reaction for each subject.

### Examples:
Starting message which begins the process of creation:

![image](https://user-images.githubusercontent.com/30019341/136101362-0ae82bab-8c9e-470e-b6ca-bcf5ddbce0d1.png)

The message you react to, to give input to the bot:

![image](https://user-images.githubusercontent.com/30019341/136101613-09d03187-428a-4631-a4b5-5e04768a9298.png)


## Music player
Music player is used to play music inside of voice channels using KoukBot. Users can add videos from YouTube to the queue, pause the player, stop the player or move around the queue of videos. As a joke a voiceline from a professor has been added every time the bot joins a voice channel.

### Commands:
* **!play <search term OR URL>** - Adds a video to the queue and starts the player is the queue was empty. (Aliases: q)
* **!pause** - Pauses the player on the current video.
* **!stop** - Stops the player and resets the queue to its first index.
* **!previous** - Moves the queue one index backwards. (Aliases: prev)
* **!next** - Moves the queue one index forwards. (Aliases: skip)

### Examples:
Adding a song to the queue and starting the player:

![image](https://user-images.githubusercontent.com/30019341/136099935-c4e24a1e-061e-43c2-a65f-73cc121b2a26.png)


## Chat leveling
Chat leveling is a ranking system which ranks players based on chat activity. It adds a random amount of EXP (between 15 and 25) per message sent. There is a cooldown of one minute to every user so the maximum amount of exp that a user can get (assuming they have maximum luck) is 25. The maximum amount of EXP needed to level up increases as your level increases.

### Commands:
* **!rank [user]** - Renders and posts a user card which contains information about the user's level, rank and EXP.
* **!setcolor [hex color code]** - Sets the main color for your user card.

### Examples:
A card of an offline user using the color `#7289DA` as the main color:

![image](https://user-images.githubusercontent.com/30019341/136097121-e8d77627-9a20-481d-a5ce-91c6b9780529.png)


## To-do task manager
To-do task manager is a system that saves your to-dos on a discord channel and give you the option to complete them. The main command of this system is `!todo <task>` which is used to add a new task to your task list. If you haven't created a to-do list before the bot will create a channel with your username in which it will post the task. When you finish the task you can react to the corresponding message to complete it.

### Examples:
A to-do task on a user's channel:

![image](https://user-images.githubusercontent.com/30019341/136098043-b6b07046-1ffa-4d8f-9818-3654ae7ccf36.png)


## Utility commands
KoukBot also has some commands which are very basic and are used very frequently by a lot of users. These commands are:
* **!clear <amount>** - Clears the amount of messages specified in the channel the command was sent to. (2 <= amount <= 99)
* **!notify <user>** - Notifies a defeaned user by moving them in a random voice channel and moving them back.
