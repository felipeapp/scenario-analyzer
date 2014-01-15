/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/03/2010
 */
package br.ufrn.sigaa.ensino.graduacao.negocio.indices;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.ensino.negocio.CalculoIndiceAcademico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável pelo cálculo do IEAN (Índice de
 * Eficiência Acadêmica Normalizado).
 * 
 * Regulamento dos Cursos de Graduação - Art. 119. O Índice de 
 * Eficiência Acadêmica Normalizado (IEAN) é o produto da MCN 
 * pelo IECH e pelo IEPL, conforme fórmula matemática definida 
 * no Anexo III do regulamento dos cursos de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalculoIEAN implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			Double mcn = dao.buscaMcnDiscente(discente.getId());
			Double iech = dao.buscaIechDiscente(discente.getId());
			Double iepl = dao.buscaIeplDiscente(discente.getId());
			
			return mcn * iech * iepl;
		} finally {
			dao.close();
		}			
	}

}
