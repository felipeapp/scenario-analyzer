/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 27/06/2007
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Calendar;
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
import javax.persistence.Transient;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.Edital;

/*******************************************************************************
 * <p>
 * Representa um edital de monitoria lançado pela PROGRAD.
 * </p>
 * <p>
 * Através de um edital de monitoria a PROGRAD estabelece prazos para o
 * recebimento de propostas, informa os recursos disponíveis para financiamento
 * dos projetos e define regras para classificação dos projetos candidatos.
 * </p>
 * 
 * Para informações mais gerais veja {@link Edital}.
 * 
 * @author Ilueny Santos
 ******************************************************************************/
@Entity
@Table(name = "edital_monitoria", schema = "monitoria")
public class EditalMonitoria implements Validatable {

	/** Atributo utilizado para representar o numero id do edital de monitoria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") })
	@Column(name = "id_edital_monitoria")
	private int id;
	
	/** Atributo utilizado para representar o edital do edital de monitoria */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_edital")	
	private Edital edital = new Edital();
	
	/**
	 *  Atributo utilizado para representar o numero do fator de carga horária do edital de monitoria que será multiplicado pela 
	 *  quantidade de dias que o membro passou no projeto.
	 */
	@Column(name = "fator_carga_horaria")
	private Integer fatorCargaHoraria;
	
	/** Atributo utilizado para representar o número de bolsas do edital de monitoria */
	@Column(name = "numero_bolsas")
	private short numeroBolsas;

	/** Atributo utilizado para representar o valor de financiamento do edital de monitoria */
	@Column(name = "valor_financiamento")
	private double valorFinanciamento;

	/** Atributo utilizado para representar o número do edital de monitoria */
	@Column(name = "numero_edital")
	private String numeroEdital;

	/** Atributo utilizado para representar o número do peso para a média de análise do edital de monitoria */
	@Column(name = "peso_media_analise")
	private double pesoMediaAnalise;

	/** Atributo utilizado para representar o número do peso dos professores do edital de monitoria */
	@Column(name = "peso_num_professores")
	private double pesoNumProfessores;

	/** Atributo utilizado para representar o número do peso dos componentes curriculares do edital de monitoria */
	@Column(name = "peso_comp_curriculares")
	private double pesoCompCurriculares;

	/** Atributo utilizado para representar o número do peso dos departamentos do edital de monitoria */
	@Column(name = "peso_num_departamentos")
	private double pesoNumDepartamentos;

	/** Atributo utilizado para representar o número do peso RT do edital de monitoria */
	@Column(name = "peso_rt")
	private double pesoRT;

	/** Atributo utilizado para representar a data de publicação do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_publicacao")
	private Date dataPublicacao;

	/** Atributo utilizado para representar a data de início da seleção do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_selecao")
	private Date inicioSelecaoMonitor;

	/** Atributo utilizado para representar a data de fim da seleção do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_selecao")
	private Date fimSelecaoMonitor;

	/** Atributo utilizado para representar a data de fim da reconsideração do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_reconsideracao_req_formais")
	private Date dataFimReconsideracaoReqFormais;

	/** Atributo utilizado para representar a data de fim da autorização do departamento do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_autorizacao_departamento")
	private Date dataFimAutorizacaoDepartamento;

	/** Atributo utilizado para representar a data do resultado final do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_resultado_final_edital")
	private Date dataResultadoFinalEdital;

	/** Atributo utilizado para validar projetos que não enviaram relatórios parciais neste período */
	@Column(name = "ano_projeto_relatorio_parcial_inicio")
	private int anoProjetoRelatorioParcialIncio;

	/** Atributo utilizado para validar projetos que não enviaram relatórios parciais neste período */
	@Column(name = "ano_projeto_relatorio_parcial_fim")
	private int anoProjetoRelatorioParcialFim;

	/** Atributo utilizado para validar projetos que não enviaram relatórios finais neste período */
	@Column(name = "ano_projeto_relatorio_final_inicio")
	private int anoProjetoRelatorioFinalIncio;

	/** Atributo utilizado para validar projetos que não enviaram relatórios finais neste período */
	@Column(name = "ano_projeto_relatorio_final_fim")
	private int anoProjetoRelatorioFinalFim;
	
	/** Atributo utilizado representar o índice da avaliação discrepante */
	@Column(name="indice_avaliacao_discrepante")
	private double indiceAvaliacaoDiscrepante;
	
	/** Atributo utilizado para representar a média da aprovação do projeto */
	@Column(name="media_aprovacao_projeto")
	private double mediaAprovacaoProjeto;

