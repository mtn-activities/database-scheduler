package mtn.activities.database.scheduler.api.v1.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import mtn.activities.database.scheduler.lib.Weekly;
import mtn.activities.database.scheduler.lib.WeeklyDay;
import mtn.activities.database.scheduler.services.beans.WeeklyBean;

@ApplicationScoped
@Path("/weeklies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WeeklyResource {
    private final Logger log = Logger.getLogger(WeeklyResource.class.getName());

    @Inject
    private WeeklyBean weeklyBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getWeekly() {
        List<Weekly> weeklies = weeklyBean.getWeekliesFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(weeklies).build();
    }

    @POST
    public Response createWeekly(Weekly weekly) {
        if (weekly == null || weekly.getWeeklyName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            weekly = weeklyBean.createWeekly(weekly);
        }

        if(weekly == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(weekly).build();
    }

    @GET
    @Path("/{weeklyId}")
    public Response getWeekly(@QueryParam("projection") String projection,
                              @PathParam("weeklyId") Integer weeklyId) {
        boolean fullProjection = projection != null && projection.equals("full");
        Weekly weekly = weeklyBean.getWeekly(weeklyId, fullProjection);

        if(weekly == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.status(Response.Status.OK).entity(weekly).build();
    }

    @PUT
    @Path("{weeklyId}")
    public Response putWeekly(@PathParam("weeklyId") Integer weeklyId, Weekly weekly) {
        if(weekly == null || weekly.getWeeklyName() == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(weeklyBean.getWeekly(weeklyId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        weekly = weeklyBean.putWeekly(weeklyId, weekly);
        if(weekly == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(weekly).build();
    }

    @DELETE
    @Path("{weeklyId}")
    public Response deleteWeekly(@PathParam("weeklyId") Integer weeklyId) {
        if(weeklyBean.getWeekly(weeklyId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        boolean deleted = weeklyBean.deleteWeekly(weeklyId);
        if (deleted)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("{weeklyId}/schedule")
    public Response createWeeklyDay(@PathParam("weeklyId") Integer weeklyId, WeeklyDay weeklyDay) {
        if(weeklyDay == null || weeklyDay.getDay() == null || weeklyDay.getDay() < 0 || weeklyDay.getDay() > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(weeklyBean.getWeekly(weeklyId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        if(!weeklyBean.validateSection(weeklyDay.getSectionId()))
            return Response.status(Response.Status.BAD_REQUEST).build();

        // TODO: validate position and fix if necessary
        if(weeklyDay.getPosition() == null)
            weeklyDay.setPosition(0);

        boolean created = weeklyBean.createWeeklyDay(weeklyId, weeklyDay);
        if(created)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("{weeklyId}/schedule/{weekday}")
    public Response getWeeklyDay(@PathParam("weeklyId") Integer weeklyId, @PathParam("weekday") Integer weekday) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();

        WeeklyDay weeklyDay = weeklyBean.getWeeklyDay(weeklyId, weekday);
        if(weeklyDay == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.status(Response.Status.OK).entity(weeklyDay).build();
    }

    @PUT
    @Path("{weeklyId}/schedule/{weekday}")
    public Response putWeeklyDay(@PathParam("weeklyId") Integer weeklyId, @PathParam("weekday") Integer weekday,
                                  WeeklyDay weeklyDay) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(weeklyDay == null || (weeklyDay.getDay() != null && !Objects.equals(weeklyDay.getDay(), weekday)))
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(weeklyBean.getWeeklyDay(weeklyId, weekday) == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        if(!weeklyBean.validateSection(weeklyDay.getSectionId()))
            return Response.status(Response.Status.BAD_REQUEST).build();

        // TODO: validate position and fix if necessary
        if(weeklyDay.getPosition() == null)
            weeklyDay.setPosition(0);
        if(weeklyDay.getDay() == null)
            weeklyDay.setDay(weekday);

        weeklyDay = weeklyBean.putWeeklyDay(weeklyId, weekday, weeklyDay);
        if (weeklyDay == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(weeklyDay).build();
    }

    @DELETE
    @Path("{weeklyId}/schedule/{weekday}")
    public Response deleteWeeklyDay(@PathParam("weeklyId") Integer weeklyId, @PathParam("weekday") Integer weekday) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(weeklyBean.getWeeklyDay(weeklyId, weekday) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        boolean deleted = weeklyBean.deleteWeeklyDay(weeklyId, weekday);
        if (deleted)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
