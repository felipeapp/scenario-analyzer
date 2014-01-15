/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/06/2008
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

import br.ufrn.arq.dominio.Comando;
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
import br.ufrn.sigaa.vestibular.negocio.MovimentoPresencaReuniaoFiscal;

/**
 * Controller responsável pelo cadastro de assiduidades dos fiscais nos
 * processos seletivos.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("assiduidadeFiscal")
@Scope("session")
public class AssiduidadeFiscalMBean extends
		SigaaAbstractController<List<Fiscal>> {

	/** ID do processo Seletivo ao qual os fiscais estão inscritos. */
	private int idProcessoSeletivo = 0;
	
	/** Local de aplicação de prova onde os fiscais trabalharam. */
	private int idLocalAplicacao;
	
	/** Lista de locais de aplicação de provas do processo seletivo*/
	private List<LocalAplicacaoProva> locaisAplicacao;
	
	/** Comando utilizado para o encapsulamento dos dados. */
	private Comando comando = null;

	/** Verifica se possui os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Construtor padrão. */
	public AssiduidadeFiscalMBean() {
		clear();
	}

	/** Limpa os atributos deste controller. */
	private void clear() {
		idProcessoSeletivo = 0;
		obj = new ArrayList<Fiscal>();
		locaisAplicacao = new ArrayList<LocalAplicacaoProva>();
	}

	/** Inicia a operação de marcação de presença de fiscais na reunião de fiscais.
	 * 
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/Fiscal/presenca_reuniao.jsp
	 * @throws ArqException
	 */
	public String iniciarPresencaReuniao() throws ArqException {
		clear();
		comando = SigaaListaComando.PROCESSAR_PRESENCA_REUNIAO_FISCAL;
		return forward("/vestibular/Fiscal/presenca_reuniao.jsp");
	}

	/** Inicia a operação de convocação de fiscais reservas.
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * @return /vestibular/Fiscal/convoca_reservas.jsp
	 * @throws ArqException
	 */
	public String iniciarConvocacaoReservas() throws ArqException {
		clear();
		comando = SigaaListaComando.CONVOCAR_FISCAIS_RESERVAS;
		return forward("/vestibular/Fiscal/convoca_reservas.jsp");
	}

	/** Inicia a operação de marcação de presença de fiscais durante a aplicação de provas.
	 * 
	 * <ul>
	 * 	<li>/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * Chamado por 
	 * @return /vestibular/Fiscal/frequencia_aplicacao.jsp
	 * @throws ArqException
	 */
	public String iniciarFrequenciaAplicacao() throws ArqException {
		clear();
		comando = SigaaListaComando.PROCESSAR_FREQUENCIA_APLICACAO_FISCAL;
		return forward("/vestibular/Fiscal/frequencia_aplicacao.jsp");
	}

	/** Retorna um data model com a lista de fiscais.
	 * @return
	 */
	public DataModel getFiscaisReuniaoDataModel() {
		return new ListDataModel(obj);
	}

	/** Retorna o ID do processo Seletivo ao qual os fiscais estão inscritos. 
	 * @return
	 */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Seta o ID do processo Seletivo ao qual os fiscais estão inscritos. 
	 * @param idProcessoSeletivo
	 */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/**
	 * Listener responsável por atualizar a lista de locais de aplicação de
	 * prova quando o usuário altera o ID do processo seletivo.
	 * <ul>
	 * 	<li>
	 * 		/vestibular/Fiscal/frequencia_aplicacao.jsp
	 * 	</li>
	 * 	<li>
	 * 		/vestibular/Fiscal/presenca_reuniao.jsp
	 * 	</li>
	 * 	 <li>
	 * 		/vestibular/Fiscal/convoca_reservas.jsp
	 * 	</li>
	 * </ul>
	 * @param evt
	 * @return
	 * @throws DAOException
	 */
	public String carregaLocalAplicacao(ValueChangeEvent evt)
			throws DAOException {
		idProcessoSeletivo = (Integer) evt.getNewValue();
		return carregaLocalAplicacao();
	}

	/**
	 * Listener responsável por atualizar a lista de fiscais associadas ao
	 * local de prova escolhido, quando o usuário altera o local de aplicação de
	 * prova.
	 * <ul>
	 * 	<li>
	 * 		/vestibular/Fiscal/convoca_reservas.jsp
	 * 	</li>
	 * 	<li>
	 * 		/vestibular/Fiscal/frequencia_aplicacao.jsp
	 * 	</li>
	 * 	<li>
	 * 		/vestibular/Fiscal/presenca_reuniao.jsp
	 * 	</li>
	 * </ul>
	 * 
	 * @param evt
	 * @return
	 * @throws ArqException
	 */
	public String carregaListaFiscais(ValueChangeEvent evt) throws ArqException {
		idLocalAplicacao = (Integer) evt.getNewValue();
		return carregaListaFiscais();
	}

	/** Carrega a lista de fiscais associadas ao local de prova escolhido.
	 * <ul>
	 * <li>/vestibular/Fiscal/frequencia_aplicacao.jsp</li>
	 * 	<li>/vestibular/Fiscal/presenca_reuniao.jsp</li>
	 * </ul>
	 *
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String carregaListaFiscais() throws DAOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		if (comando.equals(SigaaListaComando.CONVOCAR_FISCAIS_RESERVAS)) {
			obj = dao.findByProcessoSeletivoLocalAplicacao(idProcessoSeletivo,
					idLocalAplicacao, true, true);
		} else if (comando.equals(SigaaListaComando.PROCESSAR_FREQUENCIA_APLICACAO_FISCAL)) {
			obj = dao.findByProcessoSeletivoLocalAplicacao(idProcessoSeletivo,
					idLocalAplicacao, false, false);
		} else {
			obj = dao.findByProcessoSeletivoLocalAplicacao(idProcessoSeletivo,
					idLocalAplicacao, false, false);
			obj.addAll(dao.findByProcessoSeletivoLocalAplicacao(
					idProcessoSeletivo, idLocalAplicacao, true, false));
		}
		return null;
	}

	/** Carrega a lista de locais de aplicação de prova associados ao processo seletivo.
	 * @return
	 * @throws DAOException
	 */
	private String carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = dao.findByProcessoSeletivo(idProcessoSeletivo);
		obj = new ArrayList<Fiscal>();
		this.idLocalAplicacao = 0;
		return null;
	}

	/** Atualiza as assiduidades dos fiscais.
	 * 
	 * <ul>
	 * <li>/vestibular/Fiscal/presenca_reuniao.jsp</li>
	 * <li>/vestibular/Fiscal/frequencia_aplicacao.jsp</li>
	 * <li>/vestibular/Fiscal/convoca_reservas.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws ArqException {
		if (comando == null) {
			addMensagemErro("Solicitação já processada. Utilize o menu para iniciar uma nova operação.");
			redirectJSF(getSubSistema().getLink());
			return null;
		}
		MovimentoPresencaReuniaoFiscal movimento = new MovimentoPresencaReuniaoFiscal();
		movimento.setListaFiscal(obj);
		movimento.setCodMovimento(comando);
		try {
			prepareMovimento(comando);
			executeWithoutClosingSession(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		carregaListaFiscais();
		if (comando.equals(SigaaListaComando.CONVOCAR_FISCAIS_RESERVAS)) {
			addMensagemInformation("Reservas convocados com sucesso");
			return null;
		} else {
			addMensagemInformation("Assiduidades atualizadas com sucesso");
			return getSubSistema().getForward();
		}
	}
	
	/** Incrementa a frequência dos fiscais, que não estão com status ausente, de um local de aplicação de prova.
	 * 
	 * <ul>
	 * 	<li>/vestibular/Fiscal/frequencia_aplicacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String incrementarFrequencia() {
		for (Fiscal fiscal : obj) {
			if (fiscal.getPresenteAplicacao() != null
				&& fiscal.getPresenteReuniao() != null
				&& fiscal.getPresenteAplicacao().booleanValue()
				&& fiscal.getPresenteReuniao().booleanValue() )
				fiscal.setFrequencia(fiscal.getFrequencia() + 1);
		}
		return null;
	}

	/** Retorna a lista de fiscais.
	 * @return
	 */
	public List<Fiscal> getListaFiscais() {
		return obj;
	}

	/** Seta a lista de fiscais.
	 * @param listaFiscais
	 */
	public void setListaFiscais(List<Fiscal> listaFiscais) {
		this.obj = listaFiscais;
	}

	/** Retorna o ID do local de aplicação de prova onde os fiscais trabalharam. 
	 * @return
	 */
	public int getIdLocalAplicacao() {
		return idLocalAplicacao;
	}

	/** Seta o ID do local de aplicação de prova onde os fiscais trabalharam.
	 * @param idLocalAplicacao
	 */
	public void setIdLocalAplicacao(int idLocalAplicacao) {
		this.idLocalAplicacao = idLocalAplicacao;
	}

	/**
	 * Retorna uma lista de SelectItem de locais de aplicação de prova do
	 * processo seletivo.
	 * 
	 * @return
	 */
	public Collection<SelectItem> getLocaisAplicacao() {
		return AbstractController.toSelectItems(locaisAplicacao, "id", "nome");
	}

	/** Seta uma lista de SelectItem de locais de aplicação de prova do
	 * processo seletivo.
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<LocalAplicacaoProva> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/** Cancela a operação.
	 * 
	 * <ul>
	 * 	<li>/vestibular/Fiscal/frequencia_aplicacao.jsp</li>
	 * 	<li>/vestibular/Fiscal/presenca_reuniao.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		comando = null;
		idLocalAplicacao = 0;
		idProcessoSeletivo = 0;
		redirectJSF(getSubSistema().getLink());
		return null;
	}

}
