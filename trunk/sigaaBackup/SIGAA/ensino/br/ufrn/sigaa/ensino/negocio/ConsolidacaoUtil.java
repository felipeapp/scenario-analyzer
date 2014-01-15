package br.ufrn.sigaa.ensino.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;

/**
 * Classe utilit�ria para consolida��o
 * 
 * @author Henrique Andr�
 *
 */
public class ConsolidacaoUtil {

	/**
	 * V�riavel que armazena as situa��es de matr�cula que s�o de interesse no momento da consolida��o
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
