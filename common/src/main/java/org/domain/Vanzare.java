package org.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public Vanzare() { }

    public Vanzare(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        try {
            this.data = DateFormat.getDateInstance().parse(jsonObject.getString("data"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.sala = new Sala(jsonObject.getJSONObject("sala"));
        this.nrBileteVandute = jsonObject.getInt("nrBileteVandute");
        this.suma = jsonObject.getDouble("suma");
        JSONArray array = jsonObject.getJSONArray("locuriVandute");
        for (int i = 0; i < array.length(); i++) {
            this.locuriVandute.add(array.getInt(i));
        }
    }

    public JSONObject toJson() {
        JSONArray array = new JSONArray();
        this.locuriVandute.stream().forEach(array::put);
        return new JSONObject(Map.ofEntries(
                Map.entry("id", this.id),
                Map.entry("data", this.data.toString()),
                Map.entry("sala", this.sala.toJson()),
                Map.entry("nrBileteVandute", this.nrBileteVandute),
                Map.entry("suma", this.suma),
                Map.entry("locuriVandute", array)
        ));
    }
}