package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "componente_curricular_rede", schema = "ensino_rede")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ComponenteCurricularRede {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="ensino_rede.componente_rede_seq") }) 	
	@Column(name = "id_disciplina", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	private String codigo;
	private String nome;

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name = "id_programa")
	private ProgramaRede programa;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setPrograma(ProgramaRede programa) {
		this.programa = programa;
	}

	public ProgramaRede getPrograma() {
		return programa;
	}
	
	/** Retorna uma descrição resumida deste componente curricular. 
	 * @return
	 */
	@Transient
	public String getDescricaoResumida() {
		return getCodigo() + " - " + nome;
	}

}
