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

/**
 *
 * @author Carlos Navapa
 */
@Entity
@Table(name = "priorities")
@NamedQueries({
    @NamedQuery(name = "Priority.findAll", query = "SELECT p FROM Priority p")})
public class Priority implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "priorityID")
    private Integer priorityID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "level")
    private String level;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "priorityID")
    private List<DepartmentsByModule> departmentsByModuleList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "priorityID")
    private List<ProblemCategorie> problemCategorieList;

    public Priority() {
    }

    public Priority(Integer priorityID) {
        this.priorityID = priorityID;
    }

    public Priority(Integer priorityID, String level) {
        this.priorityID = priorityID;
        this.level = level;
    }

    public Integer getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(Integer priorityID) {
        this.priorityID = priorityID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<DepartmentsByModule> getDepartmentsByModuleList() {
        return departmentsByModuleList;
    }

    public void setDepartmentsByModuleList(List<DepartmentsByModule> departmentsByModuleList) {
        this.departmentsByModuleList = departmentsByModuleList;
    }

    public List<ProblemCategorie> getProblemCategorieList() {
        return problemCategorieList;
    }

    public void setProblemCategorieList(List<ProblemCategorie> problemCategorieList) {
        this.problemCategorieList = problemCategorieList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (priorityID != null ? priorityID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Priority)) {
            return false;
        }
        Priority other = (Priority) object;
        if ((this.priorityID == null && other.priorityID != null) || (this.priorityID != null && !this.priorityID.equals(other.priorityID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Priority[ priorityID=" + priorityID + " ]";
    }

}
