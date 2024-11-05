
public class SpecPack extends Pack {
	// Attribute for hour
	private int hour;
	private static final int DEFAULT_HOUR = 9;

	// Default Constructors:
	public SpecPack() {
		// Call parent constructor:
		super();
		setHour(DEFAULT_HOUR); // Call setter methods:
	}

	// Non-Default Constructors:
	public SpecPack(String comp, String zone, Date d, int w, int v, int h) {
		super(comp, zone, d, w, v); // Call parent constructor:
		setHour(h);
	}

	// getHour:
	public int getHour() {
		return hour;
	}

	// setHour:
	public void setHour(int h) {
		if (h > 9 && h < 16) { // error checking
			this.hour = h;
		} else {
			this.hour = DEFAULT_HOUR;
		}
	}

	// toString method:
	public String toString() { // Call parent toString + hour;
		return super.toString() + " Time:" + hour;
	}

	public int compareTo(SpecPack pack2) {
		int dateComparison = this.getDeliveryDate().compareTo(pack2.getDeliveryDate());
        if (dateComparison != 0) {
            return dateComparison;
        }
        // If deliveryDate is the same, then compare by deliveryZone
        int zoneCompare =  this.getDeliveryZone().compareTo(pack2.getDeliveryZone());
        if(zoneCompare != 0) {
        	return zoneCompare;
        }
        if (this.getHour() == pack2.getHour())
			return 0;
		else if (this.getHour() > pack2.getHour()) {
			return 1;
		} else {
			return -1;
		}
	}
	
}