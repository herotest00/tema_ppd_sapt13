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

    public Spectacol(){ }

    public Spectacol(JSONObject jsonObject) {
        this.id = jsonObject.getInt("id");
        this.titlu = jsonObject.getString("titlu");
        this.pret = jsonObject.getDouble("pret");
        this.sold = jsonObject.getDouble("sold");
        try {
            this.data = DateFormat.getDateInstance().parse(jsonObject.getString("data"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.sala = new Sala(jsonObject.getJSONObject("sala"));
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
                Map.entry("titlu", this.titlu),
                Map.entry("pret", this.pret),
                Map.entry("sold", this.sold),
                Map.entry("data", this.data.toString()),
                Map.entry("sala", this.sala.toJson()),
                Map.entry("locuriVandute", array)
        ));
    }
}