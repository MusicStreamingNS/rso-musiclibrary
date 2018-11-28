package si.fri.rso.musiclibrary.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.musiclibrary.models.entities.Song;
import si.fri.rso.musiclibrary.services.beans.MusiclibraryBean;

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
@Path("/songs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MusicLibraryResources {

    @Inject
    private MusiclibraryBean musiclibraryBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getSongs() {

        List<Song> songs = musiclibraryBean.getCustomers();

        return Response.ok(songs).build();
    }

    @GET
    @Path("/filtered")
    public Response getSongsFiltered() {

        List<Song> songs;

        songs = musiclibraryBean.getCustomersFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(songs).build();
    }

    @GET
    @Path("/{songId}")
    public Response getSong(@PathParam("songId") Integer songId) {

        Song song = musiclibraryBean.getSong(songId);

        if (song == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(song).build();
    }

    @POST
    public Response createSong(Song song) {

        if ((song.getArtistName() == null || song.getAlbumName().isEmpty()) || (song.getSongName() == null
                || song.getYear().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            song = musiclibraryBean.createSong(song);
        }

        if (song.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(song).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(song).build();
        }
    }

    @PUT
    @Path("{songId}")
    public Response putSong(@PathParam("songId") String songId, Song song) {

        song = musiclibraryBean.putSong(songId, song);

        if (song == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (song.getId() != null)
                return Response.status(Response.Status.OK).entity(song).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{songId}")
    public Response deleteSong(@PathParam("songId") String songId) {

        boolean deleted = musiclibraryBean.deleteSong(songId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
