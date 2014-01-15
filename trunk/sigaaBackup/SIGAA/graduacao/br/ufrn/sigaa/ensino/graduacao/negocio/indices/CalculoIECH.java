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
 * Classe responsável pelo cálculo do IECH (Índice de 
 * Eficiência em Carga Horária). 
 * 
 * Regulamento dos Cursos de Graduação - Art. 116. O Índice de 
 * Eficiência em Carga Horária (IECH) é a divisão da carga horária 
 * com aprovação pela carga horária utilizada, conforme fórmula 
 * matemática definida no Anexo III do regulamento
 * dos cursos de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalculoIECH implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			return dao.calculaIechDiscente(discente.getId());
		} finally {
			dao.close();
		}			
	}

}
