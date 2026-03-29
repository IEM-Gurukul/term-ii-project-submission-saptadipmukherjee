[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/pG3gvzt-)
# PCCCS495 – Term II Project

## Project Title

Hotel Booking System

## Problem Statement (max 150 words)
The Hotel Booking System is designed to simplify and automate the process of room reservation and management in hotels. Traditional booking methods are often manual, time-consuming, and prone to errors such as double booking or incorrect customer records. This system aims to provide a structured and efficient solution where users can search for available rooms, make bookings, and manage reservations seamlessly. It also enables hotel administrators to maintain room inventory, track customer details, and monitor booking status in real-time. The system ensures data consistency, reduces manual effort, and enhances user experience through a clear and interactive interface. By implementing object-oriented principles, the system remains scalable, maintainable, and adaptable for future enhancements.
---

## Target User
Hotel Customers looking to book rooms
Hotel Receptionists managing bookings
Hotel Administrators handling room inventory and records
---

## Core Features

Room availability search based on date and room type
Booking and cancellation of rooms
Customer record management and booking history

---

## OOP Concepts Used
Abstraction:
Abstract classes or interfaces such as BookingService define high-level operations like createBooking() and cancelBooking() without exposing internal logic.
Inheritance:
Different user types such as Customer and Admin inherit from a common User class, reusing common attributes like name, contact, and ID.
Polymorphism:
Method overriding is used where different user roles implement booking actions differently, such as priority booking for admins.
Exception Handling:
Handles scenarios like invalid booking dates, unavailable rooms, or incorrect user input using try-catch blocks.
Collections / Threads:
Collections such as ArrayList or HashMap are used to store room and booking data. Threads may be used to handle concurrent booking requests safely.

---

## Proposed Architecture Description
The system follows a layered architecture consisting of Presentation Layer, Service Layer, and Data Layer.

The Presentation Layer handles user interaction through a console-based interface. It collects input from users and displays booking details.

The Service Layer contains the core business logic such as checking room availability, processing bookings, and managing cancellations. It acts as an intermediary between the user interface and data storage.

The Data Layer manages storage and retrieval of data using in-memory collections or file-based persistence. It maintains records of rooms, customers, and bookings.

This separation of concerns ensures better maintainability, scalability, and testability of the system.
---

## How to Run
Compile all Java files using:
javac Main.java
Run the main class:
java Main
Follow the console prompts to perform booking operations
---

## Git Discipline Notes
Minimum 10 meaningful commits required.
