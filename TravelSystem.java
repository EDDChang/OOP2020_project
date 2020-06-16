import java.sql.*;
import java.util.Scanner;
import AllException.*;

public class TravelSystem{
	public static String selectCmd(String name){
		return "SELECT * FROM USER WHERE NAME = '" + name + "';";
	}
	public static String insertCmd(String name, String password){
		return 	"INSERT INTO USER (NAME,PASSWORD)  VALUES ('" + name + "' ,'"+password+"');";
	}
	public static void main(String[] args){
		System.out.println("Welcome to Travel Booking System.");
		System.out.println("Please type 'R' or 'r' for register.");
		System.out.println("Please type 'L' or 'l' for log in.");
		Scanner scanner = new Scanner(System.in);
		String mode;
		while(true){
			mode = scanner.next();
			try{
				if(!mode.equals("R") && !mode.equals("r") && !mode.equals("L") && !mode.equals("l"))
					throw new WelcomeException("invalid input.");
				else
					break;
			}
			catch(WelcomeException e){
				System.out.println("Please try again and ensure that you type correct option.");
				continue;
			}
		}
		//Connect to SQLite
		Connection c = null;
		Statement stmt = null;
		//Connect to database
		try{

			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Accounts.db");
		}
		catch(Exception e){
			
			System.err.println( e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		
		}
		//Create database if it doesn't exist
		try{
			
			stmt = c.createStatement();
			String sql = "CREATE TABLE USER " + 
						 "(NAME		TEXT	NOT NULL," + 
						 "PASSWORD	TEXT	NOT NULL)";
			stmt.executeUpdate(sql);	
		
		}catch(Exception e){
			if(!e.getMessage().equals("[SQLITE_ERROR] SQL error or missing database (table USER already exists)")){
			
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			
			}
		}
		//register
		boolean login = false;
		if(mode.equals("R") || mode.equals("r")){
			String name ;
			String password;
			System.out.println("Register an account.");
			while(true){
				try{
					System.out.printf("Please enter user name:");
					name = scanner.next();
					c.setAutoCommit(false);
					ResultSet rs = stmt.executeQuery(selectCmd(name));
					boolean valid = true;
					while( rs.next() ){
						String n = rs.getString("name");
						System.out.println(n);
						if(name.equals(n))
							throw new UserAlreadyExistException("User name already exist.");
					}
					System.out.printf("Please enter password :");
					password = scanner.next();
					stmt.executeUpdate(insertCmd(name, password));
					c.commit();
					System.out.println("Register success.");
					login = true;
					break;

				
				}
				catch(UserAlreadyExistException e){
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
					continue;
				}
				catch(Exception ee){
					System.out.println(ee.getClass().getName() + ": " + ee.getMessage());
					continue;
				}
			}
		}
		if(mode.equals("l") || mode.equals("L")){
			String name ;
			String password;
			System.out.println("Welcome to login page.");
			while(true){
				try{
					System.out.printf("Please enter user name:");
					name = scanner.next();
					System.out.printf("Please enter password :");
					password = scanner.next();
					
					c.setAutoCommit(false);
					ResultSet rs = stmt.executeQuery(selectCmd(name));
					boolean account_exist = false;
					while( rs.next() ){
						account_exist = true;
						String n = rs.getString("name");
						String p = rs.getString("password");
						if(!password.equals(p))
							throw new PasswordException("Password wrong.");
					}
					if(!account_exist)
						throw new NoAccountException("Account doesn's exist.");
					login = true;
					break;
				}
				catch(PasswordException e){
					System.out.println(e.getClass().getName() + ": " + e.getMessage());
					continue;
				}
				catch(NoAccountException ee){
					System.out.println(ee.getClass().getName() + ": " + ee.getMessage());
					continue;
				}
				catch(Exception eee){
					System.out.println(eee.getClass().getName() + ": " + eee.getMessage());
					continue;
				}
			}
		}
		if(login){
			System.out.println("You have login bitch.");
		}
	}
}
