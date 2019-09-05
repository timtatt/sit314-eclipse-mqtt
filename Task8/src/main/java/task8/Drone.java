

import java.util.List;
import java.util.Arrays;
import java.util.Date;

public class Drone {
	private static List<String> allowedTypes = Arrays.asList(new String[] {
		"long", 
		"short"
	});
	
	private String type; // "long" or "short"
	private String id;
	
	public LatLng pos = new LatLng(0, 0);
	public float speed = 0;
	public int batteryLevel = 0;
	public float altitude = 0;
	public Date altitudeTime = new Date();
	
	public Drone(String id, String type) throws Exception {
		if (!allowedTypes.contains(type)) {
			throw new Exception("Invalid drone type");
		}
		
		this.id = id;
		this.type = type;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean isBatteryLevelCritical() {
		return batteryLevel <= 10;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append("\n---------\n");
		sb.append("Type: " + type + "\n");
		sb.append("Pos: " + pos.lat + ", " + pos.lng + "\n");
		sb.append("Speed: " + speed + "m/s\n");
		sb.append("Battery Level: " + batteryLevel + "%\n");
		sb.append("Altitude: " + altitude + "m\n");
		return sb.toString();
	}
}
