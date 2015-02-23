package poke.server.queue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManagementQueueMap {

	public static Map<Integer, PerChannelQueue> internalClientMap = new ConcurrentHashMap<Integer, PerChannelQueue>();

}
