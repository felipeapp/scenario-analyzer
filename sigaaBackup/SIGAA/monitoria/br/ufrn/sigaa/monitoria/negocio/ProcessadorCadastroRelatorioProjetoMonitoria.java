/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 16/11/2006
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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.StatusRelatorio;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;


/**
 * Processador para cadastro de relatórios finais de monitoria.
 *
 * @author ilueny santos
 *
 */
public class ProcessadorCadastroRelatorioProjetoMonitoria extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	    validate(mov);
	    MovimentoCadastro cMov = (MovimentoCadastro) mov;

		/** @negocio: Todos os coordenadores de monitoria devem cadastrar um relatório parcial e final do projeto. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_PROJETO_MONITORIA)){
		    GenericDAO dao = getGenericDAO(mov);
		    try {
			RelatorioProjetoMonitoria rp = (RelatorioProjetoMonitoria) cMov.getObjMovimentado();
			rp.setStatus(new StatusRelatorio(StatusRelatorio.CADASTRO_EM_ANDAMENTO));
			rp.setDataCadastro(new Date());
			rp.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			rp.setAtivo(true);
			dao.createOrUpdate(rp);
			return rp;
		    }finally {
			dao.close();
		    }
		}

		/** @negocio: Após o cadastro o relatório deve ser enviar para avaliação pela Pró-Reitoria de Graduação. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA)){
		    GenericDAO dao = getGenericDAO(mov);
		    try {
			RelatorioProjetoMonitoria rp = (RelatorioProjetoMonitoria) cMov.getObjMovimentado();
			rp.setStatus(new StatusRelatorio(StatusRelatorio.AGUARDANDO_DISTRIBUICAO));
			rp.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
			rp.setAtivo(true);
			rp.setDataEnvio(new Date());
			dao.createOrUpdate(rp);
			
			dao.clearSession();
			
                        /** @negocio: Ao enviar o relatório para Pró-Reitoria, o projeto passa para o estado de aguardando distribuição do relatório. */
                        ProjetoEnsino pm = rp.getProjetoEnsino();
                        pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO));
                        dao.updateField(ProjetoEnsino.class, pm.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO);
                        ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
                        ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_AGUARDANDO_DISTRIBUICAO_DO_RELATORIO, pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
                        return rp;
		    }finally {
			dao.close();
		    }
		}
		
		/** @negocio: O relatório do projeto pode ser devolvido para que o coordenador altere e reenvie em caso de erro. */
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR)){
			
			GenericDAO dao = getGenericDAO(mov);
			try {
			    RelatorioProjetoMonitoria rp = (RelatorioProjetoMonitoria) cMov.getObjMovimentado();
			    rp.setStatus(new StatusRelatorio(StatusRelatorio.CADASTRO_EM_ANDAMENTO));				
			    rp.setRegistroEntradaDevolucaoReedicao(mov.getUsuarioLogado().getRegistroEntrada());
			    rp.setDataEnvio(null);
			    rp.setAtivo(true);		
			    dao.update(rp);
			    
			    dao.clearSession();
			
			    /** @negocio: Ao devolver o relatório para o coordenador, o projeto volta para o estado de em execução. */
			    ProjetoEnsino pm = rp.getProjetoEnsino();
			    pm.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO));
			    
			    //TODO: verificar caso de projetos já concluídos.
			    
			    dao.update(pm);
			    ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, pm);
			    ProjetoHelper.gravarHistoricoSituacaoProjeto(TipoSituacaoProjeto.MON_EM_EXECUCAO, pm.getProjeto().getId(), mov.getUsuarioLogado().getRegistroEntrada());
			    return rp;
			}finally {
			    dao.close();
			}
		}
		
		return null;
	}


	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.DEVOLVER_RELATORIO_COORDENADOR)){
			checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
		}
		
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_PROJETO_MONITORIA)){
			// coordenador do projeto
		}
		
		if ( ((MovimentoCadastro)mov).getCodMovimento().equals(SigaaListaComando.ENVIAR_RELATORIO_PROJETO_MONITORIA)){
			// coordenador do projeto			
		}		
	}

}