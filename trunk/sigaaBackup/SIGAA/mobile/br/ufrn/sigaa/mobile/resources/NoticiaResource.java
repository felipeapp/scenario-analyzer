package br.ufrn.sigaa.mobile.resources;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.negocio.MovimentoCadastroAva;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.mobile.dto.NoticiaDTO;

@Path("/noticia")
public class NoticiaResource extends SigaaGenericResource {

	public NoticiaResource() {
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllNoticias(@QueryParam("ownerId") final Integer idTurma)
			throws ArqException, NegocioException {
		Response response = null;
		Collection<NoticiaDTO> listaNoticias = new ArrayList<NoticiaDTO>();

		request.getSession().setAttribute("tid", idTurma);

		TurmaVirtualDao tvDao = DAOFactory.getInstance().getDAO(
				TurmaVirtualDao.class);

		try {
			Turma turma = new Turma(idTurma);

			registrarAcao(turma, null, EntidadeRegistroAva.NOTICIA,
					AcaoAva.LISTAR, 0);

			List<NoticiaTurma> lista = tvDao.findNoticiasByTurma(turma);

			for (NoticiaTurma noticia : lista) {
				NoticiaDTO dto = new NoticiaDTO();
				dto.setId(noticia.getId());
				dto.setDataCadastro(noticia.getData().getTime());
				dto.setNoticia(StringUtils.toAsciiHtml(StringUtils
						.stripHtmlTags(noticia.getNoticia())));
				dto.setTitulo(noticia.getDescricao());
				dto.setIdTurma(noticia.getTurma().getId());
				dto.setAutor(noticia.getUsuarioCadastro().getNome());

				listaNoticias.add(dto);
			}

			response = Response.ok(JSONProcessor.toJSON(listaNoticias)).build();
		} catch (ArqException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (NegocioException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} finally {
			tvDao.close();
		}

		return response;
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response atualizarNoticia(Reader reader) throws NegocioException,
			ArqException, IOException {
		NoticiaDTO noticia;
		Response response = Response.noContent().build();

		try {
			noticia = parseNoticia(reader);

			for (String idTurma : noticia.getCadastrarEm()) {
				Turma turma = new Turma();
				turma.setId(Integer.valueOf(idTurma));

				NoticiaTurma result = new NoticiaTurma();
				result.setNoticia(noticia.getNoticia());
				result.setDescricao(noticia.getTitulo());
				result.setTurma(turma);
				result.setId(noticia.getId());

				registrarAcao(turma, result.getDescricao(),EntidadeRegistroAva.NOTICIA, AcaoAva.INICIAR_INSERCAO,result.getId());

				MovimentoCadastroAva mov = new MovimentoCadastroAva();
				mov.setObjMovimentado(result);
				mov.setUsuarioLogado(getUsuarioLogado());
				mov.setCodMovimento(SigaaListaComando.ATUALIZAR_AVA);
				mov.setSpecification(getEmptySpecification());

				executarMovimento(mov);

				if (noticia.isNotificacao()) {

					// List <Turma> ts = tBean.getTurmasSemestre();

					/*
					 * if (isUserInRole(SigaaPapeis.GESTOR_TECNICO)) { if
					 * (!ts.contains(tBean.getTurma()))
					 * ts.add(tBean.getTurma()); }
					 */

					TurmaDao tDao = DAOFactory.getInstance().getDAO(
							TurmaDao.class);
					turma = tDao.findByPrimaryKeyOtimizado(turma.getId());

					String assunto = "Nova notícia cadastrada para turma virtual: " + turma.getDescricaoSemDocente();
					String descricao = "<p>" + noticia.getTitulo() + "</p>" + noticia.getNoticia();
					String texto = "Uma notícia foi cadastrada na turma virtual: " + turma.getDescricaoSemDocente() + descricao;

					List<String> idsTurmas = new ArrayList<String>();
					idsTurmas.add(String.valueOf(turma.getId()));

					notificarTurmas(idsTurmas, assunto, texto);
				}

				registrarAcao(turma, result.getDescricao(),	EntidadeRegistroAva.NOTICIA, AcaoAva.ALTERAR, result.getId());

				response = Response.ok(JSONProcessor.toJSON(noticia)).build();
			}
		} catch (NegocioException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (ArqException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		}

		return response;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response cadastrarNoticia(Reader reader) throws ArqException,
			NegocioException, IOException {
		Response response = null;

		try {
			NoticiaDTO noticia = parseNoticia(reader);

			for (String idTurma : noticia.getCadastrarEm()) {
				Turma turma = new Turma();
				turma.setId(Integer.valueOf(idTurma));

				NoticiaTurma result = new NoticiaTurma();
				result.setNoticia(noticia.getNoticia());
				result.setDescricao(noticia.getTitulo());
				result.setTurma(turma);
				// result.setUsuarioCadastro(getUsuarioLogado());
				// result.setData(noticia.getDataCadastro());

				registrarAcao(turma, result.getDescricao(),EntidadeRegistroAva.NOTICIA, AcaoAva.INICIAR_INSERCAO,result.getId());

				MovimentoCadastroAva mov = new MovimentoCadastroAva();
				mov.setObjMovimentado(result);
				mov.setUsuarioLogado(getUsuarioLogado());
				mov.setCodMovimento(SigaaListaComando.CADASTRAR_AVA);
				mov.setSpecification(getEmptySpecification());

				executarMovimento(mov);

				if (noticia.isNotificacao()) {

					// List <Turma> ts = tBean.getTurmasSemestre();

					/*
					 * if (isUserInRole(SigaaPapeis.GESTOR_TECNICO)) { if
					 * (!ts.contains(tBean.getTurma()))
					 * ts.add(tBean.getTurma()); }
					 */

					TurmaDao tDao = DAOFactory.getInstance().getDAO(
							TurmaDao.class);
					turma = tDao.findByPrimaryKeyOtimizado(turma.getId());

					String assunto = "Nova notícia cadastrada para turma virtual: "	+ turma.getDescricaoSemDocente();
					String descricao = "<p>" + noticia.getTitulo() + "</p>"	+ noticia.getNoticia();
					String texto = "Uma notícia foi cadastrada na turma virtual: " + turma.getDescricaoSemDocente() + descricao;

					List<String> idsTurmas = new ArrayList<String>();
					idsTurmas.add(String.valueOf(turma.getId()));

					notificarTurmas(idsTurmas, assunto, texto);
				}

				noticia.setId(mov.getObjMovimentado().getId());

				response = Response.created(URI.create("/noticia" + "/" + noticia.getId())).build();

				registrarAcao(turma, result.getDescricao(),	EntidadeRegistroAva.NOTICIA, AcaoAva.INSERIR, result.getId());
			}
		} catch (ArqException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (NegocioException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		}

		return response;
	}

	@DELETE
	@Path("/{idTurma}/{idNoticia}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deletarNoticia(@PathParam("idNoticia") int idNoticia,
			@PathParam("idTurma") int idTurma) throws ArqException,
			NegocioException {
		Response response = Response.noContent().build();

		try {
			NoticiaTurma object = new NoticiaTurma();
			object.setId(idNoticia);

			Turma turma = new Turma();
			turma.setId(idTurma);

			registrarAcao(turma, null, EntidadeRegistroAva.NOTICIA,
					AcaoAva.INICIAR_REMOCAO, idNoticia);

			MovimentoCadastroAva mov = new MovimentoCadastroAva();
			mov.setCodMovimento(SigaaListaComando.REMOVER_AVA);
			mov.setObjMovimentado(object);
			mov.setSpecification(getEmptySpecification());
			mov.setUsuarioLogado(getUsuarioLogado());

			executarMovimento(mov);

			registrarAcao(turma, null, EntidadeRegistroAva.NOTICIA,
					AcaoAva.REMOVER, object.getId());

			response = Response.ok().build();
		} catch (ArqException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (NegocioException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		}

		return response;
	}

	private NoticiaDTO parseNoticia(Reader reader) throws IOException {
		String noticiaJSON = readStream(reader);

		NoticiaDTO noticia = JSONProcessor.toObject(noticiaJSON,
				NoticiaDTO.class);

		return noticia;
	}

	private void notificarTurmas(List<String> cadastrarEm, String assunto,
			String texto) throws DAOException {
		List<Integer> ids = new ArrayList<Integer>();

		TurmaDao dao = null;
		PlanoDocenciaAssistidaDao daoDocenciaAssistida = null;
		try {
			// Prepara as turmas.
			dao = DAOFactory.getInstance().getDAO(TurmaDao.class);

			if (!isEmpty(cadastrarEm)) {
				for (String tid : cadastrarEm)
					ids.add(Integer.valueOf(tid));

				List<Turma> ts = dao.findByPrimaryKeyOtimizado(ids);
				List<Integer> idsTurmas = new ArrayList<Integer>();

				for (Turma t : ts) {
					// Se for turma agrupadora, utilizar as turmas que estão
					// agrupadas, pois são elas que possuem MatriculaComponente
					// e não a turma agrupadora.
					if (t.isAgrupadora()) {
						Collection<Turma> turmasAgrupadas = dao
								.findByExactField(Turma.class,
										"turmaAgrupadora.id", t.getId());
						for (Turma agrupada : turmasAgrupadas) {
							idsTurmas.add(agrupada.getId());
						}
					} else {
						// Neste caso, a turma não é agrupadora logo é ela que
						// contém as MatriculaComponente
						idsTurmas.add(t.getId());
					}
				}

				// Setando para notificar a todos os participantes. No entanto,
				// deixo semi-pronto caso seja necessário
				// adicionar dinamismo às notificações no futuro.
				boolean notificarDiscentes = true;
				boolean notificarDocentes = true;
				boolean notificarAutorizados = true;
				boolean notificarDocenciaAssistida = true;

				/*
				 * for (int t : tipoUsuario) if (t == DISCENTE)
				 * notificarDiscentes = true; else if (t == DOCENTE)
				 * notificarDocentes = true; else if (t == AUTORIZADO)
				 * notificarAutorizados = true; else if ( t ==
				 * DOCENCIA_ASSISTIDA ) notificarDocenciaAssistida = true;
				 */

				// Se for para enviar aos discentes das turmas,
				/*
				 * if (notificarDiscentes && !notificarAutorizados ){
				 * Collection<MatriculaComponente> matriculas =
				 * dao.findEmailsParticipantesTurmas(idsTurmas);
				 * notificarDiscentes(assunto, texto, matriculas); }
				 */

				// Se for para enviar aos usuários com permissão nas turmas,
				/*
				 * if (notificarAutorizados && !notificarDiscentes ){
				 * List<Usuario> autorizados =
				 * dao.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
				 * notificarUsuariosAutorizados(assunto, texto, autorizados); }
				 */

				// Se for pra enviar aos discentes e aos usuários com permissões
				// nas turmas sem enviar o mesmo e-mail duas vezes caso um
				// discente possua permissão.
				if (notificarDiscentes && notificarAutorizados) {

					Collection<MatriculaComponente> matriculas = dao
							.findEmailsParticipantesTurmas(idsTurmas);
					List<Usuario> autorizados = dao
							.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
					List<Usuario> autorizadosEDiscentes = new ArrayList<Usuario>(
							autorizados);

					if (matriculas != null) {
						for (MatriculaComponente m : matriculas) {

							if (m.getDiscente().getUsuario() != null) {
								boolean isMesmoUsuario = false;

								if (autorizados != null
										&& autorizados.size() > 0) {
									for (Usuario u : autorizados)
										if (u.getEmail().equals(
												m.getDiscente().getUsuario()
														.getEmail()))
											isMesmoUsuario = true;
								}

								if (!isMesmoUsuario)
									autorizadosEDiscentes.add(m.getDiscente()
											.getUsuario());
							}
						}
					}

					for (Usuario u : autorizadosEDiscentes) {
						if (u != null) {
							MailBody body = new MailBody();

							body.setAssunto(assunto);
							body.setMensagem(texto);
							body.setFromName("SIGAA - Turma Virtual");
							body.setEmail(u.getEmail());
							body.setContentType(MailBody.HTML);
							Mail.send(body);
						}
					}
				}

				// Se for pra enviar aos discentes e aos usuários com permissões
				// nas turmas sem enviar o mesmo e-mail duas vezes caso um
				// discente possua permissão.
				if (notificarAutorizados && notificarDocenciaAssistida) {

					daoDocenciaAssistida = DAOFactory.getInstance().getDAO(
							PlanoDocenciaAssistidaDao.class);

					List<PlanoDocenciaAssistida> docenciaAssistida = daoDocenciaAssistida
							.findEmailsDocenciaAssistidaByTurmas(idsTurmas);
					List<Usuario> autorizados = dao
							.findEmailsUsuariosAutorizadosByTurmas(idsTurmas);
					List<PlanoDocenciaAssistida> docenciaAssistidaSemAutorizados = new ArrayList<PlanoDocenciaAssistida>();

					if (!isEmpty(docenciaAssistida)) {
						for (PlanoDocenciaAssistida da : docenciaAssistida) {

							boolean isMesmoUsuario = false;

							if (!isEmpty(autorizados)) {
								for (Usuario u : autorizados)
									if (u.getEmail().equals(
											da.getDiscente().getPessoa()
													.getEmail()))
										isMesmoUsuario = true;
							}

							if (!isMesmoUsuario)
								docenciaAssistidaSemAutorizados.add(da);
						}
					}

					if (!isEmpty(docenciaAssistidaSemAutorizados)) {
						for (PlanoDocenciaAssistida da : docenciaAssistidaSemAutorizados) {
							MailBody body = new MailBody();
							body.setAssunto(assunto);
							body.setMensagem((texto));
							body.setFromName("SIGAA - Turma Virtual");
							body.setContentType(MailBody.HTML);
							if (da.getDiscente().getPessoa() != null) {
								body.setEmail(da.getDiscente().getPessoa()
										.getEmail());
								Mail.send(body);
							}
						}
					}
				}

				// Se for para enviar a docência assistida,
				/*
				 * if (notificarDocenciaAssistida && !notificarAutorizados){
				 * daoDocenciaAssistida =
				 * getDAO(PlanoDocenciaAssistidaDao.class);
				 * List<PlanoDocenciaAssistida> docenciaAssistida =
				 * daoDocenciaAssistida
				 * .findEmailsDocenciaAssistidaByTurmas(idsTurmas);
				 * notificarDocenciaAssistida(assunto, texto,
				 * docenciaAssistida); }
				 */

				// Se for para enviar aos docentes das turmas,
				if (notificarDocentes) {
					List<DocenteTurma> docentes = dao
							.findEmailsDocentesByTurmas(idsTurmas);

					for (DocenteTurma dt : docentes) {
						MailBody body = new MailBody();
						body.setAssunto(assunto);
						body.setMensagem((texto));
						body.setFromName("SIGAA - Turma Virtual");
						body.setContentType(MailBody.HTML);
						if (dt.getDocente().getPrimeiroUsuario() != null) {
							body.setEmail(dt.getDocente().getPrimeiroUsuario()
									.getEmail());
							Mail.send(body);
						}
					}
				}
			}

		} finally {
			if (dao != null)
				dao.close();
			if (daoDocenciaAssistida != null)
				daoDocenciaAssistida.close();
		}
	}

}
