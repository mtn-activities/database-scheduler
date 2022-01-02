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

import mtn.activities.database.scheduler.lib.Section;
import mtn.activities.database.scheduler.lib.SectionDay;
import mtn.activities.database.scheduler.services.beans.SectionBean;

@ApplicationScoped
@Path("/sections")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SectionResource {
    private final Logger log = Logger.getLogger(SectionResource.class.getName());

    @Inject
    private SectionBean sectionBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getSection() {
        List<Section> sections = sectionBean.getSectionsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(sections).build();
    }

    @POST
    public Response createSection(Section section) {
        if (section == null || section.getSectionName() == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        else
            section = sectionBean.createSection(section);

        if(section == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(section).build();
    }

    @GET
    @Path("/{sectionId}")
    public Response getSection(@QueryParam("projection") String projection,
                               @PathParam("sectionId") Integer sectionId) {
        boolean fullProjection = projection != null && projection.equals("full");
        Section section = sectionBean.getSection(sectionId, fullProjection);

        if(section == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.status(Response.Status.OK).entity(section).build();
    }

    @PUT
    @Path("{sectionId}")
    public Response putSection(@PathParam("sectionId") Integer sectionId, Section section) {
        if(section == null || section.getSectionName() == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(sectionBean.getSection(sectionId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        section = sectionBean.putSection(sectionId, section);
        if (section == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(section).build();
    }

    @DELETE
    @Path("{sectionId}")
    public Response deleteSection(@PathParam("sectionId") Integer sectionId) {
        if(sectionBean.getSection(sectionId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        boolean deleted = sectionBean.deleteSection(sectionId);
        if (deleted)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @POST
    @Path("{sectionId}/schedule")
    public Response createSectionDay(@PathParam("sectionId") Integer sectionId, SectionDay sectionDay) {
        if(sectionDay == null || sectionDay.getDay() == null || sectionDay.getDay() < 0 || sectionDay.getDay() > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(sectionBean.getSection(sectionId) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        // TODO: validate position and fix if necessary
        if(sectionDay.getPosition() == null)
            sectionDay.setPosition(0);

        boolean created = sectionBean.createSectionDay(sectionId, sectionDay);
        if(created)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("{sectionId}/schedule/{weekday}")
    public Response getSectionDay(@PathParam("sectionId") Integer sectionId, @PathParam("weekday") Integer weekday) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();

        SectionDay sectionDay = sectionBean.getSectionDay(sectionId, weekday);
        if(sectionDay == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.status(Response.Status.OK).entity(sectionDay).build();
    }

    @PUT
    @Path("{sectionId}/schedule/{weekday}")
    public Response putSectionDay(@PathParam("sectionId") Integer sectionId, @PathParam("weekday") Integer weekday,
                                  SectionDay sectionDay) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(sectionDay == null || (sectionDay.getDay() != null && !Objects.equals(sectionDay.getDay(), weekday)))
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(sectionBean.getSectionDay(sectionId, weekday) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        // TODO: validate position and fix if necessary
        if(sectionDay.getPosition() == null)
            sectionDay.setPosition(0);
        if(sectionDay.getDay() == null)
            sectionDay.setDay(weekday);

        sectionDay = sectionBean.putSectionDay(sectionId, weekday, sectionDay);
        if (sectionDay == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        return Response.status(Response.Status.OK).entity(sectionDay).build();
    }

    @DELETE
    @Path("{sectionId}/schedule/{weekday}")
    public Response deleteSectionDay(@PathParam("sectionId") Integer sectionId, @PathParam("weekday") Integer weekday) {
        if(weekday == null || weekday < 0 || weekday > 6)
            return Response.status(Response.Status.BAD_REQUEST).build();
        if(sectionBean.getSectionDay(sectionId, weekday) == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        boolean deleted = sectionBean.deleteSectionDay(sectionId, weekday);
        if (deleted)
            return Response.status(Response.Status.NO_CONTENT).build();
        else
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

}
