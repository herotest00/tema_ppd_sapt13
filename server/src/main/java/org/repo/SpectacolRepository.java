package org.repo;

import org.domain.Spectacol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpectacolRepository extends JpaRepository<Spectacol, Integer> {
}
