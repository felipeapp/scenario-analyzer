package br.ufrn.sigaa.ensino.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

/**
 *
 * Entidade transiente que reúne as formas de integralização das matrículas.
 *
 * Ao matricular-se numa turma de um componente, os créditos desse componente
 * devem ser contabilizados para esse discente dependendo da presença do
 * componente na estrutura curricular em que o discente está vinculado.
 *
 * @author Andre M Dantas
 *
 */
public class TipoIntegralizacao {

	// 	OBRIGR = disciplina obrigatória
	public static final String OBRIGATORIA = "OB";
	//-- COMPGR = disciplina complementar da grade do aluno
	public static final String OPTATIVA_DA_GRADE =  "OP";

	// COMPOU = disciplina complementar de mesmo curso-cidade do aluno
	public static final String OPTATIVA_CURSO_CIDADE = "OC";

	//-- EQUICO = disciplina equivalente a complementar da grade
	public static final String EQUIVALENTE_OPTATIVA_DA_GRADE = "QO";

	//-- EQUIOU = disciplina equivalente a complementar de mesmo curso-cidade do aluno
	@Deprecated
	public static final String EQUIVALENTE_OPTATIVA_CURSO = "QC"; // NAO USAR!!

	//-- EQUIOB = disciplina equivalente a uma obrigatória do aluno
	public static final String EQUIVALENTE_OBRIGATORIA = "QB";

	//	Disciplina extra-curricular
	public static final String EXTRA_CURRICULAR = "EC";

	public static String getDescricao(String sigla) {
		if (OBRIGATORIA.equals(sigla))
			return "OBRIGATÓRIA";
		if (OPTATIVA_CURSO_CIDADE.equals(sigla) || OPTATIVA_DA_GRADE.equals(sigla))
			return "OPTATIVA";
		if (EQUIVALENTE_OBRIGATORIA.equals(sigla))
			return "EQUIV. OBRIG.";
		if (EQUIVALENTE_OPTATIVA_DA_GRADE.equals(sigla))
			return "EQUIV. OPTAT.";
		if (EXTRA_CURRICULAR.equals(sigla))
			return "ELETIVA";
		return sigla;
	}

	public static String getLegenda(String tipoIntegralizacao, boolean atividade) {
		if (TipoIntegralizacao.OBRIGATORIA.equals(tipoIntegralizacao) && !atividade) {
			return "";
		} else if (TipoIntegralizacao.OBRIGATORIA.equals(tipoIntegralizacao) && atividade) {
			return "@";
		} else if ((TipoIntegralizacao.OPTATIVA_DA_GRADE.equals(tipoIntegralizacao) || TipoIntegralizacao.OPTATIVA_CURSO_CIDADE.equals(tipoIntegralizacao)) && atividade) {
			return "§";
		} else if ((TipoIntegralizacao.OPTATIVA_DA_GRADE.equals(tipoIntegralizacao) || TipoIntegralizacao.OPTATIVA_CURSO_CIDADE.equals(tipoIntegralizacao)) && !atividade) {
			return "*";
		} else if (TipoIntegralizacao.EXTRA_CURRICULAR.equals(tipoIntegralizacao)) {
			return "#";
		} else if (TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(tipoIntegralizacao)) {
			return "e";
		} else if (TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE.equals(tipoIntegralizacao)) {
			return "&";
		} else {
			return "";
		}
	}

	public static boolean isOptativa(String tipoIntegralizacao) {
		return TipoIntegralizacao.EQUIVALENTE_OPTATIVA_CURSO.equals(tipoIntegralizacao)
			|| TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE.equals(tipoIntegralizacao)
			|| TipoIntegralizacao.EXTRA_CURRICULAR.equals(tipoIntegralizacao)
			|| TipoIntegralizacao.OPTATIVA_DA_GRADE.equals(tipoIntegralizacao)
			|| TipoIntegralizacao.OPTATIVA_CURSO_CIDADE.equals(tipoIntegralizacao);		
	}

	public static boolean isObrigatoria(String tipoIntegralizacao) {
		return TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(tipoIntegralizacao)
			|| TipoIntegralizacao.OBRIGATORIA.equals(tipoIntegralizacao);
	}

	public static boolean isEquivalente(String tipo) {
		
		if (isEmpty(tipo))
			return false;
		
		return TipoIntegralizacao.EQUIVALENTE_OBRIGATORIA.equals(tipo) 
				|| TipoIntegralizacao.EQUIVALENTE_OPTATIVA_CURSO.equals(tipo) 
				|| TipoIntegralizacao.EQUIVALENTE_OPTATIVA_DA_GRADE.equals(tipo);
	}
	
}
