/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/10/2010
 *
 */
package br.ufrn.sigaa.diploma.jsf;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.diploma.dao.ResponsavelAssinaturaDiplomasDao;
import br.ufrn.sigaa.diploma.dominio.ResponsavelAssinaturaDiplomas;

/** Controller responsável pelo cadastros das pessoas que assinarão os diplomas.
 * @author Édipo Elder F. Melo
 *
 */
@Component("responsavelAssinaturaDiplomasBean")
@Scope("request")
public class ResponsavelAssinaturaDiplomasMBean extends SigaaAbstractController<ResponsavelAssinaturaDiplomas> {

	/**
	 * Cadastra os responsáveis pelas assinaturas nos diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/assinaturas/form.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		erros.addAll(obj.validate());
		if (hasErrors()) return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.RESPONSAVEIS_ASSINATURA_DIPLOMAS);
		execute(mov);
		return redirect(getListPage());
	}
	
	/**
	 * Inicia o cadastro dos responsáveis pelas assinaturas nos diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/assinaturas/lista.jsp</li>
	 * <li>/sigaa.war/diplomas/menus/registro.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		obj = new ResponsavelAssinaturaDiplomas();
		obj.setNivel(getNiveisHabilitados()[0]);
		prepareMovimento(SigaaListaComando.RESPONSAVEIS_ASSINATURA_DIPLOMAS);
		return super.preCadastrar();
	}
	
	/**
	 * Atualiza o cadastro dos responsáveis pelas assinaturas nos diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/assinaturas/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		prepareMovimento(SigaaListaComando.RESPONSAVEIS_ASSINATURA_DIPLOMAS);
		return super.atualizar();
	}
	
	/**
	 * Retorna o cadastro dos responsáveis atualmente por assinaturas nos diplomas.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ResponsavelAssinaturaDiplomas getResponsaveisAtual() throws HibernateException, DAOException {
		if (obj != null)
			return getDAO(ResponsavelAssinaturaDiplomasDao.class).findAtivo(obj.getNivel());
		else
			return null;
	}
	
	/** Ativa como atual, o cadastro dos responsáveis pelas assinaturas nos diplomas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/diplomas/assinaturas/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String ativar() throws SegurancaException, ArqException, NegocioException {
		int id = getParameterInt("id", 0);
		obj = new ResponsavelAssinaturaDiplomas();
		obj.setId(id);
		obj = getGenericDAO().refresh(obj);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		obj.setAtivo(true);
		prepareMovimento(SigaaListaComando.RESPONSAVEIS_ASSINATURA_DIPLOMAS);
		return cadastrar();
	}
	
	/** Verifica as permissões para alterar o cadastro de responsáveis por assinar os diplomas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_STRICTO, SigaaPapeis.GESTOR_LATO);
	}
	
	/** Construtor padrão. */
	public ResponsavelAssinaturaDiplomasMBean() {
		obj = new ResponsavelAssinaturaDiplomas();
	}
	
	/** Retorna o link do formulário de cadastro de responsáveis por assinar diplomas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/diplomas/assinaturas/form.jsf";
	}
	
	/** Retorna o link da listagem de responsáveis por assinar diplomas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/diplomas/assinaturas/lista.jsf";
	}
	
	/** Retorna uma coleção de responsáveis por assinar diplomas.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAll()
	 */
	@Override
	public Collection<ResponsavelAssinaturaDiplomas> getAll() throws ArqException {
		if (all == null) {
			all = getDAO(ResponsavelAssinaturaDiplomasDao.class).findAll(getNiveisHabilitados());
		}
		return all;
	}
	
}
