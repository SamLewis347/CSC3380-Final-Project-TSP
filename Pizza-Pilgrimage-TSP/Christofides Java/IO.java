import java.util.Scanner;
import java.io.File;
import java.io.IOException;
import java.util.Stack;
import java.io.PrintStream;

public class FileIO {

	/*
	Returns the string of the filename entered by the user
	*/
	public static String getFileName()
	{
		Scanner user_input = new Scanner(System.in);
		
		System.out.printf("Please enter test file name[tsp_example_1.txt]: ");
		String fileName = user_input.nextLine();
		
		user_input.close();
		
		if(fileName.equals(""))
		{
			fileName = "tsp_example_1.txt";
		}
		return fileName;
	}
	
	/*
	Asks user for input file for tsp
	Searches for file on local directory
	Defaults to tsp_example_1.txt 
	Returns an array of cities for tsp and the filename entered by the user
	*/	
	public static City[] openMap(String fileName){

		
		City[] cities = new City[8];
		
		try
		{
			Scanner file = new Scanner(new File(fileName));
			int i = 0;
			//make a city object out of each line in the file
			while(file.hasNextLine())
			{
				//double size of cities if there isn't enough space in the array
				if(i >= cities.length)
				{
					City[] newCities = new City[cities.length * 2];
					for(int j = 0; j < cities.length; j++)
					{
						newCities[j] = cities[j];
					}
					cities = newCities;
				}
				
				cities[i] = new City(file.nextLine());
				i++;
			}
			file.close();
			
			// remove null values from array...
			City[] newCities = new City[i];
			for(int j = 0; j < i; j++)
			{
				newCities[j] = cities[j];
			}
			cities = newCities;
			
		}
		catch(IOException e)		
		{	
			System.out.println( e );
			System.exit(1);
		}
		

		return cities;
	}
	
	/*
	Saves results of TSP to passed fileName
	*/	
	public static void writeMap(int totalDistance, Stack<City> path, String fileName){
		try
		{	PrintStream writer = new PrintStream( new File(fileName));
			writer.printf("%d\n", totalDistance);
			while(!path.isEmpty())
			{
					writer.printf("%d\n", path.pop().getId());
			}
			writer.close();
		}
		catch(IOException e)
		{	
			System.out.println( e );
		}
	}
	

}
