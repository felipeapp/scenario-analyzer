package br.ufrn.sigaa.mobile.resources;

import java.util.ArrayList;
import java.util.Collection;
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
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.sigaa.arq.dao.ensino.FrequenciaAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.AcaoAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.EntidadeRegistroAva;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.mobile.dto.AvaliacaoDTO;
import br.ufrn.sigaa.mobile.dto.MatriculaComponenteDTO;
import br.ufrn.sigaa.mobile.dto.NotaUnidadeDTO;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

@Path("/matriculacomponente")
public class MatriculaComponenteResource extends SigaaGenericResource {

	public MatriculaComponenteResource() {
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	//Versao inicial (Desconsiderando alunos EAD)
	public Response getMatriculaTurma(@QueryParam("ownerId") final Integer idTurma) throws ArqException, NegocioException {
		Response response = null;
		MatriculaComponenteDTO matriculaDto = new MatriculaComponenteDTO();

		request.getSession().setAttribute("tid", idTurma);

		TurmaDao dao = DAOFactory.getInstance().getDAO(TurmaDao.class);
		TurmaVirtualDao tDao = DAOFactory.getInstance().getDAO(TurmaVirtualDao.class);
		FrequenciaAlunoDao fDao = DAOFactory.getInstance().getDAO(FrequenciaAlunoDao.class);
		AvaliacaoDao aDao = DAOFactory.getInstance().getDAO(AvaliacaoDao.class);

		try {
			Turma turma = dao.findAndFetch(idTurma, Turma.class, "curso");

			matriculaDto.setId(turma.getId());

			Collection<MatriculaComponente> matriculas = null;

			registrarAcao(turma, null, EntidadeRegistroAva.NOTAS, AcaoAva.ACESSAR, turma.getId() );

			//Parametros associados a turma
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);

			//Matricula do aluno
			matriculas = dao.findMatriculasAConsolidar(turma.getId(), getDiscenteLogado().getId(), true);

			//Recuperando avaliacoes da turma
			List<Avaliacao> avaliacoes = new ArrayList<Avaliacao>();

			if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA) {
				if ( turma.isAgrupadora() )
					avaliacoes = aDao.findAvaliacoesByTurmaAgrupadora(turma);
				else
					avaliacoes = aDao.findAvaliacoes(turma);

				Collection<NotaUnidade> notas = aDao.findNotasByTurma(turma);
				ConfiguracoesAva config = tDao.findConfiguracoes(turma);

				for (MatriculaComponente matricula : matriculas) {
					if (config == null)
						matricula.setOcultarNota(false);
					else
						matricula.setOcultarNota(config.isOcultarNotas());

					int id = matricula.getDiscente().getCurso().getId();
					if (id != 0){
						if (!turma.isLato()){
							matricula.getDiscente().setCurso(dao.findByPrimaryKey(id, Curso.class, "id", "nivel"));
						} else {
							matricula.getDiscente().setCurso(dao.findByPrimaryKey(id, CursoLato.class, "id", "nivel", "propostaCurso"));
						}
					}

					EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
					EstrategiaConsolidacao estrategia = factory.getEstrategia(matricula.getDiscente(), param);

					matricula.setEstrategia(estrategia);

					matriculaDto.setFaltas(matricula.getNumeroFaltas());
					
					if (!matricula.getOcultarNotas()) {
						matriculaDto.setSituacao(matricula.getSituacaoMatricula().getId());

						List<NotaUnidade> notasMatricula = new ArrayList<NotaUnidade>();
						for ( NotaUnidade notaMatricula : notas) {
							if ( notaMatricula.getMatricula().getId() == matricula.getId() ) {
								notasMatricula.add(notaMatricula);
							}
						}

						matricula.setNotas(notasMatricula);

						String[] pesosAvaliacoes = param.getArrayPesosAvaliacoes();
						String[] pesoMediaPesoRec = param.getArrayPesosMediaRec();
						matricula.setPesoMedia(new Integer(pesoMediaPesoRec[0].trim()));
						matricula.setPesoRecuperacao(new Integer(pesoMediaPesoRec[1].trim()));

						int unidade = 0;
						for (NotaUnidade nota : matricula.getNotas()) {
							nota.setPeso(pesosAvaliacoes[unidade++].trim());
							ArrayList<Avaliacao> avaliacoesDessaNota = new ArrayList<Avaliacao>();
							List<AvaliacaoDTO> avs = new ArrayList<AvaliacaoDTO>();

							for ( Avaliacao av : avaliacoes ) {
								if ( av.getUnidade().getId() == nota.getId() ) {
									avaliacoesDessaNota.add(av);
									AvaliacaoDTO aDTO = new AvaliacaoDTO();
									aDTO.setAbreviacao(av.getAbreviacao());
									aDTO.setId(av.getId());
									aDTO.setNota(av.getNota());
									aDTO.setPeso(av.getPeso());
									avs.add(aDTO);
								}
							}

							nota.setAvaliacoes(avaliacoesDessaNota);
							NotaUnidadeDTO n = new NotaUnidadeDTO();
							n.setId(nota.getId());
							n.setMedia(nota.getNotaPreenchida());
							n.setRecuperacao(nota.isRecuperacao());
							n.setUnidade(nota.getUnidade());
							n.setPeso(Float.parseFloat(nota.getPeso()));
							n.setAvaliacoes(avs);

							matriculaDto.getNotasUnidade().add(n);
						}

						NotaUnidadeDTO n = new NotaUnidadeDTO();
						
						n.setId(matricula.getId());
						n.setMedia(matricula.getRecuperacao());
						n.setRecuperacao(true);
						n.setUnidade(0);
						n.setPeso((matricula.getPesoRecuperacao()*100)/(matricula.getPesoMedia()+matricula.getPesoRecuperacao()));
						n.setAvaliacoes(null);

						matriculaDto.getNotasUnidade().add(n);
						matriculaDto.setMediaFinal(matricula.getMediaPreenchida());
					}
				}
			} else {
				for (MatriculaComponente matricula : matriculas) {
					matricula.setMetodoAvaliacao(param.getMetodoAvaliacao());
					matricula.setComponente(turma.getDisciplina());
					matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());

					matriculaDto.setConceito(param.getMetodoAvaliacao() == MetodoAvaliacao.CONCEITO);
					matriculaDto.setFaltas(matricula.getNumeroFaltas());
					matriculaDto.setSituacao(matricula.getSituacaoMatricula().getId());

					List<NotaUnidadeDTO> a = new ArrayList<NotaUnidadeDTO>();

					matriculaDto.setNotasUnidade(a);
					matriculaDto.setMediaFinal(matricula.getMediaFinal());
				}
			}
			
			response = Response.ok(JSONProcessor.toJSON(matriculaDto)).build();
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
			dao.close();
			fDao.close();
			aDao.close();
			tDao.close();
		}

		return response;
	}

}
