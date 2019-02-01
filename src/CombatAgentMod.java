import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombatAgentMod extends Agent
{
	
	private int enemyPlayerNum = 1;
	
	public CombatAgentMod(int playernum, String[] otherargs)
	{
		super(playernum);
		if(otherargs.length > 0)
		{
			enemyPlayerNum = new Integer(otherargs[0]);
		}
	}
	
	@Override
	public Map<Integer, Action> initialStep(StateView newstate,
	                                        HistoryView statehistory)
	{
		// This stores the action that each unit will perform
		// if there are no changes to the current actions then this
		// map will be empty
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		// This is a list of all of your units
		// Refer to the resource agent example for ways of
		// differentiating between different unit types based on
		// the list of IDs
		List<Integer> myUnitIDs = newstate.getUnitIds(playernum);
		
		// This is a list of enemy units
		List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
		
		if(enemyUnitIDs.size() == 0)
		{
			// Nothing to do because there is no one left to attack
			return actions;
		}
		
		// start by commanding every single unit to attack an enemy unit
		for(Integer myUnitID : myUnitIDs)
		{
			// Command all of my units to attack the first enemy unit in the list
			actions.put(myUnitID, Action.createCompoundAttack(myUnitID, enemyUnitIDs.get(0)));
		}
		
		return actions;
	}
	
	@Override
	public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView)
	{
		
		// This is a list of enemy units
		List<Integer> enemyUnitIDs = stateView.getUnitIds(enemyPlayerNum);
		
		int currentStep = stateView.getTurnNumber();
		
		// This stores the action that each unit will perform
		// if there are no changes to the current actions then this
		// map will be empty
		Map<Integer, Action> actions = new HashMap<Integer, Action>();
		
		if(enemyUnitIDs.size() == 0)
		{
			// Nothing to do because there is no one left to attack
			return actions;
		}
		
		List<Integer> unitIDs = stateView.getUnitIds(playernum);
		
		Unit.UnitView unitView;
		
		List<Integer> footmanIDs = new ArrayList<Integer>();
		List<Integer> archerIDs = new ArrayList<Integer>();
		List<Integer> towerIDs = new ArrayList<Integer>();
		List<Integer> ballistaIDs = new ArrayList<Integer>();
		
		//This for loop puts the units in their respective lists, so
		//we can identify what kind of unit they are
		for(Integer unitID : unitIDs)
		{
			unitView = stateView.getUnit(unitID);
			
			String unitTypeName = unitView.getTemplateView().getName();
			
			if(unitTypeName.equals("Footman"))
			{
				footmanIDs.add(unitID);
			}
			else if(unitTypeName.equals("Archer"))
			{
				archerIDs.add(unitID);
			}
			else if(unitTypeName.equals("Tower"))
			{
				towerIDs.add(unitID);
			}
			else
			{
				ballistaIDs.add(unitID);
			}
		}
		int min = -1;
		//go through action history
		for(ActionResult feedback : historyView.getCommandFeedback(playernum, currentStep - 1).values())
		{
			// if the previous action is no longer in progress (either due to failure or completion)
			// then add a new action for this unit
			if(feedback.getFeedback() != ActionFeedback.INCOMPLETE)
			{
				int unitID = feedback.getAction().getUnitId();
				
				int xplayer = stateView.getUnit(unitID).getXPosition();
				int yplayer = stateView.getUnit(unitID).getYPosition();
				
				int enemyMinID = -1;
				
				for(int i = 0; i < enemyUnitIDs.size(); i++)
				{
					int xenemy = stateView.getUnit(enemyUnitIDs.get(i)).getXPosition();
					int yenemy = stateView.getUnit(enemyUnitIDs.get(i)).getYPosition();
					int distance = (xenemy - xplayer) + (yenemy - yplayer);
					if(distance < min)
					{
						min = distance;
						enemyMinID = enemyUnitIDs.get(i);
					}
				}
				actions.put(unitID, Action.createCompoundAttack(unitID, enemyMinID));
			}
		}
		return actions;
	}
	
	@Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView)
    {
	    System.out.println("Finished the episode");
    }
    
    @Override
    public void savePlayerData(OutputStream outputStream)
    {
    
    }
    
    @Override
    public void loadPlayerData(InputStream inputStream)
    {
    
    }
}