	/** Atributo utilizado para representar a relação do discente com o coordenador onde Determina a quantidade máxima de discentes que cada docente pode orientar.*/
	@Column(name="relacao_discente_orientador")
	private int relacaoDiscenteOrientador;
	
	/** Atributo utilizado para representar a Nota Mínima de Aprovação na Seleção de Monitoria */
	@Column(name="nota_minima_aprovacao_selecao_monitoria")
	private double notaMinimaAprovacaoSelecaoMonitora;
	
	
	// Constructors

	/**
	 * Informa o prazo final para autorização das propostas pelos departamentos
	 * envolvidos
	 */
	public Date getDataFimAutorizacaoDepartamento() {
		return dataFimAutorizacaoDepartamento;
	}

	/**
	 * Seta a data do prazo final para autorização das propostas pelos departamentos
	 * @param dataFimAutorizacaoDepartamento
	 */
	public void setDataFimAutorizacaoDepartamento(
			Date dataFimAutorizacaoDepartamento) {
		this.dataFimAutorizacaoDepartamento = dataFimAutorizacaoDepartamento;
	}

	/**
	 * Informa a data do início da seleção dos monitores
	 * @return
	 */
	public Date getInicioSelecaoMonitor() {
		return inicioSelecaoMonitor;
	}

	/**
	 * Seta a data do início da seleção dos monitores
	 * @param inicioSelecaoMonitor
	 */
	public void setInicioSelecaoMonitor(Date inicioSelecaoMonitor) {
		this.inicioSelecaoMonitor = inicioSelecaoMonitor;
	}

	/**
	 * Informa a data do fim da seleção dos monitores
	 * @return
	 */
	public Date getFimSelecaoMonitor() {
		return fimSelecaoMonitor;
	}

	/**
	 * Seta a data do fim da seleção dos monitores
	 * @param fimSelecaoMonitor
	 */
	public void setFimSelecaoMonitor(Date fimSelecaoMonitor) {
		this.fimSelecaoMonitor = fimSelecaoMonitor;
	}

	/** default constructor */
	public EditalMonitoria() {
		setEdital(new Edital());
	}

	/** full constructor */
	public EditalMonitoria(int idEditalMonitoria, short numeroBolsas,
			double valorFinanciamento, String numeroEdital) {
		this.numeroBolsas = numeroBolsas;
		this.valorFinanciamento = valorFinanciamento;
		this.numeroEdital = numeroEdital;
	}

	/**
	 * Informa o número de bolsas
	 * @return
	 */
	public short getNumeroBolsas() {
		return this.numeroBolsas;
	}

	/**
	 * Seta o número de bolsas
	 * @param numeroBolsas
	 */
	public void setNumeroBolsas(short numeroBolsas) {
		this.numeroBolsas = numeroBolsas;
	}

	/**
	 * Informa o valor do financiamento
	 * @return
	 */
	public double getValorFinanciamento() {
		return this.valorFinanciamento;
	}

	/**
	 * Seta o valor do financiamento
	 * @param valorFinanciamento
	 */
	public void setValorFinanciamento(double valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}

	/**
	 * Informa o número do edital
	 * @return
	 */
	public String getNumeroEdital() {
		return this.numeroEdital;
	}

	/**
	 * Seta o número do edital
	 * @param numeroEdital
	 */
	public void setNumeroEdital(String numeroEdital) {
		this.numeroEdital = numeroEdital;
	}

	/**
	 * Informa a data de publicação do edital
	 * @return
	 */
	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	/**
	 * Seta a data de publicação do edital
	 * @param dataPublicacao
	 */
	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	/**
     * Realiza as validações das informações obrigatórias para o cadastro de um edital de monitoria
     */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		lista.addAll(edital.validate());
		
