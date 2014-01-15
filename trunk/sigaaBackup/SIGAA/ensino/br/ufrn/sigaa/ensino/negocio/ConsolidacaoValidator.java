 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/08/2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AulaFrequencia;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalValidator;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Validações para a consolidação de turmas
 *
 * @author David Pereira
 *
 */
public class ConsolidacaoValidator {

	/**
	 * Faz a validação das regras de negócio para que uma turma seja consolidada.
	 * 
	 * @param dao
	 * @param turma
	 * @param avaliacao
	 * @param parametros
	 * @param metodo
	 * @param comando
	 * @param administradorDAE
	 * @param parcial
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	public static void validar(MatriculaComponenteDao dao, Turma turma, ParametrosGestoraAcademica parametros, Integer metodo, Comando comando, boolean administradorDAE, boolean parcial, ConfiguracoesAva config, Usuario usuario) throws NegocioException, ArqException {
		// recuperando a avaliação institucional
		AvaliacaoInstitucional avaliacao = AvaliacaoInstitucionalHelper.getAvaliacao(usuario, turma);

		List <MatriculaComponente> matriculas = (List<MatriculaComponente>) turma.getMatriculasDisciplina();
		boolean subturmas = turma.isAgrupadora() || turma.getTurmaAgrupadora() != null;
		
		if (turma.isConsolidada()) throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.TURMA_JA_CONSOLIDADA).getMensagem());
		
		//Se for não for PPG, não valida calendário e avaliação institucional
		if (!usuario.isUserInRole(SigaaPapeis.PPG) && !administradorDAE) {
			
			// Lato Sensu e Residência Médica/Multiprofissional não possuem calendário acadêmico
			if (!turma.isLato() && !turma.isResidenciaMedica() && !turma.isTurmaFerias()){
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
				validarExistenciaDoCalendario(cal);
				
				if (parcial) {
					validarPeriodoConsolidacaoParcial(cal);					
				} else {
					validarPeriodoConsolidacaoFinal(turma, cal);
				}
			}
			
			validarPreenchimentoDaAvaliacaoInstitucional(turma, comando, administradorDAE, avaliacao);
		}
		
		// Valida se o professor preencheu o diário de classe eletronicamente
		if (comando.equals(SigaaListaComando.CONSOLIDAR_TURMA)) {
			
			
			// Se for uma atividade coletiva, verifica pelo parâmetro se é necessário validar as frequências e tópicos de aula lançados
			if (!turma.getDisciplina().isAtividadeColetiva() || ParametroHelper.getInstance().getParametroBoolean(ParametrosGerais.ATIVIDADE_ESPECIAL_DEVE_VALIDAR_FREQUENCIAS_E_TOPICOS)){
			
				int chAula = turma.getDisciplina().getDetalhes().getChAula();
				int chTotal = turma.getDisciplina().getDetalhes().getChTotal();
				double ajusteChAula = chAula / (1.0 * chTotal); // Considerar apenas a CH de Aula para a % de tópicos de aula.
				
				int aulasMinimas = (int) Math.floor(ajusteChAula * ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PORCENTAGEM_MINIMA_AULAS_DIARIO));
				int frequenciaMinima = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.PORCENTAGEM_MINIMA_FREQUENCIA_DIARIO);
				String[] niveisEnsino = ParametroHelper.getInstance().getParametroStringArray(ParametrosGerais.NIVEIS_ENSINO_OBRIGATORIEDADE_DIARIO);
				
				CursoDao cursoDao = null;
				try {
	
					cursoDao = DAOFactory.getInstance().getDAO(CursoDao.class);
					
					for (String nivel : niveisEnsino) {
						if (nivel.charAt(0) == turma.getDisciplina().getNivel()) {
							
							// Não valida o diário de classe para turma do tipo módulo.
							// Com exceção de graduação, visto que a regra de horários de graduação é diferente.
							if (!turma.isGraduacao() && turma.getDisciplina().isModulo())
								break;
							
							
							// A porcentagem de aulas lançadas pelo docente.
							double porcAulas = 0;
							
								// Se é uma turma de EAD, a quantidade mínima de aulas é dada pela quantidade de itens no seu programa e não se verificam as frequências.
								if (turma.isEad()){
								
									List <ItemPrograma> itensPrograma = cursoDao.findListaItemProgramaByDisciplina(turma.getDisciplina().getId());
									if (isEmpty(itensPrograma))
										throw new NegocioException (UFRNUtils.getMensagem(MensagensGerais.DISCIPLINA_SEM_ITENS_DO_PROGRAMA, ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.EAD)).getMensagem());
									
									aulasMinimas = itensPrograma.size();
									porcAulas = cursoDao.count("select count (*) from ava.topico_aula where ativo = trueValue() and id_turma = " + turma.getId());
								
								// Se não for uma turma de EAD, calcula-se a porcentagem de aulas lançadas e verificam-se as frequências.
								} else {
									turma.setHorarios((List<HorarioTurma>) cursoDao.findByExactField(HorarioTurma.class, "turma.id", turma.getId()));
														
									if (!isEmpty(turma.getHorarios())) {
										
										List <AulaFrequencia> aulas = TurmaUtil.getListagemAulas(turma);
										
										porcAulas = TurmaUtil.getPorcentagemAulasLancadas(aulas, turma, parametros);
										
										double porcFrequencia = TurmaUtil.getPorcentagemFrequenciaLancada(aulas, turma, parametros);
										if (frequenciaMinima > porcFrequencia && !administradorDAE) {
											throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.POUCAS_FREQUENCIAS_REGISTRADAS).getMensagem());
										}	
									} else {
										porcAulas = aulasMinimas;
									}
								}
	
							if (aulasMinimas > porcAulas && !administradorDAE)
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.POUCAS_AULAS_REGISTRADAS).getMensagem());
		
							break;
						}
					}
				}finally{
					if (cursoDao != null)
						cursoDao.close();
				}
			}
		}
		
		TurmaUtil.setarPesosNotaUnidadeEstrategiaConolidacao(turma);
		
		if (turma.getMatriculasDisciplina() != null) {
			for (MatriculaComponente matricula : turma.getMatriculasDisciplina()) { 
				// Na consolidação parcial, somente os discentes aprovados por média devem ser validados.
				if (parcial && matricula.isEmRecuperacao())
					continue;				
				validarMatricula (matricula, matriculas, turma, parametros, metodo, comando, administradorDAE, parcial, subturmas, config);				
			}			
		}
		
		
		if( isEmpty( turma.getMatriculasDisciplina() ) )
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.CONSOLIDANDO_TURMA_SEM_DISCENTES).getMensagem());
		

		//Impedir que um peso de nota_unidade tenha sido configurado incorretamente.
		if (metodo == MetodoAvaliacao.NOTA && !turma.isEad()) {
			String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);
			for(MatriculaComponente m : turma.getMatriculasDisciplina()) {
				if (!m.isConsolidada()){
					for( NotaUnidade n : m.getNotas() ) {
						if( n.getPeso() != null && !n.getPeso().isEmpty() ) {
							Integer und =  new Integer(n.getUnidade()) -1 ;
							Integer pesoInserido = new Integer(n.getPeso().trim());				
							Integer pesoCorreto = new Integer(pesosAvaliacoes[und].trim());
							if( pesoCorreto.intValue() != pesoInserido.intValue() ) {
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.PESOS_UNIDADES_NAO_CONFIGURADOS).getMensagem());
							}
							
						}
					}		
				}
			}
		}
		
		
	}

	/**
	 * Responsável por validar o preenchimento da avaliação institucional pelo docente, para realizar a consolidação da turma.
	 * @param turma
	 * @param comando
	 * @param administradorDAE
	 * @param avaliacao
	 * @throws DAOException
	 * @throws NegocioException
	 */
	private static void validarPreenchimentoDaAvaliacaoInstitucional(Turma turma, Comando comando,
			boolean administradorDAE, AvaliacaoInstitucional avaliacao) throws DAOException, NegocioException {
		if (comando.equals(SigaaListaComando.CONSOLIDAR_TURMA) && turma.isGraduacao()) { // Graduação 
			boolean avaliacaoDocenteAtiva = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
			if (!administradorDAE && avaliacaoDocenteAtiva && !AvaliacaoInstitucionalValidator.podeConsolidarTurma(avaliacao, turma)) {
				throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.TURMA_NAO_AVALIADA).getMensagem());
			}
		}
	}
	
	/**
	 * Responsável por validar se está no período para consolidação da turma, conforme o calendário acadêmico.
	 * @param turma
	 * @param cal
	 * @throws NegocioException
	 */
	private static void validarPeriodoConsolidacaoFinal(Turma turma, CalendarioAcademico cal) throws NegocioException {
		Date inicioConsolidacaoTurma = cal.getInicioConsolidacaoTurma();
		Date fimConsolidacaoTurma = cal.getFimConsolidacaoTurma();
		
		if (turma.getDisciplina().getNivel() == NivelEnsino.LATO){
			CursoLato cursoLato = (CursoLato) turma.getCurso();
			inicioConsolidacaoTurma = cursoLato.getDataInicio();
			fimConsolidacaoTurma = cursoLato.getDataFim();
		}
		
		if ( isEmpty(inicioConsolidacaoTurma) || turma.getDisciplina().isModuloOuAtividadeColetiva() ) {
			inicioConsolidacaoTurma = turma.getDataInicio();
		}
		
		if (turma.getDisciplina().isModuloOuAtividadeColetiva())
			fimConsolidacaoTurma = null;
		
		if( !CalendarUtils.isDentroPeriodoAberto(inicioConsolidacaoTurma, fimConsolidacaoTurma) ) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO, 
					inicioConsolidacaoTurma != null ? sdf.format(inicioConsolidacaoTurma) : "( EM ABERTO )", 
					fimConsolidacaoTurma != null ? sdf.format(fimConsolidacaoTurma) : "( EM ABERTO )").getMensagem());
		}
	}

	/**
	 * Responsável por validar se está no período para consolidação parcial da turma, conforme o calendário acadêmico.
	 * @param cal
	 * @throws NegocioException
	 */
	private static void validarPeriodoConsolidacaoParcial(CalendarioAcademico cal) throws NegocioException {
		if( (!ValidatorUtil.isEmpty(cal.getInicioConsolidacaoParcialTurma()) && !ValidatorUtil.isEmpty(cal.getFimConsolidacaoParcialTurma()))) {
			if (!CalendarUtils.isDentroPeriodo(cal.getInicioConsolidacaoParcialTurma(), cal.getFimConsolidacaoParcialTurma())) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO_PARCIAL, sdf.format(cal.getInicioConsolidacaoParcialTurma()), sdf.format(cal.getFimConsolidacaoParcialTurma())).getMensagem());
			}
		}
	}

	/**
	 * Verifica a existência de calendário acadêmico.
	 * @param cal
	 * @throws NegocioException
	 */
	private static void validarExistenciaDoCalendario(CalendarioAcademico cal) throws NegocioException {
		if(ValidatorUtil.isEmpty(cal)) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.CALENDARIO_NAO_DEFINIDO).getMensagem());
		}
	}

	/**
	 * Faz a validação das regras de negócio da matrícula.
	 * 
	 * @param matricula
	 * @param matriculas
	 * @param turma
	 * @param parametros
	 * @param metodo
	 * @param comando
	 * @param administradorDAE
	 * @param parcial
	 * @param subturmas
	 * @throws NegocioException
	 */
	private static void validarMatricula (MatriculaComponente matricula , List <MatriculaComponente> matriculas , Turma turma , 
			ParametrosGestoraAcademica parametros, Integer metodo ,	Comando comando, boolean administradorDAE, 
			boolean parcial, boolean subturmas , ConfiguracoesAva config) throws NegocioException {
				
		// Valida se a nota dada a recuperação é uma nota inválida (menor que 0 ou maior que 10)
		if (matricula.getRecuperacao() != null && (matricula.getRecuperacao() < 0.0 || matricula.getRecuperacao() > 10.0)){
			if (subturmas){
				turma.setMatriculasDisciplina(matriculas);
				if (matricula.getTurma().getId() != turma.getId())
					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS_SUBTURMA).getMensagem());
			}
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
		}

		// Valida se o usuário digitou uma nota para a recuperação mesmo sem o discente estar em recuperação (apenas se não for EAD)
		if (!matricula.isEmRecuperacao() && matricula.getRecuperacao() != null && matricula.getTurma().getPolo() == null){
			if (subturmas)
				turma.setMatriculasDisciplina(matriculas);
			
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_APROVADO_COM_NOTA_DE_RECUPERACAO, matricula.getDiscente().getNome()).getMensagem());
		}

		// Valida se o número total de faltas digitado foi maior que o número total de aulas do componente curricular (Retirado em 15/12/2008 devido à tarefa 11649)
		if (!matricula.isConsolidada() && (matricula.getNumeroFaltas() != null && matricula.getNumeroFaltas() > turma.getChTotalTurma()))
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NUMERO_DE_FALTAS_MAIOR_QUE_TOTAL_DE_AULAS, matricula.getComponenteCHTotal()).getMensagem());

		if (metodo == MetodoAvaliacao.NOTA) {
			if (!isEmpty(matricula.getNotas())) {
				for (NotaUnidade nota : matricula.getNotas()) {
					// Verifica se o usuário está tentando consolidar a turma mas ainda não cadastrou todas as notas
					if (!matricula.isConsolidada() && nota.getNota() == null && comando.equals(SigaaListaComando.CONSOLIDAR_TURMA)){
						if (subturmas) {
							turma.setMatriculasDisciplina(matriculas);									
						}
						
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_NOTAS_DISCENTE_TURMA, matricula.getDiscente().getNome(), matricula.getTurma().getCodigo()).getMensagem());
					}

					// Verifica se alguma nota tem valor inválido (menor que 0 ou maior que 10)
					if (nota.getNota() != null && (nota.getNota() < 0.0 || nota.getNota() > 10.0)){
						if (subturmas){
							turma.setMatriculasDisciplina(matriculas);
							if (matricula.getTurma().getId() != turma.getId())
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS_SUBTURMA).getMensagem());
						}
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
					}
					

					//Tentando consolidar turma com discente em recuperação e sem nota de recuperação cadastrada.
					//Se o aluno estiver reprovado por falta não precisa de nota da recuperação
					if(!matricula.isReprovadoFalta(parametros.getFrequenciaMinima(),parametros.getMinutosAulaRegular()) && matricula.isEmRecuperacao() && !parcial) {
						if(matricula.getRecuperacao() == null)
							throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO_TURMA, matricula.getTurma().getCodigo(), matricula.getDiscente().getMatriculaNome()).getMensagem());
					}						

					// Verifica se as notas das avaliações têm valor inválido (menor que 0 ou maior que 10)
					if (nota.getAvaliacoes() != null && !nota.getAvaliacoes().isEmpty()) {
						for (Avaliacao aval : nota.getAvaliacoes()) {
							if (aval.getNota() != null && (aval.getNota() < 0.0 || aval.getNota() > 10.0)){
								if (subturmas){
									turma.setMatriculasDisciplina(matriculas);
									if (matricula.getTurma().getId() != turma.getId())
										throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS_SUBTURMA).getMensagem());
								}
								
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
							}
							
							if(aval.getNota() == null && !matricula.isConsolidada()) {
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_NOTAS).getMensagem());
							}
							
						}
					}
					
					if (nota.getAvaliacoes() != null && !nota.getAvaliacoes().isEmpty() && config != null){
						if (config.getTipoMediaAvaliacoes(nota.getUnidade()) == NivelEnsino.STRICTO){
							Double notaMaxima = 0.0;
							for (Avaliacao aval : nota.getAvaliacoes()) {
								if (aval.getNotaMaxima() != null )
									notaMaxima += (aval.getNotaMaxima() * 10D);
							}
							notaMaxima = notaMaxima / 10D;
							if ( notaMaxima < 10  && comando.equals(SigaaListaComando.CONSOLIDAR_TURMA))
								throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.SOMA_AVALIACOES_MAIOR_QUE_DEZ, nota.getUnidade()).getMensagem());
						}
					}
				}
			} else {
				if (!matricula.isConsolidada()){
					if (subturmas) {
						turma.setMatriculasDisciplina(matriculas);
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_SEM_NOTAS_TURMA, matricula.getDiscente().getNome(), matricula.getTurma().getCodigo()).getMensagem());
					}
	
					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_SEM_NOTAS, matricula.getDiscente().getNome()).getMensagem());
				}
			}
			
			if (!matricula.isConsolidada() && turma.getDisciplina().getNivel() != NivelEnsino.LATO && !matricula.isReprovadoFalta(parametros.getFrequenciaMinima(),parametros.getMinutosAulaRegular()) && matricula.isEmRecuperacao()) {
				if (matricula.getRecuperacao() == null && comando.equals(SigaaListaComando.CONSOLIDAR_TURMA) && matricula.getTurma().getPolo() == null){
					if (subturmas)
						turma.setMatriculasDisciplina(matriculas);

					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_NOTAS_DISCENTE, matricula.getDiscente().getNome()).getMensagem());
				}
				
				if (matricula.getRecuperacao() != null && (matricula.getRecuperacao() < 0.0 || matricula.getRecuperacao() > 10.0)){
					if (subturmas){
						turma.setMatriculasDisciplina(matriculas);
						if (matricula.getTurma().getId() != turma.getId())
							throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS_SUBTURMA).getMensagem());
					}

					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
				}
			}
			
		} else if (!matricula.isConsolidada() && comando.equals(SigaaListaComando.CONSOLIDAR_TURMA) && metodo == MetodoAvaliacao.COMPETENCIA && matricula.getApto() == null) {
			if (subturmas)
				turma.setMatriculasDisciplina(matriculas);

			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_COMPETENCIAS).getMensagem());
		} else if (!matricula.isConsolidada() && comando.equals(SigaaListaComando.CONSOLIDAR_TURMA) && metodo == MetodoAvaliacao.CONCEITO && matricula.getConceito() == null) {
			if (subturmas)
				turma.setMatriculasDisciplina(matriculas);

			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_CONCEITOS).getMensagem());
		}
	}
	
}
