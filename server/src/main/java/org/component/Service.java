package org.component;

import org.IService;
import org.component.repo.SalaRepository;
import org.component.repo.SpectacolRepository;
import org.component.repo.VanzareRepository;
import org.domain.Spectacol;
import org.domain.Vanzare;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service
public class Service implements IService {

    public final SpectacolRepository spectacolRepository;
    private final SalaRepository salaRepository;
    private final VanzareRepository vanzareRepository;

    public Service(SalaRepository salaRepository, SpectacolRepository spectacolRepository, VanzareRepository vanzareRepository) {
        this.salaRepository = salaRepository;
        this.spectacolRepository = spectacolRepository;
        this.vanzareRepository = vanzareRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @Override
    public List<Spectacol> getAllSpectacole() {
        System.out.println("getSpectacole");
        try {
            var a = spectacolRepository.findAll();
            return a;
        } catch (Exception ex) {
            System.out.println(ex.getMessage() + ex.getCause());
        }
        System.out.println("done getSpectacole");
        return null;
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    @Override
    public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
        System.out.println("getAllLocuriDisponibile");
        Optional<Spectacol> optSpectacol = spectacolRepository.findById(spectacol.getId());
        if (optSpectacol.isEmpty()) {
            return new ArrayList<>();
        }

        Spectacol spectacol1 = optSpectacol.get();
        List<Integer> locuriDisponibile = new ArrayList<>();
        for (int i = 1; i <= spectacol1.getSala().getNrLocuri(); i++) {
            locuriDisponibile.add(i);
        }
        locuriDisponibile.removeAll(optSpectacol.get().getLocuriVandute());
        return locuriDisponibile;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.REPEATABLE_READ)
    @Override
    public Vanzare rezerva(Spectacol spectacol, List<Integer> locuri) {
        System.out.println("rezerva");
        try {
            List<Integer> locuriDisponibile = getAllLocuriDisponibile(spectacol);
            if (locuriDisponibile.size() < locuri.size() || !locuriDisponibile.containsAll(locuri)) {
                throw new Exception();
            }
            double suma = locuri.size() * spectacol.getPret();
            Vanzare vanzare = new Vanzare(new Date(), spectacol.getSala(), locuri.size(), suma, locuri, spectacol);
            vanzareRepository.save(vanzare);
            spectacolRepository.findById(spectacol.getId())
                    .map(spectacol1 -> {
                        spectacol1.getLocuriVandute().addAll(locuri);
                        spectacol1.setSold(spectacol1.getSold() + suma);
                        return spectacolRepository.save(spectacol1);
                    });
            salaRepository.findById(spectacol.getSala().getId())
                    .map(sala -> {
                        sala.getVanzares().add(vanzare);
                        return salaRepository.save(sala);
                    });
            return vanzare;
        } catch (Exception ex) {
            System.out.println("Exceptie ARUNCATA");
            throw new RuntimeException("Vanzare nereusita!");
        }
    }

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public JSONObject validateData() {
        JSONObject json = new JSONObject(
                Map.ofEntries(
                        Map.entry("date", LocalDateTime.now())
                )
        );
        System.out.println("validateData");
        List<Vanzare> vanzari = vanzareRepository.findAll();
        List<Spectacol> spectacole = spectacolRepository.findAll();
        for (Spectacol spectacol : spectacole) {
            List<Integer> locuriVanduteSpectacol = new ArrayList<>(spectacol.getLocuriVandute());
            int nrLocuri = locuriVanduteSpectacol.size();
            Double sold = spectacol.getSold();
            for (Vanzare vanzare : vanzari) {
                if (vanzare.getSpectacol().getId().equals(spectacol.getId())) {
                    locuriVanduteSpectacol.removeAll(vanzare.getLocuriVandute());
                    if (nrLocuri - vanzare.getLocuriVandute().size() != locuriVanduteSpectacol.size()) {
                        return null;
                    }
                    nrLocuri = locuriVanduteSpectacol.size();
                    sold -= vanzare.getSuma();
                }
            }
            json.put(spectacol.getTitlu(), new JSONObject(
                    Map.ofEntries(
                            Map.entry("sold", spectacol.getSold()),
                            Map.entry("locuri_vandute", spectacol.getLocuriVandute())
                    )
            ));
            if (sold != 0) {
                return null;
            }
        }
        return json;
    }
}
