/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/11/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.pesquisa;

import java.text.Normalizer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;

/**
 * Dao para consultas de avaliadores de resumos do CIC e 
 * avaliadores de apresentação de resumo do CIC.
 * 
 * @author Jean Guerethes
 */
public class AvaliadorCICDao extends GenericSigaaDAO{

	/**
	 * Serve para atualizar as presenças dos avaliadores pelo responsável do CIC.
	 */
	public void atualizacaoPresenca (Collection<AvaliadorCIC> avaliadoresCIC, CongressoIniciacaoCientifica congresso) throws DAOException {
		Iterator<AvaliadorCIC> it = avaliadoresCIC.iterator();
		while(it.hasNext()){
			AvaliadorCIC ava = it.next();
			updateField(AvaliadorCIC.class, ava.getId(), "presenca", ava.isPresenca());	
		}
	}
	
	public List<AvaliadorCIC> findByNome(String nome, CongressoIniciacaoCientifica congresso) throws DAOException {
		String nomeBusca = Normalizer.normalize(nome.trim(), Normalizer.Form.NFD);
		nomeBusca = nomeBusca.replaceAll("[^\\p{ASCII}]", "");
		
		@SuppressWarnings("unchecked")
		List<AvaliadorCIC> list = getSession()
				.createQuery(
						"from AvaliadorCIC a left join a.docente d1 left join d.pessoa p1 " +
						"left join a.discente d2 left join d2.pessoa p2 " +
						"where congresso.id = :congresso " +
						"and (" + UFRNUtils.convertUtf8UpperLatin9("p1.nomeAscii") + " like "
								+ UFRNUtils.toAsciiUpperUTF8("'" + nomeBusca + "%'") 
								+ " or "+ UFRNUtils.convertUtf8UpperLatin9("p2.nomeAscii") + " like "
								+ UFRNUtils.toAsciiUpperUTF8("'" + nomeBusca + "%'") +")")
				.setInteger("congresso", congresso.getId()).list();
		
		return list;
	}
}
