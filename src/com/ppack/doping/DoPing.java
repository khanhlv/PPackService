package com.ppack.doping;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.ppack.base.BaseDAO;
import com.ppack.bean.Link;


public class DoPing  implements Runnable {

	private final Logger logger = Logger.getLogger(DoPing.class.getSimpleName());
	private String service;
	private List<Link> list;
	public DoPing(List<Link> list,String service) {
		this.list = list;
		this.service = service;
	}
	@Override
	public void run() {
		for(Link lk : list) {
			doPing(lk.getName(),lk.getUrl(),service,"weblogUpdates.extendedPing");
		}
		BaseDAO.listFinish.remove(service);
	}
	/**
	 * 
	 * @param name - Name
	 * @param URI - source URL
	 * @param client - list RPC
	 * @param method - add
	 */
	public void doPing(String name, String URI, String client, String method) {

		try {

	
			final XmlRpcClient xmlrpc = new XmlRpcClient();
			//xmlrpc.setTransportFactory(new XmlRpcCommonsTransportFactory(xmlrpc));
			final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		    config.setServerURL(new URL(client));
		    xmlrpc.setConfig(config);
		    
			final Vector<String> params = new Vector<String>();
			params.addElement(name);
			params.addElement(URI);
			final Object result = xmlrpc.execute(method, params);
			
			if ((result != null)) {
				logger.info("--------------------------------------------------------");
				logger.info("#Message: "+result.toString());
				logger.info("#Name: "+name);
				logger.info("#URI: "+URI);
				logger.info("#Client: "+client);
				logger.info("#Method: "+method);
			}
		} catch (Exception ex) {
			logger.error("ERROR: ", ex);
		}

	}
}
