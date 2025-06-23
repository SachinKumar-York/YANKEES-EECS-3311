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

