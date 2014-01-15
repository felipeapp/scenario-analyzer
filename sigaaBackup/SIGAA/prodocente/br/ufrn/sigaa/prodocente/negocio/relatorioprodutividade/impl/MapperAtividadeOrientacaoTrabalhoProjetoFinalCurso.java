/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 2, 2011
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 7
 * Descrição = Orientação - Trabalho ou Projeto de Final de Curso
 * @author Victor Hugo
 *
 */
public class MapperAtividadeOrientacaoTrabalhoProjetoFinalCurso extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades( Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades = new ArrayList<ViewAtividadeBuilder>();
			Collection<? extends ViewAtividadeBuilder> trabalhos = dao.findOrientacoesGraduacaoTrabalhoFimCursoConcluidas(docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> monografias =dao.findOrientacoesMonografiaGraduacaoConcluidas(docente, ano, validade) ;
			
			atividades.addAll(trabalhos);
			atividades.addAll(monografias);
			
			return atividades;
		}finally{
			dao.close();
		}
	}
	
}