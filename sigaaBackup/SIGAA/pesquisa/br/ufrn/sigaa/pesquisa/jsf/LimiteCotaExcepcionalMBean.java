package br.ufrn.sigaa.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.PlanoTrabalhoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.mensagens.MensagensPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LimiteCotaExcepcional;

/**
 * Gerado pelo CrudBuilder
 * Controlador respons�vel por gerenciar os limites de quantidades de cotas excepcionais
 * atribu�dos para alguns docentes.
 */
@Component
@Scope("request")
public class LimiteCotaExcepcionalMBean extends
		SigaaAbstractController<LimiteCotaExcepcional> {

	/**
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	public LimiteCotaExcepcionalMBean() {
		obj = new LimiteCotaExcepcional();
	}

	/**
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#doValidate()
	 */
	@Override
	protected void doValidate() throws ArqException {
		LimiteCotaExcepcional limite = getDAO(PlanoTrabalhoDao.class).findLimiteCotaExcepcionalByServidor(obj.getServidor());
		if(limite != null && limite.getId() != obj.getId())
			erros.addMensagem(MensagensPesquisa.LIMITE_JA_ATRIBUIDO_DOCENTE);
	}
	
	/**
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeInativar()
	 */
	protected void beforeInativar(){
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	/**
	 * M�todo n�o invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	@Override
	/**
	 * JSP: M�todo n�o invocado por JSP.
	 */
	public String getFormPage() {
		return "/pesquisa/LimiteCotaExcepcional/form.jsf";
	}

	@Override
	/**
	 * JSP: M�todo n�o invocado por JSP.
	 */
	public String getListPage() {
		return "/pesquisa/LimiteCotaExcepcional/lista.jsf";
	}

}
