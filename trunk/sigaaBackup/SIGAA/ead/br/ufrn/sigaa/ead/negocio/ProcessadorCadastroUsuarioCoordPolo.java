/*
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Processador para cadastro de usuários
 * para um coordenador de pólo.
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
		// Senha inicial é o CPF
		usr.setSenha(coordenador.getPessoa().getCpfCnpjFormatado().replaceAll("\\.", "").replaceAll("-", ""));
		// Unidade do curso
		usr.setUnidade(new Unidade(UnidadeGeral.SEDIS));
		usr.setTipo(new TipoUsuario(TipoUsuario.TIPO_OUTROS));

				
		try {
			if (getDAO(UsuarioDao.class, mov).findByCoordenadorPolo(coordenador) != null)
				throw new NegocioException("O coordenador de pólo selecionado já possui usuário cadastrado.");

			UsuarioGeralDAO dao = new UsuarioGeralDAO(Database.getInstance().getSipacDs(), Database.getInstance().getSigaaDs(), Database.getInstance().getComumDs());
			dao.cadastrarUsuario(usr, mov.getUsuarioLogado(), coordenador.getPessoa().getId());
			UserAutenticacao.atualizaSenhaAtual(uMov.getRequest(), usr.getId(), null, usr.getSenha());
			
			getGenericDAO(mov).updateField(CoordenacaoPolo.class, coordenador.getId(), "usuario", usr.getId());
			
			Mail.sendMessage("SIGAA", usr.getEmail(),
					"Cadastro de usuário no SIGAA",
					"Caro tutor,<br/><br/>" +
					"Você foi cadastrado no SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas " +
					"para que possa exercer as suas atividades de coordenador de pólo no referido sistema. <br/><br/>" +
					"O endereço do SIGAA é " + RepositorioDadosInstitucionais.getLinkSigaa() + ".<br/>" +
					"O seu login é " + usr.getLogin() + ".<br/>" +
					"Sua senha é " + usr.getSenha() + ".<br/><br/>" +
					"A senha acima foi gerada automaticamente pelo sistema. Recomendamos " +
					"que você altere a sua senha quando entrar no sistema pela primeira vez.<br/><br/>" +
					"Atenciosamente,<br/>" +
					"Diretoria de Sistemas<br/>" +
					"Superintendência de Informática - UFRN");
		} catch(DataIntegrityViolationException e) {
			throw new NegocioException("Já existe um usuário com o login especificado.");
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
