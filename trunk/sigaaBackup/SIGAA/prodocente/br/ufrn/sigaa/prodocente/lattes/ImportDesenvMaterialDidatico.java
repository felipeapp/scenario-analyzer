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
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDidatico;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoInstancia;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes ao Desenvolvimento de Material Didático - PRODUÇÕES TÉCNICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportDesenvMaterialDidatico extends ImportProducao<TextoDidatico> {

	public ImportDesenvMaterialDidatico(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "DESENVOLVIMENTO-DE-MATERIAL-DIDATICO-OU-INSTRUCIONAL");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("DESENVOLVIMENTO-DE-MATERIAL-DIDATICO-OU-INSTRUCIONAL".equals(qName)) {
			item = new TextoDidatico();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.TEXTO_DIDATICO);
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			item.setTipoInstancia(new TipoInstancia(TipoInstancia.OUTROS));
			
			parse = true;
		}

		if ("DADOS-BASICOS-DO-MATERIAL-DIDATICO-OU-INSTRUCIONAL".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO")));
			item.setLocalPublicacao((attributes.getValue("PAIS")));
			
			String destaque = attributes.getValue("FLAG-RELEVANCIA"); 
			if ("SIM".equals(destaque))
				item.setDestaque(true);
			else
				item.setDestaque(false);
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
