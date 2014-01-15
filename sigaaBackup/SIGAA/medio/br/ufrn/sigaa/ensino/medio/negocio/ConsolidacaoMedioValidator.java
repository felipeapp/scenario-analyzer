 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/08/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalValidator;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Validações para a consolidação de turmas de Ensino médio.
 *
 * @author Rafael Gomes
 *
 */
public class ConsolidacaoMedioValidator {

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
	 * @throws DAOException
	 */
	public static void validar(MatriculaComponenteDao dao, List<NotaDisciplina> notasDisciplina, Turma turma, AvaliacaoInstitucional avaliacao, 
			ParametrosGestoraAcademica parametros, Integer metodo, Comando comando, boolean gestorMedio, 
			boolean parcial, ConfiguracoesAva config, Usuario usuario) throws NegocioException, DAOException {

		if (turma.isConsolidada()) throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.TURMA_JA_CONSOLIDADA).getMensagem());		

		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
		if(ValidatorUtil.isEmpty(cal)) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.CALENDARIO_NAO_DEFINIDO).getMensagem());
		}
		if( (!ValidatorUtil.isEmpty(cal.getInicioConsolidacaoTurma()) && !ValidatorUtil.isEmpty(cal.getFimConsolidacaoTurma())) && !parcial ) {
			if(!CalendarUtils.isDentroPeriodo(cal.getInicioConsolidacaoTurma(),cal.getFimConsolidacaoTurma())) {			
				throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO, cal.getInicioConsolidacaoTurma(), cal.getFimConsolidacaoTurma()).getMensagem());
			}
		}
		if( (!ValidatorUtil.isEmpty(cal.getInicioConsolidacaoParcialTurma()) && !ValidatorUtil.isEmpty(cal.getFimConsolidacaoParcialTurma())) && parcial ) {
			if(!CalendarUtils.isDentroPeriodo(cal.getInicioConsolidacaoParcialTurma(), cal.getFimConsolidacaoParcialTurma())) {			
				throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NAO_ESTA_EM_PERIODO_DE_CONSOLIDACAO_PARCIAL, cal.getInicioConsolidacaoParcialTurma(), cal.getFimConsolidacaoParcialTurma()).getMensagem());
			}
		}					

		// Valida se as questões gerais e as questões relativas à turma na Avaliação Institucional foram respondidas
		if (comando.equals(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO) && turma.isGraduacao() && turma.getPolo() == null) { // Graduação e não ensino a distância
			boolean avaliacaoDocenteAtiva = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
			if (!gestorMedio && avaliacaoDocenteAtiva && !AvaliacaoInstitucionalValidator.podeConsolidarTurma(avaliacao, turma)) {
				throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.TURMA_NAO_AVALIADA).getMensagem());
			}
		}
		
		ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
		EstrategiaConsolidacao estrategia = factory.getEstrategia(turma, param);
				
		int totalDiscentes = 0;
		if (notasDisciplina != null) {
			
			for (NotaDisciplina notaDisc : notasDisciplina){
				
				//seta a estratégia caso não informada
				if (notaDisc.getMatricula().getEstrategia() == null)
					notaDisc.getMatricula().setEstrategia(estrategia);		
				
				// Na consolidação parcial, somente os discentes aprovados por média devem ser validados.
				if (parcial && (notaDisc.getMatricula().isEmRecuperacao() || notaDisc.getMatricula().isConsolidada()))
					continue;
				
				totalDiscentes++;
				
				// Valida se a nota dada a recuperação é uma nota inválida (menor que 0 ou maior que 10)
				if (notaDisc.getMatricula().getRecuperacao() != null && (notaDisc.getMatricula().getRecuperacao() < 0.0 || notaDisc.getMatricula().getRecuperacao() > 10.0)){
					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
				}				
				
				// Valida se o usuário digitou uma nota para a recuperação mesmo sem o discente estar em recuperação
				if (!notaDisc.getMatricula().isEmRecuperacao() && notaDisc.getMatricula().getRecuperacao() != null ){
					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_APROVADO_COM_NOTA_DE_RECUPERACAO).getMensagem());
				}		
				
				// Valida se o número total de faltas digitado foi maior que o número total de aulas do componente curricular 
				if (!notaDisc.getMatricula().isConsolidada() && notaDisc.getMatricula().getNumeroFaltas() > turma.getChTotalTurma())
					throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.NUMERO_DE_FALTAS_MAIOR_QUE_TOTAL_DE_AULAS, notaDisc.getMatricula().getComponenteCHTotal()).getMensagem());
				
				for (NotaSerie notaSerie : notaDisc.getNotas()){
					
					// Verifica se as notas das avaliações têm valor inválido (menor que 0 ou maior que 10)
					if (notaSerie.getNotaUnidade() != null && notaSerie.getNotaUnidade().getNota() != null && (notaSerie.getNotaUnidade().getNota() < 0.0 || notaSerie.getNotaUnidade().getNota() > 10.0))
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());

					// Verifica se o usuário está tentando consolidar a turma mas ainda não cadastrou todas as notas
					if(!notaDisc.getMatricula().isConsolidada() && notaSerie.getRegraNota().isNota() && notaSerie.getNotaUnidade().getNota() == null)
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.FALTAM_NOTAS).getMensagem());

				}
								
				//Tentando consolidar turma com discente em recuperação e sem nota de recuperação cadastrada.
				//Se o aluno estiver reprovado por falta não precisa de nota da recuperação
				if(!notaDisc.getMatricula().isReprovadoFalta(parametros.getFrequenciaMinima(),parametros.getMinutosAulaRegular()) && notaDisc.getMatricula().isEmRecuperacao() && !parcial) {
					if(notaDisc.getMatricula().getRecuperacao() == null)
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_EM_RECUPERACAO_SEM_NOTA_DE_RECUPERACAO, notaDisc.getMatricula().getDiscente().getMatriculaNome()).getMensagem());
				}						
				
				if (!notaDisc.getMatricula().isConsolidada() && !notaDisc.getMatricula().isReprovadoFalta(parametros.getFrequenciaMinima(),parametros.getMinutosAulaRegular()) && notaDisc.getMatricula().isEmRecuperacao()) {
					if (notaDisc.getMatricula().getRecuperacao() == null && comando.equals(SigaaListaComando.CONSOLIDAR_DISCIPLINA_MEDIO)){
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.DISCENTE_SEM_NOTAS, notaDisc.getMatricula().getDiscente().getNome()).getMensagem());
					}
					
					if (notaDisc.getMatricula().getRecuperacao() != null && (notaDisc.getMatricula().getRecuperacao() < 0.0 || notaDisc.getMatricula().getRecuperacao() > 10.0)){
						throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.EXISTEM_NOTAS_INVALIDAS).getMensagem());
					}
				}					
				
			}
		
		}
		
		if (parcial && totalDiscentes == 0)
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.CONSOLIDANDO_PARCIALMENTE_SEM_DISCENTES_APROVADOS).getMensagem());
		
		if( isEmpty( turma.getMatriculasDisciplina() ) ){
			throw new NegocioException(UFRNUtils.getMensagem(MensagensGerais.CONSOLIDANDO_TURMA_SEM_DISCENTES).getMensagem());
		}		
		
	}

}
