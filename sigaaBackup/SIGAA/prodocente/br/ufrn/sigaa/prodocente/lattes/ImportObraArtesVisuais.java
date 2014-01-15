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
import br.ufrn.sigaa.prodocente.producao.dominio.AudioVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes a Obra de Artes Visuais - OUTRAS PRODUÇÕES.
 * 
 * @author David Pereira
 *
 */
public class ImportObraArtesVisuais extends ImportProducao<AudioVisual> {

	public ImportObraArtesVisuais(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "OBRA-DE-ARTES-VISUAIS");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("OBRA-DE-ARTES-VISUAIS".equals(qName)) {
			item = new AudioVisual();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoArtistico(TipoArtistico.ARTISTICO);
			item.setTipoProducao(TipoProducao.AUDIO_VISUAIS);
			item.setTipoParticipacao(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DA-OBRA-DE-ARTES-VISUAIS".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setSubTipoArtistico(getSubTipoArtistico(attributes.getValue("NATUREZA")));
			item.setLocal((attributes.getValue("PAIS")));
			item.setTitulo((attributes.getValue("TITULO")));
			// veiculo = attributes.getValue("MEIO-DE-DIVULGACAO");
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
	
	public SubTipoArtistico getSubTipoArtistico(String natureza) {
		if (natureza.equals("CINEMA"))
			return new SubTipoArtistico(SubTipoArtistico.CINEMA_AUDIO_VISUAL);
		else if (natureza.equals("DESENHO"))
			return new SubTipoArtistico(SubTipoArtistico.DESENHO_AUDIO_VISUAL);
		else if (natureza.equals("ESCULTURA"))
			return new SubTipoArtistico(SubTipoArtistico.ESCULTURA_AUDIO_VISUAL);
		else if (natureza.equals("FOTOGRAFIA"))
			return new SubTipoArtistico(SubTipoArtistico.FOTOGRAFIAS_AUDIO_VISUAL);
		else if (natureza.equals("GRAVURA"))
			return new SubTipoArtistico(SubTipoArtistico.GRAVURAS_AUDIO_VISUAL);
		else if (natureza.equals("INSTALACAO"))
			return new SubTipoArtistico(SubTipoArtistico.INSTALACAO_AUDIO_VISUAL);
		else if (natureza.equals("PINTURA"))
			return new SubTipoArtistico(SubTipoArtistico.PINTURA_AUDIO_VISUAL);
		else if (natureza.equals("TELEVISAO"))
			return new SubTipoArtistico(SubTipoArtistico.TELEVISAO_AUDIO_VISUAL);
		else if (natureza.equals("VIDEO"))
			return new SubTipoArtistico(SubTipoArtistico.VIDEO_AUDIO_VISUAL);
		else 
			return new SubTipoArtistico(SubTipoArtistico.OUTRA_CLASSIFICACAO_AUDIO_VISUAL);
	}
}
