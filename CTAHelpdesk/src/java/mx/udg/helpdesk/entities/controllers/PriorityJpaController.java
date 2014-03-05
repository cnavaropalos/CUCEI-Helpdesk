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
import mx.udg.helpdesk.entities.Priority;
import mx.udg.helpdesk.entities.ProblemCategorie;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class PriorityJpaController implements Serializable {

    public PriorityJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Priority priority) throws RollbackFailureException, Exception {
        if (priority.getDepartmentsByModuleList() == null) {
            priority.setDepartmentsByModuleList(new ArrayList<DepartmentsByModule>());
        }
        if (priority.getProblemCategorieList() == null) {
            priority.setProblemCategorieList(new ArrayList<ProblemCategorie>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<DepartmentsByModule> attachedDepartmentsByModuleList = new ArrayList<DepartmentsByModule>();
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModuleToAttach : priority.getDepartmentsByModuleList()) {
                departmentsByModuleListDepartmentsByModuleToAttach = em.getReference(departmentsByModuleListDepartmentsByModuleToAttach.getClass(), departmentsByModuleListDepartmentsByModuleToAttach.getDepartmentsByModulePK());
                attachedDepartmentsByModuleList.add(departmentsByModuleListDepartmentsByModuleToAttach);
            }
            priority.setDepartmentsByModuleList(attachedDepartmentsByModuleList);
            List<ProblemCategorie> attachedProblemCategorieList = new ArrayList<ProblemCategorie>();
            for (ProblemCategorie problemCategorieListProblemCategorieToAttach : priority.getProblemCategorieList()) {
                problemCategorieListProblemCategorieToAttach = em.getReference(problemCategorieListProblemCategorieToAttach.getClass(), problemCategorieListProblemCategorieToAttach.getProblemID());
                attachedProblemCategorieList.add(problemCategorieListProblemCategorieToAttach);
            }
            priority.setProblemCategorieList(attachedProblemCategorieList);
            em.persist(priority);
            for (DepartmentsByModule departmentsByModuleListDepartmentsByModule : priority.getDepartmentsByModuleList()) {
                Priority oldPriorityIDOfDepartmentsByModuleListDepartmentsByModule = departmentsByModuleListDepartmentsByModule.getPriorityID();
                departmentsByModuleListDepartmentsByModule.setPriorityID(priority);
                departmentsByModuleListDepartmentsByModule = em.merge(departmentsByModuleListDepartmentsByModule);
                if (oldPriorityIDOfDepartmentsByModuleListDepartmentsByModule != null) {
                    oldPriorityIDOfDepartmentsByModuleListDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListDepartmentsByModule);
                    oldPriorityIDOfDepartmentsByModuleListDepartmentsByModule = em.merge(oldPriorityIDOfDepartmentsByModuleListDepartmentsByModule);
                }
            }
            for (ProblemCategorie problemCategorieListProblemCategorie : priority.getProblemCategorieList()) {
                Priority oldPriorityIDOfProblemCategorieListProblemCategorie = problemCategorieListProblemCategorie.getPriorityID();
                problemCategorieListProblemCategorie.setPriorityID(priority);
                problemCategorieListProblemCategorie = em.merge(problemCategorieListProblemCategorie);
                if (oldPriorityIDOfProblemCategorieListProblemCategorie != null) {
                    oldPriorityIDOfProblemCategorieListProblemCategorie.getProblemCategorieList().remove(problemCategorieListProblemCategorie);
                    oldPriorityIDOfProblemCategorieListProblemCategorie = em.merge(oldPriorityIDOfProblemCategorieListProblemCategorie);
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

    public void edit(Priority priority) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Priority persistentPriority = em.find(Priority.class, priority.getPriorityID());
            List<DepartmentsByModule> departmentsByModuleListOld = persistentPriority.getDepartmentsByModuleList();
            List<DepartmentsByModule> departmentsByModuleListNew = priority.getDepartmentsByModuleList();
            List<ProblemCategorie> problemCategorieListOld = persistentPriority.getProblemCategorieList();
            List<ProblemCategorie> problemCategorieListNew = priority.getProblemCategorieList();
            List<String> illegalOrphanMessages = null;
            for (DepartmentsByModule departmentsByModuleListOldDepartmentsByModule : departmentsByModuleListOld) {
                if (!departmentsByModuleListNew.contains(departmentsByModuleListOldDepartmentsByModule)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DepartmentsByModule " + departmentsByModuleListOldDepartmentsByModule + " since its priorityID field is not nullable.");
                }
            }
            for (ProblemCategorie problemCategorieListOldProblemCategorie : problemCategorieListOld) {
                if (!problemCategorieListNew.contains(problemCategorieListOldProblemCategorie)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain ProblemCategorie " + problemCategorieListOldProblemCategorie + " since its priorityID field is not nullable.");
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
            priority.setDepartmentsByModuleList(departmentsByModuleListNew);
            List<ProblemCategorie> attachedProblemCategorieListNew = new ArrayList<ProblemCategorie>();
            for (ProblemCategorie problemCategorieListNewProblemCategorieToAttach : problemCategorieListNew) {
                problemCategorieListNewProblemCategorieToAttach = em.getReference(problemCategorieListNewProblemCategorieToAttach.getClass(), problemCategorieListNewProblemCategorieToAttach.getProblemID());
                attachedProblemCategorieListNew.add(problemCategorieListNewProblemCategorieToAttach);
            }
            problemCategorieListNew = attachedProblemCategorieListNew;
            priority.setProblemCategorieList(problemCategorieListNew);
            priority = em.merge(priority);
            for (DepartmentsByModule departmentsByModuleListNewDepartmentsByModule : departmentsByModuleListNew) {
                if (!departmentsByModuleListOld.contains(departmentsByModuleListNewDepartmentsByModule)) {
                    Priority oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule = departmentsByModuleListNewDepartmentsByModule.getPriorityID();
                    departmentsByModuleListNewDepartmentsByModule.setPriorityID(priority);
                    departmentsByModuleListNewDepartmentsByModule = em.merge(departmentsByModuleListNewDepartmentsByModule);
                    if (oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule != null && !oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule.equals(priority)) {
                        oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule.getDepartmentsByModuleList().remove(departmentsByModuleListNewDepartmentsByModule);
                        oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule = em.merge(oldPriorityIDOfDepartmentsByModuleListNewDepartmentsByModule);
                    }
                }
            }
            for (ProblemCategorie problemCategorieListNewProblemCategorie : problemCategorieListNew) {
                if (!problemCategorieListOld.contains(problemCategorieListNewProblemCategorie)) {
                    Priority oldPriorityIDOfProblemCategorieListNewProblemCategorie = problemCategorieListNewProblemCategorie.getPriorityID();
                    problemCategorieListNewProblemCategorie.setPriorityID(priority);
                    problemCategorieListNewProblemCategorie = em.merge(problemCategorieListNewProblemCategorie);
                    if (oldPriorityIDOfProblemCategorieListNewProblemCategorie != null && !oldPriorityIDOfProblemCategorieListNewProblemCategorie.equals(priority)) {
                        oldPriorityIDOfProblemCategorieListNewProblemCategorie.getProblemCategorieList().remove(problemCategorieListNewProblemCategorie);
                        oldPriorityIDOfProblemCategorieListNewProblemCategorie = em.merge(oldPriorityIDOfProblemCategorieListNewProblemCategorie);
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
                Integer id = priority.getPriorityID();
                if (findPriority(id) == null) {
                    throw new NonexistentEntityException("The priority with id " + id + " no longer exists.");
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
            Priority priority;
            try {
                priority = em.getReference(Priority.class, id);
                priority.getPriorityID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The priority with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DepartmentsByModule> departmentsByModuleListOrphanCheck = priority.getDepartmentsByModuleList();
            for (DepartmentsByModule departmentsByModuleListOrphanCheckDepartmentsByModule : departmentsByModuleListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Priority (" + priority + ") cannot be destroyed since the DepartmentsByModule " + departmentsByModuleListOrphanCheckDepartmentsByModule + " in its departmentsByModuleList field has a non-nullable priorityID field.");
            }
            List<ProblemCategorie> problemCategorieListOrphanCheck = priority.getProblemCategorieList();
            for (ProblemCategorie problemCategorieListOrphanCheckProblemCategorie : problemCategorieListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Priority (" + priority + ") cannot be destroyed since the ProblemCategorie " + problemCategorieListOrphanCheckProblemCategorie + " in its problemCategorieList field has a non-nullable priorityID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(priority);
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

    public List<Priority> findPriorityEntities() {
        return findPriorityEntities(true, -1, -1);
    }

    public List<Priority> findPriorityEntities(int maxResults, int firstResult) {
        return findPriorityEntities(false, maxResults, firstResult);
    }

    private List<Priority> findPriorityEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Priority.class));
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

    public Priority findPriority(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Priority.class, id);
        } finally {
            em.close();
        }
    }

    public int getPriorityCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Priority> rt = cq.from(Priority.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
