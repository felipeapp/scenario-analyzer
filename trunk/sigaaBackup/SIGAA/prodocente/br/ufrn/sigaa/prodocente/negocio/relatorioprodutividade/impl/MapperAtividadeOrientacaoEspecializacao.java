/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 7, 2011
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 8
 * Descrição = Orientação - Especialização
 * @author Victor Hugo
 */
public class MapperAtividadeOrientacaoEspecializacao extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades = new ArrayList<ViewAtividadeBuilder>();
			Collection<? extends ViewAtividadeBuilder> trabalhosFimCurso = dao.findOrientacoesEspecializacaoTrabalhoFimCursoConcluidas(docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> teseOrientada = dao.findOrientacoesPosEspecializacaoDocente(docente, ano, validade);
		
			atividades.addAll(trabalhosFimCurso);
			atividades.addAll(teseOrientada);

			return atividades;
		}finally{
			dao.close();
		}
	}

}
