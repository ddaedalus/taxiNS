import java.util.LinkedList;

class Taxi { 
	public double x, y;
	public int id;
	public String available;
	public int capacity;
	public LinkedList<String> languages = new LinkedList<>();
	public float rating; 
	public String long_distance;
	public String type;

	Taxi(double X, double Y, int ID, String available, int capacity, LinkedList<String> Langs, float rat, String long_d, String type) {
		x = X;
		y = Y;
		id = ID;
		this.available = available;
		this.capacity = capacity;
		this.languages = languages;
		languages = Langs;
		rating = rat;
		long_distance = long_d;
		this.type = type;

	}	
}
