/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 18/11/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;


/**
 * Processador para recalcular as cargas hor�rias de estruturas curriculares
 * 
 * @author David Pereira
 * @author Victor Hugo
 *
 */
public class ProcessadorRecalculoEstruturasCurriculares extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoRecalculoEstruturaCurricular mov = (MovimentoRecalculoEstruturaCurricular) movimento;
		
		int id = movimento.getId();
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class, mov); 
		
		try {
		
			Curriculo curriculo = dao.findByPrimaryKey(id, Curriculo.class);
			CurriculoHelper.calcularTotaisCurriculo(curriculo);
			
			dao.update(curriculo);
			
			if( mov.isRecalcularDiscentes() ){
				/** deve recalcular os discentes que vinculados a esta estrutura curricular 
				 * O calculo dos discentes eh necess�rio quando o recalculo da estrutura curricular se d� devido a 
				 * altera��o da carga hor�ria de algum componente do curr�culo
				 * */
				dao.zerarUltimasAtualizacoes(curriculo);
			}
			
		} finally {
			dao.close();
		}
		
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
