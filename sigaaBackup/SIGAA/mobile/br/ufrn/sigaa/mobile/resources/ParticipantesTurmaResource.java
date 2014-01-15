package br.ufrn.sigaa.mobile.resources;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.mobile.dto.ListaParticipantesTurma;
import br.ufrn.sigaa.mobile.dto.ParticipanteDiscenteDTO;
import br.ufrn.sigaa.mobile.dto.ParticipanteDocenteDTO;
import br.ufrn.sigaa.mobile.dto.tipos.TipoParticipanteTurma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

@Path("/listaparticipantesturma")
public class ParticipantesTurmaResource extends SigaaGenericResource {

	public ParticipantesTurmaResource() {
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllParticipantes(@QueryParam("ownerId") final Integer idTurma) throws DAOException, ArqException, NegocioException {
		Response response = null;
		
		ListaParticipantesTurma participantes = new ListaParticipantesTurma();

		request.getSession().setAttribute("tid", idTurma);

		UsuarioDao usuarioDao = null;
		TurmaDao turmaDao = null;
		DocenteTurmaDao docenteTurmaDao = null;
		
		usuarioDao = DAOFactory.getInstance().getDAO(UsuarioDao.class);
		turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		docenteTurmaDao = DAOFactory.getInstance().getDAO(DocenteTurmaDao.class);
		
		try {
			//TODO Verificar maneira de otimizar listagem de participantes para subturmas
			Turma turma = usuarioDao.findByPrimaryKey(idTurma, Turma.class);

			registrarAcao(turma, null, EntidadeRegistroAva.PARTICIPANTES, AcaoAva.LISTAR, 0);

			List<DocenteTurma> docentes = getDocentesTurma(turma, usuarioDao, docenteTurmaDao);
			List<PlanoDocenciaAssistida> docenciaAssistida = getDocenciaAssistida(turma, usuarioDao);
			List<Discente> monitores = getMonitores(turma);
			Collection<MatriculaComponente> discentes = getDiscentesTurma(turma, turmaDao);

			List<ParticipanteDiscenteDTO> discentesDTO = new ArrayList<ParticipanteDiscenteDTO>();
			List<ParticipanteDiscenteDTO> monitoresDTO = new ArrayList<ParticipanteDiscenteDTO>();
			List<ParticipanteDiscenteDTO> docenciaAssistidaDTO = new ArrayList<ParticipanteDiscenteDTO>();
			List<ParticipanteDocenteDTO> docentesDTO = new ArrayList<ParticipanteDocenteDTO>();

			for (MatriculaComponente mc : discentes) {
				
				
				Discente disc =  mc.getDiscente().getDiscente();
				
				ParticipanteDiscenteDTO dto = montarDTODiscente(disc,TipoParticipanteTurma.TIPO_DISCENTE,idTurma);
				
				discentesDTO.add(dto);
			}

			for (Discente disc : monitores) {
				
				ParticipanteDiscenteDTO dto = montarDTODiscente(disc,TipoParticipanteTurma.TIPO_MONITOR,idTurma);

				monitoresDTO.add(dto);
			}

			for (PlanoDocenciaAssistida pda : docenciaAssistida) {
				
				Discente disc = pda.getDiscente().getDiscente();

				ParticipanteDiscenteDTO dto = montarDTODiscente(disc,TipoParticipanteTurma.TIPO_DOCENTE_ASSISTIDO,idTurma);

				docenciaAssistidaDTO.add(dto);
			}

			for (DocenteTurma dt : docentes) {
				
				ParticipanteDocenteDTO dto = montarDocente(dt,idTurma);

				docentesDTO.add(dto);
			}

			participantes.setDiscentes(discentesDTO);
			participantes.setDocentes(docentesDTO);
			participantes.setDocentesAssistidos(docenciaAssistidaDTO);
			participantes.setMonitores(monitoresDTO);

			response = Response.ok(JSONProcessor.toJSON(participantes)).build();
			
		} catch (DAOException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (ArqException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} catch (NegocioException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} finally {
			usuarioDao.close();
			turmaDao.close();
			docenteTurmaDao.close();
		}

		return response;
	}

	private ParticipanteDocenteDTO montarDocente(DocenteTurma dt, Integer idTurma) {
		ParticipanteDocenteDTO dto = new ParticipanteDocenteDTO();
		Servidor docente = dt.getDocente();
		
		dto.setTipoParticipacao(TipoParticipanteTurma.TIPO_DOCENTE);

		dto.setIdTurma(idTurma);
		dto.setId(docente.getId());
		if (docente.getPrimeiroUsuario() != null && docente.getPrimeiroUsuario().getLogin() != null)
			dto.setLogin(docente.getPrimeiroUsuario().getLogin());
		else
			dto.setLogin("Sem cadastro no sistema.");
		dto.setNome(docente.getPessoa().getNome());
		dto.setEmail(docente.getPrimeiroUsuario().getEmail());

		Integer idFoto = docente.getIdFoto();

		if (idFoto != null) {
			dto.setIdFoto(idFoto);
			dto.setChaveFoto(UFRNUtils.generateArquivoKey(idFoto));
		} else {
			dto.setIdFoto(0);
			dto.setChaveFoto("0");
		}
		
		
		dto.setSiape(String.valueOf(docente.getSiape()));
		dto.setLotacao(docente.getUnidade().getNome());

		return dto;
	}

	private ParticipanteDiscenteDTO montarDTODiscente(Discente disc,int tipo, int idTurma) {
		ParticipanteDiscenteDTO dto = new ParticipanteDiscenteDTO();
		
		dto.setTipoParticipacao(tipo);

		dto.setIdTurma(idTurma);
		dto.setId(disc.getId());
		if (disc.getUsuario() != null && disc.getUsuario().getLogin() != null)
			dto.setLogin(disc.getUsuario().getLogin());
		else
			dto.setLogin("Sem cadastro no sistema.");
		dto.setNome(disc.getPessoa().getNome());
		dto.setEmail(disc.getPessoa().getEmail());

		Integer idFoto = disc.getIdFoto();

		if (idFoto != null) {
			dto.setIdFoto(idFoto);
			dto.setChaveFoto(UFRNUtils.generateArquivoKey(idFoto));
		} else {
			dto.setIdFoto(0);
			dto.setChaveFoto("0");
		}
		
		dto.setMatricula(disc.getMatricula().toString());
		dto.setCurso(disc.getCurso().getNome());

		return dto;
	}

	private List<DocenteTurma> getDocentesTurma(Turma turma, UsuarioDao usuarioDao, DocenteTurmaDao docenteTurmaDao) throws DAOException {
		List<DocenteTurma> docentesTurma = new ArrayList<DocenteTurma>();
		
		if (isNotEmpty(turma.getSubturmas())) {
			// Em caso de turma agrupadora, deve-se buscar os docentes das sub-turmas.
			List<Servidor> docentes = new ArrayList<Servidor>();
			List<DocenteExterno> docentesExternos = new ArrayList<DocenteExterno>();
			Map<Integer, String> subTurmasDocente = new HashMap<Integer, String>();
			Map<Integer, String> subTurmasDocenteExterno = new HashMap<Integer, String>();

			List<Turma> subTurmas = turma.getSubturmas();
			for (Turma st : subTurmas) {
				for (DocenteTurma dt : st.getDocentesTurmas()) {
					if (dt.getDocente() != null) {
						// Servidor
						if (!docentes.contains(dt.getDocente())) {
							docentes.add(dt.getDocente());
						}
						if (subTurmasDocente.get(dt.getDocente().getId()) == null)
							subTurmasDocente.put(dt.getDocente().getId(), st.getCodigo() + " ");
						else
							subTurmasDocente.put(dt.getDocente().getId(), subTurmasDocente.get(dt.getDocente().getId()) + st.getCodigo() + " ");
					} else {
						// Docente externo
						if (!docentesExternos.contains(dt.getDocenteExterno())) {
							docentesExternos.add(dt.getDocenteExterno());
						}
						if (subTurmasDocenteExterno.get(dt.getDocenteExterno().getId()) == null)
							subTurmasDocenteExterno.put(dt.getDocenteExterno().getId(), st.getCodigo() + " ");
						else
							subTurmasDocenteExterno.put(dt.getDocenteExterno().getId(), subTurmasDocenteExterno.get(dt.getDocenteExterno().getId()) + st.getCodigo() + " ");
					}
				}
			}
			for (Servidor doc : docentes) {
				doc.setPrimeiroUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(doc.getPessoa().getId()));
				DocenteTurma dt = new DocenteTurma();
				dt.setTurma((Turma) UFRNUtils.deepCopy(turma));
				dt.setDocente(doc);
				dt.getTurma().setCodigo(subTurmasDocente.get(doc.getId()));
				docentesTurma.add(dt);
			}
			for (DocenteExterno doc : docentesExternos) {
				Servidor s = new Servidor();
				s.setPessoa(doc.getPessoa());
				s.setFormacao(doc.getFormacao());
				s.setPrimeiroUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(doc.getPessoa().getId()));

				DocenteExterno de = new DocenteExterno();
				de.setPessoa(doc.getPessoa());

				DocenteTurma dt = new DocenteTurma();
				dt.setTurma((Turma) UFRNUtils.deepCopy(turma));
				dt.setDocente(s);
				dt.setDocenteExterno(de);
				dt.getTurma().setCodigo(subTurmasDocenteExterno.get(doc.getId()));

				docentesTurma.add(dt);
			}
		} else {
			docentesTurma = docenteTurmaDao.findDocentesByTurma(turma);
		}
		return docentesTurma;
	}

