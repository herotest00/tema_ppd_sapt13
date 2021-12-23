package org.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "sala")
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nr_locuri")
    private Integer nrLocuri;

    @OneToMany(mappedBy = "sala")
    private List<Spectacol> spectacole = new ArrayList<>();

    @OneToMany(mappedBy = "sala")
    private List<Vanzare> vanzares = new ArrayList<>();

    public Sala(Integer nrLocuri, List<Spectacol> spectacole, List<Vanzare> vanzares) {
        this.nrLocuri = nrLocuri;
        this.spectacole = spectacole;
        this.vanzares = vanzares;
    }

    public Sala(Integer id, Integer nrLocuri, List<Spectacol> spectacole, List<Vanzare> vanzares) {
        this.id = id;
        this.nrLocuri = nrLocuri;
        this.spectacole = spectacole;
        this.vanzares = vanzares;
    }

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

    public Sala() { }

    public Sala(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.nrLocuri = jsonObject.getInt("nrLocuri");
//        JSONArray spectaclesJsonArray = jsonObject.getJSONArray("spectacole");
//        for (int i = 0; i < spectaclesJsonArray.length(); i++) {
//            this.spectacole.add(new Spectacol(spectaclesJsonArray.getJSONObject(i)));
//        }
//        JSONArray vanzaresJsonArray = jsonObject.getJSONArray("vanzares");
//        for (int i = 0; i < vanzaresJsonArray.length(); i++) {
//            this.vanzares.add(new Vanzare(vanzaresJsonArray.getJSONObject(i)));
//        }
    }

    public JSONObject toJson() {
        JSONArray spectacole = new JSONArray();
//        this.spectacole.stream().forEach(x -> spectacole.put(x.toJson()));
        JSONArray vanzari = new JSONArray();
//        this.vanzares.stream().forEach(x -> spectacole.put(x.toJson()));
        return new JSONObject(Map.ofEntries(
                Map.entry("id", this.id),
                Map.entry("nrLocuri", this.nrLocuri)
//                Map.entry("spectacole", spectacole),
//                Map.entry("vanzares", vanzari)
        ));
    }
}