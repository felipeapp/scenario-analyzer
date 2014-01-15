/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/11/2006 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.web.jsf.AbstractPerfilPessoaMBean;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.arq.jsf.ConfirmaSenhaMBean;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Controlador do perfil do aluno no portal discente
 *
 * @author David Ricardo
 *
 */
public class PerfilDiscenteMBean extends AbstractPerfilPessoaMBean {

	@Override
	public PerfilPessoa getPerfilUsuario() {
		if (getUsuarioLogado().getDiscenteAtivo().getIdPerfil() == null) return null;
		return PerfilPessoaDAO.getDao().get(getUsuarioLogado().getDiscenteAtivo().getIdPerfil());
	}

	@Override
	public void setPerfilPortal(PerfilPessoa perfil) {
		getUsuarioLogado().getDiscenteAtivo().setIdPerfil(perfil.getId());
		PortalDiscenteMBean portal = (PortalDiscenteMBean) getMBean("portalDiscente");
		portal.setPerfil(perfil);
	}

	@Override
	public void setTipoPerfil(PerfilPessoa perfil) {
		perfil.setIdDiscente(getUsuarioLogado().getDiscenteAtivo().getId());
	}

	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentSession().getAttribute("usuario");
	}

	@Override
	public String cadastrar() throws ArqException, NegocioException {
		if (!confirmaSenha())
			return null;
		return super.cadastrar();
	}
	
	/**
	 * Verifica se os dados do usuário bate com do usuário logado.
	 * Deve ser chamado em casos de uso que precisam de confirmação dos dados pessoais após a sua execução
	 * @return
	 * @throws ArqException 
	 */
	public boolean confirmaSenha() throws ArqException{

		ConfirmaSenhaMBean confirma = (ConfirmaSenhaMBean) getMBean("confirmaSenha");
		if( confirma == null )
			return false;
		String msgDadosIncorretos = "Dados Incorretos, a operação não foi realizada.";
		String param = getCurrentRequest().getParameter("apenasSenha");
		confirma.setApenasSenha( Boolean.valueOf(param) ) ;

		if( isEmpty( confirma.getSenha() ) ) {
			if( confirma.isApenasSenha() )
				addMensagemErro("Informe a Senha");
			else
				addMensagemErro("Informe os dados de confirmação.");
			return false;
		}

		if( !confirma.isApenasSenha() ){
			if( confirma.isShowDataNascimento() && confirma.getDataNascimento() == null ){
				addMensagemErro("Informe a Data de Nascimento");
				return false;
			}

			if( confirma.isShowIdentidade() && confirma.getIdentidade().length() <= 0 ){
				addMensagemErro("Informe a Identidade");
				return false;
			}

			if ( confirma.isShowIdentidade() && getUsuarioLogado().getPessoa().getIdentidade() == null ) {
				addMensagemErro("A identidade não está informada em seu cadastro. Procure a coordenação de curso para atualização");
			}

			if ( confirma.isShowDataNascimento() && getUsuarioLogado().getPessoa().getDataNascimento() == null ) {
				addMensagemErro("A data de nascimento não está informada em seu cadastro. Procure a coordenação de curso para atualização");
			}

			if( confirma.isShowIdentidade() &&
					getUsuarioLogado().getPessoa().getIdentidade() != null &&
					!confirma.getIdentidade().equals( getUsuarioLogado().getPessoa().getIdentidade().getNumero() ) ){
				addMensagemErro(msgDadosIncorretos);
				return false;
			}

			if( confirma.isShowDataNascimento() && getUsuarioLogado().getPessoa().getDataNascimento() != null && confirma.getDataNascimento().compareTo( getUsuarioLogado().getPessoa().getDataNascimento() ) != 0 ){
				addMensagemErro(msgDadosIncorretos);
				return false;
			}

		}else{
			msgDadosIncorretos = "Senha Incorreta, a operação não foi realizada.";
		}


		if( !UserAutenticacao.autenticaUsuario(getCurrentRequest(), getUsuarioLogado().getLogin(), confirma.getSenha()) ){
			addMensagemErro(msgDadosIncorretos);
			return false;
		}

		return true;

	}
	
}
