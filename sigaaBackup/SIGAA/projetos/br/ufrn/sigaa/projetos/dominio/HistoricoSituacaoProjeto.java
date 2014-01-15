/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/12/2006
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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


/**
 * Esta entidade armazena o histórico de alteração de todas as situações do projeto.
 * @author ilueny santos
 */
@Entity
@Table(name = "historico_situacao_projeto", schema = "projetos")
public class HistoricoSituacaoProjeto implements PersistDB {

	private int id;

	/** Data que foi realizada a alteração */
	private Date data;

	/** Campo responsável por armazenar a situação para qual o projeto está sendo alterado */
	private TipoSituacaoProjeto situacaoProjeto;

	/** Referência do projeto */
	private Projeto projeto;

	/** O registro entrada de quem realizou a alteração */
	private RegistroEntrada registroEntrada;

	public HistoricoSituacaoProjeto() {
	}

	public HistoricoSituacaoProjeto(int idHistoricoProjeto) {
		this.id = idHistoricoProjeto;
	}


	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_historico_situacao_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", unique = false, nullable = false, insertable = true, updatable = true, length = 4)
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto", unique = false, nullable = false, insertable = true, updatable = true)
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_situacao_projeto", unique = false, nullable = false, insertable = true, updatable = true)
	public TipoSituacaoProjeto getSituacaoProjeto() {
		return situacaoProjeto;
	}

	public void setSituacaoProjeto(
			TipoSituacaoProjeto situacaoProjeto) {
		this.situacaoProjeto = situacaoProjeto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada", unique = false, insertable = true, updatable = true, nullable = true)
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

}
