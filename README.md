# Persistent File-Based Inventory Management System

A console-based inventory management system developed using Core Java. The application supports CRUD operations, file storage, exception handling, and input validation using Java Collections and File Handling.

## Features

- Add, View, Update, Search, and Delete Products
- File-based data storage using `BufferedReader` and `BufferedWriter`
- Fast product lookup using `HashMap`
- Exception handling using `try-catch`
- Input validation for invalid or empty entries
- Automatic sample data initialization

## Technologies Used

- Java (JDK 8+)
- Java Collections Framework (`HashMap`, `Map`)
- File Handling (`FileReader`, `FileWriter`)
- Object-Oriented Programming (OOP)

## How to Run

```bash
javac InventorySystem.java
java Main
```

## Sample Execution

```text
PERSISTENT FILE-BASED INVENTORY ENGINE

1. View All Records
2. Insert New Record
3. Search Record by ID
4. Update Record Stock
5. Delete a Record
6. Exit System

Selection (1-6): 1

CURRENT INVENTORY RECORDS:
ID: P001 | Name: Enterprise Server | Stock: 12 | Price: INR 145000.0
ID: P002 | Name: Firewall Router | Stock: 25 | Price: INR 34000.0
ID: P003 | Name: Mechanical Keyboard | Stock: 110 | Price: INR 4500.0
ID: P004 | Name: FHD IPS Monitor | Stock: 45 | Price: INR 18500.0

Selection (1-6): 2

Enter Product ID: P005
Enter Product Name: Wireless Mouse
Enter Initial Stock: 60
Enter Price (INR): 1200

Product added successfully.

Selection (1-6): 6

Exiting system. Data saved successfully.
```
