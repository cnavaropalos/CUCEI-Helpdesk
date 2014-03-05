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
import mx.udg.helpdesk.entities.Module;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class ModuleJpaController implements Serializable {

    public ModuleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Module module) throws RollbackFailureException, Exception {
        if (module.getDepartmentsByModuleList() == null) {
            module.setDepartmentsByModuleList(new ArrayList<DepartmentsByModule>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<DepartmentsByModule> attachedDepartmentsByModuleList = new ArrayList<DepartmentsByModule>();
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModuleToAttach : module.getDepartmentsByModuleList()) {
                departmentsByModuleListDepartmentsByModuleToAttach = em.getReference(departmentsByModuleListDepartmentsByModuleToAttach.getClass(), departmentsByModuleListDepartmentsByModuleToAttach.getDepartmentsByModulePK());
                attachedDepartmentsByModuleList.add(departmentsByModuleListDepartmentsByModuleToAttach);
            }
            module.setDepartmentsByModuleList(attachedDepartmentsByModuleList);
            em.persist(module);
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModule : module.getDepartmentsByModuleList()) {
                Module oldModuleOfDepartmentsByModuleListDepartmentsByModule = departmentsByModuleListDepartmentsByModule.getModule();
                departmentsByModuleListDepartmentsByModule.setModule(module);
                departmentsByModuleListDepartmentsByModule = em.merge(departmentsByModuleListDepartmentsByModule);
                if (oldModuleOfDepartmentsByModuleListDepartmentsByModule != null) {
                    oldModuleOfDepartmentsByModuleListDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListDepartmentsByModule);
                    oldModuleOfDepartmentsByModuleListDepartmentsByModule = em.merge(oldModuleOfDepartmentsByModuleListDepartmentsByModule);
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

    public void edit(Module module) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Module persistentModule = em.find(Module.class, module.getModuleID());
            List<DepartmentsByModule> departmentsByModuleListOld = persistentModule.getDepartmentsByModuleList();
            List<DepartmentsByModule> departmentsByModuleListNew = module.getDepartmentsByModuleList();
            List<String> illegalOrphanMessages = null;
            for (DepartmentsByModule departmentsByModuleListOldDepartmentsByModule : departmentsByModuleListOld) {
                if (!departmentsByModuleListNew.contains(departmentsByModuleListOldDepartmentsByModule)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DepartmentsByModule " + departmentsByModuleListOldDepartmentsByModule + " since its module field is not nullable.");
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
            module.setDepartmentsByModuleList(departmentsByModuleListNew);
            module = em.merge(module);
            for (DepartmentsByModule departmentsByModuleListNewDepartmentsByModule : departmentsByModuleListNew) {
                if (!departmentsByModuleListOld.contains(departmentsByModuleListNewDepartmentsByModule)) {
                    Module oldModuleOfDepartmentsByModuleListNewDepartmentsByModule = departmentsByModuleListNewDepartmentsByModule.getModule();
                    departmentsByModuleListNewDepartmentsByModule.setModule(module);
                    departmentsByModuleListNewDepartmentsByModule = em.merge(departmentsByModuleListNewDepartmentsByModule);
                    if (oldModuleOfDepartmentsByModuleListNewDepartmentsByModule != null && !oldModuleOfDepartmentsByModuleListNewDepartmentsByModule.equals(module)) {
                        oldModuleOfDepartmentsByModuleListNewDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListNewDepartmentsByModule);
                        oldModuleOfDepartmentsByModuleListNewDepartmentsByModule = em.merge(oldModuleOfDepartmentsByModuleListNewDepartmentsByModule);
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
                Integer id = module.getModuleID();
                if (findModule(id) == null) {
                    throw new NonexistentEntityException("The module with id " + id + " no longer exists.");
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
            Module module;
            try {
                module = em.getReference(Module.class, id);
                module.getModuleID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The module with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DepartmentsByModule> departmentsByModuleListOrphanCheck = module.getDepartmentsByModuleList();
            for (DepartmentsByModule departmentsByModuleListOrphanCheckDepartmentsByModule : departmentsByModuleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Module (" + module + ") cannot be destroyed since the DepartmentsByModule " + departmentsByModuleListOrphanCheckDepartmentsByModule + " in its departmentsByModuleList field has a non-nullable module field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(module);
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

    public List<Module> findModuleEntities() {
        return findModuleEntities(true, -1, -1);
    }

    public List<Module> findModuleEntities(int maxResults, int firstResult) {
        return findModuleEntities(false, maxResults, firstResult);
    }

    private List<Module> findModuleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Module.class));
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

    public Module findModule(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Module.class, id);
        } finally {
            em.close();
        }
    }

    public int getModuleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Module> rt = cq.from(Module.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
