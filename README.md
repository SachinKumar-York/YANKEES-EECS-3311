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
