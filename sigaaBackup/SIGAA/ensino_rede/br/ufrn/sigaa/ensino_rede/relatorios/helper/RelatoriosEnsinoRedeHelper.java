package br.ufrn.sigaa.ensino_rede.relatorios.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.sigaa.ensino_rede.dominio.MatriculaComponenteRede;

public class RelatoriosEnsinoRedeHelper {

	@SuppressWarnings("unchecked")
	public static List<MatriculaComponenteRede> calculoTaxa(Collection resultadosBusca, List filtros) {
		List<MatriculaComponenteRede> matriculas = (List<MatriculaComponenteRede>) resultadosBusca;
		List<MatriculaComponenteRede> resultado = new ArrayList<MatriculaComponenteRede>();
		int total = 0;
		int taxa = 0;
		int componenteAtual = 0;
		for (int i = 0; i < matriculas.size(); i++) {
			MatriculaComponenteRede matricula = matriculas.get(i);
			if ( componenteAtual != 0 && componenteAtual != matricula.getTurma().getComponente().getId() ) {
				matricula.setTotal( (double) ((taxa * 100) / total) );
				resultado.add(matricula);
			}
			if ( filtros.contains(String.valueOf(matricula.getSituacao().getId())) )
				taxa++;
			total++;
			componenteAtual = matricula.getTurma().getComponente().getId();
			
			if ( i == matriculas.size() - 1 ) {
				matricula.setTotal( (double) (taxa != 0 ? (taxa * 100) / total : 0));
				resultado.add(matricula);
			}
			
		}
		
		return resultado;
	}
	
}