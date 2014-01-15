/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/03/2010
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.ensino.dominio.AlteracaoMatricula;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;

/**
 * Classe para calcular os tipos de integraliza��o das matr�culas
 * de um discente de gradua��o.
 * 
 * @author David Pereira
 *
 */
public class CalcularTiposIntegralizacao extends CalculosDiscenteChainNode<DiscenteGraduacao> {

	@Override
	public void processar(DiscenteGraduacao discente, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {

		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		
		try {
			// matr�culas do aluno (pagas e matriculadas)
			Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(discente);
			
			// Analisa as matr�culas
			Collection<AlteracaoMatricula> alteracoes = IntegralizacoesHelper.analisarTipoIntegralizacao(discente, matriculas, mov);
			
			// Persiste as altera��es de intergraliza��o detectadas nas matr�culas
			if (isNotEmpty(alteracoes))
				mcdao.persistirAlteracoes(alteracoes);
			
		} finally {
			mcdao.close();
		}
	}
	


}
