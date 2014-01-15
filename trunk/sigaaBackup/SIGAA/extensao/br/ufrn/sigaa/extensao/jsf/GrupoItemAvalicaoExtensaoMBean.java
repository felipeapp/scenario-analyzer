/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/09/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

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
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.GrupoItemAvaliacao;

/**
 * MBean com responsabilidade de gerenciar o CRUD de grupo de ítens de Avaliação.   
 * 
 * 
 * @author UFRN
 *
 */
@Component("grupoItemAvaliacaoExtensao")
@Scope("session")
public class GrupoItemAvalicaoExtensaoMBean extends SigaaAbstractController<GrupoItemAvaliacao> {

	/**
	 * Construtor padrão.
	 */
	public GrupoItemAvalicaoExtensaoMBean() {
		obj = new GrupoItemAvaliacao();
	}
	

	/**
	 * Usado para o cadastro de grupo de ítem de avaliação.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/GrupoItemAvaliacao/form.jsp
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		ListaMensagens mensagens = new ListaMensagens();
		obj.validate();
		
		if(obj.getTipo() == '0') {
			mensagens.addErro("Grupo para Avaliação: Campo obrigatório não informado.");
		}
		
		if(super.getConfirmButton().equals("Cadastrar") || super.getConfirmButton().equals("Alterar")) {
			Collection<GrupoItemAvaliacao> grupos = getGenericDAO().findByExactField(GrupoItemAvaliacao.class, "tipo", 'E');
			for(GrupoItemAvaliacao grupo : grupos) {
				if((obj.getId() == 0) &&
					(grupo.getDenominacao().equalsIgnoreCase(obj.getDenominacao()))) {
					mensagens.addErro("Não é possível cadastrar dois grupos com a mesma Descrição.");
					break;
				}
			}
		}
		
		if (!mensagens.isEmpty()) {
			addMensagens(mensagens);
			return null;
		}
		return super.cadastrar();		
	}

	/**
	 * Usado para retornar todos os grupos de ítens de extensão. 
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/GrupoItemAvaliacao/lista.jsp
	 * @return
	 */
	public Collection<GrupoItemAvaliacao> getAllExtensao() {

		try {
			GenericDAO dao = getDAO(ProjetoMonitoriaDao.class);
			Collection<GrupoItemAvaliacao> all = dao.findAll(GrupoItemAvaliacao.class);
			Collection<GrupoItemAvaliacao> allExtensao = new ArrayList<GrupoItemAvaliacao>();
			for(GrupoItemAvaliacao grupo : all)
				if(grupo.getTipo() == 'E')
					allExtensao.add(grupo);
			return allExtensao;			
		} catch (DAOException e) {
			notifyError(e);
			addMensagemErro("Erro Grupos de Avaliação");
			return null;

		}
	}
	
	public String iniciarCadastroGrupo() throws ArqException {
		obj = new GrupoItemAvaliacao();
		super.setConfirmButton("Cadastrar");
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(ConstantesNavegacao.CADASTRARGRUPOITEMAVALIACAO_FORM);
	}

	/**
	 * Usado para retornar todos os grupos de ítens de extensão na forma de SelectItens.
	 *
	 * Chamado por:
	 * sigaa.war/extensao/ItemAvaliacaoExtensao/form.jsp
	 * sigaa.war/extensao/ItemAvaliacaoExtensao/lista.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllComboExtensao() throws DAOException {
		Collection<GrupoItemAvaliacao> grupos = getGenericDAO().findByExactField(GrupoItemAvaliacao.class, "tipo", 'E');
		Collection<SelectItem> selectItems = new ArrayList<SelectItem>();
		
		for(GrupoItemAvaliacao grupo : grupos) {
			SelectItem item = new SelectItem();
			item.setLabel(grupo.getDenominacao());
			item.setValue(grupo.getId());
			selectItems.add(item);			
		}
		
		if(selectItems.isEmpty())
			selectItems.add(new SelectItem("0", ">> NENHUM GRUPO ENCONTRADO <<"));
		
		return selectItems;			
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	}


	
	@Override
	public String forwardCadastrar() {
		return ConstantesNavegacao.CADASTRARGRUPOITEMAVALIACAO_LISTA;
	}
	

}
