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

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Artigo;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes aos Artigos Publicados - PRODUCOES BIBLIOGRIFICAS..
 * 
 * @author David Pereira
 *
 */
public class ImportArtigoPublicado extends ImportProducao<Artigo> {


	/** Construtor parametrizado.
	 * @param input
	 * @param dao
	 * @param producoes
	 */
	public ImportArtigoPublicado(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "ARTIGO-PUBLICADO");
	}


	/** Trata elementos de produção do tipo ARTIGO-PUBLICADO.
	 * @see br.ufrn.sigaa.prodocente.lattes.ImportProducao#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if ("ARTIGO-PUBLICADO".equals(qName)) {
			item = new Artigo();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setTipoPeriodico(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DO-ARTIGO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO-DO-ARTIGO")));
			// NATUREZA??   natureza = attributes.getValue("NATUREZA");
		
			item.setTitulo(attributes.getValue("TITULO-DO-ARTIGO"));
			item.setLocalPublicacao(attributes.getValue("PAIS-DE-PUBLICACAO"));
			item.setDestaque(getDestaque(attributes.getValue("FLAG-RELEVANCIA")));
		} 
		
		if ("DETALHAMENTO-DO-ARTIGO".equals(qName) && parse) {
			item.setTituloPeriodico(attributes.getValue("TITULO-DO-PERIODICO-OU-REVISTA"));
			item.setIssn(attributes.getValue("ISSN"));
			item.setPaginaInicial(getPaginaInicialString(attributes.getValue("PAGINA-INICIAL")));
			item.setPaginaFinal(getPaginaFinalString(attributes.getValue("PAGINA-FINAL")));
			String volume = attributes.getValue("VOLUME");
			
			try {
				if (volume != null && !"".equals(volume.trim()) && StringUtils.extractInteger(volume) != null)
					item.setVolume(Integer.valueOf(StringUtils.extractInteger(volume)));
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
			
			item.setLocalPublicacao(attributes.getValue("LOCAL-DE-PUBLICACAO"));
		}

		if ("AUTORES".equals(qName) && parse) {
			String autor = attributes.getValue("NOME-COMPLETO-DO-AUTOR");
			if (item.getAutores() == null)
				item.setAutores(autor);
			else
				item.setAutores(item.getAutores() + ", " + autor);
		}

		if ("INFORMACOES-ADICIONAIS".equals(qName) && parse) {
			String informacao = attributes.getValue("DESCRICAO-INFORMACOES-ADICIONAIS");
			item.setInformacao(informacao);
		}

		if ("AREA-DO-CONHECIMENTO-1".equals(qName) && parse) {
			String area = attributes.getValue("NOME-GRANDE-AREA-DO-CONHECIMENTO");
			String subArea = attributes.getValue("NOME-DA-AREA-DO-CONHECIMENTO");
			
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
