/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Processador respons�vel pelo cadastro dos relat�rios finais
 * de projetos de pesquisas
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorRelatorioProjeto extends AbstractProcessador {

	/**
	 * Respons�vel pela execu��o do processamento dos relat�rios de Projeto 
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro movCadastro = (MovimentoCadastro) mov;

		CalendarioPesquisaDao calendarioDao = getDAO(CalendarioPesquisaDao.class, mov);
		GenericDAO dao = getGenericDAO(mov);
		Comando comando = mov.getCodMovimento();
		
		// Valida��es
		validate(mov);
		RelatorioProjeto relatorio = (RelatorioProjeto) movCadastro.getObjMovimentado();

		if ( comando.equals(SigaaListaComando.ENVIAR_RELATORIO_FINAL_PROJETO) ) {
			
			relatorio.setDataEnvio(new Date());
			relatorio.setUsuarioCadastro((Usuario) movCadastro.getUsuarioLogado());
			dao.create(relatorio);
			
			
		} else {
		
			if(!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.GESTOR_PESQUISA)){
				// Validar per�odo de submiss�o de relat�rios finais
				CalendarioPesquisa calendario = calendarioDao.findVigente();
				if ( relatorio.getProjetoPesquisa().isInterno() && !calendario.isPeriodoEnvioRelatorioAnualProjeto() ) {
					NegocioException e = new NegocioException();
					e.addErro( "Aten��o! O sistema n�o est� aberto para envio de relat�rios finais de projetos de pesquisa." );
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
	 * Respons�vel pela valida��o que � realizada no processamento dos relat�rios de projeto
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		// Validar projetos

		// Validar resumo
	}

}
