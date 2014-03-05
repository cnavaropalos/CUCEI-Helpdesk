package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.DepartmentsByModule;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Floor;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class FloorJpaController implements Serializable {

    public FloorJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Floor floor) throws RollbackFailureException, Exception {
        if (floor.getDepartmentsByModuleList() == null) {
            floor.setDepartmentsByModuleList(new ArrayList<DepartmentsByModule>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<DepartmentsByModule> attachedDepartmentsByModuleList = new ArrayList<DepartmentsByModule>();
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModuleToAttach : floor.getDepartmentsByModuleList()) {
                departmentsByModuleListDepartmentsByModuleToAttach = em.getReference(departmentsByModuleListDepartmentsByModuleToAttach.getClass(), departmentsByModuleListDepartmentsByModuleToAttach.getDepartmentsByModulePK());
                attachedDepartmentsByModuleList.add(departmentsByModuleListDepartmentsByModuleToAttach);
            }
            floor.setDepartmentsByModuleList(attachedDepartmentsByModuleList);
            em.persist(floor);
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModule : floor.getDepartmentsByModuleList()) {
                Floor oldFloorIDOfDepartmentsByModuleListDepartmentsByModule = departmentsByModuleListDepartmentsByModule.getFloorID();
                departmentsByModuleListDepartmentsByModule.setFloorID(floor);
                departmentsByModuleListDepartmentsByModule = em.merge(departmentsByModuleListDepartmentsByModule);
                if (oldFloorIDOfDepartmentsByModuleListDepartmentsByModule != null) {
                    oldFloorIDOfDepartmentsByModuleListDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListDepartmentsByModule);
                    oldFloorIDOfDepartmentsByModuleListDepartmentsByModule = em.merge(oldFloorIDOfDepartmentsByModuleListDepartmentsByModule);
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

    public void edit(Floor floor) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Floor persistentFloor = em.find(Floor.class, floor.getFloorID());
            List<DepartmentsByModule> departmentsByModuleListOld = persistentFloor.getDepartmentsByModuleList();
            List<DepartmentsByModule> departmentsByModuleListNew = floor.getDepartmentsByModuleList();
            List<String> illegalOrphanMessages = null;
            for (DepartmentsByModule departmentsByModuleListOldDepartmentsByModule : departmentsByModuleListOld) {
                if (!departmentsByModuleListNew.contains(departmentsByModuleListOldDepartmentsByModule)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DepartmentsByModule " + departmentsByModuleListOldDepartmentsByModule + " since its floorID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DepartmentsByModule> attachedDepartmentsByModuleListNew = new ArrayList<DepartmentsByModule>();
            for (DepartmentsByModule departmentsByModuleListNewDepartmentsByModuleToAttach : departmentsByModuleListNew) {
                departmentsByModuleListNewDepartmentsByModuleToAttach = em.getReference(departmentsByModuleListNewDepartmentsByModuleToAttach.getClass(), departmentsByModuleListNewDepartmentsByModuleToAttach.getDepartmentsByModulePK());
                attachedDepartmentsByModuleListNew.add(departmentsByModuleListNewDepartmentsByModuleToAttach);
            }
            departmentsByModuleListNew = attachedDepartmentsByModuleListNew;
            floor.setDepartmentsByModuleList(departmentsByModuleListNew);
            floor = em.merge(floor);
            for (DepartmentsByModule departmentsByModuleListNewDepartmentsByModule : departmentsByModuleListNew) {
                if (!departmentsByModuleListOld.contains(departmentsByModuleListNewDepartmentsByModule)) {
                    Floor oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule = departmentsByModuleListNewDepartmentsByModule.getFloorID();
                    departmentsByModuleListNewDepartmentsByModule.setFloorID(floor);
                    departmentsByModuleListNewDepartmentsByModule = em.merge(departmentsByModuleListNewDepartmentsByModule);
                    if (oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule != null && !oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule.equals(floor)) {
                        oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListNewDepartmentsByModule);
                        oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule = em.merge(oldFloorIDOfDepartmentsByModuleListNewDepartmentsByModule);
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
                Integer id = floor.getFloorID();
                if (findFloor(id) == null) {
                    throw new NonexistentEntityException("The floor with id " + id + " no longer exists.");
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
            Floor floor;
            try {
                floor = em.getReference(Floor.class, id);
                floor.getFloorID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The floor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DepartmentsByModule> departmentsByModuleListOrphanCheck = floor.getDepartmentsByModuleList();
            for (DepartmentsByModule departmentsByModuleListOrphanCheckDepartmentsByModule : departmentsByModuleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Floor (" + floor + ") cannot be destroyed since the DepartmentsByModule " + departmentsByModuleListOrphanCheckDepartmentsByModule + " in its departmentsByModuleList field has a non-nullable floorID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(floor);
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

    public List<Floor> findFloorEntities() {
        return findFloorEntities(true, -1, -1);
    }

    public List<Floor> findFloorEntities(int maxResults, int firstResult) {
        return findFloorEntities(false, maxResults, firstResult);
    }

    private List<Floor> findFloorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Floor.class));
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

    public Floor findFloor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Floor.class, id);
        } finally {
            em.close();
        }
    }

    public int getFloorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Floor> rt = cq.from(Floor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
