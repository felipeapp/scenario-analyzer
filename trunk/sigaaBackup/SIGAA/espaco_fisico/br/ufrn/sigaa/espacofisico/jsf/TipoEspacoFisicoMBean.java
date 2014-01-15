/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/07/2009
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;
import br.ufrn.sigaa.arq.dao.espacofisico.TipoEspacoDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoEspacoFisico;

/**
 * MBean para gerenciar tipo de Recurso do Espaço Físico.
 * @author Jean Guerethes
 */
@Component("tipoEspacoFisicoMBean")
@Scope("request")
public class TipoEspacoFisicoMBean extends SigaaAbstractController<TipoEspacoFisico> {
	
	public final String JSP_FORM = "/infra_fisica/tipo_espaco_fisico/form.jsp";
	public final String JSP_LISTA = "/infra_fisica/tipo_espaco_fisico/lista.jsp";
	
	public TipoEspacoFisicoMBean() {
		obj = new TipoEspacoFisico();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_INFRA_ESTRUTURA_FISICA);
	}
	
	/**
	 * Para redirecionar para a tela de busca.
	 */
	@Override
	public String getListPage() {
		return JSP_LISTA;
	}

	@Override
	public String getFormPage() {
		return JSP_FORM;
	}
	
	/**
	 * Antes de atualizar verifica-se se o usuário possui a permissão necessária para 
	 * tal operação. 
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		return super.atualizar();
	}
	
	
	/**
	 * Serve para ir para a tela da listagem 
	 * 
	 * JSP: menu.jsp
     */
	@Override
	public String listar() throws ArqException {
		return forward(JSP_LISTA);
	}

	/**
	 * Serve para redirecionar o usuário de volta para a página da listagem. 
	 */
	@Override
	protected String forwardInativar() {
		return getListPage();
	}
	
	/**
	 * Serve para passar o campo ativo para false, o que evitará a exclusão total do registro do 
	 * banco.
	 * 
	 *  JSP: lista.jsp
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		
		checkChangeRole();
		
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoEspacoFisico.class);
		
		if (obj == null) {
			addMensagemErro("O Espaço Físico já foi removido.");
		}
		
		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class);
		Collection<EspacoFisico> membros = dao.findbyEquipe(obj);

		if (membros != null && !membros.isEmpty()) {
			addMensagemErro("Esse registro não pode ser removido, pois está associado a outro(s) " +
					"registro(s) da base de dados.");
			return null;
		}
		
		prepareMovimento(ArqListaComando.DESATIVAR);
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		
		return super.inativar();
	}
	

	/**
	 * Para evitar que ocorra erro na hora de cadastrar um tipo de espaço Físico.
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		obj.setDenominacao(obj.getDenominacao().toUpperCase());
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoEspacoFisico> mesmoNome = dao.findByExactField(
					TipoEspacoFisico.class, "denominacao", obj.getDenominacao());
		
		for (TipoEspacoFisico tef : mesmoNome) {
			if (tef.getId() == obj.getId()) {
				prepareMovimento(ArqListaComando.CADASTRAR);
				super.cadastrar();
			}else if (tef.isAtivo()) {
				addMensagemErro("Já existe um Tipo de Espaço Físico cadastrado com essa Denominação.");
			}
		}
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	/**
	 * Para setar o campo ativo pra true, evitando assim que ao atualizar ele não mais apareça. 
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
		obj.setAtivo(true);
		obj.setDenominacao(obj.getDenominacao().toUpperCase());
		super.beforeCadastrarAfterValidate();
	}
	
	/**
	 * Serve para redirecionar o usuário de volta para a página da listagem.
	 */
	@Override
	protected void afterInativar() {
		forward(JSP_LISTA);
		super.afterInativar();
	}
	
	public Collection<TipoEspacoFisico> getAllOrdenado() throws DAOException{
		TipoEspacoDAO dao = getDAO(TipoEspacoDAO.class);
		return	dao.findAtivos();
	}

}
