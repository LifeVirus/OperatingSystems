import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.Set;

//Anthony Cheang
//Operating Systems Fall 2018
//Prof. Yan Shvartzshnaider
//Lab 1 Linker

public class Linker 
{

	public static void main(String[] args) throws FileNotFoundException 
	{
		File inputFile = new File(args[0]); //pull file from arguments
		if (inputFile.exists() == false) 
		{
			System.out.println("The file does not exist");
			System.exit(0);
		}
		Scanner reader = new Scanner(inputFile);
		
		String memMap = "Memory Map \n"; //memory map string
		String doubleString = " Error: This variable is multiply defined; last value used."; //print when doubly used
		String warningString = ""; //stores warnings at the bottom
		Hashtable<String, Integer> variables = new Hashtable<String, Integer>(); //stores variable and value
		Hashtable<String, Integer> moduleDefined = new Hashtable<String, Integer>(); // stores variable and what module it was defined in
		Hashtable<String, Boolean> neverUsed = new Hashtable<String, Boolean>(); //Stores variable and T/F whether it was never used
		ArrayList<String> exceedModule = new ArrayList<String>(); // Stores variable names of variables that exceed module size
		ArrayList<String> doubleDefined = new ArrayList<String>(); // stores variable names of variables that are defiend twice
		
		int machineSize = 300; //machine size
		
		int numberOfModules = Integer.parseInt(reader.next()); //number of modules
		int indexTrack = 0;										//tracks offset index
		
		for(int i = 0; i<numberOfModules; i++) 				//first pass number of modules
			{
			int numberOfVariables = Integer.parseInt(reader.next());
			// Adds variables and defines them
			for(int j = 0; j<numberOfVariables; j++) 
				{
				String variable = reader.next();
				int indexOfVariable = reader.nextInt();
				int valueOfVariable = indexOfVariable + indexTrack;
				if(variables.containsKey(variable) == false) // if variable has already been defined
					{
					variables.put(variable,valueOfVariable); //put it in hashtable
					moduleDefined.put(variable, i);  //take note of which module
					neverUsed.put(variable, true); //set neverused to true
					}
				else {
					variables.put(variable,valueOfVariable); //
					moduleDefined.put(variable, i);
					doubleDefined.add(variable); //adds it to double defined list, and never used doesn't have to be set since it already is
				}
				}
			
		//skips use list because we don't need that for first pass
		int numberOfVariableUses = reader.nextInt();
		for(int j = 0; j<numberOfVariableUses; j++ ) 
			{
			reader.next();
			int dummyInt = 0;
			dummyInt = reader.nextInt();
			while(dummyInt != -1) 
				{
				dummyInt = reader.nextInt();
				}
			}
		//skips address list, we don't need it for first run
		int numberOfArgs = reader.nextInt();
		Set <String> keys = variables.keySet(); //checks variables to see if any of them exceeded machine size, and if so, resets it correctly
		for (String key : keys) {
			if(variables.get(key) - indexTrack>= numberOfArgs) {
				variables.put(key, numberOfArgs-1+indexTrack);
				exceedModule.add(key); //after reset, add it to the exceed module so error message can be printed
			}
		}
		indexTrack += numberOfArgs; //increase index number since module is done
		for(int k = 0; k<numberOfArgs; k++) 
			{
			int dummyInt =0;
			dummyInt =reader.nextInt();
			}
		
		}
		///////////////First pass Completed/////////////////////////
		reader.close();
		inputFile = new File(args[0]);
		reader = new Scanner(inputFile);
		indexTrack = 0; //reopen file and reset index tracker
		
		//Print variable tables
		System.out.println("Symbol Table"); //print symbol table, check for double defined and exceed module size errors
		Enumeration enu = variables.keys();
		while (enu.hasMoreElements()) 
			{
			Object key = enu.nextElement();
			System.out.print(key);
			System.out.print("=");
			System.out.print(variables.get(key));
			if(exceedModule.contains(key)) {
				System.out.print(" Definition exceeds module size); last word in module used.");
			}
			if(doubleDefined.contains(key)) {
				System.out.print(doubleString);
			}
			System.out.print("\n");
			}
		System.out.print("\n");
		////////////////Second Pass//////////////////////////////
		numberOfModules = reader.nextInt(); //same thing number of modules
		for (int s =0; s<numberOfModules; s++ ) 
			{
			Hashtable<Integer, String> variableUses = new Hashtable<Integer, String>(); //tells where variables were used 
			Hashtable<Integer, String> fakeNames = new Hashtable<Integer, String>(); //makes note of variables that use 111 because not defined
			Hashtable<Integer, String> multipleUse = new Hashtable<Integer, String>(); //stores variables used multiple times
			//skip variables
			int numberOfVariables = reader.nextInt();
			for(int n = 0; n<numberOfVariables; n++) 
			{
			String dummy = "";
			int intDummy = 0;
			dummy = reader.next();
			intDummy = reader.nextInt();
			}
		//store where variables are used
			int numberOfVariableUses = reader.nextInt();
			for (int l = 0; l < numberOfVariableUses; l++) 
			{
				String variableName = reader.next();
				int variableUse = reader.nextInt();
				while (variableUse != -1) //iterates variable until -1 is hit
				{
					if (variables.containsKey(variableName)) { //if variable is in variables (it should be)
						if(variableUses.containsKey(variableUse)) { //if it is also in the variableUses, which tracks where its supposed to be used, means its been seen before
							multipleUse.put(variableUse, variableName); //its a dupe
						}
						variableUses.put(variableUse, variableName); //if its not in the variableUses, put it in there
					}else {
						variableUses.put(variableUse, "fake"); //otherwise, it is not in variables, which means not defined, use 111
						fakeNames.put(variableUse, variableName);//put it in fake names so we know its fake
					}
					variableUse = reader.nextInt();//iterate
				}
			}
			int numberOfCalls = reader.nextInt(); //number of memory addresses
			for (int m = 0; m < numberOfCalls; m++) 
			{
				int call = reader.nextInt();
				int callType = call % 10; //gives us last digit
				if (callType == 1) 
				{
					//Immediate, just do it
					memMap += Integer.toString(m+indexTrack);
					if (m+ indexTrack >= 10) {
						memMap += ": ";
					}
					else {
						memMap += ":  ";
					}
					memMap += Integer.toString((call-1)/10);
					memMap += "\n";
				}
				else if(callType == 2) 
				{
					//absolute check if its larger than machine size. if so, use 299
					memMap += Integer.toString(m+indexTrack);
					if (m+ indexTrack >= 10) {
						memMap += ": ";
					}
					else {
						memMap += ":  ";
					}
					if(((call-2)/10)% 1000 >= machineSize) {
						memMap+= Integer.toString(((call-2)/10)- (call-2)/10 % 1000 +machineSize -1);
						memMap+= " Error: Absolute address exceeds machine size; largest legal value used.";
						memMap+= "\n";
					}
					else {
						memMap += Integer.toString((call-2)/10);
						memMap += "\n";
					}
					
				}
				else if(callType == 3) 
				{
					//Relative just add the indextrack to it
					memMap += Integer.toString(m+indexTrack);
					if (m+ indexTrack >= 10) {
						memMap += ": ";
					}
					else {
						memMap += ":  ";
					}
					memMap += Integer.toString(((call-3)/10)+indexTrack);
					memMap += "\n";
				}
				else 
				{
					//External check if it is multiple use, or fake. If not, then just set memory to the variable
					memMap += Integer.toString(m+indexTrack);
					if (m+ indexTrack >= 10) {
						memMap += ": ";
					}
					else {
						memMap += ":  ";
					}
					if(multipleUse.containsKey(m)) {
						memMap += Integer.toString(((call)/10000)*1000+variables.get(variableUses.get(m)));
						memMap += " Error: Multiple variables used in instruction; all but last ignored.";
						memMap += "\n";
						neverUsed.put(variableUses.get(m), false);
						multipleUse.remove(m);
					}
					else if(variableUses.get(m)== "fake") {
						memMap+= Integer.toString((call/10000)*1000 + 111);
						memMap+= " Error: ";
						memMap+= fakeNames.get(m);
						memMap+= " is not defined; 111 used.";
						memMap+= "\n";
						
					}else {
						memMap += Integer.toString(((call)/10000)*1000+variables.get(variableUses.get(m)));
						memMap += "\n";
						neverUsed.put(variableUses.get(m), false);
					}
					
				}
			}
		indexTrack += numberOfCalls; //add to index
		}
		//never used keys. Iterate through hash and module and print errors
		enu = neverUsed.keys();
		while (enu.hasMoreElements()) {
			Object key = enu.nextElement();
			if (neverUsed.get(key) == true) {
			warningString += "Warning: ";
			warningString += key;
			warningString += " was defined in module ";
			warningString += moduleDefined.get(key);
			warningString += " but never used.";
			warningString += "\n";
			}
		}
		//print out memory map, and print out warnings if they exist.
		System.out.println(memMap);
		System.out.println(warningString);
		reader.close(); 
	}
}

