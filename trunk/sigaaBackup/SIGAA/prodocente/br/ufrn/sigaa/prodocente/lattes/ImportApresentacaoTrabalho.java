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
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Classe que extrai dados referentes aos Trabalhos Apresentados - PRODUÇÕES TÉCNICAS.
 *
 * @author David Pereira
 *
 */
public class ImportApresentacaoTrabalho extends ImportProducao<PublicacaoEvento> {

	public ImportApresentacaoTrabalho(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "APRESENTACAO-DE-TRABALHO");
	}

	@Override
	public TipoRegiao getTipoRegiao(String pais) {
		if (pais.equalsIgnoreCase("Brasil"))
			return new TipoRegiao(TipoRegiao.NACIONAL);
		else
			return new TipoRegiao(TipoRegiao.INTERNACIONAL);
	}

	public TipoEvento getTipoEvento(String natureza) {
		if (natureza.equals("COMUNICACAO"))
			return new TipoEvento(TipoEvento.CONFERENCIA);
		else if (natureza.equals("CONFERENCIA"))
			return new TipoEvento(TipoEvento.CONFERENCIA);
		else if (natureza.equals("CONGRESSO"))
			return new TipoEvento(TipoEvento.CONGRESSO);
		else if (natureza.equals("SEMINARIO"))
			return new TipoEvento(TipoEvento.SEMINARIO);
		else if (natureza.equals("SIMPOSIO"))
			return new TipoEvento(TipoEvento.WORKSHOP);
		else
			return new TipoEvento(TipoEvento.CONFERENCIA);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("APRESENTACAO-DE-TRABALHO".equals(qName)) {
			item = new PublicacaoEvento();
			item.setApresentado(true);
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));

			item.setTipoProducao(TipoProducao.PUBLICACOES_EVENTOS);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.PALESTRANTE_MINISTRANTE_PUBLICACAO_EVENTO));
			item.setTipoEvento(null);
			item.setArea(null);
			item.setSubArea(null);

			parse = true;
		}

		if ("DADOS-BASICOS-DA-APRESENTACAO-DE-TRABALHO".equals(qName) && parse) {
			item.setAnoReferencia(parseInt(attributes.getValue("ANO")));

			String natureza = attributes.getValue("NATUREZA");
			item.setTipoEvento(getTipoEvento(natureza));

			item.setTitulo((attributes.getValue("TITULO")));

			String pais = attributes.getValue("PAIS");
			item.setTipoRegiao(getTipoRegiao(pais));
		}

		if ("DETALHAMENTO-DA-APRESENTACAO-DE-TRABALHO".equals(qName) && parse) {
			item.setNomeEvento((attributes.getValue("NOME-DO-EVENTO")));
			item.setLocalPublicacao((attributes.getValue("LOCAL-DA-APRESENTACAO")));
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
