/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.dao.prodocente.AtividadesProdocenteDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.TeseOrientada;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Teses e Orienta��es de Mestrado/Doutorado e Lato-Senso (especializa��o e
 * resid�ncia m�dica)
 * 
 */
public class TeseOrientadaMBean extends AbstractControllerAtividades<TeseOrientada> {

	/** Indica se o cadastro � de um trabalho de final de curso de resid�ncia m�dica. */
	private boolean residencia;
	
	/** Indica se o trabalho � de lato sensu. */
	private boolean lato;
	
	/** Indica se o trabalho � de stricto sensu. */
	private boolean stricto;

	/** Construtor padr�o. */
	public TeseOrientadaMBean() {
		initObj();
	}

	/** Inicializa os atributos do controller. */
	private void initObj() {
		obj = new TeseOrientada();
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setSubArea(new AreaConhecimentoCnpq());
		obj.setTipoOrientacao(new TipoOrientacao());
		obj.setServidor(new Servidor());
		obj.setOrientandoDiscente(new Discente());
		obj.setProgramaPos(new Unidade());
		obj.setInstituicaoEnsino(new InstituicoesEnsino(InstituicoesEnsino.UFRN));
		residencia = false;
		getUsuarioLogado();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(TeseOrientada.class, "id", "titulo");
	}

	/**
	 * Este m�todo n�o � chamado em JSPs.
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException, SegurancaException, DAOException {
		
		try // se � um discente cadastrado ap�s a migra��o do antigo Prodocente
		{
			if (obj.getDiscenteExterno())
				obj.setOrientandoDiscente(null);

			obj.setDiscenteMigrado(false);
		} catch (Exception e) // se o discente foi migrado do antigo Prodocente:
		{
			obj.setDiscenteMigrado(true);
			obj.setOrientandoDiscente(null);
		}

		if ( !ValidatorUtil.isEmpty( obj.getPrograma()) && ValidatorUtil.isEmpty( obj.getProgramaPos()) ){
			obj.setProgramaPos(null);
		}

		super.beforeCadastrarAfterValidate();
	}
	
	/**
	 * Em Tese, existem discentes que foram Migrados do antigo Prodocente,
	 * apesar de n�o serem "discentes externos" eles s� tem a string do nome
	 * cadastrada, por�m o campo booleano discente_externo � null. Este m�todo
	 * verifica se o campo discente_externo � null, ou seja se ele foi migrado
	 * do antigo Prodocente.
	 * 
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 * @param tese
	 * @throws DAOException
	 */
	public Boolean getDiscenteMigrado() throws DAOException {
		AtividadesProdocenteDao dao = getDAO(AtividadesProdocenteDao.class);
		return dao.isDiscenteMigrado(obj);
	}

	/** Retorna a lista de teses orientadas de resid�ncias m�dicas.
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/form.jsp
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/view.jsp
	 * Chamado por /sigaa.war/prodocente/index.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String listarResidencia() throws ArqException {
		initObj();
		residencia = true;
		lato = false;
		stricto = false;
		return super.listar();
	}

	/** Retorna a lista de trabalhos orientados de lato senso.
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/view.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String listarLato() throws ArqException {
		initObj();
		residencia = false;
		lato = true;
		stricto = false;
		return super.listar();
	}

	/** Retorna a lista de trabalhos orientados de stricto senso.
	 * Chamado por /sigaa.war/portais/docente/menu_docente.jsp
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/form.jsp
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/view.jsp
	 * Chamado por /sigaa.war/stricto/coordenador.jsp 
	 * @return
	 * @throws ArqException 
	 */
	public String listarStricto() throws ArqException {
		initObj();
		residencia = false;
		lato = true;
		stricto = true;
		return super.listar();
	}

	/** Inicia o cadastro de uma orienta��o de resid�ncia m�dica.
	 * Chamado por /sigaa.war/prodocente/atividades/TeseOrientada/lista.jsp
	 * @return
	 */
	public String cadastrarResidencia() {
		residencia = true;
		super.preCadastrar();
		setConfirmButton("Cadastrar");
		return null;
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		validarData();
		super.cadastrar();
		if(residencia)
			return cadastrarResidencia();
		return forwardCadastrar();
	}
	
