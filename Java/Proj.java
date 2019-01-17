import java.util.*; 
import java.lang.*; 
import java.io.*; 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.ArrayList;
import java.lang.Math;
import java.util.stream.Collectors;
import java.text.Collator;
import java.lang.Throwable;
import java.lang.Exception;
import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.JIPQuery;
import com.ugos.jiprolog.engine.JIPSyntaxErrorException;
import com.ugos.jiprolog.engine.JIPTerm;
import com.ugos.jiprolog.engine.JIPTermParser;
import java.nio.file.Files;
import java.util.PriorityQueue ;

/* Java | Prolog, ARTIFICIAL INTELLIGENCE
 * Taxi AI Project
 *
 */


public class Proj {

	static int count_nodes = 0;
    static double clientX = 0.0, clientY = 0.0, x_dest = 0.0, y_dest = 0.0;
    static int persons = 0, luggage = 0, time = 0;
    static String language;
	static ArrayList<Node> nodes = new ArrayList<>(154404);
	static Node client_node;
   	static JIPEngine jip = new JIPEngine();
	static JIPEngine jip1 = new JIPEngine();
   	static JIPTermParser parser, parser1;
   	static JIPQuery jipQuery, jipQuery1, jipQuery2;
   	static JIPTerm term, term1, term2, term3;
	static LinkedList<LinkedList<Integer>> list_paths;
	static ArrayList<Node> avail_nodes = new ArrayList<>(154404);
	static ArrayList<Node> sort_avail_nodes = new ArrayList<>(154404);
	static int[] path_list;	
	static LinkedList<Integer> path;
	static long count = 0;
	static String[] spl;
	static String[] langs_spl;
	static LinkedList<String> languages;
	static Scanner scanner;
   	static LinkedList<Taxi> taxis = new LinkedList<>();
   	static LinkedList<Taxi> avail_taxis = new LinkedList<>();
  	static OutputStream out0;
   	static Writer writer0;
	static PriorityQueue<FrontTuple> Front;
	static int final_node_id;
	static int Y;
	static double TotalF, Fy, Fx, Gy;
	static int[] closure;
	static LinkedList<FinalTuple> final_list = new LinkedList<>();


	// argv[0] client.csv, argv[1] taxis.csv, argv[2] nodes.csv
    	public static void main(String[] args) throws FileNotFoundException, 			  JIPSyntaxErrorException, IOException {

		LinkedList<Taxi> taxis = new LinkedList<>();
		LinkedList<Taxi> avail_taxis = new LinkedList<>();

		out0 = new FileOutputStream("closure.pl", true);
	  	writer0 = new OutputStreamWriter(out0, "UTF-8");

		jip.consultFile("rules.pl");
     	parser = jip.getTermParser();

		/* CSV Reader
		 *
		 */		
		// CSV Reader client.csv
        scanner = new Scanner(new FileReader(args[0]));
        // Cut X, Y, X_dest, Y_dest, time, persons, language, luggage
        String line = scanner.nextLine();
		line = scanner.nextLine();
        // Get X, Y, X_dest, Y_dest, time, persons, language, luggage
        spl = line.split(",");
        clientX = Double.parseDouble(spl[0]);
        clientY = Double.parseDouble(spl[1]);
        x_dest = Double.parseDouble(spl[2]);
		y_dest = Double.parseDouble(spl[3]);

		String the_time = spl[4];
		String [] time_spl = the_time.split(":");
		time = Integer.parseInt(time_spl[0]);

		persons = Integer.parseInt(spl[5]);
		language = spl[6];
		luggage = Integer.parseInt(spl[7]);
        scanner.close();

		// CSV Reader taxis.csv
        scanner = new Scanner(new FileReader(args[1]));
		// Cut X,Y,id,available,capacity,languages,rating,long_distance,type
		line = scanner.nextLine();
		// Get X,Y,id,available,capacity,languages,rating,long_distance,type
		double taxiX = 0.0, taxiY = 0.0;
		String available, long_distance, type;
		int capacity, id;
		float rating;
		int count_avail_taxis = 0;
		double taxi_distance = Math.sqrt((clientX - x_dest) * (clientX - x_dest) + (clientY - y_dest) * (clientY - y_dest));
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			spl = line.split(",");
        	taxiX = Double.parseDouble(spl[0]);
			taxiY = Double.parseDouble(spl[1]);
			id = Integer.parseInt(spl[2]);
			available = spl[3];

			String [] capacity_spl = spl[4].split("-");
			capacity = Integer.parseInt(capacity_spl[1]);
			
			languages = new LinkedList<>();
			langs_spl = spl[5].split("/");
			for (String s : langs_spl) 
				languages.addLast(s);
	
			rating = Float.parseFloat(spl[6]);
			long_distance = spl[7];
			type = spl[8];		

			Taxi t = new Taxi(taxiX, taxiY, id, available, capacity, languages, rating, long_distance, type);
			taxis.addFirst(t);

			boolean avail = false;
			if (available.equals("yes") && capacity >= persons && 
				((long_distance.equals("no") && taxi_distance < 0.7) || (long_distance.equals("yes")))) {
				if ((type.equals("subcompact") && luggage <= 1) || (type.equals("compact") && luggage <= 3) 
								|| (type.equals("large") && luggage <= 5)) {
					for (String lang : languages) {
						if (language.equals(lang)) 
							avail = true;
					}
				}
			}

			if (avail) {
				avail_taxis.addFirst(t);
				// k = count_avail_taxis
				count_avail_taxis++;
			}
		}
        scanner.close();

