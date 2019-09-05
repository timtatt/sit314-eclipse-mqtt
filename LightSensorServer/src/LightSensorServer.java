import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

public class LightSensorServer extends CoapServer {
	
	public static void main(String[] args) {
		LightSensorServer server = new LightSensorServer();
		server.start();
		server.addEndpoints();
	}
	
	public LightSensorServer() {
		LightSensorResource resource = new LightSensorResource();
		resource.setObservable(true);
		resource.getAttributes().setObservable();
		add(resource);
	}
	
	private void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (addr instanceof Inet4Address || addr.isLoopbackAddress()) {
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, NetworkConfig.getStandard().getInt(NetworkConfig.Keys.COAP_PORT));
				CoapEndpoint.Builder builder = new CoapEndpoint.Builder();
				builder.setInetSocketAddress(bindToAddress);
				this.addEndpoint(builder.build());
			}
		}
	}
}
