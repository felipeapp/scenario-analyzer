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
import br.ufrn.sigaa.arq.dao.prodocente.AvaliacaoDocenteDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 53
 * Descrição = Atividade de atendimento de pacientes em Hospitais ou Ambulatórios Universitários, preferencialmente com a presença de alunos. Esta atividade deverá ser devidamente cadastrada, na PROEX como prestação 
 * item 4.6
 * @author Victor Hugo
 *
 */
public class MapperAtividadeAtendimentoPacienteHospitalAmbulatorio extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) throws DAOException {
		
		AvaliacaoDocenteDao dao = null;
		try{
			dao = new AvaliacaoDocenteDao();
			Collection<? extends ViewAtividadeBuilder> rels = dao.findRelatorioAtividadeAtendimento1( docente, ano, validade);
			Collection<? extends ViewAtividadeBuilder> projetos = dao.findRelatorioAtividadeAtendimento2(docente, ano, validade);
			Collection<ViewAtividadeBuilder> atividades = new ArrayList<ViewAtividadeBuilder>();
			atividades.addAll(rels);
			atividades.addAll(projetos);
			return atividades;
		}finally{ dao.close(); }
	}

}
