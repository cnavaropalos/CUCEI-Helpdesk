
package mx.udg.helpdesk.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "work_blogs")
@NamedQueries(
{
    @NamedQuery(name = "WorkBlog.findAll", query = "SELECT w FROM WorkBlog w"),
    @NamedQuery(name = "WorkBlog.findByWorkBlogID", query = "SELECT w FROM WorkBlog w WHERE w.workBlogID = :workBlogID"),
    @NamedQuery(name = "WorkBlog.findByBlogDate", query = "SELECT w FROM WorkBlog w WHERE w.blogDate = :blogDate")
})
public class WorkBlog implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "workBlogID")
    private Integer workBlogID;
    @Basic(optional = false)
    @NotNull
    @Column(name = "blogDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date blogDate;
    @JoinColumn(name = "statusID", referencedColumnName = "statusID")
    @ManyToOne(optional = false)
    private Status statusID;
    @JoinColumns(
    {
        @JoinColumn(name = "reportID", referencedColumnName = "reportID"),
        @JoinColumn(name = "reportDate", referencedColumnName = "reportDate")
    })
    @ManyToOne(optional = false)
    private Report report;
    @JoinColumn(name = "reportManagerID", referencedColumnName = "userID")
    @ManyToOne(optional = false)
    private ReportManager reportManagerID;

    public WorkBlog()
    {
    }

    public WorkBlog(Integer workBlogID)
    {
        this.workBlogID = workBlogID;
    }

    public WorkBlog(Integer workBlogID, Date blogDate)
    {
        this.workBlogID = workBlogID;
        this.blogDate = blogDate;
    }

    public Integer getWorkBlogID()
    {
        return workBlogID;
    }

    public void setWorkBlogID(Integer workBlogID)
    {
        this.workBlogID = workBlogID;
    }

    public Date getBlogDate()
    {
        return blogDate;
    }

    public void setBlogDate(Date blogDate)
    {
        this.blogDate = blogDate;
    }

    public Status getStatusID()
    {
        return statusID;
    }

    public void setStatusID(Status statusID)
    {
        this.statusID = statusID;
    }

    public Report getReport()
    {
        return report;
    }

    public void setReport(Report report)
    {
        this.report = report;
    }

    public ReportManager getReportManagerID()
    {
        return reportManagerID;
    }

    public void setReportManagerID(ReportManager reportManagerID)
    {
        this.reportManagerID = reportManagerID;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (workBlogID != null ? workBlogID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof WorkBlog))
        {
            return false;
        }
        WorkBlog other = (WorkBlog) object;
        if ((this.workBlogID == null && other.workBlogID != null) || (this.workBlogID != null && !this.workBlogID.equals(other.workBlogID)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "mx.udg.helpdesk.entities.WorkBlog[ workBlogID=" + workBlogID + " ]";
    }
    
}
