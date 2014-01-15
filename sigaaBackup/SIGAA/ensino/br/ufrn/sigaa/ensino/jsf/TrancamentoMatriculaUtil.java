package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/**
 * Classe auxiliar
 * 
 * @author Henrique Andr�
 *
 */
public class TrancamentoMatriculaUtil {
	
	/**
	 * Retorna o �ltimo dia de trancamento para a unidade informada.
	 * 
	 * @param u
	 * @param calendario
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static Date calcularPrazoLimiteTrancamentoTurma(Unidade u, CalendarioAcademico calendario) throws NegocioException, DAOException { 
		
		if (isEmpty(u))
			throw new NegocioException("A Unidade Acad�mica n�o foi definida para realizar o c�lculo de trancamento.");
		
		if (isEmpty(calendario))
			throw new NegocioException("Calend�rio acad�mico n�o foi definido para realizar o c�lculo de trancamento.");
		
		if(calendario.getInicioPeriodoLetivo() == null || calendario.getFimPeriodoLetivo() == null )
			throw new NegocioException("N�o foi poss�vel realizar o c�lculo de trancamento pois, os per�odos letivos n�o foram informados no calend�rio acad�mico.");
		
		
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(u, calendario.getNivel(), null, null, null);
		
		Float percentualMaxTracamento;
		
		if( calendario.isStricto() ) {
			Double paramLimiteTracamento = ParametroHelper.getInstance().getParametroDouble(ParametrosStrictoSensu.LIMITE_MAXIMO_SEMESTRE_POSSIBILITA_TRANCAMENTO);
			percentualMaxTracamento = paramLimiteTracamento.floatValue();
		}
		else {
			percentualMaxTracamento = parametros.getPercentualMaximoCumpridoTrancamento();
		}
		
		int quantDiasTotal = (int) Math.floor( CalendarUtils.calculoDias(calendario.getInicioPeriodoLetivo(), calendario.getFimPeriodoLetivo()) );
		
		int quantDiasPermitido = (int) Math.floor ((quantDiasTotal * percentualMaxTracamento) / 100);
		
		return CalendarUtils.adicionaDias(calendario.getInicioPeriodoLetivo(), quantDiasPermitido);
		
	}
	
	/**
	 * Retorna o �ltimo dia permitido para trancamento da turma.
	 * 
	 * @param turma
	 * @param param
	 * @return
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static Date calcularPrazoLimiteTrancamentoTurma(Turma turma, CalendarioAcademico calendario) throws NegocioException, DAOException { 
		
		if (turma == null)
			throw new NegocioException("N�o foi poss�vel calcular o prazo limite de trancamento porque a turma n�o foi informada.");
		
		if (turma.getDataInicio() == null)
			throw new NegocioException("N�o foi poss�vel calcular o prazo limite de trancamento porque a data de �nicio da turma n�o foi informada.");

		if (turma.getDataFim() == null)
			throw new NegocioException("N�o foi poss�vel calcular o prazo limite de trancamento porque a data fim da turma n�o foi informada.");

		if (turma.isGraduacao() && !turma.getDisciplina().isModulo()) {
			return null;
		}
		
		if (calendario == null)
			throw new NegocioException("Calend�rio acad�mico n�o foi definido para realizar o c�lculo de trancamento.");
		
		Double percentualMaxTracamento = percentualMaxTracamento(turma);
		int quantDiasTotal = (int) Math.floor( CalendarUtils.calculoDias(turma.getDataInicio(), turma.getDataFim()) );
		int quantDiasPermitido = (int) Math.floor ((quantDiasTotal * percentualMaxTracamento) / 100);
		
		
		return CalendarUtils.adicionaDias(turma.getDataInicio(), quantDiasPermitido);		
	}
	
	/**
	 * Retorna o porcentagem m�ximo para trancamento
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static Double percentualMaxTracamento(Turma turma) throws DAOException, NegocioException {
		
		if( turma.isStricto() && !turma.getDisciplina().isModulo() ) {
			return ParametroHelper.getInstance().getParametroDouble(ParametrosStrictoSensu.LIMITE_MAXIMO_SEMESTRE_POSSIBILITA_TRANCAMENTO);
		}
		
		if (turma.getDisciplina().isModulo()) {
			ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(turma);
			
			if (parametros != null)
				return new Double(parametros.getPercentualMaximoCumpridoTrancamento());
		}
		
		return 100D;
		
	}
			
}
