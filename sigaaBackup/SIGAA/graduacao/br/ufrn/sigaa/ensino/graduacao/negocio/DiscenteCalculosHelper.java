/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/05/2008
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.GrupoOptativasDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.AtualizacaoMediaDiscente;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.AlteracaoDiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.AtualizarIndicesDiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.AtualizarPrazosPerfilDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.AtualizarTotaisIntegralizados;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalcularStatusDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalcularTiposIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalculoPrazoMaximoFactory;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.PerfilInicialFactory;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar que contém métodos para cálculos sobre discentes de graduação.
 *
 *
 * @author André, Gleydson
 *
 */
public class DiscenteCalculosHelper {

	/**
	 * Método que chama o chain of responsibility para realizar
	 * os cálculos de discentes de acordo com o novo regulamento
	 * dos cursos de graduação.
	 *
	 * @param d - discente que será calculado
	 * @param mov - movimento que originou a mudança nos cálculos
	 *
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static void atualizarTodosCalculosDiscente(DiscenteGraduacao dg, Movimento mov ) throws ArqException, NegocioException {
		if (dg.getTipo() == Discente.ESPECIAL) return;

		CalculosDiscenteChainNode<DiscenteGraduacao> calculos = new CalculosDiscenteChainNode<DiscenteGraduacao>();
		
		if  ( StatusDiscente.getStatusComVinculo().contains(dg.getStatus()) ) {
			// Caso o aluno esteja com seu vínculo ativo, realizar todos os cálculos
			
			calculos.setNext(new CalcularTiposIntegralizacao()).
				setNext(new AtualizarTotaisIntegralizados()).
				setNext(new CalcularStatusDiscente()).
				setNext(new AtualizarIndicesDiscenteGraduacao()).
				setNext(new AtualizarPrazosPerfilDiscente());
		} else if ( dg.getStatus() == StatusDiscente.CONCLUIDO ) {
			// Caso o aluno esteja concluído, atualizar somente seu IRA
			calculos.setNext(new AtualizarIndicesDiscenteGraduacao());
		} else {
			throw new NegocioException("Só é permitido re-calcular discentes ativos, formandos, graduandos, trancados e concluídos");
		}
		
		calculos.executar(dg, mov, false);		
	}
	
	/**
	 * Método que atualiza tipos de integralizações, totais integralizados, status do discente e IRA.
	 *
	 * @param d - discente que será calculado
	 * @param mov - movimento que originou a mudança nos cálculos
	 *
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Deprecated
	public static void atualizarTodosCalculosDiscenteAntigo(DiscenteGraduacao d, Movimento mov ) throws ArqException, NegocioException {
		if (d.getTipo() == Discente.ESPECIAL) return;

		if  ( StatusDiscente.getStatusComVinculo().contains(d.getStatus()) ) {
			// Caso o aluno esteja com seu vínculo ativo, realizar todos os cálculos
			atualizarTiposIntegralizacaoMatriculas(d, mov);
			atualizarTotaisIntegralizados(d, mov);
			atualizarStatusDiscente(d, mov,false);
			atualizarIRA(d, mov);
			atualizarPeriodoAtual(d, mov);

			PerfilInicialFactory.getPerfilInicial(d).calcular(d, mov);

		} else if ( d.getStatus() == StatusDiscente.CONCLUIDO ) {
			// Caso o aluno esteja concluído, atualizar somente seu IRA
			atualizarIRA(d, mov);
		} else {
			throw new NegocioException("Só permitido re-calcular discentes ativos, formandos, graduandos, trancados e concluídos");
		}
		
		atualizarPrazoMaximo(d, mov);
		
	}

	/**
	 * Atualiza o período atual do discente através de um cálculo com base no ano-período atual.
	 * 
	 * @param d
	 * @param mov
	 * @throws ArqException
	 */
	public static void atualizarPeriodoAtual(DiscenteGraduacao d, Movimento mov) throws ArqException {
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		try {
			CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			int periodoAtual = dao.calculaPeriodoAtualDiscente(d, calendario.getAno(), calendario.getPeriodo());
			dao.updateField(Discente.class, d.getId(), "periodoAtual", periodoAtual);
		} finally {
			dao.close();
		}
	}

