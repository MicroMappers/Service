package qa.qcri.mm.media.store;

/**
 * Created with IntelliJ IDEA.
 * User: jlucas
 * Date: 2/22/15
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class LookUp {

    public static final Integer MEDIA_POOL_AVAILABLE = 1;
    public static final Integer MEDIA_POOL_ASSIGNED = 2;

    public static final Integer MEDIA_SOURCE_AVAILABLE = 1;
    public static final Integer MEDIA_SOURCE_PROCESSED = 2;

    public static final Integer MEDIA_TYPE_SKYBOX_SATELLITE_IMAGERY = 1;
    public static final Integer MEDIA_TYPE_AERIAL_IMAGERY = 2;

    public static final Integer PARTNER_ACTIVE = 1;

    public static final Integer TILE_WIDTH = 600;
    public static final Integer TILE_HEIGHT = 600;

    public static final String[] CSV_SKYBOX_HEADER = {"fileURL", "width","height","minX", "minY", "maxX", "maxY", "bounds"};
    public static final int CVS_FILE_URL = 0;
    public static final int CVS_FILE_WIDTH = 0;
    public static final int CVS_FILE_HEIGHT = 0;
    public static final int CVS_FILE_MIN_X = 0;
    public static final int CVS_FILE_MIN_Y = 0;
    public static final int CVS_FILE_MAX_X = 0;
    public static final int CVS_FILE_MAX_Y = 0;
    public static final int CVS_FILE_BOUNDS = 0;

}
