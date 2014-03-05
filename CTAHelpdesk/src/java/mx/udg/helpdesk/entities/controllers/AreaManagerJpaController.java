package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.User;
import mx.udg.helpdesk.entities.Area;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.AreaManager;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class AreaManagerJpaController implements Serializable {

    public AreaManagerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AreaManager areaManager) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (areaManager.getAreaList() == null) {
            areaManager.setAreaList(new ArrayList<Area>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = areaManager.getUser();
        if (userOrphanCheck != null) {
            AreaManager oldAreaManagerOfUser = userOrphanCheck.getAreaManager();
            if (oldAreaManagerOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type AreaManager whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = areaManager.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUserID());
                areaManager.setUser(user);
            }
            List<Area> attachedAreaList = new ArrayList<Area>();
            for (Area areaListAreaToAttach : areaManager.getAreaList()) {
                areaListAreaToAttach = em.getReference(areaListAreaToAttach.getClass(), areaListAreaToAttach.getAreaID());
                attachedAreaList.add(areaListAreaToAttach);
            }
            areaManager.setAreaList(attachedAreaList);
            em.persist(areaManager);
            if (user != null) {
                user.setAreaManager(areaManager);
                user = em.merge(user);
            }
            for (Area areaListArea : areaManager.getAreaList()) {
                AreaManager oldAreaManagerIDOfAreaListArea = areaListArea.getAreaManagerID();
                areaListArea.setAreaManagerID(areaManager);
                areaListArea = em.merge(areaListArea);
                if (oldAreaManagerIDOfAreaListArea != null) {
                    oldAreaManagerIDOfAreaListArea.getAreaList().remove(areaListArea);
                    oldAreaManagerIDOfAreaListArea = em.merge(oldAreaManagerIDOfAreaListArea);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAreaManager(areaManager.getUserID()) != null) {
                throw new PreexistingEntityException("AreaManager " + areaManager + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AreaManager areaManager) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AreaManager persistentAreaManager = em.find(AreaManager.class, areaManager.getUserID());
            User userOld = persistentAreaManager.getUser();
            User userNew = areaManager.getUser();
            List<Area> areaListOld = persistentAreaManager.getAreaList();
            List<Area> areaListNew = areaManager.getAreaList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                AreaManager oldAreaManagerOfUser = userNew.getAreaManager();
                if (oldAreaManagerOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type AreaManager whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            for (Area areaListOldArea : areaListOld) {
                if (!areaListNew.contains(areaListOldArea)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Area " + areaListOldArea + " since its areaManagerID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserID());
                areaManager.setUser(userNew);
            }
            List<Area> attachedAreaListNew = new ArrayList<Area>();
            for (Area areaListNewAreaToAttach : areaListNew) {
                areaListNewAreaToAttach = em.getReference(areaListNewAreaToAttach.getClass(), areaListNewAreaToAttach.getAreaID());
                attachedAreaListNew.add(areaListNewAreaToAttach);
            }
            areaListNew = attachedAreaListNew;
            areaManager.setAreaList(areaListNew);
            areaManager = em.merge(areaManager);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setAreaManager(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setAreaManager(areaManager);
                userNew = em.merge(userNew);
            }
            for (Area areaListNewArea : areaListNew) {
                if (!areaListOld.contains(areaListNewArea)) {
                    AreaManager oldAreaManagerIDOfAreaListNewArea = areaListNewArea.getAreaManagerID();
                    areaListNewArea.setAreaManagerID(areaManager);
                    areaListNewArea = em.merge(areaListNewArea);
                    if (oldAreaManagerIDOfAreaListNewArea != null && !oldAreaManagerIDOfAreaListNewArea.equals(areaManager)) {
                        oldAreaManagerIDOfAreaListNewArea.getAreaList().remove(areaListNewArea);
                        oldAreaManagerIDOfAreaListNewArea = em.merge(oldAreaManagerIDOfAreaListNewArea);
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
                String id = areaManager.getUserID();
                if (findAreaManager(id) == null) {
                    throw new NonexistentEntityException("The areaManager with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            AreaManager areaManager;
            try {
                areaManager = em.getReference(AreaManager.class, id);
                areaManager.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The areaManager with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Area> areaListOrphanCheck = areaManager.getAreaList();
            for (Area areaListOrphanCheckArea : areaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This AreaManager (" + areaManager + ") cannot be destroyed since the Area " + areaListOrphanCheckArea + " in its areaList field has a non-nullable areaManagerID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User user = areaManager.getUser();
            if (user != null) {
                user.setAreaManager(null);
                user = em.merge(user);
            }
            em.remove(areaManager);
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

    public List<AreaManager> findAreaManagerEntities() {
        return findAreaManagerEntities(true, -1, -1);
    }

    public List<AreaManager> findAreaManagerEntities(int maxResults, int firstResult) {
        return findAreaManagerEntities(false, maxResults, firstResult);
    }

    private List<AreaManager> findAreaManagerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AreaManager.class));
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

    public AreaManager findAreaManager(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AreaManager.class, id);
        } finally {
            em.close();
        }
    }

    public int getAreaManagerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AreaManager> rt = cq.from(AreaManager.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
