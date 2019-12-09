package com.idt.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws UnknownHostException
    {
    	//设置集群名称
    	Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
    	//创建client
    	TransportClient client = new PreBuiltTransportClient(settings)
    			.addTransportAddresses(new InetSocketTransportAddress(InetAddress.getByName("199.66.68.101"), 9300)
    					,new InetSocketTransportAddress(InetAddress.getByName("199.66.68.101"), 9300)
    					,new InetSocketTransportAddress(InetAddress.getByName("199.66.68.101"), 9300));
    	GetResponse response = client.prepareGet("index1", "blog", "10").execute().actionGet();
    	System.out.println(response);
    }
}
