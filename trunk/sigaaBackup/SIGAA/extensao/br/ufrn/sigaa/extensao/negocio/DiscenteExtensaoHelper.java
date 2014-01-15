/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/11/2009
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.HistoricoSituacaoDiscenteExtensao;

/**
 * Classe auxiliar para as opera��es sobre a entidade Discente de Extens�o e derivadas
 *  
 * @author Ilueny Santos
 *
 */
public class DiscenteExtensaoHelper {

	/**
	 * Grava o hist�rico da situa��o do Discente de Extens�o.
	 * 
	 * @param dao
	 * @param de
	 * @param re
	 * @throws DAOException
	 */
	public static void gravarHistoricoSituacao(GenericDAO dao, DiscenteExtensao de, RegistroEntrada re) throws DAOException{
		HistoricoSituacaoDiscenteExtensao h = new HistoricoSituacaoDiscenteExtensao();		
		h.setData(new Date());
		h.setDiscenteExtensao(de);
		h.setSituacaoDiscenteExtensao(de.getSituacaoDiscenteExtensao());
		h.setRegistroEntrada(re);
		h.setTipoVinculo(de.getTipoVinculo());
		dao.clearSession();
		dao.create(h);
	}

}
