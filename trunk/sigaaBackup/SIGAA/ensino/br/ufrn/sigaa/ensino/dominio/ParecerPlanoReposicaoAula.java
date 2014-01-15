/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/06/2009
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Depois que o professor submete o plano de aula, o chefe de departamento tem
 * que dar o parecer aprovando ou negando o que foi proposto pelo professor.
 * 
 * @author Henrique André
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "ensino", name = "parecer_plano_reposicao_aula")
public class ParecerPlanoReposicaoAula implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_parecer")
	private int id;

	@Column(name = "justificativa")
	private String justificativa;

	@ManyToOne
	@JoinColumn(name = "id_status_parecer")
	private StatusParecerPlanoReposicaoAula status;

	/**
	 * Registro de entrada para quando o registro for criado
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/**
	 * Data que o registro foi criado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/**
	 * Registro de entrada para quando o registro for atualizado
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao")
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/**
	 * Data que o registro foi atualizado
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao")
	@AtualizadoEm
	private Date dataAlteracao;

	@Transient
	private PlanoReposicaoAula planoAula;
	
	/**
	 * Seta o plano de aula no parecer e seta também o parecer no plano de aula
	 * 
	 * @param planoAula
	 */
	public void adicionarPlanoAula(PlanoReposicaoAula planoAula) {
		setPlanoAula(planoAula);
		planoAula.setParecer(this);
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public StatusParecerPlanoReposicaoAula getStatus() {
		return status;
	}

	public void setStatus(StatusParecerPlanoReposicaoAula status) {
		this.status = status;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public PlanoReposicaoAula getPlanoAula() {
		return planoAula;
	}

	public void setPlanoAula(PlanoReposicaoAula planoAula) {
		this.planoAula = planoAula;
	}
	
	public boolean isAprovado() {
		if (status != null)
			return status.getId() == StatusParecerPlanoReposicaoAula.APROVADO;
		return false;
	}

	
}
