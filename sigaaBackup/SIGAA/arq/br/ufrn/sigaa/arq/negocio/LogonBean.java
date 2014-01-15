/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 14/04/2005
 *
 */
package br.ufrn.sigaa.arq.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.RegistroEntradaDAO;
import br.ufrn.arq.dao.RegistroEntradaImpl;
import br.ufrn.arq.dominio.AlteracaoRegistroEntrada;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.dominio.RegistroEntradaDevice;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dao.ServidorDAO;
import br.ufrn.comum.dominio.AlteracaoUsuario;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Permissao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.sincronizacao.SincronizadorRegistroEntrada;
import br.ufrn.comum.sincronizacao.SincronizadorUsuarios;
import br.ufrn.rh.dominio.Designacao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador que implementa a l�gica para Logon e Logoff
 *
 * @author Gleydson Lima
 * @author Marcos Alexandre
 *
 */
public class LogonBean extends AbstractProcessador {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		UsuarioMov userMov = (UsuarioMov) mov;

		if (userMov.getCodMovimento().getId() == ArqListaComando.LOGAR_COMO_COD ) {
			int[] administradores = { SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.ADMINISTRADOR_STRICTO, SigaaPapeis.GESTOR_LATO };
			checkRole(administradores, mov);
		}

		Usuario user = userMov.getUsuario();
		UsuarioDao dao = getDAO(UsuarioDao.class, mov);
		UsuarioDao daoUpdate = null;
		
