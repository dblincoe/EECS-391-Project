import edu.cwru.sepia.action.Action;
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
        
        int i = 0;
        
        // lure out enemy footmen
        actions.put(pFootmen.get(0), Action.createCompoundAttack(pFootmen.remove(0), towerId));
        
    /*
        while (!pArchers.isEmpty())
        {
            // attack all footmen with archers and ballistas
            actions.put(pArchers.get(0), Action.createCompoundAttack(pArchers.get(0), eFootmen.get(i % eFootmen.size
            ())));
            i++;
            pArchers.remove(0);
        }*/
        while (!pArchers.isEmpty())
        {
            // attack all footmen with archers and ballistas
            actions.put(pArchers.get(0), Action.createCompoundAttack(pArchers.get(0),
                    eFootmen.get(i++ % eFootmen.size())));
            pArchers.remove(0);
        }
        
        return actions;
    }
    
    @Override
    public Map<Integer, Action> middleStep(StateView stateView, HistoryView historyView)
    {
        
        /*// This is a list of enemy units
        List<Integer> enemyUnitIDs = stateView.getUnitIds(enemyPlayerNum);
        
        // This stores the action that each unit will perform
        // if there are no changes to the current actions then this
        // map will be empty
        Map<Integer, Action> actions = new HashMap<Integer, Action>();
        
        if (enemyUnitIDs.size() == 0)
        {
            // Nothing to do because there is no one left to attack
            return actions;
        }
        
        List<Integer> unitIDs = stateView.getUnitIds(playernum);
        
        UnitView unitView;
        
        List<Integer> footmanIDs = new ArrayList<Integer>();
        List<Integer> archerIDs = new ArrayList<Integer>();
        List<Integer> towerIDs = new ArrayList<Integer>();
        List<Integer> ballistaIDs = new ArrayList<Integer>();
        
        //This for loop puts the units in their respective lists, so
        //we can identify what kind of unit they are
        for (Integer unitID : unitIDs)
        {
            unitView = stateView.getUnit(unitID);
            
            String unitType = unitView.getTemplateView().getName();
            
            if (unitType.equals("Footman"))
            {
                footmanIDs.add(unitID);
            }
            else if (unitType.equals("Archer"))
            {
                archerIDs.add(unitID);
            }
            else if (unitType.equals("Tower"))
            {
                towerIDs.add(unitID);
            }
            else
            {
                ballistaIDs.add(unitID);
            }
        }
        int currentStep = stateView.getTurnNumber();
        int min = -1;
        //go through action history
        for (ActionResult feedback : historyView.getCommandFeedback(playernum, currentStep - 1).values())
        {
            // if the previous action is no longer in progress (either due to failure or completion)
            // then add a new action for this unit
            if (feedback.getFeedback() != ActionFeedback.INCOMPLETE)
            {
                int unitID = feedback.getAction().getUnitId();
                
                // actions.put(unitID, Action.createCompoundAttack(unitID, ));
            }
        }*/
        return null;
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
