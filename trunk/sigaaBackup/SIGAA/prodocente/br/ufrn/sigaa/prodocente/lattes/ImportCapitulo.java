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
import br.ufrn.sigaa.prodocente.producao.dominio.Capitulo;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Classe que extrai dados referentes aos CapÌtulos de Livro - PRODUCOES BIBLIOGRIFICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportCapitulo extends ImportProducao<Capitulo> {

	public ImportCapitulo(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "CAPITULO-DE-LIVRO-PUBLICADO");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("CAPITULO-DE-LIVRO-PUBLICADO".equals(qName)) {
			item = new Capitulo();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			item.setTipoProducao(TipoProducao.CAPITULO_LIVROS);
			item.setTipoParticipacao(null);
			
			item.setTipoParticipacao(new TipoParticipacao(TipoParticipacao.AUTOR_GENERICO));
			item.setArea(null);
			item.setSubArea(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DO-CAPITULO".equals(qName) && parse) {
			item.setAnoReferencia(parseInt(attributes.getValue("ANO")));
			item.setTitulo((attributes.getValue("TITULO-DO-CAPITULO-DO-LIVRO")));
			String pais = (attributes.getValue("PAIS-DE-PUBLICACAO"));
			if ("BRASIL".equalsIgnoreCase(pais))
				item.setTipoRegiao(new TipoRegiao(TipoRegiao.NACIONAL));
			else
				item.setTipoRegiao(new TipoRegiao(TipoRegiao.INTERNACIONAL));
		} 

		if ("DETALHAMENTO-DO-CAPITULO".equals(qName) && parse) {
			item.setTituloLivro((attributes.getValue("TITULO-DO-LIVRO")));
			item.setPaginaInicial(getPaginaInicial(attributes.getValue("PAGINA-INICIAL")));
			item.setPaginaFinal(getPaginaFinal(attributes.getValue("PAGINA-FINAL")));
			// organizadores = attributes.getValue("ORGANIZADORES");
			// cidade = attributes.getValue("CIDADE-DA-EDITORA");
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
