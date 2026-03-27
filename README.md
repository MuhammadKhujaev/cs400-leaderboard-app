# Leaderboard Management System (Java)

A command-line leaderboard application built using a self-balancing Red-Black Tree for efficient data storage, filtering, and retrieval.

## Overview

This project is a Java-based system that allows users to manage and query leaderboard data through a text-based interface.  
Users can load data from a file, insert new records, apply filters, and display sorted results.

The system is designed using a modular architecture with separate frontend and backend components.  
A Red-Black Tree is used as the core data structure to ensure efficient operations with O(log n) time complexity.


## Features

- Load leaderboard data from a CSV file (`records.csv`)
- Insert new player records
- Filter records by continent
- Filter records using value ranges (min/max)
- Display top records with limits
- Show fastest completion times
- Sorted traversal using an in-order iterator
- Efficient data operations using Red-Black Tree

## How to Run

### Option 1: Using Makefile
```bash
make runApplication
```


### Option 2: Manual compilation
- javac *.java
- java App

### Example Commands
Once the application is running, you can use commands like:
- submit NAME CONTINENT SCORE DAMAGE_TAKEN COLLECTABLES COMPLETION_TIME
- submit multiple FILEPATH
- collectables MAX
- collectables MIN to MAX
- location CONTINENT
- show MAX_COUNT
- show fastest times
- help
- quit

Technologies and Concepts used
- Java (Object-Oriented Programming)
- Red-Black Tree (self-balancing binary search tree)
- Tree rotations (left/right)
- Iterator pattern (in-order traversal)
- Interfaces and abstraction
- Modular design (frontend/backend separation)

### My Role
Backend Developer
- Implemented backend logic and data processing (Backend.java)
- Designed functionality for loading, filtering, and querying data
- Integrated backend with the Red-Black Tree structure
- Implemented core data structures including:
	- Binary Search Tree
	- Red-Black Tree with balancing
	- Iterator for sorted traversal


### Notes
	•	This project was developed as part of a team, with separate frontend and backend responsibilities.
	•	The frontend handles user input and display, while the backend processes logic and interacts with the data structure.
