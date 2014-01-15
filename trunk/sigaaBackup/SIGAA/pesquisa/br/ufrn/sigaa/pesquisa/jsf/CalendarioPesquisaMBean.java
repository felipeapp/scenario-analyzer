/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/12/2009
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;

/**
 * Managed Bean responsável pelas operações com calendários de pesquisa.
 * 
 * @author Leonardo Campos
 *
 */
public class CalendarioPesquisaMBean extends SigaaAbstractController<CalendarioPesquisa> {
	
	private static final String JSP_CALENDARIOS = "/pesquisa/calendario/calendarios.jsp";
	
	public Collection<CalendarioPesquisa> calendarios = new HashSet<CalendarioPesquisa>();
	
	public CalendarioPesquisaMBean(){
		initObj();
	}

	private void initObj() {
		obj = new CalendarioPesquisa();
	}

	public Collection<CalendarioPesquisa> getCalendarios() {
		return calendarios;
	}

	public Collection<SelectItem> getCalendariosCombo() {
		return toSelectItems(getCalendarios(), "id", "anoVigente");
	}
	
	public void setCalendarios(Collection<CalendarioPesquisa> calendarios) {
		this.calendarios = calendarios;
	}

	/** 
	 * Inicializa o caso de uso, desde que o usuário tenha permissão para acessa-lá.
	 * Inicializa o obj e realiza um busca por todos os calendário ativos e direciona o 
	 * usuário para a tela do calendário
	 * <br><br>
	 * JSP:  /SIGAA/app/sigaa.ear/sigaa.war/WEB-INF/jsp/pesquisa/menu/projetos.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String iniciar() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
		initObj();
		calendarios = getGenericDAO().findAllAtivos(CalendarioPesquisa.class, "ano");
		setConfirmButton("Cadastrar Calendário");
		return telaCalendarios();
	}
	
	/**
	 * Esse método serve para carregar o calendário. 
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/calendario/calendarios.jsp 
	 * 
	 * @param evento
	 * @throws DAOException
	 */
	public void carregarCalendario(ValueChangeEvent evento) throws DAOException {
		GenericDAO dao = getGenericDAO();
		int idEscolhido = Integer.parseInt(evento.getNewValue().toString());
		if (idEscolhido == 0) {
			setConfirmButton("Cadastrar Calendário");
			obj = new CalendarioPesquisa();
		} else {
			obj = dao.findByPrimaryKey(idEscolhido, CalendarioPesquisa.class);
			setConfirmButton("Alterar Calendário");
		}
	}
	
	public String telaCalendarios() {
		return forward(JSP_CALENDARIOS);
	}
	
	/**
	 * Esse método serve para realizar o cadastro ou alteração de um cadastro.
	 * <br><br>
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/pesquisa/calendario/calendarios.jsp
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String confirmar() throws SegurancaException, DAOException {
		erros = new ListaMensagens();
		ListaMensagens lista = obj.validate();

		if (lista != null) {
			erros.addAll(lista.getMensagens());
		}
		if (hasErrors())
			return null;

		Comando comando = null;
		String msg = "Calendário alterado com sucesso!";
		if (obj.getId() == 0) {
			comando = ArqListaComando.CADASTRAR;
			msg = "Calendário criado com sucesso!";
			if (isEmpty(calendarios))
				obj.setVigente(true);
			obj.setAtivo(true);
		} else {
			comando = SigaaListaComando.ALTERAR_CALENDARIO_PESQUISA;
		}
		try {
			prepareMovimento(comando);
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setObjMovimentado(obj);
			mov.setCodMovimento(comando);
			executeWithoutClosingSession(mov, getCurrentRequest());
			erros = new ListaMensagens();
			addMessage(msg, TipoMensagemUFRN.INFORMATION);
		} catch (Exception e) {
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}
		if (isUserInRole(SigaaPapeis.GESTOR_PESQUISA))
			return iniciar();

		return cancelar();
	}
}