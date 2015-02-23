/*
 * copyright 2014, gash
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
package poke.server.queue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.lang.Thread.State;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.resources.ForwardResource;
import poke.resources.JobResource;
import poke.resources.MapperResource;
import poke.server.managers.ElectionManager;
import poke.server.resources.Resource;
import poke.server.resources.ResourceFactory;

import com.google.protobuf.GeneratedMessage;

import eye.Comm.PhotoHeader.ResponseFlag;
import eye.Comm.Request;

/**
 * A server queue exists for each connection (channel). A per-channel queue
 * isolates clients. However, with a per-client model. The server is required to
 * use a master scheduler/coordinator to span all queues to enact a QoS policy.
 * 
 * How well does the per-channel work when we think about a case where 1000+
 * connections?
 * 
 * @author gash
 * 
 */
public class PerChannelQueue implements ChannelQueue {
	protected static Logger logger = LoggerFactory.getLogger("server");

	private Channel channel;

	// The queues feed work to the inbound and outbound threads (workers). The
	// threads perform a blocking 'get' on the queue until a new event/task is
	// enqueued. This design prevents a wasteful 'spin-lock' design for the
	// threads
	private LinkedBlockingDeque<com.google.protobuf.GeneratedMessage> inbound;
	private LinkedBlockingDeque<com.google.protobuf.GeneratedMessage> outbound;

	// This implementation uses a fixed number of threads per channel
	private OutboundWorker oworker;
	private InboundWorker iworker;
	private final String FORWARD = "forward";
	private final String RESPONSE = "response";

	// not the best method to ensure uniqueness
	private ThreadGroup tgroup = new ThreadGroup("ServerQueue-"
			+ System.nanoTime());

	protected PerChannelQueue(Channel channel) {
		this.channel = channel;
		init();
	}

	protected void init() {
		inbound = new LinkedBlockingDeque<com.google.protobuf.GeneratedMessage>();
		outbound = new LinkedBlockingDeque<com.google.protobuf.GeneratedMessage>();

		iworker = new InboundWorker(tgroup, 1, this);
		iworker.start();

		oworker = new OutboundWorker(tgroup, 1, this);
		oworker.start();

		// let the handler manage the queue's shutdown
		// register listener to receive closing of channel
		// channel.getCloseFuture().addListener(new CloseListener(this));
	}

