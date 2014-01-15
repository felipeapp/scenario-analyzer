/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2008
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe de dom�nio que representa os projetos de infra-estrutura em pesquisa
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "projeto_infraestrutura_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class ProjetoInfraEstruturaPesquisa implements Validatable {

	/** Referente ao tipo do projeto */
	public static final int INSTITUCIONAL = 1;
	public static final int EXTERNO = 2;
	
	/** Chave prim�ria */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_projeto_infraestrutura", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
	 * Tipo do projeto: Institucional ou Externo.
	 */
	private Integer tipo = 1;

	/**
	 * �rg�o ou entidade respons�vel diretamente pela execu��o do objeto do conv�nio.
	 */
	@ManyToOne
	@JoinColumn(name = "id_unidade_executora")
	private Unidade executora;
	
	/**
	 * �rg�o ou entidade que participa do conv�nio para manifestar seu 
	 * consentimento ou para assumir obriga��es em nome pr�prio.
	 */
	@ManyToOne
	@JoinColumn(name = "id_unidade_interveniente")
	private Unidade interveniente;

	/**
	 * Elemento respons�vel pela formaliza��o do contrato e transfer�ncia dos recursos financeiros ou 
	 * descentraliza��o de cr�ditos or�ament�rios, destinados a execu��o do objeto do conv�nio, 
	 * de acordo com a legisla��o vigente.
	 */
	@ManyToOne
	@JoinColumn(name = "id_entidade_concedente")
	private EntidadeFinanciadora concedente;
	
	/**
	 * �rg�o ou entidade com a qual pactua-se a execu��o de programas de trabalho, atividade ou 
	 * evento de interesse rec�proco, mediante a celebra��o de conv�nios. � a entidade que 
	 * recebe os recursos financeiros.
	 */
	@ManyToOne
	@JoinColumn(name = "id_entidade_convenente")
	private EntidadeFinanciadora convenente;
	
	/**
	 * Benef�cios esperados no processo ensino-aprendizagem dos alunos de gradua��o 
	 * e/ou p�s-gradua��o vinculados ao projeto.
	 */
	private String beneficios;
	
	/**
	 * Retorno para os cursos de gradua��o e/ou p�s-gradua��o e para os professores 
	 * da UFRN em geral.
	 */
	private String retorno;
			
	@ManyToOne
	@JoinColumn(name = "id_coordenador_geral")
	private Servidor coordenadorGeral;
	
	/**
	 * Cole��o dos Sub-Projetos que fazem parte do projeto de Infra-Estrutura.
	 */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "projetoInfraEstruturaPesquisa")
	private Set<SubProjetoInfraEstruturaPesquisa> subProjetos;
	
	/**
	 * Projeto Base.
	 */
	@ManyToOne
	@JoinColumn(name="id_projeto")
	private Projeto projeto = new Projeto();
	
	/**
	 * Indica se o projeto est� ativou ou inativo.
	 */
	private boolean ativo = true;
	
	/**
	 * Construtor padr�o.
	 */
	public ProjetoInfraEstruturaPesquisa(){
		this.projeto.setEdital(null);
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public String getBeneficios() {
		return beneficios;
	}

	public void setBeneficios(String beneficios) {
		this.beneficios = beneficios;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public Servidor getCoordenadorGeral() {
		return coordenadorGeral;
	}

	public void setCoordenadorGeral(Servidor coordenadorGeral) {
		this.coordenadorGeral = coordenadorGeral;
	}

	/**
	 * Valida as propriedades utilizadas no cadastro do Projeto de Infra-Estrutura de Pesquisa, como o t�tulo 
	 * (n�o podendo ultrapassar 400 caracteres), o ano e seu tipo.
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(projeto.getTitulo(), "T�tulo", erros);
	
		if ( projeto.getTitulo() != null && projeto.getTitulo().trim().length() > 400 ) {
			erros.addMensagem(MensagensArquitetura.MAXIMO_CARACTERES, projeto.getTitulo(), 400);
		}
		if (projeto.getAno() == null)
			ValidatorUtil.validateRequired(projeto.getAno(), "Ano", erros);
		else if (projeto.getAno() < 2002)
			erros.addMensagem(MensagensArquitetura.DATA_POSTERIOR_A, "Ano", "2002");
		else if(projeto.getAno() > CalendarUtils.getAnoAtual())
			erros.addMensagem(MensagensArquitetura.DATA_ANTERIOR_IGUAL, "Ano", CalendarUtils.getAnoAtual());
		ValidatorUtil.validateRequiredId(tipo, "Tipo", erros);
		ValidatorUtil.validateRequiredId(interveniente.getId(), "Unidade Interveniente", erros);
		ValidatorUtil.validateRequiredId(executora.getId(), "Unidade Executora", erros);
		ValidatorUtil.validateRequiredAjaxId(coordenadorGeral.getId(), "Coordenador Geral", erros);
		ValidatorUtil.validateRequired(projeto.getObjetivos(), "Objetivos Gerais", erros);
		ValidatorUtil.validateRequired(beneficios, "Benef�cios esperados", erros);
		ValidatorUtil.validateRequired(retorno, "Retorno aos cursos", erros);
		return erros;
	}

	public Set<SubProjetoInfraEstruturaPesquisa> getSubProjetos() {
		return subProjetos;
	}

	public void setSubProjetos(Set<SubProjetoInfraEstruturaPesquisa> subProjetos) {
		this.subProjetos = subProjetos;
	}
	
	public boolean addSubProjeto(SubProjetoInfraEstruturaPesquisa subProjeto){
		subProjeto.setProjetoInfraEstruturaPesquisa(this);
		return subProjetos.add(subProjeto);
	}
	
	@Transient
	public String getDescricaoDominio(){
		return "Projeto de Infra-Estrutura em Pesquisa";
	}
	
	@Transient
	public boolean isInstitucional(){
		return tipo == INSTITUCIONAL;
	}
	
	/** Retorna o valor total do custeio informado pelo usu�rio */
	@Transient
	public double getTotalCusteio(){
		double total = 0;
		for(SubProjetoInfraEstruturaPesquisa sub: subProjetos)
			total += sub.getCusteio();
		return total;
	}
	
	/** Retorna o valor total do capital informado pelo usu�rio */
	@Transient
	public double getTotalCapital(){
		double total = 0;
		for(SubProjetoInfraEstruturaPesquisa sub: subProjetos)
			total += sub.getCapital();
		return total;
	}
	
	/** Retorna o valor total da taxa informado pelo usu�rio */
	@Transient
	public double getTotalTaxa(){
		double total = 0;
		for(SubProjetoInfraEstruturaPesquisa sub: subProjetos)
			total += sub.getTaxa();
		return total;
	}
	
	/** Retorna o valor total de Overhead informado pelo usu�rio */
	@Transient
	public double getTotalOverhead(){
		double total = 0;
		for(SubProjetoInfraEstruturaPesquisa sub: subProjetos)
			total += sub.getOverhead();
		return total;
	}
	
	/** Retorna o valor total informado pelo usu�rio */
	@Transient
	public double getValorTotal(){
		return getTotalCapital() + getTotalCusteio() + getTotalTaxa() + getTotalOverhead();
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/**
	 * @param projeto the projeto to set
	 */
	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	/**
	 * @return the projeto
	 */
	public Projeto getProjeto() {
		return projeto;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Unidade getExecutora() {
		return executora;
	}

	public void setExecutora(Unidade executora) {
		this.executora = executora;
	}

	public Unidade getInterveniente() {
		return interveniente;
	}

	public void setInterveniente(Unidade interveniente) {
		this.interveniente = interveniente;
	}

	public EntidadeFinanciadora getConcedente() {
		return concedente;
	}

	public void setConcedente(EntidadeFinanciadora concedente) {
		this.concedente = concedente;
	}

	public EntidadeFinanciadora getConvenente() {
		return convenente;
	}

	public void setConvenente(EntidadeFinanciadora convenente) {
		this.convenente = convenente;
	}
	
}
