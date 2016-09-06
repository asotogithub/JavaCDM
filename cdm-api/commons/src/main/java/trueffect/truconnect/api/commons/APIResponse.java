package trueffect.truconnect.api.commons;

import java.net.URI;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.springframework.validation.FieldError;
import org.springframework.validation.BeanPropertyBindingResult;

import trueffect.truconnect.api.commons.model.Error;
import trueffect.truconnect.api.commons.model.Errors;
import trueffect.truconnect.api.commons.validation.ApiValidationUtils;

/**
 *
 * @author Rambert Rioja
 */
public class APIResponse {

    public static URI getLocation(UriInfo uriInfo, String path) {
        return uriInfo.getBaseUriBuilder().path(path).build("");
    }

    public static ResponseBuilder ok() {
        return Response.ok();
    }

    public static ResponseBuilder ok(Object object) {
        return Response.ok(object);
    }

    public static ResponseBuilder ok(Object object, UriInfo uriInfo, String path) {
        return Response.ok(object).location(getLocation(uriInfo, path));
    }

    public static ResponseBuilder created(Object object, UriInfo uriInfo, String path) {
        return Response.status(Response.Status.CREATED)
                .location(getLocation(uriInfo, path))
                .entity(object);
    }

    public static ResponseBuilder created(Object object) {
        return Response.status(Response.Status.CREATED)
                .entity(object);
    }

    public static ResponseBuilder bad(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.BAD_REQUEST).entity(errors);
    }

    public static ResponseBuilder bad(Errors errors) {
        return Response.status(Response.Status.BAD_REQUEST).entity(errors);
    }

    public static ResponseBuilder bad(BeanPropertyBindingResult list) {
        List<Error> errors = new ArrayList<Error>();
        for (Object o : list.getAllErrors()) {
            FieldError field = (FieldError) o;
            Error error = new Error();
            error.setMessage(field.getDefaultMessage());
            error.setType(field.getCode());
            errors.add(error);
        }
        Errors result = new Errors();
        result.setErrors(errors);
        return Response.status(Response.Status.BAD_REQUEST).entity(result);
    }

    public static ResponseBuilder notFound(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_NOT_FOUND);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.NOT_FOUND).entity(errors);
    }

    public static ResponseBuilder notFound() {
        return Response.status(Response.Status.NOT_FOUND);
    }

    public static Response conflict(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.CONFLICT)
                .entity(errors).build();
    }

    public static ResponseBuilder conflict(String message, UriInfo uriInfo, String path) {
        Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.CONFLICT)
                .location(getLocation(uriInfo, path))
                .entity(errors);
    }

    public static Response forbidden(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_ACCESS_DENIED);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.FORBIDDEN)
                .header("WWW-Authenticate", message)
                .entity(errors).build();
    }

    public static Response error(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_INTERNAL_ERROR);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errors).build();
    }

    public static Response error() {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
    
    public static ResponseBuilder generic(int status, Object object) {
        return Response.status(status).entity(object);
    }

    public static Response notAcceptable(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_NOT_ACCEPTABLE);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.NOT_ACCEPTABLE)
                .entity(errors).build();
    }

    public static ResponseBuilder notModified(String message) {
        Error error = new Error(message, ApiValidationUtils.TYPE_INVALID);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.NOT_MODIFIED).entity(errors);
    }

    public static ResponseBuilder serviceUnavailable(String message) {
        Error error = new Error(message, ApiValidationUtils.SERVICE_UNAVAILABLE);
        Errors errors = new Errors(Arrays.asList(error));
        return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(errors);
    }
}