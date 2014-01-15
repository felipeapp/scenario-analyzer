/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.tecnico.dao.ProcessoSeletivoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.MovimentoProcessoSeletivoTecnico;

/** Controller responsável pelas operações sobre Processos Seletivos.
 * 
 * @author Édipo Elder F. Melo
 * @author Fred_Castro
 *
 */
@Component("processoSeletivoTecnico")
@Scope("request")
public class ProcessoSeletivoTecnicoMBean extends SigaaAbstractController<ProcessoSeletivoTecnico> {

	/** Construtor padrão. */
	public ProcessoSeletivoTecnicoMBean() {
		this.obj = new ProcessoSeletivoTecnico();
	}
	
	public String getLabelCombo () {
		return "nome";
	}

	/**
	 * Cadastra / atualiza o processo seletivo.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/ProcessoSeletivoTecnico/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);
		
		erros.addAll(obj.validate());
		if (hasErrors())
			return null;
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} 
		beforeCadastrarAfterValidate();
		prepareMovimento(SigaaListaComando.CADASTRAR_PROCESSO_SELETIVO_TECNICO);
		MovimentoProcessoSeletivoTecnico movimento = new MovimentoProcessoSeletivoTecnico();
		movimento.setProcessoSeletivo(obj);
		movimento.setCodMovimento(SigaaListaComando.CADASTRAR_PROCESSO_SELETIVO_TECNICO);
		try {
			execute(movimento);
		} catch (NegocioException e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (getConfirmButton().equalsIgnoreCase("alterar")){
			addMensagemInformation("Processo Seletivo alterado com sucesso!");
			return forward(getListPage());
		} else {
			addMensagemInformation("Processo Seletivo cadastrado com sucesso!");
			return cancelar();
		}
	}
	
	/**
	 * Método Utilizado para carregar o Processo Seletivo Técnico selecionado pelo discente
	 * para a realização das operações desejadas.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/ProcessoSeletivoTecnico/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String atualizar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO);

		try {
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
	
			setId();
			setReadOnly(false);
			setObj( getGenericDAO().findByPrimaryKey(obj.getId(), ProcessoSeletivoTecnico.class) );
			setConfirmButton("Alterar");
			afterAtualizar();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
		}
		
		return forward(getFormPage());
	}
	
	/** Evita que ocorra um NullPointerException ao setar o questionário.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj.getFormaIngresso() == null) {
			obj.setFormaIngresso(new FormaIngresso());
		}
	}
	
	/** Retorna uma coleção de processos seletivos ativos.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllAtivoCombo() throws DAOException {
		return getAllAtivo(ProcessoSeletivoTecnico.class, "id", "nome");
	}

	/** Retorna uma coleção de processos seletivos.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<ProcessoSeletivoTecnico> getAll() throws ArqException {
		if (all == null) {
			ProcessoSeletivoTecnicoDao dao = getDAO(ProcessoSeletivoTecnicoDao.class);
			all = dao.findAllOrderByAnoPeriodo();
		}
		return all;
	}
	
	/**
	 * Retorna o link para a listagem de processos seletivos.<br>
	 * Método não invocado por JSP´s.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/tecnico/ProcessoSeletivoTecnico/lista.jsf";
	}
	
	/**
	 * Prepara para a operação de cadastro.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/menus/cadastros.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		this.obj = new ProcessoSeletivoTecnico();
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		this.obj.setAnoEntrada(cal.get(Calendar.YEAR));
		return super.preCadastrar();
	}
	
	public String getFormPage () {
		return "/tecnico/ProcessoSeletivoTecnico/form.jsf";
	}
	
	/** Seta para nulos, os objetos com ID = 0.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeCadastrarAfterValidate()
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,	SegurancaException, DAOException {
		super.beforeCadastrarAfterValidate();
	}

	/** Método invocado após remover um processo seletivo.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeRemover()
	 */
	@Override
	public void beforeRemover() throws DAOException {
		super.beforeRemover();
	}
	
	/** Método invocado após cadastrar um processo seletivo.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterCadastrar()
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		this.obj = new ProcessoSeletivoTecnico();
	}

	/**
	 * Prepara para operação de remoção de um processo seletivo.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/tecnico/ProcessoSeletivoVestibular/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String prepareRemover() throws ArqException {
		prepareMovimento(ArqListaComando.REMOVER);
		GenericDAO dao = getGenericDAO();
		this.obj = new ProcessoSeletivoTecnico();
		setId();
		this.obj = dao.findByPrimaryKey(obj.getId(), ProcessoSeletivoTecnico.class);
		if (this.obj == null)
			throw new ArqException(ConstantesErro.SOLICITACAO_JA_PROCESSADA,"");

		setReadOnly(true);
		setConfirmButton("Remover");
		return forward(getFormPage());
	}
}
