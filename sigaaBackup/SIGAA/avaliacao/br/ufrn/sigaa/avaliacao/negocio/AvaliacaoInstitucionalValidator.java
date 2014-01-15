/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 26/05/2008 
 */
package br.ufrn.sigaa.avaliacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;

/**
 * Validações para a avaliação institucional 
 * 
 * @author David Pereira
 *
 */
public class AvaliacaoInstitucionalValidator {

	/**
	 * Identifica se, dado o estado atual de preenchimento da avaliação, o docente
	 * pode consolidar a turma. O docente só pode consolidar se ele tiver respondido
	 * às perguntas gerais e às perguntas especificas para a turma a ser consolidada.
	 * @throws DAOException 
	 */
	public static boolean podeConsolidarTurma(AvaliacaoInstitucional avaliacao, Turma turma) throws DAOException {
		boolean pode = true;
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
		int anoAvaliacao = cal.getAno();
		int periodoAvaliacao = cal.getPeriodo();
		boolean ativa  = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		// Se a turma for de graduação e presencial e a avaliação for ativa e o ano-período da avaliação for o mesmo da turma, verifica se o professor efetua a verificação
		if (turma.isGraduacao() && ativa && turma.isAnoPeriodo(anoAvaliacao, periodoAvaliacao)) {
			if (avaliacao == null || isEmpty(avaliacao.getRespostas())) {
				pode = false;
			} else if (avaliacao.isFinalizada()) {
				pode = true;
			} else {
				// verifica se todas as respostas à turma foram dadas.
				for (RespostaPergunta resp : avaliacao.getRespostas()) {
					if (resp.getPergunta().isAvaliarTurmas()) {
						if (resp.getDocenteTurma() != null && turma.equals(resp.getDocenteTurma().getTurma()) && resp.getResposta() == null) {
							pode = false;
							break;
						}
					} else {
						if (resp.getResposta() == null) {
							pode = false;
							break;
						}
					}
				}
			}
		}
		return pode;
	}
	
}
