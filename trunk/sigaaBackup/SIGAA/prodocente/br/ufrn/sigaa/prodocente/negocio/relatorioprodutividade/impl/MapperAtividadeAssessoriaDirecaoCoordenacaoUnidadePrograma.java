/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 1
 * Descrição = Assessoria, direção ou coordenação de Unidade ou Programa tipo 1
 * @author Victor Hugo
 *
 */
public class MapperAtividadeAssessoriaDirecaoCoordenacaoUnidadePrograma extends AbstractMapperAtividade{

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(Servidor docente, Integer ano, Integer validade) throws DAOException {
		AvaliacaoDocenteDao dao = null;
		
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<? extends ViewAtividadeBuilder> atividades = dao.findCargosDirecaoFuncaoGratificada(docente, ano, validade);
			return atividades;
		}finally{
			dao.close();
		}
	}
	
	/*@Override
	public void process(int idItem, Servidor docente, int ano, Hashtable<Integer, Integer> validades, Hashtable<Integer, Collection<ViewAtividadeBuilder> > mapaAtividades) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		
		try{
			dao = new AvaliacaoDocenteDao();
			
			Integer validade = validades.get(idItem);
			Collection atividades = dao.findCargosDirecaoFuncaoGratificada(docente, ano, validade);
			
			RelatorioHelper.adicionarAtividade(idItem, mapaAtividades, atividades);
		}finally{
			dao.close();
		}

	}*/

}