package vacuumagentproject;

import java.util.Random;
import java.util.Stack;


public class MCReflexModelAgent extends VacuumAgent{
	int height = VacuumAgentDriver.maxHeight;
    int width = VacuumAgentDriver.maxWidth;
    
	int[][] coordinates = new int[width+2][height+2];
    Stack<VacuumAction> movementLog = new Stack<VacuumAction>();	
	int x = VacuumAgentDriver.agentY, y = VacuumAgentDriver.agentX;
	
	boolean backup = false;
	boolean stop = false;
	
	MCReflexModelAgent(){
    }
	
   @Override
   public VacuumAction getAction(VacuumPercept percept){
       
       if (percept instanceof VacuumBumpPercept)
           return getActionRandomReflex((VacuumBumpPercept) percept);
       else {
           System.out.println("Error:  Expected a Bump Percept!!");
           return VacuumAction.STOP;
       }
   }
   
   private VacuumAction getActionRandomReflex(VacuumBumpPercept percept){
	   
	   if(percept.currentStatus == Status.DIRTY){
		   coordinates[x][y] = 2;
		   return VacuumAction.SUCK;
	   }
	   
	   //CHECK FOR OBSTACLES
	   actionList = VacuumAction.values();
	   if (percept.willBump(VacuumAction.BACK) && coordinates[x+1][y] != 1) 
		   coordinates[x+1][y] = 1;
		   
	   if (percept.willBump(VacuumAction.FORWARD) && coordinates[x-1][y] != 1) 
		   coordinates[x-1][y] = 1;
	   
	   if (percept.willBump(VacuumAction.LEFT)&& coordinates[x][y-1] != 1) 
		   coordinates[x][y-1] = 1;
	   
	   if (percept.willBump(VacuumAction.RIGHT) && coordinates[x][y+1] != 1) 
		   coordinates[x][y+1] = 1;
	     
	   //CHECK CLEANLINESS 
	   if (!percept.willBump(VacuumAction.BACK) && coordinates[x+1][y] != 2) {
		   actionList[0] = VacuumAction.BACK;
		   if (VacuumEnvironment.floor[x+1][y] == Status.DIRTY)
			   coordinates[x+1][y] = 3;
	   }
	   
	   if (!percept.willBump(VacuumAction.FORWARD) && coordinates[x-1][y]  != 2) {
		   actionList[1] = VacuumAction.FORWARD;
		   if (VacuumEnvironment.floor[x-1][y] == Status.DIRTY)
			   coordinates[x-1][y] = 3;
	   }
	   
	   if (!percept.willBump(VacuumAction.LEFT) && coordinates[x][y-1] != 2) {
		   actionList[2] = VacuumAction.LEFT;
		   if (VacuumEnvironment.floor[x][y-1] == Status.DIRTY)
			   coordinates[x][y-1] = 3;
	   }
	   
	   if (!percept.willBump(VacuumAction.RIGHT)) {
		   actionList[3] = VacuumAction.RIGHT;
		   if (VacuumEnvironment.floor[x][y+1] == Status.DIRTY && coordinates[x][y+1] != 2)
			   coordinates[x][y+1] = 3;
	   }
	   
	   //if all else fails generate random move
	   Random gen = new Random();
	   int index = gen.nextInt(4);
	   
	   //STORE THE MOVEMENTS TOWARDS DIRTY TILES
	   if(coordinates[x+1][y] == 3) 
		   actionList[index] = VacuumAction.BACK;	   
	   if(coordinates[x-1][y] == 3) 
			actionList[index] = VacuumAction.FORWARD;
	   if (coordinates[x][y+1] == 3)
			actionList[index] = VacuumAction.RIGHT;
	   if (coordinates[x][y-1] == 3) 
			actionList[index] = VacuumAction.LEFT;
	  
	   //NO DIRTY TILES, WHAT DO?
	   if ((coordinates[x+1][y] == 2 || coordinates[x+1][y] == 1) && coordinates[x+1][y] != 3
		&& (coordinates[x-1][y] == 2 || coordinates[x-1][y] == 1) && coordinates[x-1][y] != 3
		&& (coordinates[x][y+1] == 2 || coordinates[x][y+1] == 1) && coordinates[x][y+1] != 3
		&& (coordinates[x][y-1] == 2 || coordinates[x][y-1] == 1) && coordinates[x][y-1] != 3) {
		   	   
		   backup = true;
		   if (movementLog.isEmpty()) {
			   actionList[index] = VacuumAction.STOP;			   
		   } else
			   actionList[index] = movementLog.pop();
		   
		   if (actionList[index] == VacuumAction.BACK)
			   actionList[index] = VacuumAction.FORWARD;
		   
		   else if (actionList[index] == VacuumAction.FORWARD)
			   actionList[index] = VacuumAction.BACK;
		   
		   else if (actionList[index] == VacuumAction.RIGHT)
			   actionList[index] = VacuumAction.LEFT;
		   
		   else if (actionList[index] == VacuumAction.LEFT)
			   actionList[index] = VacuumAction.RIGHT;		   
	   } else 
		   backup = false;
	   
	   //ADJUST COORDINATES
	   if (actionList[index] == VacuumAction.BACK && !percept.willBump(VacuumAction.BACK)) 
		   x++;
	   else if (actionList[index] == VacuumAction.FORWARD && !percept.willBump(VacuumAction.FORWARD)) 
		   x--;
	   else if (actionList[index] == VacuumAction.RIGHT && !percept.willBump(VacuumAction.RIGHT)) 
		   y++;
	   else if (actionList[index] == VacuumAction.LEFT && !percept.willBump(VacuumAction.LEFT)) 
		   y--; 
	   
	   //PRINT BOARD
	   System.out.println("Movment " + actionList[index] + " x: " + x + " y: " + y);
	   for (int i = 0; i < width+2; i++ ) {
		   for (int j = 0; j < height+2; j++ ) {
			   System.out.print(coordinates[i][j]+ " ");
		   }
		   System.out.println();
	   }
	   System.out.println();
	   
	   //CHECK BACKUP
	   if(!backup)
		   movementLog.add(actionList[index]);
	   
	   return actionList[index];	   
   }
}