		try {
			switch (mov.getCodMovimento().getId()) {
			case ArqListaComando.LOGOFF_COD:

				if (userMov.getUsuario() != null) {
					RegistroEntrada reg = dao.findByPrimaryKey(userMov.getUsuarioLogado().getRegistroEntrada().getId(), RegistroEntrada.class);
					reg.setDataSaida(new Date());
					dao.update(reg);
				}
				
				// remove usu�rio on-line
				UserOnlineMonitor.logoffUser(userMov.getUsuarioLogado().getLogin(), Sistema.SIGAA);

				return null;

			case ArqListaComando.LOGON_COD:
				
				if (user.getLogin() == null)
					throw new NegocioException("Informe o login do usu�rio");

				if (user.getSenha() == null && userMov.getPassaporte() == null)
					throw new NegocioException("Informe a senha do usu�rio");

				Usuario usuarioBD = (Usuario) dao.findByLogin(user.getLogin());
				
				
				if(usuarioBD == null)
					throw new NegocioException("Usu�rio e/ou senha inv�lidos");


				if (usuarioBD.getTipo().getId() == TipoUsuario.TIPO_CONSIGNATARIA)
					throw new NegocioException("Usu�rios de empresas consignat�rias n�o possuem acesso ao SIGAA");
				
				if (usuarioBD.getTipo().getId() == TipoUsuario.TIPO_PLANO_SAUDE)
					throw new NegocioException("Usu�rios de planos de sa�de n�o possuem acesso ao SIGAA");

				if (usuarioBD.getTipo().getId() == TipoUsuario.TIPO_COOPERACAO && usuarioBD.getPessoa() != null && !dao.hasVinculoSistema(usuarioBD.getPessoa().getId()))
					throw new NegocioException("Usu�rios com termo de coopera��o n�o possuem acesso ao SIGAA");
				

				// Popular dados de consultores
				if (usuarioBD.getConsultor() != null) {
					usuarioBD.getConsultor().getAreaConhecimentoCnpq();
				}

				boolean senhaConfere = false;
				
				if (!userMov.isWap()) {
					if(userMov.getPassaporte() == null && usuarioBD != null)
						senhaConfere = UserAutenticacao.autenticaUsuario(userMov.getRequest(), usuarioBD.getLogin().toLowerCase(), user.getSenha(), userMov.isAutenticarComHash());
				} else {
					senhaConfere = UserAutenticacao.autenticaUsuarioMobile(userMov.getRequest(), usuarioBD.getLogin().toLowerCase(), user.getSenha());
				}

				if (usuarioBD != null && (userMov.getPassaporte() != null || senhaConfere)) {

					inicializaUsuario(mov, usuarioBD);

					daoUpdate = getDAO(UsuarioDao.class, mov);

					daoUpdate.disableLog();

					// Recupera data do �ltimo acesso
					Date ultimoAcesso = usuarioBD.getUltimoAcesso();
					// Grava nova data do �ltimo acesso no banco/
					usuarioBD.setUltimoAcesso(new Date());

					dao.close();

					// Seta a data do �ltimo acesso para retornar para a camada
					// WEB
					usuarioBD.setUltimoAcesso(ultimoAcesso);

					mov.setUsuarioLogado(usuarioBD);

					LogProcessorDelegate.getInstance().writeMovimentoLog(mov);
					// limpaRegistroTentativas(usuarioBD);

					daoUpdate.detach(usuarioBD);

					RegistroEntrada registro = new RegistroEntrada();
					registro.setData(new Date());
					registro.setUsuario(usuarioBD);
					registro.setIP(userMov.getIP());
					registro.setIpInternoNat(userMov.getIpInternoNat());
					registro.setServer(userMov.getHost());
					registro.setCanal(userMov.getCanal());
					
					
					if (userMov.getUserAgent().length() > 300)
						registro.setUserAgent(userMov.getUserAgent().substring(0, 299));
					else
						registro.setUserAgent(userMov.getUserAgent());

					if ( userMov.getCanal() != null &&  ( userMov.getCanal().equals( RegistroEntrada.CANAL_WEB ) || userMov.getCanal().equals( RegistroEntrada.CANAL_WEB_MOBILE ) ) )  {
						registro.setResolucao(userMov.getResolucao());
					}
					
					if (userMov.getPassaporte() != null) {
						usuarioBD.setPassaporteLogon(userMov.getPassaporte());
						registro.setPassaporte(userMov.getPassaporte().getId());
					}

					// Verifica se a unidade do usu�rio � a unidade de lota��o do servidor
					if (ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.UTILIZA_UNIDADE_LOTACAO_COMO_UNIDADE_USUARIO)) {
						if (!isEmpty(usuarioBD.getIdServidor())) {
							Servidor servidor = getGenericDAO(mov).findByPrimaryKey(usuarioBD.getIdServidor(), Servidor.class);
							if (!isEmpty(servidor)) {
								if ( usuarioBD.getUnidade().getId() != servidor.getUnidade().getId() ) {
									int idUnidade = servidor.getUnidade().getId();
									
									// Verificar designa��es ativas se n�o batem com a unidade do usu�rio (exemplo, jbb). Caso sim, sai do if
									List<Designacao> designacoes = getDAO(ServidorDAO.class, mov).findDesignacoesAtivasByServidor(servidor.getId());
									if (!isEmpty(designacoes)) {
										for (Designacao designacao : designacoes) {
											if (designacao.getIdUnidade() != null && usuarioBD.getUnidade().getId() == designacao.getIdUnidade()) {
												idUnidade = designacao.getIdUnidade();
												break;
											}
										}
									}
									
									// Caso n�o ache:
									// deve mudar em todos os bancos e registrar altera��o;
									if (usuarioBD.getUnidade().getId() != idUnidade) {
										int idUnidadeAntiga = usuarioBD.getUnidade().getId();
										
										Unidade unidade = getGenericDAO(mov).findByPrimaryKey(idUnidade, Unidade.class);
										usuarioBD.setUnidade(unidade);
										
										AlteracaoUsuario alteracao = new AlteracaoUsuario();
										alteracao.setIdUsuario(usuarioBD.getId());
										alteracao.setIdUnidadeAntiga(idUnidadeAntiga);
										alteracao.setIdUnidadeNova(idUnidade);
										alteracao.setIdUsuarioAlteracao(mov.getUsuarioLogado().getId());
										getDAO(GenericSharedDBDao.class, mov, Sistema.COMUM).create(alteracao);
										
										SincronizadorUsuarios.usandoSistema(mov, Sistema.COMUM).mudarUnidade(usuarioBD.getId(), idUnidade); 
										SincronizadorUsuarios.usandoSistema(mov, Sistema.SIPAC).mudarUnidade(usuarioBD.getId(), idUnidade);
										SincronizadorUsuarios.usandoSistema(mov, Sistema.SIGAA).mudarUnidade(usuarioBD.getId(), idUnidade);
									}
								}
							}
						}
					}
					
					SincronizadorRegistroEntrada.usandoSistema(Sistema.SIGAA).cadastrarRegistroEntrada(registro);
					
					if(userMov.getCanal() != null &&  userMov.getCanal().equals(RegistroEntrada.CANAL_DEVICE)) {
						RegistroEntradaDevice registroDevice = new RegistroEntradaDevice();
                        registroDevice.setRegistroEntrada(registro);
                        registroDevice.setDeviceInfo(userMov.getResolucao());
                        
                        RegistroEntradaDAO registroEntradaDao = getDAO(RegistroEntradaImpl.class, userMov, Sistema.COMUM);
                        
                        try {
                        	registroEntradaDao.registrarDeviceInfo(registroDevice);
                        } finally {
                        	registroEntradaDao.close();
                        }
					}

					usuarioBD.setRegistroEntrada(registro);
					
					// insere como usu�rio on-line
					UserOnlineMonitor.logonUser(usuarioBD.getLogin(), registro.getId(), Sistema.SIGAA);

					return usuarioBD;

				} else {
					throw new NegocioException("Usu�rio e/ou senha inv�lidos");
				}
			
			case ArqListaComando.LOGAR_COMO_COD:
			case SigaaListaComando.LOGAR_COMO_TUTOR_COD:
			case SigaaListaComando.LOGAR_COMO_COORD_POLO_COD:
				validate(userMov);

				Usuario usuarioLogado = (Usuario) userMov.getUsuarioLogado();
				RegistroEntrada registro = usuarioLogado.getRegistroEntrada();
				Usuario usuario = dao.findByPrimaryKey(userMov.getUsuario().getId(), Usuario.class);

				inicializaUsuario(mov, usuario);

				// O registro de entrada � mantido no usu�rio
				usuario.setRegistroEntrada(registro);

				AlteracaoRegistroEntrada alteracao = new AlteracaoRegistroEntrada();
				alteracao.setUsuario(usuario);
				alteracao.setRegistroEntrada(registro);
				alteracao.setData(new Date());
				alteracao.setMotivo(userMov.getMotivo());

				dao.create(alteracao);

				return alteracao;

			default:
				throw new IllegalArgumentException("Comando inv�lido: " + mov.getCodMovimento());
			}
		
			
		} finally {
			dao.close();
			if (daoUpdate != null) {
				daoUpdate.close();
			}
		}

	}
	
	/**
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		UsuarioMov userMov = (UsuarioMov) mov;
		Usuario usuarioLogado = (Usuario) userMov.getUsuarioLogado();

		switch (mov.getCodMovimento().getId()) {
		case ArqListaComando.LOGAR_COMO_COD:
			GenericDAO genDAO = getGenericDAO(mov);
			try {
				RegistroEntrada registro = usuarioLogado.getRegistroEntrada();

				Usuario usuario = genDAO.findByPrimaryKey(userMov.getUsuario().getId(), Usuario.class);

				/*if (registro.getUsuario().getId() != usuarioLogado.getId())
					throw new SegurancaException("Registro de entrada inconsistente"); */

				registro = genDAO.findByPrimaryKey(registro.getId(), RegistroEntrada.class);

				if (registro.getUsuario().getId() != usuarioLogado.getId())
					throw new SegurancaException("Registro de inconsistente");
			} finally {
				genDAO.close();
			}

			break;

		default:
			break;
		}
	}

	/**
	 * Inicializa cole��es lazy do usu�rio. <br>
	 * Note que � importante que o DAO n�o tenha sido fechado.
	 *
	 * @param user
	 * @throws DAOException
	 */
	private void inicializaUsuario(Movimento mov, Usuario user) throws DAOException {
		PermissaoDAO dao = new PermissaoDAO();
		
		try {
			List<Permissao> permissoes = dao.findPermissaosAtivosByUsuario(user.getId(), new Date());
			user.setPermissoes(permissoes);

			HashSet<Papel> papeis = new HashSet<Papel>();
			for (Permissao p : user.getPermissoes())
				papeis.add(p.getPapel());
			user.setPapeis(papeis);

			if (user.getTutor() != null) {
				user.getTutor().getPoloCurso().getPolo().getDescricao();
			}

		} finally {
			dao.close();
		}
	}
	
}