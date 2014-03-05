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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "problem_categories")
@NamedQueries({
    @NamedQuery(name = "ProblemCategorie.findAll", query = "SELECT p FROM ProblemCategorie p")})
public class ProblemCategorie implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "problemID")
    private Integer problemID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 70)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "problemID")
    private List<Report> reportList;
    @JoinColumn(name = "priorityID", referencedColumnName = "priorityID")
    @ManyToOne(optional = false)
    private Priority priorityID;
    @JoinColumn(name = "areaID", referencedColumnName = "areaID")
    @ManyToOne(optional = false)
    private Area areaID;

    public ProblemCategorie() {
    }

    public ProblemCategorie(Integer problemID) {
        this.problemID = problemID;
    }

    public ProblemCategorie(Integer problemID, String name, String description) {
        this.problemID = problemID;
        this.name = name;
        this.description = description;
    }

    public Integer getProblemID() {
        return problemID;
    }

    public void setProblemID(Integer problemID) {
        this.problemID = problemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Area getAreaID() {
        return areaID;
    }

    public void setAreaID(Area areaID) {
        this.areaID = areaID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (problemID != null ? problemID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProblemCategorie)) {
            return false;
        }
        ProblemCategorie other = (ProblemCategorie) object;
        if ((this.problemID == null && other.problemID != null) || (this.problemID != null && !this.problemID.equals(other.problemID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.ProblemCategorie[ problemID=" + problemID + " ]";
    }

}