        // CSV Reader nodes.csv  
		Writer writer1 = null;
		writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("belongsTo.pl"), "utf-8"));

        scanner = new Scanner(new FileReader(args[2]));
        // Cut X,Y,line_id,node_id,name
    	line = scanner.nextLine();
    	// Get X,Y,line_id,node_id,name
    	double nodeX = 0.0, nodeY = 0.0;
    	long line_id;
		long node_id;
		count_nodes = 0;
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            spl = line.split(",");
            nodeX = Double.parseDouble(spl[0]);
            nodeY = Double.parseDouble(spl[1]);
			line_id = Integer.parseInt(spl[2]);
            node_id = count_nodes;
        	Node n = new Node(nodeX, nodeY, line_id, node_id);
    		nodes.add(n);
			String str = "belongsTo(" + node_id + ", " + line_id + ").";
			writer1.append(str);
			writer1.append('\n');
			writer1.flush();
			count_nodes++;
    	}
        scanner.close();

		OutputStream out2 = new FileOutputStream("lineOneway.pl", true);
		Writer writer2 = new OutputStreamWriter(out2, "UTF-8");  

		OutputStream out3 = new FileOutputStream("lineObstacle.pl", true);
		Writer writer3 = new OutputStreamWriter(out3, "UTF-8");  

		OutputStream out6 = new FileOutputStream("lineLanes.pl", true);
		Writer writer6 = new OutputStreamWriter(out6, "UTF-8"); 

    	// CSV Reader lines.csv
	   	Scanner scanner4 = new Scanner(new FileReader(args[3]));
        /* 
		 * Cut id, highway, name, oneway, lit, lanes, maxspeed, railway, boundary, access, natural, barrier, tunnel, bridge,incline, 				waterway,
		 * busway, toll
		 */
		line = scanner4.nextLine();
        	// Get id(0), oneway(3), lanes(5), maxspeed(6), railway(7), boundary(8), access(9), natural(10), barrier(11), waterway(15)
    	int lanes = 0, maxspeed = 0;
		String oneway, railway, boundary, access, natural, barrier, waterway;
    	while (scanner4.hasNextLine()) {
      		line = scanner4.nextLine();
			line = line + ", a";
       		String [] spl1 = line.split(","); 
       		line_id = Integer.parseInt(spl1[0]);
			String highway = spl1[1]; 
			oneway = (spl1[3].isEmpty()) ? "no" : spl1[3]; 
			try {
            	lanes = Integer.parseInt(spl1[5]);
			} catch (NumberFormatException e) {
				lanes = 1;
			}
            try {
            	maxspeed = Integer.parseInt(spl1[6]);
            	} catch (NumberFormatException e) {
                	maxspeed = 30;
            	}

			writer6.append("lineLanes(" + line_id + ", " + lanes + ").");
			writer6.append('\n');
			writer6.flush();

			String obstacle = "no";
		    railway = (spl1[7].isEmpty()) ? "no" : "yes";
			boundary = (spl1[8].isEmpty()) ? "no" : "yes";
			access = (spl1[9].isEmpty()) ? "no" : "yes";
			natural = (spl1[10].isEmpty()) ? "no" : "yes";
			barrier = (spl1[11].isEmpty()) ? "no" : "yes";
			waterway = (spl1[15].isEmpty()) ? "no" : "yes";
			if (highway.equals("steps") || highway.equals("footway") || highway.equals("service") || highway.equals("pedestrian")
						|| highway.equals("tertiary") || highway.equals("unclassified")
						|| highway.equals("path") || highway.equals("track") || highway.equals("cycleway")
						|| railway.equals("yes") || boundary.equals("yes") || access.equals("yes") 
						|| natural.equals("yes") || barrier.equals("yes")  || waterway.equals("yes")) { 
				obstacle = "yes";
			}
		    	Line ln = new Line(line_id, oneway, lanes, maxspeed, railway, boundary, access, natural, barrier, waterway);
					
			String s = "lineObstacle(" + line_id + ", " + obstacle + ").";
	   		writer3.append(s);
			writer3.append('\n');	
			writer3.flush();

			if (obstacle == "no") {
				s = "lineOneway(" + line_id + ", " + oneway + ").";
				writer2.write(s);
				writer2.write('\n');
				writer2.flush();
			}
		}
    	scanner4.close();
		

		OutputStream out4 = new FileOutputStream("lineTraffic.pl", true);
		Writer writer4 = new OutputStreamWriter(out4, "UTF-8");

       	// CSV Reader traffic.csv
       	Scanner scanner5 = new Scanner(new FileReader(args[4]));
      	// Cut id, name, traffic_info
       	ine = scanner5.nextLine();        	
		// Get id,traffic_info
		String s_time;
		writer4.append("lineTraffic(_, 0, 0, medium).");
		writer4.append('\n');
		while (scanner5.hasNextLine()) {
			line = scanner5.nextLine();
			line = line + ",a";
			String [] spp = line.split(",");
			line_id = Integer.parseInt(spp[0]);
			if (spp[2].isEmpty()) {
				writer4.append("lineTraffic(" +	line_id + ", _, _, medium)."); 
				writer4.append('\n');
			}
			else {

				String [] ss = spp[2].split("/");
				String [] ss1 = ss[0].split("=");
				String [] ss2 = ss[1].split("=");
				String [] ss3 = ss[2].split("=");
				writer4.append("lineTraffic(" + line_id + ", 9, 11, " + ss1[1] + ").");
				writer4.append('\n');
				writer4.append("lineTraffic(" + line_id + ", 13, 15, " + ss2[1] + ").");
				writer4.append('\n');
				writer4.append("lineTraffic(" + line_id + ", 17, 19, " + ss3[1] + ").");
				writer4.append('\n');
				writer4.flush();

			}
		}

		OutputStream out5 = new FileOutputStream("next.pl", true);
		Writer writer5 = new OutputStreamWriter(out5, "UTF-8");  		      

       	OutputStream out7 = new FileOutputStream("nodeF.pl", true);
       	Writer writer7 = new OutputStreamWriter(out7, "UTF-8");


		JIPEngine jip2 = new JIPEngine();
		JIPEngine jip3 = new JIPEngine();
		JIPEngine jip4 = new JIPEngine();
		jip1.consultFile("lineObstacle.pl");
		jip2.consultFile("lineOneway.pl");
		jip3.consultFile("lineLanes.pl");
		jip4.consultFile("rules.pl");
		parser1 = jip1.getTermParser();
		JIPTermParser parser2 = jip2.getTermParser();
	   	JIPTermParser parser3 = jip3.getTermParser();
	  	JIPTermParser parser4 = jip4.getTermParser();

		int count_avail = 0;

		double Fn = 0.0, Fm = 0.0;
		for (int i=0; i<154403; i++) {
			Node n = nodes.get(i);
			Node m = nodes.get(i+1);
			String obstacle = "yes";
			jipQuery = jip1.openSynchronousQuery(parser1.parseTerm("lineObstacle(" + n.line_id + ", " + obstacle + ")."));
			if (jipQuery.nextSolution() != null) continue;
			avail_nodes.add(n);
			sort_avail_nodes.add(n);
			count_avail++;

			double dist_nm = Math.sqrt((m.x - n.x) * (m.x - n.x) + (m.y - n.y) * (m.y - n.y));
			double pyth_dist = Math.sqrt((clientX - n.x) * (clientX - n.x) + (clientY - n.y) * (clientY - n.y));
			
			jipQuery = jip3.openSynchronousQuery(parser3.parseTerm("lineLanes(" + n.line_id + ", Lanes)."));
    		term = jipQuery.nextSolution();
       		int Lanes = Integer.parseInt(term.getVariablesTable().get("Lanes").toString());

     		jipQuery = jip4.openSynchronousQuery(parser4.parseTerm("findTraffic(" + n.line_id + ", " + time + ", Traffic)."));
		   	term = jipQuery.nextSolution();
		  	String Traffic = "medium";
		  	int traffic_jam = 0;
		  	if (Traffic.equals("low"))
		   		traffic_jam = 1;
		 	else if (Traffic.equals("medium"))
		      	traffic_jam = 3;
		  	else
		       	traffic_jam = 5;

		  	// Setup the factors we take into account for F calculation
      	   	double dist_factor = pyth_dist * 10000;
		   	double lanes_factor = 0.0;
		   	double traffic_factor = 0.0;
		  	if (dist_factor >= 10.0) {
	   		    lanes_factor = lanes * 1.3;
		 		traffic_factor = traffic_jam * 3;
		   	}
		   	else if (dist_factor >= 1.0) {
		 	    lanes_factor = lanes * 0.16;
				traffic_factor = traffic_jam * 0.32;
		  	}
		  	else {
		     	lanes_factor = lanes * 0.016;
		      	traffic_factor = traffic_jam * 0.032;
		  	}
			Fn = dist_factor - lanes_factor + traffic_factor;


			writer7.append("nodeF(" + n.node_id + ", " + Fn + ").");
			writer7.append('\n');
			writer7.flush();

			
			if (n.line_id == m.line_id) {
				double pyth_dist_m = Math.sqrt((clientX - m.x) * (clientX - m.x) + (clientY - m.y) * (clientY - m.y));
				double dist_factor_m = pyth_dist_m * 10000;
				Fm = dist_factor_m - lanes_factor + traffic_factor;

				jipQuery = jip2.openSynchronousQuery(parser2.parseTerm("lineOneway(" + n.line_id + ", Oneway)."));
				term = jipQuery.nextSolution();
				oneway = term.getVariablesTable().get("Oneway").toString();

				// Setup factors we take into account for G calculation (only dist_factor changes)
				pyth_dist = Math.sqrt((m.x - n.x) * (m.x - n.x) + (m.y - n.y) * (m.y - n.y));
				double G = dist_nm * 10000 - lanes_factor + traffic_factor;

				if (oneway.equals("yes")) {
					writer5.append("next(" + n.node_id + ", " + m.node_id + ", " + G + ", " + Fn + ", " + Fm + ").");
					writer5.append('\n');
				}
			    	else if (oneway.equals("-1")) {
					writer5.append("next(" + m.node_id + ", " + n.node_id + ", " + G + ", " + Fm + ", " + Fn + ").");
					writer5.append('\n');
			   	}
		  		else if (oneway.equals("no")) {
			     	writer5.append("next(" + n.node_id + ", " + m.node_id + ", " + G + ", " + Fn + ", " + Fm + ").");
					writer5.append('\n');
			    	writer5.append("next(" + m.node_id + ", " + n.node_id + ", " + G + ", " + Fm + ", " + Fn + ").");
					writer5.append('\n');
		       	} 

			}
			writer5.flush();
*/
		}

		sort_avail_nodes.sort(Comparator.comparing(Node:: getX));

		int i = 0;
		while (i<count_avail-1) {
			Node n = sort_avail_nodes.get(i);
			double pyth_dist = Math.sqrt((clientX - n.x) * (clientX - n.x) + (clientY - n.y) * (clientY - n.y));

			jipQuery = jip3.openSynchronousQuery(parser3.parseTerm("lineLanes(" + n.line_id + ", Lanes)."));
			term = jipQuery.nextSolution();
			int Lanes = Integer.parseInt(term.getVariablesTable().get("Lanes").toString());
			jipQuery = jip.openSynchronousQuery(parser.parseTerm("findTraffic(" + n.line_id + ", " + time + ", Traffic)."));
			term = jipQuery.nextSolution();
			String Traffic = "medium";
			int traffic_jam = 0;
			if (Traffic.equals("low"))
				traffic_jam = 1;
			else if (Traffic.equals("medium"))
				traffic_jam = 3;
			else
				traffic_jam = 5;

			// Setup the factors we take into account for F calculation
			double dist_factor = pyth_dist * 10000;
	    	double lanes_factor = 0.0;
	    	double traffic_factor = 0.0;
	    	if (dist_factor >= 10.0) {
	      		lanes_factor = lanes * 1.3;
	        	traffic_factor = traffic_jam * 3;
	    	}
	    	else if (dist_factor >= 1.0) {
	        	lanes_factor = lanes * 0.16;
	        	traffic_factor = traffic_jam * 0.32;
	    	}
	    	else {
	        	lanes_factor = lanes * 0.016;
	        	traffic_factor = traffic_jam * 0.032;
	    	}
	    	Fn = dist_factor - lanes_factor + traffic_factor;

			int j = i + 1;
			while(true) {
				Node m = sort_avail_nodes.get(j);
          		double pyth_dist_m = Math.sqrt((clientX - m.x) * (clientX - m.x) + (clientY - m.y) * (clientY - m.y));
				jipQuery = jip3.openSynchronousQuery(parser3.parseTerm("lineLanes(" + m.line_id + ", Lanes)."));
        		term = jipQuery.nextSolution();
				Lanes = Integer.parseInt(term.getVariablesTable().get("Lanes").toString());

	            jipQuery = jip.openSynchronousQuery(parser.parseTerm(
						"findTraffic(" + m.line_id + ", " + time + ", Traffic)."));
    	        term = jipQuery.nextSolution();
    	    	Traffic = "medium";
    	    	traffic_jam = 0;
       	    	if (Traffic.equals("low"))
         			traffic_jam = 1;
       			else if (Traffic.equals("medium"))
           			traffic_jam = 3;
      			else
		 			traffic_jam = 5;

	        	// Setup the factors we take into account for F calculation
    	        dist_factor = pyth_dist_m * 10000;
        		lanes_factor = 0.0;
            	traffic_factor = 0.0;
	        	if (dist_factor >= 10.0) {
    				lanes_factor = lanes * 1.3;
              		traffic_factor = traffic_jam * 3;
        		}
	        	else if (dist_factor >= 1.0) {
           			lanes_factor = lanes * 0.16;
	        		traffic_factor = traffic_jam * 0.32;
   	    		}
     			else {
					lanes_factor = lanes * 0.016;
                	traffic_factor = traffic_jam * 0.032;
            	}
            	Fm = dist_factor - lanes_factor + traffic_factor;

				if (n.x == m.x && n.y == m.y) {
					j++;
					
					writer5.append("next(" + n.node_id + ", " + m.node_id + ", 0, " + Fn + ", " + Fm + ").");
			    	writer5.append('\n');
			  		writer5.append("next(" + m.node_id + ", " + n.node_id + ", 0, " + Fm + ", " + Fn + ").");
          			writer5.append('\n');
					writer5.flush();
				}
				else break;
			}
			i++;
		}

		
		OutputStream out42 = new FileOutputStream("node.pl", true);
    	Writer writer42 = new OutputStreamWriter(out42, "UTF-8");

		for (Node n : nodes) {
			writer42.append("node(" + n.node_id + ", " + n.x + ", " + n.y + ").");
			writer42.append('\n');
			writer42.flush();
		}


		list_paths = new LinkedList<>();			
		double distance;
		double min = 100.0;
    	for (Node n : avail_nodes) {
      		distance = Math.sqrt((clientX - n.x) * (clientX - n.x) + (clientY - n.y) * (clientY - n.y));
        	if (min >= distance) {
          		min = distance;
   	     		client_node = n;
	 		}
		}
		
		int counter = -1;
		Node taxi_node = null;
		for (Taxi taxi : avail_taxis) {
			counter++;
			min = 100.0;
			for (Node n : avail_nodes) {
				distance = Math.sqrt((taxi.x - n.x) * (taxi.x - n.x) + (taxi.y - n.y) * (taxi.y - n.y));
				if (min >= distance) {
					min = distance;
					taxi_node = n;
				}
			}
		
			count = 0;
		
			// Delete files of closure and front
			File file = new File("closure.pl");
			Files.deleteIfExists(file.toPath());
			out0 = new FileOutputStream("closure.pl", true);
			writer0 = new OutputStreamWriter(out0, "UTF-8");

			closure = new int [154404];
			for (int i=0; i<154404; i++)
				closure[i] = 0;

			Make new files of closure and front
			OutputStream out22 = new FileOutputStream("closure.pl", true);
       		Writer writer22 = new OutputStreamWriter(out22, "UTF-8");
			OutputStream out43 = new FileOutputStream("front.pl", true);
       		Writer writer43 = new OutputStreamWriter(out43, "UTF-8");

		  	jipQuery = jip.openSynchronousQuery(parser.parseTerm("findF(" + taxi_node.node_id + ", F)."));
	   		term = jipQuery.nextSolution();
			double Ftaxi = Double.parseDouble(term.getVariablesTable().get("F").toString());

			Front = new PriorityQueue<FrontTuple>();
			FrontTuple ft = new FrontTuple((int) (taxi_node.node_id), Ftaxi, 154404);
			Front.add(ft);
			writer43.append("front(" + taxi_node.node_id + ", " + Ftaxi + ").");
			writer43.append('\n');

			final_node_id = 0;

			path_list = new int [154404];
			path = new LinkedList<>();

			// 154404 is father's id of the taxi_node
			int taxi_node_id = (int) (taxi_node.node_id);
		
			FrontTuple ftp, gtp;
			double Gx = Ftaxi;
			int grandfather_id = 154404;
			double final_gx = 0.0;

			// ASTAR 
			while (!Front.isEmpty()) {	
			
				// Remove and get the first element from Front
				ftp = Front.poll();

				// Check for termination condition and if yes, put last node in path list
				if (ftp.id == client_node.node_id) {
					path_list[ftp.id] = grandfather_id;
					final_node_id = ftp.id;
					final_gx = Gx;
					break;
				}
		
				if (closure[ftp.id] == 1) 
					continue;
				
			
				// Add removed element to closure
				closure[ftp.id] = 1;

				// Add father to Path
				path_list[ftp.id] = ftp.father_id;

				// Find children
				jipQuery2 = jip.openSynchronousQuery(parser.parseTerm("findNext(" + ftp.id + ", Y, Gy, Fx, Fy)."));
				term = jipQuery2.nextSolution();
				if (term == null) 
					continue;
				while (term != null) {
					// Get Child
					Y = Integer.parseInt(term.getVariablesTable().get("Y").toString());
					Gy = Double.parseDouble(term.getVariablesTable().get("Gy").toString());
					Fx = Double.parseDouble(term.getVariablesTable().get("Fx").toString());
					Fy = Double.parseDouble(term.getVariablesTable().get("Fy").toString());
					TotalF = Fy + Gy - Fx + Gx;

					// Check if you have seen child in closure
					if (closure[Y] != 1) {
						gtp = new FrontTuple(Y, TotalF, ftp.id);
						Front.add(gtp);
					}
					// While next step
					term = jipQuery2.nextSolution();
				}

				gtp = Front.peek();
				Gx = gtp.fTotal;
				grandfather_id = gtp.father_id;
				continue;
			}
			// End of ASTAR

			double res_cost = Gx;

			// Find the path of the counter_th available taxi
			int parseId = final_node_id;
			while (parseId != 154404) {
				path.add(parseId);
				jipQuery = jip.openSynchronousQuery(parser.parseTerm("findXY(" + parseId + ", X, Y)."));
            							term = jipQuery.nextSolution();
				
//				System.out.print(term.getVariablesTable().get("X").toString() + ",");
//				System.out.println(term.getVariablesTable().get("Y").toString() + ",0");

				parseId = path_list[parseId];
			}

			list_paths.add(path);

			// Put ASTAR results in a list for sorting
			FinalTuple tuple = new FinalTuple(counter, Gx, taxi, parseId);
			final_list.addLast(tuple);			

		}

		// Show the k(=count_avail_taxis) closest to the client taxis
		System.out.println("The closest to the client available taxis are:");
		final_list.sort(Comparator.comparing(FinalTuple:: getGx));
		for (FinalTuple ft : final_list) {
			System.out.println("Taxi ID: " + ft.taxi.id);
		}

		int parseId = final_list.getFirst().parseId;
		LinkedList<Integer> best_list = list_paths.get(final_list.getFirst().counter);

		// Show the k(=count_avail_taxis) best taxis by ascending order
		System.out.println("The best user rating available taxis by ascending order are:");
		final_list.sort(Comparator.comparing(FinalTuple:: getTaxiRating));
		for (FinalTuple ft : final_list) {
			System.out.println("Taxi ID: " + ft.taxi.id + " with user rating " + ft.getTaxiRating());
		}

		// Display x,y of the best path
		System.out.println("The coordinates x,y of the best path are: ");
		for (int ID : best_list) {
			jipQuery = jip.openSynchronousQuery(parser.parseTerm("findXY(" + ID + ", X, Y)."));
            						term = jipQuery.nextSolution();
			System.out.print(term.getVariablesTable().get("X").toString() + ",");
			System.out.println(term.getVariablesTable().get("Y").toString() + ",0");
		}
	
