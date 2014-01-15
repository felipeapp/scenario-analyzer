/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 29/11/2006
 *
 */
package br.ufrn.sigaa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Date;

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
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.comum.sincronizacao.SincronizadorUsuarios;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.PessoaDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.util.PessoaHelper;
import br.ufrn.sigaa.dominio.AutoCadastroDiscente;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para realizar o auto-cadastro de discentes.
 *
 * @author David Ricardo
 *
 */
public class ProcessadorAutoCadastroDiscente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		validate(mov);
		MovimentoAutoCadastroDiscente cMov = (MovimentoAutoCadastroDiscente) mov;
		Usuario usr = cMov.getUsr();

		DiscenteDao sigaaDiscenteDao = getDAO(DiscenteDao.class, mov);		
		Connection conSipac = null;
		Connection conComum = null;

		try {
			
			if(Sistema.isAdministrativoAtivo()) {			
				conSipac = Database.getInstance().getConnection(Sistema.getSistemaAdministrativoAtivo());
			}
			conComum = Database.getInstance().getComumConnection();

			Discente discente = sigaaDiscenteDao.findByMatricula(usr.getDiscente().getMatricula());			
			
			
			
			Curso curso = null;

			if (discente.getCurso() != null && discente.getCurso().getId() != 0)
				curso = sigaaDiscenteDao.findByPrimaryKey(discente.getCurso().getId(), Curso.class);

			if (curso == null && discente.getTipo() != Discente.ESPECIAL)
				throw new NegocioException("Não foi possível realizar o cadastro pois você não está associado a nenhum curso. Por favor, entre em contato com o suporte.");
			if (curso != null && curso.getUnidade() == null)
				throw new NegocioException("Não foi possível realizar o cadastro pois não existe uma unidade associada ao seu curso. Por favor, entre em contato com o suporte.");

			// 
			verificarPessoasComMesmoCPF(cMov, usr, discente);

			Pessoa pessoaDiscente = discente.getPessoa();
			
			int idPessoaSipac = -1;

			SincronizacaoUsuarioSipac sincronizador = new SincronizacaoUsuarioSipac();
			
			// Busca pessoa no sipac, se não existir cadastra
			if(Sistema.isAdministrativoAtivo()) {
				idPessoaSipac = sincronizador.buscaIdPessoaSipac(pessoaDiscente.getCpf_cnpj(), pessoaDiscente.getPassaporte(),conSipac);
			}
			
			int idPessoaComum = sincronizador.buscaIdPessoaSipac(pessoaDiscente.getCpf_cnpj(), pessoaDiscente.getPassaporte(),conComum);
			
			int idPessoa = pessoaDiscente.getId();
			
			if ((idPessoaSipac == -1 && Sistema.isAdministrativoAtivo()) || idPessoaComum == -1) {
				int idPessoaTmp = SincronizadorPessoas.getNextIdPessoa();
				pessoaDiscente.setId(idPessoaTmp);
				
				if (idPessoaSipac == -1 && Sistema.isAdministrativoAtivo())
					idPessoaSipac = sincronizador.cadastroPessoa(pessoaDiscente, conSipac, true);
			
				if (idPessoaComum == -1)
					idPessoaComum = sincronizador.cadastroPessoa(pessoaDiscente, conComum, false);
			}
			
			pessoaDiscente.setId(idPessoa);

			AutoCadastroDiscente autoCadastro = new AutoCadastroDiscente();
			autoCadastro.setLogin(usr.getLogin());
			autoCadastro.setNome(usr.getPessoa().getNome());
			autoCadastro.setCpf(usr.getPessoa().getCpf_cnpj());
			autoCadastro.setPassaporte(usr.getPessoa().getPassaporte());
			autoCadastro.setIdentidade(usr.getPessoa().getIdentidade().getNumero());
			autoCadastro.setDataNascimento(usr.getPessoa().getDataNascimento());
			autoCadastro.setEmail(usr.getEmail());
			autoCadastro.setMatricula(usr.getDiscente().getMatricula());
			autoCadastro.setAnoIngresso(usr.getDiscente().getAnoIngresso());
			autoCadastro.setPeriodoIngresso(usr.getDiscente().getPeriodoIngresso() != null ? usr.getDiscente().getPeriodoIngresso() : 0 );
			autoCadastro.setDataHora(new Date());
			autoCadastro.setNivel(usr.getDiscente().getNivel());
			autoCadastro.setIp(cMov.getIp());

			sigaaDiscenteDao.create(autoCadastro);

			Usuario usuario = new Usuario();
			usuario.setLogin(usr.getLogin());
			usuario.setSenha(usr.getSenha());
			usuario.setPessoa(pessoaDiscente);
			usuario.setDiscente(discente);
			usuario.setEmail(usr.getEmail());
			usuario.setAutorizado(new Boolean(true));
			usuario.setFuncionario(false);
			usuario.setInativo(new Boolean(false));
			usuario.setTipo(new TipoUsuario(TipoUsuario.TIPO_ALUNO));
			if (discente.getTipo() == Discente.ESPECIAL && discente.getNivel() == NivelEnsino.GRADUACAO)
				usuario.setUnidade(new Unidade(UnidadeGeral.UNIDADE_ACADEMICA_ALUNO_ESPECIAL));
			else if (discente.getTipo() == Discente.ESPECIAL && NivelEnsino.isAlgumNivelStricto(discente.getNivel()))
				usuario.setUnidade(discente.getGestoraAcademica());
			else
				usuario.setUnidade(curso.getUnidade());
			usuario.setDataCadastro(new Date());

			// Pega próximo id no BANCO COMUM
			int idUsuario = SincronizadorUsuarios.getNextIdUsuario();
			usuario.setId(idUsuario);

			// Cadastra no Sigaa
			sigaaDiscenteDao.create(usuario);

			//Cadastra no Sipac caso esteja ativo
			if(Sistema.isAdministrativoAtivo()){							
				usuario.getPessoa().setId(idPessoaSipac);
				sincronizador.cadastroUsuario(usuario, conSipac);
				conSipac.close();
			}
			
			//Cadastra no BANCO COMUM
			usuario.getPessoa().setId(idPessoaComum);
			sincronizador.cadastroUsuario(usuario, conComum);

			conComum.close();
			
			UserAutenticacao.atualizaSenhaAtual(cMov.getRequest(), usuario.getId(), null, usuario.getSenha());
			
			// adiciona no LDAP assincronamente
			new UsuarioLDAP(usuario.getLogin(),usuario.getSenha()).start();
			
			return usuario;
		} catch (NegocioException ne) {
			throw new NegocioException(ne.getMessage());
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			sigaaDiscenteDao.close();
			Database.getInstance().close(conSipac);
			Database.getInstance().close(conComum);
		}

	}

	/**
	 * verifica se o discente tem outra pessoa oriunda do SIPAC consulta o banco
	 * do SIGAA, tabela comum.pessoa, para fazer esta verificação. Caso
	 * encontre, esta outra pessoa deve ser invalidada.
	 * 
	 * @param cMov
	 * @param usr
	 * @param discente
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private void verificarPessoasComMesmoCPF(MovimentoAutoCadastroDiscente cMov, Usuario usr, Discente discente) throws DAOException, NegocioException {
		
		PessoaDao pDao = getDAO(PessoaDao.class, cMov);
		
		try {
			Collection<Pessoa> pessoas = null;
			
			if ( isNotEmpty( usr.getPessoa().getCpf_cnpj()) )
				pessoas = pDao.findByCpfCnpj(usr.getPessoa().getCpf_cnpj());
			
			Pessoa outraPessoa = null;
			
			if (isNotEmpty(pessoas)) {
				outraPessoa = pessoas.iterator().next();

				if (outraPessoa.getId() != discente.getPessoa().getId() && 
						( StringUtils.compareInAscii(discente.getPessoa().getNome(), outraPessoa.getNome()) || 
								(StringUtils.compareInAscii(discente.getPessoa().getNomeMae(), outraPessoa.getNomeMae()) && 
										discente.getPessoa().getDataNascimento().equals(outraPessoa.getDataNascimento()))) ) {
				
					outraPessoa.setCpf_cnpj(null);
					outraPessoa.setValido(false);
					outraPessoa.setEnderecoContato(null);
					outraPessoa.setUnidadeFederativa(null);
					outraPessoa.setMunicipio(null);
					outraPessoa.setTipoRaca(null);
					outraPessoa.setTipoEtnia(null);
					outraPessoa.setPais(null);
					outraPessoa.setIdentidade(null);
					outraPessoa.setEstadoCivil(null);
					outraPessoa.setTipoRedeEnsino(null);
					outraPessoa.setSexo('M');
					
					PessoaHelper.alteraCriaPessoa( outraPessoa, pDao, cMov.getRegistroAcessoPublico(), cMov.getCodMovimento().getId() );
					
					pDao.update(outraPessoa);
				}
			} 
		} finally {
			pDao.close();
		}
		

	}

	private boolean isAlunoPossuiSolicitacaoMatricula(Movimento mov, Discente discente) throws DAOException {
		SolicitacaoMatriculaDao matDao = getDAO(SolicitacaoMatriculaDao.class, mov);
		try {
			int count = matDao.countByDiscenteAnoPeriodo(discente, 
					discente.getAnoIngresso(), 
					discente.getPeriodoIngresso(), 
					SolicitacaoMatricula.CADASTRADA, 
					SolicitacaoMatricula.ORIENTADO, 
					SolicitacaoMatricula.SUBMETIDA,
					SolicitacaoMatricula.VISTO_EXPIRADO,
					SolicitacaoMatricula.SOLICITADA_COORDENADOR,
					SolicitacaoMatricula.VISTA);
			
			if (count == 0)
				return false;
			
			return true;			
		} finally {
			matDao.close();
		}
	}	
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoAutoCadastroDiscente cMov = (MovimentoAutoCadastroDiscente) mov;
		Usuario usr = cMov.getUsr();

		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		UsuarioDao usrDao = getDAO(UsuarioDao.class, mov);

		ListaMensagens erros = new ListaMensagens();

		try {

			Discente discente = dao.findByMatricula(usr.getDiscente().getMatricula());
			
			if (discente == null)
				throw new NegocioException("Não foi encontrado nenhum discente com a matrícula informada.");
				
			if (discente != null 
					&& discente.getStatus() == StatusDiscente.PENDENTE_CADASTRO
					&& !isAlunoPossuiSolicitacaoMatricula(cMov, discente) )
				throw new NegocioException("Não foi possível realizar o cadastro pois sua matrícula ainda não está ativa no sistema.");

			if (!StringUtils.compareInAscii(discente.getPessoa().getNome().toUpperCase().trim(), usr.getPessoa().getNome().toUpperCase().trim())) {
				erros.addErro("Nome não confere com o registrado no sistema");
			}
			
			if ( !cMov.isInternacional() ) {
				ValidatorUtil.validateCPF_CNPJ(usr.getPessoa().getCpf_cnpj(), "CPF", erros);
				
				if (!usr.getPessoa().getCpf_cnpj().equals(discente.getPessoa().getCpf_cnpj())) {
					erros.addErro("CPF não confere com a registrada no sistema");
				}
				
				if (isEmpty(usr.getPessoa().getIdentidade()) || isEmpty(usr.getPessoa().getIdentidade().getNumero())) {
					erros.addErro("Informe o número da identidade.");
				} else if (!isEmpty(discente.getPessoa().getIdentidade()) && !isEmpty(discente.getPessoa().getIdentidade().getNumero())) {
					String identDigitada = StringUtils.extractLong(usr.getPessoa().getIdentidade().getNumero()).toString();
					String identDiscente = StringUtils.extractLong(discente.getPessoa().getIdentidade().getNumero()).toString();
					if (!identDigitada.equals(identDiscente)) {
						erros.addErro("Identidade não confere com a registrada no sistema");
					}
				}
			} else {
				validateRequired(usr.getPessoa().getPassaporte(), "Passaporte", erros);
				
				if (!usr.getPessoa().getPassaporte().equals(discente.getPessoa().getPassaporte())) {
					erros.addErro("Passaporte não confere com o registrado no sistema");
				}
			}
			
			

			if (!usr.getDiscente().getAnoIngresso().equals(discente.getAnoIngresso())
					|| (usr.getDiscente().getPeriodoIngresso() != null && !usr.getDiscente().getPeriodoIngresso().equals(discente.getPeriodoIngresso()))) {
				erros.addErro("Ano Semestre inválido para este aluno");
			}

			if (discente.getPessoa().getDataNascimento() != null) {
				
				if (CalendarUtils.compareTo(discente.getPessoa().getDataNascimento(), usr.getPessoa().getDataNascimento()) != 0)
					erros.addErro("Data de Nascimento não confere");
			} else {
				erros.addErro("Data de Nascimento não informada.");
			}

			usr.setLogin(UserAutenticacao.validarLogin(usr.getLogin()));

			if (!erros.isEmpty())
				throw new NegocioException(erros);

			if (!usr.getSenha().equals(usr.getConfirmaSenha()))
				throw new NegocioException("As senhas digitadas não estão iguais.");

			UsuarioGeral usrBd = usrDao.findByLogin(usr.getLogin(), false, false);
			if (usrBd != null)
				throw new NegocioException("Já existe um usuário no sistema com o login informado.");

			UsuarioGeral usrDiscente = usrDao.findByDiscente(discente);
			if (usrDiscente != null)
				throw new NegocioException("O aluno informado já possui um usuário no sistema (login:" + usrDiscente.getLogin() + ")");

			if (usr.getSenha() == null || "".equals(usr.getSenha().trim()))
				throw new NegocioException("É necessário informar uma senha");

		} finally {
			dao.close();
			usrDao.close();
		}

	}

}