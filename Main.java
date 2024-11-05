import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner; 

public class Main {	
	public static List<Truck> trucks = new ArrayList<Truck>(); 
	public static int totalTruckHours = 0;
	public static void main(String[] args) throws IOException, InvalidWeightException, InvalidVolumeException, InvalidDateException, InvalidDeliveryHourException {

		//Create Scanner Object:
		Scanner in = new Scanner(System.in);
		System.out.print("Please input the number of packages: ");
		int numPack = in.nextInt();

		//Read in text file:
		File txtFile = new File("ProjData.txt");
		Scanner read = new Scanner(txtFile);

		//Keep track of types of package
		int regularCount = 0;
		int specialCount = 0;

		// Create a new array for a length of "count":
		for (int i = 0; i < numPack; i++) {
			String[] params = read.nextLine().split(",");
			String type = params[0];
			if (type.equals("R")) {
				regularCount++;
			} else if (type.equals("S")) {
				specialCount++;
			}
		}
		//Create two separate arrays for both the regular and special packages:
		Pack [] allRegularPackages = new Pack[regularCount];
		SpecPack [] allSpecialPackages = new SpecPack[specialCount];

		read = new Scanner(txtFile);
		int regularIndex = 0;
		int specialIndex = 0;
		for (int i = 0; i < numPack; i++) {

			// Read and Parse the file:
			String[] params = read.nextLine().split(","); // Stores into an array
			String[] date = params[3].split("/"); // Stores into an array
			//Date variable is created:
			int month = Integer.parseInt(date[0]);
			int day = Integer.parseInt(date[1]);
			int year = Integer.parseInt(date[2]);
			Date d = new Date(month, day, year);

			// Parse params for appropriate parameter values for the objects:
			String type = params[0];
			String comp = params[1];
			String zone = params[2];

			//Date deadline = new Date(Integer.parseInt(date[0]),Integer.parseInt(date[1]),Integer.parseInt(date[2]));
			int weight = Integer.parseInt(params[4]);
			int volume = Integer.parseInt(params[5]);

			// Differentiate between "R" and "S":
			if (type.equals("R")) {
				// Create object and store it in the array:
				allRegularPackages[regularIndex++] = new Pack(comp, zone, d, weight, volume);
				// Error checking- System.out.println("hello");
			} else if (type.equals("S")) {
				if(params.length < 7) { //make sure the appropriate variables are present, or else throw exception
					throw new InvalidDeliveryHourException("The delivery hour needed");
				}
				int deadline = Integer.parseInt(params[6]);
				// Create object and store it in the array:
				allSpecialPackages[specialIndex++] = new SpecPack(comp, zone, d, weight, volume, deadline);
			}
		}
		// Stop Reading File:
		read.close();
		// Sort The Packages
		// 1.Sort by DeliveryDate and Zone
		// 2.Sort by delivery hour
		Arrays.sort(allSpecialPackages);

		// Sort regular Packages
		// 1.Sort by DeliveryDate and Zone
		Arrays.sort(allRegularPackages);

		for (int i = 0; i < specialCount; i++) {
			System.out.println("Delivery Date: " + allSpecialPackages[i].getDeliveryDate() + ", Delivery Zone: " + allSpecialPackages[i].getDeliveryZone() + ", Company Name: " + allSpecialPackages[i].getCompany()+" , Delivery hour: "+allSpecialPackages[i].getHour());
			//Add the package to the truck:
			addSpecialPackageToTruck(allSpecialPackages[i],allSpecialPackages);
		}

		for (int i = 0; i < regularCount; i++) {
			System.out.println("Delivery Date: " + allRegularPackages[i].getDeliveryDate() + ", Delivery Zone: " + allRegularPackages[i].getDeliveryZone() + ", Company Name: " + allRegularPackages[i].getCompany());
			//Add the package to the Truck:
			addRegularPackageToTruck(allRegularPackages[i],allRegularPackages);
		}

		int totalVolumeRegular = 0;
		int totalVolumeSpecial = 0;

		int totalWeightRegular = 0;
		int totalWeightSpecial = 0;

		// 3.Add the sorted packages total volume
		for (int i = 0; i < allSpecialPackages.length; i++) {
			Pack p = allSpecialPackages[i];
			if (p instanceof SpecPack) {
				totalVolumeSpecial += p.getVolume();
				totalWeightSpecial += p.getWeight();

			} else {
				totalVolumeRegular += p.getVolume();
				totalWeightRegular += p.getWeight();
			}
		}

		// 4. Assign the respective amount of packages in the zone, to the appropriate truck size
		// 5.Increment the respective truck hours for the assigned type of truck
		for(Truck truck : trucks) {
			List<Pack> allPackagesList = truck.allPackagesList;
			int traveledHours = getTruckDeliveryHours(allPackagesList);
			int muliplicationFactor = 1;
			if(truck.getTruckType().equalsIgnoreCase("medium")) {
				muliplicationFactor = 2;
			}else if(truck.getTruckType().equalsIgnoreCase("large")) {
				muliplicationFactor = 3;
			}
			System.out.println();
			//System.out.println();
			totalTruckHours += (muliplicationFactor*traveledHours);
		}
		System.out.print("total traveled =>"+totalTruckHours);
	}

