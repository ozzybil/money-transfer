package com.assignment.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/***
 * Base class for resources
 */
class ResourceBase {

    static final Logger log = LoggerFactory.getLogger(AccountResource.class);

    Response done(Object entity) {
        return entity != null ? ok(entity) : noContent();
    }

    Response ok(Object entity) {
        return status(Status.OK, entity);
    }

    Response error(Status status, String errorMessage) {
        log.warn(status + " - " + errorMessage);
        throw new WebApplicationException(errorMessage, status);
    }

    private Response noContent() {
        return status(Status.NO_CONTENT);
    }

    private Response status(Status status) {
        return Response.status(status).build();
    }

    private Response status(Status status, Object entity) {
        return Response.status(status).entity(entity).build();
    }
}
