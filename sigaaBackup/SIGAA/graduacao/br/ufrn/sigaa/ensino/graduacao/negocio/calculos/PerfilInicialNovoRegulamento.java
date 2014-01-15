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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DadosCalculosDiscente;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.util.RepositorioInformacoesCalculoDiscente;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Implementa��o do c�lculo do Perfil Inicial definido na nova Regulamenta��o da Gradua��o
 * 
 * @author Henrique Andr�
 * 
 */
public class PerfilInicialNovoRegulamento implements PerfilInicial {
	
	/**
	 * O perfil inicial de um aluno corresponde ao maior n�vel da estrutura curricular em 
	 * que pelo menos 75% da carga hor�ria discente correspondente a todos 
	 * os componentes curriculares obrigat�rios deste n�vel e dos seus precedentes tenham sido 
	 * aproveitados.
	 * 
	 * @param d
	 * @param mov
	 * @throws ArqException 
	 */
	@Override
	public Integer calcular(DiscenteAdapter d, Movimento mov) throws ArqException {
		EstruturaCurricularDao ecdao = DAOFactory.getInstance().getDAOMov(EstruturaCurricularDao.class, mov);
		DiscenteDao discenteDao = DAOFactory.getInstance().getDAOMov(DiscenteDao.class, mov);
		
		try {
			
			// Discente que estamos realizando a opere��o
			DadosCalculosDiscente dados = RepositorioInformacoesCalculoDiscente.INSTANCE.buscarInformacoes(d.getId());
			
			// Busca os componentes do curr�culo do aluno
			Collection<CurriculoComponente> curriculoComponentes = ecdao.findCurriculoComponentesByCurriculo(dados.getCurriculo().getId());
			
			// Busca os componentes que esse discente j� aproveitou
			Collection<ComponenteCurricular> componentesAproveitados = discenteDao.findComponentesCurriculares(d, SituacaoMatricula.APROVEITADO_CUMPRIU, SituacaoMatricula.APROVEITADO_TRANSFERIDO);
			
			// Perfil inicial do aluno inicia em 0
			Integer perfilInicial = 0;

			
			/* percorre semestre a semestre para saber em qual n�vel o aluno se encontra */ 
			for ( int i = 1; i <= dados.getCurriculo().getSemestreConclusaoIdeal(); i++ ) {

				boolean nivelAptdo = validarAptidaoPorNivel(curriculoComponentes, componentesAproveitados, i);
				
				if (nivelAptdo)
					perfilInicial = i;
				else
					break;
			}

			discenteDao.updateField(DiscenteGraduacao.class, d.getId(), "perfilInicial", perfilInicial);
			return perfilInicial;
		} finally {
			if (discenteDao != null)
				discenteDao.close();
			if (ecdao != null)
				ecdao.close();
		}
	}

	/**
	 * Retorna a quantidade de componentes obrigat�rios existente no semestre
	 * 
	 * @param curriculoComponentes
	 * @param nivel
	 * @return
	 * @throws ArqException 
	 */
	private boolean validarAptidaoPorNivel(Collection<CurriculoComponente> curriculoComponentes, Collection<ComponenteCurricular> componentesAproveitados, final int nivel) throws ArqException {
		
		// Curriculos Obrigatorios do n�vel informado
		Collection<CurriculoComponente> curriculoComponentesObrigatorios = findComponentesObrigatoriosByNivel(curriculoComponentes, nivel);

		// CH total de obrigatorias do n�vel informado
		int totalChObrigatoria = 0;
		for (CurriculoComponente cc : curriculoComponentesObrigatorios) 
			totalChObrigatoria += cc.getComponente().getChTotal();
		
		// Dentre as obrigatorias do n�vel, soma a CH que o discente pagou
		int totalChObrigatoriaAlunoPagou = 0;
		for (CurriculoComponente curriculoComponente : curriculoComponentesObrigatorios) {
			
			// Disciplina que esta sendo analisada
			ComponenteCurricular disciplina = curriculoComponente.getComponente();
			
			// se ele aproveitou direto
			boolean pagou = componentesAproveitados.contains(disciplina);
			
			// ou se foi aproveitada por equival�ncia
			boolean equivalente = false;
			if ( disciplina.getEquivalencia() != null && ! disciplina.getEquivalencia().equals("") )  {
				equivalente = ExpressaoUtil.eval(disciplina.getEquivalencia(), componentesAproveitados); 
			}
			
			if (pagou || equivalente)
				totalChObrigatoriaAlunoPagou += disciplina.getChTotal();			
		}
		
		double chObrigatoriaMinimaExigida = totalChObrigatoria * ParametroHelper.getInstance().getParametroDouble(ParametrosGraduacao.PERFIL_INICIAL_PORCENTAGEM_MINIMA_CH);
		
		return totalChObrigatoriaAlunoPagou >= chObrigatoriaMinimaExigida;
	}	
	
	
	/**
	 * Retorna uma cole��o das obrigat�rias por n�vel
	 * 
	 * @param curriculoComponentes
	 * @param nivel
	 * @return
	 */
	private Collection<CurriculoComponente> findComponentesObrigatoriosByNivel(Collection<CurriculoComponente> curriculoComponentes, final int nivel) {
		// Predicado para filtrar os componentes obrigatorios do nivel indicado
		Predicate predicateComponentesObrigatoriosPorNivel = new Predicate(){

			public boolean evaluate(Object obj) {
				
				CurriculoComponente cc = (CurriculoComponente) obj;
				
				if (cc.getSemestreOferta() == nivel && cc.getObrigatoria())
					return true;
				return false;
			}
			
		};
		
		@SuppressWarnings("unchecked")
		Collection<CurriculoComponente> curriculoComponentesObrigatorios = CollectionUtils.select(curriculoComponentes, predicateComponentesObrigatoriosPorNivel);
		return curriculoComponentesObrigatorios;
	}	
	
}
