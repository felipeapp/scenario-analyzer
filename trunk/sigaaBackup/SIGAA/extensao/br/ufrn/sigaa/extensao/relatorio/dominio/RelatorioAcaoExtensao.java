/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/11/2008
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.sigaa.extensao.dominio.AndamentoAtividade;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParecerAvaliacaoExtensao;

/**
 * Classe utilizada para unificar os relat�rios de projeto e os relat�rios de
 * curso ou evento em uma �nica entidade.
 * 
 * @author leonardo
 * @author Ilueny Santos
 * 
 */

@Entity
@Inheritance
@DiscriminatorColumn(name = "discriminador_tipo_relatorio")
@Table(schema = "extensao", name = "relatorio_acao_extensao")
public class RelatorioAcaoExtensao implements PersistDB {

	/** Atributo utilizado para representar o ID do Relar�rio de A��o de Extens�o */ 
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="sequence_name", value="extensao.relatorio_acao_extensao_sequence") })
	@Column(name = "id_relatorio_acao_extensao", nullable = false)
	private int id;

	/** Data de cadastro do relat�rio da a��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;

	/** Date de envio do relat�rio da a��o */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_envio")
	private Date dataEnvio;

	/** Registro do respons�vel pelo cadastro do relat�rio da a��o. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_registro_entrada_cadastro", updatable = false)
	private RegistroEntrada registroEntradaCadastro;

	/** Indica se o relat�rio esta ativo no sistema. */
	private boolean ativo;

	/** A��o de extens�o que se refere o relat�rio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_atividade", unique = true, updatable = false)
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Tipo de relat�rio */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_relatorio", unique = true, updatable = false)
	private TipoRelatorioExtensao tipoRelatorio = new TipoRelatorioExtensao();

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
	
	/** Atributo utilizado para representar os ajustes feitos durante a execu��o */
	@Column(name = "ajustes_durante_execucao")
	private String ajustesDuranteExecucao;

	/** Determina se a a��o de extens�o foi realmente realizada. */
	@Column(name = "acao_realizada")
	private Boolean acaoRealizada;
	
	/** Espe�o reservado para esplana��o dos motivos, caso a a��o n�o tenha sido realizada. */
	@Column(name = "motivo_acao_nao_realizada")
	private String motivoAcaoNaoRealizada;
	
	/** referencia ao arquivo do relat�rio*/
	@OneToMany(mappedBy = "relatorioAcaoExtensao")
	private Collection<ArquivoRelatorioAcaoExtensao> arquivos = new ArrayList<ArquivoRelatorioAcaoExtensao>();

	/** Detalhes dos recursos. */
	@OneToMany(mappedBy = "relatorioAcaoExtensao")
	private Collection<DetalhamentoRecursos> detalhamentoRecursos;

	/** ************** novos campos usados no relat�rio********** */
	
	@Transient
	private Collection<AndamentoAtividade> andamento;
	
	/** Armazena a observa��o geral da a��o de extens�o */
	@Column(name = "observacoes_gerais")
	private String observacoesGerais;

	/** Armazena a quantidade quantitativa das apresenta��o em Evento Cient�fico */
	@Column(name = "apresentacao_evento_cientifico")
	private Integer apresentacaoEventoCientifico;

	/** Armazena as obser��o da apresenta��o */
	@Column(name = "observacao_apresentacao")
	private String observacaoApresentacao;
	
	/** Armazena a quantidade quantitativa dos artigos em Evento Cient�fico */
	@Column(name = "artigo_evento_cientifico")
	private Integer artigosEventoCientifico;

	/** Armazena as obser��o do artigo */
	@Column(name = "observacao_artigo")
	private String observacaoArtigo;

	/** Armazena a quantidade quantitativa das produ��es Cient�ficas */
	@Column(name = "producoes_cientificas")
	private Integer producoesCientifico;

	/** Armazena as obser��o da produ��o */
	@Column(name = "observacao_producao")
	private String observacaoProducao;

	/** **************campos usados na aprova��o do relat�rio********** */

	/** Data da avalia��o do departamento */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_departamento")
	private Date dataValidacaoDepartamento;

	/** Registro de entrada do respons�vel pela avalia��o(departamento) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_departamento")
	private RegistroEntrada registroEntradaDepartamento;

	/** Parecer do departamento */
	@Column(name = "parecer_departamento")
	private String parecerDepartamento;

	/** Atributo utilizado para representar o Tipo do Parecer do Departamento */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_parecer_departamento")
	private TipoParecerAvaliacaoExtensao tipoParecerDepartamento;	

	/** Data da valida��o da PROEX */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_validacao_proex")
	private Date dataValidacaoProex;

	/** Atributo utilizado para representar o Resgistro de Entrada da PROEX */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada_proex")
	private RegistroEntrada registroEntradaProex;

	/** Parecer da proex */
	@Column(name = "parecer_proex")
	private String parecerProex;
	
	/** Atributo utilizado para representaro Tipo de Parecer da PROEX */
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name = "id_tipo_parecer_proex")
	private TipoParecerAvaliacaoExtensao tipoParecerProex;

	
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

	public RegistroEntrada getRegistroEntradaCadastro() {
		return registroEntradaCadastro;
	}

	public void setRegistroEntradaCadastro(
			RegistroEntrada registroEntradaCadastro) {
		this.registroEntradaCadastro = registroEntradaCadastro;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public TipoRelatorioExtensao getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(TipoRelatorioExtensao tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Integer getPublicoRealAtingido() {
		return publicoRealAtingido;
	}

	public void setPublicoRealAtingido(Integer publicoRealAtingido) {
		this.publicoRealAtingido = publicoRealAtingido;
	}

	public Date getDataValidacaoDepartamento() {
		return dataValidacaoDepartamento;
	}

	public void setDataValidacaoDepartamento(Date dataValidacaoDepartamento) {
		this.dataValidacaoDepartamento = dataValidacaoDepartamento;
	}

	public RegistroEntrada getRegistroEntradaDepartamento() {
		return registroEntradaDepartamento;
	}

	public void setRegistroEntradaDepartamento(
			RegistroEntrada registroEntradaDepartamento) {
		this.registroEntradaDepartamento = registroEntradaDepartamento;
	}

	public String getParecerDepartamento() {
		return parecerDepartamento;
	}

	public void setParecerDepartamento(String parecerDepartamento) {
		this.parecerDepartamento = parecerDepartamento;
	}

	public Date getDataValidacaoProex() {
		return dataValidacaoProex;
	}

	public void setDataValidacaoProex(Date dataValidacaoProex) {
		this.dataValidacaoProex = dataValidacaoProex;
	}

	public RegistroEntrada getRegistroEntradaProex() {
		return registroEntradaProex;
	}

	public void setRegistroEntradaProex(RegistroEntrada registroEntradaProex) {
		this.registroEntradaProex = registroEntradaProex;
	}

	public String getParecerProex() {
		return parecerProex;
	}

	public void setParecerProex(String parecerProex) {
		this.parecerProex = parecerProex;
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

	public Collection<DetalhamentoRecursos> getDetalhamentoRecursos() {
		return detalhamentoRecursos;
	}

	public void setDetalhamentoRecursos(
			Collection<DetalhamentoRecursos> detalhamentoRecursos) {
		this.detalhamentoRecursos = detalhamentoRecursos;
	}

	/** Verifica se o relat�rio pode ser editado pelo coordenador. */
	public boolean isEditavel() {
		return isAtivo() && (dataEnvio == null);
	}

	
	


	/**
	 * Permite que o departamento analise o relat�rio somente antes de PROEX
	 * 
	 * @return
	 */
	public boolean isDepartamentoPodeAnalisar() {
		return !isEditavel() && (dataValidacaoDepartamento == null && dataValidacaoProex == null);
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
	 * Permite que a PROEX analise o relat�rio somente ap�s a autoriza��o do
	 * departamento respons�vel pela a��o
	 * 
	 * @return
	 */
	public boolean isProexPodeAnalisar() {
		return !isEditavel() && (isAprovadoDepartamento() && dataValidacaoProex == null);
				
	}
	
	/** Relat�rio aprovado pela proex. */
	public boolean isAprovadoProex() {
		return dataValidacaoProex != null && tipoParecerProex != null && tipoParecerProex.getId() == TipoParecerAvaliacaoExtensao.APROVADO;
	}	
	
	/** Relat�rio reprovado pela proex. */
	public boolean isReprovadoProex() {
		return dataValidacaoProex != null && tipoParecerProex != null && tipoParecerProex.getId() == TipoParecerAvaliacaoExtensao.REPROVADO;
	}	
	
	/** Relat�rio aprovado com recomenda��es pela proex. */
	public boolean isAprovadoComRecomendacoesProex() {
		return dataValidacaoProex != null && tipoParecerProex != null && tipoParecerProex.getId() == TipoParecerAvaliacaoExtensao.APROVADO_COM_RECOMENDACAO;
	}	

		
	/** Pr�-reitoria afirma que a A��o de Extens�o n�o foi realizada. */
	public boolean isAcaoNaoRealizada() {
		return (dataValidacaoProex != null && tipoParecerProex != null && tipoParecerProex.getId() == TipoParecerAvaliacaoExtensao.ACAO_NAO_REALIZADA);
	}	
	

	/**
	 * Identifica a data do �ltimo envio do relat�rio para an�lise do departamento. o
	 * mesmo relat�rio pode ser enviado v�rias vezes at� que seja analisado pelo
	 * chefe do departamento.
	 * 
	 * @return
	 */
	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	/**
	 * Coordenador informa quais os ajustes realizados durante a execu��o da
	 * a��o de extens�o
	 * 
	 * @return
	 */
	public String getAjustesDuranteExecucao() {
		return ajustesDuranteExecucao;
	}

	public void setAjustesDuranteExecucao(String ajustesDuranteExecucao) {
		this.ajustesDuranteExecucao = ajustesDuranteExecucao;
	}

	public Collection<ArquivoRelatorioAcaoExtensao> getArquivos() {
		return arquivos;
	}

	public void setArquivos(Collection<ArquivoRelatorioAcaoExtensao> arquivos) {
		this.arquivos = arquivos;
	}

	/**
	 * Adiciona um Arquivo a lista de anexos do relat�rio
	 * 
	 * @param arquivo anexo ao relat�rio
	 * @return
	 */
	public boolean addArquivo(ArquivoRelatorioAcaoExtensao arquivo) {
		arquivo.setRelatorioAcaoExtensao(this);
		return arquivos.add(arquivo);
	}

	public TipoParecerAvaliacaoExtensao getTipoParecerDepartamento() {
		return tipoParecerDepartamento;
	}

	public void setTipoParecerDepartamento(
			TipoParecerAvaliacaoExtensao tipoParecerDepartamento) {
		this.tipoParecerDepartamento = tipoParecerDepartamento;
	}

	public TipoParecerAvaliacaoExtensao getTipoParecerProex() {
		return tipoParecerProex;
	}

	public void setTipoParecerProex(TipoParecerAvaliacaoExtensao tipoParecerProex) {
		this.tipoParecerProex = tipoParecerProex;
	}
	
	/** Informa se � um relat�rio final. */
	public boolean isRelatorioFinal() {
		return tipoRelatorio.getId() == TipoRelatorioExtensao.RELATORIO_FINAL;
	}

	/** Informa se � um relat�rio parcial. */
	public boolean isRelatorioParcial() {
		return tipoRelatorio.getId() == TipoRelatorioExtensao.RELATORIO_PARCIAL;
	}

	public Boolean getAcaoRealizada() {
		return acaoRealizada;
	}

	public void setAcaoRealizada(Boolean acaoRealizada) {
		this.acaoRealizada = acaoRealizada;
	}

	public String getMotivoAcaoNaoRealizada() {
		return motivoAcaoNaoRealizada;
	}

	public void setMotivoAcaoNaoRealizada(String motivoAcaoNaoRealizada) {
		this.motivoAcaoNaoRealizada = motivoAcaoNaoRealizada;
	}

	public String getObservacoesGerais() {
		return observacoesGerais;
	}

	public void setObservacoesGerais(String observacoesGerais) {
		this.observacoesGerais = observacoesGerais;
	}

	public Integer getApresentacaoEventoCientifico() {
		return apresentacaoEventoCientifico;
	}

	public void setApresentacaoEventoCientifico(Integer apresentacaoEventoCientifico) {
		this.apresentacaoEventoCientifico = apresentacaoEventoCientifico;
	}

	public String getObservacaoApresentacao() {
		return observacaoApresentacao;
	}

	public void setObservacaoApresentacao(String observacaoApresentacao) {
		this.observacaoApresentacao = observacaoApresentacao;
	}

	public Integer getArtigosEventoCientifico() {
		return artigosEventoCientifico;
	}

	public void setArtigosEventoCientifico(Integer artigosEventoCientifico) {
		this.artigosEventoCientifico = artigosEventoCientifico;
	}

	public String getObservacaoArtigo() {
		return observacaoArtigo;
	}

	public void setObservacaoArtigo(String observacaoArtigo) {
		this.observacaoArtigo = observacaoArtigo;
	}

	public Integer getProducoesCientifico() {
		return producoesCientifico;
	}

	public void setProducoesCientifico(Integer producoesCientifico) {
		this.producoesCientifico = producoesCientifico;
	}

	public String getObservacaoProducao() {
		return observacaoProducao;
	}

	public void setObservacaoProducao(String observacaoProducao) {
		this.observacaoProducao = observacaoProducao;
	}

	public Collection<AndamentoAtividade> getAndamento() {
		return andamento;
	}

	public void setAndamento(Collection<AndamentoAtividade> andamento) {
		this.andamento = andamento;
	}
	
}