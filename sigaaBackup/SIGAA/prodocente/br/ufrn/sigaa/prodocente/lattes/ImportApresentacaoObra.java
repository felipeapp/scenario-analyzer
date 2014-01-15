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
import br.ufrn.sigaa.prodocente.producao.dominio.ProducaoArtisticaLiterariaVisual;
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Classe que extrai dados referentes a Apresentação de Obra Artística - OUTRAS PRODUÇÕES.
 * 
 * @author David Pereira
 *
 */
public class ImportApresentacaoObra extends ImportProducao<ProducaoArtisticaLiterariaVisual> {

	public ImportApresentacaoObra(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "APRESENTACAO-DE-OBRA-ARTISTICA");
	}

	public SubTipoArtistico getSubTipo(String natureza) {
		if (natureza.equals("COREOGRAFICA"))
			return new SubTipoArtistico(SubTipoArtistico.COREOGRAFIA_MONTAGENS);
		else if (natureza.equals("LITERARIA"))
			return new SubTipoArtistico(SubTipoArtistico.LITERARIO_EXPOSICAO_APRESENTACAO_EVENTOS);
		else if (natureza.equals("MUSICAL"))
			return new SubTipoArtistico(SubTipoArtistico.MUSICAL_EXPOSICAO_APRESENTACAO_EVENTOS);
		else if (natureza.equals("TEATRAL"))
			return new SubTipoArtistico(SubTipoArtistico.TEATRAL_EXPOSICAO_APRESENTACAO_EVENTOS);
		else
			return new SubTipoArtistico(SubTipoArtistico.OUTRA_CLASSIFICACAO_AUDIO_VISUAL);		
	}

	public TipoRegiao getTipoRegiao(String pais) {
		if (pais.equalsIgnoreCase("Brasil"))
			return new TipoRegiao(TipoRegiao.NACIONAL);
		else
			return new TipoRegiao(TipoRegiao.INTERNACIONAL);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("APRESENTACAO-DE-OBRA-ARTISTICA".equals(qName)) {
			item = new ProducaoArtisticaLiterariaVisual();
			item.setTipoProducao(TipoProducao.EXPOSICAO_APRESENTACAO_ARTISTICAS);
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoArtistico(TipoArtistico.APRESENTACAO_EVENTO);
			item.setTipoProducao(TipoProducao.APRESENTACAO_EVENTOS);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DA-APRESENTACAO-DE-OBRA-ARTISTICA".equals(qName) && parse) {
			item.setAnoReferencia(parseInt(attributes.getValue("ANO")));
			item.setTitulo(attributes.getValue("TITULO"));
			item.setLocal(attributes.getValue("LOCAL-DO-EVENTO"));
			String natureza = attributes.getValue("NATUREZA");
			item.setSubTipoArtistico(getSubTipo(natureza));
			String pais = attributes.getValue("PAIS");
			item.setTipoRegiao(getTipoRegiao(pais));
		} 

		if ("DETALHAMENTO-DA-APRESENTACAO-DE-OBRA-ARTISTICA".equals(qName) && parse) {
			String premiacao = attributes.getValue("PREMIACAO");
			
			if (premiacao.equals(""))
				item.setPremiada(false);
			else
				item.setPremiada(true);
			
			item.setLocal(attributes.getValue("CIDADE"));
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
			if (informacao.equals(""))
				informacao = "  ";
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
