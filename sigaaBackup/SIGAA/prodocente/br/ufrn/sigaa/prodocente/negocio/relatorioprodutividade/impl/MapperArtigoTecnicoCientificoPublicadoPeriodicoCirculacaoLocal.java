/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.impl;

import java.util.Date;

import br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade.AbstractMapperProducaoIntelectual;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * prodocente.item_relatorio_produtividade.id_item_relatorio_produtividade = 26
 * Descri��o = Artigo t�cnico-cient�fico publicado em peri�dico de circula��o local
 * @author Victor Hugo
 *
 */
public class MapperArtigoTecnicoCientificoPublicadoPeriodicoCirculacaoLocal
		extends AbstractMapperProducaoIntelectual<Artigo> {

	public MapperArtigoTecnicoCientificoPublicadoPeriodicoCirculacaoLocal() {
		super(Artigo.class);
	}
	
	@Override
	protected boolean isAtendeCriterios(Artigo a) {
		return ( a.getTipoRegiao() == null || a.getTipoRegiao().getId() == TipoRegiao.LOCAL || a.getTipoRegiao().getId() == TipoRegiao.REGIONAL ) &&
                a.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_LIVRO &&
                a.getTipoParticipacao().getId() != TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS &&
                a.getTipoPeriodico() != null &&
                a.getTipoPeriodico().getId() != TipoPeriodico.REVISTA_NAO_CIENTIFICA &&
                a.getTipoPeriodico().getId() != TipoPeriodico.JORNAL_NAO_CIENTIFICO;
	}

	@Override
	public Date getDataReferencia(Artigo a) {
		return a.getDataPublicacao();
	}
	
}
