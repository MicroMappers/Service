package qa.qcri.mm.api.service.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import qa.qcri.mm.api.dao.ImageConfigDao;
import qa.qcri.mm.api.dao.SlicedImageDao;
import qa.qcri.mm.api.dao.SourceImageDao;
import qa.qcri.mm.api.entity.ImageConfig;
import qa.qcri.mm.api.entity.SlicedImage;
import qa.qcri.mm.api.entity.SourceImage;
import qa.qcri.mm.api.service.ClientAppSourceService;
import qa.qcri.mm.api.service.SlicedImageService;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

@Service("imageMetaDataService")
@Transactional(readOnly = false)
public class SlicedImageServiceImpl implements SlicedImageService {

	protected static Logger logger = Logger.getLogger("SlicedImageServiceImpl");

	@Autowired
	ImageConfigDao imageConfigDao;

	@Autowired
	SlicedImageDao slicedImageDao;

	@Autowired
	ClientAppSourceService clientAppSourceService;
	
	@Autowired
	SourceImageDao sourceImageDao;

	@Value("${image.url}")
	private String imageURL;

	@Value("${image.destination}")
	private String imageDestinationFolder;

	@Override
	public void sliceImage(Long clientAppId) {

		ImageConfig imageSource = imageConfigDao.getByClientAppId(clientAppId);

		if (imageSource != null) {
			Integer rows = imageSource.getRows() != null ? imageSource.getRows() : 1;
			Integer columns = imageSource.getColumns() != null ? imageSource.getColumns() : 1;

			File parentFolder = new File(imageSource.getSource());
			if (parentFolder.exists()) {
				File[] listOfFiles = parentFolder.listFiles();
				List<SlicedImage> imageMetaDataList = new ArrayList<SlicedImage>();

				for (File file : listOfFiles) {
					if (file.isFile() && validImageFileType(file)) {
						
						SourceImage sourceImage = new SourceImage(imageSource, file.getName());
						Long sourceImageId = null;
						try{
							sourceImageId = sourceImageDao.persist(sourceImage);
						}
						catch(Exception e){
							logger.error("Exception while persisting file: "+file.getPath(), e);
							throw e;
						}
						
						String imageFileExtension = FilenameUtils.getExtension(file.getName());
						sourceImage.setId(sourceImageId);
						
						switch (imageFileExtension.toLowerCase()) {

						case "jpg":
							processJPEGImage(imageMetaDataList, sourceImage, file, rows, columns);
							break;
						case "jpeg":
							processJPEGImage(imageMetaDataList, sourceImage, file, rows, columns);
							break;
						default:
							processExceptJPEGImage(imageMetaDataList, sourceImage, file, rows, columns);
							break;
						}
					}
					//Persisting to db in a batch of 100 records
					if (imageMetaDataList.size() > 10) {
						try{
							for (SlicedImage imageMetaData : imageMetaDataList) {
								slicedImageDao.saveMapBoxDataTile(imageMetaData);
							}
							imageMetaDataList = new ArrayList<SlicedImage>();
						}catch(Exception e){
							logger.error("Exception while persisting to db",e);
						}
					}
				}
				if (imageMetaDataList.size() > 0) {
					try{
						for (SlicedImage imageMetaData : imageMetaDataList) {
							slicedImageDao.saveMapBoxDataTile(imageMetaData);
						}
					}catch(Exception e){
						logger.error("Exception while persisting to db",e);
					}
				}
			}

		}
	}

	//Using ImageIO since Imaging not able to process JPEG images 
	private void processJPEGImage(List<SlicedImage> imageMetaDataList, SourceImage sourceImage, File imageFile,
			Integer rows, Integer columns) {
		try {

			String imageFileExtension = FilenameUtils.getExtension(imageFile.getName());
			String tempFileName = FilenameUtils.removeExtension(imageFile.getName());

			//Fetching geo info from image
			SlicedImage imageMetaData = getLatLonBoundsFromImage(imageFile);
			imageMetaData.setSourceImage(sourceImage);

			//slicing of image
			BufferedImage image = ImageIO.read(imageFile);
			int smallWidth = image.getWidth() / columns;
			int smallHeight = image.getHeight() / rows;

			String slicedParentFolderName = imageDestinationFolder + imageFile.getParentFile().getName() + "/slice/";
			File slicedParentFolder = new File(slicedParentFolderName);
			if (!slicedParentFolder.exists()) {
				slicedParentFolder.mkdirs();
			}

			for (int x = 0; x < columns; x++) {
				for (int y = 0; y < rows; y++) {
					BufferedImage subImage = image
							.getSubimage(x * smallWidth, y * smallHeight, smallWidth, smallHeight);
					try {
						String slicedFileName = tempFileName + "_" + x + "_" + y + "." + imageFileExtension;
						String slicedFilePathName = slicedParentFolderName + slicedFileName;

						ImageIO.write(subImage, imageFileExtension, new File(slicedFilePathName));

						//cloning imageMetaData object,
						//imageMetaData having data common to every sub-image
						SlicedImage imageMetaDataOfSubImage = new SlicedImage(imageMetaData);

						imageMetaDataOfSubImage.setSlicedFileURL(imageURL + imageFile.getParentFile().getName()
								+ "/slice/" + slicedFileName);
						imageMetaDataList.add(imageMetaDataOfSubImage);
					} catch(IOException e){
						logger.error("IOException while writing sub-image: ",e);
					}
				}
			}
		} catch (IOException e) {
			logger.error("IOException in file: "+imageFile.getPath(),e);
		}
	}

