import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.history.History.HistoryView;
import edu.cwru.sepia.environment.model.state.ResourceNode.Type;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State.StateView;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit.UnitView;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceCollectorMod extends Agent
{
    public ResourceCollectorMod(int playernum)
    {
        super(playernum);
    }
    
    @Override
    public Map<Integer, Action> initialStep(StateView stateView, HistoryView historyView)
    {
        return middleStep(stateView, historyView);
    }
    
    @Override
    public Map<Integer, Action> middleStep(StateView stateView, HistoryView historyView)
    {
        // This stores the action that each unit will perform
        // if there are no changes to the current actions then this
        // map will be empty.
        Map<Integer, Action> actions = new HashMap<Integer, Action>();
        
        // this will return a list of all of your units
        // You will need to check each unit ID to determine the unit's type
        List<Integer> myUnitIds = stateView.getUnitIds(playernum);
        
        // These will store the Unit IDs that are peasants and townhalls respectively
        List<Integer> peasantIds = new ArrayList<Integer>();
        List<Integer> townhallIds = new ArrayList<Integer>();
        List<Integer> farmId = new ArrayList<Integer>();
        List<Integer> barracksId = new ArrayList<Integer>();
        List<Integer> footmanIds = new ArrayList<Integer>();
    
        // examine each of our unit IDs and classify them as either a Townhall or a Peasant
        for (Integer unitID : myUnitIds)
        {
            // UnitViews extract information about a specified unit id from the current state.
            UnitView unit = stateView.getUnit(unitID);
            
            // To find properties that all units of a given type share
            // access the UnitTemplateView using the `getTemplateView()`
            String unitTypeName = unit.getTemplateView().getName();
            
            if (unitTypeName.equals("TownHall"))
            {
                townhallIds.add(unitID);
            }
            else if (unitTypeName.equals("Peasant"))
            {
                peasantIds.add(unitID);
            }
            else if (unitTypeName.equals("Footman"))
            {
                footmanIds.add(unitID);
            }
            else if (unitTypeName.equals("Farm"))
            {
                farmId.add(unitID);
            }
            else if (unitTypeName.equals("Barracks"))
            {
                barracksId.add(unitID);
            }
            else
            {
                System.err.println("Unexpected Unit type: " + unitTypeName);
            }
        }
        
        // get the amount of wood and gold you have in your Town Hall
        int currentGold = stateView.getResourceAmount(playernum, ResourceType.GOLD);
        int currentWood = stateView.getResourceAmount(playernum, ResourceType.WOOD);
        
        List<Integer> goldMines = stateView.getResourceNodeIds(Type.GOLD_MINE);
        List<Integer> trees = stateView.getResourceNodeIds(Type.TREE);
        
        // Now that we know the unit types we can assign our peasants to collect resources
        for (Integer peasantID : peasantIds)
        {
            Action action = null;
            if (stateView.getUnit(peasantID).getCargoAmount() > 0)
            {
                // If the agent is carrying cargo then command it to deposit what its carrying at the townhall.
                action = new TargetedAction(peasantID, ActionType.COMPOUNDDEPOSIT, townhallIds.get(0));
            }
            else
            {
                // If the agent isn't carrying anything instruct it to go collect either gold or wood
                // whichever you have less of
                if (currentGold < currentWood)
                {
                    action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, goldMines.get(0));
                }
                else
                {
                    action = new TargetedAction(peasantID, ActionType.COMPOUNDGATHER, trees.get(0));
                }
            }
    
            // Put the actions in the action map.
            // Without this step your agent will do nothing.
            actions.put(peasantID, action);
        }
        
        // Build new units of a farm, barracks, and footmen
        if (currentGold >= 500 && currentWood >= 250 && farmId.size() != 1)
        {
            // Get the farms template's unique ID
            Template.TemplateView farmTemplate = stateView.getTemplate(playernum, "Farm");
            int farmTemplateID = farmTemplate.getID();
    
            // Get the id of the first peasant so they can build the farm
            int peasantID = peasantIds.get(0);
    
            // create a new CompoundBuild action for peasant 0 to build the farm
            actions.put(peasantID, Action.createCompoundBuild(peasantID, farmTemplateID, 10, 10));
        }
        else if (currentGold >= 700 && currentWood >= 400 && barracksId.size() != 1)
        {
            // Get the barracks template's unique ID
            Template.TemplateView barracksTemplate = stateView.getTemplate(playernum, "Barracks");
            int barracksTemplateID = barracksTemplate.getID();
    
            // Get the id of the first peasant so they can build the barracks
            int peasantID = peasantIds.get(0);
    
            // create a new CompoundBuild action for peasant 0 to build the barracks
            actions.put(peasantID, Action.createCompoundBuild(peasantID, barracksTemplateID, 5, 5));
        }
        else if (currentGold >= 600 && footmanIds.size() < 2 && barracksId.size() == 1)
        {
            // Get the footman template's unique ID
            Template.TemplateView footmanTemplate = stateView.getTemplate(playernum, "Footman");
            int footmanTemplateID = footmanTemplate.getID();
    
            // Get the id of the barracks
            int barrackID = barracksId.get(0);
    
            // create a new CompoundProduction action at the barracks. A footman will be built at the barracks
            actions.put(barrackID, Action.createCompoundProduction(barrackID, footmanTemplateID));
        }
        
        return actions;
    }
    
    @Override
    public void terminalStep(StateView stateView, History.HistoryView historyView)
    {
        System.out.println("execution finished");
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
