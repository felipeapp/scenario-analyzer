/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 18/11/2008 
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
 * Processador para recalcular as cargas horárias de estruturas curriculares
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
				 * O calculo dos discentes eh necessário quando o recalculo da estrutura curricular se dá devido a 
				 * alteração da carga horária de algum componente do currículo
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
