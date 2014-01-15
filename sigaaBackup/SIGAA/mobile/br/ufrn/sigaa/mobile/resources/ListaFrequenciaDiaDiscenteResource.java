package br.ufrn.sigaa.mobile.resources;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mobile.dto.FrequenciaDiaDiscenteDTO;

/**
 * Lista as frequências diárias de um discente.
 * @author Bernardo
 *
 */
@Path("/frequenciadiadiscente")
public class ListaFrequenciaDiaDiscenteResource extends SigaaGenericResource {
	
	/**
	 * Construtor padrão.
	 */
	public ListaFrequenciaDiaDiscenteResource() {
	}

	/** Retorna a lista de frequência diárias do discente.
	 * @param idTurma
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getListaFrequenciaDia(@QueryParam("ownerId") final Integer idTurma) throws ArqException, NegocioException {
		Response response = null;
		
		List<FrequenciaDiaDiscenteDTO> frequenciasDiscente = new ArrayList<FrequenciaDiaDiscenteDTO>();


		//ListaFrequenciaDiaDiscenteDTO freqs = new ListaFrequenciaDiaDiscenteDTO();
		
		request.getSession().setAttribute("tid", idTurma);
		
		TurmaDao tDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		FrequenciaAlunoDao fDao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);
		
		try {
			Turma turma = fDao.findAndFetch(idTurma, Turma.class, "curso");
			
			registrarAcao(turma, null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.LISTAR, 0);
			
			// Se a turma que o aluno está for agrupadora, deve buscar a frequência dele na subturma em que ele está matriculado.
			if (turma.isAgrupadora()){
				turma = tDao.findSubturmaByTurmaDiscente(turma, getDiscenteLogado());
			}
			
			List<FrequenciaAluno> frequencias = fDao.findFrequenciasByDiscente(getDiscenteLogado(), turma);
							
			//buscar os dias que são feriados
			List<Date> feriadosTurma = TurmaUtil.getFeriados(turma);

			Iterator<FrequenciaAluno> itrFrequencias = frequencias.iterator();
			Iterator<Date> itrFeriados = feriadosTurma.iterator();
			
						
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			c.add(Calendar.YEAR, -1);
			
			//remove os feriados de anos inferiores ao ano passado.
			while (itrFeriados.hasNext()) {
				Date d = itrFeriados.next();
				if (CalendarUtils.getAno(d) < c.get(Calendar.YEAR)) {
					itrFeriados.remove();
				}
			}
						
			//para cada possivel dia de aula
			while (itrFrequencias.hasNext()) {
				FrequenciaAluno aux = itrFrequencias.next();
				Date d = aux.getData();
				
				itrFeriados = feriadosTurma.iterator();
				//caso o possivel dia de aula seja um feriado remova-o da lista.
				while (itrFeriados.hasNext() ){
					if (CalendarUtils.isSameDay(d,itrFeriados.next())) {
						itrFrequencias.remove();
						break;
					}
				}
			}
			
//			int maxFaltas = 0;
//			ParametrosGestoraAcademica param;
//			
//			param = ParametrosGestoraAcademicaHelper.getParametros(turma);
//			maxFaltas = turma.getDisciplina().getMaximoFaltasPermitido(param.getFrequenciaMinima(), param.getMinutosAulaRegular());
//			freqs.setMaxFaltas(maxFaltas);
			
//			DiscenteDTO discenteDto = new DiscenteDTO();
			Usuario usuario = getUsuarioLogado();
			DiscenteAdapter discente = usuario.getDiscenteAtivo().getDiscente();
//			discenteDto.setIdUsuario(usuario.getId());
//			discenteDto.setRegistroEntrada(usuario.getRegistroEntrada().getId());
//			discenteDto.setLogin(usuario.getLogin());
//			discenteDto.setId(discente.getId());
//			discenteDto.setNome(discente.getPessoa().getNome());
//			discenteDto.setEmail(discente.getPessoa().getEmail());
//			discenteDto.setMatricula(discente.getMatricula().toString());
//			discenteDto.setCurso(discente.getCurso().getDescricao());
			
			for (FrequenciaAluno frequencia : frequencias) {
				FrequenciaDiaDiscenteDTO dto = new FrequenciaDiaDiscenteDTO();
				
				dto.setData(frequencia.getData());
				dto.setId(frequencia.getId());
				dto.setAtivo(frequencia.getDataCadastro() != null);
				dto.setIdTurma(turma.getId());
				dto.setIdDiscente(discente.getId());
				dto.setFaltas(frequencia.getFaltas());
				
				frequenciasDiscente.add(dto);
				
			}
			
			response = Response.ok(JSONProcessor.toJSON(frequenciasDiscente)).build();
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
			tDao.close();
			fDao.close();
		}

		return response;
	}
}
