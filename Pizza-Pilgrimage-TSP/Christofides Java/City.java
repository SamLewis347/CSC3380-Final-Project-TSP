import java.util.Scanner;

	/*
	City in the tsp
	*/	

public class City {
	private int id;
	private int x;
	private int y;
	/*
	Line from a tsp file and converts it into a city object
	*/	
	public City(String line)
	{
		Scanner s = new Scanner(line);
		this.id = s.nextInt();
		this.x = s.nextInt();
		this.y = s.nextInt();
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return this.y;
	}
	
	@Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof City))
            return false;
        City c = (City)o;
        return (id == c.id);
    }

}
