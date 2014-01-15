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
 * Classe responsável pelo cálculo do MC (Média de
 * Conclusão).
 * 
 * Regulamento dos Cursos de Graduação - Art. 113. A Média de 
 * Conclusão (MC) é a media do rendimento escolar final obtido 
 * pelo aluno nos componentes curriculares em que obteve êxito, 
 * ponderadas pela carga horária discente dos componentes, conforme 
 * fórmula matemática definida no Anexo III do regulamento dos 
 * cursos de graduação.
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
