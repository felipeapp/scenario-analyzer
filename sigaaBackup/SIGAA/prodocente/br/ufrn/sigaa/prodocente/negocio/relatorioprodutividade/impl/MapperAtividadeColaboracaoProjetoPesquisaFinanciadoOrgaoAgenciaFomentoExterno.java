/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 10, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.ProjetoPesquisaDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 167
 * Descrição = Colaboração em projeto de pesquisa financiado por órgão ou agência de fomento externo
 * item = 8.54
 * @author Victor Hugo
 *
 */
public class MapperAtividadeColaboracaoProjetoPesquisaFinanciadoOrgaoAgenciaFomentoExterno
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		ProjetoPesquisaDao dao = null;
		try{
			dao = new ProjetoPesquisaDao();
			return dao.findExternosByMembro(docente, false, ano);
		}finally{ dao.close(); }
	}

}
