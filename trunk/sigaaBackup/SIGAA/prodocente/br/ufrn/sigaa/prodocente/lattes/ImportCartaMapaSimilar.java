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
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoTecnologica;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;

/**
 * Classe que extrai dados referentes a Carta, Mapa ou Similar - PRODUÇÕES TÉCNICAS.
 *  
 * @author David Pereira
 *
 */
public class ImportCartaMapaSimilar extends ImportProducao<ProducaoTecnologica> {

	public ImportCartaMapaSimilar(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "CARTA-MAPA-OU-SIMILAR");
	}

//	public void tipoproducao() {		
//		if (natureza.equals("Concurso público"))
//			tipoproducao = "1";
//		else if (natureza.equals("MAPA") || natureza.equals("CARTA"))
//			tipoproducao = "4";
//		else if (natureza.equals("OUTRA"))
//			tipoproducao = "8";
//		else if (natureza.equals("FOTOGRAMA"))
//			tipoproducao = "9";
//	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("CARTA-MAPA-OU-SIMILAR".equals(qName)) {
			item = new ProducaoTecnologica();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.OUTROS);
			item.setTipoProducaoTecnologica(new TipoProducaoTecnologica(TipoProducaoTecnologica.CARTA_MAPA));
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DE-CARTA-MAPA-OU-SIMILAR".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			//item.setTipoProducaoTecnologica(tipoProducaoTecnologica)
			//String natureza = attributes.getValue("NATUREZA");
			
			item.setTitulo((attributes.getValue("TITULO")));
			item.setLocalPublicacao((attributes.getValue("PAIS")));
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
	}
}
