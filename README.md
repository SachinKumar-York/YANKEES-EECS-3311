# YANKEES-EECS-3311
Team Members - 
- Veerman Kalra
- Sachin Kumar
- Divyansh Babbar
- Yogesh Sharma

NutriSci- "SwEATch to better" 

A desktop app built with Java Swing and MySQL to help users track meals and view nutritional breakdowns.

Setup Instructions:

1. Install Java JDK 11+, MySQL Server, and Maven.
2. Clone the repository.
3. Import cnf.sql into a MySQL database named 'cnf'.
4. Use these queries before you open sql file in mysql.
   - CREATE DATABASE cnf;
   - USE cnf;
5. Update DBConnector.java with your MySQL credentials. (password)
6. Run the app using StartPage.java from your IDE.
   
Application Features

Use Case 1: User Profile Creation & Login

Create, edit, and manage personal profiles.
Users are automatically directed to the dashboard upon login.

Use Case 2: Meal Logging

Log meals by selecting food ingredients and entering quantities.
Nutritional data fetched dynamically from CNF database.

Use Case 3: Add Nutrition Goals & Suggest Swaps

Users can define up to two goals (e.g., reduce fat or increase fiber).
The system returns a list of alternative ingredients that better match user goals.

Use Case 4: Swap Comparison View

Compare original and swapped meals side-by-side.
View differences in both ingredients and nutrient content.


Use Case 5: Apply Swaps Across Meals

Apply a food swap globally or within a selected date range.
Allows batch operations without tightly coupling logic.

Use Case 6: Daily Nutrient Visualization

Users can see their daily nutrient intake as pie and bar charts.
Compares user’s actual intake with recommended dietary intake (RDI).

Use Case 7: CFG-Based Meal Comparison

Visualize food group distribution based on Canada’s Food Guide (CFG).
Get feedback on meal alignment with CFG 2007 and 2019 standards.
Uses pie charts for intuitive, visual breakdowns.

Use Case 8: Nutrient Impact of Swaps

Compare nutritional impact of original vs. swapped meals over time.
Includes per meal, cumulative, and CFG comparisons.
Charts rendered with JFreeChart for rich, interactive insights.

