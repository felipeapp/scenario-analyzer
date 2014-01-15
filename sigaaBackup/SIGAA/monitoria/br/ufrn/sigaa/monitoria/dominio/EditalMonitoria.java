/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Representa um edital de monitoria lan�ado pela PROGRAD.
 * </p>
 * <p>
 * Atrav�s de um edital de monitoria a PROGRAD estabelece prazos para o
 * recebimento de propostas, informa os recursos dispon�veis para financiamento
 * dos projetos e define regras para classifica��o dos projetos candidatos.
 * </p>
 * 
 * Para informa��es mais gerais veja {@link Edital}.
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
	 *  Atributo utilizado para representar o numero do fator de carga hor�ria do edital de monitoria que ser� multiplicado pela 
	 *  quantidade de dias que o membro passou no projeto.
	 */
	@Column(name = "fator_carga_horaria")
	private Integer fatorCargaHoraria;
	
	/** Atributo utilizado para representar o n�mero de bolsas do edital de monitoria */
	@Column(name = "numero_bolsas")
	private short numeroBolsas;

	/** Atributo utilizado para representar o valor de financiamento do edital de monitoria */
	@Column(name = "valor_financiamento")
	private double valorFinanciamento;

	/** Atributo utilizado para representar o n�mero do edital de monitoria */
	@Column(name = "numero_edital")
	private String numeroEdital;

	/** Atributo utilizado para representar o n�mero do peso para a m�dia de an�lise do edital de monitoria */
	@Column(name = "peso_media_analise")
	private double pesoMediaAnalise;

	/** Atributo utilizado para representar o n�mero do peso dos professores do edital de monitoria */
	@Column(name = "peso_num_professores")
	private double pesoNumProfessores;

	/** Atributo utilizado para representar o n�mero do peso dos componentes curriculares do edital de monitoria */
	@Column(name = "peso_comp_curriculares")
	private double pesoCompCurriculares;

	/** Atributo utilizado para representar o n�mero do peso dos departamentos do edital de monitoria */
	@Column(name = "peso_num_departamentos")
	private double pesoNumDepartamentos;

	/** Atributo utilizado para representar o n�mero do peso RT do edital de monitoria */
	@Column(name = "peso_rt")
	private double pesoRT;

	/** Atributo utilizado para representar a data de publica��o do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_publicacao")
	private Date dataPublicacao;

	/** Atributo utilizado para representar a data de in�cio da sele��o do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio_selecao")
	private Date inicioSelecaoMonitor;

	/** Atributo utilizado para representar a data de fim da sele��o do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_selecao")
	private Date fimSelecaoMonitor;

	/** Atributo utilizado para representar a data de fim da reconsidera��o do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_reconsideracao_req_formais")
	private Date dataFimReconsideracaoReqFormais;

	/** Atributo utilizado para representar a data de fim da autoriza��o do departamento do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim_autorizacao_departamento")
	private Date dataFimAutorizacaoDepartamento;

	/** Atributo utilizado para representar a data do resultado final do edital de monitoria */
	@Temporal(TemporalType.DATE)
	@Column(name = "data_resultado_final_edital")
	private Date dataResultadoFinalEdital;

	/** Atributo utilizado para validar projetos que n�o enviaram relat�rios parciais neste per�odo */
	@Column(name = "ano_projeto_relatorio_parcial_inicio")
	private int anoProjetoRelatorioParcialIncio;

	/** Atributo utilizado para validar projetos que n�o enviaram relat�rios parciais neste per�odo */
	@Column(name = "ano_projeto_relatorio_parcial_fim")
	private int anoProjetoRelatorioParcialFim;

	/** Atributo utilizado para validar projetos que n�o enviaram relat�rios finais neste per�odo */
	@Column(name = "ano_projeto_relatorio_final_inicio")
	private int anoProjetoRelatorioFinalIncio;

	/** Atributo utilizado para validar projetos que n�o enviaram relat�rios finais neste per�odo */
	@Column(name = "ano_projeto_relatorio_final_fim")
	private int anoProjetoRelatorioFinalFim;
	
	/** Atributo utilizado representar o �ndice da avalia��o discrepante */
	@Column(name="indice_avaliacao_discrepante")
	private double indiceAvaliacaoDiscrepante;
	
	/** Atributo utilizado para representar a m�dia da aprova��o do projeto */
	@Column(name="media_aprovacao_projeto")
	private double mediaAprovacaoProjeto;

	/** Atributo utilizado para representar a rela��o do discente com o coordenador onde Determina a quantidade m�xima de discentes que cada docente pode orientar.*/
	@Column(name="relacao_discente_orientador")
	private int relacaoDiscenteOrientador;
	
	/** Atributo utilizado para representar a Nota M�nima de Aprova��o na Sele��o de Monitoria */
	@Column(name="nota_minima_aprovacao_selecao_monitoria")
	private double notaMinimaAprovacaoSelecaoMonitora;
	
	
	// Constructors

	/**
	 * Informa o prazo final para autoriza��o das propostas pelos departamentos
	 * envolvidos
	 */
	public Date getDataFimAutorizacaoDepartamento() {
		return dataFimAutorizacaoDepartamento;
	}

	/**
	 * Seta a data do prazo final para autoriza��o das propostas pelos departamentos
	 * @param dataFimAutorizacaoDepartamento
	 */
	public void setDataFimAutorizacaoDepartamento(
			Date dataFimAutorizacaoDepartamento) {
		this.dataFimAutorizacaoDepartamento = dataFimAutorizacaoDepartamento;
	}

	/**
	 * Informa a data do in�cio da sele��o dos monitores
	 * @return
	 */
	public Date getInicioSelecaoMonitor() {
		return inicioSelecaoMonitor;
	}

	/**
	 * Seta a data do in�cio da sele��o dos monitores
	 * @param inicioSelecaoMonitor
	 */
	public void setInicioSelecaoMonitor(Date inicioSelecaoMonitor) {
		this.inicioSelecaoMonitor = inicioSelecaoMonitor;
	}

	/**
	 * Informa a data do fim da sele��o dos monitores
	 * @return
	 */
	public Date getFimSelecaoMonitor() {
		return fimSelecaoMonitor;
	}

	/**
	 * Seta a data do fim da sele��o dos monitores
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
	 * Informa o n�mero de bolsas
	 * @return
	 */
	public short getNumeroBolsas() {
		return this.numeroBolsas;
	}

	/**
	 * Seta o n�mero de bolsas
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
	 * Informa o n�mero do edital
	 * @return
	 */
	public String getNumeroEdital() {
		return this.numeroEdital;
	}

	/**
	 * Seta o n�mero do edital
	 * @param numeroEdital
	 */
	public void setNumeroEdital(String numeroEdital) {
		this.numeroEdital = numeroEdital;
	}

	/**
	 * Informa a data de publica��o do edital
	 * @return
	 */
	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	/**
	 * Seta a data de publica��o do edital
	 * @param dataPublicacao
	 */
	public void setDataPublicacao(Date dataPublicacao) {
		this.dataPublicacao = dataPublicacao;
	}

	/**
     * Realiza as valida��es das informa��es obrigat�rias para o cadastro de um edital de monitoria
     */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		lista.addAll(edital.validate());
		
		ValidatorUtil.validateRequired(getTipo(), "Tipo de Edital", lista);
		ValidatorUtil.validateRequired(getNumeroEdital(), "N�mero do Edital", lista);
		if (getAnoProjetoRelatorioParcialIncio() > getAnoProjetoRelatorioParcialFim()){
			lista.addMensagem(MensagemAviso.valueOf("Ano Inicial de R. Parciais Pendentes maior que Ano Final", TipoMensagemUFRN.ERROR));
		}
		if (getAnoProjetoRelatorioFinalIncio() > getAnoProjetoRelatorioFinalFim()){
			lista.addMensagem(MensagemAviso.valueOf("Ano Inicial de R. Finais Pendentes maior que Ano Final", TipoMensagemUFRN.ERROR));
		}
		ValidatorUtil.validateRequired(getDataPublicacao(), "Data de Publica��o", lista);		
		ValidatorUtil.validateRequired(getDataResultadoFinalEdital(), "Data do Resultado Final do Edital", lista);
		ValidatorUtil.validateRequired(getNotaMinimaAprovacaoSelecaoMonitora(), "Nota M�nima para Aprova��o na Sele��o de Monitoria", lista);
		ValidatorUtil.validateRequired(getDataFimReconsideracaoReqFormais(), "Fim da Rean�lise dos Req. Formais",	lista);
		ValidatorUtil.validateRequired(getDataFimAutorizacaoDepartamento(), "Autoriza��o dos departamentos at�", lista);
		ValidatorUtil.validateRequired(getValorFinanciamento(), "Valor do Financiamento (R$)", lista);
		ValidatorUtil.validateRequired(getRelacaoDiscenteOrientador(), "Rela��o Discente/Orientador", lista);
		ValidatorUtil.validateRequired(getFatorCargaHoraria(), "Fator de Carga Hor�ria", lista);
		ValidatorUtil.validateRequired(getPesoMediaAnalise(), "Peso da M�dia de An�lise", lista);
		ValidatorUtil.validateRequired(getPesoNumProfessores(), "Peso do N�mero de Professores", lista);
		ValidatorUtil.validateRequired(getPesoCompCurriculares(), "Peso do N�mero de Componentes", lista);
		ValidatorUtil.validateRequired(getPesoNumDepartamentos(), "Peso do N�mero de Departamentos", lista);
		ValidatorUtil.validateRequired(getPesoRT(), "Peso do RT", lista);
		ValidatorUtil.validateRequired(getMediaAprovacaoProjeto(), "M�dia para Aprova��o", lista);
		ValidatorUtil.validateRequired(getIndiceAvaliacaoDiscrepante(), "�ndice de Avalia��o Discrepante", lista);		
		
		if (this.getTipo() == Edital.MONITORIA || this.getTipo() == Edital.AMBOS_MONITORIA_E_INOVACAO){
			ValidatorUtil.validaInt(getNumeroBolsas(), "N�mero de Bolsas", lista);
			ValidatorUtil.validateRequired(getInicioSelecaoMonitor(), "In�cio da Sele��o de Monitores", lista);
			ValidatorUtil.validateRequired(getFimSelecaoMonitor(), "Fim da Sele��o de Monitores", lista);
			ValidatorUtil.validaInicioFim(getInicioSelecaoMonitor(), getFimSelecaoMonitor(), "Per�odo de Sele��o de Monitores", lista);
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
	 * Informa o peso da m�dia de an�lise
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
	 * Informa se � ou n�o permitido PermitidoReconsideracaoReqFormais
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
	 * Informa se o resultado final do edital j� foi publicado
	 * 
	 */
	public Date getDataResultadoFinalEdital() {
		return dataResultadoFinalEdital;
	}

	public void setDataResultadoFinalEdital(Date dataResultadoFinalEdital) {
		this.dataResultadoFinalEdital = dataResultadoFinalEdital;
	}

	/**
	 * True Se o resultado final do edital j� foi publicado
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
	 * Informa o per�odo que dever ser verificado se o docente enviou o
	 * relat�rio parcial utilizado na valida��o dos cadastros dos docentes no
	 * projeto s� poder�o fazer parte do projeto os docentes que tiverem enviado
	 * relat�rios parciais no per�odo informado.
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
	 * Informa o per�odo que dever ser verificado se o docente enviou o
	 * relat�rio parcial utilizado na valida��o dos cadastros dos docentes no
	 * projeto s� poder�o fazer parte do projeto os docentes que tiverem enviado
	 * relat�rios parciais no per�odo informado.
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
	 * Seta a descri��o
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
	 * Seta a data de in�cio da Submiss�o
	 * @param inicioSubmissao
	 */
	public void setInicioSubmissao(Date inicioSubmissao) {
		edital.setInicioSubmissao(inicioSubmissao);
	}
	
	public Date getFimSubmissao() {
		return edital.getFimSubmissao();
	}

	/**
	 * Seta a data de fim da Submiss�o
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
	 * Seta o usu�rio
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
	 * Seta se o edital est� ou n�o ativo
	 * @param ativo
	 */
	public void setAtivo(boolean ativo) {
		edital.setAtivo(ativo);
	}
	
	/**
	 * Informa a descri��o em forma de String
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
	 * M�dia m�nima para aprova��o de um projeto de monitoria.
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
	 * �ndice que informa o quanto discrepante pode ser as avalia��es
	 * realizadas pelos membros do comit�.
	 * Caso a difer�n�a de notas entre dois avaliadores seja maior que esse �ndice
	 * o projeto dever� ser analisado por um terceiro avaliador.
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