/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
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
 * Processador para registrar solicita��es de ensino individual feitas por alunos de gradua��o
 * @author leonardo
 * @author Victor Hugo
 */
public class ProcessadorSolicitacaoEnsinoIndividual extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);
		
		
		SolicitacaoEnsinoIndividualDao dao = getDAO(SolicitacaoEnsinoIndividualDao.class, movimento);
		MovimentoCadastro mov = (MovimentoCadastro) movimento;

		if(mov.getCodMovimento().equals(SigaaListaComando.SOLICITAR_ENSINO_INDIVIDUAL)) {
			/** cadastra a solicita��o de ensino individual ou f�rias */
			Integer numSolicitacao = dao.getSequenciaSolicitacoes();
			
			if (isEmpty(numSolicitacao))
				throw new NegocioException("N�o foi poss�vel gerar o n�mero da solicita��o");
			
			SolicitacaoEnsinoIndividual sol = mov.getObjMovimentado();
			sol.setNumeroSolicitacao(numSolicitacao);
			
			dao.create(sol);

		} else if( mov.getCodMovimento().equals(SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL) ){

			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();

			if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO ) ){
				/** SE o usu�rio logado for um coordenador de curso a solicita��o vai para a situa��o NEGADA */
				
				if( isEmpty( mov.getColObjMovimentado() ) ){ /** se a cole��o for vazia  ent�o o coordenador deseja negar apenas uma solicita��o de f�rias ou ensino individual*/
					solicitacoesCancelar.add(solicitacao);
				}else{
					/** caso a cole��o de objetos n�o seja vazio � porque o coordenador deseja negar todas as solicita��es de 
					 * f�rias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
					 * Neste caso todas as solicita��es de f�rias ou ensino individual com a situa��o pendentes ser�o negadas. */
					solicitacoesCancelar = (Collection<SolicitacaoEnsinoIndividual>) mov.getColObjMovimentado();
				}
				
				for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
					sei.setSituacao( SolicitacaoEnsinoIndividual.NEGADA );
				}
					
			} else if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				/** SE o usu�rio logado for um discente vai para a situa��o CANCELADA */
				solicitacao.setSituacao( SolicitacaoEnsinoIndividual.CANCELADA );
				solicitacoesCancelar.add(solicitacao);
			} else{
				throw new SegurancaException("Voc� n�o est� autorizado a realizar esta opera��o.");
			}
			
			for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
				sei.setDataAtendimento( new Date() );
				sei.setRegistroEntradaAtendente( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(sei);
			}

		} else if( mov.getCodMovimento().equals( SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL ) ){
			
			/** retorna para pendente uma solicita��o de ensino individual ou f�rias que foi negada */
			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			
			solicitacao.setSituacao( SolicitacaoEnsinoIndividual.SOLICITADA );
			solicitacao.setDataAtendimento( null );
			solicitacao.setRegistroEntradaAtendente( null );
			
			dao.update(solicitacao);
			
		} else if( mov.getCodMovimento().equals( SigaaListaComando.CANCELAR_SOLICITACAO_ENSINO_INDIVIDUAL_DISCENTE ) ){
			
			/* Quando o aluno fizer qualquer altera��o no seu plano de matr�cula, e houver uma solicita��o de ensino individualizado, 
			 * estas solicita��es ser�o canceladas e o sistema informar� o discente destas a��es. 
			 * */
			SolicitacaoEnsinoIndividual solicitacao = (SolicitacaoEnsinoIndividual) mov.getObjMovimentado();
			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();

			if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				Collection<Integer> situacoes = new ArrayList<Integer>();
				situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
				situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
				
				solicitacoesCancelar.addAll(dao.findByDiscenteAnoPeriodo(solicitacao.getDiscente().getId(), Turma.ENSINO_INDIVIDUAL, solicitacao.getAno(), solicitacao.getPeriodo(), situacoes));
			
				/** Se o usu�rio logado for um discente vai para a situa��o CANCELADA */
				for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
					sei.setSituacao( SolicitacaoEnsinoIndividual.CANCELADA );
					sei.setDataAtendimento( new Date() );
					sei.setRegistroEntradaAtendente( mov.getUsuarioLogado().getRegistroEntrada() );
					dao.update(sei);
				}
				
				if (!solicitacoesCancelar.isEmpty()){
					ListaMensagens info = new ListaMensagens();
					info.addWarning("As solicita��es de ensino individualizado foram removidas, devido a altera��o do plano de matr�cula, favor solicit�-las novamente.");
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

			/** quem pode cancelar uma solicita��o de ensino individual eh o coordenador do curso ou ent�o o discente que solicitou */
			if( ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo() != null ){
				if( solicitacao.getDiscente().getId() != ( (Usuario) mov.getUsuarioLogado() ).getDiscenteAtivo().getId() )
					throw new NegocioException("Voc� s� pode cancelar as suas solici��es de ensino individual.");
			}else{
				checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO}, mov);
			}

			Collection<SolicitacaoEnsinoIndividual> solicitacoesCancelar = new ArrayList<SolicitacaoEnsinoIndividual>();
			
			if( isEmpty( movc.getColObjMovimentado() ) ){ /** se a cole��o for vazia  ent�o o coordenador deseja negar apenas uma solicita��o de f�rias ou ensino individual*/
				solicitacao = dao.findByPrimaryKey(solicitacao.getId(), SolicitacaoEnsinoIndividual.class);
				solicitacoesCancelar.add(solicitacao);
			}else{
				/** caso a cole��o de objetos n�o seja vazio � porque o coordenador deseja negar todas as solicita��es de 
				 * f�rias ou ensino individual de um componente simultaneamente. Entrando com a mesma justificativa para todas.
				 * Neste caso todas as solicita��es de f�rias ou ensino individual com a situa��o pendentes ser�o negadas. */
				solicitacoesCancelar = (Collection<SolicitacaoEnsinoIndividual>) movc.getColObjMovimentado();
			}
			
			for(SolicitacaoEnsinoIndividual sei : solicitacoesCancelar){
				if( sei.getSituacao() != SolicitacaoEnsinoIndividual.SOLICITADA )
					erros.addErro("N�o � poss�vel cancelar esta(s) solicita��o(�es) pois ela(s) j� foi(ram) atendida(s) ou cancelada(s).");
			}

		} else if( mov.getCodMovimento().equals( SigaaListaComando.RETORNAR_SOLICITACAO_ENSINO_INDIVIDUAL ) ){
			
			checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO,SigaaPapeis.SECRETARIA_COORDENACAO}, mov);

			solicitacao = dao.findByPrimaryKey(solicitacao.getId(), SolicitacaoEnsinoIndividual.class);
			if( solicitacao.getSituacao() != SolicitacaoEnsinoIndividual.NEGADA )
				erros.addErro("S� � poss�vel retornar solicita��es que est�o negadas.");
			
		}

		checkValidation(erros);

	}

}
