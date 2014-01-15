/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '11/05/2007'
 *
 */
package br.ufrn.sigaa.struts;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.ajax.SigaaAjaxServlet;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultorDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ConsultoriaEspecialDao;
import br.ufrn.sigaa.arq.dominio.SigaaConstantes;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ConsultoriaEspecial;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Servlet utilizada para controlar o acesso dos Consultores Externos de Pesquisa ao SIGAA
 *
 * @author Ricardo Wendell
 *
 */
public class AcessoConsultor extends SigaaAjaxServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		ConsultorDao consultorDao = new ConsultorDao();

		// Dispatcher padrão
		RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/jsp/include/acesso_consultor.jsp");

		// Popular o formulário com a identificação enviada ao consultor
		String codigo = req.getParameter( Consultor.CODIGO_CONSULTOR );
		if ( codigo == null ) {
			codigo = (String) req.getAttribute( Consultor.CODIGO_CONSULTOR );
		}
		Boolean confirmacao = (Boolean) req.getAttribute("confirmacao");

		try {

			// Verificar se o código de acesso foi informado
			if ( codigo == null || codigo.length() < 2 || codigo.charAt(0) != Consultor.PREFIXO_USUARIO ) {
				req.setAttribute("codigoInvalido",
					"Código de Acesso Inválido. <br />" +
					"É necessário acessar o sistema através do link fornecido no e-mail de comunicação");
				dispatcher.forward(req, res);
				return;
			}

			int codigoAcesso = getCodigoInt(codigo);

			// Buscar o consultor associado ao código informado
			Consultor consultor = null;
			consultor = consultorDao.findByCodigoAcesso(codigoAcesso);

			// Verificar existência do consultor
			if (consultor == null) {
				req.setAttribute("codigoInvalido",
				"Não foi encontrado um registro de consultor associado ao código informado!");
				dispatcher.forward(req, res);
				return;
			}

		} catch (DAOException e) {
			e.printStackTrace();
			throw new ServletException(e);
		}

		// Popular formulário
		req.setAttribute(Consultor.CODIGO_CONSULTOR, codigo);
		req.setAttribute("confirmacao", ( confirmacao == null ? true : confirmacao ) );

		req.getSession().removeAttribute(SigaaConstantes.USUARIO_SESSAO);
		dispatcher.forward(req, res);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.setAttribute( "sistema", Sistema.SIGAA );

		ConsultorDao consultorDao = new ConsultorDao();
		UsuarioDao usuarioDao = new UsuarioDao();
		CalendarioPesquisaDao calendarioDao = new CalendarioPesquisaDao();
		ConsultoriaEspecialDao consultoriaEspecialDao = new ConsultoriaEspecialDao();

		// Buscar dados do formulário
		String codigo = req.getParameter(Consultor.CODIGO_CONSULTOR);
		String senha = req.getParameter("senha");
		Boolean confirmacao = Boolean.valueOf( req.getParameter("confirmacao") );
		String justificativa = req.getParameter("justificativa");

		// Validar código e senha de acesso do consultor
		Consultor consultor = null;
		try {
			consultor = consultorDao.validateAcesso(getCodigoInt(codigo), senha);

			// Verificar existência do consultor
			if (consultor != null) {

				// Buscar consultoria especial
				ConsultoriaEspecial consultoriaEspecial = consultoriaEspecialDao.findByConsultor(consultor);

				// Validar período para avaliação dos projetos
				CalendarioPesquisa calendario = calendarioDao.findVigente();
				if ( (consultoriaEspecial == null && !CalendarUtils.isDentroPeriodo(calendario.getInicioAvaliacaoConsultores(), calendario.getFimEnvioAvaliacaoConsultores()))
					|| ( consultoriaEspecial != null && !CalendarUtils.isDentroPeriodo(consultoriaEspecial.getDataInicio(), consultoriaEspecial.getDataFim()) )) {
					AbstractAction.addMensagemErro("Caro consultor, o período para avaliações de projetos não está vigente.", req);

					req.setAttribute(Consultor.CODIGO_CONSULTOR, codigo);
					req.setAttribute("confirmacao", confirmacao);
					if (!confirmacao) {
						req.setAttribute("justificativa", justificativa);
					}
					doGet(req, res);
					return;
				}

				// Se o consultor não for avaliar os projetos, atualizar as avaliações a ele destinadas
				if ( !confirmacao ) {

					AvaliacaoProjeto avaliacao = new AvaliacaoProjeto();
					avaliacao.setConsultor(consultor);

					if ( justificativa != null && StringUtils.notEmpty(justificativa)  ) {
						avaliacao.setJustificativa(justificativa.trim());
					} else {
						avaliacao.setJustificativa( req.getParameter("motivo") );
					}

					avaliacao.setCodMovimento( SigaaListaComando.DESISTIR_AVALIACAO_PROJETO_PESQUISA );

					req.getSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, new Usuario());
					prepareMovimento(SigaaListaComando.DESISTIR_AVALIACAO_PROJETO_PESQUISA, req);
					execute(avaliacao, req);
					req.getSession().invalidate();

					req.setAttribute("avaliacaoJustificada", true);
					doGet(req, res);
					return;
				}

				// Buscar se já existe um usuário para o consultor
				Usuario usuario = usuarioDao.findByConsultor(consultor);

				if (usuario == null) {

					// Cadastrar um usuário para o consultor
					usuario = new Usuario();
					usuario.setConsultor(consultor);
					usuario.setLogin( "" + Consultor.PREFIXO_USUARIO + consultor.getCodigo() );
					usuario.setSenha( consultor.getSenha() );
					usuario.setEmail( consultor.getEmail() );
					usuario.setTipo( new TipoUsuario(TipoUsuario.TIPO_CONSULTOR) );
					usuario.setIdConsultor( consultor.getId() );

					// Colocar usuário em sessão para poder chamar processador
					req.getSession().invalidate();
					req.getSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);

					prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO, req);

					// Cadastrar usuário
					br.ufrn.arq.usuarios.UsuarioMov mov = new br.ufrn.arq.usuarios.UsuarioMov();
					mov.setUsuario(usuario);
					mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO);
					mov.setRequest(req);
					
					execute(mov, req);

					usuario = usuarioDao.findByConsultor(consultor);
					req.getSession().invalidate();
				}

				atualizarDadosUsuario(usuario, consultor, req);
				
				// Repopular a senha do usuário
				usuario.setSenha( consultor.getSenha() );

				// Preparar movimento de logon
				UsuarioMov usuarioMov = new UsuarioMov();
				usuarioMov.setUsuario(usuario);
				usuarioMov.setIP( req.getRemoteAddr() );
				usuarioMov.setHost( AmbienteUtils.getLocalName() );
				usuarioMov.setUserAgent( req.getHeader("User-Agent") );
				usuarioMov.setCodMovimento(SigaaListaComando.LOGON);
				usuarioMov.setRequest(req);
				
				// Logar-se e redirecionar para o portal do consultor
				req.getSession().invalidate();
				req.getSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);

				prepareMovimento(SigaaListaComando.LOGON, req);
				usuario = (Usuario) execute(usuarioMov, req);
				if (usuario == null) {
					AbstractAction.addMensagemErro( "Usuário/Senha Inválidos" , req );
					doGet(req, res);
				} else {

					// Identificar navegador do usuário
					if (!UFRNUtils.identificaBrowserRecomendado(req.getHeader("User-Agent"))) {
						req.getSession().setAttribute("avisoBrowser", Boolean.TRUE);
					}

					usuario.setPessoa(new Pessoa(0, consultor.getNome().toUpperCase()));
					usuario.setConsultor(consultor);
					req.getSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);
					req.setAttribute("codigo", codigo);
					res.sendRedirect(SigaaSubsistemas.PORTAL_CONSULTOR.getLink() + "?codigo=" + codigo);
				}
				return;
			} else {
				// Código e senha não conferem
				AbstractAction.addMensagemErro("Identificação e/ou senha não conferem", req);

				req.setAttribute(Consultor.CODIGO_CONSULTOR, codigo);
				req.setAttribute("confirmacao", confirmacao);
				if (!confirmacao) {
					req.setAttribute("justificativa", justificativa);
				}
				doGet(req, res);
			}

		} catch (DAOException e) {
			e.printStackTrace();
			throw new ServletException(e);
		} catch (ArqException e) {
			e.printStackTrace();
			throw new ServletException(e);
		} catch (NegocioException e) {
			e.printStackTrace();
			AbstractAction.addMensagens(e.getListaMensagens().getMensagens(), req);

			req.setAttribute(Consultor.CODIGO_CONSULTOR, codigo);
			req.setAttribute("confirmacao", confirmacao);
			req.setAttribute("justificativa", justificativa);

			doGet(req, res);
		} finally {
			consultorDao.close();
			usuarioDao.close();
			calendarioDao.close();
			consultoriaEspecialDao.close();
		}

	}

	private void atualizarDadosUsuario(Usuario usuario, Consultor consultor, HttpServletRequest req) throws ArqException {
		UsuarioDAO dao = DAOFactory.getInstance().getDAO(UsuarioDAO.class);
		try {
			UsuarioGeral usr = dao.findByLogin(usuario.getLogin(), false, null, Boolean.TRUE);
			if ( !usr.getSenha().equals( UFRNUtils.toMD5(consultor.getSenha())) )
				UserAutenticacao.atualizaSenhaAtual(req, usuario.getId(), null, consultor.getSenha());
		} finally {
			dao.close();
		}
	}

	private int getCodigoInt(String codigo) {
		return Integer.valueOf( codigo.substring(1) );
	}

	@Override
	public String getXmlContent(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		return null;
	}
}
