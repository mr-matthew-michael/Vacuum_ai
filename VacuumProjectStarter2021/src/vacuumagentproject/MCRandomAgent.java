package vacuumagentproject;

import java.util.Random;

public class MCRandomAgent extends VacuumAgent{
	
	MCRandomAgent(){
        //update as you see fit
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
       if (percept.currentStatus == Status.DIRTY)
          return VacuumAction.SUCK;
      else {//choose a move randomly
          Random gen = new Random();
          actionList = VacuumAction.values();
          int index = gen.nextInt(actionList.length);
          while (!actionList[index].isAMove() && !percept.willBump(actionList[index]))
              index = (index+1) % actionList.length;
          return actionList[index];
      }
   }
}
  