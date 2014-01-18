package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.dominio.Usuario;

@Entity
@Table(name = "alteracao_status_discente_associado", schema = "ensino_rede")
public class AlteracaoStatusDiscenteAssociado implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ensino_rede.alteracao_status_discente_associado_seq") })
	@Column(name = "id_alteracao_status_discente_associado")
	private int id;

	/** Situação do discente antigo*/
	private int status;
	
	/** Situação do discente*/
	@Column(name = "status_novo")
	private Integer statusNovo;
	/**
	 * usuário que realizou a alteração
	 */
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/**
	 * discente q teve o status alterado
	 */
	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada entrada;
	
	/**
	 * discente q teve o status alterado
	 */
	@ManyToOne
	@JoinColumn(name = "id_discente")
	private DiscenteAssociado discente;

	/**
	 * data da alteração
	 */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/** código do movimento que está alterando o status do aluno */
	private int movimento;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public DiscenteAssociado getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAssociado discente) {
		this.discente = discente;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getMovimento() {
		return movimento;
	}

	public void setMovimento(int movimento) {
		this.movimento = movimento;
	}

	public RegistroEntrada getEntrada() {
		return entrada;
	}

	public void setEntrada(RegistroEntrada entrada) {
		this.entrada = entrada;
	}
	
	public Integer getStatusNovo() {
		return statusNovo;
	}

	public int getStatus() {
		return status;
	}
	
	public void setStatusNovo(Integer statusNovo) {
		this.statusNovo = statusNovo;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	
	
}