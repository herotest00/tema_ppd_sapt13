package org;

import org.domain.Spectacol;
import org.domain.Vanzare;
import org.repo.SalaRepository;
import org.repo.SpectacolRepository;
import org.repo.VanzareRepository;

import java.util.*;

@org.springframework.stereotype.Service
public class Service implements IService {

    private final SalaRepository salaRepository;
    private final SpectacolRepository spectacolRepository;
    private final VanzareRepository vanzareRepository;

    public Service(SalaRepository salaRepository, SpectacolRepository spectacolRepository, VanzareRepository vanzareRepository) {
        this.salaRepository = salaRepository;
        this.spectacolRepository = spectacolRepository;
        this.vanzareRepository = vanzareRepository;
    }

    @Override
    public List<Spectacol> getAllSpectacole() {
        return spectacolRepository.findAll();
    }

    @Override
    public List<Integer> getAllLocuriDisponibile(Spectacol spectacol) {
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
    public Vanzare rezerva(Spectacol spectacol, List<Integer> locuri) {
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
}
