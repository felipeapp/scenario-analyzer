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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import br.ufrn.arq.util.HibernateUtils;

/**
 * Quando o professor falta uma aula, os alunos podem avisar a falta deste professor.
 * O chefe de departamento então homologa o aviso dos discentes e o professor fica com a 
 * obrigação de apresentar um plano de aula de reposição.
 * 
 * @author Henrique André
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "aviso_falta_docente_homologada", schema = "ensino")
public class AvisoFaltaDocenteHomologada implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_falta_homologada")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_dados_aviso")
	private DadosAvisoFalta dadosAvisoFalta;
	
	/**
	 * Plano aprovado
	 */
	@ManyToOne
	@JoinColumn(name = "id_plano_aprovado")
	private PlanoReposicaoAula planoAprovado;
	
	/**
	 * Todos os planos de resposição de aula que foram submetidos pelo professor
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy="faltaHomologada")
	private List<PlanoReposicaoAula> planosResposicao;

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
	
	/**
	 * Motivo que levou a negação do aviso de falta
	 */
	@Column(name = "motivo_negacao", columnDefinition = HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String motivoNegacao;
	
	@Column(name = "ativo")
	private boolean ativo = true;
	
	@ManyToOne
	@JoinColumn(name = "id_movimentacao")
	private MovimentacaoAvisoFaltaHomologado movimentacao = new MovimentacaoAvisoFaltaHomologado();
	
	/**
	 * Diz se o docente possui frequencia eletronica cadastrada na data do aviso.
	 */
	@Transient
	private boolean frequenciaEletronica;

	public boolean isFrequenciaEletronica() {
		return frequenciaEletronica;
	}

	public void setFrequenciaEletronica(boolean frequenciaEletronica) {
		this.frequenciaEletronica = frequenciaEletronica;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DadosAvisoFalta getDadosAvisoFalta() {
		return dadosAvisoFalta;
	}

	public void setDadosAvisoFalta(DadosAvisoFalta dadosAvisoFalta) {
		this.dadosAvisoFalta = dadosAvisoFalta;
	}

	public List<PlanoReposicaoAula> getPlanosResposicao() {
		return planosResposicao;
	}

	public void setPlanosResposicao(List<PlanoReposicaoAula> planosResposicao) {
		this.planosResposicao = planosResposicao;
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

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public PlanoReposicaoAula getPlanoAprovado() {
		return planoAprovado;
	}

	public void setPlanoAprovado(PlanoReposicaoAula planoAprovado) {
		this.planoAprovado = planoAprovado;
	}

	public MovimentacaoAvisoFaltaHomologado getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(MovimentacaoAvisoFaltaHomologado situacao) {
		this.movimentacao = situacao;
	}

	public String getMotivoNegacao() {
		return motivoNegacao;
	}

	public void setMotivoNegacao(String motivoNegacao) {
		this.motivoNegacao = motivoNegacao;
	}

	public boolean isNegado() {
		return movimentacao != null && movimentacao.getId() == MovimentacaoAvisoFaltaHomologado.HOMOLOGACAO_NEGADA.getId();
	}
	
	public boolean isPendenteAprovacaoPlano() {
		return movimentacao != null && (movimentacao.getId() == MovimentacaoAvisoFaltaHomologado.PLANO_PENDENTE_APROVACAO_CHEFIA.getId());
	}
}
