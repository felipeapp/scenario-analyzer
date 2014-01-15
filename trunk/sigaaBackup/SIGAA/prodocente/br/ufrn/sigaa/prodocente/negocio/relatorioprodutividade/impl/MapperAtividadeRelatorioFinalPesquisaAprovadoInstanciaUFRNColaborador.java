/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 10, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 159
 * Descrição = Relatório final de pesquisa aprovado pela instância competente da UFRN como colaborador
 * item = 8.50
 * @author Victor Hugo
 *
 */
public class MapperAtividadeRelatorioFinalPesquisaAprovadoInstanciaUFRNColaborador
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades = new ArrayList<ViewAtividadeBuilder>();
			atividades.addAll( dao.findRelatorioFinalPesquisaColaborador(docente, ano, validade) );
			atividades.addAll( dao.findProjetosAntigosRelatorioDocente(docente, ano, validade, true) );
			return atividades;
		}finally{ dao.close(); }
	}

}
