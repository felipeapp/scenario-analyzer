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
import br.ufrn.sigaa.prodocente.producao.dominio.PublicacaoEvento;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes aos Trabalhos publicados em Eventos - PRODUÇÕES BIBLIOGRÁFICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportTrabalhoEvento extends ImportProducao<PublicacaoEvento> {


	public ImportTrabalhoEvento(InputStream input, ImportLattesDao dao, List producoes) {
		super(input, dao, producoes, "TRABALHO-EM-EVENTOS");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if ("TRABALHO-EM-EVENTOS".equals(qName)) {
			item = new PublicacaoEvento();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoProducao(TipoProducao.PUBLICACOES_EVENTOS);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			
			item.setTipoEvento(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			parse = true;
		}
		
		if ("DADOS-BASICOS-DO-TRABALHO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO-DO-TRABALHO")));
			item.setTitulo(attributes.getValue("TITULO-DO-TRABALHO"));
			item.setLocalPublicacao(attributes.getValue("PAIS-DO-EVENTO"));
			// item.setApresentado(apresentado);
			
			String natureza = (attributes.getValue("NATUREZA"));
			if (natureza.equals("COMPLETO"))
				item.setNatureza('T');
			else
				item.setNatureza('R');		
		} 
		
		if ("DETALHAMENTO-DO-TRABALHO".equals(qName) && parse) {
			item.setTipoRegiao(getTipoRegiao(attributes.getValue("CLASSIFICACAO-DO-EVENTO")));
			item.setNomeEvento(attributes.getValue("NOME-DO-EVENTO"));
			item.setPaginaInicial(getPaginaInicial(attributes.getValue("PAGINA-INICIAL")));
			item.setPaginaFinal(getPaginaFinal(attributes.getValue("PAGINA-FINAL")));
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
				e.printStackTrace();
				item.setArea(null);
				item.setSubArea(null);
			}
		}

	}
	

	//	public void apresentado() {
	//		if (tiporegiao.equals("3") && nat.equals("R"))
	//			apresentado = "S";
	//		else
	//			apresentado = "N";
	//	}
	
}
