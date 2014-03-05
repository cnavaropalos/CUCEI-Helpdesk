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
import mx.udg.helpdesk.entities.Department;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class DepartmentJpaController implements Serializable {

    public DepartmentJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Department department) throws RollbackFailureException, Exception {
        if (department.getDepartmentsByModuleList() == null) {
            department.setDepartmentsByModuleList(new ArrayList<DepartmentsByModule>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<DepartmentsByModule> attachedDepartmentsByModuleList = new ArrayList<DepartmentsByModule>();
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModuleToAttach : department.getDepartmentsByModuleList()) {
                departmentsByModuleListDepartmentsByModuleToAttach = em.getReference(departmentsByModuleListDepartmentsByModuleToAttach.getClass(), departmentsByModuleListDepartmentsByModuleToAttach.getDepartmentsByModulePK());
                attachedDepartmentsByModuleList.add(departmentsByModuleListDepartmentsByModuleToAttach);
            }
            department.setDepartmentsByModuleList(attachedDepartmentsByModuleList);
            em.persist(department);
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModule : department.getDepartmentsByModuleList()) {
                Department oldDepartmentOfDepartmentsByModuleListDepartmentsByModule = departmentsByModuleListDepartmentsByModule.getDepartment();
                departmentsByModuleListDepartmentsByModule.setDepartment(department);
                departmentsByModuleListDepartmentsByModule = em.merge(departmentsByModuleListDepartmentsByModule);
                if (oldDepartmentOfDepartmentsByModuleListDepartmentsByModule != null) {
                    oldDepartmentOfDepartmentsByModuleListDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListDepartmentsByModule);
                    oldDepartmentOfDepartmentsByModuleListDepartmentsByModule = em.merge(oldDepartmentOfDepartmentsByModuleListDepartmentsByModule);
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

    public void edit(Department department) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Department persistentDepartment = em.find(Department.class, department.getDepartmentID());
            List<DepartmentsByModule> departmentsByModuleListOld = persistentDepartment.getDepartmentsByModuleList();
            List<DepartmentsByModule> departmentsByModuleListNew = department.getDepartmentsByModuleList();
            List<String> illegalOrphanMessages = null;
            for (DepartmentsByModule departmentsByModuleListOldDepartmentsByModule : departmentsByModuleListOld) {
                if (!departmentsByModuleListNew.contains(departmentsByModuleListOldDepartmentsByModule)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DepartmentsByModule " + departmentsByModuleListOldDepartmentsByModule + " since its department field is not nullable.");
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
            department.setDepartmentsByModuleList(departmentsByModuleListNew);
            department = em.merge(department);
            for (DepartmentsByModule departmentsByModuleListNewDepartmentsByModule : departmentsByModuleListNew) {
                if (!departmentsByModuleListOld.contains(departmentsByModuleListNewDepartmentsByModule)) {
                    Department oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule = departmentsByModuleListNewDepartmentsByModule.getDepartment();
                    departmentsByModuleListNewDepartmentsByModule.setDepartment(department);
                    departmentsByModuleListNewDepartmentsByModule = em.merge(departmentsByModuleListNewDepartmentsByModule);
                    if (oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule != null && !oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule.equals(department)) {
                        oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListNewDepartmentsByModule);
                        oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule = em.merge(oldDepartmentOfDepartmentsByModuleListNewDepartmentsByModule);
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
                Integer id = department.getDepartmentID();
                if (findDepartment(id) == null) {
                    throw new NonexistentEntityException("The department with id " + id + " no longer exists.");
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
            Department department;
            try {
                department = em.getReference(Department.class, id);
                department.getDepartmentID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The department with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DepartmentsByModule> departmentsByModuleListOrphanCheck = department.getDepartmentsByModuleList();
            for (DepartmentsByModule departmentsByModuleListOrphanCheckDepartmentsByModule : departmentsByModuleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Department (" + department + ") cannot be destroyed since the DepartmentsByModule " + departmentsByModuleListOrphanCheckDepartmentsByModule + " in its departmentsByModuleList field has a non-nullable department field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(department);
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

    public List<Department> findDepartmentEntities() {
        return findDepartmentEntities(true, -1, -1);
    }

    public List<Department> findDepartmentEntities(int maxResults, int firstResult) {
        return findDepartmentEntities(false, maxResults, firstResult);
    }

    private List<Department> findDepartmentEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Department.class));
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

    public Department findDepartment(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Department.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartmentCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Department> rt = cq.from(Department.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
