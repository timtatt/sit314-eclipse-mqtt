import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class LightSensorResource extends CoapResource {
	private float lux = 0f;
	
	public LightSensorResource() {
		super("LightSensor");
		
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this),  0, 1000);
	}
	
	private class UpdateTask extends TimerTask {
		private CoapResource resource;
		
		public UpdateTask(CoapResource resource) {
			this.resource = resource;
		}
		
		@Override
		public void run() {
			lux = new Random().nextInt(20);
			resource.changed();
		}
	}
	
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT, "LightSensor", MediaTypeRegistry.TEXT_PLAIN);
	}
}
