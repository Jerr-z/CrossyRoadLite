# My Personal Project

## Can the Chicken Cross the Road?

### Acknowledgement
This project has based its program structure on Mazen Kotb's Snake Console, available
at https://github.com/mkotb/SnakeConsole. <br>
The save and read functions are based on the JsonSerializationDemo provided by my professor
Paul Carter.
###  User story
<br>
Imagine yourself sitting in your summer English class on a tuesday night. 
Your attention is slipping and your eyelids are dropping all thanks to
your boy William Shakespeare with his mythical way of speaking. Out of sheer
survival instincts to keep your brain functioning, you stumble upon this
application, an addictive and visually appealing game all started from
the joke "Can the chicken cross the road?".
<br>

- As a user, I want to be able to add a chicken to my map
- As a user, I want to be able to control the chicken in 4 directions
- As a user, I want to be able to increment my score as I progress in game
- As a user, I want to experience the thrill by avoiding multiple cars on the road
- As a user, I want to see a randomly generated map, so it's a new experience everytime.
- As a user, I want an endless gaming experience until I die in game.
- As a user, when I want to quit from the current game session, I want to have the option 
to save my current game progress to file
- As a user, when I start the game, I want to be given the option to load my previous game
progress from file

### What will the Application do?
<br>
A thrilling experience of simple yet challenging gameplay will be provided to
the users through dodging cars, avoiding obstacles and most importantly, crossing
the **ROAD**! Scores of users will be displayed in game for you. The game will be utilizing a very simple control
scheme of 4 direction inputs which allows you to control the avatar freely in a 2d
space.

### Who will use it?
<br>
This is a game that everyone can play! If you want to save yourself from boredom, 
whether it is waiting for the bus or having nothing to do in between classes or 
distracting yourself from English class (totally not me). Give this application a shot!

### Why is this project of interest to me?
<br>
The whole reason I went into coding is because of my passion for games. Being able to develop
a basic game will allow me to take on a different perspective that is
not simply a player and consumer, but rather as a creator and developer. I would get invaluable
insights into how object-oriented programming is integrated into somewhat complex system. Which
would serve as a stepping stone for future projects that could be way more robust.

# Instructions for Grader

- You can generate the first required action related to adding Xs to a Y by starting the application, the game will automatically generate game objects and place them onto the map.
- You can generate the second required action related to adding Xs to a Y by progressing in game. The game objects will move out of view and be removed from the game
as you move forward, and new game objects will be generated and added onto the map.
- You can generate the third required action related to adding Xs to a Y by pressing Q, this will pause the game and stop the adding process.
- You can generate the first required action related to removing Xs from a Y by simply progressing in game, as going forward as time flows will remove the bottom layer of game objects from the game.
- You can locate my visual component by looking at the screen, it's right in the middle of your screen!
- You can save the state of my application by pressing Q to pause the game and select corresponding actions
- You can reload the state of my application by starting the application. The program will check automatically if a save is present in the data folder. 
If the folder exists then the game will ask if you want to load the save state or not.

# Phase 4 Task 3
- One thing I have noticed in the project is the similarity of the different game objects I have
all of them have a position, but has other different behaviors. So with the refactoring, I would probaby
have all the game objects extend off an abstract gameObject class, and then specify their behaviors there.
- The observer pattern can probably be implemented as well as I can notify my game objects to update as the game
ticks. So GameState can be the subject and all my game objects can be the observer.