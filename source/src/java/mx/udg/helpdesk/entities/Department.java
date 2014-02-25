package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "departments")
@NamedQueries(
        {
            @NamedQuery(name = "Department.findAll", query = "SELECT d FROM Department d"),
            @NamedQuery(name = "Department.findByDepartmentID", query = "SELECT d FROM Department d WHERE d.departmentID = :departmentID"),
            @NamedQuery(name = "Department.findByName", query = "SELECT d FROM Department d WHERE d.name = :name")
        })
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "departmentID")
    private Integer departmentID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "department")
    private List<DepartmentByModule> departmentByModuleList;

    public Department() {
    }

    public Department(Integer departmentID) {
        this.departmentID = departmentID;
    }

    public Department(Integer departmentID, String name) {
        this.departmentID = departmentID;
        this.name = name;
    }

    public Integer getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DepartmentByModule> getDepartmentByModuleList() {
        return departmentByModuleList;
    }

    public void setDepartmentByModuleList(List<DepartmentByModule> departmentByModuleList) {
        this.departmentByModuleList = departmentByModuleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (departmentID != null ? departmentID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Department)) {
            return false;
        }
        Department other = (Department) object;
        if ((this.departmentID == null && other.departmentID != null) || (this.departmentID != null && !this.departmentID.equals(other.departmentID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Department[ departmentID=" + departmentID + " ]";
    }

}
