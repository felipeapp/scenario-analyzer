/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/03/2010
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.GrupoOptativasDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.EquivalenciaEspecifica;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe auxiliar utilizada no cálculo das integralizações
 * de matrículas de discentes de graduação.
 * 
 * @author David Pereira
 * 
 */
public class IntegralizacoesHelper {

	/**
	 * Verifica se o discente de graduação é Graduando
	 * 
	 * @param d
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static boolean isGraduando(DiscenteGraduacao d, Movimento mov) throws DAOException, NegocioException {
		
		if (!d.isGraduacao())
			throw new NegocioException("Permitido somente para discentes de: " + NivelEnsino.getDescricao(NivelEnsino.GRADUACAO));
		
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		try {
			boolean haMatriculasNaoConsolidadas = ddao.existemMatriculasNaoConsolidadas(d.getId());
			
			if (d.getCrTotalPendentes() != null && d.getCrTotalPendentes() == 0 &&
					d.getChTotalPendente() != null && d.getChTotalPendente() == 0
					&& d.getChNaoAtividadeObrigPendente() == 0
					&& d.getChOptativaPendente() == 0 && d.getTotalAtividadesPendentes() == 0
					&& (d.getTotalGruposOptativasPendentes() == null || d.getTotalGruposOptativasPendentes() == 0) 
					&& !haMatriculasNaoConsolidadas) 
				return true;
			
			return false;
		} finally {
			ddao.close();
		}
	}
	
	/**
	 * Faz o recálculo das integralizações considerando as matrículas pagas e matriculadas.
	 * 
	 * @param idDiscente
	 * @param pendenciasMatricula
	 * @param mov
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public static boolean isFormando(DiscenteGraduacao d, short pendenciasMatricula, Movimento mov) throws ArqException, NegocioException  {
		
		if (!d.isGraduacao())
			throw new NegocioException("Permitido somente para discentes de: " + NivelEnsino.getDescricao(NivelEnsino.GRADUACAO));
		
		// É necessário passar outra referência porque nos cálculos de integralização o objeto do discente vai ser modificado.
		// Esse não é o comportamento que queremos nesse código
		DiscenteGraduacao discente = UFRNUtils.deepCopy(d);
		
		calcularIntegralizacoes(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas(), mov);
		// com os totais re-calculados
		if (discente.getCrTotalPendentes() == 0 && discente.getChAtividadeObrigPendente() == 0
				&& discente.getChOptativaPendente() == 0 && pendenciasMatricula == 0 && discente.getTotalGruposOptativasPendentes() == 0)
			return true;
		else
			return false;
		
	}
	
	/**
	 * Realiza uma analise por extenso das equivalências e remove dos componentes analisados quando encontra uma equivalência
	 * 
	 * @param idDiscente
	 * @param disciplinas
	 * @param equivalenciasDiscente
	 * @param componentesParaAnalise
	 * @throws DAOException
	 */
	public static void analisarEquivalenciasPorExtenso(int idDiscente, List<MatriculaComponente> disciplinas, List<TipoGenerico> equivalenciasDiscente, Collection<ComponenteCurricular> componentesParaAnalise) throws DAOException {
		
		// conjunto de componentes que serão usados na busca por equivalencia
		List<Integer> idsDisciplinas = new ArrayList<Integer>();
		// conjunto de componentes que se encontrão com status de matriculado
		List<Integer> idsMatriculadas = new ArrayList<Integer>();
		for (MatriculaComponente mc : disciplinas) {
			
			if (mc.getSituacaoMatricula().equals(SituacaoMatricula.MATRICULADO)) 
				idsMatriculadas.add(mc.getComponente().getId());
			
			if (mc.getTipoIntegralizacao() != null && mc.getTipoIntegralizacao().equals(TipoIntegralizacao.OBRIGATORIA))
				continue;
			
			if (mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVADO)
					|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_TRANSFERIDO)
					|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_CUMPRIU)
					|| mc.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO)) {
				idsDisciplinas.add(mc.getComponente().getId());
			} 
			
		}
		
		DiscenteDao ddao = getDAO(DiscenteDao.class, null);
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class, null);
		 
		
		Map<Integer, List<Object[]>> mapaEquivalencias = ccdao.findEquivalenciasComponentesByDiscente(componentesParaAnalise, new Discente(idDiscente));
		for (Iterator<ComponenteCurricular> it = componentesParaAnalise.iterator(); it.hasNext(); ) {
			ComponenteCurricular cc = it.next();
			List<Object[]> equivalencias = mapaEquivalencias.get(cc.getId());
			
			if (!isEmpty(equivalencias)) {
				for (Object[] infoEquivalencia : equivalencias) {
					String equivalencia = (String) infoEquivalencia[0];
					Integer idEspecifica = (Integer) infoEquivalencia[1];
					Date fimVigenciaEquivalencia = (Date) infoEquivalencia[3];
					
					EquivalenciaEspecifica especifica = null;
					if (!isEmpty(idEspecifica))
						especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
					
					
					
					ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(equivalencia);
					if (arvore != null && arvore.eval(idsDisciplinas)) {
						
						StringBuilder sb = new StringBuilder();
						sb.append("Cumpriu " + cc + " " + "(" + cc.getChTotal() + "h) através de ");
						
						int qtdeMatEquivalenciasCadastradas = 0;
						Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, cc, idsDisciplinas);
						for (Iterator<ComponenteCurricular> itMatch = equivalentes.iterator(); itMatch.hasNext();) {
							ComponenteCurricular eq = ddao.findComponenteNomeChById(itMatch.next().getId());
							MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(disciplinas, eq, SituacaoMatricula.getSituacoesPagas());
							
							if (mat != null && TipoIntegralizacao.isEquivalente(mat.getTipoIntegralizacao())) {
								if (((isEquivalenciaValendoNaDataMatricula(mat, null, fimVigenciaEquivalencia)) && especifica == null) 
										|| (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
									qtdeMatEquivalenciasCadastradas++;
									sb.append(eq + " (" + eq.getChTotal() + "h)");
									if (itMatch.hasNext()) {
										sb.append(", ");
									}
								}
							}
						}
						
						if ( equivalentes.size() == qtdeMatEquivalenciasCadastradas ) {
							equivalenciasDiscente.add(new TipoGenerico(sb.toString()));
							it.remove(); //remove componentes da lista de pendentes, já que cumpriu através de equivalencia
							break;
						}
						
					} else if (arvore != null && arvore.eval(idsMatriculadas)) {
						cc.setMatriculadoEmEquivalente(true);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Calcula integralzações do discente, seja ela de componentes obrigatórios,
	 * optativos ou equivalentes.
	 * 
	 * @param d
	 * @param situacoesPagasEMatriculadas
	 * @throws ArqException
	 */
	public static void calcularIntegralizacoes(DiscenteGraduacao d,
			Collection<SituacaoMatricula> situacoes, Movimento mov) throws ArqException {

		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class, mov);
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		d.setCrNaoAtividadeObrigInteg((short) 0);
		d.setChAtividadeObrigInteg((short) 0);
		d.setChNaoAtividadeObrigInteg((short) 0);
		d.setChOptativaIntegralizada((short) 0);
		d.setChIntegralizadaAproveitamentos((short) 0);
		
		try {
			// considerando apenas obrigatórias e optativas do curso
			dao.calcularIntegralizacaoCurriculo(d, situacoes);

			// considerando equivalentes
			calcularIntegralizacaoEquivalentes(d, situacoes, mov);

			// considerando apenas extras
			dao.calcularIntegralizacaoExtras(d, situacoes);

			d.setCrTotalIntegralizados(d.getCrNaoAtividadeObrigInteg());
			d.setChTotalIntegralizada((short) (d.getChAtividadeObrigInteg()
					+ d.getChNaoAtividadeObrigInteg() + d
					.getChOptativaIntegralizada()));

			// calcular total de atividades pendentes
			List<MatriculaComponente> disciplinas = ddao
					.findDisciplinasConcluidasMatriculadas(d.getId(), true);
			Collection<ComponenteCurricular> componentesPendentes = ddao
					.findByDisciplinasCurricularesPendentes(d.getId(), disciplinas, new ArrayList<TipoGenerico>());
			short pendentes = 0;
			for (ComponenteCurricular c : componentesPendentes) {
				if (!c.isMatriculado() && !c.isMatriculadoEmEquivalente())
					pendentes++;
			}
			d.setTotalAtividadesPendentes(pendentes);

			// calculando créditos e CH que o discente ainda falta integralizar
			// com base no seu currículo
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(d.getId());
			Curriculo c = dados.getCurriculo();
			calcularTotaisPendentes(d, c);

			// Verifica se o discente tem algum grupo de optativas pendente
			Map<GrupoOptativas, Integer> gruposOptativas = verificaGruposOptativas(d, situacoes, disciplinas, mov);
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
	public static void calcularIntegralizacaoEquivalentes(DiscenteGraduacao discente, Collection<SituacaoMatricula> situacoes, Movimento mov) throws ArqException {
		
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		MovimentacaoAlunoDao movdao = getDAO(MovimentacaoAlunoDao.class, mov);
		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class, mov);
		
		try {

			Collection<MatriculaComponente> matriculasCursadas = ddao.findMatriculasComponentesCurriculares(discente, situacoes, null);
			Collection<Integer> componentes = new ArrayList<Integer>();

			// coleção dos componentes curriculares que possui tipo de integralização equivalente
			TreeSet<Integer> componentesMarcadosComoEquivalente = new TreeSet<Integer>();
			for ( MatriculaComponente mc : matriculasCursadas  ) {
				if ( mc.getTipoIntegralizacao().equals(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA) ||
						mc.getTipoIntegralizacao().equals(TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE) ) {
					componentesMarcadosComoEquivalente.add(mc.getComponente().getId());
				}
				componentes.add(mc.getComponente().getId());
			}

			// as equivalências são baseadas nos componentes do currículo do aluno
			// que ainda não foram integralizadas.
			Collection<CurriculoComponente> pendentes = ddao.findComponentesPendentesByDiscente(discente,true,false);
			
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(discente.getId());
			Date primeiraData = dados.getDataInicio();
			Date ultimaData = dados.getDataFim();

			Map<Integer, List<Object[]>> mapaEquivalencias = ccdao.findEquivalenciasComponentesByIntervaloDatas(pendentes, dados.getCurriculo().getId(), primeiraData, ultimaData);
			for (CurriculoComponente ccPendente : pendentes) {
				// pra cada componente pendente é verificado se o aluno já cumpriu
				// seus créditos ou CH através de equivalentes.

				List<Object[]> equivalencias = mapaEquivalencias.get(ccPendente.getComponente().getId());
				if (!isEmpty(equivalencias)) {
					int ch = ccPendente.getComponente().getChTotal();
					int cr = ccPendente.getComponente().getCrTotal();

					for (Object[] infoEquivalencia : equivalencias) {
						boolean possuiEquivalencia = false;
						String equivalencia = (String) infoEquivalencia[0];
						
						// ... e tiver componentes equivalentes integralizados
						ArvoreExpressao arvore =  ArvoreExpressao.fromExpressao(equivalencia);
						
						if (arvore != null && arvore.eval( componentes ) ) {
	
							/*
							 * O componente só deve contar como equivalente, se ao menos um item da expressão de equivalência
							 * for marcado como equivalente. Isso serve para evitar um caso encontrado em ciência da computação onde
							 * a disciplina Programação Matemática (obrigatória) foi cursada pelo aluno e a disciplina pesquisa
							 * operacional possui equivalência para ela e contou indevidamente;
							 */
	
							List<Integer> componentesConsiderados = arvore.getMatches();
							MatriculaComponente mat = null;
							
							if (componentesConsiderados != null) {
								for ( Integer ccId : componentesConsiderados ) {
									if ( componentesMarcadosComoEquivalente.contains(ccId) ) {
										mat = MatriculaComponenteHelper.searchMatricula(matriculasCursadas, new ComponenteCurricular(ccId));
										possuiEquivalencia = true;
										break;
									}
								}
							}
	
							if ( possuiEquivalencia && mat != null) {
								// se o componente pendente for obrigatório e do próprio currículo do aluno
								if (ccPendente.getObrigatoria()&& ccPendente.getCurriculo().getId() == discente.getCurriculo().getId() && TipoIntegralizacao.isObrigatoria(mat.getTipoIntegralizacao())) {
	
									// é incrementado créditos e CH DO COMPONENTE PENDENTE para o aluno
									if (ccPendente.getComponente().isAtividade() || ccPendente.getComponente().isAtividadeColetiva()) {
										discente.incChAtividadeObrigInteg(ch);
									} else {
										discente.incCrNaoAtividadeObrigInteg(cr);
										discente.incChNaoAtividadeObrigInteg(ch);
									}
								} else if (!ccPendente.getObrigatoria()){
									discente.incChOptativaIntegralizada(ch);
								}
								
								// Incrementa a CH aproveitada se o componente tiver sido aproveitado
								if (mat.isAproveitado()) {
									discente.incChAproveitada(ch);
								}
								
								break;
							}
	
	
						}
					}
				}
			}

		} finally {
			ddao.close();
			movdao.close();
			mcdao.close();
			ccdao.close();
		}

	}
	
	/**
	 * Analisa as matrículas e seta os tipos de integralização.
	 * 
	 * @param discente
	 * @param matriculas
	 * @param mov
	 * @return
	 * @throws ArqException
	 */
	public static Collection<AlteracaoMatricula> analisarTipoIntegralizacao(DiscenteGraduacao discente, Collection<MatriculaComponente> matriculas, Movimento mov) throws ArqException {
		
		DiscenteGraduacaoDao dgdao = getDAO(DiscenteGraduacaoDao.class, mov);
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class, mov);
		MovimentacaoAlunoDao movdao = getDAO(MovimentacaoAlunoDao.class, mov);
		
		// conjunto de alterações das matrículas que devem ser realizadas no
		// final do método.
		// ao ser identificada uma alteração no registro de matriculaComponente
		// é criado e adicionado nessa coleção.
		Collection<AlteracaoMatricula> alteracoes = new ArrayList<AlteracaoMatricula>();

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

		// coleção de componentes que o discente já pagou (ou foram
		// integralizados, como aproveitamento, aprovação, ... )
		Collection<ComponenteCurricular> componentesPagosEMatriculados = ddao.findComponentesCurriculares(discente,SituacaoMatricula.getSituacoesPagasEMatriculadas(),null);
		
		// As matrículas pagas em duplicidade serão contabilizadas como ELETIVA
		Collection<MatriculaComponente> pagasEmDuplicidade = new LinkedList<MatriculaComponente>();
		MatriculaComponente matriculaComponenteDuplicado = null;
		do {
			matriculaComponenteDuplicado = null;
			for (MatriculaComponente mc : matriculas) {
				for (MatriculaComponente mcd : matriculas) {
					if (mcd.getId() != mc.getId() && mcd.getComponente().getId() == mc.getComponente().getId() && !mc.getComponente().isBloco()) {
						matriculaComponenteDuplicado = mcd;
						break;
					}
				}
				if (matriculaComponenteDuplicado != null)
					break;
			}
			if (matriculaComponenteDuplicado != null) {
				pagasEmDuplicidade.add(matriculaComponenteDuplicado);
				matriculas.remove(matriculaComponenteDuplicado);
			}
		} while (matriculaComponenteDuplicado != null);
		
		for (MatriculaComponente matricula : matriculas) {
			if (!componentesPagosEMatriculados.contains(matricula.getComponente())) {
				componentesPagosEMatriculados.add(matricula.getComponente());
			}
		}
		
		try {
			// verifica as matrículas que são obrigatórias da grade
			for (MatriculaComponente matricula : matriculas) {

				boolean obrigatoria = isObrigatoria(discente,matricula, componentesObrigCurriculo);

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
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(discente.getId());
			Date primeiraData = dados.getDataInicio();
			Date ultimaData = dados.getDataFim();

			Map<Integer, List<Object[]>> mapaEquivalencias = ccdao.findEquivalenciasComponentesByIntervaloDatas(pendentesObrigatoriaCurriculo, dados.getCurriculo().getId(), primeiraData, ultimaData);
			
			for (CurriculoComponente ccPendente : pendentesObrigatoriaCurriculo) {
				List<Object[]> equivalencias = mapaEquivalencias.get(ccPendente.getComponente().getId());
				
				if (!isEmpty(equivalencias)) {
					if (ccPendente.getObrigatoria()	&& ccPendente.getCurriculo().getId() == discente.getCurriculo().getId()) {
						
						for (Object[] infoEquivalencia : equivalencias) {
							String equivalencia = (String) infoEquivalencia[0];
							Integer idEspecifica = (Integer) infoEquivalencia[1];
							Date fimVigenciaEquivalencia = (Date) infoEquivalencia[3];
							EquivalenciaEspecifica especifica = null;
							if (!isEmpty(idEspecifica))
								especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
							
							// codigo para evitar que uma obrigatória seja equivalente a outra obrigatória
							Collection<ComponenteCurricular> componentesPagosEMatriculadosNaoObrigatorios = filtrarNaoObrigatorias(matriculas, componentesPagosEMatriculados);
							
							if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculadosNaoObrigatorios)) {
								// seta nas matrículas dos componentes como equ_obr
								Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculadosNaoObrigatorios);
								if (equivalentes != null) {
									int qtdeMatEquivalenciasCadastradas = 0;
									for (ComponenteCurricular componente : equivalentes) {
										// pega a matrícula do equivalente
										MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
										boolean componenteEquivalente = !isEmpty(mat);
										// Existem casos onde matricula pode ser null. 
										// Por exemplo, se estiver validando apenas um conjunto de matrículas (matriculas do semestre por exemplo)
										//o componente equivalente pago pode não estar nesse pequeno conjunto de matrículas. 
										// Mas se estiver analisando todas as matrículas do discente, ai sim não deveria ser null.
										if (mat != null) {
											// Verifica se a equivalência que foi encontrada é do tipo específica e estava valendo na data da matrícula
											if (((componenteEquivalente && isEquivalenciaValendoNaDataMatricula(mat, null, fimVigenciaEquivalencia)) && especifica == null) 
													|| (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
												AlteracaoMatricula alteracao = createAlteracaoMatricula(mat,mov,TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
												// verifica prioridade, seta caso seja qualquer outra coisa menos OBRIGATORIA
												if (!TipoIntegralizacao.OBRIGATORIA.equals(mat.getTipoIntegralizacao()) && alteracao != null) {
													mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA);
													alteracao.setExpressao(ArvoreExpressao.getMatchesExpression(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculadosNaoObrigatorios));
													alteracoes.add(alteracao);
													++qtdeMatEquivalenciasCadastradas;
												} else if (TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(mat.getTipoIntegralizacao()) && alteracao == null) { 
													++qtdeMatEquivalenciasCadastradas;													
												}
											}
										}
									}
									if ( equivalentes.size() == qtdeMatEquivalenciasCadastradas )
										break;
								}									
							}
						}
					}
				}
			}

			// analisa os componentes complementares
			for (MatriculaComponente matricula : matriculas) {

				boolean optativaGrade = isObrigatoria(discente,	matricula, componentesOptatCurriculo);
				//boolean optativaCurso = calculaTipoIntegralizacao(discente,	matrícula, componentesOptatCurriculoCurso);
				// só muda o tipo de integralização se não já tiver sido marcada como obrigatória ou equivalente a obrigatória
				if ( ( optativaGrade ) && !TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(matricula.getTipoIntegralizacao()) &&
						!TipoIntegralizacao.OBRIGATORIA.equals(matricula.getTipoIntegralizacao() ) ) {

					String tipoIntegralizacao = TipoIntegralizacao.OPTATIVA_DA_GRADE;

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
			Collection<CurriculoComponente> pendentesCurso = ddao.findComponentesPendentesByDiscente(discente, true, false);
			filtrarPendentesOptativasCurriculo(pendentesCurso, pendentesObrigatoriaCurriculo);
			mapaEquivalencias = ccdao.findEquivalenciasComponentesByIntervaloDatas(pendentesCurso, discente.getCurriculo().getId(), primeiraData, ultimaData);

			for (CurriculoComponente ccPendente : pendentesCurso) {
				List<Object[]> equivalencias = mapaEquivalencias.get(ccPendente.getComponente().getId());
				if (!isEmpty(equivalencias)) {
					for (Object[] infoEquivalencia : equivalencias) {
						String equivalencia = (String) infoEquivalencia[0];
						Integer idEspecifica = (Integer) infoEquivalencia[1];
						Date fimVigenciaEquivalencia = (Date) infoEquivalencia[3];
						EquivalenciaEspecifica especifica = null;
						if (!isEmpty(idEspecifica))
							especifica = ddao.findByPrimaryKey(idEspecifica, EquivalenciaEspecifica.class);
						
						if (equivalencia != null && ExpressaoUtil.eval(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados)) {

							// só entra aqui se o componente pendente possuir um conjunto
							// de equivalente entre seus componente integralizados.

							// pega os esses componentes que são equivalentes
							// ...
							Collection<ComponenteCurricular> equivalentes = ArvoreExpressao.getMatchesComponentes(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados);
							// ... e pra cada um, identifica na matrícula que
							// ele está registrado, o tipo de integralização.
							if (equivalentes != null) {
								int qtdeMatEquivalenciasCadastradas = 0;
								for (ComponenteCurricular componente : equivalentes) {
									MatriculaComponente mat = MatriculaComponenteHelper.searchMatricula(matriculas, componente);
									// Existem casos onde matricula pode ser null. 
									// Por exemplo, se estiver validando apenas um conjunto de matrículas (matriculas do semestre por exemplo) o componente equivalente pago pode não estar nesse pequeno conjunto de matrículas. 
									// Mas se estiver analisando todas as matrículas do discente, ai sim não deveria ser null.
									if (mat != null) {											
										// Verifica se a equivalência que foi encontrada é do tipo específica e estava valendo na data da matrícula
										if ( (especifica == null && isEquivalenciaValendoNaDataMatricula(mat, null, fimVigenciaEquivalencia)) 
											 || (especifica != null && especifica.isEquivalenciaValendoNaData(mat.getDataCadastro()))) {
											AlteracaoMatricula alteracao = createAlteracaoMatricula(mat, mov, TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE);
											// Só identifica para matrículas que ainda não tiveram o tipo de integralização registrado.
											// isso porque uma OBRG_DO_CURRIC pode fazer parte dessa equivalência, e o tipo
											// setado antes deve prevalecer
											if (mat.getTipoIntegralizacao() == null &&  alteracao != null) {
												alteracao.setExpressao(ArvoreExpressao.getMatchesExpression(equivalencia, ccPendente.getComponente(), componentesPagosEMatriculados));
												mat.setTipoIntegralizacao(TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE);
												alteracoes.add(alteracao);
												++qtdeMatEquivalenciasCadastradas;
											}
										}
									}
								}
								if ( equivalentes.size() == qtdeMatEquivalenciasCadastradas )
									break;
								
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
					AlteracaoMatricula alteracao = createAlteracaoMatricula(matricula, mov, TipoIntegralizacao.EXTRA_CURRICULAR);
					if (alteracao != null) {
						alteracoes.add(alteracao);
						matricula.setTipoIntegralizacao(TipoIntegralizacao.EXTRA_CURRICULAR);
					}
					// não precisa setar por que é persistido nas alterações
				}
			}
			
			// define as matriculas em duplicidade como ELETIVA
			for (MatriculaComponente mcd : pagasEmDuplicidade) {
				if (mcd.getTipoIntegralizacao() == null || !mcd.getTipoIntegralizacao().equals(TipoIntegralizacao.EXTRA_CURRICULAR)) {
					AlteracaoMatricula alteracao = createAlteracaoMatricula(mcd, mov, TipoIntegralizacao.EXTRA_CURRICULAR);
					if (alteracao != null) {
						alteracoes.add(alteracao);
						mcd.setTipoIntegralizacao(TipoIntegralizacao.EXTRA_CURRICULAR);
					}
				}
				// retorna à lista de componentes pagos
				matriculas.add(mcd);
			}

		} finally {
			ddao.close();
			dgdao.close();
			ccdao.close();
			movdao.close();
		}
		return alteracoes;		
	}

	/**
	 * Método para excluir as obrigatórias da verificação da equivalência. Isso evita que uma obrigatória seja equivalente a outra obrigatória.
	 * 
	 * @param matriculas
	 * @param componentesPagosEMatriculados
	 * @return
	 */
	public static Collection<ComponenteCurricular> filtrarNaoObrigatorias(Collection<MatriculaComponente> matriculas, Collection<ComponenteCurricular> componentesPagosEMatriculados) {
		Collection<ComponenteCurricular> componentesPagosEMatriculadosNaoObrigatorios = new ArrayList<ComponenteCurricular>();
		componentesPagosEMatriculadosNaoObrigatorios.addAll(componentesPagosEMatriculados);
		
		for (Iterator<ComponenteCurricular> iterator = componentesPagosEMatriculadosNaoObrigatorios.iterator(); iterator.hasNext();) {
			ComponenteCurricular componenteCurricular = iterator.next();
			for (MatriculaComponente mc : matriculas) {
				if (mc.getComponente().getId() == componenteCurricular.getId() && mc.getTipoIntegralizacao() != null && mc.getTipoIntegralizacao().equals(TipoIntegralizacao.OBRIGATORIA)) {
					iterator.remove();
					break;
				}
			}
		}
		return componentesPagosEMatriculadosNaoObrigatorios;
	}
	
	/**
	 * Método para excluir as obrigatórias da verificação da equivalência. 
	 * Isso evita que uma optativa seja equivalente a outra obrigatória.
	 * 
	 * @param matriculas
	 * @param componentesPagosEMatriculados
	 * @return
	 */
	private static void filtrarPendentesOptativasCurriculo(Collection<CurriculoComponente> pendentesCurso, Collection<CurriculoComponente> pendentesObrigatoriaCurriculo) {
		
		for (Iterator<CurriculoComponente> iterator = pendentesObrigatoriaCurriculo.iterator(); iterator.hasNext();) {
			CurriculoComponente cc = iterator.next();
			if (pendentesCurso.contains(cc)){
				pendentesCurso.remove(cc);
			}
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
	 * Verifica se o componente curricular é do currículo do aluno ou de algum currículo do seu curso.
	 * 
	 * @param discente
	 * @param matricula
	 * @param componentes
	 * @return
	 */
	private static boolean isObrigatoria(DiscenteGraduacao discente, MatriculaComponente matricula, TreeSet<Integer> componentes) {

		if (matricula.getComponente().isSubUnidade()) {
			return componentes.contains(matricula.getComponente().getBlocoSubUnidade().getId());
		} else {
			Integer idMatricula = matricula.getComponente().getId();
			
			// testa se é do currículo do aluno ou de algum currículo do seu curso
			return componentes.contains(idMatricula);			
		}
	}

	/**
	 * Retorna verdadeiro apenas se a equivalência for válida na data passada.
	 * Será considerado a equivalência que for válida em relação a data de cadastro da matrícula no componente curricular.
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isEquivalenciaValendoNaDataMatricula(MatriculaComponente mat, Date inicioVigenciaEquivalencia, Date fimVigenciaEquivalencia) {
		boolean equivalenciaValendoNaData = false;
		if (mat.getDataCadastro() == null) mat.setDataCadastro(new Date());
		if (fimVigenciaEquivalencia != null && inicioVigenciaEquivalencia != null){
			equivalenciaValendoNaData = mat.getDataCadastro().compareTo(fimVigenciaEquivalencia) <= 0 
										&& mat.getDataCadastro().compareTo(inicioVigenciaEquivalencia) >= 0;
		} else if (inicioVigenciaEquivalencia != null){
			equivalenciaValendoNaData = mat.getDataCadastro().compareTo(inicioVigenciaEquivalencia) >= 0; 
		} else if (fimVigenciaEquivalencia != null){
			equivalenciaValendoNaData = mat.getDataCadastro().compareTo(fimVigenciaEquivalencia) <= 0; 
		} else {
			equivalenciaValendoNaData = true;
		}
		return equivalenciaValendoNaData;
	}
	
	/**
	 * Calcula créditos e carga horária que o discente ainda falta integralizar
	 * 
	 * @param d
	 * @param c
	 */
	public static void calcularTotaisPendentes(DiscenteGraduacao d, Curriculo c) {
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
	 * Verifica se um discente pagou a carga horária mínima exigida nos grupos
	 * de optativas. Retorna um mapa onde a chave é um grupo de optativas e o
	 * valor é a carga horária pendente para esse grupo. Caso o discente
	 * tenha cumprido toda a carga horária de um grupo, ele não aparece no mapa.
	 * Se um discente tiver cumprido toda a carga horária de todos os grupos,
	 * o mapa retornado é vazio.
	 * @param concluidas 
	 * @throws ArqException 
	 */
	public static Map<GrupoOptativas, Integer> verificaGruposOptativas(DiscenteGraduacao d, Collection<SituacaoMatricula> situacoes, List<MatriculaComponente> concluidas, Movimento mov) throws ArqException {
		Map<GrupoOptativas, Integer> result = new HashMap<GrupoOptativas, Integer>();
		GrupoOptativasDao dao = getDAO(GrupoOptativasDao.class, mov);

		try {
			if (d.getCurriculo() != null) {
				List<GrupoOptativas> grupos = dao.findGruposOptativasByCurriculo(d.getCurriculo());
				
				if (!isEmpty(grupos)) {
					for (GrupoOptativas grupo : grupos) {
						// Verificar se o aluno já pagou o componente associado ao grupo ou um equivalente
						ComponenteCurricular componente = grupo.getComponente();
						
						if (!pagouComponenteGrupoOuEquivalente(componente, concluidas)) {
							
							int chFaltando = chGrupoFaltando(d, grupo, situacoes, mov);
							if (chFaltando > 0) {
								result.put(grupo, chFaltando);
							} else if (componente != null) {
								CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(d);
								
								// Nesse caso, o aluno não pagou o componente e já integralizou todo o grupo, então deve-se integralizar o componente
								MatriculaComponente mc = new MatriculaComponente();
								mc.setDiscente(d.getDiscente());
								mc.setComponente(componente);
								mc.setDetalhesComponente(componente.getDetalhes());
								mc.setDataCadastro(new Date());
								mc.setAno((short) calendario.getAno());
								mc.setPeriodo((byte) calendario.getPeriodo());
								mc.setTipoIntegralizacao(TipoIntegralizacao.OBRIGATORIA);
								mc.setSituacaoMatricula(SituacaoMatricula.APROVADO);
								
								dao.create(mc);
							}
							
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
	 * Verifica se o aluno pagou o componente associado a um grupo de optativas ou algum componente equivalente a ele.
	 * @param componente
	 * @param concluidas
	 * @return
	 */
	private static boolean pagouComponenteGrupoOuEquivalente(ComponenteCurricular componente, List<MatriculaComponente> concluidas) {
		if (componente != null) {
			TreeSet<Integer> componentes = new TreeSet<Integer>();
			for (MatriculaComponente mc : concluidas) {
				if (mc.getComponente().getId() == componente.getId())
					return true;
				else
					componentes.add(mc.getComponente().getId());
			}
			
			try {
				return ExpressaoUtil.eval(componente.getEquivalencia(), componentes);
			} catch (ArqException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	/**
	 * Identifica, em um grupo de optativas, se um aluno pagou os componentes ou equivalentes
	 * e retorna a carga horária pendente para o grupo.
	 * @param situacoes 
	 */
	@SuppressWarnings("unchecked")
	private static int chGrupoFaltando(DiscenteGraduacao d, GrupoOptativas grupo, Collection<SituacaoMatricula> situacoes, Movimento mov) throws ArqException {
		int chMinima = grupo.getChMinima();
		
		DiscenteDao ddao = getDAO(DiscenteDao.class, mov);
		
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
				if (cc.getComponente().getEquivalencia() != null) {
					ArvoreExpressao expressao = ArvoreExpressao.fromExpressao(cc.getComponente().getEquivalencia());
					if (expressao != null) {
						boolean pagouEquivalente = expressao.eval(idsComponentesCursados);
						boolean equivalenteNaoEstaNoCurriculo = !CollectionUtils.intersection(expressao.getMatches(), idsComponentesCursados).isEmpty();
					
						pagouPorEquivalente = pagouEquivalente && equivalenteNaoEstaNoCurriculo;
					}
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

	/**
	 * Retorna uma instância do DAO cuja classe foi passada como parâmetro.
	 * @param dao
	 * @param mov
	 * @return 
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao, Movimento mov) throws DAOException {
		if (mov != null)
			return DAOFactory.getInstance().getDAOMov(dao, mov);
		else 
			return DAOFactory.getInstance().getDAO(dao);
	}
	
}
