package si.fri.rso.musiclibrary.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.musiclibrary.models.dtos.Order;
import si.fri.rso.musiclibrary.models.entities.Song;
import si.fri.rso.musiclibrary.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@RequestScoped
public class MusiclibraryBean {

    private Logger log = Logger.getLogger(MusiclibraryBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private MusiclibraryBean musiclibraryBean;

    private Client httpClient;

    @Inject
    @DiscoverService("rso-orders")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
//        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Song> getCustomers() {

        TypedQuery<Song> query = em.createNamedQuery("Song.getAll", Song.class);

        return query.getResultList();

    }

    public List<Song> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Song.class, queryParameters);
    }

    public Song getSong(Integer songId) {

        Song song = em.find(Song.class, songId);

        if (song == null) {
            throw new NotFoundException();
        }

        List<Order> orders = musiclibraryBean.getOrders(songId);
        song.setOrders(orders);

        return song;
    }

    public Song createSong(Song song) {

        try {
            beginTx();
            em.persist(song);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return song;
    }

    public Song putSong(String songId, Song song) {

        Song c = em.find(Song.class, songId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            song.setId(c.getId());
            song = em.merge(song);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return song;
    }

    public boolean deleteSong(String songId) {

        Song song = em.find(Song.class, songId);

        if (song != null) {
            try {
                beginTx();
                em.remove(song);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    @Timed
    public List<Order> getOrders(Integer songId) {

        if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=songId:EQ:" + songId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }

        return null;

    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
