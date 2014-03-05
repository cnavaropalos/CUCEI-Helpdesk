package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.User;
import mx.udg.helpdesk.entities.Report;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Capturist;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class CapturistJpaController implements Serializable {

    public CapturistJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Capturist capturist) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (capturist.getReportList() == null) {
            capturist.setReportList(new ArrayList<Report>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = capturist.getUser();
        if (userOrphanCheck != null) {
            Capturist oldCapturistOfUser = userOrphanCheck.getCapturist();
            if (oldCapturistOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type Capturist whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User user = capturist.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUserID());
                capturist.setUser(user);
            }
            List<Report> attachedReportList = new ArrayList<Report>();
            for (Report reportListReportToAttach : capturist.getReportList()) {
                reportListReportToAttach = em.getReference(reportListReportToAttach.getClass(), reportListReportToAttach.getReportPK());
                attachedReportList.add(reportListReportToAttach);
            }
            capturist.setReportList(attachedReportList);
            em.persist(capturist);
            if (user != null) {
                user.setCapturist(capturist);
                user = em.merge(user);
            }
            for (Report reportListReport : capturist.getReportList()) {
                Capturist oldCapturisIDOfReportListReport = reportListReport.getCapturisID();
                reportListReport.setCapturisID(capturist);
                reportListReport = em.merge(reportListReport);
                if (oldCapturisIDOfReportListReport != null) {
                    oldCapturisIDOfReportListReport.getReportList().remove(reportListReport);
                    oldCapturisIDOfReportListReport = em.merge(oldCapturisIDOfReportListReport);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCapturist(capturist.getUserID()) != null) {
                throw new PreexistingEntityException("Capturist " + capturist + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Capturist capturist) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Capturist persistentCapturist = em.find(Capturist.class, capturist.getUserID());
            User userOld = persistentCapturist.getUser();
            User userNew = capturist.getUser();
            List<Report> reportListOld = persistentCapturist.getReportList();
            List<Report> reportListNew = capturist.getReportList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                Capturist oldCapturistOfUser = userNew.getCapturist();
                if (oldCapturistOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type Capturist whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            for (Report reportListOldReport : reportListOld) {
                if (!reportListNew.contains(reportListOldReport)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Report " + reportListOldReport + " since its capturisID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserID());
                capturist.setUser(userNew);
            }
            List<Report> attachedReportListNew = new ArrayList<Report>();
            for (Report reportListNewReportToAttach : reportListNew) {
                reportListNewReportToAttach = em.getReference(reportListNewReportToAttach.getClass(), reportListNewReportToAttach.getReportPK());
                attachedReportListNew.add(reportListNewReportToAttach);
            }
            reportListNew = attachedReportListNew;
            capturist.setReportList(reportListNew);
            capturist = em.merge(capturist);
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setCapturist(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setCapturist(capturist);
                userNew = em.merge(userNew);
            }
            for (Report reportListNewReport : reportListNew) {
                if (!reportListOld.contains(reportListNewReport)) {
                    Capturist oldCapturisIDOfReportListNewReport = reportListNewReport.getCapturisID();
                    reportListNewReport.setCapturisID(capturist);
                    reportListNewReport = em.merge(reportListNewReport);
                    if (oldCapturisIDOfReportListNewReport != null && !oldCapturisIDOfReportListNewReport.equals(capturist)) {
                        oldCapturisIDOfReportListNewReport.getReportList().remove(reportListNewReport);
                        oldCapturisIDOfReportListNewReport = em.merge(oldCapturisIDOfReportListNewReport);
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
                String id = capturist.getUserID();
                if (findCapturist(id) == null) {
                    throw new NonexistentEntityException("The capturist with id " + id + " no longer exists.");
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
            Capturist capturist;
            try {
                capturist = em.getReference(Capturist.class, id);
                capturist.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The capturist with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Report> reportListOrphanCheck = capturist.getReportList();
            for (Report reportListOrphanCheckReport : reportListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Capturist (" + capturist + ") cannot be destroyed since the Report " + reportListOrphanCheckReport + " in its reportList field has a non-nullable capturisID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User user = capturist.getUser();
            if (user != null) {
                user.setCapturist(null);
                user = em.merge(user);
            }
            em.remove(capturist);
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

    public List<Capturist> findCapturistEntities() {
        return findCapturistEntities(true, -1, -1);
    }

    public List<Capturist> findCapturistEntities(int maxResults, int firstResult) {
        return findCapturistEntities(false, maxResults, firstResult);
    }

    private List<Capturist> findCapturistEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Capturist.class));
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

    public Capturist findCapturist(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Capturist.class, id);
        } finally {
            em.close();
        }
    }

    public int getCapturistCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Capturist> rt = cq.from(Capturist.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
