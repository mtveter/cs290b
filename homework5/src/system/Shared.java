package system;

import java.util.List;

public interface Shared {
	
	public Object get();
	
	public boolean isOlderThan(Shared other);
	
	public List<Integer> getLbAdjacencyMapValue(Integer key);
	
	public void putAdjacencyList(int n);
	
	public void addNeighborToCity(Integer city, Integer neighbor);
	
	public void removeNeighborFromCity(Integer city, Integer neighbor);
	
	public Double getCurrentMstCost();
	
	public void setCurrentMstCost(Double mstCost);
	
	public void decrementCurrentMstCost(Double mstCost);
	
	public void incrementCurrentMstCost(Double mstCost);
	
	public void clearAdjList(Integer key);

}
