/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 2, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 6
 * Descri��o = Orienta��o - Est�gio Supervisionado e correlatos
 * @author Victor Hugo
 *
 */
public class MapperAtividadeOrientacaoEstagioSupervisionadoCorrelatos extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades( Servidor docente, Integer ano, Integer validade) throws DAOException {
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<? extends ViewAtividadeBuilder> atividades = dao.findOrientacoesGraduacaoEstagioDocente(docente, ano, validade);
			return atividades;
		}finally{
			dao.close();
		}
	}
	
}