

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Topic {
	public String topicUri;
	public String topicName;
	
	public Topic(String uri, String name) {
		topicUri = uri;
		topicName = name;
	}
	
	public void messageHandler(String topic, MqttMessage message) {
		System.out.println(topicName + ": " + message.toString());
	}
	
	public IMqttMessageListener handler() {
		return new IMqttMessageListener() {
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				try {
					messageHandler(topic, message);
				} catch (Exception e) {
					System.out.println("Something went wrong...");
					e.printStackTrace();
				}
			}
		};
	}
	
	public TopicDetails disectDroneTopic(String topic) {
		String[] splitTopic = topic.split("/");
		return new TopicDetails(splitTopic[1], splitTopic[2]);
	}
}