	private List<PlanoDocenciaAssistida> getDocenciaAssistida(Turma turma,UsuarioDao usuarioDao) throws DAOException {
		PlanoDocenciaAssistidaDao dao = DAOFactory.getInstance().getDAO(PlanoDocenciaAssistidaDao.class);
		List<PlanoDocenciaAssistida> docenciaAssistida = new ArrayList<PlanoDocenciaAssistida>();

		try {
			docenciaAssistida = dao.findAllByTurma(turma);
			for (PlanoDocenciaAssistida plano : docenciaAssistida) {
				plano.getDiscente().setUsuario(usuarioDao.findPrimeiroUsuarioByPessoa(plano.getDiscente().getPessoa().getId()));
			}
		} finally {
			dao.close();
		}

		return docenciaAssistida;
	}

	private List<Discente> getMonitores(Turma turma) throws DAOException {
		TurmaVirtualDao dao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		List<Discente> monitores = new ArrayList<Discente>();

		try {
			monitores = dao.findMonitores(turma.getDisciplina().getId());
		} finally {
			dao.close();
		}

		return monitores;
	}

	private Collection<MatriculaComponente> getDiscentesTurma(Turma turma, TurmaDao dao) {
		Collection<MatriculaComponente> discentesTurma = new ArrayList<MatriculaComponente>();
		List<MatriculaComponente> discentes = new ArrayList<MatriculaComponente>();

		if (turma != null) {
			if (isNotEmpty(turma.getSubturmas())) {
				for (Turma st : turma.getSubturmas()) {
					Collection<MatriculaComponente> list = dao.findParticipantesTurma(st.getId());
					if (list != null)
						discentes.addAll(list);
				}
				
				Collections.sort(discentes, new Comparator<MatriculaComponente>() {
							public int compare(MatriculaComponente o1, MatriculaComponente o2) {
								return o1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(o2.getDiscente().getPessoa().getNomeAscii());
							}
						});

				discentesTurma = discentes;
			} else {
				discentesTurma = dao.findParticipantesTurma(turma.getId());
			}
		}

		return discentesTurma;
	}

}
