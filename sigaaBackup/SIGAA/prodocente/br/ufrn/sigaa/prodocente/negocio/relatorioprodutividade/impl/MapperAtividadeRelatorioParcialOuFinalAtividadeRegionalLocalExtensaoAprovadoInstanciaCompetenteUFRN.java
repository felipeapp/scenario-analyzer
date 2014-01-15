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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 164
 * Descrição = Relatório parcial, ou final  de atividades (Regional ou local) de extensão, aprovado em instâncias competentes na UFRN
 * item 4.3
 * @author Victor Hugo
 *
 */
public class MapperAtividadeRelatorioParcialOuFinalAtividadeRegionalLocalExtensaoAprovadoInstanciaCompetenteUFRN
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades =  new ArrayList<ViewAtividadeBuilder>(); 
			
			Collection<? extends ViewAtividadeBuilder> rels = dao.findRelatorioAtividadeRegionalLocal1( docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> projetos = dao.findRelatorioAtividadeRegionalLocal2(docente, ano, validade);
			
			atividades.addAll( rels );
			atividades.addAll( projetos );
			
			return atividades;
		}finally{ dao.close(); }
	}

	/**
	 * 2p/mês-coord.
	 * 1/mês-colab.
	 */
	@Override
	public float calculaPontuacao(ViewAtividadeBuilder atividade){
		int meses = 1;
		float pontuacao = 1;
		
		if (atividade instanceof MembroProjeto ) {
				MembroProjeto membro = (MembroProjeto) atividade;
				meses = CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(membro.getDataInicio(), membro.getDataFim());
				if(membro.isCoordenador())
					pontuacao = 2;
				else
					pontuacao = 1;
		}
		
		return pontuacao * meses;
	}
		
}