/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/01/2011
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que representa relat�rio de a��es associadas.
 * 
 * @author geyson
 * 
 */
@Entity
@Table(schema = "projetos", name = "relatorio_projeto")
public class RelatorioAcaoAssociada implements Validatable {
	
	/** Identificador */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_relatorio_projeto", nullable = false)
	private int id;

	/** Data de cadastro do relat�rio da a��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Date de envio do relat�rio da a��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;

	/** Registro do respons�vel pelo cadastro do relat�rio da a��o. */
	@Column(name = "id_registro_entrada_cadastro", updatable = false)
	private Integer registroEntradaCadastro;

	/** Indica se o relat�rio esta ativo no sistema. */
	private boolean ativo;

	/** A��o de extens�o que se refere o relat�rio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_projeto", unique = true, updatable = false)
	private Projeto projeto = new Projeto();

	/** Tipo de relat�rio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio", unique = true, updatable = false)
	private TipoRelatorioProjeto tipoRelatorio = new TipoRelatorioProjeto();

	/** Total real do p�blico atingido */
	@Column(name = "publico_real_atingido")
	private Integer publicoRealAtingido;

	/** Descri��o textual das Atividades realizadas */
	@Column(name = "atividades_realizadas")
	private String atividadesRealizadas;

	/** Descri��o textual dos resultados qualitativos */
	@Column(name = "resultados_qualitativos")
	private String resultadosQualitativos;

	/** Descri��o textual dos resultados quantitativos */
	@Column(name = "resultados_quantitativos")
	private String resultadosQuantitativos;

	/** Descri��o textual das dificuldades encontradas */
	@Column(name = "dificuldades_encontradas")
	private String dificuldadesEncontradas;
	
	/** Descri��o dos ajustes durante a execu��o do projeto */
	@Column(name = "ajustes_durante_execucao")
	private String ajustesDuranteExecucao;
	
	/** rela��o objetiva entre a proposta pedag�gica do curso e a proposta do projeto. */
	@Column(name = "relacao_proposta_curso")
	private String relacaoPropostaCurso;
	
	/** Ano do conv�nio */
	@Column(name = "ano_convenio")
	private Integer anoConvenio;

	/** convenio do sipac */
	@Column(name = "numero_convenio")
	private Integer numeroConvenio;

	/** Ano do contrato */
	@Column(name = "ano_contrato")
	private Integer anoContrato;

	/** Contrato do sipac */
	@Column(name = "numero_contrato")
	private Integer numeroContrato;
	
	/** Detalhes dos recursos. */
	@OneToMany(mappedBy = "relatorioProjeto", fetch = FetchType.LAZY)
	private Collection<DetalhamentoRecursosProjeto> detalhamentoRecursosProjeto;
	
