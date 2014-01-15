/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/12/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.vestibular;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.ConvocacaoVestibularDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;

/** Estratégia de convocação de candidatos aprovados no vestibular, com reserva de cotas.
 * @author Édipo Elder F. de Melo
 *
 */
public class EstrategiaConvocacaoCandidatosVestibular2013 implements EstrategiaConvocacaoCandidatosVestibular {
	
	/**
	 * Método a ser invocado pelo caso de uso para processar as covocações de
	 * discentes aprovados segundo as regras definidas.
	 * @see br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular#findOfertaComVagasRemanescentes(br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular, br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao)
	 */
	@Override
	public Map<OfertaVagasCurso, Integer> findOfertaComVagasRemanescentes(ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestreConvocacao, boolean incluirPreCadastrado, boolean incluirPendenteCadastro) throws DAOException{
		Map<OfertaVagasCurso, Integer> mapa;
		ConvocacaoVestibularDao dao =  DAOFactory.getInstance().getDAO(ConvocacaoVestibularDao.class);
		try {
			mapa = dao.findOfertaComVagasRemanescentes(processoSeletivo, semestreConvocacao, incluirPreCadastrado, incluirPendenteCadastro);
		} finally {
			dao.close();
		}
		return mapa;
	}
	
	/**
	 * Retorna um mapa de ofertas de cotas em curso que possuem vagas não
	 * preenchidas. A busca por estas vagas pode variar conforme a estratégia de
	 * convocação.
	 * @see br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular#findCotasComVagasRemanescentes(br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular, br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao)
	 */
	@Override
	public Map<CotaOfertaVagaCurso, Integer> findCotasComVagasRemanescentes(ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestreConvocacao, boolean incluirPreCadastrado, boolean incluirPendenteCadastro) throws HibernateException, DAOException{
		Map<CotaOfertaVagaCurso, Integer>mapa;
		ConvocacaoVestibularDao dao =  DAOFactory.getInstance().getDAO(ConvocacaoVestibularDao.class);
		try {
			mapa = dao.findCotasComVagasRemanescentes(processoSeletivo, semestreConvocacao, incluirPreCadastrado, incluirPendenteCadastro);
		} finally {
			dao.close();
		}
		return mapa;
		
	}
	
	/**
	 * Método a ser invocado pelo caso de uso para processar as covocações de
	 * discentes aprovados segundo as regras definidas.
	 * @see br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular#convocarCandidatos(java.util.Map, java.util.Map, br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao, br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular)
	 */
	@Override
	public void convocarCandidatos(
			List<ConvocacaoProcessoSeletivoDiscente> convocacoes, Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes,
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes,
			SemestreConvocacao semestreConvocacao,
			ProcessoSeletivoVestibular processoSeletivo, boolean dentroNumeroVagas) throws DAOException {
		// uma convocação de segunda para primeira opção poderá gerar uma vaga em matriz já processada,
		// daí o laço para processar até que não haja mais convocações
		int nAnterior = 0;
		int ordemOpcao = 1;
		do {
			nAnterior = convocacoes.size();
			// convoca cotistas 
			convocaCotasOpcao(ofertaComVagasRemanescentes, cotasComVagasRemanescentes, semestreConvocacao, processoSeletivo, convocacoes, ordemOpcao, dentroNumeroVagas);
			// convoca cotistas para cotas remanescentes
			convocaCotasRemanescentesOpcao(ofertaComVagasRemanescentes, cotasComVagasRemanescentes, semestreConvocacao, processoSeletivo, convocacoes, ordemOpcao, dentroNumeroVagas);
			// convoca candidatos aprovados para as vagas gerais cal 
			convocaClassificadoGeralOpcao(ofertaComVagasRemanescentes, cotasComVagasRemanescentes, semestreConvocacao, processoSeletivo, convocacoes, ordemOpcao, dentroNumeroVagas);
			// verifica se ainda há vagas
		} while (nAnterior != convocacoes.size());
	}
	
