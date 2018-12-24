package si.fri.rso.musiclibrary.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.musiclibrary.models.dtos.Order;
import si.fri.rso.musiclibrary.models.entities.Artist;
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
public class ArtistsBean {

    private Logger log = Logger.getLogger(ArtistsBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private ArtistsBean artistsBean;

    private Client httpClient;

    @Inject
    @DiscoverService("rso-orders")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
//        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Artist> getArtists() {

        TypedQuery<Artist> query = em.createNamedQuery("Artist.getAll", Artist.class);

        return query.getResultList();

    }

    public List<Artist> getArtistsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Artist.class, queryParameters);
    }

    public Artist getArtist(Integer artistId) {

        Artist artist = em.find(Artist.class, artistId);

        if (artist == null) {
            throw new NotFoundException();
        }

        List<Order> orders = artistsBean.getOrders(artistId);
        artist.setOrders(orders);

        return artist;
    }

    public Artist createArtist(Artist artist) {

        try {
            beginTx();
            em.persist(artist);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return artist;
    }

    public Artist putArtist(String artistId, Artist artist) {

        Artist c = em.find(Artist.class, artistId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            artist.setId(c.getId());
            artist = em.merge(artist);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return artist;
    }

    public boolean deleteArtist(String artistId) {

        Artist artist = em.find(Artist.class, artistId);

        if (artist != null) {
            try {
                beginTx();
                em.remove(artist);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    @Timed
    public List<Order> getOrders(Integer artistId) {

        if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=artistId:EQ:" + artistId)
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
