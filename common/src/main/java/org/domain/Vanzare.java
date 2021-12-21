package org.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "vanzare")
public class Vanzare {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_vanzare")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "id_sala")
    private Sala sala;

    @Column(name = "nr_bilete_vandute")
    private Integer nrBileteVandute;

    @Column(name = "suma")
    private Double suma;

    @ElementCollection
    @Column(name = "locuri_vandute")
    @CollectionTable(name = "vanzare_locuri_vandute", joinColumns = @JoinColumn(name = "owner_id"))
    private List<Integer> locuriVandute = new ArrayList<>();

    public List<Integer> getLocuriVandute() {
        return locuriVandute;
    }

    public void setLocuriVandute(List<Integer> locuriVandute) {
        this.locuriVandute = locuriVandute;
    }

    public Double getSuma() {
        return suma;
    }

    public void setSuma(Double suma) {
        this.suma = suma;
    }

    public Integer getNrBileteVandute() {
        return nrBileteVandute;
    }

    public void setNrBileteVandute(Integer nrBileteVandute) {
        this.nrBileteVandute = nrBileteVandute;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}