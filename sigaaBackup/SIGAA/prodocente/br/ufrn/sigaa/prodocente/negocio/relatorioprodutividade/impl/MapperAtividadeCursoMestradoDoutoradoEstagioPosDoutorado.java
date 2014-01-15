/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: Feb 8, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoAcademicaAdapter;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoTese;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 71
 * Descrição = Curso de Mestrado, Doutorado ou estágio Pós-Doutorado 
 * item 5.1
 * @author Victor Hugo
 *
 */
public class MapperAtividadeCursoMestradoDoutoradoEstagioPosDoutorado extends AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) {
		
		Collection<FormacaoAcademicaDTO> formacoesDTO = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null, 							
				null, CalendarUtils.createDate(01, 01, (ano - validade)), CalendarUtils.createDate(31, 12, ano),
				Formacao.MESTRE, Formacao.DOUTOR, FormacaoTese.POSDOC);
		
		Collection<FormacaoAcademicaAdapter> formacoes = new ArrayList<FormacaoAcademicaAdapter>();
		
		if (ValidatorUtil.isNotEmpty(formacoesDTO)) {
			for( FormacaoAcademicaDTO dto : formacoesDTO ){
				formacoes.add( new FormacaoAcademicaAdapter(dto) );
			}
		}
		
		return formacoes;
	}

}
