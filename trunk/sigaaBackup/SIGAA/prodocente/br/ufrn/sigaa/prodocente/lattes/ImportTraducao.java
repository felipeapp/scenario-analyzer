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
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes a Tradução de Artigos e Capítulos -
 * PRODUÇÕES BIBLIOGRÁFICAS.
 * 
 * @author David Pereira
 * 
 */
public class ImportTraducao extends ImportProducao<Livro> {

	public ImportTraducao(InputStream input, ImportLattesDao dao, List producoes) {
		super(input, dao, producoes, "TRADUCAO");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("TRADUCAO".equals(qName)) {
			item = new Livro();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoProducao(TipoProducao.LIVRO);
			item.setTipoParticipacao(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			parse = true;
		}
		
		if ("DADOS-BASICOS-DA-TRADUCAO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.parseInt(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO")));
			
			String destaque = attributes.getValue("FLAG-RELEVANCIA"); 
			if ("SIM".equals(destaque))
				item.setDestaque(true);
			else
				item.setDestaque(false);

			String natureza = (attributes.getValue("NATUREZA"));
			if ("LIVRO".equals(natureza))
				item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.TRADUTOR_LIVRO));
			else
				item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.TRADUTOR_ARTIGO_PERIODICOS_JORNAIS));
			
			item.setLocalPublicacao((attributes.getValue("PAIS-DE-PUBLICACAO")));
		} 
		
		if ("DETALHAMENTO-DA-TRADUCAO".equals(qName) && parse) {
			item.setEditora(attributes.getValue("EDITORA-DA-TRADUCAO"));
		}
		
		if ("AUTORES".equals(qName) && parse) {
			String autor = (attributes.getValue("NOME-COMPLETO-DO-AUTOR"));
			if (item.getAutores() == null)
				item.setAutores(autor);
			else
				item.setAutores(item.getAutores() + ", " + autor);
		}

		if ("INFORMACOES-ADICIONAIS".equals(qName) && parse) {
			String informacao = (attributes.getValue("DESCRICAO-INFORMACOES-ADICIONAIS"));
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
