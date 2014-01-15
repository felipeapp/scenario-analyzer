/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 22/09/2011
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.usuarios.UsuarioLDAP;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dao.UsuarioGeralDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.UsuarioFamiliar;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para realizar o auto-cadastro de familiares de discentes.
 *
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorAutoCadastroFamiliar extends AbstractProcessador {

	/**
	 * Executa o processador
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		validate(mov);
		MovimentoAutoCadastroDiscente cMov = (MovimentoAutoCadastroDiscente) mov;
		Usuario usr = cMov.getUsr();
		Usuario usrDiscente = (Usuario) cMov.getObjAuxiliar();

		DiscenteDao sigaaDiscenteDao = getDAO(DiscenteDao.class, mov);
		DataSource sipacDs = null;
		DataSource sigaaDs = null;
		DataSource comumDs = null;
		UsuarioDao usrDao = getDAO(UsuarioDao.class, mov);
		
		try {			
			
			if(Sistema.isSipacAtivo()) 			
				sipacDs = Database.getInstance().getSipacDs();
			
			sigaaDs = Database.getInstance().getSigaaDs();
			comumDs = Database.getInstance().getComumDs();			
			
			UsuarioGeralDAO dao = new UsuarioGeralDAO(sipacDs, sigaaDs, comumDs);

			Discente discente = sigaaDiscenteDao.findByMatricula(usrDiscente.getDiscente().getMatricula());
			
			if(discente != null && (discente.getStatus() != StatusDiscente.ATIVO 
					&& discente.getStatus() != StatusDiscente.ATIVO_DEPENDENCIA))
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois o aluno informado ainda n�o est� com matr�cula ativa no sistema.");
			
			Curso curso = null;

			if (discente.getCurso() != null && discente.getCurso().getId() != 0)
				curso = sigaaDiscenteDao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);

			if (curso == null)
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois o aluno voc� n�o est� associado a nenhum curso. Por favor, entre em contato com o suporte.");
			if (curso != null && curso.getUnidade() == null)
				throw new NegocioException("N�o foi poss�vel realizar o cadastro, pois n�o existe uma unidade associada ao curso do aluno informado. Por favor, entre em contato com o suporte.");
			
			Usuario usuario = new Usuario();
			boolean usuarioExistente = false;
			// verifica se o usu�rio j� existe
			Collection<UsuarioGeral> usrBd = usrDao.findByCpf(usr.getPessoa().getCpf_cnpj());
			if (ValidatorUtil.isNotEmpty(usrBd)){
				
				for (UsuarioGeral u : usrBd){
					
					if (u.getLogin().equals(usr.getLogin())){
						usuario = new Usuario(u.getId());
						usuarioExistente = true;
						break;
					}
					
				}
			
			} 
			
			// se o usu�rio j� existir confere os dados 
			if (usuarioExistente){
				if (ValidatorUtil.isEmpty(usuario))
					throw new NegocioException("J� existe um usu�rio no sistema com o CPF informado ou os dados informados n�o conferem.");
				
				if (!UserAutenticacao.autenticaUsuario(cMov.getRequest(), usr.getLogin(), usr.getSenha()))
					throw new NegocioException("Os dados do usu�rio informados n�o conferem com os dados j� cadastrados.");
				
			} else {
				
				// verifica se o login j� est� cadastrado, caso contr�rio cadastra o usu�rio
				if (dao.existeLogin(usr.getLogin()))
					throw new NegocioException("J� existe um usu�rio com o login especificado.");				
							
				usuario.setLogin(usr.getLogin());
				usuario.setSenha(usr.getSenha());
				usuario.setPessoa(usr.getPessoa());
				usuario.setDiscente(discente);
				usuario.setEmail(usr.getEmail());
				usuario.setAutorizado(new Boolean(true));
				usuario.setFuncionario(false);
				usuario.setInativo(new Boolean(false));
				usuario.setTipo(new TipoUsuario(TipoUsuario.TIPO_FAMILIAR));
				usuario.setUnidade(new Unidade(discente.getUnidade()));
				usuario.setDataCadastro(new Date());	
				
				// Altera o login para se adequar as regras dos sistemas
				padroniza(usuario);
				
				cadastrarUsuario(dao, usuario, cMov);
				
			}
			
			//cria a associa��o do usu�rio do familiar com o discente
			UsuarioFamiliar usuarioFamiliar = new UsuarioFamiliar();
			usuarioFamiliar.setUsuario(usuario);
			usuarioFamiliar.setDiscenteMedio(new DiscenteMedio(discente.getId()));
			sigaaDiscenteDao.create(usuarioFamiliar);
			
			return usuario;
		} catch (NegocioException ne) {
			throw new NegocioException(ne.getMessage());
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			sigaaDiscenteDao.close();
			usrDao.close();
		}		
	}
	
	/**
	 * Padroniza os dados do usu�rio
	 * @param usuario
	 * @throws NegocioException
	 */
	private void padroniza(UsuarioGeral usuario) throws NegocioException {
		usuario.setLogin(UserAutenticacao.validarLogin(usuario.getLogin()));
		usuario.setLogin(usuario.getLogin().trim());

		if (usuario.getEmail() != null)
			usuario.setEmail(usuario.getEmail().trim());

		if (usuario.getRamal() != null)
			usuario.setRamal(usuario.getRamal().trim());
	}	
	
	/**
	 * Cadastra o usu�rio informado
	 * @param dao
	 * @param usuario
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void cadastrarUsuario(UsuarioGeralDAO dao, UsuarioGeral usuario, MovimentoAutoCadastroDiscente mov) throws ArqException, NegocioException {
		dao.cadastrarUsuario(usuario, mov.getUsuarioLogado(), usuario.getPessoa().getId());

		UserAutenticacao.atualizaSenhaAtual(mov.getRequest(), usuario.getId(), null, usuario.getSenha());
		
		// adiciona no LDAP assincronamente
		new UsuarioLDAP(usuario.getLogin(),usuario.getSenha()).start();
	}	

	/**
	 * Valida os dados do usu�rio e do discente
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoAutoCadastroDiscente cMov = (MovimentoAutoCadastroDiscente) mov;
		Usuario usrDiscente = (Usuario) cMov.getObjAuxiliar();
		Usuario usuario = (Usuario) cMov.getUsr();

		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		UsuarioDao usrDao = getDAO(UsuarioDao.class, mov);

		ListaMensagens erros = new ListaMensagens();

		try {

			Discente discente = dao.findByMatricula(usrDiscente.getDiscente().getMatricula());

			if (discente == null)
				throw new NegocioException("N�o foi encontrado nenhum discente com a matr�cula informada.");
			
			if (discente.getNivel() != NivelEnsino.MEDIO)
				throw new NegocioException("Somente ser� poss�vel criar usu�rios para familiares de discente do n�vel m�dio.");
			
			List<UsuarioFamiliar> u = (List<UsuarioFamiliar>) dao.findByExactField(UsuarioFamiliar.class, "usuario.pessoa.cpf_cnpj", usuario.getPessoa().getCpf_cnpj());
			if (ValidatorUtil.isNotEmpty(u))
				throw new NegocioException("O Familiar informado j� possui cadastro no sistema.");			

			if (!StringUtils.compareInAscii(discente.getPessoa().getNome().toUpperCase().trim(), usrDiscente.getPessoa().getNome().toUpperCase().trim()))
				erros.addErro("Nome n�o confere com o registrado no sistema");
			
			if ( !cMov.isInternacional() ) {
				ValidatorUtil.validateCPF_CNPJ(usrDiscente.getPessoa().getCpf_cnpj(), "CPF", erros);
				
				if (!usrDiscente.getPessoa().getCpf_cnpj().equals(discente.getPessoa().getCpf_cnpj()))
					erros.addErro("CPF n�o confere com a registrada no sistema");
				
				if (isEmpty(usrDiscente.getPessoa().getIdentidade()) || isEmpty(usrDiscente.getPessoa().getIdentidade().getNumero())) {
					erros.addErro("Informe o n�mero da identidade.");
				} else if (!isEmpty(discente.getPessoa().getIdentidade()) && !isEmpty(discente.getPessoa().getIdentidade().getNumero())) {
					String identDigitada = StringUtils.extractLong(usrDiscente.getPessoa().getIdentidade().getNumero()).toString();
					String identDiscente = StringUtils.extractLong(discente.getPessoa().getIdentidade().getNumero()).toString();
					if (!identDigitada.equals(identDiscente)) {
						erros.addErro("Identidade n�o confere com a registrada no sistema");
					}
				}
			} else {
				validateRequired(usrDiscente.getPessoa().getPassaporte(), "Passaporte", erros);
				
				if (!usrDiscente.getPessoa().getPassaporte().equals(discente.getPessoa().getPassaporte())) {
					erros.addErro("Passaporte n�o confere com o registrado no sistema");
				}
			}

			if (!usrDiscente.getDiscente().getAnoIngresso().equals(discente.getAnoIngresso())) 
				erros.addErro("Ano de Ingresso inv�lido para este aluno");

			if (discente.getPessoa().getDataNascimento() != null) {
				if (CalendarUtils.compareTo(discente.getPessoa().getDataNascimento(), usrDiscente.getPessoa().getDataNascimento()) != 0)
					erros.addErro("Data de Nascimento n�o confere");
			} else {
				erros.addErro("Data de Nascimento n�o informada.");
			}

			usuario.setLogin(UserAutenticacao.validarLogin(usuario.getLogin()));

			if (!erros.isEmpty())
				throw new NegocioException(erros);

			if (!usuario.getSenha().equals(usuario.getConfirmaSenha()))
				throw new NegocioException("As senhas digitadas n�o est�o iguais.");

/*			UsuarioGeral usrBd = usrDao.findByLogin(usuario.getLogin(), false, false);
			if (usrBd != null)
				throw new NegocioException("J� existe um usu�rio no sistema com o login informado.");
			
			Collection<UsuarioGeral> usrs = usrDao.findByCpf(usuario.getPessoa().getCpf_cnpj());
			if (ValidatorUtil.isNotEmpty(usrs))
				throw new NegocioException("J� existe um usu�rio no sistema com o CPF informado.");		*/	

			if (usuario.getSenha() == null || "".equals(usuario.getSenha().trim()))
				throw new NegocioException("� necess�rio informar uma senha");

		} finally {
			dao.close();
			usrDao.close();
		}		
	}

}
