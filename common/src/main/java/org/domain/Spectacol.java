package org.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "spectacol")
public class Spectacol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "titlu")
    private String titlu;

    @Column(name = "pret_bilet")
    private Double pret;

    @Column(name = "sold")
    private Double sold;

    @Temporal(TemporalType.DATE)
    @Column(name = "data_spectacol")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "id_sala")
    private Sala sala;

    @ElementCollection
    @Column(name = "locuri_vandute")
    @CollectionTable(name = "spectacol_locuri_vandute", joinColumns = @JoinColumn(name = "owner_id"))
    private List<Integer> locuriVandute = new ArrayList<>();

    public List<Integer> getLocuriVandute() {
        return locuriVandute;
    }

    public void setLocuriVandute(List<Integer> locuriVandute) {
        this.locuriVandute = locuriVandute;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }


    public Double getSold() {
        return sold;
    }

    public void setSold(Double sold) {
        this.sold = sold;
    }

    public Double getPret() {
        return pret;
    }

    public void setPret(Double pret) {
        this.pret = pret;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
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