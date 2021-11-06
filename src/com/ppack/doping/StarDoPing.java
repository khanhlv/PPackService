package com.ppack.doping;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import com.ppack.base.BaseDAO;
import com.ppack.bean.Link;

public class StarDoPing extends BaseDAO {
	
	private final static Logger logger = Logger.getLogger(StarDoPing.class.getSimpleName());
	public void startDoPing() {
		ConcurrentLinkedQueue<String> linkedQueue = readerFileRPC();
		List<Link> listLinkPing = listLinkPing();
		try {
			int timeout = Integer.parseInt(loadValueConfig("timeout"));
			
			while (true) {
				if (linkedQueue.size() > 0) {
					if (listFinish.size() < Integer.parseInt(loadValueConfig("threadService"))) {

						int count = listFinish.size() == 0 ? Integer.parseInt(loadValueConfig("threadService")) : Integer.parseInt(loadValueConfig("threadService")) - listFinish.size();

						for (int i = 0; i < count; i++) {
							if (linkedQueue.size() > 0) {
								final String linkService = linkedQueue.poll();
								listFinish.add(linkService);
								logger.info("SERVICE START: "+linkService);
								Thread threadDoPing = new Thread(new DoPing(listLinkPing, linkService));
								threadDoPing.start();
								
							}
						}
					}
				}
				
				try {
					Thread.sleep(timeout);
				} catch (Exception e) {

				}
				if(linkedQueue.size() == 0 && listFinish.size() == 0) {
					logger.info("PPing Done...");
					break;
				}
			}
			System.exit(-1);
		} catch(Exception ex) {
			logger.error(Thread.currentThread().getStackTrace()[1].getMethodName(), ex);
		}
	}
	
	public static void main(String[] args) {
		new StarDoPing().startDoPing();
	}
}