		ValidatorUtil.validateRequired(getTipo(), "Tipo de Edital", lista);
		ValidatorUtil.validateRequired(getNumeroEdital(), "Número do Edital", lista);
		if (getAnoProjetoRelatorioParcialIncio() > getAnoProjetoRelatorioParcialFim()){
			lista.addMensagem(MensagemAviso.valueOf("Ano Inicial de R. Parciais Pendentes maior que Ano Final", TipoMensagemUFRN.ERROR));
		}
		if (getAnoProjetoRelatorioFinalIncio() > getAnoProjetoRelatorioFinalFim()){
			lista.addMensagem(MensagemAviso.valueOf("Ano Inicial de R. Finais Pendentes maior que Ano Final", TipoMensagemUFRN.ERROR));
		}
		ValidatorUtil.validateRequired(getDataPublicacao(), "Data de Publicação", lista);		
		ValidatorUtil.validateRequired(getDataResultadoFinalEdital(), "Data do Resultado Final do Edital", lista);
		ValidatorUtil.validateRequired(getNotaMinimaAprovacaoSelecaoMonitora(), "Nota Mínima para Aprovação na Seleção de Monitoria", lista);
		ValidatorUtil.validateRequired(getDataFimReconsideracaoReqFormais(), "Fim da Reanálise dos Req. Formais",	lista);
		ValidatorUtil.validateRequired(getDataFimAutorizacaoDepartamento(), "Autorização dos departamentos até", lista);
		ValidatorUtil.validateRequired(getValorFinanciamento(), "Valor do Financiamento (R$)", lista);
		ValidatorUtil.validateRequired(getRelacaoDiscenteOrientador(), "Relação Discente/Orientador", lista);
		ValidatorUtil.validateRequired(getFatorCargaHoraria(), "Fator de Carga Horária", lista);
		ValidatorUtil.validateRequired(getPesoMediaAnalise(), "Peso da Média de Análise", lista);
		ValidatorUtil.validateRequired(getPesoNumProfessores(), "Peso do Número de Professores", lista);
		ValidatorUtil.validateRequired(getPesoCompCurriculares(), "Peso do Número de Componentes", lista);
		ValidatorUtil.validateRequired(getPesoNumDepartamentos(), "Peso do Número de Departamentos", lista);
		ValidatorUtil.validateRequired(getPesoRT(), "Peso do RT", lista);
		ValidatorUtil.validateRequired(getMediaAprovacaoProjeto(), "Média para Aprovação", lista);
		ValidatorUtil.validateRequired(getIndiceAvaliacaoDiscrepante(), "Índice de Avaliação Discrepante", lista);		
		
