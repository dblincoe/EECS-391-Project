import edu.cwru.sepia.action.*;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import java.io.*;
import java.util.*;

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
    public Map<Integer, Action> initialStep(StateView newstate, HistoryView statehistory)
    {
        // get lists of player and enemy units
        List<Integer> unitIDs = newstate.getUnitIds(playernum);
        List<Integer> enemyUnitIDs = newstate.getUnitIds(enemyPlayerNum);
        
        // stores each unit's performed actions
        Map<Integer, Action> actions = new HashMap<>();
        
        if (enemyUnitIDs.size() == 0)
        {
            // Nothing to do because there is no one left to attack
            return actions;
        }
        
        List<Integer> pFootmen = new ArrayList<>();
        List<Integer> pArchers = new ArrayList<>();
        List<Integer> pBallistas = new ArrayList<>();
        List<Integer> eFootmen = new ArrayList<>();
        
        UnitView unitView;
        
        // separate player units by type
        for (Integer unitID : unitIDs)
        {
            unitView = newstate.getUnit(unitID);
            
            String unitType = unitView.getTemplateView().getName();
            
            if (unitType.equals("Footman"))
            {
                pFootmen.add(unitID);
            }
            else if (unitType.equals("Archer"))
            {
                pArchers.add(unitID);
            }
            else
            {
                pBallistas.add(unitID);
            }
        }
        
        int towerId = 5;
        for (Integer enemyId : enemyUnitIDs)
        {
            unitView = newstate.getUnit(enemyId);
            String unitType = unitView.getTemplateView().getName();
            if (unitType.equals("Footman"))
            {
                eFootmen.add(enemyId);
            }
            else
            {
                towerId = enemyId;
            }
        }
    
        if (!pFootmen.isEmpty())
        {
            int id = pFootmen.remove(0);
            actions.put(id, Action.createCompoundAttack(id, towerId));
        }
        
        /*for (int unitID : pArchers)
        {
	        actions.put(unitID, Action.createPrimitiveAttack(unitID, 0));
        }
	
	    for (int unitID : pBallistas)
	    {
		    actions.put(unitID, Action.createPrimitiveAttack(unitID, 0));
	    }*/
        
        return actions;
    }
    
    @Override
    public Map<Integer, Action> middleStep(StateView stateView, HistoryView historyView)
    {
        // This is a list of enemy units
        List<Integer> enemyUnitIDs = stateView.getUnitIds(enemyPlayerNum);
	    List<Integer> unitIDs = stateView.getUnitIds(playernum);
    
        // stores unit actions
        Map<Integer, Action> actions = new HashMap<>();
    
        // do nothing because there is no one left to attack
        if (enemyUnitIDs.size() == 0)
        {
            return actions;
        }
        
        int currentStep = stateView.getTurnNumber();
    
        List<Integer> eFootmen = new ArrayList<>();
        for (Integer id : enemyUnitIDs)
        {
            if (stateView.getUnit(id).getTemplateView().getName().equals("Footman"))
            {
                eFootmen.add(id);
            }
        }
	
	    List<Integer> pFootmen = new ArrayList<>();
	    for (Integer id : unitIDs)
	    {
		    if (stateView.getUnit(id).getTemplateView().getName().equals("Footman"))
		    {
			    pFootmen.add(id);
		    }
	    }
        
        if (pFootmen.size() < 3)
        {
        	for (Integer id : unitIDs)
	        {
		        actions.put(id, Action.createCompoundAttack(id, eFootmen.get(0)));
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