	//A method to return the Truck's delivery hours:
	private static int getTruckDeliveryHours(List<Pack> allPackagesList) {
		int hours = 1;
		int lastDeliveryHour = allPackagesList.get(0).getDeliveryHour();
		for(Pack pack: allPackagesList) {
			if(lastDeliveryHour != pack.getDeliveryHour()) {
				hours++;
				lastDeliveryHour = pack.getDeliveryHour();
			}
		}
		return hours;
	}

	//Method to add the Special Package to a Truck:
	private static void addSpecialPackageToTruck(SpecPack specPack,SpecPack [] allSpecialPackages) {
		String zone = specPack.getZone();
		int truckTotalVolume = getZoneTotalVolume(specPack.getDeliveryDate(),specPack.getDeliveryZone(),allSpecialPackages);
		int truckTotalWeight = getZoneTotalWeight(specPack.getDeliveryDate(),specPack.getDeliveryZone(),allSpecialPackages);
		String truckType = "";
		if(truckTotalVolume <= 1000 && truckTotalWeight<=2000) {
			truckTotalVolume = 1000;
			truckTotalWeight = 2000;
			truckType = "small";
		}else if(truckTotalVolume <= 2000 && truckTotalWeight<=4000) {
			truckTotalVolume = 2000;
			truckTotalWeight = 4000;
			truckType = "medium";
		}else{
			truckTotalVolume = 4000;
			truckTotalWeight = 8000;
			truckType = "large";
		}
		System.out.println("truck size=>"+trucks.size());
		if(trucks.size() > 0) {
			for (Truck truck : trucks) {
				System.out.println("truck sp date =>"+truck.getDeliveryDate());
				System.out.println("truck sp pk date =>"+specPack.getDeliveryDate().toString());
				if(truck.getDeliveryDate().equalsIgnoreCase(specPack.getDeliveryDate().toString())) {
					addPackageToSelectedTruck(truck,specPack);
					if(specPack.getAssignedTruckId() > 0) {
						break;
					}
				}
			}
		}
		System.out.println("specPack.getAssignedTruckId()=>"+specPack.getAssignedTruckId());
		if(specPack.getAssignedTruckId()==0) {
			Truck newTruck = new Truck(zone,truckTotalVolume,truckTotalWeight,specPack.getDeliveryDate().toString());
			newTruck.setTruckType(truckType);
			addPackageToSelectedTruck(newTruck,specPack);
			trucks.add(newTruck);
		}
	}

