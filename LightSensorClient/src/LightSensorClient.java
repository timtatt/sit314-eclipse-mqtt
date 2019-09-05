import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.Code;

public class LightSensorClient extends CoapClient {
	public static void main(String[] args) {
		CoapClient client = new CoapClient("coap://localhost:5683/LightSensor");
		Request request = new Request(Code.GET);
		
		CoapObserveRelation relation = client.observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				String content = response.getResponseText();
				System.out.println("Notification: " + content);
			}
			
			@Override
			public void onError() { 
				System.out.println("Observing Failed");
			}
		});
		
		relation.proactiveCancel();
		
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			
			while (true) {
				String input = reader.readLine();
				
				if (input != null) {
					System.out.println("EXIT");
					System.exit(0);
				}
				
				try {
					CoapResponse coapRes = client.advanced(request);
					
					System.out.println(Utils.prettyPrint(coapRes));
				} catch (Exception e) {
					System.out.println(e.toString());
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
