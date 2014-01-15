/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 14/11/2012
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
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 *
 * <p> Processador que realiza a inscrição de um participante uma mini ativdade de extensão. </p>
 *
 * <p> <i> A partir desse segunda versão de inscrição o usuário não precisa mais confirma sua inscrição, 
 * porque ele já se cadastrou no sistema anteriormente.
 * 
 * Caso o inscrição exije confirmação do coordenador: <br/>
 *   - Uma inscrição com status INSCRITO é salva no banco para o coordenador validar a inscrição.<br/>
 *   
 *   - Se ouver taxa de inscrição, o coordenador só consegue validar se a confirmação do pagamento tiver sido feita. 
 *   (ou seja, serão duas etapas, confirmar pagamento e confirmar inscrição) <br/>
 *   
 * Caso o inscrição NÃO exije confirmação do coordenador:<br/>
 *    - Se não tiver taxa de cobança, uma inscrição com status APROVADO é salva diretamente do banco e é criado um ParticipanteAcaoExtensao <br/>  
 *   
 *    - Se ouver taxa de inscrição, é salvo como INSCRITO e no momento em que confirma o pagamento dessa taxa o participante 
 *    passa automaticamente APROVADO.(ou seja, existe apenas uma etapas, confirmar pagamento)
 *   
 * </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorInscreveParticipanteMiniAtividadeExtensao extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoInscreveParticipanteMiniCursoEventoExtensao movimento = (MovimentoInscreveParticipanteMiniCursoEventoExtensao) mov;
		
		InscricaoAtividadeParticipante inscricaoParticipante = movimento.getInscricaoParticipante();
		List<InscricaoAtividadeParticipante> inscricoesAnteriores = movimento.getInscricoesParticipanteAnteriores();

		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
		
			ParticipanteAcaoExtensao participante = null;
			
			// configura o status da inscrição 
			if(inscricaoParticipante.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO
					&& ! inscricaoParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula()){
				
				inscricaoParticipante.setStatusInscricao(new StatusInscricaoParticipante(StatusInscricaoParticipante.APROVADO));
				
				participante = new ParticipanteAcaoExtensao();
				participante.setInscricaoAtividadeParticipante(inscricaoParticipante);
				participante.setCadastroParticipante(inscricaoParticipante.getCadastroParticipante());
				
				participante.setSubAtividade(inscricaoParticipante.getInscricaoAtividade().getSubAtividade());
				participante.setTipoParticipacao(new TipoParticipacaoAcaoExtensao(TipoParticipacaoAcaoExtensao.PARTICIPANTE)); // por padrão na inscrição todo mundo é aluno
				participante.setAtivo(true);
				
				
			}else{
				// mesmo sendo preencimento automático se tiver taxa de matrículo fica pendente de confirmação do pagamento para ser aprovado
				inscricaoParticipante.setStatusInscricao(new StatusInscricaoParticipante(StatusInscricaoParticipante.INSCRITO));
			}
			
			
			// configura o status do pagamento, se não cobra taxa fica null
			if(inscricaoParticipante.getInscricaoAtividade().isCobrancaTaxaMatricula()){
				inscricaoParticipante.setStatusPagamento(StatusPagamentoInscricao.EM_ABERTO); 
			}
			
			// salva o arquivo enviado pelo usuário
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
			
			
			// SALVA O QUESTIONÁRIO, DEPOIS DE SALVAS A INSCRIÇÃO, SENÃO DÁ ERRO //
			if (inscricaoParticipante.getQuestionarioRespostas() != null) {
				ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
				processadorQuestionarioRespostas.cadastrarRespostas(mov, inscricaoParticipante.getQuestionarioRespostas());
			}
				
			
			
			/// Caso o usuário realize duas inscrições para o mesmo evento, cancela a anterior///
			if(inscricoesAnteriores != null){
				
				 // Normalmente só tem uma, porque sempre as anteriores são canceladas
				for (InscricaoAtividadeParticipante inscricaoAnterior : inscricoesAnteriores) {
					dao.updateField(InscricaoAtividadeParticipante.class, inscricaoAnterior.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);
					
					
					// Se está aprovada, obrigatoriamente tem participante, então para cancelar, remove esse participante //
					if(inscricaoAnterior.getStatusInscricao().isStatusAprovado() && inscricaoAnterior.getParticipanteExtensao() != null){
						dao.updateField(ParticipanteAcaoExtensao.class, inscricaoAnterior.getParticipanteExtensao().getId(), "ativo", false);
					}
				}
			}
		
		}finally{	
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoInscreveParticipanteMiniCursoEventoExtensao movimento = (MovimentoInscreveParticipanteMiniCursoEventoExtensao) mov;
		
		InscricaoAtividadeParticipante inscricaoParticipante = movimento.getInscricaoParticipante();
		
		
		erros.addAll(inscricaoParticipante.validate());
		
		if(inscricaoParticipante.getQuestionarioRespostas() != null)
			erros.addAll(inscricaoParticipante.getQuestionarioRespostas().validate());
		
		
		checkValidation(erros);
	
		// Regra muito importante para mini atividades, verifica se o usuário se inscreveu na atividade pai //
		
		InscricaoParticipanteAtividadeExtensaoDao dao = null;
		ParticipanteAcaoExtensaoDao daoParticipante = null;
		
		try{
			dao = getDAO(InscricaoParticipanteAtividadeExtensaoDao.class, movimento);
			daoParticipante = getDAO(ParticipanteAcaoExtensaoDao.class, movimento);
			
			List<InscricaoAtividadeParticipante> inscricoesAtividade = dao.findInscricoesParticipanteAtividade(
					inscricaoParticipante.getInscricaoAtividade().getSubAtividade().getAtividade().getId()
					, inscricaoParticipante.getCadastroParticipante().getId()
					, StatusInscricaoParticipante.getStatusAtivos());
			
			if(inscricoesAtividade.size() == 0){
				erros.addErro(" Para realizara inscrição em uma mini atividade é preciso primeiro realizar a inscrição na atividade principal.");
			}
			
			// Se for preenchimento automático já cria automaticamente o participante, então precisa validar aqui //
			int qtdMiniAtividade  = daoParticipante.countQtdParticipanteNaMiniAtividade(inscricaoParticipante.getCadastroParticipante().getId(), inscricaoParticipante.getInscricaoAtividade().getSubAtividade().getId());
			
			if(qtdMiniAtividade > 0){
				erros.addErro(" Caro "+inscricaoParticipante.getCadastroParticipante().getNome()+" o senhor(a) já está incluído nessa mini atividade de extensão, não é mais possível nem necessário realizar uma nova inscrição.");
			}
		
		}finally{
			if (dao != null) dao.close();
			if (daoParticipante != null) daoParticipante.close();
			
			checkValidation(erros);
		}
		
		
		
		if(! inscricaoParticipante.getInscricaoAtividade().isAberta()){
			erros.addErro(" A inscrição para essa encontra-se encerrada.");
		}
		
		
		/* Verificar se o número de vagas foi ultrapassado apenas no caso do preencimento ser automático
		 * 
		 * Mesmo que hava taxa de matrícula faz a verifiação, porque no momento da confirmação do pagamento 
		 * passsa automaticamente para aprovado, então não pode deixar passar
		 *
		 * caso contrario a verificação vai ser no momento do coordenador aprovar.
		 */
		
		if(inscricaoParticipante.getInscricaoAtividade().getMetodoPreenchimento() == InscricaoAtividade.PREENCHIMENTO_AUTOMATICO ){
			if( inscricaoParticipante.getInscricaoAtividade().getQuantidadeVagasRestantes() < 1 ){
				erros.addErro(" Essa mini atividade encontra-se com o número de vagas esgotado.");
			}
		}

		checkValidation(erros);
		
	}
	
}
