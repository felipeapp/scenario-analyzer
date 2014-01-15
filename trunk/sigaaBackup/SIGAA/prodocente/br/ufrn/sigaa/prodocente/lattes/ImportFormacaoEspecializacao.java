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
import br.ufrn.sigaa.prodocente.atividades.dominio.FormacaoAcademica;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Classe que extrai dados referentes a Monografia de Especialização do Docente- FORMACAO ACADEMICA.
 * 
 * Deprecated devido ao Refactoring em Formação Acadêmica, que será consultado utilizando o serviço disponível no SIGRH
 * @author David Pereira
 */
@Deprecated
public class ImportFormacaoEspecializacao extends ImportProducao<FormacaoAcademica> {

	public ImportFormacaoEspecializacao(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "FORMACAO-ACADEMICA-TITULACAO");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("FORMACAO-ACADEMICA-TITULACAO".equals(qName)) {
			item = new FormacaoAcademica();
			//item.setFormacao();
			parse = true;
		}
		
		if ("ESPECIALIZACAO".equals(qName)) {
			//anoconclusao = atts.getValue("ANO-DE-CONCLUSAO");
			
			item.setTitulo(attributes.getValue("TITULO-DA-MONOGRAFIA"));
			item.setOrientador(attributes.getValue("NOME-DO-ORIENTADOR"));
			//instituicao = attributes.getValue("NOME-INSTITUICAO");
		}
		
		if ("RESIDENCIA-MEDICA".equals(qName)) {
			//anoconclusao = atts.getValue("ANO-DE-CONCLUSAO");
			
			item.setTitulo(attributes.getValue("TITULO-DA-RESIDENCIA-MEDICA"));
			//instituicao = attributes.getValue("NOME-INSTITUICAO");
		}

		if ("PALAVRAS-CHAVE".equals(qName)) {
			item.setPalavraChave1(attributes.getValue("PALAVRA-CHAVE-1"));
			item.setPalavraChave2(attributes.getValue("PALAVRA-CHAVE-2"));
			item.setPalavraChave3(attributes.getValue("PALAVRA-CHAVE-3"));
		} 

		if ("AREA-DO-CONHECIMENTO-1".equals(qName)) {
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
