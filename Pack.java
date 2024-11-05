public class Pack implements Comparable<Pack> {
	private static int packageID = 0;
	private String companyName;
	private String deliveryZone;
	private Date deliveryDate = new Date();
	private int weight;
	private int volume;
	private int timeDeadline;
	private int assignedTruckId;
	private int deliveryHour;

	// Constants:
	// private static final int DEFAULT_ID = 1;
	private static final String DEFAULT_COMPANY = "Default Company";
	private static final String DEFAULT_DELIVERY_ZONE = "A1";
	private static final Date DEFAULT_DATE = new Date(); // Date Class default constructor;
	private static final int DEFAULT_WEIGHT = 1;
	private static final int DEFAULT_VOLUME = 1;
	//	private static final int DEFAULT_TIME_DEADLINE = 9;

	// Default Constructors:
	public Pack() {
		// setPackageID(packageID);
		packageID++;
		setCompany(DEFAULT_COMPANY);
		setZone(DEFAULT_DELIVERY_ZONE);
		setDeliveryDate(DEFAULT_DATE);
		setWeight(DEFAULT_WEIGHT);
		setVolume(DEFAULT_VOLUME);
	}

	// Non-default Constructors:
	public Pack(String comp, String zone, Date d, int w, int v) {
		packageID++;
		setCompany(comp);
		setZone(zone);
		setDeliveryDate(d);
		setWeight(w);
		setVolume(v);
	}

	public int getID() {
		return packageID;
	}

	public void setCompany(String c) { // use error checking for zone and
		if (c != null) {
			companyName = c;
		} else {
			companyName = DEFAULT_COMPANY;
		}
	}

	public void setZone(String z) {
		if (z != null) {
			deliveryZone = z;
		} else {
			deliveryZone = DEFAULT_DELIVERY_ZONE;
		}
	}

	public void setDeliveryDate(Date d) {
		// deep copy:
		Date copy = new Date(d);
		if (copy != null) {
			deliveryDate = copy;
		} else {
			deliveryDate = DEFAULT_DATE;
		}
	}

	public void setWeight(int w) {
		if (w <= 0) { // error checking:
			weight = DEFAULT_WEIGHT;
		} else {
			weight = w;
		}
	}

	public void setVolume(int v) {
		if (v <= 0) { // error checking:
			volume = DEFAULT_VOLUME;
		} else {
			volume = v;
		}
	}

	// getCompany:
	public String getCompany() {
		return companyName;
	}

	// getZone:
	public String getZone() {
		return deliveryZone;
	}

	// getDate:
	public Date getDeliveryDate() {
		Date copy = new Date(deliveryDate);
		// deliveryDate = cop
		return copy;
	}

	// getWeight:
	public int getWeight() {
		return weight;
	}

	// getVolume:
	public int getVolume() {
		return volume;
	}

	// toString method:
	public String toString() {
		return " Company:" + companyName + " Zone:" + deliveryZone + " Date:" + deliveryDate
				+ " Weight:" + weight + " Volume:" + volume;
	}

	// Write a compareTo() for deliveryDate:

	public int compareTo(Pack pack2) {
		// First compare by deliveryDate
		int dateComparison = this.getDeliveryDate().compareTo(pack2.getDeliveryDate());
		if (dateComparison != 0) {
			return dateComparison;
		}
		// If deliveryDate is the same, then compare by deliveryZone
		return this.getDeliveryZone().compareTo(pack2.getDeliveryZone());
	}

	public String getDeliveryZone() {
		return deliveryZone;
	}

	public void setDeliveryZone(String deliveryZone) {
		this.deliveryZone = deliveryZone;
	}

	public int getAssignedTruckId() {
		return assignedTruckId;
	}

	public void setAssignedTruckId(int assignedTruckId) {
		this.assignedTruckId = assignedTruckId;
	}

	public int getDeliveryHour() {
		return deliveryHour;
	}

	public void setDeliveryHour(int deliveryHour) {
		this.deliveryHour = deliveryHour;
	}

}