	//ImageIO not able to process TIFF images so using Imaging
	private void processExceptJPEGImage(List<SlicedImage> imageMetaDataList, SourceImage sourceImage, File imageFile,
			Integer rows, Integer columns) {
		try {

			String imageFileExtension = FilenameUtils.getExtension(imageFile.getName());
			String tempFileName = FilenameUtils.removeExtension(imageFile.getName());

			//Fetching geo info from image
			SlicedImage imageMetaData = getLatLonBoundsFromImage(imageFile);
			imageMetaData.setSourceImage(sourceImage);

			//slicing of image
			BufferedImage image = Imaging.getBufferedImage(imageFile);
			int smallWidth = image.getWidth() / columns;
			int smallHeight = image.getHeight() / rows;

			String slicedParentFolderName = imageDestinationFolder + imageFile.getParentFile().getName() + "/slice/";
			File slicedParentFolder = new File(slicedParentFolderName);
			if (!slicedParentFolder.exists()) {
				slicedParentFolder.mkdirs();
			}

			for (int x = 0; x < columns; x++) {
				for (int y = 0; y < rows; y++) {
					BufferedImage subImage = image
							.getSubimage(x * smallWidth, y * smallHeight, smallWidth, smallHeight);
					try {
						String slicedFileName = tempFileName + "_" + x + "_" + y + "." + imageFileExtension;
						String slicedFilePathName = slicedParentFolderName + slicedFileName;

						Imaging.writeImage(subImage, new File(slicedFilePathName), getImageFormat(imageFileExtension),null);

						//cloning imageMetaData object,
						//imageMetaData having data common to every sub-image
						SlicedImage imageMetaDataOfSubImage = new SlicedImage(imageMetaData);

						imageMetaDataOfSubImage.setSlicedFileURL(imageURL + imageFile.getParentFile().getName()
								+ "/slice/" + slicedFileName);
						imageMetaDataList.add(imageMetaDataOfSubImage);
					}  catch (ImageWriteException | IOException e) {
						logger.error("IOException while writing sub-image: ",e);
					} 
				}
			}
		}  catch (IOException | ImageReadException e) {
			logger.error("IOException in file: "+imageFile.getPath(),e);
		}
	}

	private SlicedImage getLatLonBoundsFromImage(File image) {
		SlicedImage imageMetaData = new SlicedImage();
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(image);
			GpsDirectory gpsDirectory = metadata.getDirectory(GpsDirectory.class);
			if (gpsDirectory != null) {
				GeoLocation location = gpsDirectory.getGeoLocation();
				if (location != null) {
					double latitude = location.getLatitude();
					imageMetaData.setLat(Double.toString(latitude));
					double longitude = location.getLongitude();
					imageMetaData.setLon(Double.toString(longitude));
					JSONArray bounds = clientAppSourceService.getBoundsByLatLng(new double[] { latitude, longitude });
					imageMetaData.setBounds(bounds.toJSONString());
				}
			}
		} catch(IOException e){
			logger.error("IOException while fetching lat lon bounds from image: "+image.getPath(), e);
		} catch (ImageProcessingException ipe) {
			logger.error("ImageProcessingException while fetching meta-data from image: "+image.getPath(), ipe);
		}
		return imageMetaData;
	}

	private boolean validImageFileType(File file) {
		MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
		mtftp.addMimeTypes("image png tif tiff jpg jpeg bmp");
		String mimetype = mtftp.getContentType(file);
		String type = mimetype.split("/")[0];
		if (type.toLowerCase().equals("image")) {
			return true;
		} else {
			return false;
		}
	}

	private ImageFormats getImageFormat(String extension) {

		switch (extension.toLowerCase()) {
		case "png":
			return ImageFormats.PNG;
		case "tif":
			return ImageFormats.TIFF;
		case "tiff":
			return ImageFormats.TIFF;
		case "bmp":
			return ImageFormats.BMP;
		case "JPG":
			return ImageFormats.JPEG;
		case "JPEG":
			return ImageFormats.JPEG;
		default:
			return null;
		}
	}

}
