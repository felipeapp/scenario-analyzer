/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.rh.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.ResponsavelUnidadeAvaliacao;
import br.ufrn.comum.dominio.UnidadeGeral;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 *
 * Classe que contém os dados de um servidor da UFRN
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "servidor", schema = "rh")
public class Servidor implements PersistDB {

	/** Identificador do servidor*/
	@Id
	@Column(name = "id_servidor", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Matrícula SIAPE do servidor */
	protected int siape;

	/** Matrícula interna do servidor */
	@Transient
	protected Integer matriculaInterna;
	
	/** Digito da matricula do servidor no SIAPE*/
	@Column(name="digito_siape")
	protected Character digitoSiape;

	/** Dados da pessoa associada aos dados do servidor */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_pessoa")
	protected PessoaGeral pessoa;

	/** Cargo assumido pelo servidor */
	@Transient
	protected Cargo cargo;

	/** Escolaridade do servidor */
	@Transient
	protected Escolaridade escolaridade;

	/** Categoria do servidor */
	@Transient
	protected Categoria categoria;

	/** Informação do status da atividade do servidor */
	@Transient
	protected Ativo ativo;

	/** Situação do Servidor (Contrato Temporário, Médico Residente, etc)*/
	@Transient
	protected Situacao situacao;

	/**  Informação da atividade que o servidor exerce.  */
	@Transient
	protected AtividadeServidor atividade;

	/** Nome da unidade de lotação do servidor no SIAPE */
	protected String lotacao;

	/** Código da unidade de lotação do servidor no SIAPE */
	@Transient
	private String uorg;

	/** Armazena data e hora da última atualização dos dados do servidor */
	@Transient
	protected Date ultimaAtualizacao;

	/** Informa o valor do auxílio transporte recebido pelo servidor */
	@Transient
	protected double auxilioTransporte;
	
	/** Informa o valor do vale alimentação recebido pelo servidor */
	@Transient
	protected Double valeAlimentacao;

	/**  Formação representa se o servidor tem doutorado, mestrado, etc*/
	@Transient
	protected Formacao formacao;

	/** Identifica a jornada de trabalho do servidor */
	@Transient
	protected Integer regimeTrabalho;

	/** Unidade de exercício atribuída a este servidor*/
	@Transient
	protected UnidadeGeral unidade;
	
	/** Unidade de Lotação atribuída a este servidor, campo útil quando se utiliza o conceito de exercício */
	@Transient
	protected UnidadeGeral unidadeLotacao;

	/** Informa se o servidor foi contratado em regime de dedicação exclusiva ou não */
	@Transient
	protected boolean dedicacaoExclusiva;

	/** Descrição da categoria */
	@Deprecated
	@Transient
	protected String tipoVinculo;
	
	@Transient
	protected Collection<Designacao> designacoes;
	
	/** Coleção que retorna as unidades para qual o servidor é responsável de avaliação */
	@Transient
	protected Collection<ResponsavelUnidadeAvaliacao> responsavelUnidadesAvaliacao;
	
	/** Classe funcional do servidor: A, B, C, D, E, Adjunto, Assistente, etc */
	@Transient
	protected ClasseFuncional classeFuncional;
	
	/**
	 * informa a data de desligamento do funcionário, caso não seja mais ativo, ou nulo, caso seja ativo
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="data_desligamento")
	private Date dataDesligamento;
	
	/** Nome que identifica o servidor na instituição,
	 * tal como um apelido ou mesmo um nome de guerra
	 * em instituições policiais ou militares.
	 */
	@Column (name="nome_identificacao")
	private String nomeIdentificacao;

	public Servidor() {
		pessoa = new PessoaGeral();
	}

	public Servidor(int id) {
		this.id = id;
	}

	/**
	 * Construtor usado em projeção do hibernate.
	 */
	public Servidor(int id, String nome, int siape, int idPessoa) {
		this.id = id;
		this.siape = siape;
		this.pessoa = new PessoaGeral(idPessoa);
		this.pessoa.setNome(nome);

	}

	public Formacao getFormacao() {
		return pessoa.getFormacao();
	}

	public void setFormacao(Formacao formacao) {
		this.formacao = formacao;
	}
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public PessoaGeral getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaGeral pessoa) {
		this.pessoa = pessoa;
	}


	public Cargo getCargo() {
		return cargo;
	}

	public void setCargo(Cargo cargo) {
		this.cargo = cargo;
	}

	public Ativo getAtivo() {
		return ativo;
	}

	public void setAtivo(Ativo ativo) {
		this.ativo = ativo;
	}


	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Escolaridade getEscolaridade() {
		return escolaridade;
	}

	public void setEscolaridade(Escolaridade escolaridade) {
		this.escolaridade = escolaridade;
	}

	public Situacao getSituacao() {
		return situacao;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}

	public Collection<Designacao> getDesignacoes() {
		return designacoes;
	}

	/**
	 * Retorna as designações do servidor que estão ativas.
	 * @return
	 */
	public Collection<Designacao> getDesignacoesAtivas() {
		Collection<Designacao> copia = new ArrayList<Designacao>();
			
		if(!ValidatorUtil.isEmpty(designacoes)) {
			copia = new ArrayList<Designacao>(designacoes);

			for (Iterator<Designacao> it = copia.iterator(); it.hasNext(); ) {
				Designacao d = it.next();
				if (d.getFim() != null && d.getFim().before(new Date()))
					it.remove();
			}
		}

		return copia;
	}

