package org;

import org.domain.Spectacol;
import org.domain.Vanzare;

import java.util.List;

public interface IService {
    List<Spectacol> getAllSpectacole();

    List<Integer> getAllLocuriDisponibile(Spectacol spectacol);

    Vanzare rezerva(Spectacol spectacol, List<Integer> locuri);
}