	@Override
	public String forwardCadastrar() {
		return getFormPage();
	}
	
	/**
	 * M�todo serve para valida��o das datas, ou seja se foram informadas datas v�lidas.<br />
	 * M�todo n�o invocado por JSP(s).
	 * 
	 * @return
	 */
	public String validarData() {

		FacesContext context = FacesContext.getCurrentInstance();
		Iterator<FacesMessage> messages = context.getMessages();
		
		if (messages.hasNext()) {
			FacesMessage facesMessage = messages.next();
			addMensagemErro(facesMessage.getSummary());
		}
		if (hasErrors())
			return null;

		if (obj.getPeriodoInicio() == null) {
			addMensagemErro("Per�odo In�cio: Campo obrigat�rio n�o informado.");
			return null;
		}

		return null;
	}

	/**
	 * Chamado na JSP: /sigaa.war/prodocente/atividades/TeseOrientada/lista.jsp
	 */
	@Override
	public String preCadastrar() {
		novaStricto();
		super.preCadastrar();
		setConfirmButton("Cadastrar");
		return null; 
	}

	/**
	 * Retorna uma lista de SelectItem de TiposOrienta��o. 
	 *
	 * @return
	 */
	public Collection<SelectItem> getTipoOrientacao() {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>();

		if (residencia) {
			SelectItem sl1 = new SelectItem(String.valueOf(TipoOrientacao.RESIDENCIA_MEDICA), "RESID�NCIA MEDICA");
			tipos.add(sl1);

		}
		if (lato) {
			SelectItem sl1 = new SelectItem(String.valueOf(TipoOrientacao.ESPECIALIZACAO), "ESPECIALIZA��O");
			tipos.add(sl1);
		}
		if (stricto) {
			SelectItem sl1 = new SelectItem(String.valueOf(TipoOrientacao.MESTRADO), "MESTRADO");
			SelectItem sl2 = new SelectItem(String.valueOf(TipoOrientacao.DOUTORADO), "DOUTORADO");
			SelectItem sl3 = new SelectItem(String.valueOf(TipoOrientacao.POS_DOUTORADO), "P�S-DOUTORADO");
			tipos.add(sl1);
			tipos.add(sl2);
			tipos.add(sl3);
		}
		return tipos;
	}

	/** Retorna uma cole��o de SelecItem de OrientacaoAcademica (Orientador/Co-Orientador).
	 * @return
	 */
	public Collection<SelectItem> getTipoOrientacaoDocente() {
		ArrayList<SelectItem> tipos = new ArrayList<SelectItem>();

		// SelectItem sl = new SelectItem(' ',"Selecione");
		SelectItem sl1 = new SelectItem(OrientacaoAcademica.ORIENTADOR, "Orientador");
		SelectItem sl2 = new SelectItem(OrientacaoAcademica.CoORIENTADOR, "Co-Orientador");
		// tipos.add(sl);
		tipos.add(sl1);
		tipos.add(sl2);
		return tipos;
	}

	/**
	 * Chamado na JSP: /sigaa.war/prodocente/atividades/TeseOrientada/lista.jsp
	 */
	public String buscar() throws DAOException {
		buscarTeses();
		
		return null;
	}

	/**
	 * Este m�todo n�o � chamado em JSPs.
	 */
	@Override
	public void afterRemover() {
		super.afterRemover();
		try {
			buscarTeses();
		} catch (DAOException e) {
			notifyError(e);
			e.printStackTrace();
		}
	}