	//A Method that adds a Regular package to a Truck object:
	private static void addRegularPackageToTruck(Pack regularPack,Pack [] allRegularPackages) {
		String zone = regularPack.getZone();
		int truckTotalVolume = 4000;
		int truckTotalWeight = 4000;
		String truckType = "";
		if(truckTotalVolume <= 1000 && truckTotalWeight<=2000) {
			truckTotalVolume = 1000;
			truckTotalWeight = 2000;
			truckType = "small";
		}else if(truckTotalVolume <= 2000 && truckTotalWeight<=4000) {
			truckTotalVolume = 2000;
			truckTotalWeight = 4000;
			truckType = "medium";
		}else{
			truckTotalVolume = 4000;
			truckTotalWeight = 8000;
			truckType = "large";
		}
		System.out.println("truck size=>"+trucks.size());
		if(trucks.size() > 0) {
			for (Truck truck : trucks) {
				System.out.println("truck date =>"+truck.getDeliveryDate());
				System.out.println("truck pk date =>"+regularPack.getDeliveryDate().toString());
				if(truck.getDeliveryDate().equalsIgnoreCase(regularPack.getDeliveryDate().toString())) {
					addRegularPackageToSelectedTruck(truck,regularPack);
					if(regularPack.getAssignedTruckId() > 0) {
						break;
					}
				}
			}
		}
		System.out.println("specPack.getAssignedTruckId()=>"+regularPack.getAssignedTruckId());
		if(regularPack.getAssignedTruckId()==0) {
			Truck newTruck = new Truck(zone,truckTotalVolume,truckTotalWeight,regularPack.getDeliveryDate().toString());
			newTruck.setTruckType(truckType);
			addRegularPackageToSelectedTruck(newTruck,regularPack);
			trucks.add(newTruck);
		}
	}

	//A method that adds a Package to a Particular Truck
	private static void addPackageToSelectedTruck(Truck truck, SpecPack pack) {
		String zone = pack.getDeliveryZone();
		String companyName  = pack.getCompany();
		int packageVolume = pack.getVolume();
		int packageWeight = pack.getWeight();
		int deliveryHour = pack.getHour();
		int availableHour = truck.checkAvailabilty(zone,deliveryHour,companyName,"S",packageVolume,packageWeight,truck);
		System.out.println("availableHour==>"+availableHour);
		pack.setDeliveryHour(availableHour);
		if(availableHour >= 9) {
			truck.addCompanyPackage(pack,availableHour);
			truck.setTruckVolume(truck.getTruckVolume()- packageVolume);
			truck.setTruckWeight(truck.getTruckWeight()- packageWeight);
		}
	}
	//A method that adds a Regular Package to a Particular Truck
	private static void addRegularPackageToSelectedTruck(Truck truck, Pack pack) {
		String zone = pack.getDeliveryZone();
		String companyName  = pack.getCompany();
		int packageVolume = pack.getVolume();
		int packageWeight = pack.getWeight();
		int deliveryHour = 17;
		int availableHour = truck.checkAvailabilty(zone,deliveryHour,companyName,"R",packageVolume,packageWeight,truck);
		System.out.println("availableHour==>"+availableHour);
		pack.setDeliveryHour(availableHour);
		if(availableHour >= 9) {
			truck.addCompanyPackage(pack,availableHour);
			truck.setTruckVolume(truck.getTruckVolume()- packageVolume);
			truck.setTruckWeight(truck.getTruckWeight()- packageWeight);
		}
	}
	//Returns the total weight of the required packages to be delivered to a Zone
	private static int getZoneTotalWeight(Date deliveryDate, String deliveryZone,SpecPack [] allSpecialPackage) {
		int totalWeight = 0;
		boolean zoneFound = false;
		for(SpecPack specPack : allSpecialPackage) {
			if(deliveryZone.equalsIgnoreCase(specPack.getDeliveryZone()) && specPack.getDeliveryDate().toString().equalsIgnoreCase(deliveryDate.toString())) {
				zoneFound = true;
				totalWeight += specPack.getWeight();
			}else {
				if(zoneFound)
				{
					break;
				}
			}
		}
		return totalWeight;
	}

	//Returns the total volume of the required packages to be delivered to a Zone
	private static int getZoneTotalVolume(Date deliveryDate, String deliveryZone,SpecPack [] allSpecialPackage) {
		int totalVolume = 0;
		boolean zoneFound = false;
		for(SpecPack specPack : allSpecialPackage) {
			if(deliveryZone.equalsIgnoreCase(specPack.getDeliveryZone()) && specPack.getDeliveryDate().toString().equalsIgnoreCase(deliveryDate.toString())) {
				zoneFound = true;
				totalVolume += specPack.getVolume();
			}else {
				if(zoneFound){
					break;
				}
			}
		}
		return totalVolume;
	}
}

