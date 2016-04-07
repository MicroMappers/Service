package qa.qcri.mm.api.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "image_config")
public class ImageConfig implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4209080697905661477L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

	@ManyToOne
	@JoinColumn(name = "client_app_id")
    private ClientApp clientApp;

    @Column (name = "source", nullable = true)
    private String source;
    
    @Column(name = "rows")
    private Integer rows;
    
    @Column(name = "columns")
    private Integer columns;

    @Column (name = "created_at")
    private Date createdAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ClientApp getClientApp() {
		return clientApp;
	}

	public void setClientApp(ClientApp clientApp) {
		this.clientApp = clientApp;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}
    
}
