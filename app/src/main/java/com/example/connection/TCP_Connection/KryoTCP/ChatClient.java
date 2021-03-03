
package com.example.connection.TCP_Connection.KryoTCP;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import java.io.IOException;

public class ChatClient {
	Client client;
	String name;

	public ChatClient() {
		client = new Client();
		client.start();

		// For consistency, the classes to be sent over the network are
		// registered by the same method for both the client and server.
		Network.register(client);

		client.addListener(new Listener() {
			public void connected(Connection connection) {
				Network.RegisterName registerName = new Network.RegisterName();
				registerName.name = name;
				client.sendTCP(registerName);
			}

			public void received(Connection connection, Object object) {
				if (object instanceof Network.UpdateNames) {
					Network.UpdateNames updateNames = (Network.UpdateNames) object;
					System.out.println(updateNames);
					return;
				}

				if (object instanceof Network.ChatMessage) {
					Network.ChatMessage chatMessage = (Network.ChatMessage) object;
					System.out.println(chatMessage);
					return;
				}
			}
		});

		final String host = "192.168.1.23";

		name = "client";

		Network.ChatMessage chatMessage = new Network.ChatMessage();
		chatMessage.text = "PORCO DIO";
		client.sendTCP(chatMessage);

		new Thread("Connect") {
			public void run() {
				try {
					client.connect(5000, host, 50000);
					// Server communication after connection can go here, or in Listener#connected().
				} catch (IOException ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
		}.start();

	}
}