class FinalTuple {
	int counter;
	double Gx;
	Taxi taxi;
	int parseId;

	FinalTuple(int count, double gx, Taxi t, int parse) {
		counter = count;
		Gx = gx;
		taxi = t;
		parseId = parse;
	}
	
	double getGx() {return Gx;}

	float getTaxiRating() {
		return taxi.rating;
	}

}
