/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 9, 2011
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 84
 * Descrição = Coordenação de curso de pós-graduação lato sensu (devidamente comprovado que não recebe remuneração para esta função)
 * item = 6.10
 * @author Victor Hugo
 *
 */
public class MapperAtividadeCoordCursoPosLatoSemRemuneracao extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades = new ArrayList<ViewAtividadeBuilder>();
			
			atividades.addAll( dao.findRelatorioChefiaCoordenacaoCursoLato(docente, ano, validade) );
			atividades.addAll( dao.findRelatorioCoordenacaoCursoLato(docente, ano, validade) );
			return atividades;
		}finally{ dao.close(); }
	}

}
