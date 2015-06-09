package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TspShared implements Shared,Serializable{
	
	/** Generated serial idenitifer	 */
	private static final long serialVersionUID = -5789613529078638250L;
	private double value;
	public ConcurrentHashMap<Integer, List<Integer>> lbAdjacencyMap = new ConcurrentHashMap<Integer, List<Integer>>();
	public Double currentMstCost;
	
	public TspShared(double value){
		this.value = value;
		putAdjacencyList(16);
	}

	@Override
	public Object get() {
		return value;
	}

	@Override
	public boolean isOlderThan(Shared other) {
		return ((double) other.get() < (double) this.get());
	}
	
	@Override
	public List<Integer> getLbAdjacencyMapValue(Integer key) {
		return lbAdjacencyMap.get(key);
	}
	
	@Override
	public void putAdjacencyList(int n) {
		if(lbAdjacencyMap.isEmpty()) {
			for (int v = 0; v < n; v++) {
				lbAdjacencyMap.put(v, Collections.synchronizedList(new ArrayList<Integer>()));
			}
		}
	}

	@Override
	public Double getCurrentMstCost() {
		return currentMstCost;
	}
	@Override
	public void setCurrentMstCost(Double mstCost) {
		this.currentMstCost = mstCost;
	}
	@Override
	public void decrementCurrentMstCost(Double mstCost) {
		if(mstCost <= this.currentMstCost) {
			this.currentMstCost -= mstCost;
		}
	}
	@Override
	public void incrementCurrentMstCost(Double mstCost) {
		if(mstCost > 0) {
			this.currentMstCost += mstCost;
		}
	}

	@Override
	public void addNeighborToCity(Integer city, Integer neighbor) {
		List<Integer> list = lbAdjacencyMap.get(city);
		list.add(neighbor);
	}

	@Override
	public void removeNeighborFromCity(Integer city, Integer neighbor) {
		lbAdjacencyMap.get(city).remove(new Integer(neighbor));
	}

	@Override
	public void clearAdjList(Integer key) {
		lbAdjacencyMap.get(key).clear();
	}
}
