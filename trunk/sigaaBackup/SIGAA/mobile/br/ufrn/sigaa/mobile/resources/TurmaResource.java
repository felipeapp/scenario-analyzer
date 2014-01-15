package br.ufrn.sigaa.mobile.resources;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.sigaa.arq.dao.ensino.HorarioTurmaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.mobile.dto.HorarioDTO;
import br.ufrn.sigaa.mobile.dto.TurmaDTO;
import br.ufrn.sigaa.mobile.resources.dao.CursoDao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Resource para operação com turma
 * 
 * @author bernardo
 *
 */
@Path("/turma")
public class TurmaResource extends SigaaGenericResource {

	/** Retorna as matrículas em aberto do usuário. 
	 * 
	 * @param ownerId ID do usuário do qual se quer buscar as turmas 
	 * @return
	 * @throws DAOException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllTurmas(@QueryParam("ownerId") final Integer ownerId) throws DAOException {
		Response response = null;
		
		Usuario usuarioLogado = getUsuarioLogado();
		Collection<Turma> turmasSigaa = null;
		Collection<TurmaDTO> dto = null;
		br.ufrn.sigaa.mobile.resources.dao.TurmaDao tDao = null;
		//TurmaDao turmaDao = null;
		HorarioTurmaDao horarioDao = null;
		
		try {
			if(usuarioLogado.getDiscenteAtivo() != null) {
				tDao = DAOFactory.getInstance().getDAO(br.ufrn.sigaa.mobile.resources.dao.TurmaDao.class);

				DiscenteAdapter discente = new Discente();
				discente.setId(ownerId);

				popularDiscenteAtivo();

				CalendarioAcademico calendarioVigente = getCalendarioVigente();

				turmasSigaa = tDao.findTurmasMatriculadoByDiscente(discente, calendarioVigente);

				dto = montarDTO(turmasSigaa);

				response = Response.ok(JSONProcessor.toJSON(dto)).build();
			}
			else {
				//turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
				tDao = DAOFactory.getInstance().getDAO(br.ufrn.sigaa.mobile.resources.dao.TurmaDao.class);
				
				Servidor servidor = new Servidor();
				servidor.setId(ownerId);
				
				//turmasSigaa = turmaDao.findByDocenteOtimizado(servidor, SituacaoTurma.ABERTA, null, true);
				turmasSigaa = tDao.findTurmasByDocente(servidor, null, true, SituacaoTurma.ABERTA);
				
				//Recuperando horários
				horarioDao = DAOFactory.getInstance().getDAO(HorarioTurmaDao.class);
				
				if (turmasSigaa != null && turmasSigaa.size() > 0){
					for (Turma t : turmasSigaa){						
						List<HorarioTurma> horarios = horarioDao.findByTurma(t);
						t.setHorarios(horarios);
					}
				}

				dto = montarDTO(turmasSigaa);
				
				response = Response.ok(JSONProcessor.toJSON(dto)).build();
			}
		} catch (DAOException e) {
			if(!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} finally {
			if(tDao != null)
				tDao.close();
			/*if(turmaDao != null)
				turmaDao.close();*/
			if(horarioDao != null)
				horarioDao.close();
		}

		return response;
		
	}

	/**
	 * Atribui os dados do usuario logado na sessão do sistema mobile
	 * 
	 * @param
	 * @return
	 */
	private void popularDiscenteAtivo() throws DAOException {
		Usuario usuarioLogado = getUsuarioLogado();

		CursoDao dao = DAOFactory.getInstance().getDAO(CursoDao.class);

		try {
			if (usuarioLogado.getDiscenteAtivo().getCurso() != null) {
				int idCurso = usuarioLogado.getDiscenteAtivo().getCurso().getId();
			
				if(isNotEmpty(idCurso)) {
					Curso curso = dao.findByPrimaryKey(idCurso, Curso.class);
					//Curso curso = dao.findOtimizado(idCurso);
				
					usuarioLogado.getDiscenteAtivo().setCurso(curso);
				
					request.getSession(false).setAttribute("usuario", usuarioLogado);
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Monta DTO do mobile a partir das informações da entidade do sigaa
	 * 
	 * @param turmasSigaa
	 * @return turmasDTO
	 * @throws DAOException 
	 */
	private Collection<TurmaDTO> montarDTO(Collection<Turma> turmasSigaa) throws DAOException {
		Collection<TurmaDTO> turmas = new ArrayList<TurmaDTO>();

		if(isNotEmpty(turmasSigaa)) {
			for (Turma turma : turmasSigaa) {
				TurmaDTO turmaDTO = new TurmaDTO();

//				ComponenteCurricularDTO componente = new ComponenteCurricularDTO();
//				componente.setCodigo(t.getDisciplina().getCodigo());
//				componente.setCreditos(t.getDisciplina().getCrTotal());
//				componente.setNome(t.getDisciplina().getNome());
				
				int maxFaltas = 0;
				float mediaMinimaPassarPorMedia = 0;
				float mediaMinimaAprovacao = 0;
				ParametrosGestoraAcademica param;
				
				param = ParametrosGestoraAcademicaHelper.getParametros(turma);
				maxFaltas = turma.getDisciplina().getMaximoFaltasPermitido(param.getFrequenciaMinima(), param.getMinutosAulaRegular());
					
				if (turma.getDisciplina().getNivel() == NivelEnsino.LATO){
					mediaMinimaPassarPorMedia = (((CursoLato) param.getCurso()).getPropostaCurso().getMediaMinimaAprovacao());
					mediaMinimaAprovacao = (((CursoLato) param.getCurso()).getPropostaCurso().getMediaMinimaAprovacao());
				}
				else {
					mediaMinimaPassarPorMedia = param.getMediaMinimaPassarPorMedia();
					mediaMinimaAprovacao = param.getMediaMinimaAprovacao();
				}
				
				turmaDTO.setAno(turma.getAno());
				turmaDTO.setCodigo(turma.getCodigo());
				turmaDTO.setId(turma.getId());
				turmaDTO.setLocal(turma.getLocal());
				turmaDTO.setPeriodo(turma.getPeriodo());
				turmaDTO.setCodigoComponente(turma.getDisciplina().getCodigo());
				turmaDTO.setCreditos(turma.getDisciplina().getCrTotal());
				turmaDTO.setNomeComponente(turma.getDisciplina().getNome());
				turmaDTO.setMaxFaltas(maxFaltas);
				turmaDTO.setDescricaoHorario(turma.getDescricaoHorario());
				turmaDTO.setQtdMatriculados(turma.getQtdMatriculados());
				turmaDTO.setMediaMinimaPassarPorMedia(mediaMinimaPassarPorMedia);
				turmaDTO.setMediaMinimaAprovacao(mediaMinimaAprovacao);
				
				if (turma.getTurmaAgrupadora() != null)
					turmaDTO.setIdTurmaAgrupadora(turma.getTurmaAgrupadora().getId());
				
				//Setando informações de horário
				List<HorarioDTO> horariosDto = new ArrayList<HorarioDTO>();
				
				for (HorarioTurma h : turma.getHorarios()) {
					HorarioDTO horarioDto = new HorarioDTO();
					//horarioDto.setId(h.getId());
					horarioDto.setId(gerarIdHorario(Integer.parseInt(String.valueOf(h.getDia())), 
							h.getHorario().getInicio().getTime(),h.getHorario().getFim().getTime()));
					horarioDto.setFim(h.getHorario().getFim().getTime());
					horarioDto.setInicio(h.getHorario().getInicio().getTime());
					horarioDto.setDia(h.getDia());
					horarioDto.setTurno(h.getHorario().getTipo());
					
					horariosDto.add(horarioDto);
				}
				
				turmaDTO.setHorariosTurma(horariosDto);
				
				turmas.add(turmaDTO);
			}
		}

		return turmas;
	}
	
	/**
	 * Gera um Id para o horario com base nos valores de data.
	 * 
	 * @param dia
	 * @param inicio
	 * @param fim
	 * @return id
	 */
	private int gerarIdHorario(int dia, long inicio, long fim){
		long temp = inicio + fim;
		
		String tail = Long.toString(temp);
		String head = Integer.toString(dia);
		String result = (head+tail).substring(0, 7);
		
		return Integer.parseInt(result);
	}
}
