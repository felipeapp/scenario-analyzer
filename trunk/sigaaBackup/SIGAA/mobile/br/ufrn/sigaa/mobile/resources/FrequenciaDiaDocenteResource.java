package br.ufrn.sigaa.mobile.resources;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mobile.utils.IDGenerator;
import br.ufrn.arq.mobile.utils.JSONProcessor;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TopicoAulaDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.FrequenciaAluno;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.FrequenciaMov;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mobile.dto.FrequenciaDTO;
import br.ufrn.sigaa.mobile.dto.FrequenciaDiaDocenteDTO;
import br.ufrn.sigaa.mobile.dto.tipos.TipoStatusFrequencia;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@Path("/frequenciadiadocente")
public class FrequenciaDiaDocenteResource extends SigaaGenericResource {

	public FrequenciaDiaDocenteResource() {
	}

	/**
	 * Retorna a frequência do dia.
	 * 
	 * @param idTurma
	 * @param dataLong
	 * @return
	 * @throws DAOException
	 */
	@GET
	@Path("/{turma}/{data}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFrequenciaDia(@PathParam("turma") Integer idTurma, @PathParam("data") Long dataLong) throws DAOException {
		Response response = null;
		FrequenciaDiaDocenteDTO frequencia = new FrequenciaDiaDocenteDTO();

		Turma turmaSigaa = new Turma(idTurma);
		Date data = new Date(dataLong);

		FrequenciaAlunoDao fDao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);

		try {
			turmaSigaa = fDao.findAndFetch(idTurma, Turma.class, "horarios");

			frequencia.setData(data);
			frequencia.setIdTurma(idTurma);
			frequencia.setFrequencias(getFrequencia(turmaSigaa, data));
			frequencia.setMaxFaltas(getMaxFaltas(data, turmaSigaa));
			
			if (fDao.diaTemFrequencia(data, turmaSigaa))
				frequencia.setStatus(TipoStatusFrequencia.LANCADA);
			else
				frequencia.setStatus(TipoStatusFrequencia.NAO_LANCADA);

			response = Response.ok(JSONProcessor.toJSON(frequencia)).build();
		} catch (DAOException e) {
			if (!e.isNotificavel())
				response = Response.serverError().entity(e.getMessage()).build();
			else
				throw e;
		} finally {
			fDao.close();
		}

