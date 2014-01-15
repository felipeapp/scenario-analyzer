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
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 162
 * Descri��o = Relat�rio parcial, ou final  de atividades (Internacional) de extens�o, aprovado em inst�ncias competentes na UFRN
 * item 4.1
 * @author Victor Hugo
 *
 */
public class MapperAtividadeRelatorioParcialAtividadeInternacionalExtensaoAprovadoInstanciaCompetenteUFRN
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<ViewAtividadeBuilder> atividades =  new ArrayList<ViewAtividadeBuilder>(); 
			
			Collection<? extends ViewAtividadeBuilder> rels = dao.findRelatorioAtividadeInternacional1( docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> projetos = dao.findRelatorioAtividadeInternacional2( docente, ano, validade);
			
			atividades.addAll( rels );
			atividades.addAll( projetos );
			
			return atividades;
		}finally{ dao.close(); }
	}

	/**
	 * 3p/m�s-coord.
	 * 2/m�s-colab.
	 */
	@Override
	public float calculaPontuacao(ViewAtividadeBuilder atividade){
		int meses = 1;
		float pontuacao = 2;
		
		if (atividade instanceof MembroProjeto ) {
				MembroProjeto membro = (MembroProjeto) atividade;
				meses = CalendarUtils.calculaQuantidadeMesesEntreDatasIntervaloFechado(membro.getDataInicio(), membro.getDataFim());
				if(membro.isCoordenador())
					pontuacao = 3;
				else
					pontuacao = 2;
		}
		
		return pontuacao * meses;
	}

}
