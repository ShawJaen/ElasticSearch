package com.idt.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
	
	@Bean
	public TransportClient client() throws UnknownHostException {
		//集群
		Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
		//
		TransportClient client = new PreBuiltTransportClient(settings).addTransportAddresses(
				new InetSocketTransportAddress(InetAddress.getByName("199.66.68.101"), 9300),
				new InetSocketTransportAddress(InetAddress.getByName("199.66.68.102"), 9300),
				new InetSocketTransportAddress(InetAddress.getByName("199.66.68.103"), 9300));
		return client;
	}
}
