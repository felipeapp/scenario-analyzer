/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 * 
 * Created on 09/11/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.monitoria.AvaliacaoMonitoriaDao;
import br.ufrn.sigaa.arq.dao.projetos.MembroComissaoDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.monitoria.dominio.AvaliacaoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao;
import br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador para registrar a avaliação de um projeto de monitoria.
 *
 * @author David Ricardo
 * @author ilueny santos
 *
 */
public class ProcessadorAvaliacaoProjetoMonitoria extends AbstractProcessador {
	
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
	    validate(mov);
	    Object obj = null;
	    switch(((ProjetoMonitoriaMov)mov).getAcao()){
	    	case ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_COMISSAO:
	    	    obj = avaliarPorMembroComissao(mov);
	    	    break;
	    	case ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_PROGRAD:
	    	    obj = avaliarPorMembroPrograd(mov);
	    	    break;
	    	case ProjetoMonitoriaMov.ACAO_PUBLICAR_RESULTADO_AVALIACOES:
	    		publicarResultadoAvaliacoes(mov);
	    		break;	    	    
	    	default:
	    	    throw new NegocioException("Tipo de ação desconhecida!");
	    }
	    return obj;
	}

	/**
	 * Realiza a avaliação de um projeto por um 
	 * membro da Pró-Reitoria de graduação.
	 * 
	 */
	private Object avaliarPorMembroPrograd(Movimento mov) throws NegocioException, ArqException, RemoteException {
	    MembroComissaoDao dao =  getDAO(MembroComissaoDao.class, mov) ;		
	    try {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		AvaliacaoMonitoria aval = cMov.getObjMovimentado();
		aval.calcularMedia();
		aval.setDataAvaliacao(new Date());
		aval.setTipoAvaliacao(TipoAvaliacaoMonitoria.getAvaliacaoProjeto());
		aval.setStatusAvaliacao(StatusAvaliacao.getStatusAvaliado());
		aval.setAvaliador(dao.findByUsuario((Usuario) mov.getUsuarioLogado(), MembroComissao.MEMBRO_COMISSAO_MONITORIA));
		aval.setAvaliacaoPrograd(true);
		dao.createOrUpdate(aval);			
		dao.detach(aval);

		ProjetoEnsino pm = dao.findByPrimaryKey(aval.getProjetoEnsino().getId(), ProjetoEnsino.class);

		/** @negocio: A média de análise do projeto é definida na avaliação por discrepância realizada pela Pró-Reitoria de Graduação. */
		pm.setMediaAnalise(aval.getNotaAvaliacao());
		pm.setNotaAvaliacaoFinal(aval.getNotaAvaliacao());
		pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AVALIADO));
		pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AVALIADO));
		pm.setAtivo(true);

		dao.update(pm);
		ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());			
		return aval;
	    } finally {
		dao.close();
	    }
	}
	
	
	
	
	/**
	 * Na avaliação realizada por membros da comissão de da prograd 2 avaliadores dão notas para o projeto
	 * se a diferença entre essas notas for superior a 3 o projeto será reavaliado por um membro da prograd
	 * diretamente e a nota dessa avaliação final será a nota definitiva do projeto.
	 * 
	 * Este método grava a 1 e a 2 nota e calcula a média, verificando se o projeto deverá ser analisado depois 
	 * pela prograd.
	 * 
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Object avaliarPorMembroComissao(Movimento mov) throws NegocioException, ArqException, RemoteException {
	    MembroComissaoDao dao = getDAO(MembroComissaoDao.class, mov) ;
	    AvaliacaoMonitoriaDao avaDao = getDAO(AvaliacaoMonitoriaDao.class, mov) ;
	    
	    try {
	    	ProjetoMonitoriaMov cMov = (ProjetoMonitoriaMov) mov;
	    	AvaliacaoMonitoria aval = cMov.getObjMovimentado();

			aval.calcularMedia();
			aval.setDataAvaliacao(new Date());
			aval.setTipoAvaliacao(TipoAvaliacaoMonitoria.getAvaliacaoProjeto());
			aval.setStatusAvaliacao(StatusAvaliacao.getStatusAvaliado());
			aval.setAvaliador(dao.findByUsuario((Usuario) mov.getUsuarioLogado(), MembroComissao.MEMBRO_COMISSAO_MONITORIA));
			aval.setAvaliacaoPrograd(false);		
			dao.update(aval);
			dao.detach(aval); //retira aval da mesma sessão de projetos
	
			ProjetoEnsino pm = dao.findByPrimaryKey(aval.getProjetoEnsino().getId(), ProjetoEnsino.class);
			
			boolean haAvaliacaoPendente = avaDao.haAvaliadoresSemAvaliacao(  pm.getId(), aval.getId() );
			int qntAvaliadores = avaDao.totalAvaliadores(pm.getId());
			
			if ( haAvaliacaoPendente ) {
			    pm.setNotaPrimeiraAvaliacao( aval.getNotaAvaliacao() );				
			    //evitar null pointer no cálculo da média
			    if (pm.getNotaSegundaAvaliacao() ==  null) { 
				pm.setNotaSegundaAvaliacao( 0.0 );
			    }				
			    pm.calculaMedia();				
	
			} else {
				if ( qntAvaliadores == 1 )
					pm.setNotaPrimeiraAvaliacao( aval.getNotaAvaliacao() );
				pm.setNotaSegundaAvaliacao( aval.getNotaAvaliacao() );
			    pm.calculaMedia();			
			    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AVALIADO));
			    pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AVALIADO));
			}

			dao.update(pm);
			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
			ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
			
			return aval;

	    } finally {
	    	dao.close();
	    	avaDao.close();
	    }
	
	}
	
	/**
	 * Realiza a publicação dos resultados das avaliações dos projetos de ensino.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	@SuppressWarnings("unchecked")
	private Object publicarResultadoAvaliacoes(Movimento mov) throws NegocioException, ArqException, RemoteException{
	    GenericDAO dao = getGenericDAO(mov);
	    try {
		Collection<ProjetoEnsino> lista = (Collection<ProjetoEnsino>) ((MovimentoCadastro) mov).getColObjMovimentado();
		for (ProjetoEnsino pm : lista) {

		    /* @negocio: Projetos de monitoria com média igual ou superior a 6 são recomendados. */
		    if ( pm.getMediaAnalise() >= pm.getEditalMonitoria().getMediaAprovacaoProjeto() ){ 
		    	pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_RECOMENDADO));
		    	pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_RECOMENDADO));
		    }

		    /* @negocio: Projetos de monitoria com média inferior a 6 não são recomendados. */
		    if( pm.getMediaAnalise() < pm.getEditalMonitoria().getMediaAprovacaoProjeto() ) {
		    	pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_RECOMENDADO));
		    	pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_RECOMENDADO));
		    }					

		    pm.setAtivo(true);
			dao.updateFields(ProjetoEnsino.class, pm.getId(), new String[] {"situacaoProjeto.id","ativo"}, 
					new Object[] {pm.getSituacaoProjeto().getId(), pm.isAtivo()});
			
		    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
		    ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
		}
	    }  finally {
		dao.close();
	    }

	    return null;
	}

	/**
	 * valida para que apenas os membros de monitoria executem a avaliação. 
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
	    ListaMensagens lista = new ListaMensagens();

	    switch(((ProjetoMonitoriaMov)mov).getAcao()){
	    	case ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_COMISSAO:
	    	    checkRole(new int[]{SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.GESTOR_MONITORIA}, mov);
	    	    break;
	    	case ProjetoMonitoriaMov.ACAO_AVALIAR_PROJETO_POR_MEMBRO_PROGRAD:
	    	    checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
	    		MembroComissao membroComissao = getDAO(MembroComissaoDao.class, mov).findByUsuario((Usuario)mov.getUsuarioLogado(), MembroComissao.MEMBRO_COMISSAO_MONITORIA);
	    		if (ValidatorUtil.isEmpty(membroComissao)){
	    			lista.addErro("Somente Gestores que fazem parte da Comissão de Monitoria podem realizar esta operação.");
	    		}
	    	    break;
	    	case ProjetoMonitoriaMov.ACAO_PUBLICAR_RESULTADO_AVALIACOES:
	    	    checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
	    	    EditalMonitoria edital = ((MovimentoCadastro)mov).getObjMovimentado();
	    	    ProjetoMonitoriaValidator.validaAvaliacoesDiscrepantes(edital, lista);
	    	    break;	    	    
	    	default:
	    	    throw new NegocioException("Tipo de ação desconhecida!");
	    }

	    checkValidation(lista);	    
	}
}