	/** Realiza a busca de teses orientadas.
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void buscarTeses() throws DAOException {
		List<Integer> filtroTipoOrientacao = new ArrayList<Integer>();
		if (residencia)
			filtroTipoOrientacao.add(TipoOrientacao.RESIDENCIA_MEDICA);
		if (lato)
			filtroTipoOrientacao.add(TipoOrientacao.ESPECIALIZACAO);
		if (stricto) {
			filtroTipoOrientacao.add(TipoOrientacao.MESTRADO);
			filtroTipoOrientacao.add(TipoOrientacao.DOUTORADO);
			filtroTipoOrientacao.add(TipoOrientacao.POS_DOUTORADO);
		}

		erros = new ListaMensagens();
		if (!SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema()))
			ValidatorUtil.validateRequiredAjaxId(getIdServidor(), "Docente", erros);
 		if (!SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema())) {
 			if (!hasErrors()) {
 				buscarTese(filtroTipoOrientacao);
 			}
 		} else {
 			// busca s� pro docente logado
 			AtividadesProdocenteDao dao = getDAO(AtividadesProdocenteDao.class);
 			atividades = dao.findByServidorDepartamentoOrientacao(TeseOrientada.class, getServidorUsuario().getId(), -1, getIdTipoOrientacao(), filtroTipoOrientacao);
 		}
	}

	@Override
	protected void afterCadastrar() throws ArqException {
		super.afterCadastrar();
		initObj();
		if (stricto)
			novaStricto();
		else if (residencia)
			novaResidencia();
		else if (lato)
			novaEspecializacao();
	}

	/**
	 * Chamado na JSP: /sigaa.war/prodocente/atividades/TeseOrientada/lista.jsp
	 */
	@Override
	public String atualizar() throws ArqException {
		initObj();
		super.atualizar();
		setConfirmButton("Alterar");
		return null; 
	}

	/**
	 * Este m�todo n�o � chamado em JSPs.
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if (obj.getArea() == null)
			obj.setArea(new AreaConhecimentoCnpq());
		if (obj.getSubArea() == null)
			obj.setSubArea(new AreaConhecimentoCnpq());
		if (obj.getOrientandoDiscente() == null)
			obj.setOrientandoDiscente(new Discente());
		if (obj.getEntidadeFinanciadora() == null)
			obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		if (obj.getInstituicaoEnsino() == null)
			obj.setInstituicaoEnsino(new InstituicoesEnsino());
		if (obj.getProgramaPos() == null)
			obj.setProgramaPos( new Unidade() );

		try // os orientandos migrados do Prodocente est�o com o campo DiscenteExterno = Null no banco
		{
			// Se o orientando era da UFRN e passou a ser externo, tem que retirar o da UFRN da tabela (para n�o ficarem
			// dados inconsistentes) ambar@info.ufrn.br
			if (obj.getOrientandoDiscente() == null) {
				obj.setDiscenteExterno(true);
				obj.setOrientandoDiscente(new Discente());
			}
			// o caso contr�rio somente � verdadeiro caso o DiscenteExterno n�o seja null (ou seja caso n�o sejam os
			// discente migrados do antigo Prodocente)
			else if (obj.getOrientandoDiscente().getPessoa().getNome() != null)
				obj.setOrientando("");
			// --//
		} catch (Exception e) {
			obj.setOrientandoDiscente(new Discente());
		}

		changeArea();
		setConfirmButton("Cadastrar");
		atividades = null;
		super.afterAtualizar();
	}

	/**
	 * Chamado nas JSPs: 
	 * 	/sigaa.war/prodocente/atividades/TeseOrientada/form.jsp
	 * 	/sigaa.war/prodocente/atividades/TeseOrientada/view.jsp
	 */
	@Override
	public void changeArea() throws DAOException {

		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		if ( !ValidatorUtil.isEmpty( obj.getArea()) ) {
			subArea = toSelectItems(dao.findAreas( obj.getArea()), "id", "nome");
		}

	}

	/** Indica se � p�gina de cadastro ou atualiza��o.
	 * @return
	 */
	public boolean isAtualizar() {
		if (getConfirmButton().equalsIgnoreCase("Alterar"))
			return true;
		return false;
	}

	/**
	 * Chamado nas JSPs: 
	 * 	/sigaa.war/prodocente/atividades/TeseOrientada/form.jsp
	 * 	/sigaa.war/prodocente/atividades/TeseOrientada/view.jsp
	 */
	@Override
	public String cancelar() {
		initObj();
		return super.cancelar();
	}

