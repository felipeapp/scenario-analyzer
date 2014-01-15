/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 31/10/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 * <p> Processador que realiza a inscri��o de um participante uma ativdade de extens�o. </p>
 *
 * <p> <i> A partir desse segunda vers�o de inscri��o o usu�rio n�o precisa mais confirma sua inscri��o, 
 * porque ele j� se cadastrou no sistema anteriormente.
 * 
 * Caso a inscri��o exija confirma��o do coordenador: <br/>
 *   - Uma inscri��o com status INSCRITO � salva no banco para o coordenador validar a inscri��o.<br/>
 *   
 * Caso a inscri��o N�O exija confirma��o do coordenador:<br/>
 *    - Se tiver taxa de coban�a, funciona igual a inscri��o com confirma��o. <br/>  
 *   
 *    - Se n�o ouver taxa de inscri��o, � salvo diretamente como APROVADO e um participante de extens�o � criado para a atividade.
 *   
 * </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorInscreveParticipanteAtividadeExtensao extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoInscreveParticipanteCursoEventoExtensao movimento = (MovimentoInscreveParticipanteCursoEventoExtensao) mov;
		
		InscricaoAtividadeParticipante inscricaoParticipante = movimento.getInscricaoParticipante();
		List<InscricaoAtividadeParticipante> inscricoesAnteriores = movimento.getInscricoesParticipanteAnteriores();
		List<InscricaoAtividadeParticipante> inscricoesAnterioresSubAtividade = movimento.getInscricoesParticipanteAnterioresSubAtividade();
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
		
			
			ParticipanteAcaoExtensao participante = null;
			
			// configura o status da inscri��o 
			if(inscricaoParticipante.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO
					&& ! inscricaoParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula()){
				
				
				inscricaoParticipante.setStatusInscricao(new StatusInscricaoParticipante(StatusInscricaoParticipante.APROVADO));
				
				participante = new ParticipanteAcaoExtensao();
				participante.setInscricaoAtividadeParticipante(inscricaoParticipante);
				participante.setCadastroParticipante(inscricaoParticipante.getCadastroParticipante());
				
				participante.setAtividadeExtensao(inscricaoParticipante.getInscricaoAtividade().getAtividade());
				participante.setTipoParticipacao(new TipoParticipacaoAcaoExtensao(TipoParticipacaoAcaoExtensao.PARTICIPANTE)); // por padr�o na inscri��o todo mundo � aluno
				participante.setAtivo(true);
				
				
				
			}else{
				// mesmo sendo preencimento autom�tico se tiver taxa de matr�culo fica pendente de confirma��o do pagamento para ser aprovado
				inscricaoParticipante.setStatusInscricao(new StatusInscricaoParticipante(StatusInscricaoParticipante.INSCRITO));
			}
			
			
			// configura o status do pagamento, se n�o cobra taxa fica null
			if(inscricaoParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula()){
				inscricaoParticipante.setStatusPagamento(StatusPagamentoInscricao.EM_ABERTO); 
			}
			
			// salva o arquivo enviado pelo usu�rio
			if (inscricaoParticipante.getFile() != null) {
	    		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
	    		try {
					EnvioArquivoHelper.inserirArquivo(idArquivo, inscricaoParticipante.getFile().getBytes(), inscricaoParticipante.getFile().getContentType(), inscricaoParticipante.getFile().getName());
					inscricaoParticipante.setIdArquivo(idArquivo);
				} catch (IOException e) {
					throw new NegocioException("Erro ao anexar arquivo.");
				}
			}
				
		
			
			dao.create(inscricaoParticipante);
			
			if(participante != null){
				dao.create(participante);
				dao.updateField(InscricaoAtividadeParticipante.class, inscricaoParticipante.getId(), "participanteExtensao.id", participante.getId());
			}
			
			
			// SALVA O QUESTION�RIO, DEPOIS DE SALVAS A INSCRI��O, SEN�O D� ERRO //
			if (inscricaoParticipante.getQuestionarioRespostas() != null) {
				ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
				processadorQuestionarioRespostas.cadastrarRespostas(mov, inscricaoParticipante.getQuestionarioRespostas());
			}
					
			
			/// Caso o usu�rio realize duas inscri��es para o mesmo evento, cancela a anterior///
			if(inscricoesAnteriores != null){
				
				 // Normalmente s� tem uma, porque sempre as anteriores s�o canceladas
				for (InscricaoAtividadeParticipante inscricaoAnterior : inscricoesAnteriores) {
					dao.updateField(InscricaoAtividadeParticipante.class, inscricaoAnterior.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);
					
					// Se est� aprovada, obrigatoriamente tem participante, ent�o para cancelar, remove essa participante //
					if(inscricaoAnterior.getStatusInscricao().isStatusAprovado() && inscricaoAnterior.getParticipanteExtensao() != null){
						dao.updateField(ParticipanteAcaoExtensao.class, inscricaoAnterior.getParticipanteExtensao().getId(), "ativo", false);
					}
					
				}
			}
			
			/// Caso o usu�rio realize duas inscri��es para o mesmo evento, cancela as anteriores nas mini atividades, vai ter fazer de novo ///
			if(inscricoesAnterioresSubAtividade != null){
				
				 // Normalmente s� tem uma, porque sempre as anteriores s�o canceladas
				for (InscricaoAtividadeParticipante inscricaoAnteriorSubAtividade : inscricoesAnterioresSubAtividade) {
					dao.updateField(InscricaoAtividadeParticipante.class, inscricaoAnteriorSubAtividade.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);
					
					// Se est� aprovada, obrigatoriamente tem participante, ent�o para cancelar, remove esse participante //
					if(inscricaoAnteriorSubAtividade.getStatusInscricao().isStatusAprovado() && inscricaoAnteriorSubAtividade.getParticipanteExtensao() != null){
						dao.updateField(ParticipanteAcaoExtensao.class, inscricaoAnteriorSubAtividade.getParticipanteExtensao().getId(), "ativo", false);
					}
					
				}
			}
		
		}finally{	
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoInscreveParticipanteCursoEventoExtensao movimento = (MovimentoInscreveParticipanteCursoEventoExtensao) mov;
		
		InscricaoAtividadeParticipante inscricaoParticipante = movimento.getInscricaoParticipante();
		
		
		ParticipanteAcaoExtensaoDao dao = null;
		
		try{
			

			dao = getDAO(ParticipanteAcaoExtensaoDao.class, movimento);
		
			erros.addAll(inscricaoParticipante.validate());
			
			if(inscricaoParticipante.getQuestionarioRespostas() != null)
				erros.addAll(inscricaoParticipante.getQuestionarioRespostas().validate());
			
			
			checkValidation(erros);
			
			
			if(! inscricaoParticipante.getInscricaoAtividade().isAberta()){
				erros.addErro(" A inscri��o para essa encontra-se encerrada.");
			}
			
			
			/* Verificar se o n�mero de vagas foi ultrapassado apenas no caso do preencimento ser autom�tico
			 * 
			 * Mesmo que hava taxa de matr�cula faz a verifia��o, porque no momento da confirma��o do pagamento 
			 * passsa automaticamente para aprovado, ent�o n�o pode deixar passar
			 *
			 * caso contrario a verifica��o vai ser no momento do coordenador aprovar.
			 */
			
			if(inscricaoParticipante.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
				if( inscricaoParticipante.getInscricaoAtividade().getQuantidadeVagasRestantes() < 1 ){
					erros.addErro(" Esse curso ou evento encontra-se com o n�mero de vagas esgotado.");
				}
			}
			
			
			// Se for preenchimento autom�tico j� cria automaticamente o participante, ent�o precisa validar aqui //
			
			int qtdAtividade  = dao.countQtdParticipanteAtivoNaAtividade(inscricaoParticipante.getCadastroParticipante().getId(), inscricaoParticipante.getInscricaoAtividade().getAtividade().getId());
			
			if(qtdAtividade > 0){
				erros.addErro(" Caro "+inscricaoParticipante.getCadastroParticipante().getNome()+" o senhor(a) j� est� inclu�do nessa atividade de extens�o, n�o � mais poss�vel nem necess�rio realizar uma nova inscri��o.");
			}
			

		}finally{
			if(dao != null) dao.close();
			
			checkValidation(erros);
		}
		
		
	}

}
