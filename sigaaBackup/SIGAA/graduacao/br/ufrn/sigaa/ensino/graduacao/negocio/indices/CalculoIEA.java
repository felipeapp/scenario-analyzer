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
 * Classe responsável pelo cálculo do IEA (Índice de
 * Eficiência Acadêmica).
 * 
 * Regulamento dos Cursos de Graduação - Art. 118. O Índice de 
 * Eficiência Acadêmica (IEA) é o produto da MC pelo IECH e pelo IEPL, 
 * conforme fórmula matemática definida no Anexo III do regulamento 
 * dos cursos de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalculoIEA implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			Double mc = dao.buscaMcDiscente(discente.getId());
			Double iech = dao.buscaIechDiscente(discente.getId());
			Double iepl = dao.buscaIeplDiscente(discente.getId());
			
			return mc * iech * iepl;
		} finally {
			dao.close();
		}			
	}

}
