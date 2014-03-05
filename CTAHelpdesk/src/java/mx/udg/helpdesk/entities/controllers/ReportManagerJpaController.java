package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.User;
import mx.udg.helpdesk.entities.WorkBlog;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.ReportManager;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class ReportManagerJpaController implements Serializable {

    public ReportManagerJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ReportManager reportManager) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (reportManager.getWorkBlogList() == null) {
            reportManager.setWorkBlogList(new ArrayList<WorkBlog>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = reportManager.getUser();
        if (userOrphanCheck != null) {
            ReportManager oldReportManagerOfUser = userOrphanCheck.getReportManager();
            if (oldReportManagerOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type ReportManager whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = reportManager.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUserID());
                reportManager.setUser(user);
            }
            List<WorkBlog> attachedWorkBlogList = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListWorkBlogToAttach : reportManager.getWorkBlogList()) {
                workBlogListWorkBlogToAttach = em.getReference(workBlogListWorkBlogToAttach.getClass(), workBlogListWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogList.add(workBlogListWorkBlogToAttach);
            }
            reportManager.setWorkBlogList(attachedWorkBlogList);
            em.persist(reportManager);
            if (user != null) {
                user.setReportManager(reportManager);
                user = em.merge(user);
            }
            for (WorkBlog workBlogListWorkBlog : reportManager.getWorkBlogList()) {
                ReportManager oldReportManagerIDOfWorkBlogListWorkBlog = workBlogListWorkBlog.getReportManagerID();
                workBlogListWorkBlog.setReportManagerID(reportManager);
                workBlogListWorkBlog = em.merge(workBlogListWorkBlog);
                if (oldReportManagerIDOfWorkBlogListWorkBlog != null) {
                    oldReportManagerIDOfWorkBlogListWorkBlog.getWorkBlogList().remove(workBlogListWorkBlog);
                    oldReportManagerIDOfWorkBlogListWorkBlog = em.merge(oldReportManagerIDOfWorkBlogListWorkBlog);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReportManager(reportManager.getUserID()) != null) {
                throw new PreexistingEntityException("ReportManager " + reportManager + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ReportManager reportManager) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ReportManager persistentReportManager = em.find(ReportManager.class, reportManager.getUserID());
            User userOld = persistentReportManager.getUser();
            User userNew = reportManager.getUser();
            List<WorkBlog> workBlogListOld = persistentReportManager.getWorkBlogList();
            List<WorkBlog> workBlogListNew = reportManager.getWorkBlogList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                ReportManager oldReportManagerOfUser = userNew.getReportManager();
                if (oldReportManagerOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type ReportManager whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            for (WorkBlog workBlogListOldWorkBlog : workBlogListOld) {
                if (!workBlogListNew.contains(workBlogListOldWorkBlog)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorkBlog " + workBlogListOldWorkBlog + " since its reportManagerID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserID());
                reportManager.setUser(userNew);
            }
            List<WorkBlog> attachedWorkBlogListNew = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListNewWorkBlogToAttach : workBlogListNew) {
                workBlogListNewWorkBlogToAttach = em.getReference(workBlogListNewWorkBlogToAttach.getClass(), workBlogListNewWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogListNew.add(workBlogListNewWorkBlogToAttach);
            }
            workBlogListNew = attachedWorkBlogListNew;
            reportManager.setWorkBlogList(workBlogListNew);
            reportManager = em.merge(reportManager);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setReportManager(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setReportManager(reportManager);
                userNew = em.merge(userNew);
            }
            for (WorkBlog workBlogListNewWorkBlog : workBlogListNew) {
                if (!workBlogListOld.contains(workBlogListNewWorkBlog)) {
                    ReportManager oldReportManagerIDOfWorkBlogListNewWorkBlog = workBlogListNewWorkBlog.getReportManagerID();
                    workBlogListNewWorkBlog.setReportManagerID(reportManager);
                    workBlogListNewWorkBlog = em.merge(workBlogListNewWorkBlog);
                    if (oldReportManagerIDOfWorkBlogListNewWorkBlog != null && !oldReportManagerIDOfWorkBlogListNewWorkBlog.equals(reportManager)) {
                        oldReportManagerIDOfWorkBlogListNewWorkBlog.getWorkBlogList().remove(workBlogListNewWorkBlog);
                        oldReportManagerIDOfWorkBlogListNewWorkBlog = em.merge(oldReportManagerIDOfWorkBlogListNewWorkBlog);
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
                String id = reportManager.getUserID();
                if (findReportManager(id) == null) {
                    throw new NonexistentEntityException("The reportManager with id " + id + " no longer exists.");
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
            ReportManager reportManager;
            try {
                reportManager = em.getReference(ReportManager.class, id);
                reportManager.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The reportManager with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkBlog> workBlogListOrphanCheck = reportManager.getWorkBlogList();
            for (WorkBlog workBlogListOrphanCheckWorkBlog : workBlogListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ReportManager (" + reportManager + ") cannot be destroyed since the WorkBlog " + workBlogListOrphanCheckWorkBlog + " in its workBlogList field has a non-nullable reportManagerID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User user = reportManager.getUser();
            if (user != null) {
                user.setReportManager(null);
                user = em.merge(user);
            }
            em.remove(reportManager);
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

    public List<ReportManager> findReportManagerEntities() {
        return findReportManagerEntities(true, -1, -1);
    }

    public List<ReportManager> findReportManagerEntities(int maxResults, int firstResult) {
        return findReportManagerEntities(false, maxResults, firstResult);
    }

    private List<ReportManager> findReportManagerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ReportManager.class));
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

    public ReportManager findReportManager(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ReportManager.class, id);
        } finally {
            em.close();
        }
    }

    public int getReportManagerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ReportManager> rt = cq.from(ReportManager.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
