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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes a artigos publicados em Jornal ou Revista
 *  
 * @author David Pereira
 *
 */
public class ImportJornalRevista extends ImportProducao<Artigo> {

	/** Construtor parametrizado.
	 * @param input
	 * @param dao
	 * @param producoes
	 */
	public ImportJornalRevista(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "TEXTO-EM-JORNAL-OU-REVISTA");
	}

	/** Trata elementos de produção do tipo TEXTO-EM-JORNAL-OU-REVISTA.
	 * @see br.ufrn.sigaa.prodocente.lattes.ImportProducao#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("TEXTO-EM-JORNAL-OU-REVISTA".equals(qName)) {
			item = new Artigo();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.ARTIGO_PERIODICO_JORNAIS_SIMILARES);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			item.setTipoPeriodico(null);
			
			parse = true;
		}

		if ("DADOS-BASICOS-DO-TEXTO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO-DO-TEXTO")));
			
			String tipoPeriodico = attributes.getValue("NATUREZA");
			if ("JORNAL_DE_NOTICIAS".equals(tipoPeriodico))
				item.setTipoPeriodico(new TipoPeriodico(TipoPeriodico.JORNAL_NAO_CIENTIFICO));
			else
				item.setTipoPeriodico(new TipoPeriodico(TipoPeriodico.REVISTA_NAO_CIENTIFICA));

			item.setTitulo((attributes.getValue("TITULO-DO-TEXTO")));
			item.setDestaque(getDestaque((attributes.getValue("FLAG-RELEVANCIA"))));
		} 

		if ("DETALHAMENTO-DO-TEXTO".equals(qName) && parse) {
			item.setTitulo((attributes.getValue("TITULO-DO-JORNAL-OU-REVISTA")));
			item.setIssn(attributes.getValue("ISSN"));
			item.setPaginaInicial(getPaginaInicialString(attributes.getValue("PAGINA-INICIAL")));
			item.setPaginaFinal(getPaginaFinalString(attributes.getValue("PAGINA-FINAL")));
			
			String volume = attributes.getValue("VOLUME");
			if ("".equals(volume))
				volume = "0";
			
			item.setVolume(StringUtils.extractInteger(volume));
			item.setLocalPublicacao((attributes.getValue("LOCAL-DE-PUBLICACAO")));
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
