package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.Priority;
import mx.udg.helpdesk.entities.Area;
import mx.udg.helpdesk.entities.Report;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.ProblemCategorie;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class ProblemCategorieJpaController implements Serializable {

    public ProblemCategorieJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProblemCategorie problemCategorie) throws RollbackFailureException, Exception {
        if (problemCategorie.getReportList() == null) {
            problemCategorie.setReportList(new ArrayList<Report>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Priority priorityID = problemCategorie.getPriorityID();
            if (priorityID != null) {
                priorityID = em.getReference(priorityID.getClass(), priorityID.getPriorityID());
                problemCategorie.setPriorityID(priorityID);
            }
            Area areaID = problemCategorie.getAreaID();
            if (areaID != null) {
                areaID = em.getReference(areaID.getClass(), areaID.getAreaID());
                problemCategorie.setAreaID(areaID);
            }
            List<Report> attachedReportList = new ArrayList<Report>();
            for (Report reportListReportToAttach : problemCategorie.getReportList()) {
                reportListReportToAttach = em.getReference(reportListReportToAttach.getClass(), reportListReportToAttach.getReportPK());
                attachedReportList.add(reportListReportToAttach);
            }
            problemCategorie.setReportList(attachedReportList);
            em.persist(problemCategorie);
            if (priorityID != null) {
                priorityID.getProblemCategorieList().add(problemCategorie);
                priorityID = em.merge(priorityID);
            }
            if (areaID != null) {
                areaID.getProblemCategorieList().add(problemCategorie);
                areaID = em.merge(areaID);
            }
            for (Report reportListReport : problemCategorie.getReportList()) {
                ProblemCategorie oldProblemIDOfReportListReport = reportListReport.getProblemID();
                reportListReport.setProblemID(problemCategorie);
                reportListReport = em.merge(reportListReport);
                if (oldProblemIDOfReportListReport != null) {
                    oldProblemIDOfReportListReport.getReportList().remove(reportListReport);
                    oldProblemIDOfReportListReport = em.merge(oldProblemIDOfReportListReport);
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

    public void edit(ProblemCategorie problemCategorie) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            ProblemCategorie persistentProblemCategorie = em.find(ProblemCategorie.class, problemCategorie.getProblemID());
            Priority priorityIDOld = persistentProblemCategorie.getPriorityID();
            Priority priorityIDNew = problemCategorie.getPriorityID();
            Area areaIDOld = persistentProblemCategorie.getAreaID();
            Area areaIDNew = problemCategorie.getAreaID();
            List<Report> reportListOld = persistentProblemCategorie.getReportList();
            List<Report> reportListNew = problemCategorie.getReportList();
            List<String> illegalOrphanMessages = null;
            for (Report reportListOldReport : reportListOld) {
                if (!reportListNew.contains(reportListOldReport)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Report " + reportListOldReport + " since its problemID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (priorityIDNew != null) {
                priorityIDNew = em.getReference(priorityIDNew.getClass(), priorityIDNew.getPriorityID());
                problemCategorie.setPriorityID(priorityIDNew);
            }
            if (areaIDNew != null) {
                areaIDNew = em.getReference(areaIDNew.getClass(), areaIDNew.getAreaID());
                problemCategorie.setAreaID(areaIDNew);
            }
            List<Report> attachedReportListNew = new ArrayList<Report>();
            for (Report reportListNewReportToAttach : reportListNew) {
                reportListNewReportToAttach = em.getReference(reportListNewReportToAttach.getClass(), reportListNewReportToAttach.getReportPK());
                attachedReportListNew.add(reportListNewReportToAttach);
            }
            reportListNew = attachedReportListNew;
            problemCategorie.setReportList(reportListNew);
            problemCategorie = em.merge(problemCategorie);
            if (priorityIDOld != null && !priorityIDOld.equals(priorityIDNew)) {
                priorityIDOld.getProblemCategorieList().remove(problemCategorie);
                priorityIDOld = em.merge(priorityIDOld);
            }
            if (priorityIDNew != null && !priorityIDNew.equals(priorityIDOld)) {
                priorityIDNew.getProblemCategorieList().add(problemCategorie);
                priorityIDNew = em.merge(priorityIDNew);
            }
            if (areaIDOld != null && !areaIDOld.equals(areaIDNew)) {
                areaIDOld.getProblemCategorieList().remove(problemCategorie);
                areaIDOld = em.merge(areaIDOld);
            }
            if (areaIDNew != null && !areaIDNew.equals(areaIDOld)) {
                areaIDNew.getProblemCategorieList().add(problemCategorie);
                areaIDNew = em.merge(areaIDNew);
            }
            for (Report reportListNewReport : reportListNew) {
                if (!reportListOld.contains(reportListNewReport)) {
                    ProblemCategorie oldProblemIDOfReportListNewReport = reportListNewReport.getProblemID();
                    reportListNewReport.setProblemID(problemCategorie);
                    reportListNewReport = em.merge(reportListNewReport);
                    if (oldProblemIDOfReportListNewReport != null && !oldProblemIDOfReportListNewReport.equals(problemCategorie)) {
                        oldProblemIDOfReportListNewReport.getReportList().remove(reportListNewReport);
                        oldProblemIDOfReportListNewReport = em.merge(oldProblemIDOfReportListNewReport);
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
                Integer id = problemCategorie.getProblemID();
                if (findProblemCategorie(id) == null) {
                    throw new NonexistentEntityException("The problemCategorie with id " + id + " no longer exists.");
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
            ProblemCategorie problemCategorie;
            try {
                problemCategorie = em.getReference(ProblemCategorie.class, id);
                problemCategorie.getProblemID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The problemCategorie with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Report> reportListOrphanCheck = problemCategorie.getReportList();
            for (Report reportListOrphanCheckReport : reportListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This ProblemCategorie (" + problemCategorie + ") cannot be destroyed since the Report " + reportListOrphanCheckReport + " in its reportList field has a non-nullable problemID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Priority priorityID = problemCategorie.getPriorityID();
            if (priorityID != null) {
                priorityID.getProblemCategorieList().remove(problemCategorie);
                priorityID = em.merge(priorityID);
            }
            Area areaID = problemCategorie.getAreaID();
            if (areaID != null) {
                areaID.getProblemCategorieList().remove(problemCategorie);
                areaID = em.merge(areaID);
            }
            em.remove(problemCategorie);
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

    public List<ProblemCategorie> findProblemCategorieEntities() {
        return findProblemCategorieEntities(true, -1, -1);
    }

    public List<ProblemCategorie> findProblemCategorieEntities(int maxResults, int firstResult) {
        return findProblemCategorieEntities(false, maxResults, firstResult);
    }

    private List<ProblemCategorie> findProblemCategorieEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProblemCategorie.class));
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

    public ProblemCategorie findProblemCategorie(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProblemCategorie.class, id);
        } finally {
            em.close();
        }
    }

    public int getProblemCategorieCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProblemCategorie> rt = cq.from(ProblemCategorie.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
