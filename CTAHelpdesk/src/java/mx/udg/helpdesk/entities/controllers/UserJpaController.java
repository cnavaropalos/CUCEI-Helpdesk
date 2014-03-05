package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.Capturist;
import mx.udg.helpdesk.entities.ReportManager;
import mx.udg.helpdesk.entities.AreaManager;
import mx.udg.helpdesk.entities.Client;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.User;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class UserJpaController implements Serializable {

    public UserJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Capturist capturist = user.getCapturist();
            if (capturist != null) {
                capturist = em.getReference(capturist.getClass(), capturist.getUserID());
                user.setCapturist(capturist);
            }
            ReportManager reportManager = user.getReportManager();
            if (reportManager != null) {
                reportManager = em.getReference(reportManager.getClass(), reportManager.getUserID());
                user.setReportManager(reportManager);
            }
            AreaManager areaManager = user.getAreaManager();
            if (areaManager != null) {
                areaManager = em.getReference(areaManager.getClass(), areaManager.getUserID());
                user.setAreaManager(areaManager);
            }
            Client client = user.getClient();
            if (client != null) {
                client = em.getReference(client.getClass(), client.getUserID());
                user.setClient(client);
            }
            em.persist(user);
            if (capturist != null) {
                User oldUserOfCapturist = capturist.getUser();
                if (oldUserOfCapturist != null) {
                    oldUserOfCapturist.setCapturist(null);
                    oldUserOfCapturist = em.merge(oldUserOfCapturist);
                }
                capturist.setUser(user);
                capturist = em.merge(capturist);
            }
            if (reportManager != null) {
                User oldUserOfReportManager = reportManager.getUser();
                if (oldUserOfReportManager != null) {
                    oldUserOfReportManager.setReportManager(null);
                    oldUserOfReportManager = em.merge(oldUserOfReportManager);
                }
                reportManager.setUser(user);
                reportManager = em.merge(reportManager);
            }
            if (areaManager != null) {
                User oldUserOfAreaManager = areaManager.getUser();
                if (oldUserOfAreaManager != null) {
                    oldUserOfAreaManager.setAreaManager(null);
                    oldUserOfAreaManager = em.merge(oldUserOfAreaManager);
                }
                areaManager.setUser(user);
                areaManager = em.merge(areaManager);
            }
            if (client != null) {
                User oldUserOfClient = client.getUser();
                if (oldUserOfClient != null) {
                    oldUserOfClient.setClient(null);
                    oldUserOfClient = em.merge(oldUserOfClient);
                }
                client.setUser(user);
                client = em.merge(client);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUser(user.getUserID()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            User persistentUser = em.find(User.class, user.getUserID());
            Capturist capturistOld = persistentUser.getCapturist();
            Capturist capturistNew = user.getCapturist();
            ReportManager reportManagerOld = persistentUser.getReportManager();
            ReportManager reportManagerNew = user.getReportManager();
            AreaManager areaManagerOld = persistentUser.getAreaManager();
            AreaManager areaManagerNew = user.getAreaManager();
            Client clientOld = persistentUser.getClient();
            Client clientNew = user.getClient();
            List<String> illegalOrphanMessages = null;
            if (capturistOld != null && !capturistOld.equals(capturistNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Capturist " + capturistOld + " since its user field is not nullable.");
            }
            if (reportManagerOld != null && !reportManagerOld.equals(reportManagerNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain ReportManager " + reportManagerOld + " since its user field is not nullable.");
            }
            if (areaManagerOld != null && !areaManagerOld.equals(areaManagerNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain AreaManager " + areaManagerOld + " since its user field is not nullable.");
            }
            if (clientOld != null && !clientOld.equals(clientNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Client " + clientOld + " since its user field is not nullable.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (capturistNew != null) {
                capturistNew = em.getReference(capturistNew.getClass(), capturistNew.getUserID());
                user.setCapturist(capturistNew);
            }
            if (reportManagerNew != null) {
                reportManagerNew = em.getReference(reportManagerNew.getClass(), reportManagerNew.getUserID());
                user.setReportManager(reportManagerNew);
            }
            if (areaManagerNew != null) {
                areaManagerNew = em.getReference(areaManagerNew.getClass(), areaManagerNew.getUserID());
                user.setAreaManager(areaManagerNew);
            }
            if (clientNew != null) {
                clientNew = em.getReference(clientNew.getClass(), clientNew.getUserID());
                user.setClient(clientNew);
            }
            user = em.merge(user);
            if (capturistNew != null && !capturistNew.equals(capturistOld)) {
                User oldUserOfCapturist = capturistNew.getUser();
                if (oldUserOfCapturist != null) {
                    oldUserOfCapturist.setCapturist(null);
                    oldUserOfCapturist = em.merge(oldUserOfCapturist);
                }
                capturistNew.setUser(user);
                capturistNew = em.merge(capturistNew);
            }
            if (reportManagerNew != null && !reportManagerNew.equals(reportManagerOld)) {
                User oldUserOfReportManager = reportManagerNew.getUser();
                if (oldUserOfReportManager != null) {
                    oldUserOfReportManager.setReportManager(null);
                    oldUserOfReportManager = em.merge(oldUserOfReportManager);
                }
                reportManagerNew.setUser(user);
                reportManagerNew = em.merge(reportManagerNew);
            }
            if (areaManagerNew != null && !areaManagerNew.equals(areaManagerOld)) {
                User oldUserOfAreaManager = areaManagerNew.getUser();
                if (oldUserOfAreaManager != null) {
                    oldUserOfAreaManager.setAreaManager(null);
                    oldUserOfAreaManager = em.merge(oldUserOfAreaManager);
                }
                areaManagerNew.setUser(user);
                areaManagerNew = em.merge(areaManagerNew);
            }
            if (clientNew != null && !clientNew.equals(clientOld)) {
                User oldUserOfClient = clientNew.getUser();
                if (oldUserOfClient != null) {
                    oldUserOfClient.setClient(null);
                    oldUserOfClient = em.merge(oldUserOfClient);
                }
                clientNew.setUser(user);
                clientNew = em.merge(clientNew);
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
                String id = user.getUserID();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
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
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Capturist capturistOrphanCheck = user.getCapturist();
            if (capturistOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Capturist " + capturistOrphanCheck + " in its capturist field has a non-nullable user field.");
            }
            ReportManager reportManagerOrphanCheck = user.getReportManager();
            if (reportManagerOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the ReportManager " + reportManagerOrphanCheck + " in its reportManager field has a non-nullable user field.");
            }
            AreaManager areaManagerOrphanCheck = user.getAreaManager();
            if (areaManagerOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the AreaManager " + areaManagerOrphanCheck + " in its areaManager field has a non-nullable user field.");
            }
            Client clientOrphanCheck = user.getClient();
            if (clientOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Client " + clientOrphanCheck + " in its client field has a non-nullable user field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
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

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
