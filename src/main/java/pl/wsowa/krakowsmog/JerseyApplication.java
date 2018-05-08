package pl.wsowa.krakowsmog;

import com.googlecode.objectify.ObjectifyService;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import pl.wsowa.krakowsmog.dataanalyser.CSVDataSource;
import pl.wsowa.krakowsmog.datacollector.DataCollector;
import pl.wsowa.krakowsmog.datauploader.CSVDataUploader;

import java.io.IOException;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        // property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/classes");
        register(JspMvcFeature.class);
        register(JacksonFeature.class);

        register(DataCollector.class);

        try {
            CSVDataSource dataSource = new CSVDataSource("WEB-INF/resources/pm25.csv", "WEB-INF/resources/pm10.csv");
            register(new CSVDataUploader(dataSource));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
