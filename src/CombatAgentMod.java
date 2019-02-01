import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;
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
        if (otherargs.length > 0)
        {
            enemyPlayerNum = new Integer(otherargs[0]);
        }
    }
    
    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView)
    {
        return null;
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
        
        List<Integer> unitIDs = stateView.getUnitIds(playernum);
        
        Unit.UnitView unitView;
        
        List<Integer> footmanIDs = new ArrayList<Integer>();
        List<Integer> archerIDs = new ArrayList<Integer>();
        List<Integer> towerIDs = new ArrayList<Integer>();
        List<Integer> ballistaIDs = new ArrayList<Integer>();
        
        //This for loop puts the units in their respective lists, so
        //we can identify what kind of unit they are
        for (Integer unitID : unitIDs)
        {
            unitView = stateView.getUnit(unitID);
            
            String unitTypeName = unitView.getTemplateView().getName();
            
            if (unitTypeName.equals("Footman"))
            {
                footmanIDs.add(unitID);
            }
            else if (unitTypeName.equals("Archer"))
            {
                archerIDs.add(unitID);
            }
            else if (unitTypeName.equals("Tower"))
            {
                towerIDs.add(unitID);
            }
            else
            {
                ballistaIDs.add(unitID);
            }
        }
	
	    List<Unit.UnitView> units = stateView.getUnits(enemyPlayerNum);
	
	    // go through the action history
	    for(ActionResult feedback : historyView.getCommandFeedback(playernum, currentStep - 1).values())
	    {
		    // if the previous action is no longer in progress (either due to failure or completion)
		    // then add a new action for this unit
		    if(feedback.getFeedback() != ActionFeedback.INCOMPLETE)
		    {
			    int unitID = feedback.getAction().getUnitId();
			    //check if the
			    for(Unit.UnitView unit : units)
			    {
				    if(unit.equals("Footman"))
				    {
					    actions.put(unitID, Action.createCompoundAttack(unitID, unit.getID()));
				    }
				    else
				    {
					    actions.put(unitID, Action.createCompoundAttack(unitID, enemyUnitIDs.get(0)));
				    }
			    }
		    }
	    }
        /*
        //This for loop gives the footmen an action
        for (Integer footmanID : footmanIDs)
        {
            for (ActionResult feedback : historyView.getCommandFeedback(playernum, currentStep - 1).values())
            {
                
                // if the previous action is no longer in progress (either due to failure or completion)
                // then add a new action for this unit
                if (feedback.getFeedback() != ActionFeedback.INCOMPLETE)
                {
                    // attack the first enemy unit in the list
                    actions.put(footmanID, Action.createCompoundAttack(footmanID, enemyUnitIDs.get(0)));
                }
            }
        }
	    
        List<Integer> templateIDs = stateView.getAllTemplateIds();
        
        //This for loop gives the archers an action
        for (Integer archerID : archerIDs)
        {
        	for(Integer templateID: templateIDs)
	        {
	        	if(stateView.hasUnit(enemyPlayerNum, templateID))
		        {
		        	actions.put(archerID, Action.createCompoundAttack(archerID, enemyUnitIDs.get(templateID)));
		        }
	        }
        }
        
        //This for loop gives the towers an action
        for (Integer towerID : towerIDs)
        {
	        for(Integer templateID : templateIDs)
	        {
		        if(stateView.hasUnit(enemyPlayerNum, templateID))
		        {
			        actions.put(towerID, Action.createCompoundAttack(towerID, enemyUnitIDs.get(templateID)));
		        }
		        else
		        {
			        actions.put(towerID, Action.createCompoundAttack(towerID, enemyUnitIDs.get(0)));
		        }
	        }
        }
        
        //This for loop gives the ballista an action
        for (Integer ballistaID : ballistaIDs)
        {
	        for(Integer templateID : templateIDs)
	        {
		        if(stateView.hasUnit(enemyPlayerNum, templateID))
		        {
			        actions.put(ballistaID, Action.createCompoundAttack(ballistaID, enemyUnitIDs.get(templateID)));
		        }
		        else
		        {
		        	actions.put(ballistaID, Action.createCompoundAttack(ballistaID, enemyUnitIDs.get(0)));
		        }
	        }
        }
        */
	
	    return actions;
    }
    
    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView)
    {
    
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
