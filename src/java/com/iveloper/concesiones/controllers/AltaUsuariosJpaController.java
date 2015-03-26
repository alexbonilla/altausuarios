/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.concesiones.controllers;

import com.iveloper.concesiones.controllers.exceptions.NonexistentEntityException;
import com.iveloper.concesiones.controllers.exceptions.PreexistingEntityException;
import com.iveloper.concesiones.controllers.exceptions.RollbackFailureException;
import com.iveloper.concesiones.consultasweb.entities.AltaUsuarios;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author alexbonilla
 */
public class AltaUsuariosJpaController implements Serializable {

    public AltaUsuariosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AltaUsuarios altaUsuarios) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            em.persist(altaUsuarios);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAltaUsuarios(altaUsuarios.getUsuario()) != null) {
                throw new PreexistingEntityException("AltaUsuarios " + altaUsuarios + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AltaUsuarios altaUsuarios) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            altaUsuarios = em.merge(altaUsuarios);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = altaUsuarios.getUsuario();
                if (findAltaUsuarios(id) == null) {
                    throw new NonexistentEntityException("The altaUsuarios with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AltaUsuarios altaUsuarios;
            try {
                altaUsuarios = em.getReference(AltaUsuarios.class, id);
                altaUsuarios.getUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The altaUsuarios with id " + id + " no longer exists.", enfe);
            }
            em.remove(altaUsuarios);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AltaUsuarios> findAltaUsuariosEntities() {
        return findAltaUsuariosEntities(true, -1, -1);
    }

    public List<AltaUsuarios> findAltaUsuariosEntities(int maxResults, int firstResult) {
        return findAltaUsuariosEntities(false, maxResults, firstResult);
    }

    private List<AltaUsuarios> findAltaUsuariosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AltaUsuarios.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public AltaUsuarios findAltaUsuarios(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AltaUsuarios.class, id);
        } finally {
            em.close();
        }
    }

    public int getAltaUsuariosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AltaUsuarios> rt = cq.from(AltaUsuarios.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public boolean validateCredentials(String user, String pwd) {

        AltaUsuarios validatingUser = findAltaUsuarios(user);
        boolean validation = false;
        if (validatingUser != null) {
            validation = validatingUser.getAlta() && pwd.equals(validatingUser.getClave());
        }
        return validation;
    }

    public List<AltaUsuarios> findAdmins() {
        Query query = getEntityManager().createNamedQuery("AltaUsuarios.findByEsAdmin", com.iveloper.concesiones.consultasweb.entities.AltaUsuarios.class);
        query.setParameter("esAdmin", true);
        return query.getResultList();
    }

}
