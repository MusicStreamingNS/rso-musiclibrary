package si.fri.rso.musiclibrary.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;

import si.fri.rso.musiclibrary.models.entities.Artist;
import si.fri.rso.musiclibrary.services.beans.ArtistsBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Log
@ApplicationScoped
@Path("/artists")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtistResources {

    @Inject
    private ArtistsBean artistsBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getArtists() {

        List<Artist> artists = artistsBean.getArtists();

        return Response.ok(artists).build();
    }

    @GET
    @Path("/filtered")
    public Response getArtistsFiltered() {

        List<Artist> artists;

        artists = artistsBean.getArtistsFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(artists).build();
    }

    @GET
    @Path("/{artistId}")
    public Response getArtist(@PathParam("artistId") Integer artistId) {

        Artist artist = artistsBean.getArtist(artistId);

        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(artist).build();
    }

    @POST
    public Response createArtist(Artist artist) {

        if ((artist.getArtistName() == null || artist.getArtistBirthDate().isEmpty()) || (artist.getArtistCountry().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            artist = artistsBean.createArtist(artist);
        }

        if (artist.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(artist).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(artist).build();
        }
    }

    @PUT
    @Path("{artistId}")
    public Response putArtist(@PathParam("artistId") String artistId, Artist artist) {

        artist = artistsBean.putArtist(artistId, artist);

        if (artist == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (artist.getId() != null)
                return Response.status(Response.Status.OK).entity(artist).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{artistId}")
    public Response deleteArtist(@PathParam("artistId") String artistId) {

        boolean deleted = artistsBean.deleteArtist(artistId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
