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

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.FormacaoAcademicaDTO;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoAcademicaAdapter;
import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperAtividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 15
 * Descrição = Monografia de especialização defendida com aprovação
 * item 3.3
 * @author Victor Hugo
 *
 */
public class MapperAtividadeMonografiaEspecializacaoDefendidaAprovacao extends
		AbstractMapperAtividade {

	@Override
	public Collection<? extends ViewAtividadeBuilder> buscarAtividades(
			Servidor docente, Integer ano, Integer validade) {
		
		Collection<FormacaoAcademicaDTO> formacoesDTO = serviceFormacao.consultarFormacaoAcademica(docente.getId(), null, null, null,  null,	
				CalendarUtils.createDate(01, 01, (ano - validade)), CalendarUtils.createDate(31, 12, ano), Formacao.ESPECIALISTA) ;
		
		Collection<FormacaoAcademicaAdapter> formacoes = new ArrayList<FormacaoAcademicaAdapter>();

		if (ValidatorUtil.isNotEmpty(formacoesDTO)) {

			for( FormacaoAcademicaDTO dto : formacoesDTO ){
				formacoes.add( new FormacaoAcademicaAdapter(dto) );
			}
			
		}
		
		return formacoes;
		
	}

}
