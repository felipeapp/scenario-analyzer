/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/10/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.Date;

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
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoIndividualValidator;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.negocio.ProcessadorConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.ensino.negocio.dominio.ConsolidacaoIndividualMov;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Processador responsável por realizar a persistência da consolidação individual 
 * do ensino médio.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorConsolidacaoIndividualMedio extends ProcessadorConsolidacao {

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
				cons.getConsolicacao().getMatricula().setAnoFim(CalendarUtils.getAnoAtual());
				cons.getConsolicacao().getMatricula().setMesFim(CalendarUtils.getMesAtual());
			}
			
			dao.create( cons.getConsolicacao() );
			dao.update( cons.getConsolicacao().getMatricula() );
	
			MatriculaComponenteHelper.alterarSituacaoMatricula(matOriginal,
					cons.getConsolicacao().getMatricula().getSituacaoMatricula(), mov, dao);
	
			return null;
		}finally{
			if(dao != null){
				dao.close();
			}
		}
	}
	
	/** 
	 * Método responsável por retornar os parâmetros da gestora acadêmica. 
	 * */
	private ParametrosGestoraAcademica getParametros(Turma turma) throws DAOException {
		ParametrosGestoraAcademica param = null;
		param = ParametrosGestoraAcademicaHelper.getParametros(turma);
		return param;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		ConsolidacaoIndividualMov cons = (ConsolidacaoIndividualMov) mov;
		ListaMensagens lista = new ListaMensagens();

		ConsolidacaoIndividualValidator.validarConsolidacao(cons.getConsolicacao(), lista);
		checkValidation(lista);
	}
}