		return response;
	}

	/**
	 * Retorna todas as frequências da turma.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFrequenciaDia(@QueryParam("ownerId") final Integer idTurma, @QueryParam("vinculo") final Integer vinculo)
			throws DAOException {
		
		Usuario usuario = getUsuarioLogado();
		usuario.setVinculoAtivo(vinculo);
		
		Response response = null;
		Collection<FrequenciaDiaDocenteDTO> freqs = new ArrayList<FrequenciaDiaDocenteDTO>();

		request.getSession().setAttribute("tid", idTurma);

		FrequenciaAlunoDao dao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);

		try {
			Turma turma = dao.findByPrimaryKey(idTurma, Turma.class, "id", "ano");
			Turma turmaSigaa = new Turma(idTurma);
			turmaSigaa = dao.findAndFetch(idTurma, Turma.class, "horarios");
			turma.setHorarios(turmaSigaa.getHorarios());

			// buscar os dias que são feriados
			List<Date> feriadosTurma = TurmaUtil.getFeriados(turma);
			List<Date> diasSemAulas = null;

			if (turma.getTurmaAgrupadora() == null) {
				diasSemAulas = TurmaUtil.getDatasCanceladas(idTurma);
			} else {
				diasSemAulas = TurmaUtil.getDatasCanceladas(turma.getTurmaAgrupadora().getId());
			}

			CalendarioAcademico calendarioAcademico = getCalendarioVigente();

			Set<Date> datasAulas = TurmaUtil.getDatasAulasTruncate(turma, calendarioAcademico);

			for (Date date : datasAulas) {
				FrequenciaDiaDocenteDTO temp = new FrequenciaDiaDocenteDTO();
				temp.setData(date);
				temp.setIdTurma(turma.getId());
				temp.setMaxFaltas(getMaxFaltas(date, turma));
				if (!feriadosTurma.contains(date)) {
					if (dao.diaTemFrequencia(date, turma))
						temp.setStatus(TipoStatusFrequencia.LANCADA);
					else
						temp.setStatus(TipoStatusFrequencia.NAO_LANCADA);

					if (diasSemAulas != null && !diasSemAulas.isEmpty()) {
						for (Date d : diasSemAulas) {
							if (date.equals(d)) {
								temp.setStatus(TipoStatusFrequencia.CANCELADA);
							}
						}
					}
				} else {
					temp.setStatus(TipoStatusFrequencia.FERIADO);
				}

				freqs.add(temp);
			}

			response = Response.ok(JSONProcessor.toJSON(freqs)).build();
		} catch (DAOException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
		} finally {
			dao.close();
		}

		return response;
	}

	/**
	 * Retorna todas as frequências da turma no dia passado.
	 * 
	 * @param turma
	 * @param dataSelecionada
	 * @return
	 */
	private List<FrequenciaDTO> getFrequencia(Turma turma, Date dataSelecionada) {
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		br.ufrn.sigaa.mobile.resources.dao.TurmaDao turmaDaoMobile = DAOFactory.getInstance().getDAO(br.ufrn.sigaa.mobile.resources.dao.TurmaDao.class);

		try {
			List<FrequenciaAluno> frequencias = turmaDaoMobile.findFrequenciasByTurma(turma, dataSelecionada);
			Collection<MatriculaComponente> matriculas = turmaDao.findMatriculasAConsolidar(turma);

			if (frequencias != null && matriculas != null) {
				for (MatriculaComponente m : matriculas) {
					if (!contem(frequencias, m)) {
						FrequenciaAluno freq = new FrequenciaAluno();

						freq.setId(m.getId());
						freq.setData(dataSelecionada);
						Pessoa p = m.getDiscente().getPessoa();
						freq.setDiscente(m.getDiscente().getDiscente());
						freq.getDiscente().setPessoa(p);
						freq.setTurma(turma);
						freq.setFaltas((short) 0);

						frequencias.add(freq);
					}
				}

				Collections.sort(frequencias, new Comparator<FrequenciaAluno>() {
					public int compare(FrequenciaAluno f1, FrequenciaAluno f2) {
						return f1.getDiscente().getPessoa().getNomeAscii().compareToIgnoreCase(f2.getDiscente().getPessoa().getNomeAscii());
					}
				});
			}

			List<FrequenciaDTO> list = new ArrayList<FrequenciaDTO>();

			for (FrequenciaAluno fa : frequencias) {
				FrequenciaDTO fd = new FrequenciaDTO();
				// DiscenteDTO disc = new DiscenteDTO();

				// disc.setId(fa.getDiscente().getId());
				// disc.setNome(StringUtils.toAscii(fa.getDiscente().getNome()));
				// disc.setMatricula(fa.getDiscente().getMatricula().toString());

				int idDiscente = fa.getDiscente().getId();
				int idFrequencia = IDGenerator.cantorPairing(idDiscente, fa.getData());

				fd.setId(idFrequencia);
				fd.setIdDiscente(idDiscente);
				fd.setFaltas(fa.getFaltas());

				list.add(fd);
			}

			return list;
		} finally {
			turmaDao.close();
		}
	}

	private boolean contem(List<FrequenciaAluno> frequencias, MatriculaComponente m) {
		for (int i = 0; i < frequencias.size(); i++) {
			if (m.getDiscente().getMatricula().equals(frequencias.get(i).getDiscente().getMatricula())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Atualiza ou remove a frequência do dia.
	 * 
	 * @param reader
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws IOException
	 */
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public Response atualizarFrequencia(Reader reader) throws NegocioException, ArqException, IOException {
		FrequenciaDiaDocenteDTO frequencia;
		Response response = Response.noContent().build();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		FrequenciaAlunoDao daof = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);

		try {
			frequencia = parseFrequencia(reader);
			Turma turma = dao.findByPrimaryKey(frequencia.getIdTurma(), Turma.class, "situacaoTurma");
			turma.setId(frequencia.getIdTurma());

			Date dataSelecionada = frequencia.getData();
			
			List<FrequenciaAluno> frequenciaSigaa = daof.findAllFrequenciasByTurmaData(turma, dataSelecionada);
			
			List<FrequenciaAluno> frequencias = new ArrayList<FrequenciaAluno>();

			for (FrequenciaDTO f : frequencia.getFrequencias()) {
				
				for (FrequenciaAluno fa : frequenciaSigaa){
					
					if(fa.getDiscente().getId() == f.getIdDiscente()){
						fa.setFaltas(f.getFaltas());
						fa.setData(dataSelecionada);
						fa.setTurma(turma);
						frequencias.add(fa);
						break;
					}
					
				}
				
			}

			if (dataSelecionada == null) {
				if (frequencia.getStatus() == 1)
					registrarAcao(turma, null, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, turma.getId());

				return null;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String data = sdf.format(dataSelecionada);

			if (frequencia.getStatus() == 1)
				registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, turma.getId());
			if (frequencia.getStatus() == 0)
				registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_REMOCAO, turma.getId());
			
			// TODO melhorar tratamento do caso
			if (dataSelecionada.after(new Date())) {
				return null;
			}

			if (turma.isAberta()) {
				FrequenciaMov mov = new FrequenciaMov(frequencias);
				mov.setUsuarioLogado(getUsuarioLogado());

				if (frequencia.getStatus() == 1)
					mov.setCodMovimento(SigaaListaComando.LANCAR_FREQUENCIA);
				else if (frequencia.getStatus() == 0)
					mov.setCodMovimento(SigaaListaComando.REMOVER_FREQUENCIA);

				executarMovimento(mov);

				if (frequencia.getStatus() == 1)
					registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INSERIR, turma.getId());
				if (frequencia.getStatus() == 0)
					registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.REMOVER, turma.getId());


				// TODO retornar corretamente
				response = Response.ok().build();
			} else {
				response = Response.serverError().build();
			}
		} catch (DAOException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
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
			dao.close();
			daof.close();
		}

		return response;
	}

	/**
	 * Realiza o cadastro da frequência do dia
	 * 
	 * @param reader
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws IOException
	 */
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response cadastrarFrequencia(Reader reader) throws NegocioException, ArqException, IOException {
		FrequenciaDiaDocenteDTO frequencia;
		Response response = Response.noContent().build();
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		FrequenciaAlunoDao fdao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);

		try {
			List<FrequenciaAluno> frequencias = new ArrayList<FrequenciaAluno>();
			frequencia = parseFrequencia(reader);

			Turma turma = dao.findByPrimaryKey(frequencia.getIdTurma(), Turma.class, "situacaoTurma");
			turma.setId(frequencia.getIdTurma());
			Turma turmaSigaa = new Turma(turma.getId());
			turmaSigaa = dao.findAndFetch(turma.getId(), Turma.class, "horarios");
			turma.setHorarios(turmaSigaa.getHorarios());
			Date dataSelecionada = frequencia.getData();
			if (!fdao.diaTemFrequencia(dataSelecionada, turma)) {
				for (FrequenciaDTO f : frequencia.getFrequencias()) {
					FrequenciaAluno result = new FrequenciaAluno();

					result.setTurma(turma);
					result.setData(dataSelecionada);
					result.setFaltas(f.getFaltas());
					Discente d = new Discente();
					d.setId(f.getIdDiscente());
					result.setDiscente(d);

					frequencias.add(result);
				}

				// TODO Verificar necessidade da verificação
				if (dataSelecionada == null) {
					return null;
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				String data = dataSelecionada == null ? null : sdf.format(dataSelecionada);

				registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INICIAR_INSERCAO, turma.getId());

				// TODO Melhorar tratamento do caso
				if (dataSelecionada.after(new Date())) {
					return null;
				}

				if (turma.isAberta()) {
					FrequenciaMov mov = new FrequenciaMov(frequencias);
					mov.setUsuarioLogado(getUsuarioLogado());
					mov.setCodMovimento(SigaaListaComando.LANCAR_FREQUENCIA);

					executarMovimento(mov);

					registrarAcao(turma, data, EntidadeRegistroAva.FREQUENCIA, AcaoAva.INSERIR, turma.getId());

					response = Response.created(URI.create("/frequenciadiadocente" + "/" + frequencia.getId())).build();
				} else {
					response = Response.serverError().build();
				}
			} else {
				response = Response.notModified().build();
			}
		} catch (DAOException e) {
			if (!e.isNotificavel())
				response = montarResponseException(e);
			else
				throw e;
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
		} finally {
			if (dao != null) {
				dao.close();
			}
			if (fdao != null) {
				fdao.close();
			}
		}

		return response;
	}

	private int getMaxFaltas(Date data, Turma turma) throws DAOException {
		TurmaVirtualDao tvDao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		TopicoAulaDao topDao = DAOFactory.getInstance().getDAO(TopicoAulaDao.class);

		int maxFaltas = 0;

		try {
			int diaSemana = CalendarUtils.getDiaSemanaByData(data);

			for (HorarioTurma ht : turma.getHorarios()) {
				if (ht.getDataInicio().getTime() <= data.getTime() && ht.getDataFim().getTime() >= data.getTime() && diaSemana == Character
						.getNumericValue(ht.getDia())) {
					maxFaltas++;
				}
			}

			// Verificando se a aula foi cancelada
			TopicoAula ultimaAulaCancelada = null;
			List<TopicoAula> aulasCanceladas;

			aulasCanceladas = topDao.findTopicosSemAula(turma.getId(), data);

			if (!aulasCanceladas.isEmpty()) {
				maxFaltas = 0;
				ultimaAulaCancelada = aulasCanceladas.get(0);
			}

			// Verificar aulas extras
			List<AulaExtra> aulasExtra = tvDao.buscarAulasExtra(turma, data);

			if (aulasExtra != null) {
				for (AulaExtra aula : aulasExtra) {
					if (ultimaAulaCancelada == null || (ultimaAulaCancelada != null && aula.getCriadoEm().getTime() > ultimaAulaCancelada
							.getDataCadastro().getTime()))
						maxFaltas += aula.getNumeroAulas();
				}
			}
		} finally {
			if (tvDao != null) {
				tvDao.close();
			}
			if (topDao != null) {
				topDao.close();
			}
		}

		if (maxFaltas == 0)
			maxFaltas = 10;

		return maxFaltas;
	}

	/**
	 * Transforma o stream passado em {@link FrequenciaDiaDocenteDTO}.
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private FrequenciaDiaDocenteDTO parseFrequencia(Reader reader) throws IOException {
		String frequenciaJSON = readStream(reader);

		FrequenciaDiaDocenteDTO frequencia = JSONProcessor.toObject(frequenciaJSON, FrequenciaDiaDocenteDTO.class);

		return frequencia;
	}

}
