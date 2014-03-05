package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Status;
import mx.udg.helpdesk.entities.Report;
import mx.udg.helpdesk.entities.ReportManager;
import mx.udg.helpdesk.entities.WorkBlog;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class WorkBlogJpaController implements Serializable {

    public WorkBlogJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(WorkBlog workBlog) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status statusID = workBlog.getStatusID();
            if (statusID != null) {
                statusID = em.getReference(statusID.getClass(), statusID.getStatusID());
                workBlog.setStatusID(statusID);
            }
            Report report = workBlog.getReport();
            if (report != null) {
                report = em.getReference(report.getClass(), report.getReportPK());
                workBlog.setReport(report);
            }
            ReportManager reportManagerID = workBlog.getReportManagerID();
            if (reportManagerID != null) {
                reportManagerID = em.getReference(reportManagerID.getClass(), reportManagerID.getUserID());
                workBlog.setReportManagerID(reportManagerID);
            }
            em.persist(workBlog);
            if (statusID != null) {
                statusID.getWorkBlogList().add(workBlog);
                statusID = em.merge(statusID);
            }
            if (report != null) {
                report.getWorkBlogList().add(workBlog);
                report = em.merge(report);
            }
            if (reportManagerID != null) {
                reportManagerID.getWorkBlogList().add(workBlog);
                reportManagerID = em.merge(reportManagerID);
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

    public void edit(WorkBlog workBlog) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WorkBlog persistentWorkBlog = em.find(WorkBlog.class, workBlog.getWorkBlogID());
            Status statusIDOld = persistentWorkBlog.getStatusID();
            Status statusIDNew = workBlog.getStatusID();
            Report reportOld = persistentWorkBlog.getReport();
            Report reportNew = workBlog.getReport();
            ReportManager reportManagerIDOld = persistentWorkBlog.getReportManagerID();
            ReportManager reportManagerIDNew = workBlog.getReportManagerID();
            if (statusIDNew != null) {
                statusIDNew = em.getReference(statusIDNew.getClass(), statusIDNew.getStatusID());
                workBlog.setStatusID(statusIDNew);
            }
            if (reportNew != null) {
                reportNew = em.getReference(reportNew.getClass(), reportNew.getReportPK());
                workBlog.setReport(reportNew);
            }
            if (reportManagerIDNew != null) {
                reportManagerIDNew = em.getReference(reportManagerIDNew.getClass(), reportManagerIDNew.getUserID());
                workBlog.setReportManagerID(reportManagerIDNew);
            }
            workBlog = em.merge(workBlog);
            if (statusIDOld != null && !statusIDOld.equals(statusIDNew)) {
                statusIDOld.getWorkBlogList().remove(workBlog);
                statusIDOld = em.merge(statusIDOld);
            }
            if (statusIDNew != null && !statusIDNew.equals(statusIDOld)) {
                statusIDNew.getWorkBlogList().add(workBlog);
                statusIDNew = em.merge(statusIDNew);
            }
            if (reportOld != null && !reportOld.equals(reportNew)) {
                reportOld.getWorkBlogList().remove(workBlog);
                reportOld = em.merge(reportOld);
            }
            if (reportNew != null && !reportNew.equals(reportOld)) {
                reportNew.getWorkBlogList().add(workBlog);
                reportNew = em.merge(reportNew);
            }
            if (reportManagerIDOld != null && !reportManagerIDOld.equals(reportManagerIDNew)) {
                reportManagerIDOld.getWorkBlogList().remove(workBlog);
                reportManagerIDOld = em.merge(reportManagerIDOld);
            }
            if (reportManagerIDNew != null && !reportManagerIDNew.equals(reportManagerIDOld)) {
                reportManagerIDNew.getWorkBlogList().add(workBlog);
                reportManagerIDNew = em.merge(reportManagerIDNew);
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
                Integer id = workBlog.getWorkBlogID();
                if (findWorkBlog(id) == null) {
                    throw new NonexistentEntityException("The workBlog with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            WorkBlog workBlog;
            try {
                workBlog = em.getReference(WorkBlog.class, id);
                workBlog.getWorkBlogID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The workBlog with id " + id + " no longer exists.", enfe);
            }
            Status statusID = workBlog.getStatusID();
            if (statusID != null) {
                statusID.getWorkBlogList().remove(workBlog);
                statusID = em.merge(statusID);
            }
            Report report = workBlog.getReport();
            if (report != null) {
                report.getWorkBlogList().remove(workBlog);
                report = em.merge(report);
            }
            ReportManager reportManagerID = workBlog.getReportManagerID();
            if (reportManagerID != null) {
                reportManagerID.getWorkBlogList().remove(workBlog);
                reportManagerID = em.merge(reportManagerID);
            }
            em.remove(workBlog);
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

    public List<WorkBlog> findWorkBlogEntities() {
        return findWorkBlogEntities(true, -1, -1);
    }

    public List<WorkBlog> findWorkBlogEntities(int maxResults, int firstResult) {
        return findWorkBlogEntities(false, maxResults, firstResult);
    }

    private List<WorkBlog> findWorkBlogEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(WorkBlog.class));
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

    public WorkBlog findWorkBlog(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(WorkBlog.class, id);
        } finally {
            em.close();
        }
    }

    public int getWorkBlogCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<WorkBlog> rt = cq.from(WorkBlog.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
