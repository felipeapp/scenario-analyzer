/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;


/**
 * MBean responsável pelo gerenciamento do CRUD de grupo de ítens. 
 * 
 * @author UFRN
 *
 */
@Component("grupoItemAvaliacao")
@Scope("request")
public class GrupoItemAvaliacaoMBean extends SigaaAbstractController<GrupoItemAvaliacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construtor padrão
	 */
	public GrupoItemAvaliacaoMBean() {
		obj = new GrupoItemAvaliacao();
	}	

	/**
	 * Método usado para cadastrar um grupo de ítens.
	 * <br />	
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul> 
	 *  <li>sigaa.war/monitoria/GrupoItemAvaliacao/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		ListaMensagens mensagens = new ListaMensagens();
		obj.validate();
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		if(getConfirmButton().equals("Cadastrar") && !obj.isAtivo()){
			super.cadastrar();
			obj.setAtivo(false);
			getGenericDAO().updateField(obj.getClass(), obj.getId(), "ativo", obj.isAtivo());
			return getListPage();
		}else {
			return super.cadastrar();
		}
		
	}	

	/**
	 * Método usado para retornar todos os grupos de ítens de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/GrupoItemAvaliacao/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public Collection<GrupoItemAvaliacao> getAllMonitoria() {

		try {
			GenericDAO dao = getDAO(ProjetoMonitoriaDao.class);
			Collection<GrupoItemAvaliacao> all = dao.findAll(GrupoItemAvaliacao.class);
			Collection<GrupoItemAvaliacao> allMonitoria = new ArrayList<GrupoItemAvaliacao>();
			for(GrupoItemAvaliacao grupo : all)
				if(grupo.getTipo() == 'P' || grupo.getTipo() == 'R')
					allMonitoria.add(grupo);
			return allMonitoria;
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro Grupos de Avaliação");
			return null;

		}
	}

	
	/**
	 * Método usado para retornar todos os grupos de ítens de monitoria na forma de SelectItem
	 * para que possa ser usado no ComboBox
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ItemAvaliacaoMonitoria/form.jsp</li>
	 *  <li>sigaa.war/monitoria/ItemAvaliacaoMonitoria/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<SelectItem> getAllComboMonitoria() throws ArqException {
		Collection<GrupoItemAvaliacao> grupos = getAll();
		Collection<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		for(GrupoItemAvaliacao grupo : grupos) {
			if(grupo.getTipo() == 'P' || grupo.getTipo() == 'R' ) { // P = Projeto de Monitoria
				SelectItem item = new SelectItem();                 // R = Relatório de Monitoria(Parcial ou Final) 
				item.setLabel(grupo.getDenominacao());
				item.setValue(grupo.getId());
				selectItems.add(item);
			}
		}
		
		if(selectItems.isEmpty())
			selectItems.add(new SelectItem("0", ">> NENHUM GRUPO ENCONTRADO <<"));
		
		return selectItems;
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}


	@Override
	public String forwardCadastrar() {
		return ConstantesNavegacaoMonitoria.CADASTRARGRUPOITEMAVALIACAO_LISTA;
	}
	
	/**
	 * Usado para redirecionar para tela de cadastro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarCadastroGrupo() {		
		return forward(ConstantesNavegacaoMonitoria.CADASTRARGRUPOITEMAVALIACAO_FORM);
	}

}
