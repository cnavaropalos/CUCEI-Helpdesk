package mx.udg.helpdesk.entities.controllers;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.udg.helpdesk.entities.Priority;
import mx.udg.helpdesk.entities.Floor;
import mx.udg.helpdesk.entities.Department;
import mx.udg.helpdesk.entities.Module;
import mx.udg.helpdesk.entities.Report;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import mx.udg.helpdesk.entities.Client;
import mx.udg.helpdesk.entities.DepartmentsByModule;
import mx.udg.helpdesk.entities.DepartmentsByModulePK;
import mx.udg.helpdesk.errorHandlers.exceptions.IllegalOrphanException;
import mx.udg.helpdesk.errorHandlers.exceptions.NonexistentEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.PreexistingEntityException;
import mx.udg.helpdesk.errorHandlers.exceptions.RollbackFailureException;

/**
 *
 * @author Carlos Navapa
 */
public class DepartmentsByModuleJpaController implements Serializable {

    public DepartmentsByModuleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DepartmentsByModule departmentsByModule) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (departmentsByModule.getDepartmentsByModulePK() == null) {
            departmentsByModule.setDepartmentsByModulePK(new DepartmentsByModulePK());
        }
        if (departmentsByModule.getReportList() == null) {
            departmentsByModule.setReportList(new ArrayList<Report>());
        }
        if (departmentsByModule.getClientList() == null) {
            departmentsByModule.setClientList(new ArrayList<Client>());
        }
        departmentsByModule.getDepartmentsByModulePK().setDepartmentID(departmentsByModule.getDepartment().getDepartmentID());
        departmentsByModule.getDepartmentsByModulePK().setModuleID(departmentsByModule.getModule().getModuleID());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Priority priorityID = departmentsByModule.getPriorityID();
            if (priorityID != null) {
                priorityID = em.getReference(priorityID.getClass(), priorityID.getPriorityID());
                departmentsByModule.setPriorityID(priorityID);
            }
            Floor floorID = departmentsByModule.getFloorID();
            if (floorID != null) {
                floorID = em.getReference(floorID.getClass(), floorID.getFloorID());
                departmentsByModule.setFloorID(floorID);
            }
            Department department = departmentsByModule.getDepartment();
            if (department != null) {
                department = em.getReference(department.getClass(), department.getDepartmentID());
                departmentsByModule.setDepartment(department);
            }
            Module module = departmentsByModule.getModule();
            if (module != null) {
                module = em.getReference(module.getClass(), module.getModuleID());
                departmentsByModule.setModule(module);
            }
            List<Report> attachedReportList = new ArrayList<Report>();
            for (Report reportListReportToAttach : departmentsByModule.getReportList()) {
                reportListReportToAttach = em.getReference(reportListReportToAttach.getClass(), reportListReportToAttach.getReportPK());
                attachedReportList.add(reportListReportToAttach);
            }
            departmentsByModule.setReportList(attachedReportList);
            List<Client> attachedClientList = new ArrayList<Client>();
            for (Client clientListClientToAttach : departmentsByModule.getClientList()) {
                clientListClientToAttach = em.getReference(clientListClientToAttach.getClass(), clientListClientToAttach.getUserID());
                attachedClientList.add(clientListClientToAttach);
            }
            departmentsByModule.setClientList(attachedClientList);
            em.persist(departmentsByModule);
            if (priorityID != null) {
                priorityID.getDepartmentsByModuleList().add(departmentsByModule);
                priorityID = em.merge(priorityID);
            }
            if (floorID != null) {
                floorID.getDepartmentsByModuleList().add(departmentsByModule);
                floorID = em.merge(floorID);
            }
            if (department != null) {
                department.getDepartmentsByModuleList().add(departmentsByModule);
                department = em.merge(department);
            }
            if (module != null) {
                module.getDepartmentsByModuleList().add(departmentsByModule);
                module = em.merge(module);
            }
            for (Report reportListReport : departmentsByModule.getReportList()) {
                DepartmentsByModule oldDepartmentsByModuleOfReportListReport = reportListReport.getDepartmentsByModule();
                reportListReport.setDepartmentsByModule(departmentsByModule);
                reportListReport = em.merge(reportListReport);
                if (oldDepartmentsByModuleOfReportListReport != null) {
                    oldDepartmentsByModuleOfReportListReport.getReportList().remove(reportListReport);
                    oldDepartmentsByModuleOfReportListReport = em.merge(oldDepartmentsByModuleOfReportListReport);
                }
            }
            for (Client clientListClient : departmentsByModule.getClientList()) {
                DepartmentsByModule oldDepartmentsByModuleOfClientListClient = clientListClient.getDepartmentsByModule();
                clientListClient.setDepartmentsByModule(departmentsByModule);
                clientListClient = em.merge(clientListClient);
                if (oldDepartmentsByModuleOfClientListClient != null) {
                    oldDepartmentsByModuleOfClientListClient.getClientList().remove(clientListClient);
                    oldDepartmentsByModuleOfClientListClient = em.merge(oldDepartmentsByModuleOfClientListClient);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDepartmentsByModule(departmentsByModule.getDepartmentsByModulePK()) != null) {
                throw new PreexistingEntityException("DepartmentsByModule " + departmentsByModule + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(DepartmentsByModule departmentsByModule) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        departmentsByModule.getDepartmentsByModulePK().setDepartmentID(departmentsByModule.getDepartment().getDepartmentID());
        departmentsByModule.getDepartmentsByModulePK().setModuleID(departmentsByModule.getModule().getModuleID());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DepartmentsByModule persistentDepartmentsByModule = em.find(DepartmentsByModule.class, departmentsByModule.getDepartmentsByModulePK());
            Priority priorityIDOld = persistentDepartmentsByModule.getPriorityID();
            Priority priorityIDNew = departmentsByModule.getPriorityID();
            Floor floorIDOld = persistentDepartmentsByModule.getFloorID();
            Floor floorIDNew = departmentsByModule.getFloorID();
            Department departmentOld = persistentDepartmentsByModule.getDepartment();
            Department departmentNew = departmentsByModule.getDepartment();
            Module moduleOld = persistentDepartmentsByModule.getModule();
            Module moduleNew = departmentsByModule.getModule();
            List<Report> reportListOld = persistentDepartmentsByModule.getReportList();
            List<Report> reportListNew = departmentsByModule.getReportList();
            List<Client> clientListOld = persistentDepartmentsByModule.getClientList();
            List<Client> clientListNew = departmentsByModule.getClientList();
            List<String> illegalOrphanMessages = null;
            for (Report reportListOldReport : reportListOld) {
                if (!reportListNew.contains(reportListOldReport)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Report " + reportListOldReport + " since its departmentsByModule field is not nullable.");
                }
            }
            for (Client clientListOldClient : clientListOld) {
                if (!clientListNew.contains(clientListOldClient)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Client " + clientListOldClient + " since its departmentsByModule field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (priorityIDNew != null) {
                priorityIDNew = em.getReference(priorityIDNew.getClass(), priorityIDNew.getPriorityID());
                departmentsByModule.setPriorityID(priorityIDNew);
            }
            if (floorIDNew != null) {
                floorIDNew = em.getReference(floorIDNew.getClass(), floorIDNew.getFloorID());
                departmentsByModule.setFloorID(floorIDNew);
            }
            if (departmentNew != null) {
                departmentNew = em.getReference(departmentNew.getClass(), departmentNew.getDepartmentID());
                departmentsByModule.setDepartment(departmentNew);
            }
            if (moduleNew != null) {
                moduleNew = em.getReference(moduleNew.getClass(), moduleNew.getModuleID());
                departmentsByModule.setModule(moduleNew);
            }
            List<Report> attachedReportListNew = new ArrayList<Report>();
            for (Report reportListNewReportToAttach : reportListNew) {
                reportListNewReportToAttach = em.getReference(reportListNewReportToAttach.getClass(), reportListNewReportToAttach.getReportPK());
                attachedReportListNew.add(reportListNewReportToAttach);
            }
            reportListNew = attachedReportListNew;
            departmentsByModule.setReportList(reportListNew);
            List<Client> attachedClientListNew = new ArrayList<Client>();
            for (Client clientListNewClientToAttach : clientListNew) {
                clientListNewClientToAttach = em.getReference(clientListNewClientToAttach.getClass(), clientListNewClientToAttach.getUserID());
                attachedClientListNew.add(clientListNewClientToAttach);
            }
            clientListNew = attachedClientListNew;
            departmentsByModule.setClientList(clientListNew);
            departmentsByModule = em.merge(departmentsByModule);
            if (priorityIDOld != null && !priorityIDOld.equals(priorityIDNew)) {
                priorityIDOld.getDepartmentsByModuleList().remove(departmentsByModule);
                priorityIDOld = em.merge(priorityIDOld);
            }
            if (priorityIDNew != null && !priorityIDNew.equals(priorityIDOld)) {
                priorityIDNew.getDepartmentsByModuleList().add(departmentsByModule);
                priorityIDNew = em.merge(priorityIDNew);
            }
            if (floorIDOld != null && !floorIDOld.equals(floorIDNew)) {
                floorIDOld.getDepartmentsByModuleList().remove(departmentsByModule);
                floorIDOld = em.merge(floorIDOld);
            }
            if (floorIDNew != null && !floorIDNew.equals(floorIDOld)) {
                floorIDNew.getDepartmentsByModuleList().add(departmentsByModule);
                floorIDNew = em.merge(floorIDNew);
            }
            if (departmentOld != null && !departmentOld.equals(departmentNew)) {
                departmentOld.getDepartmentsByModuleList().remove(departmentsByModule);
                departmentOld = em.merge(departmentOld);
            }
            if (departmentNew != null && !departmentNew.equals(departmentOld)) {
                departmentNew.getDepartmentsByModuleList().add(departmentsByModule);
                departmentNew = em.merge(departmentNew);
            }
            if (moduleOld != null && !moduleOld.equals(moduleNew)) {
                moduleOld.getDepartmentsByModuleList().remove(departmentsByModule);
                moduleOld = em.merge(moduleOld);
            }
            if (moduleNew != null && !moduleNew.equals(moduleOld)) {
                moduleNew.getDepartmentsByModuleList().add(departmentsByModule);
                moduleNew = em.merge(moduleNew);
            }
            for (Report reportListNewReport : reportListNew) {
                if (!reportListOld.contains(reportListNewReport)) {
                    DepartmentsByModule oldDepartmentsByModuleOfReportListNewReport = reportListNewReport.getDepartmentsByModule();
                    reportListNewReport.setDepartmentsByModule(departmentsByModule);
                    reportListNewReport = em.merge(reportListNewReport);
                    if (oldDepartmentsByModuleOfReportListNewReport != null && !oldDepartmentsByModuleOfReportListNewReport.equals(departmentsByModule)) {
                        oldDepartmentsByModuleOfReportListNewReport.getReportList().remove(reportListNewReport);
                        oldDepartmentsByModuleOfReportListNewReport = em.merge(oldDepartmentsByModuleOfReportListNewReport);
                    }
                }
            }
            for (Client clientListNewClient : clientListNew) {
                if (!clientListOld.contains(clientListNewClient)) {
                    DepartmentsByModule oldDepartmentsByModuleOfClientListNewClient = clientListNewClient.getDepartmentsByModule();
                    clientListNewClient.setDepartmentsByModule(departmentsByModule);
                    clientListNewClient = em.merge(clientListNewClient);
                    if (oldDepartmentsByModuleOfClientListNewClient != null && !oldDepartmentsByModuleOfClientListNewClient.equals(departmentsByModule)) {
                        oldDepartmentsByModuleOfClientListNewClient.getClientList().remove(clientListNewClient);
                        oldDepartmentsByModuleOfClientListNewClient = em.merge(oldDepartmentsByModuleOfClientListNewClient);
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
                DepartmentsByModulePK id = departmentsByModule.getDepartmentsByModulePK();
                if (findDepartmentsByModule(id) == null) {
                    throw new NonexistentEntityException("The departmentsByModule with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(DepartmentsByModulePK id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DepartmentsByModule departmentsByModule;
            try {
                departmentsByModule = em.getReference(DepartmentsByModule.class, id);
                departmentsByModule.getDepartmentsByModulePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departmentsByModule with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Report> reportListOrphanCheck = departmentsByModule.getReportList();
            for (Report reportListOrphanCheckReport : reportListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DepartmentsByModule (" + departmentsByModule + ") cannot be destroyed since the Report " + reportListOrphanCheckReport + " in its reportList field has a non-nullable departmentsByModule field.");
            }
            List<Client> clientListOrphanCheck = departmentsByModule.getClientList();
            for (Client clientListOrphanCheckClient : clientListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DepartmentsByModule (" + departmentsByModule + ") cannot be destroyed since the Client " + clientListOrphanCheckClient + " in its clientList field has a non-nullable departmentsByModule field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Priority priorityID = departmentsByModule.getPriorityID();
            if (priorityID != null) {
                priorityID.getDepartmentsByModuleList().remove(departmentsByModule);
                priorityID = em.merge(priorityID);
            }
            Floor floorID = departmentsByModule.getFloorID();
            if (floorID != null) {
                floorID.getDepartmentsByModuleList().remove(departmentsByModule);
                floorID = em.merge(floorID);
            }
            Department department = departmentsByModule.getDepartment();
            if (department != null) {
                department.getDepartmentsByModuleList().remove(departmentsByModule);
                department = em.merge(department);
            }
            Module module = departmentsByModule.getModule();
            if (module != null) {
                module.getDepartmentsByModuleList().remove(departmentsByModule);
                module = em.merge(module);
            }
            em.remove(departmentsByModule);
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

    public List<DepartmentsByModule> findDepartmentsByModuleEntities() {
        return findDepartmentsByModuleEntities(true, -1, -1);
    }

    public List<DepartmentsByModule> findDepartmentsByModuleEntities(int maxResults, int firstResult) {
        return findDepartmentsByModuleEntities(false, maxResults, firstResult);
    }

    private List<DepartmentsByModule> findDepartmentsByModuleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DepartmentsByModule.class));
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

    public DepartmentsByModule findDepartmentsByModule(DepartmentsByModulePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DepartmentsByModule.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartmentsByModuleCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DepartmentsByModule> rt = cq.from(DepartmentsByModule.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
