package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.WorkBlog;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Status;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class StatusJpaController implements Serializable {

    public StatusJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Status status) throws RollbackFailureException, Exception {
        if (status.getWorkBlogList() == null) {
            status.setWorkBlogList(new ArrayList<WorkBlog>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<WorkBlog> attachedWorkBlogList = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListWorkBlogToAttach : status.getWorkBlogList()) {
                workBlogListWorkBlogToAttach = em.getReference(workBlogListWorkBlogToAttach.getClass(), workBlogListWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogList.add(workBlogListWorkBlogToAttach);
            }
            status.setWorkBlogList(attachedWorkBlogList);
            em.persist(status);
            for (WorkBlog workBlogListWorkBlog : status.getWorkBlogList()) {
                Status oldStatusIDOfWorkBlogListWorkBlog = workBlogListWorkBlog.getStatusID();
                workBlogListWorkBlog.setStatusID(status);
                workBlogListWorkBlog = em.merge(workBlogListWorkBlog);
                if (oldStatusIDOfWorkBlogListWorkBlog != null) {
                    oldStatusIDOfWorkBlogListWorkBlog.getWorkBlogList().remove(workBlogListWorkBlog);
                    oldStatusIDOfWorkBlogListWorkBlog = em.merge(oldStatusIDOfWorkBlogListWorkBlog);
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

    public void edit(Status status) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status persistentStatus = em.find(Status.class, status.getStatusID());
            List<WorkBlog> workBlogListOld = persistentStatus.getWorkBlogList();
            List<WorkBlog> workBlogListNew = status.getWorkBlogList();
            List<String> illegalOrphanMessages = null;
            for (WorkBlog workBlogListOldWorkBlog : workBlogListOld) {
                if (!workBlogListNew.contains(workBlogListOldWorkBlog)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorkBlog " + workBlogListOldWorkBlog + " since its statusID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<WorkBlog> attachedWorkBlogListNew = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListNewWorkBlogToAttach : workBlogListNew) {
                workBlogListNewWorkBlogToAttach = em.getReference(workBlogListNewWorkBlogToAttach.getClass(), workBlogListNewWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogListNew.add(workBlogListNewWorkBlogToAttach);
            }
            workBlogListNew = attachedWorkBlogListNew;
            status.setWorkBlogList(workBlogListNew);
            status = em.merge(status);
            for (WorkBlog workBlogListNewWorkBlog : workBlogListNew) {
                if (!workBlogListOld.contains(workBlogListNewWorkBlog)) {
                    Status oldStatusIDOfWorkBlogListNewWorkBlog = workBlogListNewWorkBlog.getStatusID();
                    workBlogListNewWorkBlog.setStatusID(status);
                    workBlogListNewWorkBlog = em.merge(workBlogListNewWorkBlog);
                    if (oldStatusIDOfWorkBlogListNewWorkBlog != null && !oldStatusIDOfWorkBlogListNewWorkBlog.equals(status)) {
                        oldStatusIDOfWorkBlogListNewWorkBlog.getWorkBlogList().remove(workBlogListNewWorkBlog);
                        oldStatusIDOfWorkBlogListNewWorkBlog = em.merge(oldStatusIDOfWorkBlogListNewWorkBlog);
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
                Integer id = status.getStatusID();
                if (findStatus(id) == null) {
                    throw new NonexistentEntityException("The status with id " + id + " no longer exists.");
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
            Status status;
            try {
                status = em.getReference(Status.class, id);
                status.getStatusID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The status with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkBlog> workBlogListOrphanCheck = status.getWorkBlogList();
            for (WorkBlog workBlogListOrphanCheckWorkBlog : workBlogListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Status (" + status + ") cannot be destroyed since the WorkBlog " + workBlogListOrphanCheckWorkBlog + " in its workBlogList field has a non-nullable statusID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(status);
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

    public List<Status> findStatusEntities() {
        return findStatusEntities(true, -1, -1);
    }

    public List<Status> findStatusEntities(int maxResults, int firstResult) {
        return findStatusEntities(false, maxResults, firstResult);
    }

    private List<Status> findStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Status.class));
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

    public Status findStatus(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Status.class, id);
        } finally {
            em.close();
        }
    }

    public int getStatusCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Status> rt = cq.from(Status.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
