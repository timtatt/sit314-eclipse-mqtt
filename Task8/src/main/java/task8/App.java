import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class App implements MqttCallback {
	String topicBase = "SIT314Deakin";
	Hashtable<String, Drone> drones = new Hashtable<String, Drone>();
	Topic[] topics = new Topic[] {
			new Topic("/+/+/altitude", "Short Dist Drone Altitudes") {
				@Override
				public void messageHandler(String topic, MqttMessage message) {
					TopicDetails details = this.disectDroneTopic(topic);
					checkDrone(details);
					
					try {
						Drone drone = drones.get(details.droneId);
						Float newAltitude = Float.parseFloat(message.toString());
						if (drone.altitude != newAltitude) {
							drone.altitudeTime = new Date();
						}
						drone.altitude = newAltitude;
					} catch (Exception e) {}
				}
			},
			new Topic("/+/+/speed", "Short Dist Drone Speeds") {
				@Override
				public void messageHandler(String topic, MqttMessage message) {
					TopicDetails details = this.disectDroneTopic(topic);
					checkDrone(details);
					
					try {
						Drone drone = drones.get(details.droneId);
						drone.speed = Float.parseFloat(message.toString());
					} catch (Exception e) {}
				}
			},
			new Topic("/+/+/battery", "All Drone Battery Levels") {
				@Override
				public void messageHandler(String topic, MqttMessage message) {
					TopicDetails details = this.disectDroneTopic(topic);
					checkDrone(details);
					
					try {
						Drone drone = drones.get(details.droneId);
						drone.batteryLevel = Integer.parseInt(message.toString());
					} catch (Exception e) {}
				}
			},
			new Topic("/+/+/pos", "Long Dist Drone Positions") {
				@Override
				public void messageHandler(String topic, MqttMessage message) {
					TopicDetails details = this.disectDroneTopic(topic);
					checkDrone(details);
					
					try {
						Drone drone = drones.get(details.droneId);
						
						JSONObject json = (JSONObject) new JSONParser().parse(message.toString());
						
						drone.pos.lat = (float) Float.parseFloat(json.get("lat").toString());
						drone.pos.lng = (float) Float.parseFloat(json.get("lng").toString());
					} catch (Exception e) {
						System.out.println(details.droneId + " not found");
					}
				}
			},
	};
	String broker = "tcp://broker.hivemq.com:1883";
	String clientId = "217288933";
	MemoryPersistence persistence = new MemoryPersistence();
	MqttClient client = null;
	
	public static void main(String[] args) {
		final App appObj = new App();
		try {
			System.out.println("Connecting");
			appObj.connect();
			appObj.subscribeToTopics();
			
			appObj.configureCEP();
		} catch (MqttException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}		
	}
	
	public void configureCEP() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println();
				System.out.println(drones);
				checkBatteryLevels();
				checkStationaryDrones();
			}
		}, 0, 2000);
	}
	
	public void checkBatteryLevels() {
		int numLowBattery = 0;
		for (Drone drone: drones.values()) {
			if (drone.isBatteryLevelCritical()) {
				numLowBattery += 1;
			}
		}
		
		if (numLowBattery >= 2) {
			System.out.println("TWO DRONES WITH CRITICAL BATTERY");
		}
	}
	
	public void checkStationaryDrones() {
		for (Drone drone: drones.values()) {
			Date now = new Date();
			long diff = now.getTime() - drone.altitudeTime.getTime();
			
			if (diff >= 3000 && drone.altitude > 100) {
				System.out.println(drone.getId().toUpperCase() + " IS STATIONARY");
			}
		}
	}
	
	public void checkDrone(TopicDetails details) {
		if (!drones.containsKey(details.droneId)) {
			System.out.println("Added " + details.droneId);
			try {
				drones.put(details.droneId, new Drone(details.droneId, details.type));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void connect() throws MqttException {
		client = new MqttClient(broker, clientId, persistence);
		client.setCallback(this);
		MqttConnectOptions mqOptions = new MqttConnectOptions();
		mqOptions.setAutomaticReconnect(true);
		mqOptions.setCleanSession(true);
		client.connect(mqOptions); //connecting to broker
		System.out.println("Connected");
	}
	
	public void subscribeToTopics() throws MqttException {
		for (int i = 0; i < topics.length; i++) {
			Topic topic = topics[i];
			client.subscribe(topicBase + topic.topicUri, topic.handler());
			System.out.println("Subscribed to: " + topicBase + topic.topicUri);
		}
	}

	public void connectionLost(Throwable cause) {
		// TODO Auto-generated method stub
		System.out.println(cause.getMessage());
	}

	public void messageArrived(String topic, MqttMessage message) throws Exception {
//		System.out.println(topic + ": " + message);
	}

	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
}