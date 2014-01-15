/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 20/11/2012 
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Collection;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.CHDedicadaResidenciaMedicaDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Mapper utilizado para buscar atividades de orientação/supervisão de residência médica ou multiprofissional.
 * @author Leonardo
 *
 */
public class MapperAtividadeResidenciaMedicaMultiprofissional extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade)
			throws DAOException {
		
		CHDedicadaResidenciaMedicaDao dao = null;
		try {
			dao = new CHDedicadaResidenciaMedicaDao();
			return dao.findByServidorAno(docente.getId(), ano);
		} finally {
			dao.close();
		}
		
	}

}
