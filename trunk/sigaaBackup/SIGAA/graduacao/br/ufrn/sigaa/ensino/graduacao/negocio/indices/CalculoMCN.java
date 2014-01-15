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
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe respons�vel pelo c�lculo do MCN (M�dia de
 * Conclus�o Normalizada).
 * 
 * Regulamento dos Cursos de Gradua��o - Art. 114. O c�lculo da 
 * M�dia de Conclus�o Normalizada (MCN) corresponde � padroniza��o da 
 * MC do aluno, considerando-se a m�dia e o desvio-padr�o das MCs de 
 * todos os alunos que conclu�ram o mesmo curso/modalidade na UFRN nos 
 * �ltimos 5 (cinco) anos, conforme f�rmula matem�tica definida no 
 * Anexo III do regulamento dos cursos de gradua��o.
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
