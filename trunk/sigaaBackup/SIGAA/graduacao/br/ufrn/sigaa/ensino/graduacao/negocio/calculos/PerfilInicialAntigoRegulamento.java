/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '22/07/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Implementa��o do c�culo do Perfil Inicial para alunos que entraram antes do valor definido em ParametrosGraduacao.ANO_VIGOR_NOVO_REGULAMENTO
 * 
 * @author Henrique Andr�
 *
 */
public class PerfilInicialAntigoRegulamento implements PerfilInicial {
	
	/**
	 * Determina o perfilInicial do aluno depois dos aproveitamentos.
	 * O perfil inicial � atualizado caso o aluno tenha aproveitado todos os componentes obrigat�rios
	 * de um semestre.
	 * Exemplo:
	 * Caso o aluno aproveite TODOS os componentes obrigat�rios dos 2 primeiros semestres do seu curso,
	 * ele passa a ter o perfilInicial 2.
	 * @param d
	 * @param mov
	 * @throws ArqException 
	 * @throws ArqException 
	 */
	@Override
	public Integer calcular(DiscenteAdapter d, Movimento mov) throws ArqException {
		EstruturaCurricularDao ecdao = DAOFactory.getInstance().getDAOMov(EstruturaCurricularDao.class, mov);
		DiscenteDao discenteDao = DAOFactory.getInstance().getDAOMov(DiscenteDao.class, mov);
		try {
			DiscenteGraduacao discente = ecdao.findByPrimaryKey(d.getId(), DiscenteGraduacao.class);
			// busca os componentes do curr�culo do aluno
			Collection<CurriculoComponente> curriculoComponentes = ecdao.findCurriculoComponentesByCurriculo(discente.getCurriculo().getId());
			// busca os componentes que esse discente j� aproveitou
			Collection<ComponenteCurricular> componentesAproveitados = discenteDao.findComponentesCurriculares(
					discente, SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_TRANSFERIDO);
			Integer perfilInicial = 0;
	
			/* percorre semestre a semestre para saber em qual n�vel o aluno se encontra */ 
			calculo: 
				for ( int i = 1; i <= discente.getCurriculo().getSemestreConclusaoIdeal(); i++ ) {
				
					// para todos os componentes curriculares do per�odo
					for (CurriculoComponente ccc  : curriculoComponentes) {
						
						ComponenteCurricular cc = ccc.getComponente();
						
						// se a disciplina for obrigat�rio no curr�culo e do semestre em teste
						if (ccc.getObrigatoria() && ccc.getSemestreOferta() == i ) {
							
							boolean pagou = componentesAproveitados.contains(cc); // se ele aproveitou direto
							// ou se foi aproveitada por equival�ncia
							boolean equivalente = false;
							if ( cc.getEquivalencia() != null && ! cc.getEquivalencia().equals("") )  {
								equivalente = ExpressaoUtil.eval(cc.getEquivalencia(), componentesAproveitados); 
							}
							if ( !pagou && !equivalente ) {
								break calculo;
							}
						}
					}
					perfilInicial = i;
				}
	
			discenteDao.updateField(DiscenteGraduacao.class, discente.getId(), "perfilInicial", perfilInicial);
			return perfilInicial;
		} finally {
			if (discenteDao != null)
				discenteDao.close();
			if (ecdao != null)
				ecdao.close();
		}
	}
}
