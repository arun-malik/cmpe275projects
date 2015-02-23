/**
 * 
 */
package poke.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import poke.server.conf.NodeDesc;
import poke.server.conf.ServerConf;
import poke.server.managers.HeartbeatManager;
import poke.server.queue.PerChannelQueue;
import poke.server.resources.Resource;

import com.lifeForce.storage.MapperStorage;
import com.lifeForce.storage.ReplicatedDbServiceImplementation;

import eye.Comm.Header;
import eye.Comm.PhotoHeader;
import eye.Comm.PhotoHeader.ResponseFlag;
import eye.Comm.Request;

/**
 * @author arun_malik
 *
 */
public class MapperResource implements Resource {

	protected static Logger logger = LoggerFactory.getLogger("MapperResource");
	private final String FORWARD = "forward";
	private final String RESPONSE = "response";
	private ServerConf cfg;
	private PerChannelQueue sq;

	/**
	 * @return the sq
	 */
	public PerChannelQueue getSq() {
		return sq;
	}

	/**
	 * @param sq
	 *            the sq to set
	 */
	public void setSq(PerChannelQueue sq) {
		this.sq = sq;
	}

	public ServerConf getCfg() {
		return cfg;
	}

	/**
	 * Set the server configuration information used to initialized the server.
	 * 
	 * @param cfg
	 */
	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	@Override
	public Request process(Request request) {

		// create objects
		MapperStorage mapper = new MapperStorage();
		ReplicatedDbServiceImplementation mapperService = ReplicatedDbServiceImplementation.getInstance();
		String nextNodeIp = null;
		int nextNodePort = 0;

		try {

			// if request is not null
			if (request != null
					&& request.getBody().getPhotoPayload().hasUuid()) {

				// get node it from mapper db
				mapper = mapperService.findNodeIdByUuid(request.getBody()
						.getPhotoPayload().getUuid());

				// image mapping found in the database
				if (null != mapper
						&& ( HeartbeatManager.getInstance().isNodeAlive(
								(int) mapper.getNodeId())|| mapper.getNodeId() == cfg.getNodeId()  )) {

					System.out
							.println(" --------->  found image in mapper. so  data is in my cluster.");

					if (mapper.getNodeId() == cfg.getNodeId()) {

						System.out
								.println(" --------->  found image in my db. Leader has data.");

						// create the request builder
						Request.Builder requestBuilder = Request
								.newBuilder(request);

						// create the photo header builder
						eye.Comm.PhotoHeader.Builder photoHeaderBuilder = PhotoHeader
								.newBuilder(request.getHeader()
										.getPhotoHeader());

						// create the header builder
						eye.Comm.Header.Builder headerBuilder = Header
								.newBuilder(request.getHeader());

						// set reply message as response in the header
						headerBuilder.setReplyMsg(RESPONSE);

						// set response as success in photoheader
						photoHeaderBuilder
								.setResponseFlag(ResponseFlag.success);

						// build photoheader builder
						headerBuilder
								.setPhotoHeader(photoHeaderBuilder.build());

						// build header
						requestBuilder.setHeader(headerBuilder.build());

						// build request
						request = requestBuilder.build();

						return request;
					} else {

						System.out
								.println(" --------->  found image in my cluster. Node id : "
										+ mapper.getNodeId()
										+ " has data in it's db");

						for (NodeDesc node : cfg.getAdjacent()
								.getAdjacentNodes().values()) {
							if (mapper.getNodeId() == node.getNodeId()) {

								nextNodeIp = node.getHost();
								nextNodePort = node.getPort();
								break;
							}
						}

						// create the request builder
						Request.Builder requestBuilder = Request
								.newBuilder(request);

						// create the photo header builder
						eye.Comm.PhotoHeader.Builder photoHeaderBuilder = PhotoHeader
								.newBuilder(request.getHeader()
										.getPhotoHeader());

						// create the header builder
						eye.Comm.Header.Builder headerBuilder = Header
								.newBuilder(request.getHeader());

						// set reply message as response in the header
						headerBuilder.setReplyMsg(FORWARD);

						// set originator in header
						headerBuilder.setOriginator(cfg.getNodeId());

						// set ip
						headerBuilder.setIp(nextNodeIp);

						// set port
						headerBuilder.setPort(nextNodePort);

						// set response as success in photoheader
						photoHeaderBuilder
								.setResponseFlag(ResponseFlag.success);

						// build photoheader builder
						headerBuilder
								.setPhotoHeader(photoHeaderBuilder.build());

						// build header
						requestBuilder.setHeader(headerBuilder.build());

						// build request
						request = requestBuilder.build();

						return request;
					}
				} else {

					System.out
							.println(" --------->  data not found in my cluster. Set failure and forward the request to some other leader in cluster");

					// create the request builder
					Request.Builder requestBuilder = Request
							.newBuilder(request);

					// create the photo header builder
					eye.Comm.PhotoHeader.Builder photoHeaderBuilder = PhotoHeader
							.newBuilder(request.getHeader().getPhotoHeader());

					// create the header builder
					eye.Comm.Header.Builder headerBuilder = Header
							.newBuilder(request.getHeader());

					// set reply message as response in the header
					headerBuilder.setReplyMsg(RESPONSE);

					// set response as success in photoheader
					photoHeaderBuilder.setResponseFlag(ResponseFlag.failure);

					// build photoheader builder
					headerBuilder.setPhotoHeader(photoHeaderBuilder.build());

					// set originator in header
					headerBuilder.setOriginator(cfg.getNodeId());

					// build header
					requestBuilder.setHeader(headerBuilder.build());

					// build request
					request = requestBuilder.build();

					return request;
				}
			}
			
			System.out.println(" --------->  request is null.");
			return null;
		} catch (Exception ex) {

		} finally {
			mapper = null;
			mapperService = null;
		}
		return null;
	}
}