	protected Channel getChannel() {
		return channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see poke.server.ChannelQueue#shutdown(boolean)
	 */
	@Override
	public void shutdown(boolean hard) {
		logger.info("server is shutting down");

		channel = null;

		if (hard) {
			// drain queues, don't allow graceful completion
			inbound.clear();
			outbound.clear();
		}

		if (iworker != null) {
			iworker.forever = false;
			if (iworker.getState() == State.BLOCKED
					|| iworker.getState() == State.WAITING)
				iworker.interrupt();
			iworker = null;
		}

		if (oworker != null) {
			oworker.forever = false;
			if (oworker.getState() == State.BLOCKED
					|| oworker.getState() == State.WAITING)
				oworker.interrupt();
			oworker = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see poke.server.ChannelQueue#enqueueRequest(eye.Comm.Finger)
	 */
	@Override
	public void enqueueRequest(Request req, Channel notused) {
		try {
			inbound.put(req);
		} catch (InterruptedException e) {
			logger.error("message not enqueued for processing", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see poke.server.ChannelQueue#enqueueResponse(eye.Comm.Response)
	 */
	@Override
	public void enqueueResponse(Request reply, Channel notused) {
		if (reply == null)
			return;

		try {
			outbound.put(reply);
		} catch (InterruptedException e) {
			logger.error("message not enqueued for reply", e);
		}
	}

	protected class OutboundWorker extends Thread {
		int workerId;
		PerChannelQueue sq;
		boolean forever = true;

		public OutboundWorker(ThreadGroup tgrp, int workerId, PerChannelQueue sq) {
			super(tgrp, "outbound-" + workerId);
			this.workerId = workerId;
			this.sq = sq;

			if (outbound == null)
				throw new RuntimeException(
						"connection worker detected null queue");
		}

		@Override
		public void run() {
			Channel conn = sq.channel;
			if (conn == null || !conn.isOpen()) {
				PerChannelQueue.logger
						.error("connection missing, no outbound communication");
				return;
			}

			while (true) {
				if (!forever && sq.outbound.size() == 0)
					break;

				try {

					// block until a message is enqueued
					GeneratedMessage msg = sq.outbound.take();
					if (conn.isWritable()) {
						boolean rtn = false;
						if (channel != null && channel.isOpen()
								&& channel.isWritable()) {
							ChannelFuture cf = channel.writeAndFlush(msg);

							// blocks on write - use listener to be async
							cf.awaitUninterruptibly();
							rtn = cf.isSuccess();
							if (!rtn)
								sq.outbound.putFirst(msg);
						}

					} else
						sq.outbound.putFirst(msg);
				} catch (InterruptedException ie) {
					break;
				} catch (Exception e) {
					PerChannelQueue.logger.error(
							"Unexpected communcation failure", e);
					break;
				}
			}

			if (!forever) {
				PerChannelQueue.logger.info("connection queue closing");
			}
		}
	}

	protected class InboundWorker extends Thread {
		int workerId;
		PerChannelQueue sq;
		boolean forever = true;

		public InboundWorker(ThreadGroup tgrp, int workerId, PerChannelQueue sq) {
			super(tgrp, "inbound-" + workerId);
			this.workerId = workerId;
			this.sq = sq;

			if (outbound == null)
				throw new RuntimeException(
						"connection worker detected null queue");
		}

		@Override
		public void run() {
			Channel conn = sq.channel;
			if (conn == null || !conn.isOpen()) {
				PerChannelQueue.logger
						.error("connection missing, no inbound communication");
				return;
			}

			while (true) {
				if (!forever && sq.inbound.size() == 0)
					break;

				try {
					System.out.println("--------------------------------->>>>> per channel queue ");
					// block until a message is enqueued
					GeneratedMessage msg = sq.inbound.take();

					System.out.println("-------------------------->>>>> take");
					
					// process request and enqueue response
					if (msg instanceof Request) {
						Request req = ((Request) msg);

						// if leader and entryNode - (flag - can be changed) -check request is new  
						if(ElectionManager.getInstance().whoIsTheLeader()!=null //&& ServerManager.getInstance().getNodeId() == ElectionManager.getInstance().whoIsTheLeader()  
								&& !req.getHeader().hasReplyMsg()) {
								//||req.getHeader().getPhotoHeader().hasEntryNode() ) {

							//add value entry node
							
							Resource rsc = ResourceFactory.getInstance()
									
									.resourceInstance(req.getHeader());
							
							// if job resource process locally
							if (rsc instanceof JobResource) {
								Request reply = rsc.process(req);
								sq.enqueueResponse(reply, null);
							}
							
							// if forward resource - forward it
							if (rsc instanceof ForwardResource) {
								((ForwardResource) rsc).setSq(sq);
								 rsc.process(req);
							}

							// if mapper resource - mapper get mapping from mapping db to get node location where image is stored
							if (rsc instanceof MapperResource) {
								((MapperResource) rsc).getSq();
								Request reply = rsc.process(req);
								
								// if mapper found uuid in map and header has node location where image is store - call Forward Resource
								if(reply.getHeader().getReplyMsg().equals(FORWARD) && reply.getHeader().getPhotoHeader().getResponseFlag() == ResponseFlag.success){
									ForwardResource fwdrsc = new ForwardResource();
									fwdrsc.setSq(sq);
									fwdrsc.process(reply);
									
//									((ForwardResource) rsc).setSq(sq);
//									rsc.process(reply);
								}
								
								// if mapper found uuid in map and image is stored in local - call Job Resource
								if(reply.getHeader().getReplyMsg().equals(RESPONSE) && reply.getHeader().getPhotoHeader().getResponseFlag() == ResponseFlag.success)
								{
									((JobResource) rsc).process(req);
									sq.enqueueResponse(reply, null);
								}
								
								//if mapper didnt found mapping of image and node response failure to client
								if(reply.getHeader().getPhotoHeader().getResponseFlag() == ResponseFlag.failure){
									sq.enqueueResponse(reply, null);
								}
								
							}

						}else{
							// secondary nodes
							
							System.out.println("---------------> inside secondary ");
							System.out.println("---> originator : "+req.getHeader().getOriginator());
							System.out.println("---> leader : "+ElectionManager.getInstance().whoIsTheLeader() );
							System.out.println("---> reply msg : "+req.getHeader().getReplyMsg());
							
							
							if(req.getHeader().getOriginator() == ElectionManager.getInstance().whoIsTheLeader() && req.getHeader().getReplyMsg().equals(FORWARD)){
								
								Resource rsc = ResourceFactory.getInstance()
										
										.resourceInstance(req.getHeader());
								
								// if job resource process locally
								if (rsc instanceof JobResource) {
									Request reply = rsc.process(req);
									sq.enqueueResponse(reply, null);
								}
							}
						}
					}

				} catch (InterruptedException ie) {
					break;
				} catch (Exception e) {
					PerChannelQueue.logger.error(
							"Unexpected processing failure", e);
					break;
				}
			}

			if (!forever) {
				PerChannelQueue.logger.info("connection queue closing");
			}
		}
	}

	public class CloseListener implements ChannelFutureListener {
		private ChannelQueue sq;

		public CloseListener(ChannelQueue sq) {
			this.sq = sq;
		}

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {

			// System.out.println("=> => => Inside CloseListener" );
			// sq.enqueueResponse(testReq, null);
			sq.shutdown(true);
		}
	}
}
