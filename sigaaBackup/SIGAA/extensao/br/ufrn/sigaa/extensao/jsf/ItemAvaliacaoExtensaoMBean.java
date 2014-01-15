/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/09/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;

/**
 * MBean responsável pelo gerenciamento do CRUD de ítens associados a grupos. 
 * 
 * @author UFRN
 *
 */
@Component("itemAvaliacaoExtensao")
@Scope("session")
public class ItemAvaliacaoExtensaoMBean extends SigaaAbstractController<ItemAvaliacaoMonitoria> {

	private Collection<ItemAvaliacaoMonitoria> itens;

	/**
	 * Construtor padrão.
	 */
	public ItemAvaliacaoExtensaoMBean() {
		obj = new ItemAvaliacaoMonitoria();
	}

	/**
	 * Método chamado antes do cadastro de um ítem de avaliação.
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public void beforeCadastrar() throws DAOException, NegocioException {
		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
		double notaTotal = dao.findNotaTotalAtivos();		
		if (notaTotal > 10.0) {
			throw new NegocioException("A soma das notas dos itens ativos não pode ultrapassar 10");
		}		
	}

	
	/**
	 * Método usado para preencher colecao de ítens de grupos de extensão.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/ItemAvaliacaoExtensao/lista.jsp
	 * 
	 * @return
	 */
	public String filtrarPorGrupo() {

		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
		try {
			itens = dao.findByGrupoExtensao(obj.getGrupo());
		} catch (DAOException e) {
			addMensagemErro(e.getMessage());
		}
		return null;
	}

	/**
	 * Método usado para retornar ítens de grupos de extensão.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/ItemAvaliacaoExtensao/lista.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public Collection<ItemAvaliacaoMonitoria> getItens() throws ArqException {
		if ( itens == null ) {
			ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
			try {
				itens = dao.findByGrupoExtensao(obj.getGrupo());
			} catch (DAOException e) {
				addMensagemErro(e.getMessage());
			}
		}
		return itens;	
	}

	public void setItens(Collection<ItemAvaliacaoMonitoria> itens) {
		this.itens = itens;
	}

	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
	}

	
	@Override
	public String forwardCadastrar() {
		return ConstantesNavegacao.CADASTRARITEMAVALIACAO_LISTA;
	}
	
	
	public String getDirBase() {
		return "/extensao/ItemAvaliacaoExtensao";
	}
	
	
	/**
	 * Usado para redirecionar para tela de cadastro.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/menu.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarCadastroItem() throws ArqException {
		super.setConfirmButton("Cadastrar");
		obj = new ItemAvaliacaoMonitoria();
		obj.setNotaMaxima(10.0); //definindo nota máxima por default
		prepareMovimento(ArqListaComando.CADASTRAR);
		return forward(ConstantesNavegacao.CADASTRARITEMAVALIACAO_FORM);
	}
	
	/*
	 * Usado para cadastrar um item de avaliação.
	 * 
	 * Chamado por:
	 * sigaa.war/extensao/ItemAvaliacaoExtensao/form.jsp
	 * 
	 * (non-Javadoc)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		ListaMensagens listaErros = new ListaMensagens();
		
		
		listaErros = obj.validate();
		
		if(obj.getGrupo().getId() == 0) {
			listaErros.addErro("Grupo: Campo obrigatório não informado.");			
		}		
		
		if( obj.getNotaMaxima() < 0 || obj.getNotaMaxima() > 10 )
			listaErros.addErro("A nota de um ítem deve ser maior que zero e menor que dez.");
		
		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
		
		
		if(super.getConfirmButton().equals("Cadastrar") || super.getConfirmButton().equals("Alterar")) {
		    Collection<ItemAvaliacaoMonitoria> itensDeUmGrupo = dao.findByGrupoExtensao(obj.getGrupo());
		    for(ItemAvaliacaoMonitoria item : itensDeUmGrupo) {
			if((obj.getId() == 0) && 
				item.getDescricao().equalsIgnoreCase(obj.getDescricao()) && 
				item.getGrupo().getId() == obj.getGrupo().getId()) {
			    listaErros.addErro("Não é possível cadastrar dois ítens com a mesma Descrição no mesmo grupo.");
			    break;
			}
		    }
		}
		
		if(!listaErros.isEmpty()) {
			addMensagens(listaErros);
			return null;
		}
		
		return super.cadastrar();
	}
	
}
