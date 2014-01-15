package br.ufrn.sigaa.ensino.negocio.consolidacao;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Interface que define os m�todos do factory da estrat�gia de consolida��o.
 * Sempre que alguma institui��o personalizar a estrat�gia de consolida��o ser� 
 * necess�rio implementar esta interface para definir quais as implementa��es das estrat�gias ser�o utilizadas
 * A implementa��o a ser utilizada no SIGAA � definida atrav�s do parametro de c�digo 2_10100_35 - ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY
 * 
 * @author Victor Hugo
 *
 */
public interface EstrategiaConsolidacaoFactory {

	/**
	 * Retorna a estrat�gia de consolida��o a partir do discente
	 * @param discente
	 * @param params
	 * @return
	 */
	public EstrategiaConsolidacao getEstrategia(DiscenteAdapter discente, ParametrosGestoraAcademica params);
	
	/**
	 * Retorna a estrat�gia de consolida��o a partir da turma
	 * @param turma
	 * @param params
	 * @return
	 */
	public EstrategiaConsolidacao getEstrategia(Turma turma, ParametrosGestoraAcademica params) throws DAOException;
	
}
