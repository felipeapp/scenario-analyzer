/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.jsf.BuscaUsuarioBibliotecaMBean;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p>Responsável por redirencionar para a página onde será mostra o campo para o usuário enviar a mensagem</p>
 * 
 * @author jadson
 *
 */
@Component("enviaMensagemUsuariosBibliotecaMBean")
@Scope("request")
public class EnviaMensagemUsuariosBibliotecaMBean extends SigaaAbstractController <ReservaMaterialBiblioteca> implements PesquisarUsuarioBiblioteca{

	
	public static final String FORM_MENSAGEM_INDIVIDUAL_USUARIO = "/biblioteca/circulacao/formMensagemIndividualUsuario.jsp";
	
	/** A pessoa para que a mensagem vai ser enviada */
	private Pessoa p;
	
	/** O usuário da pessoa selecionada, contém o login utilizado para o envio de mensagens pelo sistema */
	private Usuario usuarioSelecionado;
	
	public String iniciarEnvioIndividual() throws SegurancaException, DAOException{
		BuscaUsuarioBibliotecaMBean pBean = getMBean("buscaUsuarioBibliotecaMBean");
		return pBean.iniciar(this, true, false, false, false, "Enviar Mensagem", null);
	}
	
	/**
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#selecionouUsuarioBuscaPadrao()
	 */
	@Override
	public String selecionouUsuarioBuscaPadrao() throws ArqException {
		UsuarioBibliotecaDao daoUsuario = null;
		
		try {
			daoUsuario = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class);
			this.usuarioSelecionado = daoUsuario.findUsuarioMaisRecenteAtivoByPessoa( p.getId());
			
			if(usuarioSelecionado == null || StringUtils.isEmpty( usuarioSelecionado.getLogin() ) ){
				addMensagemErro("Não é possível enviar mensagem para o usuário selecionado");
				return null;
			}
			
			return forward(FORM_MENSAGEM_INDIVIDUAL_USUARIO);
			
		} finally {
			if (daoUsuario != null) daoUsuario.close();
		}
	}

	
	/**
	 * 
	 * Volta para a busca do usuário
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/circulacao/formMensagemIndividualUsuario.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltaTelaBusca(){
		return ((BuscaUsuarioBibliotecaMBean) getMBean("buscaUsuarioBibliotecaMBean")).telaBuscaUsuarioBiblioteca();
	}
	
	/**
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setBibliotecaBuscaPadrao(br.ufrn.sigaa.biblioteca.dominio.Biblioteca)
	 */
	@Override
	public void setBibliotecaBuscaPadrao(Biblioteca biblioteca) {
	}

	/**
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setParametrosExtra(boolean, java.lang.String[])
	 */
	@Override
	public void setParametrosExtra(boolean parametroDePessoa,String... parametros) {
		
	}

	/**
	 * @see br.ufrn.sigaa.biblioteca.circulacao.jsf.PesquisarUsuarioBiblioteca#setPessoaBuscaPadrao(br.ufrn.sigaa.pessoa.dominio.Pessoa)
	 */
	@Override
	public void setPessoaBuscaPadrao(Pessoa p) {
		this.p = p;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}
	
}
