/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 8, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 65
 * Descrição = Participação em eventos científicos, desportivos ou artístico-culturais regionais ou locais, na Comissão Organizadora  
 * item 4.18
 * @author Victor Hugo
 *
 */
public class MapperAtividadeParticipacaoEventoCientificoDesportivoArtisticoRegionalLocalComissaoOrganizadora
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {

		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			return dao.findRelatorioParticipacaoEventoRegionalMembro(docente, ano, validade);
		}finally{ dao.close(); }
	}

}