	/** Convoca os candidatos concorrentes à amplas vagas classificados para uma oferta de curso. 
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes
	 * @param semestreConvocacao
	 * @param processoSeletivo
	 * @param convocacoes
	 * @param opcao
	 * @param dentroNumeroVagas 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void convocaClassificadoGeralOpcao(
			Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes,
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes,
			SemestreConvocacao semestreConvocacao,
			ProcessoSeletivoVestibular processoSeletivo,
			List<ConvocacaoProcessoSeletivoDiscente> convocacoes,
			int opcao, boolean dentroNumeroVagas) throws HibernateException, DAOException {
		ConvocacaoVestibularDao dao = DAOFactory.getInstance().getDAO(ConvocacaoVestibularDao.class);
		try {
			for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
				int vagasAConvocar = ofertaComVagasRemanescentes.get(oferta);
				if (vagasAConvocar > 0) {
					System.out.println("Ampla Concorrência: " + oferta.getMatrizCurricular().getDescricao());
					Collection<ResultadoOpcaoCurso> resultados = dao.resultadosClassificadosOpcao(oferta, vagasAConvocar, processoSeletivo, semestreConvocacao, convocacoes, opcao);
					if (!isEmpty(resultados)) {
						// verifica se há cotista dentro do número de vagas (mérito)
						Iterator<ResultadoOpcaoCurso> iterator = resultados.iterator();
						while (iterator.hasNext()) {
							ResultadoOpcaoCurso resultadoOpcaoCurso = iterator.next();
							// verifica se já tem convocação no processamento 
							for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
								if (convocacao.getInscricaoVestibular().getId() == resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId()) {
									iterator.remove(); break;
								}
							}
						}
						// cria as convocações e atualiza o número de vagas
						iterator = resultados.iterator();
						while (iterator.hasNext() && vagasAConvocar > 0) {
							ResultadoOpcaoCurso resultadoOpcaoCurso = iterator.next();
							// seta os dados completos da matriz, já que não vem na busca resultadosOpcaoCursoClassificados
							resultadoOpcaoCurso.setMatrizCurricular(oferta.getMatrizCurricular());
							ConvocacaoProcessoSeletivoDiscente convocacao;
							boolean segundoPeriodo = false;
							if (processoSeletivo.isEntradaDoisPeriodos() && oferta.getVagasPeriodo2() > 0
									|| !processoSeletivo.isEntradaDoisPeriodos() && processoSeletivo.getPeriodoEntrada() == 2) 
								segundoPeriodo = true;
							if (opcao == 1)
								convocacao = criarConvocacao(resultadoOpcaoCurso, processoSeletivo, TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, segundoPeriodo);
							else 
								convocacao = criarConvocacao(resultadoOpcaoCurso, processoSeletivo, TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO, segundoPeriodo);
							convocacao.setDentroNumeroVagas(dentroNumeroVagas);
							// Se já possui uma convocação anterior, cria um cancelamento para essa convocação anterior.
							// caso já esteja matriculado, não será gerado o cancelamento e o vínculo será ativado após o cancelamento da convocação anterior
							if(resultadoOpcaoCurso.getConvocacaoAnterior() != null){
								criaCancelamento(processoSeletivo, resultadoOpcaoCurso.getConvocacaoAnterior(), TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, semestreConvocacao, ofertaComVagasRemanescentes);
							}
							if (convocacao != null) {
								convocacoes.add(convocacao);
								vagasAConvocar--;
							}
						}
					}
					ofertaComVagasRemanescentes.put(oferta, vagasAConvocar);
				}
			}
		} finally {
			dao.close();
		}
	}

	/** Convoca os candidatos concorrentes à amplas vagas classificados para uma oferta de curso, que escolheram a oferta como primeira opção.
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes
	 * @param semestreConvocacao
	 * @param processoSeletivo
	 * @param convocacoes
	 * @param dentroNumeroVagas 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void convocaCotasRemanescentesOpcao(
			Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes,
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes,
			SemestreConvocacao semestreConvocacao,
			ProcessoSeletivoVestibular processoSeletivo, List<ConvocacaoProcessoSeletivoDiscente> convocacoes,
			int ordemOpcao, boolean dentroNumeroVagas) throws HibernateException, DAOException {
		ConvocacaoVestibularDao dao = DAOFactory.getInstance().getDAO(ConvocacaoVestibularDao.class);
		try {
			for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
				int vagas = ofertaComVagasRemanescentes.get(oferta);
				if (vagas > 0) {
					System.out.println("Remanejamento entre cotas: " + oferta.getMatrizCurricular().getDescricao());
					// determina se todas as vagas nos grupos de contas estão preechidas
					Map<CotaOfertaVagaCurso, Integer> cotasRemanescentes = filtraCotas(cotasComVagasRemanescentes, oferta);
					for (CotaOfertaVagaCurso cota : cotasRemanescentes.keySet()) {
						int vagasCota = cotasRemanescentes.get(cota);
						if (vagasCota > 0) {
							// busca os resultados de opção de curso dentro do número de vagas das cotas
							for (GrupoCotaVagaCurso grupoCota : cota.getGrupoCota().getOrdemChamadaVagasRemanescentes()) {
								Collection<ResultadoOpcaoCurso> resultados = dao.resultadosOpcaoCursoClassificadosCota(oferta, grupoCota, vagasCota, processoSeletivo, semestreConvocacao, convocacoes, ordemOpcao);
								if (resultados != null && vagasCota > 0 && vagas > 0) {
									// cria as convocações e atualiza o número de vagas
									for (ResultadoOpcaoCurso resultadoOpcaoCurso : resultados) {
										// seta os dados completos da matriz, já que não vem na busca resultadosOpcaoCursoClassificados
										resultadoOpcaoCurso.setMatrizCurricular(oferta.getMatrizCurricular());
										boolean segundoPeriodo = false;
										if (processoSeletivo.isEntradaDoisPeriodos() && oferta.getVagasPeriodo2() > 0
												|| !processoSeletivo.isEntradaDoisPeriodos() && processoSeletivo.getPeriodoEntrada() == 2) 
											segundoPeriodo = true;
										ConvocacaoProcessoSeletivoDiscente convocacao = criarConvocacao(resultadoOpcaoCurso, processoSeletivo, TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, segundoPeriodo);
										convocacao.setDentroNumeroVagas(dentroNumeroVagas);
										convocacao.setGrupoCotaConvocado(cota.getGrupoCota());
										convocacao.setGrupoCotaRemanejado(true);
										// Se já possui uma convocação anterior, cria um cancelamento para essa convocação anterior.
										// caso já esteja matriculado, não será gerado o cancelamento e o vínculo será ativado após o cancelamento da convocação anterior
										if(resultadoOpcaoCurso.getConvocacaoAnterior() != null){
											criaCancelamento(processoSeletivo, resultadoOpcaoCurso.getConvocacaoAnterior(), TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, semestreConvocacao, ofertaComVagasRemanescentes);
										}
										if (convocacao != null) {
											convocacoes.add(convocacao);
											vagas--;
											vagasCota--;
										}
									}
								}
							}
							cotasComVagasRemanescentes.put(cota, vagasCota);
						}
					}
					ofertaComVagasRemanescentes.put(oferta, vagas);
				}
			}
		} finally {
			dao.close();
		}
	}

	/** Convoca os candidatos concorrentes à cotas de uma oferta de curso, que escolheram a oferta como primeira opção.
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes
	 * @param semestreConvocacao
	 * @param processoSeletivo
	 * @param convocacoes
	 * @param dentroNumeroVagas 
	 * @throws DAOException
	 */
	private void convocaCotasOpcao(
			Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes,
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes, 
			SemestreConvocacao semestreConvocacao,
			ProcessoSeletivoVestibular processoSeletivo, List<ConvocacaoProcessoSeletivoDiscente> convocacoes,
			int ordemOpcao, boolean dentroNumeroVagas) throws DAOException {
		ConvocacaoVestibularDao dao = DAOFactory.getInstance().getDAO(ConvocacaoVestibularDao.class);
		try {
			for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
				int vagas = ofertaComVagasRemanescentes.get(oferta);
				if (vagas > 0) {
					System.out.println("Cota: " + oferta.getMatrizCurricular().getDescricao());
					// determina se todas as vagas nos grupos de contas estão preechidas
					Map<CotaOfertaVagaCurso, Integer> cotasRemanescentes = filtraCotas(cotasComVagasRemanescentes, oferta);
					for (CotaOfertaVagaCurso cota : cotasRemanescentes.keySet()) {
						int vagasCota = cotasRemanescentes.get(cota);
						if (vagasCota > 0) {
							// busca os resultados de opção de curso dentro do número de vagas das cotas
							Collection<ResultadoOpcaoCurso> resultados = dao.resultadosOpcaoCursoClassificadosCota(oferta, cota.getGrupoCota(), vagasCota, processoSeletivo, semestreConvocacao, convocacoes, ordemOpcao);
							if (resultados != null) {
								// cria as convocações e atualiza o número de vagas
								for (ResultadoOpcaoCurso resultadoOpcaoCurso : resultados) {
									// seta os dados completos da matriz, já que não vem na busca resultadosOpcaoCursoClassificados
									resultadoOpcaoCurso.setMatrizCurricular(oferta.getMatrizCurricular());
									ConvocacaoProcessoSeletivoDiscente convocacao;
									boolean segundoPeriodo = false;
									if (processoSeletivo.isEntradaDoisPeriodos() && oferta.getVagasPeriodo2() > 0
											|| !processoSeletivo.isEntradaDoisPeriodos() && processoSeletivo.getPeriodoEntrada() == 2) 
										segundoPeriodo = true;
									if (ordemOpcao == 1)
										convocacao = criarConvocacao(resultadoOpcaoCurso, processoSeletivo, TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, segundoPeriodo);
									else
										convocacao = criarConvocacao(resultadoOpcaoCurso, processoSeletivo, TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO, segundoPeriodo);
									convocacao.setDentroNumeroVagas(dentroNumeroVagas);
									convocacao.setGrupoCotaConvocado(cota.getGrupoCota());
									// Se já possui uma convocação anterior, cria um cancelamento para essa convocação anterior.
									// caso já esteja matriculado, não será gerado o cancelamento e o vínculo será ativado após o cancelamento da convocação anterior
									if(resultadoOpcaoCurso.getConvocacaoAnterior() != null){
										criaCancelamento(processoSeletivo, resultadoOpcaoCurso.getConvocacaoAnterior(), TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO, semestreConvocacao, ofertaComVagasRemanescentes);
									}
									if (convocacao != null) {
										convocacoes.add(convocacao);
										vagas--;
										vagasCota--;
									}
								}
							}
							cotasComVagasRemanescentes.put(cota, vagasCota);
						}
					}
					ofertaComVagasRemanescentes.put(oferta, vagas);
				}
			}
		} finally {
			dao.close();
		}
	}

	/** Retorna um mapa contendo as cotas de oferta de vagas de um curso.
	 * @param cotasComVagasRemanescentes
	 * @param oferta
	 * @return
	 */
	private Map<CotaOfertaVagaCurso, Integer> filtraCotas(
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes,
			OfertaVagasCurso oferta) {
		Map<CotaOfertaVagaCurso, Integer> filtrado = new LinkedHashMap<CotaOfertaVagaCurso, Integer>();
		for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
			if (cota.getOfertaVagasCurso().getId() == oferta.getId()) {
				filtrado.put(cota, cotasComVagasRemanescentes.get(cota));
			}
		}
		return filtrado;
	}

	/**
	 * Gera uma convocação do tipo informado a partir das informações contidas
	 * no resultado do candidato.
	 * 
	 * @param resultadoOpcaoCurso
	 * @param tipo
	 * @throws DAOException 
	 */
	private ConvocacaoProcessoSeletivoDiscente criarConvocacao(ResultadoOpcaoCurso resultadoOpcaoCurso, ProcessoSeletivoVestibular processoSeletivo, TipoConvocacao tipo, boolean segundoPeriodo) throws DAOException {
		ConvocacaoProcessoSeletivoDiscente cpsd = new ConvocacaoProcessoSeletivoDiscente();
		cpsd.setInscricaoVestibular(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular());
		cpsd.setResultado(resultadoOpcaoCurso);
		cpsd.setTipo(tipo);
		
		Pessoa pessoa = new Pessoa();
		pessoa.setCpf_cnpj(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa().getCpf_cnpj());
		pessoa.setNome(resultadoOpcaoCurso.getResultadoClassificacaoCandidato().getInscricaoVestibular().getPessoa().getNome());
		
		DiscenteGraduacao discente = resultadoOpcaoCurso.getConvocacaoAnterior() == null 
									? new DiscenteGraduacao() 
									: copiaDiscente(resultadoOpcaoCurso.getConvocacaoAnterior().getDiscente(), tipo);
		discente.setPessoa(pessoa);
		discente.setAnoIngresso( processoSeletivo.getAnoEntrada() );
		// Se a convocação é uma mudança de semestre, o período de ingresso é sempre o 1º período.
		if(tipo.equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE))
			discente.setPeriodoIngresso(1);
		else
			discente.setPeriodoIngresso(segundoPeriodo ? 2 : 1 );
		discente.setMatrizCurricular( resultadoOpcaoCurso.getMatrizCurricular() );
		discente.setCurso( resultadoOpcaoCurso.getMatrizCurricular().getCurso() );
		discente.setNivel( NivelEnsino.GRADUACAO );
		discente.setStatus(StatusDiscente.PENDENTE_CADASTRO);
		discente.setTipo(Discente.REGULAR);
		discente.setFormaIngresso(processoSeletivo.getFormaIngresso());
		
		cpsd.setDiscente(discente);
		cpsd.setConvocacaoAnterior(resultadoOpcaoCurso.getConvocacaoAnterior());
		return cpsd;
	}
	
	/** Cria um cancelamento de uma convocação anterior do candidato.
	 * @param processoSeletivo
	 * @param convocacao
	 * @param tipo
	 * @param semestreConvocacao
	 * @param ofertaComVagasRemanescentes
	 * @throws DAOException
	 */
	private void criaCancelamento(ProcessoSeletivoVestibular processoSeletivo, ConvocacaoProcessoSeletivoDiscente convocacao, TipoConvocacao tipo, SemestreConvocacao semestreConvocacao, Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes) throws DAOException {
		MatriculaComponenteDao matriculaDao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class);
		CancelamentoConvocacao cancelamento = null;
		try {
			Collection<MatriculaComponente> matriculasDiscente = matriculaDao.findByDiscenteOtimizado(convocacao.getDiscente(), TipoComponenteCurricular.getAll(), SituacaoMatricula.getSituacoesTodas());
			boolean cancela = true;
			if (!isEmpty(matriculasDiscente)) {
				for (MatriculaComponente mc : matriculasDiscente) {
					if (!SituacaoMatricula.getSituacoesNegativas().contains(mc.getSituacaoMatricula()))
						cancela = false; 
				}
			}
			if (cancela) {
				if(convocacao.getDiscente().getStatus() == StatusDiscente.EXCLUIDO)
					return;
				MotivoCancelamentoConvocacao motivo = null;
				if (tipo == TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO) 
					motivo = MotivoCancelamentoConvocacao.RECONVOCACAO_PRIMEIRA_OPCAO; 
				cancelamento = new CancelamentoConvocacao();
				cancelamento.setMotivo(motivo);
				cancelamento.setConvocacao(convocacao);
				
				// O cancelamento gerado abre uma vaga na matriz da qual o aluno saiu.
				// somente se a convocação for para os dois períodos.
				if (semestreConvocacao != SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE) {
					int idMatriz = convocacao.getDiscente().getMatrizCurricular().getId();
					boolean atualizado = false;
					for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
						if (oferta.getMatrizCurricular().getId() == idMatriz) {
							int vagas = ofertaComVagasRemanescentes.get(oferta); 
							ofertaComVagasRemanescentes.put(oferta, ++vagas);
							atualizado = true;
							break;
						}
					}
					// o remanjamento do discente abriu vaga em uma oferta em que todas as vagas estavam preenchidas
					if (!atualizado) {
						String[] fields = {"ano", "formaIngresso.id", "matrizCurricular.id"};
						Object[] values = {processoSeletivo.getAnoEntrada(), 
								processoSeletivo.getFormaIngresso().getId(),
								idMatriz};
						Collection<OfertaVagasCurso> ofertas = matriculaDao.findByExactField(OfertaVagasCurso.class, fields, values);
						// teoricamente, só há uma oferta
						OfertaVagasCurso oferta = ofertas.iterator().next();
						ofertaComVagasRemanescentes.put(oferta, 1);
					}
				}
			} else {
				convocacao.setPendenteCancelamento(true);
				// como será pendente do cancelamento, será criado um novo discente, e não mantêm o ID do discente da convocação anterior
				convocacao.getDiscente().setId(0);
				convocacao.getDiscente().getDiscente().setId(0);
			}
		} finally {
			matriculaDao.close();
		}
		convocacao.setCancelamento(cancelamento);
	}
	
	/**
	 * Cria uma cópia do conteúdo do objeto {@link DiscenteGraduacao discente} passado retornando uma nova referência.
	 * 
	 * @param discente
	 * @return
	 */
	private DiscenteGraduacao copiaDiscente(DiscenteGraduacao discente, TipoConvocacao tipo) {
		DiscenteGraduacao copia = new DiscenteGraduacao(discente.getId(), discente.getMatricula(), discente.getNome());
		
		if(!tipo.equals(TipoConvocacao.CONVOCACAO_MUDANCA_SEMESTRE)){
			MatrizCurricular matrizCopia = new MatrizCurricular(discente.getMatrizCurricular().getId());
			matrizCopia.setCurso(new Curso(discente.getMatrizCurricular().getCurso().getId()));
			matrizCopia.setTurno(new Turno(discente.getMatrizCurricular().getTurno().getId()));
			matrizCopia.setGrauAcademico(new GrauAcademico(discente.getMatrizCurricular().getGrauAcademico().getId()));
			matrizCopia.setHabilitacao(discente.getMatrizCurricular().getHabilitacao() != null ? new Habilitacao(discente.getMatrizCurricular().getHabilitacao().getId()) : null);
			
			copia.setMatrizCurricular(matrizCopia);
		}
		return copia;
	}

	/**
	 * Retorna uma descrição curta desta estratégia. Esta descrição é utilizada,
	 * por exemplo, em combobox (select) na interface para associar a estratégia
	 * ao processo seletivo.
	 * @see br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular#getDescricaoCurta()
	 */
	@Override
	public String getDescricaoCurta() {
		return "Convocação de aprovados por Cotas (Lei 12.711, de 29 de agosto de 2012).";
	}

	/**
	 * Retorna uma descrição completa desta estratégia. Esta descrição é
	 * utilizada, por exemplo, em listagens detalhadas e relatórios.
	 * @see br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular#getDescricaoDetalhada()
	 */
	@Override
	public String getDescricaoDetalhada() {
		return "Convocação de Candidatos aprovados obedecendo às regras definidas pela Lei 12.711, de 29 de agosto de 2012 (Lei de Cotas).";
	}
}
