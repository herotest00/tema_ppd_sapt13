package org.component;

import org.IService;
import org.component.repo.SalaRepository;
import org.component.repo.SpectacolRepository;
import org.component.repo.VanzareRepository;
import org.domain.Spectacol;
import org.domain.Vanzare;

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


    public List<Spectacol> getAllSpectacole() {
        return spectacolRepository.findAll();
    }

    @Override
    synchronized public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
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

    @Override
    synchronized public Vanzare rezerva(Spectacol spectacol, List<Integer> locuri) {
        System.out.println("rezerva");
        try {
            List<Integer> locuriDisponibile = getAllLocuriDisponibile(spectacol);
            if (locuriDisponibile.size() < locuri.size() || !Collections.disjoint(locuriDisponibile, locuri)) {
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
            throw new RuntimeException("Vanzare nereusita!");
        }
    }

    synchronized public boolean validateData() {
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
                        return false;
                    }
                    nrLocuri = locuriVanduteSpectacol.size();
                    sold -= vanzare.getSuma();
                }
            }
            if (sold != 0) {
                return false;
            }
        }
        return true;
    }
}
