/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
	 * valida se a eleição esta apta para votação
	 * @param eleicao
	 * @param lista
	 */
	public static void validarEleicao(Eleicao eleicao, ListaMensagens lista){
		
		Date hoje = new Date();
//		hoje = DateUtils.truncate(hoje, Calendar.DAY_OF_MONTH);
		
		int result = hoje.compareTo( eleicao.getDataFim() );
		if( result > 0 )
			lista.addErro("O prazo para votação nesta eleição está encerrado.");
			
	}
		
	/**
	 * valida se um discente pode votar em uma eleição
	 * discente não pode votar em uma eleição mais de uma vez.
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
				lista.addErro("Você já votou nesta eleição e não poderá votar novamente.");
		} finally {
			dao.close();
		}
	}
	
}
