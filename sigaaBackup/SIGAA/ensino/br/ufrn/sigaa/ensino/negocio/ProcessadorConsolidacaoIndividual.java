/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.ConsolidacaoIndividualMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Processador responsável por realizar a persistência da consolidação individual
 * 
 * @author Gleydson
 *
 */
public class ProcessadorConsolidacaoIndividual extends ProcessadorConsolidacao {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		ConsolidacaoIndividualMov cons = (ConsolidacaoIndividualMov) mov;

		cons.getConsolicacao().setData(new Date());
		cons.getConsolicacao().setRegistro(mov.getUsuarioLogado().getRegistroEntrada());

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try{
			MatriculaComponente matOriginal = dao.findByPrimaryKey(cons.getConsolicacao().
					getMatricula().getId(), MatriculaComponente.class);
			cons.getConsolicacao().getMatricula().setComponente( dao.findByPrimaryKey(cons.getConsolicacao().getMatricula().getComponente().getId(), ComponenteCurricular.class ) );
			if( cons.getConsolicacao().getMatricula().getDetalhesComponente() == null )
				cons.getConsolicacao().getMatricula().setDetalhesComponente( dao.findByPrimaryKey(matOriginal.getComponente().getDetalhes().getId(), ComponenteDetalhes.class ) );
			else
				cons.getConsolicacao().getMatricula().setDetalhesComponente( dao.findByPrimaryKey(matOriginal.getDetalhesComponente().getId(), ComponenteDetalhes.class ) );
			dao.detach( matOriginal );
	
			ParametrosGestoraAcademica params = getParametros(cons.getConsolicacao().getMatricula().getTurma());
	
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(cons.getConsolicacao().getMatricula().getDiscente(), params);
			
			// calcula o tipo de resultado da consolidação
			cons.getConsolicacao().getMatricula().setMetodoAvaliacao(cons.getMetodoAvaliacao());
			cons.getConsolicacao().getMatricula().setEstrategia(estrategia);
			cons.getConsolicacao().getMatricula().consolidar();
	
			if (cons.getConsolicacao().getMatricula().getDiscente().isStricto()) {
				cons.getConsolicacao().getMatricula().setAnoFim(CalendarUtils.getAno(cons.getConsolicacao().getMatricula().getTurma().getDataFim()));
				cons.getConsolicacao().getMatricula().setMesFim(CalendarUtils.getMesByData(cons.getConsolicacao().getMatricula().getTurma().getDataFim())+1);
			}
			
			dao.create( cons.getConsolicacao() );
			dao.update( cons.getConsolicacao().getMatricula() );
	
			MatriculaComponenteHelper.alterarSituacaoMatricula(matOriginal,
					cons.getConsolicacao().getMatricula().getSituacaoMatricula(), mov, dao);
	
			// Se a disciplina atual fizer parte de um bloco, processar o bloco completamente
			if (matOriginal.getTurma().getDisciplina().getBlocoSubUnidade() != null) {
				MatriculaComponente mat = dao.findByPrimaryKey(cons.getConsolicacao().getMatricula().getId(), MatriculaComponente.class);
				cons.getConsolicacao().setMatricula(mat);
				cons.getConsolicacao().getMatricula().setEstrategia(estrategia);
				processarMatriculasBloco(mov, cons.getConsolicacao().getMatricula());
			}
	
			if( cons.getConsolicacao().getMatricula().getDiscente().getNivel() == NivelEnsino.GRADUACAO ){
				DiscenteGraduacao dg = dao.findByPrimaryKey(cons.getConsolicacao().getMatricula().getDiscente().getId(), DiscenteGraduacao.class);
				DiscenteCalculosHelper.atualizarTodosCalculosDiscente( dg, mov);
			}
	
			return null;
		}finally{
			if(dao != null){
				dao.close();
			}
		}
	}

	/**
	 * Retorna os parâmetros com base na turma. Se a turma
	 * for de gradação retorna os parâmetros da unidade global
	 * da graudação, senão retorna os parâmetros da turma.
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	private ParametrosGestoraAcademica getParametros(Turma turma) throws DAOException {
		char nivel = turma.getDisciplina().getNivel();
		ParametrosGestoraAcademica param = null;
		
		if (nivel == NivelEnsino.GRADUACAO) {
			param = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();
		} else {
			param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		}
		return param;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		ConsolidacaoIndividualMov cons = (ConsolidacaoIndividualMov) mov;
		ListaMensagens lista = new ListaMensagens();

		ConsolidacaoIndividualValidator.validarConsolidacao(cons.getConsolicacao(), lista);
		checkValidation(lista);
	}


}
