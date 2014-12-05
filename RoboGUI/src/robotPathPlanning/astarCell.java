package robotPathPlanning;

import robotGUI.Cell;
import robotGenericValues.StandardValues;

public class astarCell {
	
	astarCell parent;
	Cell pos;
	float f,g,h;


	public void setPos(Cell pos) {
		this.pos = pos;
	}


	public astarCell(Cell pos)
	{
		this.pos = pos;
		this.parent = null;
	}

	public astarCell(Cell pos, astarCell parent)
	{
		this.pos = pos;
		this.parent = parent;
	}
	
	public boolean equals(astarCell q)
	{
		return q.getPos().getCol() == pos.getCol() && q.getPos().getRow() == pos.getRow();
		
	}
	
	public float getCost(Cell Goal)
	{
		if(parent!= null)
			this.g = parent.g + StandardValues.ASTAR_STEP_COST; 
		else
			this.g = StandardValues.ASTAR_STEP_COST;
		this.f = this.g + calcHeuristics(Goal);
		return this.f;
	}

	public astarCell getParent()
	{
		return parent;
	}


	private float calcHeuristics(Cell goal)
	{
		return (float) Math.pow(Math.pow(goal.getCol() - pos.getCol(), 2) + Math.pow(goal.getRow() - pos.getRow(), 2), 1/2); 
	}

	public Cell getPos() {
		return pos;
	}
}
