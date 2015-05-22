package se.asplund;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Provider
public class UnauthorizedMapper implements ExceptionMapper<SecurityException> {
	@Override
	public Response toResponse(SecurityException exception) {
		return Response.status(UNAUTHORIZED).entity(exception.getMessage()).type("text/plain").build();
	}
}
