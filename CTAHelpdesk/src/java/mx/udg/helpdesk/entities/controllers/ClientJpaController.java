package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.DepartmentsByModule;
import mx.udg.helpdesk.entities.User;
import mx.udg.helpdesk.entities.Report;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Client;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class ClientJpaController implements Serializable {

    public ClientJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Client client) throws IllegalOrphanException, PreexistingEntityException, RollbackFailureException, Exception {
        if (client.getReportList() == null) {
            client.setReportList(new ArrayList<Report>());
        }
        List<String> illegalOrphanMessages = null;
        User userOrphanCheck = client.getUser();
        if (userOrphanCheck != null) {
            Client oldClientOfUser = userOrphanCheck.getClient();
            if (oldClientOfUser != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("The User " + userOrphanCheck + " already has an item of type Client whose user column cannot be null. Please make another selection for the user field.");
            }
        }
        if (illegalOrphanMessages != null) {
            throw new IllegalOrphanException(illegalOrphanMessages);
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DepartmentsByModule departmentsByModule = client.getDepartmentsByModule();
            if (departmentsByModule != null) {
                departmentsByModule = em.getReference(departmentsByModule.getClass(), departmentsByModule.getDepartmentsByModulePK());
                client.setDepartmentsByModule(departmentsByModule);
            }
            User user = client.getUser();
            if (user != null) {
                user = em.getReference(user.getClass(), user.getUserID());
                client.setUser(user);
            }
            List<Report> attachedReportList = new ArrayList<Report>();
            for (Report reportListReportToAttach : client.getReportList()) {
                reportListReportToAttach = em.getReference(reportListReportToAttach.getClass(), reportListReportToAttach.getReportPK());
                attachedReportList.add(reportListReportToAttach);
            }
            client.setReportList(attachedReportList);
            em.persist(client);
            if (departmentsByModule != null) {
                departmentsByModule.getClientList().add(client);
                departmentsByModule = em.merge(departmentsByModule);
            }
            if (user != null) {
                user.setClient(client);
                user = em.merge(user);
            }
            for (Report reportListReport : client.getReportList()) {
                Client oldClientIDOfReportListReport = reportListReport.getClientID();
                reportListReport.setClientID(client);
                reportListReport = em.merge(reportListReport);
                if (oldClientIDOfReportListReport != null) {
                    oldClientIDOfReportListReport.getReportList().remove(reportListReport);
                    oldClientIDOfReportListReport = em.merge(oldClientIDOfReportListReport);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findClient(client.getUserID()) != null) {
                throw new PreexistingEntityException("Client " + client + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Client client) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Client persistentClient = em.find(Client.class, client.getUserID());
            DepartmentsByModule departmentsByModuleOld = persistentClient.getDepartmentsByModule();
            DepartmentsByModule departmentsByModuleNew = client.getDepartmentsByModule();
            User userOld = persistentClient.getUser();
            User userNew = client.getUser();
            List<Report> reportListOld = persistentClient.getReportList();
            List<Report> reportListNew = client.getReportList();
            List<String> illegalOrphanMessages = null;
            if (userNew != null && !userNew.equals(userOld)) {
                Client oldClientOfUser = userNew.getClient();
                if (oldClientOfUser != null) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("The User " + userNew + " already has an item of type Client whose user column cannot be null. Please make another selection for the user field.");
                }
            }
            for (Report reportListOldReport : reportListOld) {
                if (!reportListNew.contains(reportListOldReport)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Report " + reportListOldReport + " since its clientID field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (departmentsByModuleNew != null) {
                departmentsByModuleNew = em.getReference(departmentsByModuleNew.getClass(), departmentsByModuleNew.getDepartmentsByModulePK());
                client.setDepartmentsByModule(departmentsByModuleNew);
            }
            if (userNew != null) {
                userNew = em.getReference(userNew.getClass(), userNew.getUserID());
                client.setUser(userNew);
            }
            List<Report> attachedReportListNew = new ArrayList<Report>();
            for (Report reportListNewReportToAttach : reportListNew) {
                reportListNewReportToAttach = em.getReference(reportListNewReportToAttach.getClass(), reportListNewReportToAttach.getReportPK());
                attachedReportListNew.add(reportListNewReportToAttach);
            }
            reportListNew = attachedReportListNew;
            client.setReportList(reportListNew);
            client = em.merge(client);
            if (departmentsByModuleOld != null && !departmentsByModuleOld.equals(departmentsByModuleNew)) {
                departmentsByModuleOld.getClientList().remove(client);
                departmentsByModuleOld = em.merge(departmentsByModuleOld);
            }
            if (departmentsByModuleNew != null && !departmentsByModuleNew.equals(departmentsByModuleOld)) {
                departmentsByModuleNew.getClientList().add(client);
                departmentsByModuleNew = em.merge(departmentsByModuleNew);
            }
            if (userOld != null && !userOld.equals(userNew)) {
                userOld.setClient(null);
                userOld = em.merge(userOld);
            }
            if (userNew != null && !userNew.equals(userOld)) {
                userNew.setClient(client);
                userNew = em.merge(userNew);
            }
            for (Report reportListNewReport : reportListNew) {
                if (!reportListOld.contains(reportListNewReport)) {
                    Client oldClientIDOfReportListNewReport = reportListNewReport.getClientID();
                    reportListNewReport.setClientID(client);
                    reportListNewReport = em.merge(reportListNewReport);
                    if (oldClientIDOfReportListNewReport != null && !oldClientIDOfReportListNewReport.equals(client)) {
                        oldClientIDOfReportListNewReport.getReportList().remove(reportListNewReport);
                        oldClientIDOfReportListNewReport = em.merge(oldClientIDOfReportListNewReport);
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
                String id = client.getUserID();
                if (findClient(id) == null) {
                    throw new NonexistentEntityException("The client with id " + id + " no longer exists.");
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
            Client client;
            try {
                client = em.getReference(Client.class, id);
                client.getUserID();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The client with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Report> reportListOrphanCheck = client.getReportList();
            for (Report reportListOrphanCheckReport : reportListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Client (" + client + ") cannot be destroyed since the Report " + reportListOrphanCheckReport + " in its reportList field has a non-nullable clientID field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            DepartmentsByModule departmentsByModule = client.getDepartmentsByModule();
            if (departmentsByModule != null) {
                departmentsByModule.getClientList().remove(client);
                departmentsByModule = em.merge(departmentsByModule);
            }
            User user = client.getUser();
            if (user != null) {
                user.setClient(null);
                user = em.merge(user);
            }
            em.remove(client);
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

    public List<Client> findClientEntities() {
        return findClientEntities(true, -1, -1);
    }

    public List<Client> findClientEntities(int maxResults, int firstResult) {
        return findClientEntities(false, maxResults, firstResult);
    }

    private List<Client> findClientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Client.class));
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

    public Client findClient(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Client.class, id);
        } finally {
            em.close();
        }
    }

    public int getClientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Client> rt = cq.from(Client.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
