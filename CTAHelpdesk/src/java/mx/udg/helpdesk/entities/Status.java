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
@Table(name = "status")
@NamedQueries(
        {
            @NamedQuery(name = "Status.findAll", query = "SELECT s FROM Status s"),
            @NamedQuery(name = "Status.findByStatusID", query = "SELECT s FROM Status s WHERE s.statusID = :statusID"),
            @NamedQuery(name = "Status.findByName", query = "SELECT s FROM Status s WHERE s.name = :name")
        })
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "statusID")
    private Integer statusID;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusID")
    private List<WorkBlog> workBlogList;

    public Status() {
    }

    public Status(Integer statusID) {
        this.statusID = statusID;
    }

    public Status(Integer statusID, String name) {
        this.statusID = statusID;
        this.name = name;
    }

    public Integer getStatusID() {
        return statusID;
    }

    public void setStatusID(Integer statusID) {
        this.statusID = statusID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkBlog> getWorkBlogList() {
        return workBlogList;
    }

    public void setWorkBlogList(List<WorkBlog> workBlogList) {
        this.workBlogList = workBlogList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusID != null ? statusID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Status)) {
            return false;
        }
        Status other = (Status) object;
        if ((this.statusID == null && other.statusID != null) || (this.statusID != null && !this.statusID.equals(other.statusID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Status[ statusID=" + statusID + " ]";
    }

}
