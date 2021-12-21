package org.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sala")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nr_locuri")
    private Integer nrLocuri;

    @OneToMany(mappedBy = "sala", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Spectacol> spectacole = new ArrayList<>();

    @OneToMany(mappedBy = "sala", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<Vanzare> vanzares = new ArrayList<>();

    public List<Vanzare> getVanzares() {
        return vanzares;
    }

    public void setVanzares(List<Vanzare> vanzares) {
        this.vanzares = vanzares;
    }

    public List<Spectacol> getSpectacole() {
        return spectacole;
    }

    public void setSpectacole(List<Spectacol> spectacole) {
        this.spectacole = spectacole;
    }

    public Integer getNrLocuri() {
        return nrLocuri;
    }

    public void setNrLocuri(Integer nrLocuri) {
        this.nrLocuri = nrLocuri;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}