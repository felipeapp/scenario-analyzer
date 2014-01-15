/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Classe auxiliar para as operações sobre a entidade Discente de Extensão e derivadas
 *  
 * @author Ilueny Santos
 *
 */
public class DiscenteExtensaoHelper {

	/**
	 * Grava o histórico da situação do Discente de Extensão.
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
