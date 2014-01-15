/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/24
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;

import org.springframework.dao.DataIntegrityViolationException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;

/**
 * Processador para cadastro de usu�rios
 * para um tutor.
 *
 * @author David Pereira
 *
 */
public class ProcessadorCadastroUsuarioTutor extends AbstractProcessador {

	/**
	 * M�todo respons�vel pela execu��o do processamento do cadastro do Tutor
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoUsuarioTutor uMov = (MovimentoUsuarioTutor) mov;
		Usuario usr = uMov.getUsuario();
		TutorOrientador tutor = uMov.getTutor();

		if (mov.getCodMovimento().getId() == SigaaListaComando.CADASTRAR_USUARIO_TUTOR.getId()) 
			cadastrarUsuario(mov, usr, tutor, uMov);
		if (mov.getCodMovimento().getId() == SigaaListaComando.REATIVAR_USUARIO_TUTOR.getId()) {
			reativarUsuario(mov, tutor);
		}
		return null;
	}

	/**
	 * Cadastro de um usu�rio para um Tutor
	 * 
	 * @param mov
	 * @param usr
	 * @param tutor
	 * @param uMov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void cadastrarUsuario(Movimento mov, Usuario usr, TutorOrientador tutor, MovimentoUsuarioTutor uMov) throws NegocioException, ArqException{

		// Pessoa do Tutor
		usr.setPessoa(tutor.getPessoa());
		// Senha inicial � o CPF
		usr.setSenha(tutor.getPessoa().getCpfCnpjFormatado().replaceAll("\\.", "").replaceAll("-", ""));
		// Unidade do curso
		usr.setUnidade(new Unidade(UnidadeGeral.SEDIS));
		usr.setTipo(new TipoUsuario(TipoUsuario.TIPO_OUTROS));
		usr.setTutor(tutor);
		usr.setIdTutor(tutor.getId());

		try {
			
			UsuarioGeralDAO dao = new UsuarioGeralDAO(Database.getInstance().getSipacDs(), Database.getInstance().getSigaaDs(), Database.getInstance().getComumDs());
			dao.cadastrarUsuario(usr, mov.getUsuarioLogado(), tutor.getPessoa().getId());
			UserAutenticacao.atualizaSenhaAtual(uMov.getRequest(), usr.getId(), null, usr.getSenha());
			
			Mail.sendMessage("SIGAA", usr.getEmail(),
					"Cadastro de usu�rio no SIGAA",
					"Caro tutor,<br/><br/>" +
					"Voc� foi cadastrado no SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas " +
					"para que possa exercer as suas atividades de tutor no referido sistema. <br/><br/>" +
					"O endere�o do SIGAA � " + RepositorioDadosInstitucionais.getLinkSigaa() + ".<br/>" +
					"O seu login � " + usr.getLogin() + ".<br/>" +
					"Sua senha � " + usr.getSenha() + ".<br/><br/>" +
					"A senha acima foi gerada automaticamente pelo sistema. Recomendamos " +
					"que voc� altere a sua senha quando entrar no sistema pela primeira vez.<br/><br/>" +
					"Atenciosamente,<br/>" +
					"Diretoria de Sistemas<br/>" +
					"Superintend�ncia de Inform�tica - UFRN");
		} catch(DataIntegrityViolationException e) {
			throw new NegocioException("J� existe um usu�rio com o login especificado.");
		}
	}
	
	
	/**
	 * Reativa��o do usu�rio do Tutor
	 * 
	 * @param mov
	 * @param tutor
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void reativarUsuario(Movimento mov, TutorOrientador tutor) throws DAOException, NegocioException {
		Usuario usuario = getDAO(UsuarioDao.class, mov).findByTuTor(tutor); 
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			if ( usuario.isInativo()) 
				dao.updateFields(Usuario.class, usuario.getId(), 
						new String [] {"inativo", "expiraSenha"}, 
						new Object [] {Boolean.FALSE, null});
			
			if (usuario != null && mov.getCodMovimento().getId() == SigaaListaComando.CADASTRAR_USUARIO_TUTOR.getId()){
				throw new NegocioException("O tutor selecionado j� possui usu�rio cadastrado, reative-o.");
			}else if (usuario == null && mov.getCodMovimento().getId() == SigaaListaComando.REATIVAR_USUARIO_TUTOR.getId()){ 
				throw new NegocioException("O tutor selecionado n�o possui usu�rio cadastrado.");
			}
		
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida��o das informa��es
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {}

}
