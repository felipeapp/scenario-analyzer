package br.ufrn.sigaa.test.dao;

import java.sql.SQLException;

import junit.framework.TestCase;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dominio.SeqAno;

/** Teste unitário da classe GenericSigaaDaoTest
 * @author Édipo Elder F. Melo
 *
 */
public class GenericSigaaDaoTest extends TestCase {

	/** Dao a ser testado. */
	GenericSigaaDAO dao = new GenericSigaaDAO();
	
	/**
	 * Recupera o próximo valor da sequence
	 *
	 * @param numSeqAno
	 * @param ano
	 * @return
	 * @throws SQLException
	 */
	public void testFindGetNextSeqAno() throws DAOException {
		assertNotNull(dao.getNextSeq(SeqAno.SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO, 2007));
		System.out.println(dao.getNextSeq(SeqAno.SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO, 2007));
	}
	
	/**
	 * Recupera o próximo valor da sequence
	 *
	 * @param sequence
	 * @return
	 * @throws SQLException
	 */
	public void testFindGetNextSeq() throws DAOException {
		assertNotNull(dao.getNextSeq("atividade_seq"));
		System.out.println(dao.getNextSeq(SeqAno.SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO, 2007));
	}

}
