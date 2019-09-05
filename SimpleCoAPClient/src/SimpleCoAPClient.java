import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.coap.CoAP.Code;

public class SimpleCoAPClient {
	public static void main(String[] args) {
		CoapClient client = new CoapClient("coap://localhost:5683/");
		Request request = new Request(Code.GET);
		
		
		try {
			CoapResponse coapRes = client.advanced(request);
			
			System.out.println(Utils.prettyPrint(coapRes));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
