/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/09/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaProcessoSeletivoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProvaProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;

/**
 * Controlador da associa��o de Locais de Aplica��o de Prova � Processos
 * Seletivos.
 * 
 * @author �dipo Elder F. de Melo
 */
@Component("localAplicacaoProcessoSeletivo")
@Scope("session")
public class LocalAplicacaoProvaProcessoSeletivoMBean extends
		SigaaAbstractController<LocalAplicacaoProvaProcessoSeletivo> {

	/** Lista de munic�pios dos locais de aplica��o de prova cadastrado. */
	private List<Municipio> listaMunicipios;
	
	/**
	 * Lista de locais de aplica��o de prova n�o associados de um determinado munic�pio. */
	private List<LocalAplicacaoProva> listaLocaisProva;
	
	/**
	 * Lista de locais de aplica��o de prova associados ao Processo Seletivo de
	 * um determinado munic�pio. */
	private List<LocalAplicacaoProvaProcessoSeletivo> listaLocalProvaProcessoSeletivo;

	/**
	 * Nome do bot�o no formul�rio. */
	private final String ASSOCIAR = "Associar ao Processo Seletivo";

	/**
	 * Listener para carregar a lista de munic�pios dos locais de prova
	 * cadastrados, caso seja selecionado outro Processo Seletivo
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * */
	public void carregaListaMunicipio(ValueChangeEvent evento)
			throws DAOException {
		// this.idProcessoSeletivo = (Integer) evento.getNewValue();
		this.obj.setProcessoSeletivoVestibular(new ProcessoSeletivoVestibular(
				(Integer) evento.getNewValue()));
		carregaListaMunicipio();
		this.obj.getLocalAplicacaoProva().getEndereco().getMunicipio().setId(0);
		forward ("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}

	/**
	 * Carrega a lista de munic�pios dos locais de prova cadastrados
	 * 
	 * @throws DAOException
	 * */
	private void carregaListaMunicipio() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		if (this.obj.getProcessoSeletivoVestibular().getId() < 1) {
			this.obj.getLocalAplicacaoProva().getEndereco().getMunicipio()
					.setId(0);
			this.listaMunicipios = new ArrayList<Municipio>();
		} else {
			this.listaMunicipios = dao.findAllMunicipios();
		}
		this.obj.getLocalAplicacaoProva().getEndereco().getMunicipio().setId(0);
		carregaListaLocalProva();
	}

	/**
	 * Listener para carregar a lista de locais de prova de um determinado
	 * munic�pio, caso seja selecionado o Processo Seletivo
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 * */
	public void carregaLocalProva(ValueChangeEvent evento)
			throws DAOException {
		this.obj.getLocalAplicacaoProva().getEndereco().getMunicipio().setId(
				(Integer) evento.getNewValue());
		carregaListaLocalProva();
		forward("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}

	/**
	 * Carrega a lista de locais de prova de um determinado munic�pio
	 * 
	 * @throws DAOException
	 * */
	private void carregaListaLocalProva() throws DAOException {
		carregaListaLocalProvaProcessoSeletivo();
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		this.listaLocaisProva = new ArrayList<LocalAplicacaoProva>();
		// inclui na lista apenas os locais que ainda n�o foram associados.
		for (LocalAplicacaoProva local : dao.findByMunicipio(obj
				.getLocalAplicacaoProva().getEndereco().getMunicipio())) {
			boolean associado = false;
			for (LocalAplicacaoProvaProcessoSeletivo localAssociado : this.listaLocalProvaProcessoSeletivo) {
				if (localAssociado.getLocalAplicacaoProva().getId() == local
						.getId()) {
					associado = true;
					break;
				}
			}
			if (!associado) {
				this.listaLocaisProva.add(local);
			}
		}
	}

	/**
	 * carrega a lista de locais de provas associados ao processo seletivo
	 * 
	 * @throws DAOException
	 */
	private void carregaListaLocalProvaProcessoSeletivo() throws DAOException {
		LocalAplicacaoProvaProcessoSeletivoDao dao = getDAO(LocalAplicacaoProvaProcessoSeletivoDao.class);
		this.listaLocalProvaProcessoSeletivo = dao
				.findAllByMunicipioProcessoSeletivo(obj
						.getLocalAplicacaoProva().getEndereco().getMunicipio(),
						obj.getProcessoSeletivoVestibular());
	}

	/** Retorna uma cole��o de SelectItem de munic�pios.
	 * @return
	 */
	public Collection<SelectItem> getMunicipiosCombo() {
		return toSelectItems(this.listaMunicipios, "id", "nome");
	}

	/** Retorna uma cole��o de SelectItem de locais de prova.
	 * @return
	 */
	public Collection<SelectItem> getLocaisProvaCombo() {
		return toSelectItems(this.listaLocaisProva, "id", "nome");
	}

	/**
	 * Inicializa as propriedades do controller.
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp
	 */
	public String iniciar() {
		this.obj = new LocalAplicacaoProvaProcessoSeletivo();
		this.listaLocalProvaProcessoSeletivo = new ArrayList<LocalAplicacaoProvaProcessoSeletivo>();
		this.listaLocaisProva = new ArrayList<LocalAplicacaoProva>();
		this.listaMunicipios = new ArrayList<Municipio>();
		setConfirmButton(ASSOCIAR);
		return forward("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}

	/**
	 *  Associa um local de aplica��o de prova � um processo seletivo.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		// caso use o bot�o "voltar" do navegador
		if (obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		validacaoDados(erros.getMensagens());
		if (hasErrors())
			return null;
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setObjMovimentado(obj);
		movimento.setCodMovimento(ArqListaComando.CADASTRAR);
		try {
			prepareMovimento(ArqListaComando.CADASTRAR);
			executeWithoutClosingSession(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		// mantem o processo seletivo e munic�pio selecionado pelo usu�rio
		LocalAplicacaoProvaProcessoSeletivo novoObj = new LocalAplicacaoProvaProcessoSeletivo();
		novoObj.setProcessoSeletivoVestibular(obj.getProcessoSeletivoVestibular());
		novoObj.setLocalAplicacaoProva(obj.getLocalAplicacaoProva());
		obj = novoObj;
		carregaListaLocalProva();
		carregaListaLocalProvaProcessoSeletivo();
		addMensagemInformation("Local de aplica��o de prova associado ao Processo Seletivo com sucesso.");
		return forward("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}

	/** limpa os atributos do controller, ap�s a remo��o da associa��o.
	 * <br/>M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterRemover()
	 */
	@Override
	public void afterRemover() {
		// ap�s remover,
		try {
			carregaListaLocalProva();
			obj.getLocalAplicacaoProva().setId(0);
			obj.setCoordenador(null);
			obj.setNomeCoordenador(null);
			obj.setLocalReuniao(null);
		} catch (DAOException e) {
			e.printStackTrace();
		} finally {
			setConfirmButton(ASSOCIAR);
		}
	}

	/** Prepara para remover uma associa��o entre o local de aplica��o de prova e um processo seletivo.
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preRemover()
	 */
	@Override
	public String preRemover() {
		super.preRemover();
		return forward("/vestibular/LocalAplicacaoProva/remover_associacao.jsp");
	}

	/** Retorna o endere�o dor formul�rio para listar as associa��es .
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp";
	}

	/** Verifica os pap�is: VESTIBULAR.
	 * <br/>M�todo n�o invocado por JSP's
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/** Retorna a capacidade total associada.
	 * @return 
	 */
	public int getCapacidadeTotalAssociada() {
		int total = 0;
		if (this.listaLocalProvaProcessoSeletivo.size() == 0)
			return 0;
		for (LocalAplicacaoProvaProcessoSeletivo localPS : this.listaLocalProvaProcessoSeletivo) {
			total += localPS.getLocalAplicacaoProva().getCapacidadeIdealTotal();
		}
		return total;
	}

	/** Retorna o link do formul�rio de cadastro
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/vestibular/LocalAplicacaoProva/editar_associacao_processo_seletivo.jsp";
	}

	/** Redireciona o usu�rio ap�s remover uma associa��o.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardRemover()
	 */
	@Override
	protected String forwardRemover() {
		return forward("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}

	/** Valida os dados do formul�rio.
	 * 
	 * <br/>M�todo n�o invocado por JSP's
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(Collection<MensagemAviso> mensagens) {
		boolean retorno = true;
		if (this.obj.getLocalAplicacaoProva().getEndereco().getMunicipio()
				.getId() == 0) {
			mensagens.add(new MensagemAviso("Selecione um Munic�pio v�lido",
					TipoMensagemUFRN.ERROR));
			retorno = false;
		}
		if (this.obj.getProcessoSeletivoVestibular().getId() == 0) {
			mensagens.add(new MensagemAviso(
					"Selecione um Processo Seletivo v�lido",
					TipoMensagemUFRN.ERROR));
			retorno = false;
		}
		if (this.obj.getLocalAplicacaoProva().getId() == 0) {
			mensagens.add(new MensagemAviso(
					"Selecione um Local de Aplica��o de Prova v�lido",
					TipoMensagemUFRN.ERROR));
			retorno = false;
		}
		return retorno;
	}

	/** Remove uma associa��o entre um local de aplica��o de prova e um munic�pio.
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/remover_associacao.jsp</li>
	 * </ul> 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 * 
	 */
	@Override
	public String remover() throws ArqException {
		if (obj == null || obj.getId() == 0)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		String retorno = super.remover();
		obj.setId(0);
		return retorno;
	}

	/** Retorna uma lista de locais de aplica��o de prova associados ao processo seletivo.
	 * @return
	 */
	public List<LocalAplicacaoProvaProcessoSeletivo> getListaLocalProvaProcessoSeletivo() {
		return listaLocalProvaProcessoSeletivo;
	}

	/** Retorna um datamodel da lista de locais de provas associadas ao processo seletivo.
	 * @return
	 */
	public DataModel getLocalProvaDataModel() {
		return new ListDataModel(this.listaLocalProvaProcessoSeletivo);
	}

	/**
	 * Grava as altera��es de coordenador, local da reuni�o e hor�rio. Seletivo
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/editar_associacao_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp
	 * @throws ArqException
	 */
	public String alterar() throws ArqException {
		// caso use o bot�o "voltar" do navegador
		if (obj == null || obj.getId() == 0)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA, "");
		// caso o nome seja diferente do nome em banco, desassocia do servidor
		Servidor srv = null;
		if (obj.getCoordenador() != null)
			srv = getGenericDAO().findByPrimaryKey(obj.getCoordenador().getId(), Servidor.class);
		if (srv != null && !srv.getNome().equalsIgnoreCase(obj.getNomeCoordenador()))
			obj.setCoordenador(null);
		MovimentoCadastro movimento = new MovimentoCadastro();
		movimento.setObjMovimentado(obj);
		movimento.setCodMovimento(ArqListaComando.ALTERAR);
		try {
			prepareMovimento(ArqListaComando.ALTERAR);
			executeWithoutClosingSession(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		obj.setId(0);
		obj.getLocalAplicacaoProva().setId(0);
		obj.setCoordenador(null);
		obj.setNomeCoordenador(null);
		obj.setLocalReuniao(null);
		carregaListaLocalProva();
		// carregaListaLocalProvaProcessoSeletivo();
		addMensagemInformation("Local de aplica��o de prova atualizado com sucesso.");
		return forward("/vestibular/LocalAplicacaoProva/associar_processo_seletivo.jsp");
	}
	
	/** Retorna uma cole��o de coordenadores para o recurso de autocompletar do formul�rio.
	 * 
	 * <br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/vestibular/LocalAplicacaoProva/editar_associacao_processo_seletivo.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Servidor> autocompleteCoordenador(Object event) throws DAOException {
		String nome = event.toString();
		ServidorDao servidorDao = getDAO(ServidorDao.class);
		return (List<Servidor>) servidorDao.findByNome(nome, 0);
	}
	
	/** Atualiza o coordenador do pr�dio, quando o usu�rio seleciona um valor do suggestionbox do formul�rio.
	 *  
	 *  <br/>
	 * M�todo chamado pela seguinte JSP:
	 *  <ul>
	 *  	<li>/vestibular/LocalAplicacaoProva/editar_associacao_processo_seletivo.jsp</li>
	 *  </ul> 
	 *  
	 * @return
	 * @throws DAOException
	 */
	public void atualizaServidor(ActionEvent e) throws DAOException {
		ServidorDao servidorDAO = getDAO(ServidorDao.class);
		int idServidor = (Integer) e.getComponent().getAttributes().get("idServidor");
		obj.setCoordenador(servidorDAO.findByPrimaryKey(idServidor, Servidor.class));
	}

}
