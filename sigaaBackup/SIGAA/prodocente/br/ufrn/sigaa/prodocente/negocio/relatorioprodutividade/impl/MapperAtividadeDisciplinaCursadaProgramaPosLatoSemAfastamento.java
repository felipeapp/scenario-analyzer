/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 9, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 107
 * Descri��o = Disciplina cursada (com aprova��o) como parte de programa de p�s-gradua��o Lato Sensu (especializa��o) sem afastamento (disciplina obrigat�ria ou complementar de cr�dito)
 * item = 7.15
 * @author Victor Hugo
 *
 */
public class MapperAtividadeDisciplinaCursadaProgramaPosLatoSemAfastamento
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			return dao.findRelatorioDisciplinaCursadaLato(docente, ano, validade);
		}finally{ dao.close(); }
	}

}
