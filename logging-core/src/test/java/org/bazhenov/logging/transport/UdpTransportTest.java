package org.bazhenov.logging.transport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.*;

public class UdpTransportTest {

	@Test
	public void transportMustDeliverMessages()
		throws InterruptedException, TransportException, IOException {
		Listener listener = new Listener();
		Transport t = new UdpTransport(30054, listener);
		t.start();

		sendMessage(30054, "msg");
		synchronized ( listener ) {
			listener.wait(1000);
			assertThat(listener.getData(), equalTo("msg"));
		}
	}

	private void sendMessage(int port, String message) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length());
		InetAddress address;
		try {
			address = InetAddress.getLocalHost();
		} catch ( UnknownHostException e ) {
			throw new RuntimeException(e);
		}
		packet.setAddress(address);
		packet.setPort(port);
		socket.send(packet);
	}

	static class Listener implements TransportListener {

		private String data;

		public void onMessage(String message) throws TransportException {
			data = message;
			synchronized ( this ) {
				notify();
			}
		}

		public String getData() {
			return data;
		}
	}
}
