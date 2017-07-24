This project was part of the 'SCC.413 Applied Data Mining' module of Lancaster University. 

Follow the below instructions to get the search engine working

STEPS:
1. Install Apache Web Server and MySQL database.
	a. sudo apt-get install apache2
	b. sudo apt-get install mysql-server
2. Install phpMyAdmin (optional - you can use it to view the records stored in the database)
3. Start MySQL by running the following command:
	"sudo /etc/init.d/mysql start" 
4. Create a project with the following files:
	a. QueryProcessor.java
	b. Spider.java
5. Make sure that you import all the files specified into the "libCrawler" folder into the project.
6. In the Spider.java file, set the path that you want to create your index by adding a path to the "indexPath" variable (the variable 	is initially set to be blank).
7. Also, in the Spider.java file, set the username and the password of MySQL you have just installed. Do that by adjusting the values of the "username" and "password" variables (the variables are initially set to be blank).
8. Compile and run the code (the seed page is initially set to "http://dbpedia.org/resource/Leeds_University")
9. Create a new project and add the search.java file and the "META-INF" folder in the same directory.
10. Make sure that you import all the files specified into the "libSearch" folder into the project.
11. In the search.java file set the path of your directory index by adding the path to the "indexPath" variable (the variable is initially set to be blank).
12. Compile the project and build a jar file using this file (search.java) and name the jar file "search.jar" (the folder "META_INF" includes the manifest file and is essential to get the jar file working properly).
13. Place the following files into your server directory (/var/www/html/):
	a. searchInterface.html
	b. search.php
	c. search.jar
	d. all the files of the "libSearch" folder
14. Access the initial interface by accessing the following URL:
	"http://localhost/searchInterface.html"
15. Perform queries and observe the results