		if (this.getTipo() == Edital.MONITORIA || this.getTipo() == Edital.AMBOS_MONITORIA_E_INOVACAO){
			ValidatorUtil.validaInt(getNumeroBolsas(), "Número de Bolsas", lista);
			ValidatorUtil.validateRequired(getInicioSelecaoMonitor(), "Início da Seleção de Monitores", lista);
			ValidatorUtil.validateRequired(getFimSelecaoMonitor(), "Fim da Seleção de Monitores", lista);
			ValidatorUtil.validaInicioFim(getInicioSelecaoMonitor(), getFimSelecaoMonitor(), "Período de Seleção de Monitores", lista);
		}
		return lista;
	}

	/**
	 * Informa o peso dos componentes curriculares
	 * @return
	 */
	public double getPesoCompCurriculares() {
		return pesoCompCurriculares;
	}

	/**
	 * Seta o peso dos componentes curriculares
	 * @param pesoCompCurriculares
	 */
	public void setPesoCompCurriculares(double pesoCompCurriculares) {
		this.pesoCompCurriculares = pesoCompCurriculares;
	}

	/**
	 * Informa o peso da média de análise
	 * @return the pesoMediaAnalise
	 */
	public double getPesoMediaAnalise() {
		return pesoMediaAnalise;
	}

	/**
	 * Seta o pesoMediaAnalise
	 * @param pesoMediaAnalise
	 *            
	 */
	public void setPesoMediaAnalise(double pesoMediaAnalise) {
		this.pesoMediaAnalise = pesoMediaAnalise;
	}

	/**
	 * Informa o pesoNumDepartamentos
	 * @return the pesoNumDepartamentos
	 */
	public double getPesoNumDepartamentos() {
		return pesoNumDepartamentos;
	}

	/**
	 * Seta o pesoNumDepartamentos
	 * @param pesoNumDepartamentos
	 */
	public void setPesoNumDepartamentos(double pesoNumDepartamentos) {
		this.pesoNumDepartamentos = pesoNumDepartamentos;
	}

	/**
	 * Informa o pesoNumProfessores
	 * @return the pesoNumProfessores
	 */
	public double getPesoNumProfessores() {
		return pesoNumProfessores;
	}

	/**
	 * Seta o pesoNumProfessores
	 * @param pesoNumProfessores
	 */
	public void setPesoNumProfessores(double pesoNumProfessores) {
		this.pesoNumProfessores = pesoNumProfessores;
	}

	/**
	 * Informa o pesoRT
	 * @return the pesoRT
	 */
	public double getPesoRT() {
		return pesoRT;
	}

	/**
	 * Seta o pesoRT
	 * @param pesoRT
	 */
	public void setPesoRT(double pesoRT) {
		this.pesoRT = pesoRT;
	}

	@Transient
	public String getDescricaoCompleta() {
		return this.numeroEdital + " - " + getDescricao() + " - " + getTipoString();
	}

	public Date getDataFimReconsideracaoReqFormais() {
		return dataFimReconsideracaoReqFormais;
	}

	public void setDataFimReconsideracaoReqFormais(
			Date dataFimReconsideracaoReqFormais) {
		this.dataFimReconsideracaoReqFormais = dataFimReconsideracaoReqFormais;
	}

	/**
	 * Departamento pode autorizar propostas enviando pra prograd
	 */
	public boolean isPermitidoDepartamentoAutorizar() {
	    if (getDataFimAutorizacaoDepartamento() != null) {
		return (DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
				.compareTo(getDataFimAutorizacaoDepartamento()) <= 0);
	    }
	    return false;
	}

	/**
	 * Informa se é ou não permitido PermitidoReconsideracaoReqFormais
	 * @return
	 */
	public boolean isPermitidoReconsideracaoReqFormais() {
	    if (getDataFimReconsideracaoReqFormais() !=  null) {
		return (DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
				.compareTo(getDataFimReconsideracaoReqFormais()) <= 0);
	    }
	    return false;
	}

	/**
	 * Informa se o resultado final do edital já foi publicado
	 * 
	 */
	public Date getDataResultadoFinalEdital() {
		return dataResultadoFinalEdital;
	}

	public void setDataResultadoFinalEdital(Date dataResultadoFinalEdital) {
		this.dataResultadoFinalEdital = dataResultadoFinalEdital;
	}

	/**
	 * True Se o resultado final do edital já foi publicado
	 * 
	 * @return
	 */
	public boolean isResultadoFinalPublicado() {
	    if (getDataResultadoFinalEdital() != null) {
		return (DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)
				.compareTo(getDataResultadoFinalEdital()) >= 0);
	    }
	    return false;
	}

	/**
	 * Informa o período que dever ser verificado se o docente enviou o
	 * relatório parcial utilizado na validação dos cadastros dos docentes no
	 * projeto só poderão fazer parte do projeto os docentes que tiverem enviado
	 * relatórios parciais no período informado.
	 * 
	 * @return
	 */
	public int getAnoProjetoRelatorioParcialIncio() {
		return anoProjetoRelatorioParcialIncio;
	}

	public void setAnoProjetoRelatorioParcialIncio(
			int anoProjetoRelatorioParcialIncio) {
		this.anoProjetoRelatorioParcialIncio = anoProjetoRelatorioParcialIncio;
	}

	public int getAnoProjetoRelatorioParcialFim() {
		return anoProjetoRelatorioParcialFim;
	}

	public void setAnoProjetoRelatorioParcialFim(
			int anoProjetoRelatorioParcialFim) {
		this.anoProjetoRelatorioParcialFim = anoProjetoRelatorioParcialFim;
	}

	/**
	 * Informa o período que dever ser verificado se o docente enviou o
	 * relatório parcial utilizado na validação dos cadastros dos docentes no
	 * projeto só poderão fazer parte do projeto os docentes que tiverem enviado
	 * relatórios parciais no período informado.
	 * 
	 * @return
	 */
	public int getAnoProjetoRelatorioFinalIncio() {
		return anoProjetoRelatorioFinalIncio;
	}

	public void setAnoProjetoRelatorioFinalIncio(
			int anoProjetoRelatorioFinalIncio) {
		this.anoProjetoRelatorioFinalIncio = anoProjetoRelatorioFinalIncio;
	}

	public int getAnoProjetoRelatorioFinalFim() {
		return anoProjetoRelatorioFinalFim;
	}

	public void setAnoProjetoRelatorioFinalFim(int anoProjetoRelatorioFinalFim) {
		this.anoProjetoRelatorioFinalFim = anoProjetoRelatorioFinalFim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Edital getEdital() {
		return edital;
	}

	public void setEdital(Edital edital) {
		this.edital = edital;
	}
	
	public String getDescricao() {
		return edital.getDescricao();
	}

	/**
	 * Seta a descrição
	 * @param descricao
	 */
	public void setDescricao(String descricao) {
		edital.setDescricao(descricao);
	}
	
	public Integer getIdArquivo() {
		return edital.getIdArquivo();
	}

	/**
	 * Seta o id do Arquivo
	 * @param idArquivo
	 */
	public void setIdArquivo(Integer idArquivo) {
		edital.setIdArquivo(idArquivo);
	}

	public char getTipo() {
		return edital.getTipo();
	}

	/**
	 * Seta o tipo do Arquivo	
	 * @param tipo
	 */
	public void setTipo(char tipo) {
		edital.setTipo(tipo);
	}
	
	public Date getInicioSubmissao() {
		return edital.getInicioSubmissao();
	}

	/**
	 * Seta a data de início da Submissão
	 * @param inicioSubmissao
	 */
	public void setInicioSubmissao(Date inicioSubmissao) {
		edital.setInicioSubmissao(inicioSubmissao);
	}
	
	public Date getFimSubmissao() {
		return edital.getFimSubmissao();
	}

	/**
	 * Seta a data de fim da Submissão
	 * @param fimSubmissao
	 */
	public void setFimSubmissao(Date fimSubmissao) {
		edital.setFimSubmissao(fimSubmissao);
	}

	public Date getDataCadastro() {
		return edital.getDataCadastro();
	}

	/**
	 * Seta a data de cadastro
	 * @param dataCadastro
	 */
	public void setDataCadastro(Date dataCadastro) {
		edital.setDataCadastro(dataCadastro);
	}
	
	public Usuario getUsuario() {
		return edital.getUsuario();
	}

	/**
	 * Seta o usuário
	 * @param usuario
	 */
	public void setUsuario(Usuario usuario) {
		edital.setUsuario(usuario);
	}
	
	public String getTipoString(){
		return edital.getTipoString();
	}
	
	public int getAno() {
		return edital.getAno();
	}

	/**
	 * Seta o ano
	 * @param ano
	 */
	public void setAno(int ano) {
		edital.setAno(ano);
	}

	public int getSemestre() {
		return edital.getSemestre();
	}

	/**
	 * Seta o semestre
	 * @param semestre
	 */
	public void setSemestre(int semestre) {
		edital.setSemestre(semestre);
	}

	public boolean isAtivo() {
		return edital.isAtivo();
	}

	/**
	 * Seta se o edital está ou não ativo
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		edital.setAtivo(ativo);
	}
	
	/**
	 * Informa a descrição em forma de String
	 */
	public String toString() {
		return getDescricao() + " (" + getTipoString() + ")";
	}
	
	public boolean isEmAberto() {
		return edital.isEmAberto();
	}
	
	public boolean isAssociado() {
		return edital.getTipo() == Edital.ASSOCIADO;
	}

	public Integer getFatorCargaHoraria() {
		return fatorCargaHoraria;
	}

	public void setFatorCargaHoraria(Integer fatorCargaHoraria) {
		this.fatorCargaHoraria = fatorCargaHoraria;
	}

	/**
	 * Média mínima para aprovação de um projeto de monitoria.
	 * 
	 * @return
	 */
	public double getMediaAprovacaoProjeto() {
	    return mediaAprovacaoProjeto;
	}

	public void setMediaAprovacaoProjeto(double mediaAprovacaoProjeto) {
	    this.mediaAprovacaoProjeto = mediaAprovacaoProjeto;
	}

	/**
	 * Índice que informa o quanto discrepante pode ser as avaliações
	 * realizadas pelos membros do comitê.
	 * Caso a diferênça de notas entre dois avaliadores seja maior que esse índice
	 * o projeto deverá ser analisado por um terceiro avaliador.
	 *  
	 * @return
	 */
	public double getIndiceAvaliacaoDiscrepante() {
	    return indiceAvaliacaoDiscrepante;
	}

	public void setIndiceAvaliacaoDiscrepante(double indiceAvaliacaoDiscrepante) {
	    this.indiceAvaliacaoDiscrepante = indiceAvaliacaoDiscrepante;
	}

	/**
	 * Determina a quantidade de discentes que cada docente do projeto pode orientar.
	 * 
	 * @return
	 */
	public int getRelacaoDiscenteOrientador() {
	    return relacaoDiscenteOrientador;
	}

	public void setRelacaoDiscenteOrientador(int relacaoDiscenteOrientador) {
	    this.relacaoDiscenteOrientador = relacaoDiscenteOrientador;
	}

	public double getNotaMinimaAprovacaoSelecaoMonitora() {
		return notaMinimaAprovacaoSelecaoMonitora;
	}

	public void setNotaMinimaAprovacaoSelecaoMonitora(
			double notaMinimaAprovacaoSelecaoMonitora) {
		this.notaMinimaAprovacaoSelecaoMonitora = notaMinimaAprovacaoSelecaoMonitora;
	}
	
}