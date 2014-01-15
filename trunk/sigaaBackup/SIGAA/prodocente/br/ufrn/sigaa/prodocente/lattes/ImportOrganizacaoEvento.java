/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.lattes;

import java.io.InputStream;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.prodocente.producao.dominio.ParticipacaoComissaoOrgEventos;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Classe que extrai dados referentes a Organização de Eventos - PRODUÇÕES TÉCNICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportOrganizacaoEvento extends ImportProducao<ParticipacaoComissaoOrgEventos> {
	
	public ImportOrganizacaoEvento(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "ORGANIZACAO-DE-EVENTO");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if ("ORGANIZACAO-DE-EVENTO".equals(qName)) {
			item = new ParticipacaoComissaoOrgEventos();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.PARTICIPACAO_COMISSAO_ORGANIZACAO_EVENTO);
			item.setTipoParticipacao(null);
			item.setTipoParticipacaoOrganizacao(null);
			item.setArea(null);
			item.setSubArea(null);
			
			parse = true;
		}

		if ("DADOS-BASICOS-DA-ORGANIZACAO-DE-EVENTO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO")));
			String pais = (attributes.getValue("PAIS"));

			if (pais.equalsIgnoreCase("Brasil"))
				item.setAmbito(new TipoRegiao(TipoRegiao.NACIONAL));
			else
				item.setAmbito(new TipoRegiao(TipoRegiao.INTERNACIONAL));
		} 

		if ("DETALHAMENTO-DA-ORGANIZACAO-DE-EVENTO".equals(qName) && parse) {
			item.setLocal((attributes.getValue("LOCAL")));
		}
		
		//	if ("AUTORES".equals(qName)) {
		//		item.set
		//		String autor = attributes.getValue("NOME-COMPLETO-DO-AUTOR");
		//		item.setAutores(item.getAutores() + " " + autor);
		//	}

		if ("INFORMACOES-ADICIONAIS".equals(qName) && parse) {
			String informacao = (attributes.getValue("DESCRICAO-INFORMACOES-ADICIONAIS"));
			if (informacao.equals(""))
				informacao = "  ";
			item.setInformacao(informacao);
		} 

		if ("AREA-DO-CONHECIMENTO-1".equals(qName) && parse) {
			String area = (attributes.getValue("NOME-GRANDE-AREA-DO-CONHECIMENTO"));
			String subArea = (attributes.getValue("NOME-DA-AREA-DO-CONHECIMENTO"));
			
			try {
				item.setArea(getGrandeAreaConhecimentoCnpq(area));
				item.setSubArea(getSubAreaConhecimentoCnpq(subArea));
			} catch(Exception e) {
				item.setArea(null);
				item.setSubArea(null);
			}
		}
		
	}
}