	/**
	 * Atualiza os dados do discente, caso seja um discente regular e que possua vínculo, para realizar
	 * o pré processamento de matrículas.
	 * 
	 * @param d
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static void atualizaDadosPreProcessamento(DiscenteGraduacao d, Movimento mov ) throws ArqException, NegocioException {

		if (d.getTipo() == Discente.ESPECIAL) return;

		if (d.isRegular() && StatusDiscente.getStatusComVinculo().contains(d.getStatus())) {
			atualizarTiposIntegralizacaoMatriculas(d, mov);
			atualizarTotaisIntegralizados(d, mov);
			atualizarStatusDiscente(d, mov, true );
		}

	}


	/**
	 * Atualiza os tipos de integralizações das matrículas e o status do discente
	 * @param d
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Deprecated
	public static void atualizarStatusDiscenteETiposIntegralizacao(DiscenteGraduacao d, Movimento mov) throws ArqException, NegocioException {
		
		if (d.getTipo() == Discente.ESPECIAL) return;

		if (d.isRegular() && StatusDiscente.getStatusComVinculo().contains(d.getStatus())) {
			atualizarTiposIntegralizacaoMatriculas(d, mov);
			atualizarStatusDiscente(d, mov,false);
		} else {
			throw new NegocioException("Só permitido re-calcular discentes ativos, formandos, graduandos e trancados");
		}
	}

	/**
	 * Atualiza os dados da consolidação do discente. Um cálculo é feito para saber o quanto
	 * já foi cursado e o quanto falta para cursar. 
	 * 
	 * @param d
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static void atualizarConsolidacao(DiscenteGraduacao d, Movimento mov) throws ArqException, NegocioException {
		
		if (d.getTipo() == Discente.ESPECIAL) return;

		if (d.isRegular() && StatusDiscente.getStatusComVinculo().contains(d.getStatus())) {
			atualizarTotaisIntegralizados(d, mov);
			atualizarStatusDiscente(d, mov,false);
			atualizarIRA(d, mov);
		} else {
			throw new NegocioException("Só permitido recalcular discentes ativos, formandos, graduandos e trancados");
		}
	}

	/**
	 * Atualiza o valor do IRA em DiscenteGraduacao
	 *
	 * @param discente
	 * @param mov 
	 * @throws DAOException
	 */
	public static void atualizarIRA(DiscenteGraduacao discente, Movimento mov)
			throws DAOException {
		IndiceAcademicoDao dao = getDAO(IndiceAcademicoDao.class, mov);
		try {
			float ira = (float) dao.calculaIraDiscente(discente.getId());
			dao.updateField(DiscenteGraduacao.class, discente.getId(), "ira",
					ira);
			/* registra a atualização do IRA */
			AtualizacaoMediaDiscente update = new AtualizacaoMediaDiscente();
			update.setData(new Date());
			update.setDiscente(discente.getDiscente());
			update.setMedia(ira);
			dao.create(update);
			
		} finally {
			dao.close();
		}
	}

