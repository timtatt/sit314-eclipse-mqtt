import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.network.config.NetworkConfig;

public class SimpleCoAPServer extends CoapServer {

	public static void main(String[] args) {
		
		SimpleCoAPServer server = new SimpleCoAPServer();
		server.start();
		server.addEndpoints();
		
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
