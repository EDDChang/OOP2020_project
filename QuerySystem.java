import java.sql.*;
import java.util.Scanner;
import AllException.*;

public class QuerySystem{
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
		//Create database if it isn't exist
		try{
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:Accounts.db");

			stmt = c.createStatement();
			String sql = "CREATE TABLE USER " + 
						 "(NAME		CHAR	NOT NULL," + 
						 "PASSWORD	TEXT	NOT NULL)";
			stmt.executeUpdate(sql);
			//stmt.close();
			//c.close();
		}catch(Exception e){
			if(!e.getMessage().equals("[SQLITE_ERROR] SQL error or missing database (table USER already exists)"))
			{
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			}
		}
		boolean RegToLog = false;
		//For register, insert sql
		
		if(mode.equals("R") || mode.equals("r")){
			String name = "";
			String password;
			System.out.println("Register an account.");
			try{
				/*Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:Accounts.db");
				c.setAutoCommit(false);
				stmt = c.createStatement();*/
			
				while(true){
					try{
						System.out.printf("Please enter user name:");
						name = scanner.next();
						//ResultSet rs = stmt.executeQuery("SELECT "+name+" FROM USER;");

						ResultSet rs = stmt.executeQuery("SELECT * FROM USER;");
						boolean valid = true;
						while( rs.next() ){
							String n = rs.getString("name");
							System.out.println(n);
							if(name.equals(n))
								valid = false;
						}
						if(!valid)
							throw new UserAlreadyExistException("123");
					}
					catch(UserAlreadyExistException e){
						System.out.println("User name already exist, Please try again.");
						continue;
					}
					System.out.printf("Please enter password :");
					password = scanner.next();
					String sql = "INSERT INTO USER (NAME,PASSWORD) " + 
								 "VALUES ("+name+" ,"+password+");";
					stmt.executeUpdate(sql);
					System.out.println("Register success. Redirect to Login page .......");
					RegToLog = true;
					break;
				}
				stmt.close();
				c.commit();
				c.close();
			}
			catch(Exception e){
				System.err.println("fuck" +  e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		}
		//For log in, select sql
		if(mode.equals("L") || mode.equals("l") || RegToLog){
			System.out.println("Welcome to Login page.");
			String name = "";
			String password;
			
			try{
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:Accounts.db");
				c.setAutoCommit(false);
			
				while(true){
					try{
						System.out.printf("Please enter user name:");
						name = scanner.next();
						System.out.printf("Please enter user's password:");
						password = scanner.next();
						//ResultSet rs = stmt.executeQuery("SELECT "+name+" FROM USER;");

						ResultSet rs = stmt.executeQuery("SELECT * FROM USER;");
						boolean login = false;
						while( rs.next() ){
							String n = rs.getString("name");
							String p = rs.getString("password");
							if(name.equals(n) && password.equals(p))
								login = true;
						}
						if(!login){
							throw new UserOrPasswordException("123");
						}
					}
					catch(UserOrPasswordException e){
						System.out.println("User name or password error, Please try again.");
						continue;
					}
					System.out.println("Log in success.");
					break;
				}
				stmt.close();
				c.close();
			}
			catch(Exception e){
				System.err.println("fuxk" +  e.getClass().getName() + ": " + e.getMessage() );
				System.exit(0);
			}
		}
	}	
}
