package it.polito.extgol;

import java.util.List;

import jakarta.persistence.EntityManager;

public class GenerationRepository extends GenericExtGOLRepository<Generation, Long> {
    public GenerationRepository()  { super(Generation.class);  }

    public List<Generation> getGenerations() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return this.getGenerations();
        } finally {
            em.close();
        }
    }
}