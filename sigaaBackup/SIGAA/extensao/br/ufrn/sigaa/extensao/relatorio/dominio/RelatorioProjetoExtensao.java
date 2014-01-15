/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Relat�rio de projeto de extens�o.
 * 
 * � atrav�s deste relat�rio que o coordenador da a��o de extens�o presta contas
 * dos recursos utilizados durante a execu��o da a��o, informa se os objetivos
 * propostos foram alcan�ados, produ��es geradas, etc.
 * 
 * @author Ilueny Santos
 * 
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("P")
public class RelatorioProjetoExtensao extends RelatorioAcaoExtensao implements
		Validatable {

	/** Armazena a rela��o entre a proposta de curso */
	@Column(name = "relacao_proposta_curso")
	private String relacaoPropostaCurso;

	/** Armazena o tipo do curso Evento de extens�o */
	@ManyToMany(targetEntity = TipoCursoEventoExtensao.class)
	@JoinTable(name = "extensao.relatorio_projeto_outras_acoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_evento") })
	private Collection<TipoCursoEventoExtensao> outrasAcoesProjeto = new HashSet<TipoCursoEventoExtensao>();

	/** Armazena o tipo do curso Evento de extens�o */
	@ManyToMany(targetEntity = TipoCursoEventoExtensao.class)
	@JoinTable(name = "extensao.relatorio_projeto_apresentacoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_evento") })
	private Collection<TipoCursoEventoExtensao> apresentacaoProjetos = new HashSet<TipoCursoEventoExtensao>();

	/** Armazena o tipo de produ��o */
	@ManyToMany(targetEntity = TipoProducao.class)
	@JoinTable(name = "extensao.relatorio_projeto_producoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_producao") })
	private Collection<TipoProducao> producoesGeradas = new HashSet<TipoProducao>();

	/** Ano do Conv�nio */
	@Column(name = "ano_convenio")
	private Integer anoConvenio;
	
	/** N�mero do Conv�nio */
	// convenio do sipac
	@Column(name = "numero_convenio")
	private Integer numeroConvenio;

	/** Ano do Contrato */
	@Column(name = "ano_contrato")
	private Integer anoContrato;

	/** N�emero do Contrato */
	// contrato do sipac
	@Column(name = "numero_contrato")
	private Integer numeroContrato;

	public Collection<TipoCursoEventoExtensao> getApresentacaoProjetos() {
		return apresentacaoProjetos;
	}

	public void setApresentacaoProjetos(
			Collection<TipoCursoEventoExtensao> apresentacaoProjetos) {
		this.apresentacaoProjetos = apresentacaoProjetos;
	}

	public Collection<TipoCursoEventoExtensao> getOutrasAcoesProjeto() {
		return outrasAcoesProjeto;
	}

	public void setOutrasAcoesProjeto(
			Collection<TipoCursoEventoExtensao> outrasAcoesProjeto) {
		this.outrasAcoesProjeto = outrasAcoesProjeto;
	}

	public Collection<TipoProducao> getProducoesGeradas() {
		return producoesGeradas;
	}

	public void setProducoesGeradas(Collection<TipoProducao> producoesGeradas) {
		this.producoesGeradas = producoesGeradas;
	}

	public String getRelacaoPropostaCurso() {
		return relacaoPropostaCurso;
	}

	public void setRelacaoPropostaCurso(String relacaoPropostaCurso) {
		this.relacaoPropostaCurso = relacaoPropostaCurso;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getAtividade().getProjetoExtensao(), "Selecione o Projeto de Extens�o", lista);
		ValidatorUtil.validateRequired(relacaoPropostaCurso, "Rela��o Proposta Curso", lista);
		ValidatorUtil.validateRequired(getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(getPublicoRealAtingido(), "P�blico Real Atingido", lista);
		return lista;
	}

	public Integer getAnoConvenio() {
		return anoConvenio;
	}

	public void setAnoConvenio(Integer anoConvenio) {
		this.anoConvenio = anoConvenio;
	}

	public Integer getNumeroConvenio() {
		return numeroConvenio;
	}

	public void setNumeroConvenio(Integer numeroConvenio) {
		this.numeroConvenio = numeroConvenio;
	}

	public Integer getAnoContrato() {
		return anoContrato;
	}

	public void setAnoContrato(Integer anoContrato) {
		this.anoContrato = anoContrato;
	}

	public Integer getNumeroContrato() {
		return numeroContrato;
	}

	public void setNumeroContrato(Integer numeroContrato) {
		this.numeroContrato = numeroContrato;
	}

}