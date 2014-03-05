package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.Client;
import mx.udg.helpdesk.entities.Capturist;
import mx.udg.helpdesk.entities.ProblemCategorie;
import mx.udg.helpdesk.entities.DepartmentsByModule;
import mx.udg.helpdesk.entities.WorkBlog;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Report;
import mx.udg.helpdesk.entities.ReportPK;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class ReportJpaController implements Serializable {

    public ReportJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Report report) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (report.getReportPK() == null) {
            report.setReportPK(new ReportPK());
        }
        if (report.getWorkBlogList() == null) {
            report.setWorkBlogList(new ArrayList<WorkBlog>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Client clientID = report.getClientID();
            if (clientID != null) {
                clientID = em.getReference(clientID.getClass(), clientID.getUserID());
                report.setClientID(clientID);
            }
            Capturist capturisID = report.getCapturisID();
            if (capturisID != null) {
                capturisID = em.getReference(capturisID.getClass(), capturisID.getUserID());
                report.setCapturisID(capturisID);
            }
            ProblemCategorie problemID = report.getProblemID();
            if (problemID != null) {
                problemID = em.getReference(problemID.getClass(), problemID.getProblemID());
                report.setProblemID(problemID);
            }
            DepartmentsByModule departmentsByModule = report.getDepartmentsByModule();
            if (departmentsByModule != null) {
                departmentsByModule = em.getReference(departmentsByModule.getClass(), departmentsByModule.getDepartmentsByModulePK());
                report.setDepartmentsByModule(departmentsByModule);
            }
            List<WorkBlog> attachedWorkBlogList = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListWorkBlogToAttach : report.getWorkBlogList()) {
                workBlogListWorkBlogToAttach = em.getReference(workBlogListWorkBlogToAttach.getClass(), workBlogListWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogList.add(workBlogListWorkBlogToAttach);
            }
            report.setWorkBlogList(attachedWorkBlogList);
            em.persist(report);
            if (clientID != null) {
                clientID.getReportList().add(report);
                clientID = em.merge(clientID);
            }
            if (capturisID != null) {
                capturisID.getReportList().add(report);
                capturisID = em.merge(capturisID);
            }
            if (problemID != null) {
                problemID.getReportList().add(report);
                problemID = em.merge(problemID);
            }
            if (departmentsByModule != null) {
                departmentsByModule.getReportList().add(report);
                departmentsByModule = em.merge(departmentsByModule);
            }
            for (WorkBlog workBlogListWorkBlog : report.getWorkBlogList()) {
                Report oldReportOfWorkBlogListWorkBlog = workBlogListWorkBlog.getReport();
                workBlogListWorkBlog.setReport(report);
                workBlogListWorkBlog = em.merge(workBlogListWorkBlog);
                if (oldReportOfWorkBlogListWorkBlog != null) {
                    oldReportOfWorkBlogListWorkBlog.getWorkBlogList().remove(workBlogListWorkBlog);
                    oldReportOfWorkBlogListWorkBlog = em.merge(oldReportOfWorkBlogListWorkBlog);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findReport(report.getReportPK()) != null) {
                throw new PreexistingEntityException("Report " + report + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Report report) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Report persistentReport = em.find(Report.class, report.getReportPK());
            Client clientIDOld = persistentReport.getClientID();
            Client clientIDNew = report.getClientID();
            Capturist capturisIDOld = persistentReport.getCapturisID();
            Capturist capturisIDNew = report.getCapturisID();
            ProblemCategorie problemIDOld = persistentReport.getProblemID();
            ProblemCategorie problemIDNew = report.getProblemID();
            DepartmentsByModule departmentsByModuleOld = persistentReport.getDepartmentsByModule();
            DepartmentsByModule departmentsByModuleNew = report.getDepartmentsByModule();
            List<WorkBlog> workBlogListOld = persistentReport.getWorkBlogList();
            List<WorkBlog> workBlogListNew = report.getWorkBlogList();
            List<String> illegalOrphanMessages = null;
            for (WorkBlog workBlogListOldWorkBlog : workBlogListOld) {
                if (!workBlogListNew.contains(workBlogListOldWorkBlog)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain WorkBlog " + workBlogListOldWorkBlog + " since its report field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clientIDNew != null) {
                clientIDNew = em.getReference(clientIDNew.getClass(), clientIDNew.getUserID());
                report.setClientID(clientIDNew);
            }
            if (capturisIDNew != null) {
                capturisIDNew = em.getReference(capturisIDNew.getClass(), capturisIDNew.getUserID());
                report.setCapturisID(capturisIDNew);
            }
            if (problemIDNew != null) {
                problemIDNew = em.getReference(problemIDNew.getClass(), problemIDNew.getProblemID());
                report.setProblemID(problemIDNew);
            }
            if (departmentsByModuleNew != null) {
                departmentsByModuleNew = em.getReference(departmentsByModuleNew.getClass(), departmentsByModuleNew.getDepartmentsByModulePK());
                report.setDepartmentsByModule(departmentsByModuleNew);
            }
            List<WorkBlog> attachedWorkBlogListNew = new ArrayList<WorkBlog>();
            for (WorkBlog workBlogListNewWorkBlogToAttach : workBlogListNew) {
                workBlogListNewWorkBlogToAttach = em.getReference(workBlogListNewWorkBlogToAttach.getClass(), workBlogListNewWorkBlogToAttach.getWorkBlogID());
                attachedWorkBlogListNew.add(workBlogListNewWorkBlogToAttach);
            }
            workBlogListNew = attachedWorkBlogListNew;
            report.setWorkBlogList(workBlogListNew);
            report = em.merge(report);
            if (clientIDOld != null && !clientIDOld.equals(clientIDNew)) {
                clientIDOld.getReportList().remove(report);
                clientIDOld = em.merge(clientIDOld);
            }
            if (clientIDNew != null && !clientIDNew.equals(clientIDOld)) {
                clientIDNew.getReportList().add(report);
                clientIDNew = em.merge(clientIDNew);
            }
            if (capturisIDOld != null && !capturisIDOld.equals(capturisIDNew)) {
                capturisIDOld.getReportList().remove(report);
                capturisIDOld = em.merge(capturisIDOld);
            }
            if (capturisIDNew != null && !capturisIDNew.equals(capturisIDOld)) {
                capturisIDNew.getReportList().add(report);
                capturisIDNew = em.merge(capturisIDNew);
            }
            if (problemIDOld != null && !problemIDOld.equals(problemIDNew)) {
                problemIDOld.getReportList().remove(report);
                problemIDOld = em.merge(problemIDOld);
            }
            if (problemIDNew != null && !problemIDNew.equals(problemIDOld)) {
                problemIDNew.getReportList().add(report);
                problemIDNew = em.merge(problemIDNew);
            }
            if (departmentsByModuleOld != null && !departmentsByModuleOld.equals(departmentsByModuleNew)) {
                departmentsByModuleOld.getReportList().remove(report);
                departmentsByModuleOld = em.merge(departmentsByModuleOld);
            }
            if (departmentsByModuleNew != null && !departmentsByModuleNew.equals(departmentsByModuleOld)) {
                departmentsByModuleNew.getReportList().add(report);
                departmentsByModuleNew = em.merge(departmentsByModuleNew);
            }
            for (WorkBlog workBlogListNewWorkBlog : workBlogListNew) {
                if (!workBlogListOld.contains(workBlogListNewWorkBlog)) {
                    Report oldReportOfWorkBlogListNewWorkBlog = workBlogListNewWorkBlog.getReport();
                    workBlogListNewWorkBlog.setReport(report);
                    workBlogListNewWorkBlog = em.merge(workBlogListNewWorkBlog);
                    if (oldReportOfWorkBlogListNewWorkBlog != null && !oldReportOfWorkBlogListNewWorkBlog.equals(report)) {
                        oldReportOfWorkBlogListNewWorkBlog.getWorkBlogList().remove(workBlogListNewWorkBlog);
                        oldReportOfWorkBlogListNewWorkBlog = em.merge(oldReportOfWorkBlogListNewWorkBlog);
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
                ReportPK id = report.getReportPK();
                if (findReport(id) == null) {
                    throw new NonexistentEntityException("The report with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ReportPK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Report report;
            try {
                report = em.getReference(Report.class, id);
                report.getReportPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The report with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<WorkBlog> workBlogListOrphanCheck = report.getWorkBlogList();
            for (WorkBlog workBlogListOrphanCheckWorkBlog : workBlogListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Report (" + report + ") cannot be destroyed since the WorkBlog " + workBlogListOrphanCheckWorkBlog + " in its workBlogList field has a non-nullable report field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Client clientID = report.getClientID();
            if (clientID != null) {
                clientID.getReportList().remove(report);
                clientID = em.merge(clientID);
            }
            Capturist capturisID = report.getCapturisID();
            if (capturisID != null) {
                capturisID.getReportList().remove(report);
                capturisID = em.merge(capturisID);
            }
            ProblemCategorie problemID = report.getProblemID();
            if (problemID != null) {
                problemID.getReportList().remove(report);
                problemID = em.merge(problemID);
            }
            DepartmentsByModule departmentsByModule = report.getDepartmentsByModule();
            if (departmentsByModule != null) {
                departmentsByModule.getReportList().remove(report);
                departmentsByModule = em.merge(departmentsByModule);
            }
            em.remove(report);
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

    public List<Report> findReportEntities() {
        return findReportEntities(true, -1, -1);
    }

    public List<Report> findReportEntities(int maxResults, int firstResult) {
        return findReportEntities(false, maxResults, firstResult);
    }

    private List<Report> findReportEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Report.class));
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

    public Report findReport(ReportPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Report.class, id);
        } finally {
            em.close();
        }
    }

    public int getReportCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Report> rt = cq.from(Report.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
