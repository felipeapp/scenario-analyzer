/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Apr 10, 2007
 *
 */
package br.ufrn.sigaa.eleicao.negocio;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.eleicao.VotoDao;
import br.ufrn.sigaa.eleicao.dominio.Eleicao;
import br.ufrn.sigaa.eleicao.dominio.Voto;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * 
 * @author Victor Hugo
 *
 */
public class VotoValidator {

	/**
	 * valida se a elei��o esta apta para vota��o
	 * @param eleicao
	 * @param lista
	 */
	public static void validarEleicao(Eleicao eleicao, ListaMensagens lista){
		
		Date hoje = new Date();
//		hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
		
		int result = hoje.compareTo( eleicao.getDataFim() );
		if( result > 0 )
			lista.addErro("O prazo para vota��o nesta elei��o est� encerrado.");
			
	}
		
	/**
	 * valida se um discente pode votar em uma elei��o
	 * discente n�o pode votar em uma elei��o mais de uma vez.
	 * @param eleicao
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 */
	public static void validarVotacao(Eleicao eleicao, DiscenteAdapter discente, ListaMensagens lista) throws DAOException{
		
		VotoDao dao = DAOFactory.getInstance().getDAO( VotoDao.class );
		
		try {
			Voto v = dao.findByEleicaoDiscente( eleicao.getId(), discente.getId() );		
			
			if( v != null && v.getId() != 0 )
				lista.addErro("Voc� j� votou nesta elei��o e n�o poder� votar novamente.");
		} finally {
			dao.close();
		}
	}
	
}