	/**
	 * Este m�todo n�o � chamado em JSPs.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
//		if (!SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema()))
//			checkRole(SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG);
	}

	/**
	 * Este m�todo n�o � chamado em JSPs.
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkChangeRole();
	}

	/** Indica se o cadastro � de um trabalho de final de curso de resid�ncia m�dica. 
	 * @return 
	 */
	public boolean isResidencia() {
		return residencia;
	}

	/** Seta se o cadastro � de um trabalho de final de curso de resid�ncia m�dica. 
	 * @param residencia
	 */
	public void setResidencia(boolean residencia) {
		this.residencia = residencia;
	}

	/** Indica se o trabalho � de lato sensu. 
	 * @return
	 */
	public boolean isLato() {
		return lato;
	}

	/** Seta se o trabalho � de lato sensu. 
	 * @param lato
	 */
	public void setLato(boolean lato) {
		this.lato = lato;
	}

	/** Indica se o trabalho � de stricto sensu. 
	 * @return
	 */
	public boolean isStricto() {
		return stricto;
	}

	/** Seta se o trabalho � de stricto sensu. 
	 * @param stricto
	 */
	public void setStricto(boolean stricto) {
		this.stricto = stricto;
	}

	/** Inicia o cadastro de uma orienta��o de especializa��o.
	 * JSPs: 
	 * 	/sigaa.war/stricto/coordenador.jsp
	 * 	/sigaa.war/WEB-INF/jsp/menus/menu_lato_coordenador.jsp
	 * @return
	 */
	public String novaEspecializacao() {
		initObj();
		setConfirmButton("Cadastrar");
		lato = true;
		stricto = false;
		residencia = false;
		return super.preCadastrar();
	}

	/** Inicia o cadastro de uma orienta��o de stricto sensu.
	 * Este m�todo n�o � chamado em JSPs.
	 * @return
	 */
	public String novaStricto() {
		initObj();
		setConfirmButton("Cadastrar");
		stricto = true;
		lato = true;
		residencia = false;
		obj.setDiscenteExterno(false);
		if (SigaaSubsistemas.PORTAL_DOCENTE.equals(getSubSistema()))
			obj.setServidor(getServidorUsuario());
		return super.preCadastrar();
	}

	/** Retorna uma cole��o de SelectItem de poss�veis programas de p�s-gradua��o.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPossiveisProgramasPos() throws DAOException {
		if (stricto) {
			EquipeProgramaDao dao = getDAO(EquipeProgramaDao.class);
			List<SelectItem> selects = toSelectItems(dao.findProgramasByServidor(getServidorUsuario().getId()), "id", "nome");
			if (isEmpty(selects))
				return (new UnidadeMBean()).getAllProgramaPosCombo();
			else
				return selects;
		} else {
			return (new UnidadeMBean()).getAllProgramaPosCombo();
		}
	}

	/** Inicia o cadastro de uma orienta��o de resid�ncia m�dica.
	 * JSP: /sigaa.war/stricto/coordenador.jsp
	 * @return
	 */
	public String novaResidencia() {
		initObj();
		setConfirmButton("Cadastrar");
		residencia = true;
		lato = false;
		stricto = false;
		return super.preCadastrar();
	}
	
	/** Retorna os poss�veis n�veis para a busca de discente no "autocomplete" do formul�rio.
	 * @return
	 */
	public String getNivelEnsinoAutoComplete(){
		switch (this.obj.getTipoOrientacao().getId()) {
		case TipoOrientacao.ESPECIALIZACAO: return "L";
		case TipoOrientacao.GRADUACAO: return "G";
		case TipoOrientacao.MESTRADO: return "E";
		case TipoOrientacao.DOUTORADO: 
		case TipoOrientacao.POS_DOUTORADO: return "D";
		case TipoOrientacao.RESIDENCIA_MEDICA: return "L";
		default: return "X";
		}
	}

	/** Listener respons�vel por verificar altera��es no tipo de orienta��o
	 * JSP: /sigaa.war/prodocente/atividades/TeseOrientada/form.jsp
	 * @param evt
	 */
	public void tipoOrientacaoListener(ValueChangeEvent evt){
		this.obj.getTipoOrientacao().setId((Integer) evt.getNewValue());
	}
	
}
