/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/01/2011
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.projetos.dominio.DiscenteProjeto;
import br.ufrn.sigaa.projetos.dominio.HistoricoSituacaoDiscenteProjeto;

/**
 * Classe auxiliar para as operações sobre a entidade Discente de Projeto 
 *  
 * @author geyson
 *
 */
public class DiscenteProjetoHelper {

	/**
	 * Grava o histórico da situação do Discente de Projetos.
	 * 
	 * @param dao
	 * @param dp
	 * @param re
	 * @throws DAOException
	 */
	public static void gravarHistoricoSituacao(GenericDAO dao, DiscenteProjeto dp, RegistroEntrada re) throws DAOException{
		HistoricoSituacaoDiscenteProjeto h = new HistoricoSituacaoDiscenteProjeto();		
		h.setData(new Date());
		h.setDiscenteProjeto(dp);
		h.setSituacaoDiscenteProjeto(dp.getSituacaoDiscenteProjeto());
		h.setRegistroEntrada(re);
		h.setTipoVinculo(dp.getTipoVinculo());
		dao.clearSession();
		dao.create(h);
	}
	
}
