/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 51
 * Descri��o = Atividade em cursos de extens�o, devidamente comprovadas por inst�ncia respons�vel pela emiss�o dos certificados, aprovados em inst�ncias competentes na UFRN e Cadastrados na PROEX 
 * item 4.4
 * @author Victor Hugo
 *
 */
public class MapperAtividadeCursoExtensaoComprovadaInstanciaAprovadoUFRNCadastradoPROEX
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades =  new ArrayList<ViewAtividadeBuilder>(); 
			
			Collection<? extends ViewAtividadeBuilder> rels = dao.findRelatorioAtividadeCurso1( docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> projetos = dao.findRelatorioAtividadeCurso2(docente, ano, validade);
			
			atividades.addAll( rels );
			atividades.addAll( projetos );
			
			return atividades;
		}finally{ dao.close(); }
			
	}

}
