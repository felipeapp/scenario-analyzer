/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 02/04/2013 
 */
package br.ufrn.sigaa.prodocente.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.prodocente.dao.CVLattesDao;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;

/**
 * Controlador respons�vel por registrar as pessoas que podem ter as informa��es
 * dos seus curr�culos lattes obtidas automaticamente pelo sistema.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("pessoaLattesMBean") @Scope("request")
public class PessoaLattesMBean extends SigaaAbstractController<PessoaLattes> {

	public PessoaLattesMBean() {
	}
	
	/**
	 * Verifica se a pessoa do usu�rio logado j� possui registro na entidade
	 * PessoaLattes e o carrega. Caso contr�rio cria uma nova inst�ncia.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		CVLattesDao dao = getDAO(CVLattesDao.class);		
		
		if(getUsuarioLogado().getPessoa() != null)
			obj = dao.findByIdPessoa(getUsuarioLogado().getPessoa().getId());
		
		if(obj == null)
			obj = new PessoaLattes();
		
		prepareMovimento(ArqListaComando.CADASTRAR);
		
		return forward("/prodocente/producao/PessoaLattes/form.jsp");
	}
	
	/**
	 * Confirma a autoriza��o/desautoriza��o do acesso aos dados do curr�culo lattes.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/prodocente/producao/PessoaLattes/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String confirmar() throws NegocioException, ArqException {
		
		ValidatorUtil.validaInt(obj.getAnoReferencia(), "Ano de Refer�ncia", true, erros);
		
		if(hasErrors()){
			return null;
		}
		
		obj.setPessoa(getUsuarioLogado().getPessoa());
		obj.setServidor(getServidorUsuario());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		mov.setObjMovimentado(obj);
		
		execute(mov);
		
		addMensagemInformation("Autoriza��o realizada com sucesso.");
		
		cancelar();
		
		return forward(getSubSistema().getForward());
	}
}
