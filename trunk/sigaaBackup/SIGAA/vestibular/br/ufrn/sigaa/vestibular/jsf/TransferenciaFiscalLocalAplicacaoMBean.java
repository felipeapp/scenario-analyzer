/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/06/2008
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

/** Controller responsável pela transferência de fiscais entre locais de aplicação de prova.
 * @author Édipo Elder F. Melo
 *
 */
@Component("transferenciaFiscal")
@Scope("session")
public class TransferenciaFiscalLocalAplicacaoMBean extends	SigaaAbstractController<ArrayList<ObjectSeletor<Fiscal>>> {

	/** ID do processo seletivo a transferir fiscais. */
	private int idProcessoSeletivo;
	
	/** ID do local de aplicação de prova de origem. */
	private int idLocalAplicacaoOrigem = 0;
	
	/** ID do local de aplicação de prova de destino. */
	private int idLocalAplicacaoDestino = 0;
	
	/** Lista de locais de aplicação. */
	private List<LocalAplicacaoProva> locaisAplicacao;

	/** Verifica os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Reseta os atributos deste controller. */
	private void clear() {
		idProcessoSeletivo = 0;
		idLocalAplicacaoOrigem = 0;
		idLocalAplicacaoDestino = 0;
		obj = new ArrayList<ObjectSeletor<Fiscal>>();
		locaisAplicacao = new ArrayList<LocalAplicacaoProva>();
	}

	/** Inicia o processo de transferência de fiscais entre locais de aplicação de prova.
	 * 
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/Fiscal/transf_fiscal.jsp
	 * @throws ArqException
	 */
	public String iniciarTransferencia() throws ArqException {
		clear();
		prepareMovimento(SigaaListaComando.TRANSFERENCIA_FISCAL_LOCAL_APLICACAO);
		return forward("/vestibular/Fiscal/transf_fiscal.jsp");
	}

	/** Carrega a lista de fiscais do local de aplicação de origem.
	 * @return
	 * @throws DAOException
	 */
	private String carregaListaFiscal() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		List<Fiscal> lista = dao.findByProcessoSeletivoLocalAplicacao(
				idProcessoSeletivo, idLocalAplicacaoOrigem, false, true);
		lista.addAll(dao.findByProcessoSeletivoLocalAplicacao(
				idProcessoSeletivo, idLocalAplicacaoOrigem, true, true));
		obj = new ArrayList<ObjectSeletor<Fiscal>>();
		for (Fiscal fiscal : lista) {
			ObjectSeletor<Fiscal> fiscalTransf = new ObjectSeletor<Fiscal>();
			fiscalTransf.setSelecionado(false);
			fiscalTransf.setObjeto(fiscal);
			obj.add(fiscalTransf);
		}
		return null;
	}

	/** Carrega a lista de locais de aplicação.
	 * @return
	 * @throws DAOException
	 */
	private String carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = dao.findByProcessoSeletivo(idProcessoSeletivo);
		obj = new ArrayList<ObjectSeletor<Fiscal>>();
		idLocalAplicacaoOrigem = 0;
		return null;
	}

	/** Transfere fiscais entre os locais de aplicação de prova.
	 * 
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/vestibular/Fiscal/transf_fiscal.jsp</li>
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
		movimento.setListaFiscalTransferencia(obj);
		LocalAplicacaoProvaDao localDao = getDAO(LocalAplicacaoProvaDao.class);
		LocalAplicacaoProva local = localDao.findByPrimaryKey(
				idLocalAplicacaoDestino, LocalAplicacaoProva.class);
		movimento.setLocalAplicacaoDestino(local);
		movimento.setCodMovimento(SigaaListaComando.TRANSFERENCIA_FISCAL_LOCAL_APLICACAO);
		List<Fiscal> fiscaisTransferidos = new ArrayList<Fiscal>();
		try {
			fiscaisTransferidos = (List<Fiscal>) executeWithoutClosingSession(movimento);
			prepareMovimento(SigaaListaComando.TRANSFERENCIA_FISCAL_LOCAL_APLICACAO);
			carregaListaFiscal();
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (fiscaisTransferidos.size() > 0) {
			addMensagemInformation(fiscaisTransferidos.size()
					+ " Fiscais transferidos com sucesso!");
		} else {
			addMensagemErro("Selecione pelo menos um fiscal a ser transferido");
		}
		return getSubSistema().getForward();
	}

	/** Retorna o ID do processo seletivo a transferir fiscais. 
	 * @return
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do processo seletivo a transferir fiscais. 
	 * @param idProcessoSeletivo
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Retorna o ID do local de aplicação de prova de origem. 
	 * @return
	 */
	public int getIdLocalAplicacaoOrigem() {
		return idLocalAplicacaoOrigem;
	}

	/** Seta o ID do local de aplicação de prova de origem. 
	 * @param idLocalAplicacaoOrigem
	 */
	public void setIdLocalAplicacaoOrigem(int idLocalAplicacaoOrigem) {
		this.idLocalAplicacaoOrigem = idLocalAplicacaoOrigem;
	}

	/** Retorna o ID do local de aplicação de prova de destino. 
	 * @return
	 */
	public int getIdLocalAplicacaoDestino() {
		return idLocalAplicacaoDestino;
	}

	/** Seta o ID do local de aplicação de prova de destino. 
	 * @param idLocalAplicacaoDestino
	 */
	public void setIdLocalAplicacaoDestino(int idLocalAplicacaoDestino) {
		this.idLocalAplicacaoDestino = idLocalAplicacaoDestino;
	}

	/** Retorna uma coleção de SelectItem de locais de aplicação de prova. 
	 * @return
	 */
	public Collection<SelectItem> getLocaisAplicacaoOrigem() {
		return AbstractController.toSelectItems(locaisAplicacao, "id", "nome");
	}

	/** Retorna uma coleção de SelectItem de locais de aplicação de prova de destino.
	 * @return
	 */
	public Collection<SelectItem> getLocaisAplicacaoDestino() {
		ArrayList<LocalAplicacaoProva> locaisAplicacaoDestino = new ArrayList<LocalAplicacaoProva>();
		for (LocalAplicacaoProva local : locaisAplicacao) {
			if (local.getId() != idLocalAplicacaoOrigem)
				locaisAplicacaoDestino.add(local);
		}
		if (idLocalAplicacaoDestino == idLocalAplicacaoOrigem) {
			idLocalAplicacaoDestino = 0;
		}
		return AbstractController.toSelectItems(locaisAplicacaoDestino, "id",
				"nome");
	}

	/** Seta a lista de locais de aplicação. 
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<LocalAplicacaoProva> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/** Retorna um datamodel da lista de fiscais a transferir.
	 * @return
	 */
	public DataModel getFiscaisDataModel() {
		return new ListDataModel(obj);
	}

	/** Listener responsável por carregar a lista de locais de aplicação quando o valor do ID do processo seletivo é mudado.
	 * 
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/vestibular/Fiscal/transf_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String localProvaListener(ValueChangeEvent evt) throws DAOException{
		this.idProcessoSeletivo = (Integer) evt.getNewValue();
		return carregaLocalAplicacao();
	}
	
	/** Listener responsável por carregar a lista de fiscais quando o valor do ID do local de aplicação de prova de origem é mudado.
	 * 
	 * <br />
	 * chamado por 
	 * <ul>
	 * <li>/vestibular/Fiscal/transf_fiscal.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String localOrigemListener(ValueChangeEvent evt) throws DAOException{
		this.idLocalAplicacaoOrigem = (Integer) evt.getNewValue();
		return carregaListaFiscal();
	}
}
