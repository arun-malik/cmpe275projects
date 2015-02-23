/*
 * copyright 2012, gash
 * 
 * Gash licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package poke.server.resources;

import java.beans.Beans;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.resources.ForwardResource;
import poke.resources.JobResource;
import poke.resources.MapperResource;
import poke.server.conf.ServerConf;
import poke.server.conf.ServerConf.ResourceConf;
import poke.server.managers.ElectionManager;
import poke.server.managers.HeartbeatManager;
import eye.Comm.Header;

/**
 * Resource factory provides how the server manages resource creation. We hide
 * the creation of resources to be able to change how instances are managed
 * (created) as different strategies will affect memory and thread isolation. A
 * couple of options are:
 * <p>
 * <ol>
 * <li>instance-per-request - best isolation, worst object reuse and control
 * <li>pool w/ dynamic growth - best object reuse, better isolation (drawback,
 * instances can be dirty), poor resource control
 * <li>fixed pool - favor resource control over throughput (in this case failure
 * due to no space must be handled)
 * </ol>
 * 
 * @author gash
 * 
 */
public class ResourceFactory {
	protected static Logger logger = LoggerFactory.getLogger("server");

	private static ServerConf cfg;
	private static AtomicReference<ResourceFactory> factory = new AtomicReference<ResourceFactory>();

	public static void initialize(ServerConf cfg) {
		try {
			ResourceFactory.cfg = cfg;
			factory.compareAndSet(null, new ResourceFactory());
		} catch (Exception e) {
			logger.error("failed to initialize ResourceFactory", e);
		}
	}

	public static ResourceFactory getInstance() {
		ResourceFactory rf = factory.get();
		if (rf == null)
			throw new RuntimeException("Server not intialized");

		return rf;
	}

	private ResourceFactory() {
	}

	/**
	 * Obtain a resource
	 * 
	 * @param route
	 * @return
	 */
	public Resource resourceInstance(Header header) {

		ResourceConf rc = cfg.findById(header.getRoutingId().getNumber());
		if (rc == null)
			return null;

		try {

			// if node is a leader node will decide to create the type of object
			if (ElectionManager.getInstance().whoIsTheLeader() != null
					&& cfg.getNodeId() == ElectionManager.getInstance()
							.whoIsTheLeader()) {

				// check the type of request
				switch (header.getPhotoHeader().getRequestType()) {

				// if write
				case write:
					// get node -- follow round Robin
					int node = RoundRobin.getForwardingNode();

					// if round robin gives node id of self / leader - create
					// Job Resource object
					if (cfg.getNodeId() == node) {

						JobResource rsc = (JobResource) Beans.instantiate(this
								.getClass().getClassLoader(), rc.getClazz());
						rsc.setCfg(cfg);
						return rsc;
					}

					// else create a forward resource object
					ForwardResource rsc = (ForwardResource) Beans.instantiate(
							this.getClass().getClassLoader(),
							cfg.getForwardingImplementation());
					rsc.setCfg(cfg);

					return rsc;

				case delete:
				case read:

					// for all read and delete request create a mapper resource
					// object - to identify where the data is stored
					MapperResource rscMapper = (MapperResource) Beans
							.instantiate(this.getClass().getClassLoader(),
									"poke.resources.MapperResource");
					rscMapper.setCfg(cfg);

					return rscMapper;

				default:
					return null;

				}
			} else {

				// if you are not leader - i.e scondary nodes just create job
				// resource object and follow the request
				JobResource rsc = (JobResource) Beans.instantiate(this
						.getClass().getClassLoader(), rc.getClazz());
				rsc.setCfg(cfg);
				return rsc;
			}

		} catch (Exception e) {
			logger.error("unable to create resource " + rc.getClazz());
			return null;
		}
	}

	// RoundRobin class is used to generate node Id - by referring it to
	// HearbeatManager -outboundHb
	public static class RoundRobin {
		private static Integer value = 0;
		private static int nextNode;

		public static int getForwardingNode() {

			// implementataion changed to RoundRobin
			// takes a value from 0 to max nodes
			// check value heartbeat from heartbeat manager - wheather node is
			// alive
			// increment the value -- initial 0
			// value++;

			// get aliveNodes from Heartbeat Manager - OutboundHB
			int aliveNodes = HeartbeatManager.getInstance().getAliveNodes();

			while (true) {

				// check whether node is alive or value is my NodeId
				if (cfg.getNodeId() == value
						|| HeartbeatManager.getInstance().isNodeAlive(value)) {

					RoundRobin.setNextNode(value);
					int node = value;
					System.out.println("roundrobin -- set node" + value);

					value++;
					return node;
				} else {

					// if node is not alive increment or just set it to zero
					// when max number of alive nodes are reached
					value = value - 1 == aliveNodes ? 0 : value++;
				}
			}
		}

		public static int getNextNode() {
			return nextNode;
		}

		public static void setNextNode(int nextNode) {
			RoundRobin.nextNode = nextNode;
		}

	}

}
