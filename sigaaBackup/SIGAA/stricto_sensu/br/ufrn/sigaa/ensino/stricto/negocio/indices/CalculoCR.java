/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.indices;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.graduacao.IndiceAcademicoDao;
import br.ufrn.sigaa.ensino.negocio.CalculoIndiceAcademico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe respons�vel pelo c�lculo do CR (Coeficiente de
 * Rendimento), �ndice acad�mico dos alunos de p�s gradua��o
 * stricto sensu. C�lculo � semelhante ao IRA.
 * 
 * @author David Pereira
 *
 */
public class CalculoCR implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente d, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();

		try {
			return dao.calculaIraDiscenteStricto(d.getId());
		} finally {
			dao.close();
		}
	}

}
