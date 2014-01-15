/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 25/06/2007
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;

/**
 * Processador para registrar solicitações de ensino individual feitas por alunos de graduação
 * @author leonardo
 * @author Victor Hugo
 */
public class ProcessadorSolicitacaoEnsinoIndividual extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);
		
		
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class, movimento);
		MovimentoCadastro mov = (MovimentoCadastro) movimento;

		if(mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL)) {
			/** cadastra a solicitação de ensino individual ou férias */
			Integer numSolicitacao = dao.getSequenciaSolicitacoes();
			
			if (isEmpty(numSolicitacao))
				throw new NegocioException("Não foi possível gerar o número da solicitação");
			
			SolicitacaoEnsinoIndividual sol = mov.getObjMovimentado();
			sol.setNumeroSolicitacao(numSolicitacao);
			
			dao.create(sol);

		} else if( mov.getCodMovimento().equals(SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL) ){

			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();

			if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				/** SE o usuário logado for um coordenador de curso a solicitação vai para a situação NEGADA */
				
				if( isEmpty( mov.getColObjMovimentado() ) ){ /** se a coleção for vazia  então o coordenador deseja negar apenas uma solicitação de férias ou ensino individual*/
					solicitacoesCancelar.add(solicitacao);
				}else{
					/** caso a coleção de objetos não seja vazio é porque o coordenador deseja negar todas as solicitações de 
					 * férias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
					 * Neste caso todas as solicitações de férias ou ensino individual com a situação pendentes serão negadas. */
					solicitacoesCancelar = (Collection<SolicitacaoEnsinoIndividual>) mov.getColObjMovimentado();
				}
				
				for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
					sei.setSituacao( SolicitacaoEnsinoIndividual.NEGADA );
				}
					
			} else if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				/** SE o usuário logado for um discente vai para a situação CANCELADA */
				solicitacao.setSituacao( SolicitacaoEnsinoIndividual.CANCELADA );
				solicitacoesCancelar.add(solicitacao);
			} else{
				throw new SegurancaException("Você não está autorizado a realizar esta operação.");
			}
			
			for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
				sei.setDataAtendimento( new Date() );
				sei.setRegistroEntradaAtendente( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(sei);
			}

		} else if( mov.getCodMovimento().equals( SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL ) ){
			
			/** retorna para pendente uma solicitação de ensino individual ou férias que foi negada */
			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			
			solicitacao.setSituacao( SolicitacaoEnsinoIndividual.SOLICITADA );
			solicitacao.setDataAtendimento( null );
			solicitacao.setRegistroEntradaAtendente( null );
			
			dao.update(solicitacao);
			
		} else if( mov.getCodMovimento().equals( SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL_DISCENTE ) ){
			
			/* Quando o aluno fizer qualquer alteração no seu plano de matrícula, e houver uma solicitação de ensino individualizado, 
			 * estas solicitações serão canceladas e o sistema informará o discente destas ações. 
			 * */
			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();

			if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				Collection<Integer> situacoes = new ArrayList<Integer>();
				situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
				situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
				
				solicitacoesCancelar.addAll(dao.findByDiscenteAnoPeriodo(solicitacao.getDiscente().getId(), Turma.ENSINO_INDIVIDUAL, solicitacao.getAno(), solicitacao.getPeriodo(), situacoes));
			
				/** Se o usuário logado for um discente vai para a situação CANCELADA */
				for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
					sei.setSituacao( SolicitacaoEnsinoIndividual.CANCELADA );
					sei.setDataAtendimento( new Date() );
					sei.setRegistroEntradaAtendente( mov.getUsuarioLogado().getRegistroEntrada() );
					dao.update(sei);
				}
				
				if (!solicitacoesCancelar.isEmpty()){
					ListaMensagens info = new ListaMensagens();
					info.addWarning("As solicitações de ensino individualizado foram removidas, devido a alteração do plano de matrícula, favor solicitá-las novamente.");
					mov.setMensagens(info);
				}	
			} 
		}

		return mov;
	}

	/**
	 * valida os movimentos
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) movc.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);

		if(mov.getCodMovimento() == SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL){

			SolicitacaoEnsinoIndividualValidator.validarSolicitacao(solicitacao, erros);

		} else if( mov.getCodMovimento() == SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL ){

			/** quem pode cancelar uma solicitação de ensino individual eh o coordenador do curso ou então o discente que solicitou */
			if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				if( solicitacao.getDiscente().getId() != ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo().getId() )
					throw new NegocioException("Você só pode cancelar as suas solicições de ensino individual.");
			}else{
				checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO}, mov);
			}

			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();
			
			if( isEmpty( movc.getColObjMovimentado() ) ){ /** se a coleção for vazia  então o coordenador deseja negar apenas uma solicitação de férias ou ensino individual*/
				solicitacao = dao.findByPrimaryKey(solicitacao.getId(), SolicitacaoEnsinoIndividual.class);
				solicitacoesCancelar.add(solicitacao);
			}else{
				/** caso a coleção de objetos não seja vazio é porque o coordenador deseja negar todas as solicitações de 
				 * férias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
				 * Neste caso todas as solicitações de férias ou ensino individual com a situação pendentes serão negadas. */
				solicitacoesCancelar = (Collection<SolicitacaoEnsinoIndividual>) movc.getColObjMovimentado();
			}
			
			for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
				if( sei.getSituacao() != SolicitacaoEnsinoIndividual.SOLICITADA )
					erros.addErro("Não é possível cancelar esta(s) solicitação(ões) pois ela(s) já foi(ram) atendida(s) ou cancelada(s).");
			}

		} else if( mov.getCodMovimento().equals( SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL ) ){
			
			checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.SECRETARIA_COORDENACAO}, mov);

			solicitacao = dao.findByPrimaryKey(solicitacao.getId(), SolicitacaoEnsinoIndividual.class);
			if( solicitacao.getSituacao() != SolicitacaoEnsinoIndividual.NEGADA )
				erros.addErro("Só é possível retornar solicitações que estão negadas.");
			
		}

		checkValidation(erros);

	}

}
