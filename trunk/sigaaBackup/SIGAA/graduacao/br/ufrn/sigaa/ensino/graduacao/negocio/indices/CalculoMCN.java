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
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe responsável pelo cálculo do MCN (Média de
 * Conclusão Normalizada).
 * 
 * Regulamento dos Cursos de Graduação - Art. 114. O cálculo da 
 * Média de Conclusão Normalizada (MCN) corresponde à padronização da 
 * MC do aluno, considerando-se a média e o desvio-padrão das MCs de 
 * todos os alunos que concluíram o mesmo curso/modalidade na UFRN nos 
 * últimos 5 (cinco) anos, conforme fórmula matemática definida no 
 * Anexo III do regulamento dos cursos de graduação.
 * 
 * @author David Pereira
 *
 */
public class CalculoMCN implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			return dao.calculaMcnDiscente(CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(), discente.getId());
		} finally {
			dao.close();
		}
	}

}
