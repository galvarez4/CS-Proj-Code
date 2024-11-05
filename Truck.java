import java.util.*;

public class Truck  {
	private String truckType;
	private int truckWeight, truckVolume;
	private int truckID;
	private String deliveryDate;

	private final int WORKING_HOUR_START_TIME = 9;
	private final int WORKING_HOUR_END_TIME = 17;
	private String startingZone;
	private String lastDeliveredZone;
	private String currentZone;
	private int lastDeliveredTime;
	private int currentPossibleDeliveryStops = 5;

	List<Pack> allPackagesList =  new ArrayList<>();

	// Default Constructor
	public Truck() {
		//truckID++;
	}

	// Non-Default Constructor
	public Truck(String startingZone, int volume, int weight,String deliveryDate) {
		truckID++; //UUID is not necessary (for the random ID Generator)
		this.startingZone = startingZone;
		this.lastDeliveredZone = startingZone;
		this.currentZone = startingZone;
		this.truckVolume = volume;
		this.truckWeight = weight;
		this.deliveryDate = deliveryDate;
	}

	//If the truck is in the same zone:
	public boolean isTruckTravelingToNewZone(String zone) {
		if(allPackagesList.size()>0) {
			for (Pack pack : allPackagesList) {
				if(pack.getDeliveryZone().equalsIgnoreCase(zone)) {
					return false;
				}
			}
			return true;
		}else {
			return false;
		}
	}

	//Check whether the truck object has sufficient weight and volume (capacities):
	public boolean isTruckHaveEoughVolumeAndWeight(int packWeight, int packVolume) {
		return packVolume <= this.truckVolume && packWeight <= this.truckWeight;
	}

	//Check whether the delivery hour is within the "working zone" and the package's "delivery time":
	public boolean validateDeliveryHour(int deliveryHour, int packageDeliveryHour) {
		return deliveryHour <= packageDeliveryHour && deliveryHour <= WORKING_HOUR_END_TIME;
	}

	public int getAssignedCompanySizeForZoneAndHour(String zone,int deliveryHour) {
		int count = 0;
		for (Pack pack : allPackagesList) {
			if(pack.getDeliveryZone().equalsIgnoreCase(zone) && pack.getDeliveryHour() == deliveryHour) {
				count++;
			}
		}
		return count;
	}

	private boolean isTruckAlreadyHaveSameCompanyStop(String zone, String companyName, int deliveryHour) {
		for (Pack pack : allPackagesList) {
			if(pack.getDeliveryZone().equalsIgnoreCase(zone) && pack.getDeliveryHour() == deliveryHour && pack.getCompany().equalsIgnoreCase(companyName)) {
				return true;
			}
		}
		return false;
	}

	//Check if the truck object has any "leftover" volume, weight, and time:
	public int checkAvailabilty(String zone, int packageDeliveryHour, String companyName, String deliveryType, int v, int w, Truck truck) {
		int startDeliveryHour = WORKING_HOUR_START_TIME;
		if (deliveryType.equalsIgnoreCase("S")) {
			startDeliveryHour = lastDeliveredTime;
		}
		startDeliveryHour = startDeliveryHour >= 9 ? startDeliveryHour : 9;

		if (isTruckTravelingToNewZone(zone)) {
			startDeliveryHour++;
		}
		// regular package:
		startDeliveryHour = this.getNextDeliveryHourByDeliveryType((startDeliveryHour - 1), deliveryType, zone);
		
		while (validateDeliveryHour(startDeliveryHour, packageDeliveryHour) && isTruckHaveEnoughVolumeAndWeight(w, v)) {
			// get list of packages assigned on current package deliverytime
			int assignedCompanySize = getAssignedCompanySizeForZoneAndHour(zone,startDeliveryHour);

			// calculate how many zones to travel to reach the current package zone from
			// previouly delivered zone
			int zoneTravelCount = this.getCountOfZonesToTravel(zone);

			//System.out.println("assignedCompanySize=>" + assignedCompanySize);
			//System.out.println("zoneTravelCount=>" + zoneTravelCount);
			//System.out.println("currentPossibleDeliveryStops=>" + currentPossibleDeliveryStops);

			// reduce 1 stop from every zone travel, here we reduced the total number of
			// zones it is traveled from previous delivery zone and previous possible
			// delivery stops
			int allowedDeliveryStops = currentPossibleDeliveryStops - (zoneTravelCount);
			//System.out.println(zone + "==>allowedDeliveryStops=>" + allowedDeliveryStops);

			// checking reached maximum stops
			if (assignedCompanySize < allowedDeliveryStops && allowedDeliveryStops > 0) {

				// if there are no packages assigned for this particular time, then it is
				// eligible and can be selected.
				if (assignedCompanySize==0) {
					if(startDeliveryHour>lastDeliveredTime) {
						lastDeliveredTime = startDeliveryHour;
						lastDeliveredZone = zone;
						currentPossibleDeliveryStops = allowedDeliveryStops;
					}
					return startDeliveryHour;
				}

				//verify if the truck already has some packages to deliver at the company's stop. 
				//if it exists, then there is no need to reduce the possible delivery stops.

				boolean isTruckAlreadyHaveSameCompanyStopFlag = isTruckAlreadyHaveSameCompanyStop(zone,companyName,startDeliveryHour);
				if (isTruckAlreadyHaveSameCompanyStopFlag) {
					return startDeliveryHour;
				}else {
					if(startDeliveryHour>lastDeliveredTime) {
						lastDeliveredTime = startDeliveryHour;
						lastDeliveredZone = zone;
						currentPossibleDeliveryStops = allowedDeliveryStops;
					}
					return startDeliveryHour;
				}
			}else {

				// get next delivery hour to check that have possiblity to deliver package
				startDeliveryHour = this.getNextDeliveryHourByDeliveryType(startDeliveryHour, deliveryType, zone);
			}
		}
		return -1;
	}

