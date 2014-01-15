/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 18/07/2011
 */

package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Classe Utilitária para centralizar o cálculo dos totais de componente curricular
 * 
 * @author Henrique André
 *
 */
public class ComponenteCurricularTotaisHelper {

	/**
	 * Realiza o cálculo dos totais
	 * @param cc
	 * @throws DAOException
	 */
	public static void calcularTotais(ComponenteCurricular cc) throws DAOException {
		
		ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(cc);
		
		if (!cc.isTecnico() && !cc.isMedio()) {
			if (cc.isDisciplina()) {
				cc.getDetalhes().setChAula(params.getHorasCreditosAula() * cc.getDetalhes().getCrAula());
				cc.getDetalhes().setChLaboratorio(params.getHorasCreditosLaboratorio() * cc.getDetalhes().getCrLaboratorio());
				cc.getDetalhes().setChEstagio(params.getHorasCreditosEstagio() * cc.getDetalhes().getCrEstagio());
				cc.getDetalhes().setChEad(params.getHorasCreditosAula() * cc.getDetalhes().getCrEad());
			} else {
				// se não é disciplina, zera os créditos.
				cc.getDetalhes().setCrAula(0);
				cc.getDetalhes().setCrEad(0);
				cc.getDetalhes().setCrEstagio(0);
				cc.getDetalhes().setCrLaboratorio(0);
				cc.getDetalhes().setCrTotal(0);
			}
		}		
	
		if (cc.isBloco()) {
			int crTotal = 0;
			int chTotal = 0;
			for (ComponenteCurricular subUnidade : cc.getSubUnidades()) {
				if( subUnidade.isDisciplina() )
					crTotal += subUnidade.getCrTotal();
				else if( subUnidade.isModuloOuAtividadeColetiva() )
					chTotal += subUnidade.getChTotal();
			}
			
		cc.getDetalhes().setChAula(chTotal + (params.getHorasCreditosAula() * crTotal));
		cc.getDetalhes().setCrLaboratorio(crTotal);				
		}
		
		// Força o recalculo
		cc.getDetalhes().calcularCHTotal();
	}
	
}
