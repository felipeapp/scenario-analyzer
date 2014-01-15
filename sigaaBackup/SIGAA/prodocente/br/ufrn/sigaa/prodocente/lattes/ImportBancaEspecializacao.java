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

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Banca;
import br.ufrn.sigaa.prodocente.producao.dominio.NaturezaExame;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoBanca;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Classe que extrai dados referentes extrai dados referentes a Participação em Banca de Especialização
 * 
 * @author David Pereira
 *
 */
public class ImportBancaEspecializacao extends ImportProducao<Banca> {

	public ImportBancaEspecializacao(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes) {
		super(input, dao, producoes, "PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO");
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if ("PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO".equals(qName)) {
			item = new Banca();
			item.setTipoBanca(new TipoBanca(TipoBanca.CURSO));
			item.setNaturezaExame(NaturezaExame.MONOGRAFIA_ESPECIALIZACAO);
			item.setSequenciaProducao(parseInt(attributes.getValue("SEQUENCIA-PRODUCAO")));
			
			item.setTipoProducao(TipoProducao.BANCA_CURSO_SELECOES);
			item.setTipoParticipacao(null);
			item.setArea(null);
			item.setSubArea(null);
			item.setDepartamento(null);
			item.setInstituicao(null);
			item.setMunicipio(null);
			item.setCategoriaFuncional(null);
			
			parse = true;
		}
		if ("DADOS-BASICOS-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO".equals(qName) && parse) {
			item.setAnoReferencia(Integer.valueOf(attributes.getValue("ANO")));
			item.setTitulo(attributes.getValue("TITULO"));
			
			String pais = attributes.getValue("PAIS");
			
			try {
				item.setPais(getPais(pais));
			} catch (DAOException e) {
				item.setPais(null);
			}
		}

		if ("DETALHAMENTO-DA-PARTICIPACAO-EM-BANCA-DE-APERFEICOAMENTO-ESPECIALIZACAO".equals(qName) && parse) {
			//item.setInstituicao(instituicao)attributes.getValue("NOME-INSTITUICAO");
		}

		if ("PARTICIPANTE-BANCA".equals(qName) && parse) {
			String autor = attributes.getValue("NOME-COMPLETO-DO-PARTICIPANTE-DA-BANCA");
			if (item.getAutor() == null)
				item.setAutor(autor);
			else
				item.setAutor(item.getAutor() + ", " + autor);
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

		if ("INFORMACOES-ADICIONAIS".equals(qName) && parse) {
			String informacao = attributes.getValue("DESCRICAO-INFORMACOES-ADICIONAIS");
			if (informacao.equals(""))
				informacao = "  ";
			item.setInformacao(informacao);
		}
	}
}
