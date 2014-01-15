/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 7, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 52
 * Descri��o = Atividades de assessoria, mini-curso em congresso, consultoria, per�cia ou sindic�ncia, (manuten��o de obra art�stica) devidamente comprovadas por inst�ncia respons�vel pela contrata��o do servi�o, ap 
 * item 4.5
 * @author Victor Hugo
 *
 */
public class MapperAtividadeAssessoriaMiniCursoCongressoConsultoriaPericiaSindicanciaComprovada
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			return dao.findRelatorioAtividadeAssessoria1(docente, ano, validade);
		}finally{ dao.close(); }
	}

}