/////////////////////////////////////////

		// Find the path to client destination that the best taxi will follow

		Node dest_node = null;
		min = 100.0;
  		for (Node n : avail_nodes) {
      			distance = Math.sqrt((x_dest - n.x) * (x_dest - n.x) + (y_dest - n.y) * (y_dest - n.y));
        		if (min >= distance) {
          			min = distance;
   	          		dest_node = n;
	 		}
		}

		jipQuery = jip.openSynchronousQuery(parser.parseTerm("findF(" + dest_node.node_id + ", F)."));
        	term = jipQuery.nextSolution();
		double Fdest = Double.parseDouble(term.getVariablesTable().get("F").toString());

		closure = new int [154404];
		for (int i=0; i<154404; i++)
			closure[i] = 0;
		
		Front = new PriorityQueue<FrontTuple>();
		FrontTuple ft = new FrontTuple((int) (dest_node.node_id), Fdest, 154404);
		Front.add(ft);

		final_node_id = 0;

		path_list = new int [154404];
		path = new LinkedList<>();

		// 154404 is father's id of the dest_node
		
		FrontTuple ftp, gtp;
		double Gx = Fdest;
		int grandfather_id = 154404;
		double final_gx = 0.0;

		// ASTAR (again...)		
		while (!Front.isEmpty()) {	
			
			// Remove and get the first element from Front
			ftp = Front.poll();

			// Check for termination condition and if yes, put last node in path list
			if (ftp.id == client_node.node_id) {
				path_list[ftp.id] = grandfather_id;
				final_node_id = ftp.id;
				final_gx = Gx;
				break;
			}
		
			if (closure[ftp.id] == 1) 
				continue;
				
			
			// Add removed element to closure
			closure[ftp.id] = 1;

			// Add father to Path
			path_list[ftp.id] = ftp.father_id;

			// Find children
			jipQuery2 = jip.openSynchronousQuery(parser.parseTerm("findNext(" + ftp.id + ", Y, Gy, Fx, Fy)."));
			term = jipQuery2.nextSolution();
			if (term == null) 
				continue;
			while (term != null) {
				// Get Child
				Y = Integer.parseInt(term.getVariablesTable().get("Y").toString());
				Gy = Double.parseDouble(term.getVariablesTable().get("Gy").toString());
				Fx = Double.parseDouble(term.getVariablesTable().get("Fx").toString());
				Fy = Double.parseDouble(term.getVariablesTable().get("Fy").toString());
				TotalF = Fy + Gy - Fx + Gx;

				// Check if you have seen child in closure
				if (closure[Y] != 1) {
					gtp = new FrontTuple(Y, TotalF, ftp.id);
					Front.add(gtp);
				}
				// While next step
				term = jipQuery2.nextSolution();
			}

			gtp = Front.peek();
			Gx = gtp.fTotal;
			grandfather_id = gtp.father_id;
			continue;
			
		}
		// End of ASTAR
		
		// Finito La Musica
		System.out.println("The path to the client's destination:");
		parseId = final_node_id;
		while (parseId != 154404) {
			path.add(parseId);
			jipQuery = jip.openSynchronousQuery(parser.parseTerm("findXY(" + parseId + ", X, Y)."));
            						term = jipQuery.nextSolution();
				
			System.out.print(term.getVariablesTable().get("X").toString() + ",");
			System.out.println(term.getVariablesTable().get("Y").toString() + ",0");

			parseId = path_list[parseId];
		}


	}
}		
