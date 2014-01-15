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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes a Composições Musicais - OUTRAS PRODUÇÕES.
 * 
 * @author David Pereira
 *
 */
public class ImportComposicaoMusical extends ImportProducao<AudioVisual> {

	public ImportComposicaoMusical(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "COMPOSICAO-MUSICAL");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("COMPOSICAO-MUSICAL".equals(qName)) {
			item = new AudioVisual();
			item.setSubTipoArtistico(new SubTipoArtistico(SubTipoArtistico.COMPOSICAO_MUSICAL_AUDIO_VISUAL));
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoArtistico(TipoArtistico.ARTISTICO);
			item.setTipoProducao(TipoProducao.AUDIO_VISUAIS);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			
			parse = true;
		}

		if ("DADOS-BASICOS-DA-COMPOSICAO-MUSICAL".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO")));
			item.setLocal((attributes.getValue("PAIS-DE-PUBLICACAO")));
			// divulgacao = attributes.getValue("MEIO-DE-DIVULGACAO");
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
