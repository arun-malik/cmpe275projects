/**
 * 
 */
package com.lifeForce.handlers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.lifeForce.domain.BlobStorage;
import com.lifeForce.service.BlobStorageService;
import com.lifeForce.service.implementation.BlobStorageServiceImplementation;
import com.lifeForce.wrappers.BlobStorageProfile;

/**
 * @author arun_malik
 *
 */
public class LifeForce {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
     
		File imgPath = new File("/Users/arun_malik/Downloads/arun.jpg");
		BufferedImage bufferedImage;
		WritableRaster raster;
		DataBufferByte data;
		BlobStorage blob = new BlobStorage();
		
		try {
			
			bufferedImage = ImageIO.read(imgPath);
			raster = bufferedImage.getRaster();
			data = (DataBufferByte) raster.getDataBuffer();
		
		blob.setCaption("TestCaption");
		blob.setContentLength(40);
		blob.setCreatedBy("Malik");
		blob.setImageData(data.getData());
		blob.setUuid(java.util.UUID.randomUUID().toString());

		
		BlobStorageService blobStore = new BlobStorageServiceImplementation();
//		BlobStorageProfile savedBlob = blobStore.createBlobStorage(blob);
//		System.out.println("Saved Blob Id: "
//				+ savedBlob.getBlobStorageId().toString());
		
//		BlobStorageProfile getBlob = blobStore.findByUuid("660cca0c-2daa-4758-9206-b5f1ee67a82a");
//		System.out.println("Get Blob Id: "
//				+ getBlob.getBlobStorageId().toString());
		
		List<BlobStorageProfile> lstblobs = blobStore.findByCreatedBy("Malik");
		System.out.println("Get Blob Id: "
				+ lstblobs.get(0).getBlobStorageId().toString());
		
		} catch (IOException e1) {
			 System.out.println(e1.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
		}
	}

}
