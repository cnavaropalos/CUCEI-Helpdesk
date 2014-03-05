package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Carlos Navapa
 */
@Entity
@Table(name = "departments_by_module")
@NamedQueries({
    @NamedQuery(name = "DepartmentsByModule.findAll", query = "SELECT d FROM DepartmentsByModule d")})
public class DepartmentsByModule implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DepartmentsByModulePK departmentsByModulePK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "departmentsByModule")
    private List<Report> reportList;
    @JoinColumn(name = "priorityID", referencedColumnName = "priorityID")
    @ManyToOne(optional = false)
    private Priority priorityID;
    @JoinColumn(name = "floorID", referencedColumnName = "floorID")
    @ManyToOne(optional = false)
    private Floor floorID;
    @JoinColumn(name = "departmentID", referencedColumnName = "departmentID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Department department;
    @JoinColumn(name = "moduleID", referencedColumnName = "moduleID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Module module;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "departmentsByModule")
    private List<Client> clientList;

    public DepartmentsByModule() {
    }

    public DepartmentsByModule(DepartmentsByModulePK departmentsByModulePK) {
        this.departmentsByModulePK = departmentsByModulePK;
    }

    public DepartmentsByModule(int moduleID, int departmentID) {
        this.departmentsByModulePK = new DepartmentsByModulePK(moduleID, departmentID);
    }

    public DepartmentsByModulePK getDepartmentsByModulePK() {
        return departmentsByModulePK;
    }

    public void setDepartmentsByModulePK(DepartmentsByModulePK departmentsByModulePK) {
        this.departmentsByModulePK = departmentsByModulePK;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public Priority getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(Priority priorityID) {
        this.priorityID = priorityID;
    }

    public Floor getFloorID() {
        return floorID;
    }

    public void setFloorID(Floor floorID) {
        this.floorID = floorID;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public List<Client> getClientList() {
        return clientList;
    }

    public void setClientList(List<Client> clientList) {
        this.clientList = clientList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (departmentsByModulePK != null ? departmentsByModulePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DepartmentsByModule)) {
            return false;
        }
        DepartmentsByModule other = (DepartmentsByModule) object;
        if ((this.departmentsByModulePK == null && other.departmentsByModulePK != null) || (this.departmentsByModulePK != null && !this.departmentsByModulePK.equals(other.departmentsByModulePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.DepartmentsByModule[ departmentsByModulePK=" + departmentsByModulePK + " ]";
    }

}
