/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/12/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import java.sql.SQLException;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dominio.SeqAno;

/** Classe responsável por consultas genéricas às entidades persistentes.
 * Qualquer DAO a ser implementado para consultas específicas deverá extender esta classe.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public class GenericSigaaDAO extends GenericDAOImpl {

	/** Construtor padrão. */
	public GenericSigaaDAO() {
		super(Sistema.SIGAA);
	}
	
	/**
	 * Recupera o próximo valor da sequência dados o número da sequência e o ano. 
	 *
	 * @param numSeqAno
	 * @param ano
	 * @return
	 * @throws SQLException
	 */
	public int getNextSeq(SeqAno numSeqAno, int ano) throws DAOException {
		int codigo = numSeqAno.ordinal();
		return getNextSeq(null, "seq_" + codigo + "_" + ano);
	}


	/**
	 * Recupera o próximo valor da sequência informada como argumento.
	 * 
	 * @param sequence
	 * @return
	 * @throws SQLException
	 */
	public int getNextSeq(String seq) throws DAOException {
		String esquema = null;
		String sequencia = seq;
		if(seq.contains(".")){
			int i = seq.indexOf(".");
			esquema = seq.substring(0, i);
			sequencia = seq.substring(i+1);
		}
		return getNextSeq(esquema, sequencia);
	}
}
