
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

@Entity
@Table(name = "departments_by_module")
@NamedQueries(
{
    @NamedQuery(name = "DepartmentByModule.findAll", query = "SELECT d FROM DepartmentByModule d"),
    @NamedQuery(name = "DepartmentByModule.findByModuleID", query = "SELECT d FROM DepartmentByModule d WHERE d.departmentByModulePK.moduleID = :moduleID"),
    @NamedQuery(name = "DepartmentByModule.findByDepartmentID", query = "SELECT d FROM DepartmentByModule d WHERE d.departmentByModulePK.departmentID = :departmentID")
})
public class DepartmentByModule implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected DepartmentByModulePK departmentByModulePK;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "departmentByModule")
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

    public DepartmentByModule()
    {
    }

    public DepartmentByModule(DepartmentByModulePK departmentByModulePK)
    {
        this.departmentByModulePK = departmentByModulePK;
    }

    public DepartmentByModule(int moduleID, int departmentID)
    {
        this.departmentByModulePK = new DepartmentByModulePK(moduleID, departmentID);
    }

    public DepartmentByModulePK getDepartmentByModulePK()
    {
        return departmentByModulePK;
    }

    public void setDepartmentByModulePK(DepartmentByModulePK departmentByModulePK)
    {
        this.departmentByModulePK = departmentByModulePK;
    }

    public List<Report> getReportList()
    {
        return reportList;
    }

    public void setReportList(List<Report> reportList)
    {
        this.reportList = reportList;
    }

    public Priority getPriorityID()
    {
        return priorityID;
    }

    public void setPriorityID(Priority priorityID)
    {
        this.priorityID = priorityID;
    }

    public Floor getFloorID()
    {
        return floorID;
    }

    public void setFloorID(Floor floorID)
    {
        this.floorID = floorID;
    }

    public Department getDepartment()
    {
        return department;
    }

    public void setDepartment(Department department)
    {
        this.department = department;
    }

    public Module getModule()
    {
        return module;
    }

    public void setModule(Module module)
    {
        this.module = module;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (departmentByModulePK != null ? departmentByModulePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DepartmentByModule))
        {
            return false;
        }
        DepartmentByModule other = (DepartmentByModule) object;
        if ((this.departmentByModulePK == null && other.departmentByModulePK != null) || (this.departmentByModulePK != null && !this.departmentByModulePK.equals(other.departmentByModulePK)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mx.udg.helpdesk.entities.DepartmentByModule[ departmentByModulePK=" + departmentByModulePK + " ]";
    }
    
}
