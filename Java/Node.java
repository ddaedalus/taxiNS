class Node {

	public double x, y;
	public long line_id;
	public long node_id;

	Node(double X, double Y, long ID, long PID) {
		x = X;
		y = Y;
		line_id = ID;
		node_id = PID;
	}

	public double getX() {
		return x;
	}
}
