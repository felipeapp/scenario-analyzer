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
 * Classe respons�vel pelo c�lculo do MC (M�dia de
 * Conclus�o).
 * 
 * Regulamento dos Cursos de Gradua��o - Art. 113. A M�dia de 
 * Conclus�o (MC) � a media do rendimento escolar final obtido 
 * pelo aluno nos componentes curriculares em que obteve �xito, 
 * ponderadas pela carga hor�ria discente dos componentes, conforme 
 * f�rmula matem�tica definida no Anexo III do regulamento dos 
 * cursos de gradua��o.
 * 
 * @author David Pereira
 *
 */
public class CalculoMC implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			return dao.calculaMcDiscente(discente.getId());
		} finally {
			dao.close();
		}
	}

}
