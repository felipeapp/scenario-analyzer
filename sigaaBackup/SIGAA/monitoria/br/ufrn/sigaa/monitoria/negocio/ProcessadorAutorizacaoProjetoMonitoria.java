/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;
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
import br.ufrn.sigaa.arq.dao.monitoria.AutorizacaoProjetoMonitoriaDao;
import br.ufrn.sigaa.monitoria.dominio.AutorizacaoProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Processador usado nas opera��es de autoriza��o do projeto 
 * pelos chefes dos departamentos.
 *
 * 
 * @author ilueny santos
 *
 */
public class ProcessadorAutorizacaoProjetoMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);		
		AutorizacaoProjetoMonitoria auto = null;		
		switch( ( (MovimentoCadastro)mov ).getAcao() ){
			case MovimentoCadastro.ACAO_CRIAR:
				auto = (AutorizacaoProjetoMonitoria)criar(mov);
				break;
			case MovimentoCadastro.ACAO_ALTERAR:
				auto = (AutorizacaoProjetoMonitoria) alterar(mov);
				break;
			case MovimentoCadastro.ACAO_REMOVER:
				remover(mov);
				break;
			default:
				throw new NegocioException("Tipo de a��o desconhecida!");
		}
		return auto;		
	}
	
	
	/**
 	 * Cria autoriza��o de projetos de monitoria que ser�o autorizados pelos 
 	 * chefes de departamentos
 	 * 
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
	public Object criar(Movimento mov) throws NegocioException, ArqException,	RemoteException {
	    GenericDAO dao = getGenericDAO(mov);
	    MovimentoCadastro cMov = (MovimentoCadastro) mov;
	    AutorizacaoProjetoMonitoria auto = (AutorizacaoProjetoMonitoria) cMov.getObjMovimentado();
	    try {
		auto.setAtivo(true);
		dao.create(auto);
		return auto;
	    } finally {
		dao.close();
	    }
	}
	
 	
	/**
 	 * Altera autoriza��o de projetos de monitoria 
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
	public Object alterar(Movimento mov) throws NegocioException, ArqException,	RemoteException {
		AutorizacaoProjetoMonitoriaDao dao = getDAO(AutorizacaoProjetoMonitoriaDao.class, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		AutorizacaoProjetoMonitoria auto = (AutorizacaoProjetoMonitoria) cMov.getObjMovimentado();
		
		try {
		    dao.update(auto);
			
		    /** @negocio: N�o h� mais autoriza��es pendentes... devemos mudar o status do projeto. */
		    if (dao.totalAutorizacoesPendentes(auto.getProjetoEnsino().getId()) == 0){
			ProjetoEnsino pm = dao.findByPrimaryKey(auto.getProjetoEnsino().getId(), ProjetoEnsino.class);
			dao.refresh(pm.getProjeto().getCoordenador());

			/** @negocio: N�o h� reprova��es dos departamentos. */
			if (dao.totalReprovacoesPelosDepatamentos(auto.getProjetoEnsino().getId()) == 0){
			
			    /** @negocio: Aguardando distribui��o do projeto para avalia��o pela comiss�o de monitoria. */
			    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO));
			    pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_PROJETO));
			    /** @negocio: Data de envio do projeto � pr�-reitoria de graua��o, ser� a data da �ltima autoriza��o. */
			    pm.setDataEnvio(new Date());
			    
			}else{
			    /** @negocio: Algum dos departamentos envolvidos n�o autorizou o envio do projeto. */
			    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS));
			    pm.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_NAO_AUTORIZADO_PELOS_DEPARTAMENTOS_ENVOLVIDOS));
			}

			ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);        				
			ProjetoHelper.gravarHistoricoSituacaoProjeto(pm.getSituacaoProjeto().getId(), pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());				
			dao.update(pm);

		    }
		    return auto;
		} finally {
		    dao.close();
		}
	}
	
 	
	/**
 	 * Remove autoriza��o de projetos de monitoria 
 	 *
 	 * @param mov
 	 * @return 
 	 * @throws NegocioException
 	 * @throws ArqException
 	 * @throws RemoteException
 	 */
	public Object remover(Movimento mov) throws NegocioException, ArqException,	RemoteException { 		
		GenericDAO dao = getGenericDAO(mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		AutorizacaoProjetoMonitoria auto = (AutorizacaoProjetoMonitoria) cMov.getObjMovimentado();
		try {
		    auto.setAtivo(false);
		    dao.update(auto);		
		    return null;
		} finally {
		    dao.close();
		}
 	}
 	
	/**
 	 * Faz a valida��o aqui apenas quando a a��o for de alterar, quando for
 	 * a��o criar ou remover a valida��o esta sendo feita no MBean.
 	 *
 	 * @param mov 	   
 	 * @throws NegocioException
 	 * @throws ArqException 	 
 	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro cMov = (MovimentoCadastro) mov;
		AutorizacaoProjetoMonitoria auto = (AutorizacaoProjetoMonitoria) cMov.getObjMovimentado();
		ListaMensagens erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(auto.getProjetoEnsino(), "Projeto de Monitoria", erros);
		ValidatorUtil.validateRequired(auto.getUnidade(), "Unidade", erros);

		switch(cMov.getAcao()){
		case MovimentoCadastro.ACAO_CRIAR:
			break;
		case MovimentoCadastro.ACAO_ALTERAR:
			checkRole(new int[] {SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO, SigaaPapeis.CHEFE_UNIDADE, SigaaPapeis.GESTOR_MONITORIA}, mov);
			break;
		case MovimentoCadastro.ACAO_REMOVER:
			break;
		}
		
		checkValidation(erros);
		
	}

}