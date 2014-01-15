/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Classe respons�vel pelo c�lculo do IEA (�ndice de
 * Efici�ncia Acad�mica).
 * 
 * Regulamento dos Cursos de Gradua��o - Art. 118. O �ndice de 
 * Efici�ncia Acad�mica (IEA) � o produto da MC pelo IECH e pelo IEPL, 
 * conforme f�rmula matem�tica definida no Anexo III do regulamento 
 * dos cursos de gradua��o.
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