	public void setDesignacoes(Collection<Designacao> designacoes) {
		this.designacoes = designacoes;
	}

	/**
	 * @return Returns the lotacao.
	 */
	public String getLotacao() {
		return lotacao;
	}

	/**
	 * @param lotacao The lotacao to set.
	 */
	public void setLotacao(String lotacao) {
		this.lotacao = lotacao;
	}

	public Date getUltimaAtualizacao() {
		return ultimaAtualizacao;
	}

	public void setUltimaAtualizacao(Date ultimaAtualizacao) {
		this.ultimaAtualizacao = ultimaAtualizacao;
	}

	/**
	 * @return the auxilioTransporte
	 */
	public double getAuxilioTransporte() {
		return auxilioTransporte;
	}

	/**
	 * @param auxilioTransporte the auxilioTransporte to set
	 */
	public void setAuxilioTransporte(double auxilioTransporte) {
		this.auxilioTransporte = auxilioTransporte;
	}
	
	public Double getValeAlimentacao() {
		return valeAlimentacao;
	}

	public void setValeAlimentacao(Double valeAlimentacao) {
		this.valeAlimentacao = valeAlimentacao;
	}

	public boolean isDedicacaoExclusiva() {
		return dedicacaoExclusiva;
	}

	public void setDedicacaoExclusiva(boolean dedicacaoExclusiva) {
		this.dedicacaoExclusiva = dedicacaoExclusiva;
	}

	public Integer getRegimeTrabalho() {
		return regimeTrabalho;
	}

	public void setRegimeTrabalho(Integer regimeTrabalho) {
		this.regimeTrabalho = regimeTrabalho;
	}

	public String getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(String tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public Character getDigitoSiape() {
		return digitoSiape;
	}

	public void setDigitoSiape(Character digitoSiape) {
		this.digitoSiape = digitoSiape;
	}
	
	public AtividadeServidor getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeServidor atividade) {
		this.atividade = atividade;
	}

	public int getSiape() {
		return siape;
	}

	public void setSiape(int siape) {
		this.siape = siape;
	}

	public String getUorg() {
		return this.uorg;
	}

	public void setUorg(String uorg) {
		this.uorg = uorg;
	}

	/** Retorna nome da pessoa concatenado com siape */
	public String getNomeSiape() {
		String apelido = "";
		if (!isEmpty(nomeIdentificacao)){
			apelido = " - "+nomeIdentificacao+" - ";
		}
		return pessoa.getNome() +apelido+ " (" + getSiape() + ")";
	}

	public String getNomeSiapeCategoria(){

		return pessoa.getNome() + " (" + siape + " - " + categoria.getDescricao() + ")";
	}

	@Override
	public String toString() {
		return pessoa != null ? pessoa.getNome() : null;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * @return the unidade
	 */
	public UnidadeGeral getUnidade() {
		return unidade;
	}

	/**
	 * @param unidade the unidade to set
	 */
	public void setUnidade(UnidadeGeral unidade) {
		this.unidade = unidade;
	}

	/**
	 * @return the unidadeLotacao
	 */
	public UnidadeGeral getUnidadeLotacao() {
		return unidadeLotacao;
	}

	/**
	 * @param unidadeLotacao the unidadeLotacao to set
	 */
	public void setUnidadeLotacao(UnidadeGeral unidadeLotacao) {
		this.unidadeLotacao = unidadeLotacao;
	}

	/** Retorna siape da pessoa concatenado com nome */
	public String getSiapeNomeFormatado() {
		return "<div style='border-bottom: dashed 1px; padding: 2px'><div style='width:20px; float: left; text-align: right'>" + siape + "&nbsp;&nbsp;</div><div style='margin-left: 60px'>" + pessoa.getNome() + "</div></div>";
	}

	/**
	 * Retorna a designação do servidor que está ativa e com o tipo de gerência
	 * titular.
	 * 
	 * @return
	 */
	public Designacao getDesignacaoTitularAtiva(){
		if(designacoes != null){
			List<Designacao> desigs = (List<Designacao>) getDesignacoesAtivas();
			if(getDesignacoesAtivas() != null){
				for(Designacao des : desigs){
					if(des.getGerencia().equals(Designacao.TITULAR))
						return des;
				}
			}
		}
		return null;
	}
	
	public Collection<ResponsavelUnidadeAvaliacao> getResponsavelUnidadesAvaliacao() {
		return responsavelUnidadesAvaliacao;
	}

	public void setResponsavelUnidadesAvaliacao(
			Collection<ResponsavelUnidadeAvaliacao> responsavelUnidadesAvaliacao) {
		this.responsavelUnidadesAvaliacao = responsavelUnidadesAvaliacao;
	}

	public ClasseFuncional getClasseFuncional() {
		return classeFuncional;
	}

	public void setClasseFuncional(ClasseFuncional classeFuncional) {
		this.classeFuncional = classeFuncional;
	}

	public Date getDataDesligamento() {
		return dataDesligamento;
	}

	public void setDataDesligamento(Date dataDesligamento) {
		this.dataDesligamento = dataDesligamento;
	}

	public String getNomeIdentificacao() {
		return nomeIdentificacao;
	}

	public void setNomeIdentificacao(String nomeIdentificacao) {
		this.nomeIdentificacao = nomeIdentificacao;
	}
	public Integer getMatriculaInterna() {
		return matriculaInterna;
	}
	public void setMatriculaInterna(Integer matriculaInterna) {
		this.matriculaInterna = matriculaInterna;
	}
}