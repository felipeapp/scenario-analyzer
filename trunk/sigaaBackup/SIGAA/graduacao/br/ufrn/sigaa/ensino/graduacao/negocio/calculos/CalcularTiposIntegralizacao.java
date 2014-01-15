/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * Classe para calcular os tipos de integralização das matrículas
 * de um discente de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalcularTiposIntegralizacao extends CalculosDiscenteChainNode<DiscenteGraduacao> {

	@Override
	public void processar(DiscenteGraduacao discente, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {

		MatriculaComponenteDao mcdao = getDAO(MatriculaComponenteDao.class, mov);
		
		try {
			// matrículas do aluno (pagas e matriculadas)
			Collection<MatriculaComponente> matriculas = mcdao.findPagaseMatriculadasByDiscente(discente);
			
			// Analisa as matrículas
			Collection<AlteracaoMatricula> alteracoes = IntegralizacoesHelper.analisarTipoIntegralizacao(discente, matriculas, mov);
			
			// Persiste as alterações de intergralização detectadas nas matrículas
			if (isNotEmpty(alteracoes))
				mcdao.persistirAlteracoes(alteracoes);
			
		} finally {
			mcdao.close();
		}
	}
	


}
