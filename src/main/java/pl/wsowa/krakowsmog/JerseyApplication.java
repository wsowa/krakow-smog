package pl.wsowa.krakowsmog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.googlecode.objectify.ObjectifyService;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;
import pl.wsowa.krakowsmog.dataanalyser.DataAnalyser;
import pl.wsowa.krakowsmog.datastore.DatastoreDataSource;
import pl.wsowa.krakowsmog.datastore.MeasurementDataObject;
import pl.wsowa.krakowsmog.datastore.SensorDataObject;

public class JerseyApplication extends ResourceConfig {

    public JerseyApplication() {
        ObjectifyService.init();
        ObjectifyService.register(MeasurementDataObject.class);
        ObjectifyService.register(SensorDataObject.class);

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new ParameterNamesModule())
                .registerModule(new Jdk8Module())
                .registerModule(new JavaTimeModule());
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(mapper);
        register(JspMvcFeature.class);
        register(jsonProvider);

        register(new DataAnalyser(new DatastoreDataSource()));

    }
}
