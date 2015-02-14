package GameEngine;
import java.util.*;
import org.mockito.ArgumentMatcher;

public class isGivenHashSet extends ArgumentMatcher<HashSet> {
	     
		 HashSet territories;
		 
		 public isGivenHashSet(HashSet territories){
			 this.territories = territories;
		 }
		 
		 public boolean matches(Object set) {
			// System.out.println(set);
			// System.out.println(territories + "\n");
			 
			 if(set == null)
				 return false;
			 
	         if(((Set) set).size() != territories.size())
	        	 return false;
	        
	         Iterator i = ((Set) set).iterator();
	         while(i.hasNext()){
	        	 if(!territories.contains(i.next()))
	        		 return false;
	         }
	         
	         return true;
	         
	     }
}

	
