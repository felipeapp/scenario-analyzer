/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/07/2009
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.espacofisico.RecursoEspacoFisicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.espacofisico.dominio.RecursoEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoRecursoEspacoFisico;

/**
 * MBean para gerenciar tipo de Recurso do Espaço Físico.
 * @author Jean Guerethes
 */

@Component("tipoRecursoEspacoFisicoMBean")
@Scope("request")
public class TipoRecursoEspacoFisicoMBean extends SigaaAbstractController<TipoRecursoEspacoFisico> {

	public final String JSP_FORM = "/infra_fisica/tipo_recurso_espaco_fisico/form.jsp";
	public final String JSP_LISTA = "/infra_fisica/tipo_recurso_espaco_fisico/lista.jsp";
	
	public TipoRecursoEspacoFisicoMBean() {
		obj = new TipoRecursoEspacoFisico();
	}

	public void iniciar() throws SegurancaException {
		forward(JSP_FORM);
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_INFRA_ESTRUTURA_FISICA);
	}
	
	/**
	 * Direciona para a página da Listagem.
	 */
	@Override
	public String getListPage() {
		return (JSP_LISTA);
	}
	
	/**
	 * Direciona para a página do formulário.
	 */
	@Override
	public String getFormPage() {
		return (JSP_FORM); 
	}
	
	/**
	 * Serve pra volta para a tela de listagem logo após a inativação.
	 */
	@Override
	protected String forwardInativar() {
		return getListPage();
	}
	
	/**
	 * Serve para direcionar para a tela de listagem.
	 */
	@Override
	public String listar() throws ArqException {
		return forward(getListPage());
	}
	
	/**
	 * Para Evitar que ocorra "Operação já processada" e para setar como operação ativa 
	 * como sendo a de cadastrar. 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		return super.atualizar();
	}
	
	/**
	 * Serve para trocar o campo ativo de true para false.
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		
		checkChangeRole();
		
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoRecursoEspacoFisico.class);
		if (obj == null) {
			addMensagemErro("O Tipo de Recurso já foi removido.");
		}else {
			setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
			prepareMovimento(ArqListaComando.DESATIVAR);
		}
		
		RecursoEspacoFisicoDao dao = getDAO(RecursoEspacoFisicoDao.class);
		Collection<RecursoEspacoFisico> membros = dao.findbyTipoRecurso(obj);
		
		if (membros != null && !membros.isEmpty()) {
			iniciar();
			addMensagemErro("Esse registro não pode ser removido, pois está associado a outro(s) " +
			"registro(s) da base de dados.");
			return forward(JSP_LISTA);
		}
		return super.inativar();
	}	
	
}
