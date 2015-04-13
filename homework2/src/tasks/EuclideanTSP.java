package tasks;
import java.util.ArrayList;
import java.util.List;

import api.Task;

public class EuclideanTSP implements Task<List<Integer>>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5779120438091516215L;
	private final ArrayList<City> inputCities;
	//
	// Minimum Spanning Tree Method
	//
	public EuclideanTSP(double[][] cities){
		this.inputCities = new ArrayList<City>();
		addCitiesToList(cities);
		
	}
	
	public List<Integer> execute() {
		ArrayList<City> cities = new ArrayList<City>();
		ArrayList<Edge> MST = new ArrayList<Edge>();

		ArrayList<City> treeCities = new ArrayList<City>();
		for(City city : this.inputCities){
			cities.add(city);
		}

		treeCities.add(cities.remove(0));		//add start city to tree

		while(!cities.isEmpty()){		// while there are cities not connected to tree

			Edge lightestEdge = null;
			double minDistance = Double.MAX_VALUE;	// initialize minimum distance 

			for(City start : treeCities){		// for each city in tree

				for(City end : cities){		// for each city not yet added to tree

					double distance = distance(start, end);		// get the distance from start to end

					if( distance < minDistance ){	// if distance is a new minimum
						lightestEdge = new Edge(start, end);
						minDistance = distance;		// set new minimum distance
					}
				}
			}
			cities.remove(lightestEdge.getEnd());					// remove end from city array
			treeCities.add(lightestEdge.getEnd());
			MST.add(lightestEdge);		// add edge to tree (grow the tree with lightest edge possible)
		}

		ArrayList<City> MSTtour = MSTTour(MST);
		List<Integer> cityTour = getCityIDList(MSTtour);
		return cityTour;
	}
	
	private void addCitiesToList(double[][] cities) {
		for (int i = 0; i < cities.length; i++){
			City tempCity = new City(i, cities[i][0], cities[i][1]);
			this.inputCities.add(tempCity);
		}
	}
	public List<Integer> getCityIDList(ArrayList<City> cityList) {
		List<Integer> cityTour = new ArrayList<Integer>();
		for(int i = 0; i < cityList.size(); i++) {
			cityTour.add(cityList.get(i).getId());
		}
		return cityTour;
	}
	
	//
	// Min Spanning Tree Tour Method using depth first search
	//
	private ArrayList<City> MSTTour(ArrayList<Edge> m){	
		
		ArrayList<Edge> MST = new ArrayList<Edge>();
		ArrayList<City> MSTTour = new ArrayList<City>();
		
		for(Edge edge : m){
			MST.add(edge);
		}
		
		return MSTTour(MST.get(0).getStart(), MST, MSTTour);
	}
	
	private ArrayList<City> MSTTour(City city, ArrayList<Edge> MST, ArrayList<City> MSTTour){
		
		MSTTour.add(city);
		
		for(Edge edge : MST){
			City parent = edge.getStart();
			City child = edge.getEnd();
			if( parent.equals(city) && !MSTTour.contains(child)){		// for each child not already in tour
				MSTTour = MSTTour(edge.getEnd(), MST, MSTTour);
			}
		}
		return MSTTour;
	}

	//
	// Weight method
	//
	public static String weight(ArrayList<Edge> MST) {
		double weight = 0;
		for(Edge edge : MST){
			weight += distance(edge.getStart(), edge.getEnd());
		}
		return Double.toString(weight);
	}

	//
	// Length method
	//
	public static String length(ArrayList<City> MSTTour) {
		double length = 0;
		for (int i=0 ; i<MSTTour.size()-1 ; i++) {
			length += distance(MSTTour.get(i), MSTTour.get(i+1)) ;
		}
		return Double.toString(length);
	}

	//
	// Distance Method
	//
	private static double distance(City start, City end){
		double px = start.getX();
		double py = start.getY();
		double qx = end.getX();
		double qy = end.getY();

		return Math.sqrt(( (qx-px)*(qx-px) ) + ( (qy-py)*(qy-py) ));
	}

	@Override
	public List<Integer> call() {
		// TODO Auto-generated method stub
		return null;
	}
}

