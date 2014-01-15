/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 82
 * Descrição = Chefia ou coordenação de setores acadêmicos de apoio: laboratórios, núcleos de estudos, bibliotecas, oficinas ou órgão similar, descrito pela PRH como tipo 3 e designado por portaria
 * item = 6.8
 * @author Victor Hugo
 *
 */
public class MapperAtividadeChefiaCoordAcademicaTipo3 extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			return dao.findRelatorioChefiaCoordenacaoSetoresAcademicoApoio(docente, ano, validade);
		}finally{ dao.close(); }
	}

}
