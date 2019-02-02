import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import java.io.*;
import java.util.*;

@SuppressWarnings("WeakerAccess")
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
    public Map<Integer, Action> initialStep(StateView newState, HistoryView stateHistory)
    {
        // get lists of player and enemy units
        List<Integer> unitIDs = newState.getUnitIds(playernum);
        List<Integer> enemyUnitIDs = newState.getUnitIds(enemyPlayerNum);
        
        // stores each unit's performed actions
        Map<Integer, Action> actions = new HashMap<>();
        
        if (enemyUnitIDs.size() == 0)
        {
            // Nothing to do because there is no one left to attack
            return actions;
        }
        
        List<Integer> playerFootmen = new ArrayList<>();
        UnitView unitView;
        
        // separate player units by type
        for (Integer unitID : unitIDs)
        {
            unitView = newState.getUnit(unitID);
            
            String unitType = unitView.getTemplateView().getName();
            
            if (unitType.equals("Footman"))
            {
                playerFootmen.add(unitID);
            }
        }
        int towerId = 5;
        for (int i : enemyUnitIDs)
        {
            if (newState.getUnit(i).getTemplateView().getName().equals("ScoutTower"))
            {
                towerId = i;
            }
        }
        
        if (!playerFootmen.isEmpty())
        {
            int id = playerFootmen.remove(0);
            actions.put(id, Action.createCompoundAttack(id, towerId));
        }
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
        
        List<Integer> enemyFootmen = new ArrayList<>();
        for (Integer id : enemyUnitIDs)
        {
            if (stateView.getUnit(id).getTemplateView().getName().equals("Footman"))
            {
                enemyFootmen.add(id);
            }
        }
        
        List<Integer> playerFootmen = new ArrayList<>();
        for (Integer id : unitIDs)
        {
            if (stateView.getUnit(id).getTemplateView().getName().equals("Footman"))
            {
                playerFootmen.add(id);
            }
        }
        
        if (playerFootmen.size() < 3)
        {
            for (Integer id : unitIDs)
            {
                actions.put(id, Action.createCompoundAttack(id, enemyFootmen.get(0)));
            }
        }
        
        return actions;
    }
    
    @Override
    public void terminalStep(StateView stateView, HistoryView historyView)
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
