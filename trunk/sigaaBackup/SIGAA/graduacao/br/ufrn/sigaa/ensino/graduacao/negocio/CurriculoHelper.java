/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 15/04/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Classe que ter� m�todos de aux�lio com as opera��es sobre curr�culo
 * @author Victor Hugo
 */
public class CurriculoHelper {

	/**
	 * Calcula os totais do curr�culo informado:
	 *  - carga hor�ria de atividades obrigat�rias
	 *  - carga hor�ria de n�o atividade obrigat�ria
	 *  - cr�ditos de n�o atividade obrigat�rios
	 *  - carga hor�ria total m�nima
	 *  - cr�ditos totais m�nimos
	 */
	public static void calcularTotaisCurriculo( Curriculo curriculo ) throws DAOException{
	
		GenericDAO dao = DAOFactory.getGeneric(Sistema.SIGAA);
		
		try {
			
			int chAtividadeObrig = 0;
			int chNaoAtividadeObrig = 0;
			int crNaoAtividadeObrig = 0;
	
			int crPratico = 0;
			int crTeorico = 0;
			int chPratico = 0;
			int chTeorico = 0;
			int chAAE = 0; //CH de atividades acad�micas especificas
			
			ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(curriculo.getCurso());
			
			for (CurriculoComponente componenteCurricular : curriculo.getCurriculoComponentes()) {
	
				ComponenteCurricular comp = dao.findByPrimaryKey(componenteCurricular.getComponente().getId(), ComponenteCurricular.class);
	
				if (componenteCurricular.getObrigatoria() && (comp.isAtividade() || comp.isAtividadeColetiva())) {
					chAtividadeObrig += comp.getChTotal();
					
				} else if (componenteCurricular.getObrigatoria() && !comp.isAtividade() && !comp.isAtividadeColetiva()) {
					chNaoAtividadeObrig += comp.getChTotal();
					crNaoAtividadeObrig += comp.getCrTotal();
				}
				
				if( componenteCurricular.getObrigatoria() ){
					
					chTeorico += comp.getChAula();
					chPratico += comp.getChEstagio() + comp.getChLaboratorio();
					
					crTeorico += comp.getDetalhes().getCrAula();
					crPratico += comp.getDetalhes().getCrEstagio() + comp.getDetalhes().getCrLaboratorio();
					
					if( comp.isAtividade() )
						chAAE += comp.getChAula() + comp.getChEstagio() + comp.getChLaboratorio();
				}
				
				dao.detach(comp);
			}
			
			curriculo.setCrNaoAtividadeObrigatorio(crNaoAtividadeObrig);
			curriculo.setChNaoAtividadeObrigatoria(chNaoAtividadeObrig);
			curriculo.setChAtividadeObrigatoria(chAtividadeObrig);
			curriculo.setChTotalMinima(curriculo.getChOptativasMinima() + chAtividadeObrig + chNaoAtividadeObrig);
			
			if (curriculo.getCurso().isStricto()) {
				int crOptativasMinima = curriculo.getChOptativasMinima() / parametros.getHorasCreditosAula();
				curriculo.setCrTotalMinimo(crNaoAtividadeObrig + crOptativasMinima);
			} else {
				curriculo.setCrTotalMinimo(crNaoAtividadeObrig);
			}
			
			curriculo.setChTeoricos(chTeorico);
			curriculo.setChPraticos(chPratico);
			curriculo.setCrTeoricos(crTeorico);
			curriculo.setChTeoricos(chTeorico);
			curriculo.setChAAE(chAAE);
			
		} finally {
			dao.close();
		}
	
	}
	
	/**
	 * Calcula os cr�ditos para os componentes das �reas de concentra��o comum e espec�fica do discente.
	 *  - cr�ditos totais m�nimos contabilizando os componentes pertencentes as �reas de concentra��o comum e espec�fica do discente
	 */
	public static void calcularCrTotalAreaConcentracaoCurriculo( DiscenteStricto discente ) throws DAOException{
	
		EstruturaCurricularDao dao = new EstruturaCurricularDao();
		try {
			if (!discente.isRegular())
				return;
			
			Curriculo curriculo = discente.getCurriculo();
			
			int chAtividadeObrig = 0;
			int chNaoAtividadeObrig = 0;
			int crNaoAtividadeObrig = 0;
	
			ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(discente.getCurriculo().getCurso());
			Collection<CurriculoComponente> listCurriculoComponentes = dao.findCurriculoComponentesObrigatoriosAreaConcentracaoByDiscente(discente);
			for (CurriculoComponente curriculoComponente : listCurriculoComponentes) {
	
				ComponenteCurricular comp = curriculoComponente.getComponente();
	
				if (curriculoComponente.getObrigatoria() && (comp.isAtividade() || comp.isAtividadeColetiva())) {
					chAtividadeObrig += comp.getChTotal();
					
				} else if (curriculoComponente.getObrigatoria() && !comp.isAtividade() && !comp.isAtividadeColetiva()) {
					chNaoAtividadeObrig += comp.getChTotal();
					crNaoAtividadeObrig += comp.getChTotal() / parametros.getHorasCreditosAula();
				}
				
				dao.detach(comp);
			}
			
			curriculo.setChTotalMinima(curriculo.getChOptativasMinima() + chAtividadeObrig + chNaoAtividadeObrig);
			
			int crOptativasMinima = curriculo.getChOptativasMinima() / parametros.getHorasCreditosAula();
			curriculo.setCrTotalMinimo(crNaoAtividadeObrig + crOptativasMinima);
			
			discente.setCrExigidosAreaConcentracao(curriculo.getCrTotalMinimo());
			
		} finally {
			dao.close();
		}
	
	}
}
