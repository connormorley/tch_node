package controllers;

import java.util.ArrayList;

public class PasswordGenerator {
	public static char[] masterCharSet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ`¨!\"£$%^&*()-_=+[{]};:'@#~,<.>/?\\|≈…Ê ".toCharArray();
	public static boolean onlyOneColumn;
	//From the generated password list retrieve the associated Integer array and translate into characters.
		public static String getPassword(int position) {
			String ret = "";
			ArrayList<Integer> passwordSet = AttackController.testingSet.get(position);
			for (int i = 0; i < passwordSet.size(); i++) {
				ret = ret + masterCharSet[passwordSet.get(i)];
			}
			return ret;
		}
		
		//Using the passwordMasterCount as a controller, generate a series of Integer arrays in incrementing values from right to left with max 98 to be 
		//used as password keys.
		public static ArrayList<ArrayList<Integer>> generatePasswords() {
			System.out.println(masterCharSet.length);
			ArrayList<ArrayList<Integer>> ret = new ArrayList<ArrayList<Integer>>();

			int startPoint = AttackController.passwordMasterCounter * AttackController.balanceNumber; // Start value
			//startPoint = startPoint / 98;

			ArrayList<Integer> startArray = new ArrayList<Integer>();
			startArray = getArrayPoint(startPoint);

			int endPoint = (AttackController.passwordMasterCounter + 1) * AttackController.balanceNumber; // End value
			//endPoint = endPoint / 98;
			ArrayList<Integer> endArray = new ArrayList<Integer>();
			endArray = getArrayPoint(endPoint);

			ArrayList<Integer> test = new ArrayList<Integer>();

			System.out.println(endArray.size());
			
			int counter = startArray.size() - 1;

			while (!startArray.toString().equals(endArray.toString())) {

				while (startArray.get(counter) != masterCharSet.length && !startArray.toString().equals(endArray.toString())) {
					ArrayList<Integer> temp = new ArrayList<Integer>(startArray);
					Integer testNumber = new Integer(ret.size());
					ret.add(temp);
					ArrayList<ArrayList<Integer>> checkDebug = ret; // Adds the new integer sequence to the return array to be used for password sequence.
					startArray.set(counter, startArray.get(counter) + 1); // Loops on the chose column (set by counter to start at 0) and then increments up to the designated value in the column
				}
				
				if(startArray.toString().equals(endArray.toString()))
						break;

				startArray.set(counter, 0);

				if(counter == 0 && !onlyOneColumn)
				{
						//startArray.set(counter, startArray.get(counter) + 1);
						counter++;
						startArray.add(counter, 0);
				}
				else{
					for (int placement = counter - 1; placement != -1; placement--) {
						if (startArray.get(placement) == (masterCharSet.length - 1)) { // If the column has reached is maximum value
							startArray.set(placement, 0); // reset the columns value to 0
							if(placement == 0) // Once all the columns have been reset, add a new column
							{
								counter++;
								startArray.add(counter, 0);
							}
						} else { // If the column terminates of a value that was not the limit
							startArray.set(placement, startArray.get(placement) + 1); // Place one additional value
							break; // Then terminate to return array for password use.
						}
					}
				}
			}
			
			
			
			
			
			System.out.println("array size : " + ret.size());
			return ret;
		}
		
//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////

		//From the masterKey number generate a Integer array, these are used as start and end points for the current numerical value.
		private static ArrayList<Integer> getArrayPoint(int passwordsUsed) {
			ArrayList<Integer> columns = new ArrayList<Integer>();
			int limit = masterCharSet.length;
			int columnCount = 0;
			boolean endReached = false;
			columns.add(columnCount, 0); // Will always have at least one column
			columnCount++;
			
			if (passwordsUsed > (masterCharSet.length))
			{
			if (passwordsUsed > limit) {
				columns.add(columnCount, 0); // If above one run it has moved onto the next columns such as 1a
				columnCount++;
				passwordsUsed = passwordsUsed - limit;
				limit = limit * (masterCharSet.length);

				while (endReached == false) {

					if (passwordsUsed > limit) // While the number of password runs is above the limit which is a multiple of 98 keep running
					{
						columns.add(columnCount, 0); // If count is above the limit this means another column
						columnCount++;
						passwordsUsed = passwordsUsed - limit;
						limit = limit * (masterCharSet.length); // Each multiplication is a column to the left, then must be checked left to right dividing limit by 98
						System.out.println(limit);
					}

					else {
						int finalCounter = 0;
						limit = limit / (masterCharSet.length); // Divide limit by 98 from limit that caused entry to else statement
						while (limit != 1) {
							if (passwordsUsed > limit) // If the password count is greater than the new limit value then count into appropriate column
							{
								int tally = passwordsUsed / limit; // Find divisible value ignoring remainders, result is how many runs in that column
								passwordsUsed = passwordsUsed - (tally * limit);
								//if(columnCount < 3)
								columns.set(finalCounter, tally - 1); // First column (0) counted from limit first
								/*else
									columns.set(finalCounter, tally - 1);*/
								System.out.println(tally);
								finalCounter++; // Increment counter to next column
								if (limit == (masterCharSet.length)) {
									tally = passwordsUsed % limit;
									System.out.println(tally);
									columns.set(finalCounter, tally);
									break;
								}
								limit = limit / (masterCharSet.length);
							} else {
								limit = limit / (masterCharSet.length);
								columns.set(finalCounter, 0);
								finalCounter++;
							}
						}

						endReached = true; // Exit while loop
					}

					// Down here should be some sort of array relation to stored
					// tally values to find appropriate characters from store
				}
				System.out.println(columns);
			}
			}
			else{
				columns.set((columnCount - 1), (passwordsUsed));
			}
			return columns;
			
			
			
		}
}
