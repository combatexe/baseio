package test;

import java.io.IOException;

import com.gifisan.mtp.client.NIOClient;
import com.gifisan.mtp.client.Response;
import com.gifisan.mtp.servlet.impl.StopServerServlet;

public class TestStopServer {

	
	public static void main(String[] args) throws IOException {
		String serviceKey = StopServerServlet.SERVICE_NAME;
		
		NIOClient client = ClientUtil.getClient();
		String params = ClientUtil.getParamString();
		
		client.connect();
		Response response = client.request(serviceKey, params);
		client.close();
		
		System.out.println(response.getContent());
		
	}
}
