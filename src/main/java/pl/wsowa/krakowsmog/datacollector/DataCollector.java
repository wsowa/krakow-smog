package pl.wsowa.krakowsmog.datacollector;

import com.typesafe.config.ConfigFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

@Path("/jersey")
public class DataCollector {

    @GET
    @Path("/x")
    @Produces(MediaType.TEXT_PLAIN)
    public String x() {
        return ConfigFactory.load().getString("airly.sensorsFetchUrl");
    }

    @GET
    @Path("/y")
    @Produces(MediaType.TEXT_PLAIN)
    public String y() {
        return "yyyyy";
    }

    @GET
    @Path("/z")
    @Produces(MediaType.TEXT_PLAIN)
    public String z() {
        try {
            return CSVParser.parse(new File("WEB-INF/resources/pm25.csv"), Charset.defaultCharset(), CSVFormat.RFC4180.withHeader()).iterator().next().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

//    private Set<Sensor> fetchSensors() {
//        String url = ConfigFactory.load().getString("airly.sensorsFetchUrl");
//        try {
//            URLConnection yc = new URL(url).openConnection();
//            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null)
//                System.out.println(inputLine);
//            in.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
}
