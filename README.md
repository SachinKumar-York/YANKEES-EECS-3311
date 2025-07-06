<<<<<<< HEAD
# YANKEES-EECS-3311
Contributors:
Veerman
Divyansh
Yogesh
Sachin

🥗 Nutrition Tracker Java Application
A desktop-based nutrition tracking application built with Java Swing and backed by a MySQL (Canadian Nutrient File) database. Users can create profiles, log meals, view nutritional values, and visualize macronutrient breakdowns (Protein, Carbs, Fat) using interactive pie charts.

Getting Started
1. Clone the Repository
   
2. Import the MySQL Database
This app depends on a MySQL schema based on the Canadian Nutrient File (CNF).

Option A: Using MySQL Workbench
Open MySQL Workbench.
Go to Server > Data Import.
Choose Import from Self-Contained File.
Select database/cnf.sql in the java project folder.
Select or create a schema (e.g., cnf).
Click Start Import.

OR OptionB: you can also import the DB by writing sql queries.

3. Configure Database Connection
Edit DAO/DBConnector.java to match your local MySQL setup:
change the below lines:
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";

Now RUN THE APPLICATION:

Step-by-Step UI Journey
Start the application by running SplashScreen.java class in UI folder.

Splash Screen (SplashScreen.java):
Displays all user profiles.
+ Add User button opens the Create Profile dialog.

Create Profile Dialog (CreateProfileDialog.java):
Allows creating a new user.
After creation, the user appears in the splash screen list.

Edit Profile (EditProfileDialog.java):
Option to update name or other details of existing user.

User Dashboard (Dashboard.java):
Opens when a user is clicked.
Session is started (Session.java tracks the logged-in user).

Provides two options:
Log Meal -> Opens MealLogFrame
View Meals -> Opens ViewMealsFrame

Log Meal (MealLogFrame.java):
Select meal name, type, date.
Add multiple food ingredients with quantity.
Saves data to Meal, UserMeal, and MealIngredient tables.

View Meals (ViewMealsFrame.java):
Displays list of meals logged by the user.
Shows meal name, type, date, and total calories.

Meal Nutrient Breakdown (MealPieChartFrame.java):
Double-clicking a meal shows a Pie Chart of macronutrients (Protein, Fat, Carbs).

Uses JFreeChart for rendering the chart.

=======
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
6. Run the app using SplashScreen.java from your IDE.

App Features:

- Create and manage user profiles.
- Log meals with food ingredients and quantities.
- View logged meals and total calories.
- Visualize nutrient breakdown (protein, fat, carbs) using pie charts.

NOTE - We have added Swap meal functionality, but currently it is only comparing with the exact value. We will change this into a range of values in upcoming days.

If you get the message box, "NO SUITABLE FOOD SWAPS FOUND", it is because the value was not the exact match. In this case, we have provided with data in the console. Please find corresponding data from the console and use the exact value to get the food swap suggestions. (For example: If your goal nutrient is, PROT then find PROT: x.xxx in the console (like 0.003) and use this exact value in delta input box. This will provide with the accurate meal suggestions based on the selected goal by user.

Team Members - 
- Veerman Kalra
- Sachin Kumar
- Divyansh Babbar
- Yogesh Sharma

Contributing:

- Fork the repository, create a new branch, and submit a pull request.
>>>>>>> branch 'main' of https://github.com/SachinKumar-York/YANKEES-EECS-3311.git
