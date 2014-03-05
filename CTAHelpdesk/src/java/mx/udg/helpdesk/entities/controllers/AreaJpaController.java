package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.AreaManager;
import mx.udg.helpdesk.entities.ProblemCategorie;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Area;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class AreaJpaController implements Serializable {

    public AreaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Area area) throws RollbackFailureException, Exception {
        if (area.getProblemCategorieList() == null) {
            area.setProblemCategorieList(new ArrayList<ProblemCategorie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AreaManager areaManagerID = area.getAreaManagerID();
            if (areaManagerID != null) {
                areaManagerID = em.getReference(areaManagerID.getClass(), areaManagerID.getUserID());
                area.setAreaManagerID(areaManagerID);
            }
            List<ProblemCategorie> attachedProblemCategorieList = new ArrayList<ProblemCategorie>();
            for (ProblemCategorie problemCategorieListProblemCategorieToAttach : area.getProblemCategorieList()) {
                problemCategorieListProblemCategorieToAttach = em.getReference(problemCategorieListProblemCategorieToAttach.getClass(), problemCategorieListProblemCategorieToAttach.getProblemID());
                attachedProblemCategorieList.add(problemCategorieListProblemCategorieToAttach);
            }
            area.setProblemCategorieList(attachedProblemCategorieList);
            em.persist(area);
            if (areaManagerID != null) {
                areaManagerID.getAreaList().add(area);
                areaManagerID = em.merge(areaManagerID);
            }
            for (ProblemCategorie problemCategorieListProblemCategorie : area.getProblemCategorieList()) {
                Area oldAreaIDOfProblemCategorieListProblemCategorie = problemCategorieListProblemCategorie.getAreaID();
                problemCategorieListProblemCategorie.setAreaID(area);
                problemCategorieListProblemCategorie = em.merge(problemCategorieListProblemCategorie);
                if (oldAreaIDOfProblemCategorieListProblemCategorie != null) {
                    oldAreaIDOfProblemCategorieListProblemCategorie.getProblemCategorieList().remove(problemCategorieListProblemCategorie);
                    oldAreaIDOfProblemCategorieListProblemCategorie = em.merge(oldAreaIDOfProblemCategorieListProblemCategorie);
                }
            }
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

    public void edit(Area area) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Area persistentArea = em.find(Area.class, area.getAreaID());
            AreaManager areaManagerIDOld = persistentArea.getAreaManagerID();
            AreaManager areaManagerIDNew = area.getAreaManagerID();
            List<ProblemCategorie> problemCategorieListOld = persistentArea.getProblemCategorieList();
            List<ProblemCategorie> problemCategorieListNew = area.getProblemCategorieList();
            List<String> illegalOrphanMessages = null;
            for (ProblemCategorie problemCategorieListOldProblemCategorie : problemCategorieListOld) {
                if (!problemCategorieListNew.contains(problemCategorieListOldProblemCategorie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProblemCategorie " + problemCategorieListOldProblemCategorie + " since its areaID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (areaManagerIDNew != null) {
                areaManagerIDNew = em.getReference(areaManagerIDNew.getClass(), areaManagerIDNew.getUserID());
                area.setAreaManagerID(areaManagerIDNew);
            }
            List<ProblemCategorie> attachedProblemCategorieListNew = new ArrayList<ProblemCategorie>();
            for (ProblemCategorie problemCategorieListNewProblemCategorieToAttach : problemCategorieListNew) {
                problemCategorieListNewProblemCategorieToAttach = em.getReference(problemCategorieListNewProblemCategorieToAttach.getClass(), problemCategorieListNewProblemCategorieToAttach.getProblemID());
                attachedProblemCategorieListNew.add(problemCategorieListNewProblemCategorieToAttach);
            }
            problemCategorieListNew = attachedProblemCategorieListNew;
            area.setProblemCategorieList(problemCategorieListNew);
            area = em.merge(area);
            if (areaManagerIDOld != null && !areaManagerIDOld.equals(areaManagerIDNew)) {
                areaManagerIDOld.getAreaList().remove(area);
                areaManagerIDOld = em.merge(areaManagerIDOld);
            }
            if (areaManagerIDNew != null && !areaManagerIDNew.equals(areaManagerIDOld)) {
                areaManagerIDNew.getAreaList().add(area);
                areaManagerIDNew = em.merge(areaManagerIDNew);
            }
            for (ProblemCategorie problemCategorieListNewProblemCategorie : problemCategorieListNew) {
                if (!problemCategorieListOld.contains(problemCategorieListNewProblemCategorie)) {
                    Area oldAreaIDOfProblemCategorieListNewProblemCategorie = problemCategorieListNewProblemCategorie.getAreaID();
                    problemCategorieListNewProblemCategorie.setAreaID(area);
                    problemCategorieListNewProblemCategorie = em.merge(problemCategorieListNewProblemCategorie);
                    if (oldAreaIDOfProblemCategorieListNewProblemCategorie != null && !oldAreaIDOfProblemCategorieListNewProblemCategorie.equals(area)) {
                        oldAreaIDOfProblemCategorieListNewProblemCategorie.getProblemCategorieList().remove(problemCategorieListNewProblemCategorie);
                        oldAreaIDOfProblemCategorieListNewProblemCategorie = em.merge(oldAreaIDOfProblemCategorieListNewProblemCategorie);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = area.getAreaID();
                if (findArea(id) == null) {
                    throw new NonexistentEntityException("The area with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Area area;
            try {
                area = em.getReference(Area.class, id);
                area.getAreaID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The area with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<ProblemCategorie> problemCategorieListOrphanCheck = area.getProblemCategorieList();
            for (ProblemCategorie problemCategorieListOrphanCheckProblemCategorie : problemCategorieListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Area (" + area + ") cannot be destroyed since the ProblemCategorie " + problemCategorieListOrphanCheckProblemCategorie + " in its problemCategorieList field has a non-nullable areaID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            AreaManager areaManagerID = area.getAreaManagerID();
            if (areaManagerID != null) {
                areaManagerID.getAreaList().remove(area);
                areaManagerID = em.merge(areaManagerID);
            }
            em.remove(area);
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

    public List<Area> findAreaEntities() {
        return findAreaEntities(true, -1, -1);
    }

    public List<Area> findAreaEntities(int maxResults, int firstResult) {
        return findAreaEntities(false, maxResults, firstResult);
    }

    private List<Area> findAreaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Area.class));
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

    public Area findArea(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Area.class, id);
        } finally {
            em.close();
        }
    }

    public int getAreaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Area> rt = cq.from(Area.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
