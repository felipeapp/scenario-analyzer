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
 * Classe que extrai dados referentes as Maquetes - PRODUÇÕES TÉCNICAS.
 *  
 * @author David Pereira
 *
 */
public class ImportMaquete extends ImportProducao<ProducaoTecnologica> {

	public ImportMaquete(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "MAQUETE");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("MAQUETE".equals(qName)) {
			item = new ProducaoTecnologica();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoParticipacao(null);
			item.setArea(null);	
			item.setSubArea(null);
			item.setTipoRegiao(null);
			item.setTipoProducao(TipoProducao.MAQUETES_PROTOTIPOS_OUTROS);
			item.setTipoProducaoTecnologica(new TipoProducaoTecnologica(TipoProducaoTecnologica.MAQUETE));
			parse = true;
		}

		if ("DADOS-BASICOS-DA-MAQUETE".equals(qName) && parse) {

			item.setTitulo((attributes.getValue("TITULO")));
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setLocalPublicacao(attributes.getValue("PAIS"));
			item.setTipoProducao(TipoProducao.MAQUETES_PROTOTIPOS_OUTROS);
			
		}
		
		if ("AUTORES".equals(qName) && parse) {
			String autor = (attributes.getValue("NOME-COMPLETO-DO-AUTOR"));
			if (item.getAutores() == null)
				item.setAutores(autor);
			else
				item.setAutores(item.getAutores() + ", " + autor);
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
