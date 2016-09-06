package trueffect.truconnect.api.commons.filter.web;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.Response;

/**
 * Jersey 1.8 Response Filter in charge of adding CORS support.
 *
 * Created by marcelo.heredia on 05/11/2015.
 */
public class JerseyCorsFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request, ContainerResponse contResp) {
        Response.ResponseBuilder response = Response.fromResponse(contResp.getResponse());
        response = response.header("Access-Control-Allow-Origin", "*");
        response = response.header("Access-Control-Allow-Credentials", "true");
        response = response.header("Access-Control-Expose-Headers", "WWW-Authenticate");

        if (request.getHeaderValue("Access-Control-Request-Method") != null
                && "OPTIONS".equals(request.getMethod())) {
            // CORS "pre-flight" request
            response = response.header("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            response = response.header("Access-Control-Allow-Headers",
                    "X-Requested-With, Origin, Content-Type, Context-Id, Authorization, Accept");
        }

        contResp.setResponse(response.build());
        return contResp;
    }

}