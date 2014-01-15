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
import br.ufrn.sigaa.prodocente.producao.dominio.Livro;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

/**
 * Classe que extrai dados referentes aos Livros publicados ou organizados - PRODUCOES BIBLIOGRAFICAS.
 * 
 * @author David Pereira
 *
 */
public class ImportLivroPublicado extends ImportProducao<Livro> {
	
	public ImportLivroPublicado(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "LIVRO-PUBLICADO-OU-ORGANIZADO");
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("LIVRO-PUBLICADO-OU-ORGANIZADO".equals(qName)) {
			item = new Livro();
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.LIVRO);
			item.setTipoParticipacao(null);
			item.setArea(null);
			item.setSubArea(null);
			
			parse = true;
		}
		
		if ("DADOS-BASICOS-DO-LIVRO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			//item.setTipoParticipacao(TipoParticipacao.)
			item.setTitulo(attributes.getValue("TITULO-DO-LIVRO"));
			item.setDestaque(Boolean.valueOf(attributes.getValue("FLAG-RELEVANCIA")));
			String pais = attributes.getValue("PAIS-DE-PUBLICACAO");
			if (pais.equalsIgnoreCase("Brasil"))
				item.setTipoRegiao(new TipoRegiao(TipoRegiao.NACIONAL));
			else
				item.setTipoRegiao(new TipoRegiao(TipoRegiao.INTERNACIONAL));
		} 
		
		if ("DETALHAMENTO-DO-LIVRO".equals(qName) && parse) {
			String paginas = (attributes.getValue("NUMERO-DE-PAGINAS"));
			if ("".equals(paginas))
				item.setQuantidadePaginas(0);
			else
				item.setQuantidadePaginas(Integer.valueOf(paginas));
			
			item.setEditora((attributes.getValue("NOME-DA-EDITORA")));
			item.setLocalPublicacao((attributes.getValue("CIDADE-DA-EDITORA")));
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
