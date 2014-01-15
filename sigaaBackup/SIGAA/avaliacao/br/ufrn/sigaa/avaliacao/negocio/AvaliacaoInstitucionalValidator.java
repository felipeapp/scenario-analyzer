/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 26/05/2008 
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
 * Valida��es para a avalia��o institucional 
 * 
 * @author David Pereira
 *
 */
public class AvaliacaoInstitucionalValidator {

	/**
	 * Identifica se, dado o estado atual de preenchimento da avalia��o, o docente
	 * pode consolidar a turma. O docente s� pode consolidar se ele tiver respondido
	 * �s perguntas gerais e �s perguntas especificas para a turma a ser consolidada.
	 * @throws DAOException 
	 */
	public static boolean podeConsolidarTurma(AvaliacaoInstitucional avaliacao, Turma turma) throws DAOException {
		boolean pode = true;
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
		int anoAvaliacao = cal.getAno();
		int periodoAvaliacao = cal.getPeriodo();
		boolean ativa  = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		// Se a turma for de gradua��o e presencial e a avalia��o for ativa e o ano-per�odo da avalia��o for o mesmo da turma, verifica se o professor efetua a verifica��o
		if (turma.isGraduacao() && ativa && turma.isAnoPeriodo(anoAvaliacao, periodoAvaliacao)) {
			if (avaliacao == null || isEmpty(avaliacao.getRespostas())) {
				pode = false;
			} else if (avaliacao.isFinalizada()) {
				pode = true;
			} else {
				// verifica se todas as respostas � turma foram dadas.
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
