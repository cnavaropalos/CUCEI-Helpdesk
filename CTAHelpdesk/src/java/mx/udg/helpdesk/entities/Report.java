package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Carlos Navapa
 */
@Entity
@Table(name = "reports")
@NamedQueries({
    @NamedQuery(name = "Report.findAll", query = "SELECT r FROM Report r")})
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ReportPK reportPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "reportTime")
    @Temporal(TemporalType.TIME)
    private Date reportTime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 500)
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "notes")
    private String notes;
    @JoinColumn(name = "clientID", referencedColumnName = "userID")
    @ManyToOne(optional = false)
    private Client clientID;
    @JoinColumn(name = "capturisID", referencedColumnName = "userID")
    @ManyToOne(optional = false)
    private Capturist capturisID;
    @JoinColumn(name = "problemID", referencedColumnName = "problemID")
    @ManyToOne(optional = false)
    private ProblemCategorie problemID;
    @JoinColumns({
        @JoinColumn(name = "moduleID", referencedColumnName = "moduleID"),
        @JoinColumn(name = "departmentID", referencedColumnName = "departmentID")})
    @ManyToOne(optional = false)
    private DepartmentsByModule departmentsByModule;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<WorkBlog> workBlogList;

    public Report() {
    }

    public Report(ReportPK reportPK) {
        this.reportPK = reportPK;
    }

    public Report(ReportPK reportPK, Date reportTime, String description, String notes) {
        this.reportPK = reportPK;
        this.reportTime = reportTime;
        this.description = description;
        this.notes = notes;
    }

    public Report(int reportID, Date reportDate) {
        this.reportPK = new ReportPK(reportID, reportDate);
    }

    public ReportPK getReportPK() {
        return reportPK;
    }

    public void setReportPK(ReportPK reportPK) {
        this.reportPK = reportPK;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Client getClientID() {
        return clientID;
    }

    public void setClientID(Client clientID) {
        this.clientID = clientID;
    }

    public Capturist getCapturisID() {
        return capturisID;
    }

    public void setCapturisID(Capturist capturisID) {
        this.capturisID = capturisID;
    }

    public ProblemCategorie getProblemID() {
        return problemID;
    }

    public void setProblemID(ProblemCategorie problemID) {
        this.problemID = problemID;
    }

    public DepartmentsByModule getDepartmentsByModule() {
        return departmentsByModule;
    }

    public void setDepartmentsByModule(DepartmentsByModule departmentsByModule) {
        this.departmentsByModule = departmentsByModule;
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
        hash += (reportPK != null ? reportPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Report)) {
            return false;
        }
        Report other = (Report) object;
        if ((this.reportPK == null && other.reportPK != null) || (this.reportPK != null && !this.reportPK.equals(other.reportPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mx.udg.helpdesk.entities.Report[ reportPK=" + reportPK + " ]";
    }

}
