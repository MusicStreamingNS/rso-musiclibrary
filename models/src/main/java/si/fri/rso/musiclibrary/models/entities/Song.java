package si.fri.rso.musiclibrary.models.entities;

import si.fri.rso.musiclibrary.models.dtos.Order;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity(name = "song")
@NamedQueries(value =
        {
                @NamedQuery(name = "Song.getAll", query = "SELECT c FROM song c")
        })
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "song_name")
    private String songName;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "album_name")
    private String albumName;


    @Column(name = "year")
    private String songyear;

    @Transient
    private List<Order> orders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String firstName) {
        this.songName = songName;
    }


    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String lastName) {
        this.artistName = artistName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String lastName) { this.albumName = albumName; }

    public String getYear() {
        return songyear;
    }

    public void setYear(String address) {
        this.songyear = songyear;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}