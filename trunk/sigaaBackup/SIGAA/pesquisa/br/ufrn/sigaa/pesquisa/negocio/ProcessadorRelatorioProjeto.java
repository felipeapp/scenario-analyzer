/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/06/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.CalendarioPesquisaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.CalendarioPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;

/**
 * Processador responsável pelo cadastro dos relatórios finais
 * de projetos de pesquisas
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorRelatorioProjeto extends AbstractProcessador {

	/**
	 * Responsável pela execução do processamento dos relatórios de Projeto 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;

		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, mov);
		GenericDAO dao = getGenericDAO(mov);
		Comando comando = mov.getCodMovimento();
		
		// Validações
		validate(mov);
		RelatorioProjeto relatorio = (RelatorioProjeto) movCadastro.getObjMovimentado();

		if ( comando.equals(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO) ) {
			
			relatorio.setDataEnvio(new Date());
			relatorio.setUsuarioCadastro((Usuario) movCadastro.getUsuarioLogado());
			dao.create(relatorio);
			
			
		} else {
		
			if(!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
				// Validar período de submissão de relatórios finais
				CalendarioPesquisa calendario = calendarioDao.findVigente();
				if ( relatorio.getProjetoPesquisa().isInterno() && !calendario.isPeriodoEnvioRelatorioAnualProjeto() ) {
					NegocioException e = new NegocioException();
					e.addErro( "Atenção! O sistema não está aberto para envio de relatórios finais de projetos de pesquisa." );
					throw e;
				}
			}
	
	
			relatorio.setDataEnvio( new Date() );
			relatorio.setUsuarioCadastro( (Usuario) mov.getUsuarioLogado() );
	
			if ( relatorio.getId() == 0) {
				dao.create( relatorio );
			} else {
				dao.update(relatorio);
			}

		}
		
		return relatorio;
	}

	/**
	 * Responsável pela validação que é realizada no processamento dos relatórios de projeto
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		// Validar projetos

		// Validar resumo
	}

}
