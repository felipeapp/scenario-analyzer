/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/07/2010
 */
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

/**
 * DAO com consultas associadas � classe
 * de dom�nio ConceitoNota.
 * 
 * @author David Pereira
 *
 */
public class ConceitoNotaDao extends GenericSigaaDAO {

	/**
	 * Retorna o valor de um conceito de acordo com sua descri��o. 
	 * @param descricao
	 * @return
	 */
	public Double findValorByDescricao(String descricao) {
		try {
			return getJdbcTemplate().queryForDouble("select valor from stricto_sensu.conceito_nota where conceito = ?", new Object[] { descricao });
		} catch(EmptyResultDataAccessException e) {
			throw new ConfiguracaoAmbienteException("O valor para o conceito '" + descricao + "' n�o foi devidamente configurado.");
		}
	}
	
	/**
	 * Retorna a descri��o de um conceito de acordo com seu valor.
	 * @param valor
	 * @return
	 */
	public String findDescricaoByValor ( Double valor ) throws DAOException
	{
		try {
			Object result = getSession().createQuery("select c.conceito from ConceitoNota c where c.valor = " + valor).uniqueResult();
			
			return result != null ? result.toString() : null;
		}catch(EmptyResultDataAccessException e) {
			throw new ConfiguracaoAmbienteException("O conceito para o valor '" + valor + "' n�o foi devidamente configurado.");
		}
	}
}
