class FrontTuple implements Comparable<FrontTuple> {
	public int id;
	public double fTotal;
	public int father_id;

	FrontTuple(int ID, double f, int father) {
		id = ID;
		fTotal = f;
		father_id = father;
	}

	public boolean equals(FrontTuple other) {
		return this.fTotal == other.fTotal;
	}

	public int compareTo(FrontTuple other) {
		if(fTotal == other.fTotal)
			return 0;
		else if (fTotal > other.fTotal) 
			return 1;
		else
			return -1;
	}
}

