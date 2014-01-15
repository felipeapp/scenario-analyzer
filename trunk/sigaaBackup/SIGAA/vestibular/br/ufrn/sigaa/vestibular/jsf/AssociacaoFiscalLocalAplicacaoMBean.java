/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/11/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.ObjectSeletor;
import br.ufrn.sigaa.vestibular.negocio.MovimentoTransferenciaFiscal;

/** Controller responsável pela alocação de fiscais aos locais de aplicação de prova.
 * @author Édipo Elder F. Melo
 *
 */
@Component("associacaoFiscalLocalAplicacao")
@Scope("session")
public class AssociacaoFiscalLocalAplicacaoMBean extends
		SigaaAbstractController<Fiscal> {

	/** ID do processo seletivo em que os fiscais estão inscritos. */
	private int idProcessoSeletivo;
	
	/** ID do local de aplicação de prova de origem, quando transferindo fiscais entre locais. */
	private int idLocalAplicacaoOrigem = 0;
	
	/** ID do local de aplicação de prova de destino. */
	private int idLocalAplicacaoDestino = 0;
	
	/** Ordem preferencial de escolha do local de aplicação de prova.*/
	private int ordemPreferencial = 0;
	
	/** Lista de fiscais a alocar. */
	private List<ObjectSeletor<Fiscal>> listaFiscais;

	/** Lista de locais de aplicação de prova. */
	private List<LocalAplicacaoProva> locaisAplicacao;
	
	/** Total de fiscais titulares alocados. */
	private int totalTitularAlocado;
	
	/** Total de fiscais reservas alocados. */
	private int totalReservaAlocado;
	
	/** Indica se a operação de alocação de fiscais é para os com disponibilidade para viajar para outras cidades. */
	private boolean outraCidade;
	
	/** Construtor padrão. */
	public AssociacaoFiscalLocalAplicacaoMBean() {
		
	}
	
	/** Aloca os fiscais selecionados na lista.
	 * 
	 * <ul>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String cadastrar() throws ArqException {
		if (idLocalAplicacaoDestino == 0) {
			addMensagemErro("Selecione um local de aplicação de prova de destino");
			return null;
		}
		MovimentoTransferenciaFiscal movimento = new MovimentoTransferenciaFiscal();
		movimento.setListaFiscalTransferencia(listaFiscais);
		LocalAplicacaoProvaDao localDao = getDAO(LocalAplicacaoProvaDao.class);
		LocalAplicacaoProva local = localDao.findByPrimaryKey(
				idLocalAplicacaoDestino, LocalAplicacaoProva.class);
		movimento.setLocalAplicacaoDestino(local);
		movimento
				.setCodMovimento(SigaaListaComando.ASSOCIAR_FISCAL_LOCAL_APLICACAO);
		List<Fiscal> fiscaisTransferidos = new ArrayList<Fiscal>();
		try {
			fiscaisTransferidos = (List<Fiscal>) executeWithoutClosingSession(movimento);
			prepareMovimento(SigaaListaComando.ASSOCIAR_FISCAL_LOCAL_APLICACAO);
			carregaListaFiscal();
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (fiscaisTransferidos.size() > 0) {
			atualizaTotaisAlocados();
			if (outraCidade)
				carregaListaFiscalOutraCidade();
			else
				carregaListaFiscal();
			addMensagemInformation(fiscaisTransferidos.size() + " Fiscais associados com sucesso!");
		} else {
			addMensagemErro("Selecione pelo menos um fiscal a ser transferido");
		}
		return getSubSistema().getForward();
	}

	/** Carrega a lista de fiscais de acordo com o local preferencial de trabalho.
	 *  
	 * @param evt
	 * @throws DAOException
	 */
	private void carregaListaFiscal() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		List<Fiscal> lista = new ArrayList<Fiscal>();
		lista = dao.findByProcessoSeletivoLocalPreferencia(idProcessoSeletivo,
				idLocalAplicacaoOrigem, ordemPreferencial, false);
		listaFiscais = new ArrayList<ObjectSeletor<Fiscal>>();
		for (Fiscal fiscal : lista) {
			ObjectSeletor<Fiscal> fiscalTransf = new ObjectSeletor<Fiscal>();
			fiscalTransf.setSelecionado(false);
			fiscalTransf.setObjeto(fiscal);
			listaFiscais.add(fiscalTransf);
		}
		// fiscais que já estão alocados
		lista = dao.findByProcessoSeletivoLocalPreferencia(idProcessoSeletivo,
				idLocalAplicacaoOrigem, ordemPreferencial, true);
		for (Fiscal fiscal : lista) {
			ObjectSeletor<Fiscal> fiscalTransf = new ObjectSeletor<Fiscal>();
			fiscalTransf.setSelecionado(false);
			fiscalTransf.setObjeto(fiscal);
			listaFiscais.add(fiscalTransf);
		}
	}

	/**
	 * Carrega a lista de locais de aplicação e fiscais com disponibilidade para
	 * trabalhar em outra cidade. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void processoSeletivoOutraCidade(ValueChangeEvent evt) throws DAOException {
		this.idProcessoSeletivo = (Integer) evt.getNewValue();
		carregaLocalAplicacao();
		carregaListaFiscalOutraCidade();
		forward("/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp");
	}
	
	/** Carrega lista de fiscais com disponibilidade para trabalhar em outra cidade. 
	 * @throws DAOException
	 */
	private void carregaListaFiscalOutraCidade() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		List<Fiscal> lista = new ArrayList<Fiscal>();
		lista.addAll(dao
				.findByProcessoSeletivoDisponibilidadeOutraCidade(idProcessoSeletivo, false));
		lista.addAll(dao
				.findByProcessoSeletivoDisponibilidadeOutraCidade(idProcessoSeletivo, true));
		listaFiscais = new ArrayList<ObjectSeletor<Fiscal>>();
		for (Fiscal fiscal : lista) {
			ObjectSeletor<Fiscal> fiscalTransf = new ObjectSeletor<Fiscal>();
			fiscalTransf.setSelecionado(false);
			fiscalTransf.setObjeto(fiscal);
			listaFiscais.add(fiscalTransf);
		}
	}

	/** Listener responsável por atualizar os totais alocados, quando é alterado o local de origem ou destino.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp</li>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void localAplicacaoDestinoListener(ValueChangeEvent evt)
			throws HibernateException, DAOException {
		if (evt != null) {
			this.idLocalAplicacaoDestino = (Integer) evt.getNewValue();
			this.idLocalAplicacaoOrigem = (Integer) evt.getNewValue();
			this.ordemPreferencial = 1;
			atualizaTotaisAlocados();
			if (outraCidade) {
				carregaListaFiscalOutraCidade();
				forward("/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp");
			} else {
				carregaListaFiscal();
				forward("/vestibular/Fiscal/associar_processo_seletivo.jsp");
			}
		}
	}
	
	/** Atualiza o total de fiscais alocados.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void atualizaTotaisAlocados() throws HibernateException, DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		this.totalTitularAlocado = (int) dao.findTotalFiscaisLocalAplicacao(
				idProcessoSeletivo, idLocalAplicacaoDestino, false);
		this.totalReservaAlocado = (int) dao.findTotalFiscaisLocalAplicacao(
				idProcessoSeletivo, idLocalAplicacaoDestino, true);
	}

	/** Listener responsável por carregar a lista de locais de aplicação de prova ao mudar o processo seletivo.
	 *<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp</li>
	 * <li>/vestibular/Fiscal/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void processoSeletivoListener(ValueChangeEvent evt) throws DAOException {
		if (evt != null)
			this.idProcessoSeletivo = (Integer) evt.getNewValue();
		carregaLocalAplicacao();
		forward("/vestibular/Fiscal/associar_processo_seletivo.jsp");
	}
	
	/** Carrega a lista de locais de aplicação de prova.
	 * @throws DAOException
	 */
	private void carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = dao.findByProcessoSeletivo(idProcessoSeletivo);
		idLocalAplicacaoOrigem = 0;
		idLocalAplicacaoDestino = 0;
		listaFiscais = new ArrayList<ObjectSeletor<Fiscal>>();
	}

	/** Verifica se possui os papéis: VESTIBULAR.
	 * <br>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Limpa os atributos do controller. */
	private void clear() {
		idProcessoSeletivo = 0;
		idLocalAplicacaoOrigem = 0;
		idLocalAplicacaoDestino = 0;
		ordemPreferencial = 1;
		totalReservaAlocado = 0;
		totalTitularAlocado = 0;
		listaFiscais = new ArrayList<ObjectSeletor<Fiscal>>();
		locaisAplicacao = new ArrayList<LocalAplicacaoProva>();
	}

	/** Retorna um DataModel da lista de fiscais.
	 * @return DataModel da lista de fiscais.
	 */
	public DataModel getFiscaisDataModel() {
		return new ListDataModel(listaFiscais);
	}

	/** Retorna o ID do local de aplicação de prova de destino. 
	 * @return ID do local de aplicação de prova de destino.
	 */
	public int getIdLocalAplicacaoDestino() {
		return idLocalAplicacaoDestino;
	}

	/** Retorna o ID do local de aplicação de prova de origem, quando transferindo fiscais entre locais. 
	 * @return ID do local de aplicação de prova de origem, quando transferindo fiscais entre locais.
	 */
	public int getIdLocalAplicacaoOrigem() {
		return idLocalAplicacaoOrigem;
	}

	/** Retorna o ID do processo seletivo em que os fiscais estão inscritos. 
	 * @return ID do processo seletivo em que os fiscais estão inscritos.
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Retorna a lista de fiscais a alocar. 
	 * @return Lista de fiscais a alocar. 
	 */
	public List<ObjectSeletor<Fiscal>> getListaFiscais() {
		return listaFiscais;
	}

	/** Retorna uma coleção de SelectItem de locais de aplicação de prova.
	 * @return Coleção de SelecItem de locais de aplicação de prova.
	 */
	public Collection<SelectItem> getLocaisAplicacao() {
		return AbstractController.toSelectItems(locaisAplicacao, "id", "nome");
	}

	/** Inicia a alocação de fiscais com disponibilidade de viajar para outras cidades.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * Chamado por 
	 * @return /vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp
	 * @throws ArqException
	 */
	public String iniciarOutraCidade() throws ArqException {
		clear();
		prepareMovimento(SigaaListaComando.ASSOCIAR_FISCAL_LOCAL_APLICACAO);
		outraCidade = true;
		return forward("/vestibular/Fiscal/associar_processo_seletivo_outra_cidade.jsp");
	}

	/** Inicia a alocação de fiscais aos locais de aplicação de prova.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/Fiscal/associar_processo_seletivo.jsp
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();
		prepareMovimento(SigaaListaComando.ASSOCIAR_FISCAL_LOCAL_APLICACAO);
		outraCidade = false;
		return forward("/vestibular/Fiscal/associar_processo_seletivo.jsp");
	}

	/** Seta o ID do local de aplicação de prova de destino. 
	 * @param idLocalAplicacaoDestino
	 */
	public void setIdLocalAplicacaoDestino(int idLocalAplicacaoDestino) {
		this.idLocalAplicacaoDestino = idLocalAplicacaoDestino;
	}

	/** Seta o ID do local de aplicação de prova de origem, quando transferindo fiscais entre locais. 
	 * @param idLocalAplicacaoOrigem
	 */
	public void setIdLocalAplicacaoOrigem(int idLocalAplicacaoOrigem) {
		this.idLocalAplicacaoOrigem = idLocalAplicacaoOrigem;
	}

	/** Seta o ID do processo seletivo em que os fiscais estão inscritos. 
	 * @param idProcessoSeletivo
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Retorna a ordem preferencial de escolha do local de aplicação de prova.
	 * @return
	 */
	public int getOrdemPreferencial() {
		return ordemPreferencial;
	}

	/** Seta a ordem preferencial de escolha do local de aplicação de prova.
	 * @param ordemPreferencial
	 */
	public void setOrdemPreferencial(int ordemPreferencial) {
		this.ordemPreferencial = ordemPreferencial;
	}

	/** Retorna o Total de fiscais titulares alocados. 
	 * @return
	 */
	public int getTotalTitularAlocado() {
		return totalTitularAlocado;
	}

	/** Retorna o total de fiscais reservas alocados. 
	 * @return
	 */
	public int getTotalReservaAlocado() {
		return totalReservaAlocado;
	}

	/** Seta o Total de fiscais titulares alocados.
	 * @param totalTitularAlocado
	 */
	public void setTotalTitularAlocado(int totalTitularAlocado) {
		this.totalTitularAlocado = totalTitularAlocado;
	}

	/** Seta o total de fiscais reservas alocados.
	 * @param totalReservaAlocado
	 */
	public void setTotalReservaAlocado(int totalReservaAlocado) {
		this.totalReservaAlocado = totalReservaAlocado;
	}

	/** Listener responsável pela atualização da lista de fiscais quando o valor do local de aplicação de prova de origem é mudado.
	 * <br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/Fiscal/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void localAplicacaoOrigemListener(ValueChangeEvent evt) throws DAOException {
		if (evt != null)
			idLocalAplicacaoOrigem = (Integer) evt.getNewValue();
		carregaListaFiscal();
	}

	/** Listener responsável pela atualização da lista de fiscais quando o valor da ordem preferencial do local de aplicação de prova de origem é mudado.
	 *<br> Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/vestibular/Fiscal/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void ordemPreferencialListener(ValueChangeEvent evt)
			throws DAOException {
		if (evt != null)
			ordemPreferencial = (Integer) evt.getNewValue();
		carregaListaFiscal();
		forward("/vestibular/Fiscal/associar_processo_seletivo.jsf");
	}
}
