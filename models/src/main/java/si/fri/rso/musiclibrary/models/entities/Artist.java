package si.fri.rso.musiclibrary.models.entities;

import si.fri.rso.musiclibrary.models.dtos.Order;

import javax.persistence.*;
import java.util.List;

@Entity(name = "artist")
@NamedQueries(value =
        {
                @NamedQuery(name = "Artist.getAll", query = "SELECT c FROM artist c")
        })
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "artist_name")
    private String artistName;


    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "country")
    private String artistCountry;


    @Transient

    private List<Order> orders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String lastName) {
        this.artistName = artistName;
    }

    public String getArtistBirthDate() {
        return birthDate;
    }

    public void setArtistBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getArtistCountry() {
        return artistCountry;
    }

    public void setArtistCountry(String artistCountry) {
        this.artistCountry = artistCountry;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}