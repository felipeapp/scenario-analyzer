/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/24
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;

import org.springframework.dao.DataIntegrityViolationException;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;

/**
 * Processador para cadastro de usu�rios
 * para um coordenador de p�lo.
 *
 * @author David Pereira
 *
 */
public class ProcessadorCadastroUsuarioCoordPolo extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoUsuarioCoordPolo uMov = (MovimentoUsuarioCoordPolo) mov;
		Usuario usr = uMov.getUsuario();
		CoordenacaoPolo coordenador = uMov.getCoordenador();

		// Pessoa do Tutor
		usr.setPessoa(coordenador.getPessoa());
		// Senha inicial � o CPF
		usr.setSenha(coordenador.getPessoa().getCpfCnpjFormatado().replaceAll("\\.", "").replaceAll("-", ""));
		// Unidade do curso
		usr.setUnidade(new Unidade(UnidadeGeral.SEDIS));
		usr.setTipo(new TipoUsuario(TipoUsuario.TIPO_OUTROS));

				
		try {
			if (getDAO(UsuarioDao.class, mov).findByCoordenadorPolo(coordenador) != null)
				throw new NegocioException("O coordenador de p�lo selecionado j� possui usu�rio cadastrado.");

			UsuarioGeralDAO dao = new UsuarioGeralDAO(Database.getInstance().getSipacDs(), Database.getInstance().getSigaaDs(), Database.getInstance().getComumDs());
			dao.cadastrarUsuario(usr, mov.getUsuarioLogado(), coordenador.getPessoa().getId());
			UserAutenticacao.atualizaSenhaAtual(uMov.getRequest(), usr.getId(), null, usr.getSenha());
			
			getGenericDAO(mov).updateField(CoordenacaoPolo.class, coordenador.getId(), "usuario", usr.getId());
			
			Mail.sendMessage("SIGAA", usr.getEmail(),
					"Cadastro de usu�rio no SIGAA",
					"Caro tutor,<br/><br/>" +
					"Voc� foi cadastrado no SIGAA - Sistema Integrado de Gest�o de Atividades Acad�micas " +
					"para que possa exercer as suas atividades de coordenador de p�lo no referido sistema. <br/><br/>" +
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

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
