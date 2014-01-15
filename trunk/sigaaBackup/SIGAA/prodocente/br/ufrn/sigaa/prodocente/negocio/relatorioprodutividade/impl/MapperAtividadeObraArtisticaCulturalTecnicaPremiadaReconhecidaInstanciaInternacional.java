/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 56
 * Descri��o = Obra art�stica, cultural ou t�cnica-cient�fica, premiada ou reconhecida em inst�ncia competente em n�vel internacional, na �rea de atividade acad�mica do docente 
 * item 4.9
 * @author Victor Hugo
 *
 */
public class MapperAtividadeObraArtisticaCulturalTecnicaPremiadaReconhecidaInstanciaInternacional
		extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			return dao.findRelatorioObraArtisticaInternacional(docente, ano, validade);
		}finally{ dao.close(); }
	}

}
