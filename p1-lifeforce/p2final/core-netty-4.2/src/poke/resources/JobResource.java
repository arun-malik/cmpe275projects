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
package poke.resources;

import poke.server.conf.ServerConf;
import poke.server.resources.Resource;

import com.google.protobuf.ByteString;
import com.lifeForce.storage.BlobStorage;
import com.lifeForce.storage.BlobStorageProfile;
import com.lifeForce.storage.BlobStorageService;
import com.lifeForce.storage.BlobStorageServiceImplementation;
import com.lifeForce.storage.MapperStorage;
import com.lifeForce.storage.ReplicatedDbServiceImplementation;

import eye.Comm.Header;
import eye.Comm.Payload;
import eye.Comm.PhotoHeader;
import eye.Comm.PhotoHeader.ResponseFlag;
import eye.Comm.PhotoPayload;
import eye.Comm.PhotoPayload.Builder;
import eye.Comm.Request;

public class JobResource implements Resource {

	private final String RESPONSE = "response";
	private ServerConf cfg;

	public ServerConf getCfg() {
		return cfg;
	}

	public void setCfg(ServerConf cfg) {
		this.cfg = cfg;
	}

	@Override
	public Request process(Request request) {

		BlobStorage blob = new BlobStorage();
		BlobStorageService blobService = new BlobStorageServiceImplementation();
		MapperStorage mapper = new MapperStorage();
		ReplicatedDbServiceImplementation mapperService = new ReplicatedDbServiceImplementation();
		try {

			if (request != null && request.getBody().hasPhotoPayload()) {

				switch (request.getHeader().getPhotoHeader().getRequestType()) {
				case write:
					System.out
							.println("****************Inside Write - Job Resource**************");
					final String uuid = java.util.UUID.randomUUID().toString();

					blob.setCaption(request.getBody().getPhotoPayload()
							.getName());
					blob.setContentLength(request.getHeader().getPhotoHeader()
							.getContentLength());
					blob.setImageData(request.getBody().getPhotoPayload()
							.getData().toByteArray());
					blob.setUuid(uuid);
					mapper.setUuid(uuid);
					mapper.setNodeId(cfg.getNodeId());

					try {

						// create blob in db
						blobService.createBlobStorage(blob);

						// create blob in mapper db
						mapperService.createMapperStorage(mapper);

						// create a body - photo payload builder from request
						Builder photoPayLoadBuilder = PhotoPayload
								.newBuilder(request.getBody().getPhotoPayload());

						// create a body - payload builder from request
						eye.Comm.Payload.Builder payLoadBuilder = Payload
								.newBuilder(request.getBody());

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

						// set uuid in photopayloadbuilder
						photoPayLoadBuilder.setUuid(uuid);

						// set image
						payLoadBuilder.setPhotoPayload(photoPayLoadBuilder
								.build());

						// set response as success in photoheader
						photoHeaderBuilder
								.setResponseFlag(ResponseFlag.success);

						// build photoheader builder
						headerBuilder
								.setPhotoHeader(photoHeaderBuilder.build());

						// build header
						requestBuilder.setHeader(headerBuilder.build());

						// build body
						requestBuilder.setBody(payLoadBuilder.build());

						// build request
						request = requestBuilder.build();

						return request;

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;

				case read:
					try {

						BlobStorageProfile blobFound = blobService
								.findByUuid(request.getBody().getPhotoPayload()
										.getUuid());

						// create a body - photo payload builder from request
						Builder photoPayLoadBuilder = PhotoPayload
								.newBuilder(request.getBody().getPhotoPayload());

						// create a body - payload builder from request
						eye.Comm.Payload.Builder payLoadBuilder = Payload
								.newBuilder(request.getBody());

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

						// if image found
						if (blobFound != null) {
							// set uuid, caption, image data
							photoPayLoadBuilder.setUuid(blobFound.getUuid());
							photoPayLoadBuilder.setName(blobFound.getCaption());
							photoPayLoadBuilder.setData(ByteString
									.copyFrom(blobFound.getImageData()));

							// set response as success in photoheader
							photoHeaderBuilder
									.setResponseFlag(ResponseFlag.success);

						} else {
							// if image not found
							photoPayLoadBuilder.setName("Image Not Found");

							// set reponse as failure in photo header
							photoHeaderBuilder
									.setResponseFlag(ResponseFlag.failure);
						}
						// build photoheader builder
						headerBuilder
								.setPhotoHeader(photoHeaderBuilder.build());
						// build payload
						payLoadBuilder.setPhotoPayload(photoPayLoadBuilder
								.build());
						// build header
						requestBuilder.setHeader(headerBuilder.build());
						// build body
						requestBuilder.setBody(payLoadBuilder.build());
						// build request
						request = requestBuilder.build();

						return request;

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;

				case delete:

					Boolean success = false;

					// create a body - photo payload builder from request
					Builder photoPayLoadBuilder = PhotoPayload
							.newBuilder(request.getBody().getPhotoPayload());

					// create a body - payload builder from request
					eye.Comm.Payload.Builder payLoadBuilder = Payload
							.newBuilder(request.getBody());

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

					try {

						Boolean blobSuccess = blobService
								.deleteBlobStorageByUuid(request.getBody()
										.getPhotoPayload().getUuid());

						Boolean mapperSuccess = mapperService
								.deleteMapper(request.getBody()
										.getPhotoPayload().getUuid());

						if (blobSuccess && mapperSuccess) {
							// set response as success in photoheader
							photoHeaderBuilder
									.setResponseFlag(ResponseFlag.success);
						} else {
							// set response as failure in photoheader
							photoHeaderBuilder
									.setResponseFlag(ResponseFlag.failure);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						photoHeaderBuilder
								.setResponseFlag(ResponseFlag.failure);

						e.printStackTrace();
					}

					// build photoheader builder
					headerBuilder.setPhotoHeader(photoHeaderBuilder.build());
					// build payload
					payLoadBuilder.setPhotoPayload(photoPayLoadBuilder.build());
					// build header
					requestBuilder.setHeader(headerBuilder.build());
					// build body
					requestBuilder.setBody(payLoadBuilder.build());
					// build request
					request = requestBuilder.build();

					return request;

				default:
					break;

				}
			}
		} catch (Exception ex) {

			eye.Comm.Header.Builder headerBuilder = Header.newBuilder(request
					.getHeader());
			headerBuilder.setReplyMsg(RESPONSE);
			Request.Builder requestBuilder = Request.newBuilder(request);
			requestBuilder.setHeader(headerBuilder.build());
			request = requestBuilder.build();

		} finally {

		}
		return request;
	}
}
