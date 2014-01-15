/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 9, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 83
 * Descrição = Coordenação de Base de Pesquisa
 * item = 6.9
 * @author Victor Hugo
 *
 */
public class MapperAtividadeCoordBasePesquisa extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		
		GrupoPesquisaDao dao = null;
		try{
			dao = new GrupoPesquisaDao();
			return dao.findByCoordenador(docente, null,ano-validade,ano);
		}finally{ dao.close(); }
	}

}