	private int getNextDeliveryHourByDeliveryType(int startDeliveryHour, String deliveryType, String zone) {

		if (deliveryType.equalsIgnoreCase("R")) {
			while (true) {
				startDeliveryHour++;
				//System.out.println("getNextDeliveryHourByDeliveryType startDeliveryHour=>"+startDeliveryHour+" deliveryType=>"+deliveryType+" lastDeliveredTime=>"+lastDeliveredTime);
				if (startDeliveryHour > lastDeliveredTime || (getAssignedCompanySizeForZoneAndHour(zone,startDeliveryHour) > 0)) {
					break;
				}
			}
		} else {
			startDeliveryHour++;
		}
		return startDeliveryHour;
	}

	// get  the number of zones that the truck needs to travel to.
	private int getCountOfZonesToTravel(String zone) {
		int zoneNumber = Integer.valueOf(zone.charAt(1));
		char zoneChar = zone.charAt(0);
		int startZoneNumber = Integer.valueOf(lastDeliveredZone.charAt(1));
		char startZoneChar = lastDeliveredZone.charAt(0);
		int resultZoneNumberDifference = Math.abs(zoneNumber - startZoneNumber);
		int resultZoneCharDifference = Math.abs(zoneChar - startZoneChar);
		return resultZoneNumberDifference + resultZoneCharDifference;
	}

	public int getCountOfPackDeliverToZone(String zone) {
		int count = 0;
		for (Pack pack : allPackagesList) {
			if(pack.getDeliveryZone().equalsIgnoreCase(zone)) {
				count++;
			}
		}
		return count;
	}
	//assign the package to truck for selected delivery hour
	public void addCompanyPackage(SpecPack pack, int deliveryHours) {
		pack.setAssignedTruckId(this.truckID);
		String zone = pack.getDeliveryZone();
		//System.out.println(zone + "==>" + deliveryHours);
		//this.specialPackList.add(pack);
		this.allPackagesList.add(pack);
	}

	public void setTruckID(int id) {
		if(id > 0) {
			truckID = id;
		}
		else {
			//throw exception;
		}
	}

	public void addCompanyPackage(Pack pack, int deliveryHour) {
		pack.setAssignedTruckId(this.truckID);
		String zone = pack.getDeliveryZone();
		//System.out.println(zone + "==>" + deliveryHour);
		//this.regularPackList.add(pack);
		this.allPackagesList.add(pack);
	}

	public boolean isTruckHaveEnoughVolumeAndWeight(int packageWeight, int packageVolume) {
		return packageVolume <= this.truckVolume && packageWeight <= this.truckWeight;
	}

	public String getTruckType() {
		return truckType;
	}

	public void setTruckType(String truckType) {
		this.truckType = truckType;
	}

	public int getTruckWeight() {
		return truckWeight;
	}

	public void setTruckWeight(int truckWeight) {
		this.truckWeight = truckWeight;
	}

	public int getTruckVolume() {
		return truckVolume;
	}

	public void setTruckVolume(int truckVolume) {
		this.truckVolume = truckVolume;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

}
