/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/11/2010
 */
package br.ufrn.sigaa.estagio.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.estagio.RelatorioEstagioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.RelatorioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRelatorioEstagio;
import br.ufrn.sigaa.estagio.dominio.StatusRenovacaoEstagio;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 * Processador responsável pela a persistência dos dados relacionados 
 * ao Relatório de Estágios.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorRelatorioEstagio extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		RelatorioEstagio relatorio = ((MovimentoCadastro)mov).getObjMovimentado();
		RelatorioEstagioDao dao = getDAO(RelatorioEstagioDao.class, mov);
		try {						
			
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RELATORIO_ESTAGIO)){
				validate(mov);
				
				// Salva as respostas do questionário
				ProcessadorQuestionarioRespostas proc = new ProcessadorQuestionarioRespostas();
				proc.cadastrarRespostas(mov, relatorio.getRelatorioRespostas().getQuestionarioRespostas());
				
				relatorio.getRelatorioRespostas().setRelatorioEstagio(relatorio);
				
				relatorio.setStatus(new StatusRelatorioEstagio(StatusRelatorioEstagio.PENDENTE));

				// salva o relatório
				dao.create(relatorio);
				
			} else if (mov.getCodMovimento().equals(SigaaListaComando.APROVAR_RELATORIO_ESTAGIO)){
				
				if (relatorio.getRenovacaoEstagio() != null){										

					boolean discenteOK = false;
					boolean supervisorOK = false;
					boolean orientadorOK = false;
					
					relatorio = getGenericDAO(mov).refresh(relatorio);										
					
					/* Verifica os relatórios já preenchidos para a Renovação e identificando quem preencheu e está validado */
					Collection<RelatorioEstagio> relatorios = dao.findRelatorioByEstagio(relatorio.getEstagio(), null, 
							relatorio.getRenovacaoEstagio(), null,true);				
					for (RelatorioEstagio re : relatorios){
						if (re.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getDiscente().getPessoa())) 
							discenteOK = true;
						if (re.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getOrientador().getPessoa()))
							orientadorOK = true;
						if (re.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getSupervisor())) 
							supervisorOK = true;					
					}
					
					/* Identifica de quem é o relatório que está sendo validado */
					if (relatorio.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getDiscente().getPessoa())) 
						discenteOK = true;
					if (relatorio.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getSupervisor())) 
						supervisorOK = true;					
					if (relatorio.getRegistroCadastro().getUsuario().getPessoa().equals(relatorio.getEstagio().getOrientador().getPessoa()))
						orientadorOK = true;
					
					// caso todos os envolvidos preencher o relatório, a renovação será aprovada.
					if (discenteOK && supervisorOK && orientadorOK){
					
						relatorio.getRenovacaoEstagio().setStatus(new StatusRenovacaoEstagio(StatusRenovacaoEstagio.APROVADO));

						// atribui a nova data de fim do estágio 
						dao.updateField(Estagiario.class, relatorio.getEstagio().getId(), "dataFim", relatorio.getRenovacaoEstagio().getDataRenovacao());
						
					}
					
				}
				
				relatorio.setDataAprovacao(new Date());
				
				relatorio.setRegistroAprovacao(mov.getUsuarioLogado().getRegistroEntrada());
				
				relatorio.setStatus(new StatusRelatorioEstagio(StatusRelatorioEstagio.APROVADO));
				
				dao.update(relatorio);
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return relatorio;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		RelatorioEstagio relatorio = ((MovimentoCadastro)mov).getObjMovimentado();
		checkValidation( relatorio.getRelatorioRespostas().getQuestionarioRespostas().validate() );			
	}

}
