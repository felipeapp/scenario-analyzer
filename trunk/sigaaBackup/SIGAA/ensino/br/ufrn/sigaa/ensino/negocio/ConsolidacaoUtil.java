package br.ufrn.sigaa.ensino.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe utilitária para consolidação
 * 
 * @author Henrique André
 *
 */
public class ConsolidacaoUtil {

	/**
	 * Váriavel que armazena as situações de matrícula que são de interesse no momento da consolidação
	 */
	private static List<SituacaoMatricula> situacaoesAConsolidar = new ArrayList<SituacaoMatricula>();
	
	static {
		situacaoesAConsolidar.add(SituacaoMatricula.MATRICULADO);
		situacaoesAConsolidar.add(SituacaoMatricula.APROVADO);
		situacaoesAConsolidar.add(SituacaoMatricula.REPROVADO);
		situacaoesAConsolidar.add(SituacaoMatricula.REPROVADO_FALTA);
		situacaoesAConsolidar.add(SituacaoMatricula.REPROVADO_MEDIA_FALTA);
	}

	public static List<SituacaoMatricula> getSituacaoesAConsolidar() {
		return situacaoesAConsolidar;
	}
	
}
