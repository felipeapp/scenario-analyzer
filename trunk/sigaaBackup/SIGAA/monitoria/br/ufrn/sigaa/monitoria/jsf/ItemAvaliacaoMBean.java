/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.monitoria.dominio.ItemAvaliacaoMonitoria;

/**
 * MBean responsável pelo gerenciamento de ítens associados a grupos.
 * 
 * @author UFRN
 *
 */
@Component("itemAvaliacaoMonitoria")
@Scope("session")
public class ItemAvaliacaoMBean extends SigaaAbstractController<ItemAvaliacaoMonitoria> {

	/**
	 * serial version ItemAvaliacaiMBean
	 */
	private static final long serialVersionUID = 1L;
	private Collection<ItemAvaliacaoMonitoria> itens;

	/**
	 * Construtor padrão
	 */
	public ItemAvaliacaoMBean() {
		obj = new ItemAvaliacaoMonitoria();
	}

	/**
	 * Método chamado antes de cadastrar um ítem.
	 * 
	 * Não é chamado por JSPs.
	 * 
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
	 * Usado para fazer uma busca de ítens associados a um grupo específico.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ItemAvaliacaoMonitoria/lista.jsp</li>
	 *  <li>sigaa.war/extensao/ItemAvaliacaoExtensao/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String filtrarPorGrupo() {

		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
		try {
			itens = dao.findByGrupoMonitoria(obj.getGrupo());
		} catch (DAOException e) {
			addMensagemErro(e.getMessage());
		}
		return null;
	}

	
	/**
	 * 
	 * Usado para preencher a coleção de ítens com grupos de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/ItemAvaliacaoMonitoria/lista.jsp</li>
	 * </ul
	 * @return
	 * @throws ArqException
	 */
	public Collection<ItemAvaliacaoMonitoria> getItens() throws ArqException {
		if ( itens == null ) {
			ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class);
			try {
				itens = dao.findByGrupoMonitoria(obj.getGrupo());
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
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
	}

	
	@Override
	public String forwardCadastrar() {
		return ConstantesNavegacaoMonitoria.CADASTRARITEMAVALIACAO_LISTA;
	}
	
}