	/**
	 * Recalcula e altera status do discente para GRADUANDO ou FORMANDO com base
	 * no total de créditos e CH integralizados CUIDADO! ao final desse método
	 * os totais integralizados do discente podem estar alterados.
	 *
	 * @param preProcessamento - Indica se o status não deve ser atualizado pois está 
	 * 							 realizando cálculos do pré-processamento da rematrícula
	 * @throws ArqException
	 */
	private static void atualizarStatusDiscente(DiscenteGraduacao d,
			Movimento mov, boolean preProcessamento ) throws ArqException {

		/* variável que controla a quantidade de componentes do currículo que estão pendentes que o aluno ainda não se matriculou */
		short pendenciasMatricula = 0;
		
		if (StatusDiscente.getAtivos().contains(d.getStatus())) {	
			DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class, mov);
			DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
			try {
				int status = d.getStatus();
				boolean haMatriculasNaoConsolidadas = ddao.existemMatriculasNaoConsolidadas(d.getId());
				// se já pagou no mínimo todas as obrigatórias e as complementares
				// mínimas
				
				// ainda podem existir registros com esse atributo nulo.
				if (d.getTotalAtividadesPendentes() == null) {
					List<MatriculaComponente> disciplinas = ddao.findDisciplinasConcluidasMatriculadas(d.getId(), true);
					Collection<ComponenteCurricular> componentesPendentes = ddao.findByDisciplinasCurricularesPendentes(d.getId(), disciplinas, new ArrayList<TipoGenerico>());
					// total de pendências
					short pendentes = (short) componentesPendentes.size();
					
					for (ComponenteCurricular c : componentesPendentes) {
						if (!c.isMatriculado() && !c.isMatriculadoEmEquivalente())
							pendenciasMatricula++;
					}
					d.setTotalAtividadesPendentes(pendentes);

				}

				if (d.getCrTotalPendentes() != null && d.getCrTotalPendentes() == 0 &&
						d.getChTotalPendente() != null && d.getChTotalPendente() == 0
						&& d.getChNaoAtividadeObrigPendente() == 0
						&& d.getChOptativaPendente() == 0 && d.getTotalAtividadesPendentes() == 0
						&& (d.getTotalGruposOptativasPendentes() == null || d.getTotalGruposOptativasPendentes() == 0) 
						&& !haMatriculasNaoConsolidadas) { 
					status = StatusDiscente.GRADUANDO;
				} else {
					// se as que está pagando são as que faltam pra integralizar
					// toda CH

					// apenas calcula as integralizações em DiscenteGraduacao com as
					// matrículas pagas (integralizadas) e
					// as que estão atualmente matriculadas
					calcularIntegralizacoes(d, SituacaoMatricula
							.getSituacoesPagasEMatriculadas());

					// com os totais re-calculados, o mesmo teste é feito ...
					if (d.getCrTotalPendentes() == 0 && d.getChAtividadeObrigPendente() == 0
							&& d.getChOptativaPendente() == 0 && pendenciasMatricula == 0 && d.getTotalGruposOptativasPendentes() == 0)
						status = StatusDiscente.FORMANDO;
					else
						status = StatusDiscente.ATIVO;
				}

				// se o status foi alterado, atualiza no banco
				if ( preProcessamento ) {
					boolean possivelFormando = (  status == StatusDiscente.FORMANDO );
					dgdao.updateField(DiscenteGraduacao.class, d.getId(), "possivelFormando",  possivelFormando);
					//TODO
					dgdao.getJdbcTemplate().update("insert into calculo_formando (id_discente, possivel_formando) values (?,?)", 
							new Object[] { d.getId(), possivelFormando });
				} else {
					if ( status != d.getStatus()) {
						DiscenteHelper.alterarStatusDiscente(d, status, mov, dgdao);
					}
				}
			} finally {
				dgdao.close();
				ddao.close();
			}
		}
	}

	/**
	 * Verifica se o componente curricular é do currículo do aluno ou de algum currículo do seu curso.
	 * 
	 * @param discente
	 * @param matricula
	 * @param componentes
	 * @return
	 */
	private static boolean calculaTipoIntegralizacao(DiscenteGraduacao discente, MatriculaComponente matricula, TreeSet<Integer> componentes) {

		if (matricula.getComponente().isSubUnidade()) {
			return componentes.contains(matricula.getComponente().getBlocoSubUnidade().getId());
		} else {
			Integer idMatricula = matricula.getComponente().getId();
			
			// testa se é do currículo do aluno ou de algum currículo do seu curso
			return componentes.contains(idMatricula);			
		}
	}

	/**
	 * Recalcula e atribui o tipo de integralização para todas as matrículas de
	 * um discente.
	 *
	 * @param discente
	 * @param mov
	 * @param mcdao
	 * @param dgdao
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private static void atualizarTiposIntegralizacaoMatriculas(DiscenteGraduacao discente, Movimento mov ) throws ArqException, NegocioException {

		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class, mov);
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);

		// conjunto de alterações das matrículas que devem ser realizadas no
		// final do método.
		// ao ser identificada uma alteração no registro de matriculaComponente
		// é criado e adicionado nessa coleção.
		Collection<AlteracaoMatricula> alteracoes = new ArrayList<AlteracaoMatricula>(0);

		// Pegar componentes obrigatórios do currículo (sem contar os blocos)
		TreeSet<Integer> componentesObrigCurriculo = dgdao.findComponentesDoCurriculoByDiscente(discente, true);
		
		// pegar todas as sub-unidades dos blocos obrigatórios e adicionar à lista
		TreeSet<Integer> componentesSubUnidadesObrigCurriculo = dgdao.findComponentesSubUnidadesDoCurriculoByDiscente(discente, true);
		componentesObrigCurriculo.addAll(componentesSubUnidadesObrigCurriculo);
		
		// Pegar componentes optativos do currículo (sem contar os blocos)
		TreeSet<Integer> componentesOptatCurriculo = dgdao.findComponentesDoCurriculoByDiscente(discente, false);
		
		// pegar todas as sub-unidades dos blocos obrigatórios e adicionar à lista
		TreeSet<Integer> componentesSubUnidadesOptatCurriculo = dgdao.findComponentesSubUnidadesDoCurriculoByDiscente(discente, false);
		componentesOptatCurriculo.addAll(componentesSubUnidadesOptatCurriculo);
		
		try {
			// loop das matrículas do aluno (pagas e matriculadas)
			Collection<MatriculaComponente> matriculas = mcdao .findPagaseMatriculadasByDiscente(discente);


//			 coleção de componentes que o discente já pagou (ou foram
			// integralizados, como aproveitamento, aprovação, ... )
			Collection<ComponenteCurricular> componentesPagosEMatriculados = ddao.findComponentesCurriculares(discente,SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);

			// verifica as matrículas que são obrigatórias da grade
			for (MatriculaComponente matricula : matriculas) {

				boolean obrigatoria = calculaTipoIntegralizacao(discente,matricula, componentesObrigCurriculo);

				if ( obrigatoria && !TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) ) {
					AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.OBRIGATORIA);

					matricula.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
					if (alteracao != null)
						alteracoes.add(alteracao);
				}
			}

			// coleção de componentes de todos os currículos do CURSO do aluno,
			// que o aluno ainda não integralizou
			Collection<CurriculoComponente> pendentesObrigatoriaCurriculo = ddao.findComponentesPendentesByDiscente(discente, true,true);


			// analisa as equivalentes a obrigatórias do curso
			for (CurriculoComponente ccPendente : pendentesObrigatoriaCurriculo) {

//				System.out.println( ccPendente.getComponente().getDescricao());
				String equivalencia = ccPendente.getComponente().getEquivalencia();
//				 só é necessário verificar caso o componente pendente tenha alguma
				// equivalência, senão é lógico
				// que o aluno não pagou nada equivalente a ele... MAS se a
				// equivalência estiver do lado do componente integralizado ?!?!?!

				if (equivalencia != null) {

					if (ccPendente.getObrigatoria()	&& ccPendente.getCurriculo().getId() == discente.getCurriculo().getId()) {
						// ... e se o aluno possui um conjunto de equivalentes
						// entre seus componentes integralizados.
						if (ExpressaoUtil.eval(equivalencia, componentesPagosEMatriculados)) {
							// seta nas matrículas dos componentes como equ_obr
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, componentesPagosEMatriculados);
							if (equivalentes != null) {
								for (ComponenteCurricular componente : equivalentes) {
									// pega a matrícula do equivalente
									MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
									AlteracaoMatricula alteracao = createAlteracaoMatricula(mat,mov,TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
									// verifica prioridade, seta caso seja qualquer outra coisa menos OBRIGATORIA
									if (!TipoIntegralizacao.OBRIGATORIA.equals(mat.getTipoIntegralizacao()) && alteracao != null) {
										mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
										alteracao.setExpressao(ArvoreExpressao.getMatchesExpression(equivalencia, componentesPagosEMatriculados));
										alteracoes.add(alteracao);
									}
								}
							}
						}
					}
				}
			}

			// analisa os componentes complementares
			for (MatriculaComponente matricula : matriculas) {

//				System.out.println( matricula.getComponente().getDescricao());
				boolean optativaGrade = calculaTipoIntegralizacao(discente,	matricula, componentesOptatCurriculo);
				//boolean optativaCurso = calculaTipoIntegralizacao(discente,	matrícula, componentesOptatCurriculoCurso);
				// só muda o tipo de integralização se não já tiver sido marcada como obrigatória ou equivalente a obrigatória
				if ( ( optativaGrade ) && !TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) &&
						!TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao() ) ) {

					String tipoIntegralizacao = null;
					if ( optativaGrade ) {
						tipoIntegralizacao = TipoIntegralizacao.OPTATIVA_DA_GRADE;
					} else {
						tipoIntegralizacao = TipoIntegralizacao.OPTATIVA_CURSO_CIDADE;
					}
//					System.out.println(tipoIntegralizacao);
					AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, tipoIntegralizacao);
					if (alteracao != null) {
						alteracoes.add(alteracao);
						matricula.setTipoIntegralizacao(tipoIntegralizacao);
					}
				}
			}

			// verificar equivalentes de optativas
			//	 coleção de componentes de todos os currículos do CURSO do aluno,
			// que o aluno ainda não integralizou
			Collection<CurriculoComponente> pendentesCurso = ddao.findComponentesPendentesByDiscente(discente, false, false);

			for (CurriculoComponente ccPendente : pendentesCurso) {

				String expressao = ccPendente.getComponente().getEquivalencia();

				if ( expressao != null ) {

					if (ExpressaoUtil.eval(ccPendente.getComponente()
							.getEquivalencia(), componentesPagosEMatriculados)) {

						// só entra aqui se o componente pendente possuir um conjunto
						// de equivalente entre seus componente integralizados.

						// se o componente for exatamente do currículo
						if (ccPendente.getCurriculo().getId() == discente.getCurriculo().getId()) {
							// pega os esses componentes que são equivalentes
							// ...
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(expressao, componentesPagosEMatriculados);
							// ... e pra cada um, identifica na matrícula que
							// ele está registrado, o tipo de integralização.
							if (equivalentes != null) {
								for (ComponenteCurricular componente : equivalentes) {
									MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
									AlteracaoMatricula alteracao = createAlteracaoMatricula(mat, mov, TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE);
									// mas só identifica para matrículas que ainda
									// não foram identificadas.
									// isso porque uma OBRG_DO_CURRIC pode fazer
									// parte dessa equivalência, e o tipo
									// setado antes deve prevalecer
									if (mat.getTipoIntegralizacao() == null &&  alteracao != null) {
										alteracao.setExpressao(ArvoreExpressao.getMatchesExpression(expressao, componentesPagosEMatriculados));
										mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE);
										alteracoes.add(alteracao);
									}
								}
							}
						}
					}

				}
			}

			// verificar extra-curriculares

			// as extra-curriculares serão todas matrículas que ainda não foram
			// identificadas
			for (MatriculaComponente matricula : matriculas) {
//				System.out.println( matricula.getComponente().getDescricao());
				// QC foi um tipo migrado do Ponto@, equivalente a optativa do curso, mas não foi usado no SIGAA pelo fato de não valer semanticamente
				if (matricula.getTipoIntegralizacao() == null || matricula.getTipoIntegralizacao().equals("QC")) {
					AlteracaoMatricula alteracao = createAlteracaoMatricula(
							matricula, mov, TipoIntegralizacao.EXTRA_CURRICULAR);
					if (alteracao != null)
						alteracoes.add(alteracao);
					// não precisa setar por que é persistido nas alterações
				}
			}


			mcdao.persistirAlteracoes(alteracoes);

		} finally {
			ddao.close();
			dgdao.close();
			mcdao.close();
		}
	}

	/**
	 * Cria um registro de alteração de situação de matrícula de um discente
	 * 
	 * @param matricula
	 * @param mov
	 * @param novoTipo
	 * @return
	 */
	private static AlteracaoMatricula createAlteracaoMatricula(MatriculaComponente matricula, Movimento mov, String novoTipo) {
		// só é criada uma instância de AlteracaoMatricula se o tipo de integer.
		// for realmente diferente
		if (novoTipo != null && novoTipo.equalsIgnoreCase(matricula.getTipoIntegralizacao())) {
			return null;
		}
		AlteracaoMatricula alteracao = new AlteracaoMatricula();
		alteracao.setMatricula(matricula);
		alteracao.setDataAlteracao(new Date());
		alteracao.setUsuario((Usuario) mov.getUsuarioLogado());
		alteracao.setCodMovimento(mov.getCodMovimento().getId());
		alteracao.setTipoIntegralizacaoAntigo(matricula.getTipoIntegralizacao());
		alteracao.setTipoIntegralizacaoNovo(novoTipo);
		alteracao.setSituacaoAntiga(matricula.getSituacaoMatricula());
		alteracao.setSituacaoNova(matricula.getSituacaoMatricula());

		return alteracao;
	}

	/**
	 * Atualiza os campos (em DiscenteGraduacao) dos créditos e CH
	 * integralizados pelo discente
	 *
	 * @param d
	 * @param dao
	 * @throws ArqException
	 */
	private static void atualizarTotaisIntegralizados(DiscenteGraduacao d, Movimento mov) throws ArqException {
		
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		dao.setUsuario(mov.getUsuarioLogado());
		
		try {
			
			calcularIntegralizacoes(d, SituacaoMatricula.getSituacoesPagas());
			dao.atualizaTotaisIntegralizados(d);

			registraAlteracaoCalculos(d.getId(), dao, mov);
			
		} finally {
			dao.close();
		}
	}


	/**
	 * Calcula integralizações do discente, seja ela de componentes obrigatórios, optativos ou equivalentes.
	 * 
	 * @param d
	 * @param situacoesPagasEMatriculadas
	 * @throws ArqException
	 */
	private static void calcularIntegralizacoes(DiscenteGraduacao d, Collection<SituacaoMatricula> situacoes) throws ArqException {

		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		d.setCrNaoAtividadeObrigInteg((short) 0);
		d.setChAtividadeObrigInteg((short) 0);
		d.setChNaoAtividadeObrigInteg((short) 0);
		d.setChOptativaIntegralizada((short) 0);

		try {
			// considerando apenas obrigatórias e optativas do curso
			dao.calcularIntegralizacaoCurriculo(d, situacoes);

			// considerando equivalentes
			calcularIntegralizacaoEquivalentes(d, situacoes);

			// considerando apenas extras
			dao.calcularIntegralizacaoExtras(d, situacoes);

			d.setCrTotalIntegralizados(d.getCrNaoAtividadeObrigInteg());
			d.setChTotalIntegralizada((short) (d.getChAtividadeObrigInteg() + d.getChNaoAtividadeObrigInteg() + d.getChOptativaIntegralizada()));

			// calcular total de atividades pendentes
			List<MatriculaComponente> disciplinas = ddao.findDisciplinasConcluidasMatriculadas(d.getId(), true);
			Collection<ComponenteCurricular> componentesPendentes = ddao.findByDisciplinasCurricularesPendentes(d.getId(), disciplinas, new ArrayList<TipoGenerico>());
			short pendentes = 0;
			for (ComponenteCurricular c : componentesPendentes) {
				if (!c.isMatriculado() && !c.isMatriculadoEmEquivalente())
					pendentes++;
			}
			d.setTotalAtividadesPendentes(pendentes);

			// calculando créditos e CH que o discente ainda falta integralizar
			// com base no seu currículo
			Curriculo c = dao.findByPrimaryKey(d.getCurriculo().getId(), Curriculo.class);
			calcularTotaisPendentes(d, c);
			
			// Verifica se o discente tem algum grupo de optativas pendente
			Map<GrupoOptativas, Integer> gruposOptativas = verificaGruposOptativas(d, situacoes);
			d.setTotalGruposOptativasPendentes((short) gruposOptativas.size());
			

		} finally {
			dao.close();
			ddao.close();
		}

	}

	/**
	 * Busca nos componentes pendentes do currículo se o aluno pagou
	 * equivalentes a ele;
	 *
	 * @param discente
	 * @param situacoes
	 * @throws ArqException
	 */
	private static void calcularIntegralizacaoEquivalentes(DiscenteGraduacao discente, Collection<SituacaoMatricula> situacoes) throws ArqException {
		
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		
		try {

			Collection<MatriculaComponente> matriculasCursadas = ddao.findMatriculasComponentesCurriculares(discente, situacoes, null);
			Collection<Integer> componentes = new ArrayList<Integer>();

			// coleção dos componentes curriculares que possui tipo de integralização equivalente
			TreeSet<Integer> componentesMarcadosComoEquivalente = new TreeSet<Integer>();
			for ( MatriculaComponente mc : matriculasCursadas  ) {
				if ( TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(mc.getTipoIntegralizacao()) ||
						TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE.equals(mc.getTipoIntegralizacao()) ) {
					componentesMarcadosComoEquivalente.add(mc.getComponente().getId());
				}
				componentes.add(mc.getComponente().getId());
			}

			// as equivalências são baseadas nos componentes do currículo do aluno
			// que ainda não foram integralizadas.
			for (CurriculoComponente ccPendente : ddao
					.findComponentesPendentesByDiscente(discente,true,false)) {
				// pra cada componente pendente é verificado se o aluno já cumpriu
				// seus créditos ou CH através de equivalentes.

				if ( ccPendente.getComponente().getEquivalencia() != null ) {

					int ch = ccPendente.getComponente().getChTotal();
					int cr = ccPendente.getComponente().getCrTotal();

					// ... e tiver componentes equivalentes integralizados
					ArvoreExpressao arvore =  ArvoreExpressao.fromExpressao(ccPendente.getComponente().getEquivalencia());

					boolean possuiEquivalencia = false;

					if (arvore != null && arvore.eval( componentes ) ) {

						/*
						 * O componente só deve contar como equivalente, se ao menos um item da expressão de equivalência
						 * for marcado como equivalente. Isso serve para evitar um caso encontrado em ciência da computação onde
						 * a disciplina Programação Matemática (obrigatória) foi cursada pelo aluno e a disciplina pesquisa
						 * operacional possui equivalência para ela e contou indevidamente;
						 */

						List<Integer> componentesConsiderados = arvore.getMatches();

						for ( Integer ccId : componentesConsiderados ) {
							if ( componentesMarcadosComoEquivalente.contains(ccId) ) {
								possuiEquivalencia = true;
								break;
							}
						}

						if ( possuiEquivalencia ) {

							// se o componente pendente for obrigatório e do próprio currículo do aluno
							if (ccPendente.getObrigatoria()
									&& ccPendente.getCurriculo().getId() == discente
											.getCurriculo().getId()) {

								// é incrementado créditos e CH DO COMPONENTE PENDENTE para o aluno
								if (ccPendente.getComponente().isAtividade() || ccPendente.getComponente().isAtividadeColetiva()) {
									discente.incChAtividadeObrigInteg(ch);
								} else {
									discente.incCrNaoAtividadeObrigInteg(cr);
									discente.incChNaoAtividadeObrigInteg(ch);
								}
							} else {
								discente.incChOptativaIntegralizada(ch);
							}
						}


					}

				}
			}

		} finally {
			ddao.close();
		}

	}

	/**
	 * Calcula créditos e carga horária que o discente ainda falta integralizar
	 * 
	 * @param d
	 * @param c
	 */
	private static void calcularTotaisPendentes(DiscenteGraduacao d, Curriculo c) {
		d.setCrNaoAtividadeObrigPendente((short) max(0, c.getCrNaoAtividadeObrigatorio() - d.getCrNaoAtividadeObrigInteg()));

		d.setChNaoAtividadeObrigPendente((short) max(0, c.getChNaoAtividadeObrigatoria() - d.getChNaoAtividadeObrigInteg()));

		d.setChAtividadeObrigPendente((short) max(0, c.getChAtividadeObrigatoria() - d.getChAtividadeObrigInteg()));

		d.setChOptativaPendente((short) max(0, c.getChOptativasMinima() - d.getChOptativaIntegralizada()));

		d.setChTotalPendente((short) (d.getChAtividadeObrigPendente() + d.getChNaoAtividadeObrigPendente() + d.getChOptativaPendente()) );

		d.setCrTotalPendentes((short) max(0, c.getCrTotalMinimo() - d.getCrTotalIntegralizados()));

		if (d.getChAulaPendente() == null) d.setChAulaPendente((short)0);
		if (d.getCrAulaPendente() == null) d.setCrAulaPendente((short)0);

		if (d.getChEstagioPendente() == null) d.setChEstagioPendente((short)0);
		if (d.getCrEstagioPendente() == null) d.setCrEstagioPendente((short)0);

		if (d.getChLabPendente() == null) d.setChLabPendente((short)0);
		if (d.getCrLabPendente() == null) d.setCrLabPendente((short)0);
	}

	/**
	 * Registra a alteração nos cálculos realizados para o discente
	 * 
	 * @param idDiscenteGraduacao
	 * @param dao
	 * @param mov
	 * @throws DAOException
	 */
	private static void registraAlteracaoCalculos(int idDiscenteGraduacao, DiscenteGraduacaoDao dao, Movimento mov) throws DAOException {

		DiscenteGraduacao dg = dao.findByPrimaryKey(idDiscenteGraduacao,DiscenteGraduacao.class);

		AlteracaoDiscenteGraduacao alteracao = new AlteracaoDiscenteGraduacao();
		alteracao.setDiscente(dg);
		alteracao.setData(new Date());
		alteracao.setOperacao(mov.getCodMovimento().getId());
		alteracao.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());

		alteracao.setChTotalIntegralizada(dg.getChTotalIntegralizada());
		alteracao.setChTotalPendente(dg.getChTotalPendente());
		alteracao.setChOptativaIntegralizada(dg.getChOptativaIntegralizada());
		alteracao.setChOptativaPendente(dg.getChOptativaIntegralizada());
		alteracao.setChNaoAtividadeObrigInteg(dg.getChNaoAtividadeObrigInteg());
		alteracao.setChNaoAtividadeObrigPendente(dg.getChNaoAtividadeObrigPendente());
		alteracao.setChAtividadeObrigInteg(dg.getChAtividadeObrigInteg());
		alteracao.setChAtividadeObrigPendente(dg.getChAtividadeObrigPendente());
		alteracao.setChAulaIntegralizada(dg.getChAulaIntegralizada());
		alteracao.setChAulaPendente(dg.getChAulaPendente());
		alteracao.setChLabIntegralizada(dg.getChLabIntegralizada());
		alteracao.setChLabPendente(dg.getChLabPendente());
		alteracao.setChEstagioIntegralizada(dg.getChEstagioIntegralizada());
		alteracao.setChEstagioPendente(dg.getChEstagioPendente());
		alteracao.setCrTotalIntegralizados(dg.getCrTotalIntegralizados());
		alteracao.setCrTotalPendentes(dg.getCrTotalPendentes());
		alteracao.setCrExtraIntegralizados(dg.getCrExtraIntegralizados());
		alteracao.setCrNaoAtividadeObrigInteg(dg.getCrNaoAtividadeObrigInteg());
		alteracao.setCrNaoAtividadeObrigPendente(dg.getCrNaoAtividadeObrigPendente());
		alteracao.setCrLabIntegralizado(dg.getCrLabIntegralizado());
		alteracao.setCrLabPendente(dg.getCrLabPendente());
		alteracao.setCrEstagioIntegralizado(dg.getCrEstagioIntegralizado());
		alteracao.setCrEstagioPendente(dg.getCrEstagioPendente());
		alteracao.setCrAulaIntegralizado(dg.getCrAulaIntegralizado());
		alteracao.setCrAulaPendente(dg.getCrAulaPendente());

		dao.create(alteracao);
	}

	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return getDAO(dao, null);
	}
	
	private static <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		return DAOFactory.getInstance().getDAOMov(dao, mov);
	}


	/**
	 * Calcula os tipos de integralizações das matrículas para o discente
	 *
	 * @param discente
	 * @param mov
	 * @param mcdao
	 * @param dgdao
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Deprecated
	public static void calcularTipoIntegralizacaoMatriculas(DiscenteGraduacao discente, Collection<MatriculaComponente> matriculas ) throws ArqException, NegocioException {

		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class);
		DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class);
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		
		// conjunto de alterações das matrículas que devem ser realizadas no
		// final do método.
		// ao ser identificada uma alteração no registro de matriculaComponente
		// é criado e adicionado nessa coleção.

		// Pegar componentes obrigatórios do currículo (sem contar os blocos)
		TreeSet<Integer> componentesObrigCurriculo = dgdao.findComponentesDoCurriculoByDiscente(discente, true);
		
		// pegar todas as sub-unidades dos blocos obrigatórios e adicionar à lista
		TreeSet<Integer> componentesSubUnidadesObrigCurriculo = dgdao.findComponentesSubUnidadesDoCurriculoByDiscente(discente, true);
		componentesObrigCurriculo.addAll(componentesSubUnidadesObrigCurriculo);
		
		// Pegar componentes optativos do currículo (sem contar os blocos)
		TreeSet<Integer> componentesOptatCurriculo = dgdao.findComponentesDoCurriculoByDiscente(discente, false);
		
		// pegar todas as sub-unidades dos blocos obrigatórios e adicionar à lista
		TreeSet<Integer> componentesSubUnidadesOptatCurriculo = dgdao.findComponentesSubUnidadesDoCurriculoByDiscente(discente, false);
		componentesOptatCurriculo.addAll(componentesSubUnidadesOptatCurriculo);
		
		try {

			//coleção de componentes que o discente já pagou (ou foram
			// integralizados, como aproveitamento, aprovação, ... )
			Collection<ComponenteCurricular> componentesPagosEMatriculados =
				ddao.findComponentesCurriculares(discente,SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);

			// adiciona os componentes pagos com as matrículas que estão sendo passadas.
			// Isto é necessário pois a inclusão de um novo componente no aproveitamento pode mudar seu tipo de integralização.
			for ( MatriculaComponente matricula : matriculas ) {
				componentesPagosEMatriculados.add(matricula.getComponente());
			}

			// verifica as matrículas que são obrigatórias da grade
			for (MatriculaComponente matricula : matriculas) {
				// evitar erro de LazyInitializationException 
				if (matricula.getComponente().isSubUnidade() && 
						matricula.getId() != 0) {
					matricula = mcdao.refresh(matricula);
					matricula.getComponente().getBlocoSubUnidade();
				}
				boolean obrigatoria = calculaTipoIntegralizacao(discente,matricula, componentesObrigCurriculo);

				if ( obrigatoria && !TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) ) {
					matricula.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
				}
			}

			// coleção de componentes de todos os currículos do CURSO do aluno,
			// que o aluno ainda não integralizou
			Collection<CurriculoComponente> pendentesObrigatoriaCurriculo = ddao.findComponentesPendentesByDiscente(discente, true,true);


			// analisa as equivalentes a obrigatórias do curso
			for (CurriculoComponente ccPendente : pendentesObrigatoriaCurriculo) {

				String equivalencia = ccPendente.getComponente().getEquivalencia();
//				 só é necessário verificar caso o componente pendente tenha alguma
				// equivalência, senão é lógico
				// que o aluno não pagou nada equivalente a ele... MAS se a
				// equivalência estiver do lado do componente integralizado ?!?!?!

				if (equivalencia != null) {

					if (ccPendente.getObrigatoria()	&& ccPendente.getCurriculo().getId() == discente.getCurriculo().getId()) {
						// ... e se o aluno possui um conjunto de equivalentes
						// entre seus componentes integralizados.
						if (ExpressaoUtil.eval(equivalencia, componentesPagosEMatriculados)) {
							// seta nas matrículas dos componentes como equ_obr
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, componentesPagosEMatriculados);
							if (equivalentes != null) {
								for (ComponenteCurricular componente : equivalentes) {
									// pega a matrícula do equivalente
									MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
									// verifica prioridade, seta caso seja qualquer outra coisa menos OBRIGATORIA
									if (mat != null &&  !TipoIntegralizacao.OBRIGATORIA.equals(mat.getTipoIntegralizacao())) {
										mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
									}
								}
							}
						}
					}
				}
			}

			// analisa os componentes complementares
			for (MatriculaComponente matricula : matriculas) {
//				evitar erro de LazyInitializationException 
				if (matricula.getComponente().isSubUnidade() && 
						matricula.getId() != 0) {
					matricula = mcdao.refresh(matricula);
					matricula.getComponente().getBlocoSubUnidade();
				}
				boolean optativaGrade = calculaTipoIntegralizacao(discente,	matricula, componentesOptatCurriculo);
				boolean optativaCurso = false;// calculaTipoIntegralizacao(discente,	matricula, componentesOptatCurriculoCurso);
				// só muda o tipo de integralização se não já tiver sido marcada como obrigatória ou equivalente a obrigatória
				if ( (optativaGrade || optativaCurso ) && !TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) &&
						!TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao() ) ) {

					String tipoIntegralizacao = null;
					if ( optativaGrade ) {
						tipoIntegralizacao = TipoIntegralizacao.OPTATIVA_DA_GRADE;
					} else {
						tipoIntegralizacao = TipoIntegralizacao.OPTATIVA_CURSO_CIDADE;
					}
					matricula.setTipoIntegralizacao(tipoIntegralizacao);


				}
			}

			// verificar equivalentes de optativas
			//	 coleção de componentes de todos os currículos do CURSO do aluno,
			// que o aluno ainda não integralizou
			Collection<CurriculoComponente> pendentesCurso = ddao.findComponentesPendentesByDiscente(discente, false, false);

			for (CurriculoComponente ccPendente : pendentesCurso) {

				String expressao = ccPendente.getComponente().getEquivalencia();

				if ( expressao != null ) {

					if (ExpressaoUtil.eval(ccPendente.getComponente()
							.getEquivalencia(), componentesPagosEMatriculados)) {
						// só entra aqui se o componente pendente possuir um conjunto
						// de equivalente entre seus componente integralizados.

						// se o componente for exatamente do currículo
						if (ccPendente.getCurriculo().getId() == discente.getCurriculo().getId()) {
							// pega os esses componentes que são equivalentes
							// ...
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(expressao, componentesPagosEMatriculados);
							// ... e pra cada um, identifica na matrícula que
							// ele está registrado, o tipo de integralização.
							if (equivalentes != null) {
								for (ComponenteCurricular componente : equivalentes) {
									MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
									/* mas só identifica para matrículas que ainda
								 não foram identificadas.  isso porque uma OBRG_DO_CURRIC pode fazer
									 parte dessa equivalência, e o tipo  setado antes deve prevalecer */
									if (mat != null && mat.getTipoIntegralizacao() == null ) {
										mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE);
									}
								}
							}
						}
					}

				}
			}

			// verificar extra-curriculares

			// as extra-curriculares serão todas matrículas que ainda não foram
			// identificadas
			for (MatriculaComponente matricula : matriculas) {

				// QC foi um tipo migrado do Ponto@, equivalente a optativa do curso, mas não foi usado no SIGAA pelo fato de não valer semanticamente
				if (matricula.getTipoIntegralizacao() == null || matricula.getTipoIntegralizacao().equals("QC")) {
					matricula.setTipoIntegralizacao(TipoIntegralizacao.EXTRA_CURRICULAR);
				}
			}

			// e finalmente persiste as alterações em batch.

		} finally {
			ddao.close();
			dgdao.close();
			mcdao.close();
		}
	}

	/**
	 * Atualiza o prazo máximo para conclusão do curso pelo discente
	 * 
	 * @param d
	 * @param mov
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public static void atualizarPrazoMaximo(DiscenteGraduacao d, Movimento mov) throws ArqException, NegocioException {
		CalculoPrazoMaximoFactory.getCalculoGraduacao(d).calcular(d, mov);
	}

	/**
	 * Verifica se um discente pagou a carga horária mínima exigida nos grupos
	 * de optativas. Retorna um mapa onde a chave é um grupo de optativas e o
	 * valor é a carga horária pendente para esse grupo. Caso o discente
	 * tenha cumprido toda a carga horária de um grupo, ele não aparece no mapa.
	 * Se um discente tiver cumprido toda a carga horária de todos os grupos,
	 * o mapa retornado é vazio.
	 * @throws ArqException 
	 */
	public static Map<GrupoOptativas, Integer> verificaGruposOptativas(DiscenteGraduacao d, Collection<SituacaoMatricula> situacoes) throws ArqException {
		Map<GrupoOptativas, Integer> result = new HashMap<GrupoOptativas, Integer>();
		GrupoOptativasDao dao = getDAO(GrupoOptativasDao.class);
		
		try {
			if (d.getCurriculo() != null) {
				List<GrupoOptativas> grupos = dao.findGruposOptativasByCurriculo(d.getCurriculo());
				
				if (!isEmpty(grupos)) {
					for (GrupoOptativas grupo : grupos) {
						int chFaltando = chGrupoFaltando(d, grupo, situacoes);
						if (chFaltando > 0) {
							result.put(grupo, chFaltando);
						}
					}
				}
			}
		} finally {
			dao.close();
		}
		
		return result;
	}

	/**
	 * Identifica, em um grupo de optativas, se um aluno pagou os componentes ou equivalentes
	 * e retorna a carga horária pendente para o grupo.
	 * @param situacoes 
	 */
	@SuppressWarnings("unchecked")
	private static int chGrupoFaltando(DiscenteGraduacao d, GrupoOptativas grupo, Collection<SituacaoMatricula> situacoes) throws ArqException {
		int chMinima = grupo.getChMinima();
		
		DiscenteDao ddao = getDAO(DiscenteDao.class);
		
		try {
			Collection<ComponenteCurricular> componentesCursados = ddao.findComponentesCurriculares(d, situacoes, null);
			Collection<Integer> idsComponentesCursados = CollectionUtils.collect(componentesCursados, new Transformer() {
				public Object transform(Object obj) {
					ComponenteCurricular componente = (ComponenteCurricular) obj;
					return componente.getId();
				}
			});
			
			for (final CurriculoComponente cc : grupo.getComponentes()) {
				boolean pagouDiretamente = CollectionUtils.exists(componentesCursados, new Predicate() {
					public boolean evaluate(Object item) {
						ComponenteCurricular comp = (ComponenteCurricular) item;
						return comp.getId() == cc.getComponente().getId();
					} 
				});
				
				boolean pagouPorEquivalente = false;
				if (!isEmpty(cc.getComponente().getEquivalencia())) {
					ArvoreExpressao expressao = ArvoreExpressao.fromExpressao(cc.getComponente().getEquivalencia());
					boolean pagouEquivalente = expressao.eval(idsComponentesCursados);
					boolean equivalenteNaoEstaNoCurriculo = !CollectionUtils.intersection(expressao.getMatches(), idsComponentesCursados).isEmpty();
					
					pagouPorEquivalente = pagouEquivalente && equivalenteNaoEstaNoCurriculo; 
				}
				
				if (pagouDiretamente || pagouPorEquivalente) {
					chMinima -= cc.getComponente().getChTotal();
				}
			}
		} finally {
			ddao.close();
		}
		
		return chMinima;
	}
	
}
