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
 * Classe respons�vel pelo c�lculo do IEPL (�ndice de
 * Efici�ncia em Per�odos Letivos).
 * 
 * Regulamento dos Cursos de Gradua��o - Art. 117. O �ndice de 
 * Efici�ncia em Per�odos Letivos (IEPL) � divis�o da carga hor�ria 
 * acumulada pela carga hor�ria esperada, conforme f�rmula matem�tica 
 * definida no Anexo III do regulamento dos cursos de gradua��o.
 * 
 * @author David Pereira
 *
 */
public class CalculoIEPL implements CalculoIndiceAcademico {

	@Override
	public double calcular(Discente discente, Movimento mov) throws DAOException {
		IndiceAcademicoDao dao = new IndiceAcademicoDao();
		
		try {
			return dao.calculaIeplDiscente(discente.getId());
		} finally {
			dao.close();
		}		
	}

}