	/** Outras a��es relacionadas */
	@ManyToMany(targetEntity = TipoCursoEventoExtensao.class, fetch = FetchType.LAZY )
	@JoinTable(name = "extensao.relatorio_projeto_outras_acoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_evento") })
	private Collection<TipoCursoEventoExtensao> outrasAcoesProjeto = new HashSet<TipoCursoEventoExtensao>();

	/** Apresenta��es geradas a partir da a��o associada */
	@ManyToMany(targetEntity = TipoCursoEventoExtensao.class, fetch = FetchType.LAZY)
	@JoinTable(name = "extensao.relatorio_projeto_apresentacoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_evento") })
	private Collection<TipoCursoEventoExtensao> apresentacaoProjetos = new HashSet<TipoCursoEventoExtensao>();

	/** Produ��es geradas a partir da a��o associada */
	@ManyToMany(targetEntity = TipoProducao.class, fetch = FetchType.LAZY)
	@JoinTable(name = "extensao.relatorio_projeto_producoes", joinColumns = { @JoinColumn(name = "id_relatorio_projeto") }, inverseJoinColumns = { @JoinColumn(name = "id_tipo_producao") })
	private Collection<TipoProducao> producoesGeradas = new HashSet<TipoProducao>();
	
	/** referencia ao arquivo do relat�rio*/
	@OneToMany(mappedBy = "relatorioProjeto")
	private Collection<ArquivoProjeto> arquivos = new ArrayList<ArquivoProjeto>();
	
	
	/** Campos utilizados na valida��o do relat�rio **/
	
	/** Data da avalia��o do departamento */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_departamento")
	private Date dataValidacaoDepartamento;

	/** Registro de entrada do respons�vel pela avalia��o(departamento) */
	@Column(name = "id_registro_entrada_departamento")
	private Integer registroEntradaDepartamento;

	/** Parecer do departamento */
	@Column(name = "parecer_departamento")
	private String parecerDepartamento;

	/** Tipo de parecer do departamento */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_parecer_departamento")
	private TipoParecerAvaliacaoProjeto tipoParecerDepartamento;	
	

	/** Data da avalia��o do comit� */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_comite")
	private Date dataValidacaoComite;

	/** Registro de entrada do respons�vel pela avalia��o(comit�) */
	@Column(name = "id_registro_entrada_comite")
	private Integer registroEntradaComite;

	/** Parecer do comit� */
	@Column(name = "parecer_comite")
	private String parecerComite;

	/** Tipo paracer do Comit� */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_parecer_comite")
	private TipoParecerAvaliacaoProjeto tipoParecerComite;	

	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}


	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public TipoRelatorioProjeto getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioProjeto tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Integer getPublicoRealAtingido() {
		return publicoRealAtingido;
	}

	public void setPublicoRealAtingido(Integer publicoRealAtingido) {
		this.publicoRealAtingido = publicoRealAtingido;
	}

	public String getAtividadesRealizadas() {
		return atividadesRealizadas;
	}

	public void setAtividadesRealizadas(String atividadesRealizadas) {
		this.atividadesRealizadas = atividadesRealizadas;
	}

	public String getResultadosQualitativos() {
		return resultadosQualitativos;
	}

	public void setResultadosQualitativos(String resultadosQualitativos) {
		this.resultadosQualitativos = resultadosQualitativos;
	}

	public String getResultadosQuantitativos() {
		return resultadosQuantitativos;
	}

	public void setResultadosQuantitativos(String resultadosQuantitativos) {
		this.resultadosQuantitativos = resultadosQuantitativos;
	}

	public String getDificuldadesEncontradas() {
		return dificuldadesEncontradas;
	}

	public void setDificuldadesEncontradas(String dificuldadesEncontradas) {
		this.dificuldadesEncontradas = dificuldadesEncontradas;
	}

	public String getAjustesDuranteExecucao() {
		return ajustesDuranteExecucao;
	}

	public void setAjustesDuranteExecucao(String ajustesDuranteExecucao) {
		this.ajustesDuranteExecucao = ajustesDuranteExecucao;
	}

	public String getRelacaoPropostaCurso() {
		return relacaoPropostaCurso;
	}

	public void setRelacaoPropostaCurso(String relacaoPropostaCurso) {
		this.relacaoPropostaCurso = relacaoPropostaCurso;
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

	public Collection<TipoCursoEventoExtensao> getOutrasAcoesProjeto() {
		return outrasAcoesProjeto;
	}

	public void setOutrasAcoesProjeto(
			Collection<TipoCursoEventoExtensao> outrasAcoesProjeto) {
		this.outrasAcoesProjeto = outrasAcoesProjeto;
	}

	public Collection<TipoCursoEventoExtensao> getApresentacaoProjetos() {
		return apresentacaoProjetos;
	}

	public void setApresentacaoProjetos(
			Collection<TipoCursoEventoExtensao> apresentacaoProjetos) {
		this.apresentacaoProjetos = apresentacaoProjetos;
	}

	public Collection<TipoProducao> getProducoesGeradas() {
		return producoesGeradas;
	}

	public void setProducoesGeradas(Collection<TipoProducao> producoesGeradas) {
		this.producoesGeradas = producoesGeradas;
	}

	public Collection<DetalhamentoRecursosProjeto> getDetalhamentoRecursosProjeto() {
		return detalhamentoRecursosProjeto;
	}

	public void setDetalhamentoRecursosProjeto(
			Collection<DetalhamentoRecursosProjeto> detalhamentoRecursosProjeto) {
		this.detalhamentoRecursosProjeto = detalhamentoRecursosProjeto;
	}

	public Collection<ArquivoProjeto> getArquivos() {
		return arquivos;
	}

	public void setArquivos(Collection<ArquivoProjeto> arquivos) {
		this.arquivos = arquivos;
	}

	public Date getDataValidacaoDepartamento() {
		return dataValidacaoDepartamento;
	}

	public void setDataValidacaoDepartamento(Date dataValidacaoDepartamento) {
		this.dataValidacaoDepartamento = dataValidacaoDepartamento;
	}


	public String getParecerDepartamento() {
		return parecerDepartamento;
	}

	public void setParecerDepartamento(String parecerDepartamento) {
		this.parecerDepartamento = parecerDepartamento;
	}

	public TipoParecerAvaliacaoProjeto getTipoParecerDepartamento() {
		return tipoParecerDepartamento;
	}

	public void setTipoParecerDepartamento(
			TipoParecerAvaliacaoProjeto tipoParecerDepartamento) {
		this.tipoParecerDepartamento = tipoParecerDepartamento;
	}

	public Integer getRegistroEntradaCadastro() {
		return registroEntradaCadastro;
	}

	public void setRegistroEntradaCadastro(Integer registroEntradaCadastro) {
		this.registroEntradaCadastro = registroEntradaCadastro;
	}

	public Integer getRegistroEntradaDepartamento() {
		return registroEntradaDepartamento;
	}

	public void setRegistroEntradaDepartamento(Integer registroEntradaDepartamento) {
		this.registroEntradaDepartamento = registroEntradaDepartamento;
	} 
	
	public Date getDataValidacaoComite() {
		return dataValidacaoComite;
	}

	public void setDataValidacaoComite(Date dataValidacaoComite) {
		this.dataValidacaoComite = dataValidacaoComite;
	}

	public Integer getRegistroEntradaComite() {
		return registroEntradaComite;
	}

	public void setRegistroEntradaComite(Integer registroEntradaComite) {
		this.registroEntradaComite = registroEntradaComite;
	}

	public String getParecerComite() {
		return parecerComite;
	}

	public void setParecerComite(String parecerComite) {
		this.parecerComite = parecerComite;
	}

	public TipoParecerAvaliacaoProjeto getTipoParecerComite() {
		return tipoParecerComite;
	}

	public void setTipoParecerComite(TipoParecerAvaliacaoProjeto tipoParecerComite) {
		this.tipoParecerComite = tipoParecerComite;
	}	
	
	/** Verifica se o relat�rio pode ser editado pela coordena��o. */
	public boolean isEditavel() {
		return isAtivo() && (dataEnvio == null);
	}

		

	/**
	 * Permite que o departamento analise o relat�rio somente antes do Comit�
	 * 
	 * @return
	 */
	public boolean isDepartamentoPodeAnalisar() {
		return !isEditavel() && (dataValidacaoDepartamento == null && dataValidacaoComite == null);
	}
	
	/** Relat�rio aprovado pelo departamento. */
	public boolean isAprovadoDepartamento() {
		return dataValidacaoDepartamento != null && tipoParecerDepartamento != null && tipoParecerDepartamento.getId() == TipoParecerAvaliacaoExtensao.APROVADO;
	}
	
	/** Relat�rio reprovado pelo departamento. */
	public boolean isReprovadoDepartamento() {
		return dataValidacaoDepartamento != null && tipoParecerDepartamento != null && tipoParecerDepartamento.getId() == TipoParecerAvaliacaoExtensao.REPROVADO;
	}
	
	/** Relat�rio aprovado com recomenda��es pelo departamento. */
	public boolean isAprovadoComRecomendacoesDepartamento() {
		return dataValidacaoDepartamento != null && tipoParecerDepartamento != null && tipoParecerDepartamento.getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO;
	}
		
	
	/**
	 * Permite que Comit� analise o relat�rio somente ap�s a autoriza��o do
	 * departamento respons�vel pela a��o.
	 * 
	 * @return
	 */
	public boolean isComitePodeAnalisar() {
		return !isEditavel() && dataValidacaoComite == null;				
	}
	
	/** Relat�rio aprovado pelo comit�. */
	public boolean isAprovadoComite() {
		return dataValidacaoComite != null && tipoParecerComite != null && tipoParecerComite.getId() == TipoParecerAvaliacaoExtensao.APROVADO;
	}	
	
	/** Relat�rio reprovado pelo comit�. */
	public boolean isReprovadoComite() {
		return dataValidacaoComite != null && tipoParecerComite != null && tipoParecerComite.getId() == TipoParecerAvaliacaoExtensao.REPROVADO;
	}	
	
	/** Relat�rio aprovado com recomenda��es pelo comit�. */
	public boolean isAprovadoComRecomendacoesComite() {
		return dataValidacaoComite != null && tipoParecerComite != null && tipoParecerComite.getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO;
	}

	/** Relat�rio reprovado sem justificativa pelo departamento. */
	public boolean isValidadoSemJustificativaDepartamento() {
		return (isReprovadoDepartamento() || isAprovadoComRecomendacoesDepartamento()) && (getParecerDepartamento() == null || getParecerDepartamento().trim().equals("")); 
	}

	/** Relat�rio reprovado sem justificativa pelo Comit� Integrado. */
	public boolean isValidadoSemJustificativaComite() {
		return (isReprovadoComite() || isAprovadoComRecomendacoesComite()) && (getParecerComite() == null || getParecerComite().trim().equals("")); 
	}

	@Override
	public ListaMensagens validate() {		
		ListaMensagens lista = new ListaMensagens();		
		if (isValidadoSemJustificativaDepartamento() || isValidadoSemJustificativaComite()) {
			lista.addErro("Justificativa deve ser informada obrigatoriamente para relat�rios n�o aprovados ou aprovados com recomenda��o.");
		}
		return lista;
	}


	/** Informa se � um relat�rio final. */
	public boolean isRelatorioFinal() {
		return tipoRelatorio.getId() == TipoRelatorioProjeto.RELATORIO_FINAL;
	}

	/** Informa se � um relat�rio parcial. */
	public boolean isRelatorioParcial() {
		return tipoRelatorio.getId() == TipoRelatorioProjeto.RELATORIO_PARCIAL;
	}

}
