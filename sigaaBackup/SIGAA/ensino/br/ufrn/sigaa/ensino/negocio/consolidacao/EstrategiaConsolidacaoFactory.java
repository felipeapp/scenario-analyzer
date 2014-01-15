package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Interface que define os métodos do factory da estratégia de consolidação.
 * Sempre que alguma instituição personalizar a estratégia de consolidação será 
 * necessário implementar esta interface para definir quais as implementações das estratégias serão utilizadas
 * A implementação a ser utilizada no SIGAA é definida através do parametro de código 2_10100_35 - ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY
 * 
 * @author Victor Hugo
 *
 */
public interface EstrategiaConsolidacaoFactory {

	/**
	 * Retorna a estratégia de consolidação a partir do discente
	 * @param discente
	 * @param params
	 * @return
	 */
	public EstrategiaConsolidacao getEstrategia(DiscenteAdapter discente, ParametrosGestoraAcademica params);
	
	/**
	 * Retorna a estratégia de consolidação a partir da turma
	 * @param turma
	 * @param params
	 * @return
	 */
	public EstrategiaConsolidacao getEstrategia(Turma turma, ParametrosGestoraAcademica params) throws DAOException;
	
}
