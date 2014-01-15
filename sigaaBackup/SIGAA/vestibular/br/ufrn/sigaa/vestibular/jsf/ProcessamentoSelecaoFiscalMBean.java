/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/06/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.QuantidadeFiscalPorMunicipio;
import br.ufrn.sigaa.vestibular.dominio.ResumoProcessamentoSelecao;
import br.ufrn.sigaa.vestibular.negocio.MovimentoSelecaoFiscal;

/** Controller respons�vel pelo processamento da sele��o de fiscais.
 * @author �dipo Elder F. Melo
 *
 */
@Component("processamentoSelecaoFiscal")
@Scope("session")
public class ProcessamentoSelecaoFiscalMBean extends
		SigaaAbstractController<MovimentoSelecaoFiscal> {
	
	/** Quantidades (inscritos, a processar) de fiscais por munic�pios. */ 
	private Collection<QuantidadeFiscalPorMunicipio> fiscaisPorMunicipio = null;
	
	/** ID do processo seletivo a processar. */
	private int idProcessoSeletivo;
	
	/** Movimento usado para encapsular os dados utilizados no processamento de fiscais. */
	private MovimentoSelecaoFiscal movimento; 
	
	/** Resumo quantitativo da simula��o do processamento */
	private List<ResumoProcessamentoSelecao> resumoProcessamento;
	
	private IndiceAcademico indiceSelecaoGraduacao;

	/**
	 * Checa permiss�es, inicializa os atributos e retorna o formul�rio para escolha do PS a processar.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/processamentos/selecao_fiscal.jsp
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.VESTIBULAR);
		fiscaisPorMunicipio = null;
		indiceSelecaoGraduacao = null;
		idProcessoSeletivo = 0;
		movimento = new MovimentoSelecaoFiscal();
		resumoProcessamento = new ArrayList<ResumoProcessamentoSelecao>();
		movimento.setResumoProcessamento(resumoProcessamento);
		return forward("/vestibular/processamentos/selecao_fiscal.jsp");
	}

	/**
	 * Simula a sele��o de fiscais e retorna para o usu�rio, um resumo do processamento.
	 *
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>/vestibular/processamentos/selecao_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.sigaa.vestibular.negocio.ProcessadorSelecaoFiscal
	 * 
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	@SuppressWarnings("unchecked")
	public String simularProcessamento() throws ArqException, NegocioException {
		checkRole(SigaaPapeis.VESTIBULAR);
		if (movimento == null) {
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		}
		validacaoDados(erros.getMensagens());
		if (hasErrors()) {
			return null;
		}
		prepareMovimento(SigaaListaComando.PROCESSAR_SELECAO_FISCAL);
		movimento.setFiscaisPorMunicipio(fiscaisPorMunicipio);
		movimento.setCodMovimento(SigaaListaComando.PROCESSAR_SELECAO_FISCAL);
		movimento.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular(idProcessoSeletivo));
		movimento.setSimulaProcessamento(true);
		
		resumoProcessamento = (List<ResumoProcessamentoSelecao>) execute(movimento);
		
		
		if (resumoProcessamento == null || resumoProcessamento.size() == 0) {
			addMensagemInformation("N�o h� fiscais selecionados para o Processo Seletivo para os valores informados.");
			return null;
		} else {
			return forward("/vestibular/processamentos/pre_processamento.jsp");
		}
	}
	
	/**
	 * Consolida o processamento da sele��o de fiscais.
	 *
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>/vestibular/processamentos/pre_processamento.jsp</li>
	 * </ul>
	 *  
	 * @see br.ufrn.sigaa.vestibular.negocio.ProcessadorSelecaoFiscal
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String consolidarProcessamento() throws ArqException {
		checkRole(SigaaPapeis.VESTIBULAR);
		if (movimento == null) {
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");
		}
		validacaoDados(erros.getMensagens());
		if (getResumoProcessamento() == null|| getResumoProcessamento().size() == 0)
			addMensagemErro("Uma simula��o do processamento deve ser realizada antes da consolida��o.");
		if (hasErrors()) {
			return null;
		}
		prepareMovimento(SigaaListaComando.PROCESSAR_SELECAO_FISCAL);
		movimento.setCodMovimento(SigaaListaComando.PROCESSAR_SELECAO_FISCAL);
		movimento.setResumoProcessamento(resumoProcessamento);
		movimento.setSimulaProcessamento(false);
		
		int numSelecionado = 0;
		
		try {
			numSelecionado = (Integer) execute(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		addMensagemInformation("Processamento conclu�do com " + numSelecionado
				+ " fiscais selecionados.");
		movimento = null;
		resetBean();
		return redirectJSF(getSubSistema().getLink());
	}

	/** Retorna o ID do processo seletivo a processar. 
	 * @return ID do processo seletivo a processar. 
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do processo seletivo a processar. 
	 * @param idProcessoSeletivo ID do processo seletivo a processar. 
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/**
	 * Lista contendo objetos da class QuantidadeFiscalPorMunicipio utilizada no
	 * formul�rio de entrada do processamento da sele��o de fiscais. Cada objeto
	 * cont�m o munic�pio, o n�mero de inscritos respectivo, e coleta do
	 * formul�rio o n�mero de fiscais e o percentual de reservas a selecionar
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>vestibular/processamentos/entrada.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<QuantidadeFiscalPorMunicipio> getFiscaisPorMunicipio()
			throws DAOException {
		if (fiscaisPorMunicipio == null) {
			InscricaoFiscalDao dao = getDAO(InscricaoFiscalDao.class);
			fiscaisPorMunicipio = dao
					.findQuantidadeInscritoPorMunicipio(idProcessoSeletivo);
		}
		return fiscaisPorMunicipio;
	}

	/** Seta a quantidades (inscritos, a processar) de fiscais por munic�pios. 
	 * @param fiscaisPorMunicipio
	 */
	public void setFiscaisPorMunicipio(
			Collection<QuantidadeFiscalPorMunicipio> fiscaisPorMunicipio) {
		this.fiscaisPorMunicipio = fiscaisPorMunicipio;
	}

	/** Valida os dados necess�rios ao processamento.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {
		ArrayList<MensagemAviso> msgs = new ArrayList<MensagemAviso>();
		if (idProcessoSeletivo == 0) {
			addMensagemErro("Selecione um Processo Seletivo V�lido");
		} else {
			if (isSelecaoProcessada())
				msgs
						.add(new MensagemAviso(
								"A Sele��o de Fiscais para este Processo Seletivo j� foi realizada.",
								TipoMensagemUFRN.ERROR));
			if (fiscaisPorMunicipio.size() == 0) {
				msgs
						.add(new MensagemAviso(
								"N�o h� inscri��es de fiscais para este Processo Seletivo",
								TipoMensagemUFRN.ERROR));
			} else {
				boolean algumZero = false;
				for (QuantidadeFiscalPorMunicipio quant : fiscaisPorMunicipio) {
					if (quant.getNumFiscais() > 9999)
						msgs
								.add(new MensagemAviso(
										"O n�mero de fiscais do munic�pio "+quant.getNome()+" n�o deve ser superior � 9999",
										TipoMensagemUFRN.ERROR));
					else if (quant.getNumFiscais() < 0)
						msgs.add(new MensagemAviso(
								"O n�mero de fiscais do munic�pio "+quant.getNome()+" � inv�lido",
								TipoMensagemUFRN.ERROR));
					else if (quant.getNumFiscais() > quant.getNumInscritos())
						msgs
								.add(new MensagemAviso(
										"O n�mero de fiscais do munic�pio "+quant.getNome()+" n�o pode ser maior que o n�mero de inscritos",
										TipoMensagemUFRN.ERROR));
					else if (quant.getNumFiscais() == 0)
						algumZero = true;
					if (quant.getPercentualReserva() < 0)
						msgs.add(new MensagemAviso(
								"O percentual de reservas do munic�pio "+quant.getNome()+" � inv�lido",
								TipoMensagemUFRN.ERROR));
					if (quant.getPercentualReserva() > 100)
						msgs
								.add(new MensagemAviso(
										"O percentual de reservas do munic�pio "+quant.getNome()+" n�o pode ser maior que 100%",
										TipoMensagemUFRN.ERROR));
				}
				if (algumZero)
					msgs
							.add(new MensagemAviso(
									"Informe o n�mero de fiscais a selecionar em todos munic�pio(s)",
									TipoMensagemUFRN.ERROR));
			}
		}
		mensagens.addAll(msgs);
		return msgs.size() > 0;
	}

	/**
	 * Listener do formul�rio cuja a fun��o � recarregar a lista de quantidade
	 * de fiscais por munic�pio, caso seja selecionado um novo Processo Seletivo
	 * no formul�rio
	 * 
	 * <br />
	 * Chamado por
	 * <ul>
	 * <li>/vestibular/processamentos/entrada.jsp</li>
	 * </ul>
	 * 
	 * @param evento
	 * @return
	 */
	public String processoSeletivoListener(ValueChangeEvent evento) {
		this.idProcessoSeletivo = (Integer) evento.getNewValue();
		this.fiscaisPorMunicipio = null;
		this.resumoProcessamento = new ArrayList<ResumoProcessamentoSelecao>();
		movimento.setResumoProcessamento(resumoProcessamento);
		return null;
	}

	/** Indica se a sele��o de fiscais para este processo seletivo j� foi realizada.
	 * @return
	 */
	public boolean isSelecaoProcessada() {
		GenericDAO dao = getGenericDAO();
		try {
			ProcessoSeletivoVestibular ps = dao.findByPrimaryKey(
					this.idProcessoSeletivo, ProcessoSeletivoVestibular.class);
			return ps.isSelecaoFiscalProcessada();
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
			return false;
		}
	}
	
	/** Retorna o resumo quantitativo do processamento.
	 * @return resumoProcessamento
	 */
	public List<ResumoProcessamentoSelecao> getResumoProcessamento() {
		return resumoProcessamento;
	}

	/** Seta o resumo quantitativo do processamento
	 * @param resumoProcessamento 
	 */
	public void setResumoProcessamento(List<ResumoProcessamentoSelecao> resumoProcessamento) {
		this.resumoProcessamento = resumoProcessamento;
	}
	
	/** Retorna ao formul�rio de entrada do processamento da sele��o de fiscais.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/vestibular/processamentos/pre_processamento.jsp</li>
	 * </ul>
	 * @return /vestibular/processamentos/selecao_fiscal.jsp
	 */
	public String telaProcessamento(){
		return forward("/vestibular/processamentos/selecao_fiscal.jsp");
	}

	public IndiceAcademico getIndiceSelecaoGraduacao() throws DAOException {
		if (indiceSelecaoGraduacao == null) {
			indiceSelecaoGraduacao = getGenericDAO().findByPrimaryKey(ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO), IndiceAcademico.class);
		}
		return indiceSelecaoGraduacao;
	}
}
