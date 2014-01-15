/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 18/07/2011
 */

package br.ufrn.sigaa.ensino.negocio;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * Classe Utilit�ria para centralizar o c�lculo dos totais de componente curricular
 * 
 * @author Henrique Andr�
 *
 */
public class ComponenteCurricularTotaisHelper {

	/**
	 * Realiza o c�lculo dos totais
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
				// se n�o � disciplina, zera os cr�ditos.
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
		
		// For�a o recalculo
		cc.getDetalhes().calcularCHTotal();
	}
	
}
