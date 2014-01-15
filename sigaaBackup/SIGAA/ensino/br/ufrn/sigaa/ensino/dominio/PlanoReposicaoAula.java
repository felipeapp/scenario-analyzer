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
import br.ufrn.sigaa.ava.dominio.AulaExtra;

/**
 * Plano de reposição de aula apresentado pelo professor.
 * 
 * @author Henrique André
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "ensino", name = "plano_reposicao_aula")
public class PlanoReposicaoAula implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_plano_reposicao_aula")
	private int id;

	@ManyToOne
	@JoinColumn(name = "id_falta_homologada")
	private AvisoFaltaDocenteHomologada faltaHomologada;

	@ManyToOne
	@JoinColumn(name = "id_parecer")
	private ParecerPlanoReposicaoAula parecer;

	@Column(name = "didatica")
	private String didatica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_aula_reposicao")
	private Date dataAulaReposicao;

	@Transient
	private String horaAulaReposicao;
	
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

	@ManyToOne
	@JoinColumn(name = "id_aula_extra")
	private AulaExtra aulaExtra;
	
	/**
	 * Indica se o plano foi aprovado pelo chefe de departamento
	 * 
	 * @return
	 */
	public boolean isAprovado() {
		if (parecer == null)
			return false;
		
		return parecer.getStatus().getId() == StatusParecerPlanoReposicaoAula.APROVADO;
	}
	
	/**
	 * Indica se o plano foi aprovado pelo chefe de departamento
	 * 
	 * @return
	 */
	public boolean isNegado() {
		if (parecer == null)
			return true;
		
		return parecer.getStatus().getId() == StatusParecerPlanoReposicaoAula.NEGADO;
	}	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AvisoFaltaDocenteHomologada getFaltaHomologada() {
		return faltaHomologada;
	}

	public void setFaltaHomologada(AvisoFaltaDocenteHomologada faltaHomologada) {
		this.faltaHomologada = faltaHomologada;
	}

	public ParecerPlanoReposicaoAula getParecer() {
		return parecer;
	}

	public void setParecer(ParecerPlanoReposicaoAula parecer) {
		this.parecer = parecer;
	}

	public String getDidatica() {
		return didatica;
	}

	public void setDidatica(String didatica) {
		this.didatica = didatica;
	}

	public Date getDataAulaReposicao() {
		return dataAulaReposicao;
	}

	public void setDataAulaReposicao(Date dataAulaReposicao) {
		this.dataAulaReposicao = dataAulaReposicao;
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

	public String getHoraAulaReposicao() {
		return horaAulaReposicao;
	}

	public void setHoraAulaReposicao(String horaAulaReposicao) {
		this.horaAulaReposicao = horaAulaReposicao;
	}

	public AulaExtra getAulaExtra() {
		return aulaExtra;
	}

	public void setAulaExtra(AulaExtra aulaExtra) {
		this.aulaExtra = aulaExtra;
	}

}

