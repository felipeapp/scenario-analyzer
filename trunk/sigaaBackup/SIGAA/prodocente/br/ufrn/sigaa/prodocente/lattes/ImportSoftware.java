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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;

/**
 * Classe que extrai dados referentes aos Softwares - PRODUÇÕES TÉCNICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportSoftware extends ImportProducao<ProducaoTecnologica> {

	public ImportSoftware(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "SOFTWARE");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("SOFTWARE".equals(qName)) {
			item = new ProducaoTecnologica();
			item.setTipoProducaoTecnologica(new TipoProducaoTecnologica(TipoProducaoTecnologica.SOFTWARE));
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoProducao(TipoProducao.MAQUETES_PROTOTIPOS_OUTROS);
			item.setTipoParticipacao(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoRegiao(null);
			parse = true;
		}
		
		if ("DADOS-BASICOS-DO-SOFTWARE".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO-DO-SOFTWARE")));
			item.setLocalPublicacao((attributes.getValue("PAIS")));
		} 

//		if ("REGISTRO-OU-PATENTE".equals(qName)) {
//			item.set
//			numero = attributes.getValue("CODIGO-DO-REGISTRO-OU-PATENTE");
//			titulopat = attributes.getValue("TITULO-PATENTE");
//			isPatente = true;
//			if (numero.equals("")) {
//				numero = "0";
//			}
//		}

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
