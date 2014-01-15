/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/10/2009
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;

/**
 * Processador para realizar o cadastro e gerenciamento das inscrições realizadas pelo portal público e 
 * o gerenciamento das inscrições pelo coordenador através do portal docente.
 * 
 * @author Daniel Augusto
 *
 * 
 * @Deprecated código impossível de se manter. Esse código é um bom exemplo de como não se deve programar!
 */
@Deprecated
public class ProcessadorInscricaoParticipante extends AbstractProcessador {

//	/** representa parte da mensagem de submissão com sucesso para o usuário. */
//	private static final String SUBMETIDA_SUCESSO = "submetida com sucesso!";
//	
//	/** representa parte da mensagem  enviadas para o usuário informando da aprovação de sua participação. */
//	private static final String ACEITA_COORDENADOR = "aceita pelo coordenador da ação.";
//	
//	/** representa parte da mensagem  enviadas para o usuário informando da rejeição de sua participação*/
//	private static final String RECUSADA_COORDENADOR = "recusada pelo coordenador da ação.";
//	
//	/**
//	 * Recebe o movimento e executa o comando que foi definido no MBean.
//	 * 
//	 * @return Object
//	 * @throws NegocioException, ArqException, RemoteException
//	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
//
//		final String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
//		
//		MovimentoCadastro mov = (MovimentoCadastro) movimento;
//		GenericDAO dao = getGenericDAO(mov);
//		InscricaoAtividadeExtensaoDao daoInscricao = null;
//		
//		Collection<InscricaoAtividadeParticipante> selecionados = new ArrayList<InscricaoAtividadeParticipante>();
//		
//		try{
//		
//		if (SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO.equals(mov.getCodMovimento())) {
//
//			validate(mov);
//			InscricaoAtividadeParticipante participante = mov.getObjMovimentado();
//			participante = getGenericDAO(mov).findByPrimaryKey(participante.getId(), InscricaoAtividadeParticipante.class);
//
//			participante.setCodigoAcesso(participante.getCodigoHash());
//			participante.setSenha(UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria()).substring(0, 10));
//			dao.updateFields(InscricaoAtividadeParticipante.class, participante.getId(), new String[] {"codigoAcesso", "senha"},
//					new Object[] {participante.getCodigoAcesso(), participante.getSenha()});
//
//			StringBuffer mensagem = new StringBuffer();
//			
//			if(isInscricaoEmAtividade(participante)) {
//				mensagem.append(" Esta ação inicia em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getAtividade().getDataInicio()));
//				mensagem.append(" e finaliza em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getAtividade().getDataFim()));
//			} else {
//				mensagem.append(" Esta mini ação inicia em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getSubAtividade().getInicio()));
//				mensagem.append(" e finaliza em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getSubAtividade().getFim()));
//			}
//			
//			
//			
//			
//			if (StatusInscricaoParticipante.INSCRITO.equals(participante.getStatusInscricao().getId())) {
//			    mensagem.append(". Para confirmar sua solicitação acesse o endereço abaixo e informe a senha recebida.\n\n ");
//			    mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			    mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//			    mensagem.append(participante.getCodigoAcesso() + "/");
//			    mensagem.append(participante.getInscricaoAtividade().getId());
//			    mensagem.append("\n\nSenha: ");
//			    mensagem.append(participante.getSenha());
//			    mensagem.append("\n\nATENÇÃO: Sua inscrição está pendente de aprovação pelo coordenador da ação, ");
//			    mensagem.append("acompanhe o andamento através da área de inscritos pelo portal público do "+siglaSigaa+".");
//			    
//			} else if (StatusInscricaoParticipante.APROVADO.equals(participante.getStatusInscricao().getId()) ||
//				StatusInscricaoParticipante.CONFIRMADO.equals(participante.getStatusInscricao().getId())) {
//			    mensagem.append("\n\nSua senha e código de acesso foram alterados com sucesso.\n\n");
//			    mensagem.append("\n\nParticipe desta ação através da área de inscritos pelo portal público do "+siglaSigaa+" informando o e-mail e senha.\n\n");
//			    mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			    mensagem.append("/sigaa/link/public/extensao/acessarAreaInscrito \n\n");
//			    mensagem.append("\n\nE-mail: ");
//			    mensagem.append(participante.getEmail());
//			    mensagem.append("\n\nSenha: ");
//			    mensagem.append(participante.getSenha());
//			} else {
//			    mensagem.append("\n\nNão foram encontradas inscrições válidas para o usuário informado.\n\n");
//			    mensagem.append("\n\nPor favor, realize uma nova inscrição através do portal público do "+siglaSigaa+".\n\n");
//			}
//
//			enviarMensagemInscrito(participante, "Submetida", SUBMETIDA_SUCESSO, mensagem.toString());
//		}
//
//		
//		
//		if (SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//
//		    	/**
//		    	 * @negocio: Não há subdivisão de participantes por inscrição on-line aberta.
//		    	 * Todos os participantes são ligados diretamente a uma ação de extensão.
//		    	 * Ou seja, um participante cadastrado através da inscrição que ficou aberta de 01 a 05 de junho
//		    	 * está vinculado a ação de extensão da mesma forma que um participante que fez a inscrição através da inscrição
//		    	 * que ficou aberta de 06 a 10 de junho.
//		    	 * A gestão de mini-cursos ou sub-eventos ainda não está implementada. 		
//		    	 */
//		    
//			validate(mov);
//			InscricaoAtividadeParticipante participante = mov.getObjMovimentado();
//			InscricaoAtividadeParticipante inscricaoAnterior = (InscricaoAtividadeParticipante) mov.getObjAuxiliar();
//			verificarEmailInformado(mov);
//			
//			//Cancelando inscrição anterior
//			if (inscricaoAnterior != null && inscricaoAnterior.getInscricaoAtividade().getId() == participante.getInscricaoAtividade().getId()) {
//				dao.updateField(InscricaoAtividadeParticipante.class, inscricaoAnterior.getId(), "statusInscricao",	new StatusInscricaoParticipante(StatusInscricaoParticipante.CANCELADO));
//			}
//
//			if (participante.getFile() != null) {
//	    		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
//	    		try {
//					EnvioArquivoHelper.inserirArquivo(idArquivo, participante.getFile().getBytes(), participante.getFile().getContentType(), participante.getFile().getName());
//					participante.setIdArquivo(idArquivo);
//				} catch (IOException e) {
//					throw new NegocioException("Erro ao anexar arquivo.");
//				}
//			}
//
//			daoInscricao = getDAO(InscricaoAtividadeExtensaoDao.class, mov);
//			CursoEventoExtensao cursoEEvento = daoInscricao.findCursoEventoExtensaoByInscricaoAtividade(participante.getInscricaoAtividade().getId());
//			
//			/* ********************************************************************************
//			 * Se o curso exige pagamento, cria com status do pagamento EM ABERTO
//			 * Significa que não foi pago ainda. Caso a atividade não possua taxa de matrícula
//			 * Esse campo ficará nulo no banco.
//			 * ********************************************************************************/
//			
//			if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//				participante.setStatusPagamento(StatusPagamentoInscricao.EM_ABERTO); 
//			}	
//				
//			dao.create(participante);
//			participante.setCodigoAcesso(participante.getCodigoHash());
//			participante.setSenha(UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria()).substring(0, 10));
//			dao.updateFields(InscricaoAtividadeParticipante.class, participante.getId(), new String[] {"codigoAcesso", "senha"},
//					new Object[] {participante.getCodigoAcesso(), participante.getSenha()});
//			
//			
//			// Caso um questionário personalizado para o processo seletivo 
//			// tenha sido respondido, persistir respostas informadas
//			if (participante.getQuestionarioRespostas() != null) {
//				ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
//				processadorQuestionarioRespostas.cadastrarRespostas(mov, participante.getQuestionarioRespostas());
//			}
//			
//			
//			StringBuffer mensagem = new StringBuffer();
//			mensagem.append(" Esta ação inicia em ");
//			mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//					.getAtividade().getDataInicio()));
//			mensagem.append(" e finaliza em ");
//			mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//					.getAtividade().getDataFim()));
//			mensagem.append(". Para confirmar sua solicitação acesse o endereço abaixo e informe a senha recebida.\n\n ");
//			mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//			mensagem.append(participante.getCodigoAcesso() + "/");
//			mensagem.append(participante.getInscricaoAtividade().getId());
//			mensagem.append("\n\nSenha: ");
//			mensagem.append(participante.getSenha());
//			
//			mensagem.append("\n\nATENÇÃO: \n\tSua inscrição está pendente de aprovação pelo coordenador da ação, ");
//			mensagem.append("acompanhe o andamento através da área de inscritos pelo portal público do "+siglaSigaa+".");
//
//			/*
//			 * Avisa ao usuário que o curso exige cobrança de taxa e que ele deve emitir a GRU na área de acompanhamento de cursos e eventos extensão 
//			 */
//		
//			if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//				mensagem.append("\n\n\tEssa atividade exige a cobrança de uma taxa de matrícula de "+cursoEEvento.getTaxaMatriculaFormatada()+".");
//				mensagem.append(" Na área de inscritos existe um link para a emissão da GRU para efitivar o pagamento. A aprovação do coordenador irá depender da confirmação desse pagamento. ");
//			}
//			
//			
//			enviarMensagemInscrito(participante, "Submetida", SUBMETIDA_SUCESSO, mensagem.toString());
//			
//			
//		
//			
//		}else if (SigaaListaComando.ALTERAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//			
//			validate(mov);
//			
//			// Atualiza apenas os campos permitidos da inscrição //
//			// O nome, endereço, data de nascimento e email.
//			InscricaoAtividadeParticipante participante = mov.getObjMovimentado();
//			dao.updateFields(InscricaoAtividadeParticipante.class, participante.getId()
//					, new String[]{"nome", "dataNascimento", "instituicao", "logradouro", "numero", "bairro", "municipio.id", "unidadeFederativa.id", "cep", "email"}
//					, new Object[]{participante.getNome(), participante.getDataNascimento(), participante.getInstituicao(), participante.getLogradouro()
//				                   , participante.getNumero(), participante.getBairro(), participante.getMunicipio().getId()
//				                   , participante.getUnidadeFederativa().getId(), participante.getCep(), participante.getEmail()});
//			
//			
//		
//			
//		
//		} else if (SigaaListaComando.CADASTRAR_INSCRICAO_PARTICIPANTE_SUB_ATIVIDADE.equals(mov.getCodMovimento())) {			
//		 	
//	    
//		validate(mov);
//		InscricaoAtividadeParticipante participante = mov.getObjMovimentado();
//		InscricaoAtividadeParticipante inscricaoAnterior = (InscricaoAtividadeParticipante) mov.getObjAuxiliar();
//		
//		//Cancelando inscrição anterior
//		if (inscricaoAnterior != null && inscricaoAnterior.getInscricaoAtividade().getId() == participante.getInscricaoAtividade().getId()) {
//			dao.updateField(InscricaoAtividadeParticipante.class, inscricaoAnterior.getId(), "statusInscricao",	new StatusInscricaoParticipante(StatusInscricaoParticipante.CANCELADO));
//		}
//
//		if (participante.getFile() != null) {
//    		int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
//    		try {
//				EnvioArquivoHelper.inserirArquivo(idArquivo, participante.getFile().getBytes(), participante.getFile().getContentType(), participante.getFile().getName());
//				participante.setIdArquivo(idArquivo);
//			} catch (IOException e) {
//				throw new NegocioException("Erro ao anexar arquivo.");
//			}
//		}
//
//		
//		CursoEventoExtensao cursoEEvento = participante.getInscricaoAtividade().getSubAtividade().getAtividade().getCursoEventoExtensao();
//		
//		if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//			participante.setStatusPagamento(StatusPagamentoInscricao.EM_ABERTO); 
//		}	
//			
//		dao.create(participante);
//		participante.setCodigoAcesso(participante.getCodigoHash());
//		participante.setSenha(UFRNUtils.toMD5(UFRNUtils.geraSenhaAleatoria()).substring(0, 10));
//		dao.updateFields(InscricaoAtividadeParticipante.class, participante.getId(), new String[] {"codigoAcesso", "senha"},
//				new Object[] {participante.getCodigoAcesso(), participante.getSenha()});
//		
//		
//		StringBuffer mensagem = new StringBuffer();
//		mensagem.append(" Esta mini ação inicia em ");
//		mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//				.getSubAtividade().getInicio()));
//		mensagem.append(" e finaliza em ");
//		mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//				.getSubAtividade().getFim()));
//		mensagem.append(". Para confirmar sua solicitação acesse o endereço abaixo e informe a senha recebida.\n\n ");
//		mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//		mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//		mensagem.append(participante.getCodigoAcesso() + "/");
//		mensagem.append(participante.getInscricaoAtividade().getId());
//		mensagem.append("\n\nSenha: ");
//		mensagem.append(participante.getSenha());
//		
//		mensagem.append("\n\nATENÇÃO: \n\tSua inscrição está pendente de aprovação pelo coordenador da ação, ");
//		mensagem.append("acompanhe o andamento através da área de inscritos pelo portal público do "+siglaSigaa+".");
//
//		/*
//		 * Avisa ao usuário que o curso exige cobrança de taxa e que ele deve emitir a GRU na área de acompanhamento de cursos e eventos extensão 
//		 */
//	
//		if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//			mensagem.append("\n\n\tEssa atividade exige a cobrança de uma taxa de matrícula de "+cursoEEvento.getTaxaMatriculaFormatada()+".");
//			mensagem.append(" Na área de inscritos existe um link para a emissão da GRU para efitivar o pagamento. A aprovação do coordenador irá depender da confirmação desse pagamento. ");
//		}
//		
//		
//		
//		
//		StringBuffer msg = new StringBuffer();
//		msg.append("Prezado(a) ");
//		msg.append(participante.getNome());
//		msg.append(", \n\n");
//		msg.append("\tSua solicitação de inscrição para participação na Mini Ação \"");
//		msg.append(participante.getInscricaoAtividade().getSubAtividade().getTitulo().toUpperCase());
//		msg.append("\" foi Submetida com sucesso!");		
//
//		MailBody mail = new MailBody();
//		mail.setContentType(MailBody.TEXT_PLAN);
//		mail.setAssunto("Inscrição  Submetida - MENSAGEM AUTOMÁTICA");
//		mail.setMensagem(mensagem.toString());
//		mail.setEmail(participante.getEmail());
//		mail.setNome(participante.getNome());
//		Mail.send(mail);
//			
//			
//			
//
//		} else if (SigaaListaComando.CONFIRMAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//			alterarStatus(mov, StatusInscricaoParticipante.CONFIRMADO);
//		}
//
//		else if (SigaaListaComando.CANCELAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//			alterarStatus(mov, StatusInscricaoParticipante.CANCELADO);
//		}
//
//		else if (SigaaListaComando.APROVAR_INSCRICAO_PARTICIPANTE_EXTENSAO.equals(mov.getCodMovimento())) {
//
//			selecionados = retornarParticipantesSelecionados(mov);
//			InscricaoAtividade inscricao = (InscricaoAtividade) mov.getObjMovimentado();
//			int vagasRestantes = inscricao.getQuantidadeVagas() - getDAO(InscricaoAtividadeParticipanteDao.class, mov)
//					.findParticipantesByInscricaoStatus(inscricao.getId(), StatusInscricaoParticipante.APROVADO).size();
//
//			//Verifica-se existe um número suficiente de vagas
//			if (vagasRestantes < selecionados.size()) {
//				throw getNegocioException("O número de inscritos selecionados excede o número de vagas disponíveis");
//			}
//			if (selecionados.isEmpty()) {
//				throw getNegocioException("Nenhum inscrito foi selecionado");
//			}
//
//			/*
//			 * OS INSCRITOS APROVADOS NA AÇÃO DE EXTENSÃO terão as informações das suas inscrições 
//			 * replicadas em participante externo de extensão
//			 */
//			Collection<ParticipanteAcaoExtensao> participantes = new ArrayList<ParticipanteAcaoExtensao>();
//			for (InscricaoAtividadeParticipante inscricaoOnline : selecionados) {	
//
//				ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();
//				
//				InscricaoAtividade inscricaoBanco = getGenericDAO(mov).findByPrimaryKey(inscricao.getId(), InscricaoAtividade.class);
//				
//				
//				/* ************************************************************************************
//				 *  As inscrições para sub atividade estão vindo com a informação da atividade
//				 *  Ai todas inscrições aprovadas das sub atividade estão indo para a atividade pai.
//				 *  Aqui tem que testar primeiro se a inscrição é para uma sub atividade 
//				 * ************************************************************************************/
//				
//				if( ! ValidatorUtil.isEmpty(inscricaoBanco.getSubAtividade())) {
//					participante.setSubAtividade(inscricaoBanco.getSubAtividade());
//					participante.setAcaoExtensao(null);
//				} else { 
//					
//					if( ValidatorUtil.isEmpty(inscricaoBanco.getSubAtividade())  && ! ValidatorUtil.isEmpty(inscricaoBanco.getAtividade()) ) {
//						participante.setAcaoExtensao(inscricaoBanco.getAtividade());
//						participante.setSubAtividade(null);
//					}else{
//						throw new NegocioException(" A incriação realizada não pertence nem a uma atividade nem mini atividade de extensão.");
//					}	
//				}				
//				
//				participante.setAtivo(true);
//				
//				//verifica se participante tem cpf ou é estrangeiro com passaporte cadastrado. 
//				if(inscricaoOnline.getPassaporte() != null){
//					participante.setInternacional(true);
//				    participante.setPassaporte(inscricaoOnline.getPassaporte());
//				}else{
//				    participante.setCpf(inscricaoOnline.getCpf());
//				}
//				participante.setNome(inscricaoOnline.getNome());
//				participante.setEmail(inscricaoOnline.getEmail());
//				
//				//participante.setEndereco(inscricaoOnline.getLogradouro() + ", " + inscricaoOnline.getNumero() + " - " + inscricaoOnline.getBairro() + 
//				//		" - " + inscricaoOnline.getMunicipio().getNome() + "/" + inscricaoOnline.getUnidadeFederativa().getSigla());
//				// SEPARA UF E MUNICIPIO DE ENDEREÇO
//				participante.setEndereco(inscricaoOnline.getLogradouro() + ", " + inscricaoOnline.getNumero() + " - " + inscricaoOnline.getBairro());
//				participante.setUnidadeFederativa(inscricaoOnline.getUnidadeFederativa());
//				participante.setMunicipio(inscricaoOnline.getMunicipio());
//				
//				participante.setDataCadastro(inscricaoOnline.getDataCadastro());
//				participante.setDataNascimento(inscricaoOnline.getDataNascimento());
//				participante.setInstituicao(inscricaoOnline.getInstituicao());
//				participante.setTipoParticipante(ParticipanteAcaoExtensao.OUTROS);
//
//				participante.setCep(inscricaoOnline.getCep());
//				// Preencher o tipo de participação
//				participante.getTipoParticipacao().setId(TipoParticipacaoAcaoExtensao.OUVINTE);
//
//				// Definir Discente ou Servidor do Participante 
//				inscricaoOnline.setDiscente(this.definirDiscente(inscricaoOnline));
//				inscricaoOnline.setServidor(this.definirServidor(inscricaoOnline));
//				participante.setDiscente(inscricaoOnline.getDiscente());
//				participante.setServidor(inscricaoOnline.getServidor());
//
//				participantes.add(participante);
//			}
//
//			ProcessadorParticipantesExtensao proc = new ProcessadorParticipantesExtensao();
//			MovimentoCadastro cadastroMov = new MovimentoCadastro();
//			cadastroMov.setSistema(Sistema.SIGAA);
//			cadastroMov.setUsuarioLogado(mov.getUsuarioLogado());
//			cadastroMov.setCodMovimento(SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO);
//			cadastroMov.setColObjMovimentado(participantes);
//			proc.execute(cadastroMov);
//			
//			// TODO verificar solução mais confiável que um iterator
//			Iterator<ParticipanteAcaoExtensao> i = participantes.iterator();
//			for (InscricaoAtividadeParticipante inscricaoOnline : selecionados) {
//				inscricaoOnline.getStatusInscricao().setId(StatusInscricaoParticipante.APROVADO);
//				inscricaoOnline.setParticipanteExtensao(i.next());
//								
//				dao.updateNoFlush(inscricaoOnline);
//				// Enviar email para os inscrito informando da aprovação de sua participação
//				enviarMensagemInscrito(inscricaoOnline, "Aceita", ACEITA_COORDENADOR, " Consulte a área de inscritos " +
//						"pelo Portal Público do "+siglaSigaa+" através do endereço abaixo para maiores informações.\n\n" +
//						ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO) +
//						"/sigaa/link/public/extensao/acessarAreaInscrito");
//			}
//
//			//atualiza a listagem dos participantes na tela para o usuário.
//			((InscricaoAtividade) mov.getObjMovimentado()).getParticipantesInscritos().removeAll(selecionados);
//		}
//
//		else if (SigaaListaComando.RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO.equals(mov.getCodMovimento())) {
//			selecionados = retornarParticipantesSelecionados(mov);
//			if (selecionados.isEmpty())
//				throw getNegocioException("Nenhum inscrito foi selecionado");
//			
//			recusarInscritos(mov, selecionados, siglaSigaa);
//
//			//atualiza a listagem dos participantes na tela para o usuário.
//			((InscricaoAtividade) mov.getObjMovimentado()).getParticipantesInscritos().removeAll(selecionados);
//		}
//		}finally{
//			dao.close();
//			if(daoInscricao != null) daoInscricao.close();
//		}
//		
		return null;
	}
//	
//	/** Responsável por visualizar o email informado e alterar das demais inscrições já realizadas */
//	private void verificarEmailInformado(MovimentoCadastro mov) throws DAOException {
//		List<InscricaoAtividadeParticipante> inscricoes = new ArrayList<InscricaoAtividadeParticipante>();
//		InscricaoAtividadeParticipanteDao dao = getDAO(InscricaoAtividadeParticipanteDao.class, mov);
//		InscricaoAtividadeParticipante inscr = mov.getObjMovimentado();
//		try {
//			 if(! inscr.isInternacional()){
//				 inscricoes = dao.findInformacoesEmailInscricoesParticipanteByCPF(inscr.getCpf());
//				 if ( inscricoes != null && !inscricoes.isEmpty() && !inscr.getEmail().equals(inscricoes.get(0).getEmail()) )
//						dao.atualizacaoEmailParticipante(inscr.getCpf(), inscr.getEmail());
//			 }else{
//				 inscricoes = dao.findInformacoesEmailInscricoesParticipanteByPassaporte(inscr.getPassaporte());
//				 if ( inscricoes != null && !inscricoes.isEmpty() && !inscr.getEmail().equals(inscricoes.get(0).getEmail()) )
//						dao.atualizacaoEmailParticipante(inscr.getPassaporte(), inscr.getEmail());
//			 }
//			
//		} finally {
//			
//		}
//		
//	}
//
//	/**
//	 * Definir servidor Participante.
//	 * 
//	 * @param inscricaoOnline
//	 * @throws DAOException 
//	 */
//	private Servidor definirServidor(InscricaoAtividadeParticipante inscricaoOnline) throws DAOException {
//		InscricaoAtividadeParticipanteDao dao = new InscricaoAtividadeParticipanteDao();
//		if( isNotEmpty( inscricaoOnline.getPassaporte() ) ){
//			return dao.findServidorParticipanteByPassaporte(inscricaoOnline.getPassaporte());	
//		} else {
//			return dao.findServidorParticipanteByCpf(inscricaoOnline.getCpf());	
//		}	
//	}
//
//	/**
//	 * Definir discente Participante.
//	 * 
//	 * @param inscricaoOnline
//	 * @throws DAOException 
//	 */
//	public Discente definirDiscente(InscricaoAtividadeParticipante inscricaoOnline) throws DAOException {
//		
//		InscricaoAtividadeParticipanteDao dao = new InscricaoAtividadeParticipanteDao();
//		if( isNotEmpty( inscricaoOnline.getPassaporte() ) ){
//			return dao.findDiscenteParticipanteByPassaporte(inscricaoOnline.getPassaporte());	
//		} else {
//			return dao.findDiscenteParticipanteByCpf(inscricaoOnline.getCpf());	
//		}		
//	}
//
//	
//	
//	
//	/**
//	 * Faz a validação da inscrição de extensão a ser cadastrada/atualizada.
//	 * 
//	 * @param mov
//	 * @throws NegocioException, ArqException
//	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
//		InscricaoAtividadeParticipante inscricao = ((MovimentoCadastro)mov).getObjMovimentado();
//		ListaMensagens mensagens = new ListaMensagens();
//		
//		if (SigaaListaComando.REENVIAR_SENHA_PARTICIPANTE_EXTENSAO.equals(mov.getCodMovimento())) {
//			if (StatusInscricaoParticipante.CANCELADO.equals(inscricao.getStatusInscricao().getId())) {
//				mensagens.addErro("Não foram encontradas inscrições válidas para o usuário informado.");
//			}
//		}else if (SigaaListaComando.ALTERAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//			
//			InscricaoAtividadeParticipante participante = ((MovimentoCadastro)mov).getObjMovimentado();
//			
//			ValidatorUtil.validateRequired(participante.getNome(), "Nome Completo", mensagens);
//			ValidatorUtil.validateRequired(participante.getLogradouro(), "Logradouro", mensagens);
//			ValidatorUtil.validateRequired(participante.getNumero(), "Número", mensagens);
//			ValidatorUtil.validateRequired(participante.getBairro(), "Bairro", mensagens);
//			ValidatorUtil.validateRequiredId(participante.getMunicipio().getId(), "Município", mensagens);
//			ValidatorUtil.validateRequiredId(participante.getUnidadeFederativa().getId(), "UF", mensagens);
//			
//			if (ValidatorUtil.validateCEP(participante.getCep(), "CEP", mensagens) == 0)
//				ValidatorUtil.validateRequired(participante.getCep(), "CEP", mensagens);
//			
//			ValidatorUtil.validateRequired(participante.getDataNascimento(), "Data de Nascimento", mensagens);
//			
//			if ( ! StringUtils.isEmpty(participante.getEmail() ))
//				ValidatorUtil.validateEmail(participante.getEmail(), "Email", mensagens);
//			else
//				ValidatorUtil.validateRequired(participante.getEmail(), "Email", mensagens);
//			
//		}else {
//			mensagens = inscricao.validate();
//		}
//		
//		checkValidation(mensagens);
}	
//
//	/**
//	 * Atualiza a situação da inscrição do participante.
//	 * A alteração pode ser realizada 1 a 1 ou também pode ser realizada
//	 * selecionado as inscrições que devem ser confirmadas. 
//	 * 
//	 * @param mov
//	 * @param status
//	 * @throws DAOException
//	 */
//	private void alterarStatus(MovimentoCadastro mov, int status) throws DAOException {
//		GenericDAO dao = getGenericDAO(mov);
//		try{			
//			if ( mov.getObjMovimentado() instanceof InscricaoAtividadeParticipante ) {
//				InscricaoAtividadeParticipante p = mov.getObjMovimentado();
//				dao.updateField(InscricaoAtividadeParticipante.class, p.getId(), "statusInscricao.id", status);
//			} else {
//				for (InscricaoAtividadeParticipante inscr : (Collection<InscricaoAtividadeParticipante>) mov.getObjAuxiliar() ) {
//					if ( inscr.isMarcado() )
//						dao.updateField(InscricaoAtividadeParticipante.class, inscr.getId(), "statusInscricao.id", status);
//				}
//			}
//
//		} finally {
//			dao.close();
//		}
//	}
//
//	/**
//	 * Retorna a lista dos participantes selecionados pelo coordenador.
//	 * 
//	 * @param mov
//	 * @return
//	 * @throws DAOException 
//	 */
//	private Collection<InscricaoAtividadeParticipante> retornarParticipantesSelecionados(MovimentoCadastro mov) throws DAOException{
//
//		Collection<InscricaoAtividadeParticipante> participantesSelecionados = new ArrayList<InscricaoAtividadeParticipante>();
//		for (InscricaoAtividadeParticipante participante : ((InscricaoAtividade) 
//				mov.getObjMovimentado()).getParticipantesInscritos())
//			if (participante.isMarcado()) {
//				participante = getGenericDAO(mov).findByPrimaryKey(participante.getId(), InscricaoAtividadeParticipante.class);
//				participantesSelecionados.add(participante);
//			}
//		return participantesSelecionados;
//	}
//
//	/**
//	 * Realiza a mudança da situação da inscrição para RECUSADA e envia um email notificando o candidato.
//	 * 
//	 * @param mov
//	 * @param inscritos
//	 * @throws DAOException
//	 */
//	public void recusarInscritos(MovimentoCadastro mov, Collection<InscricaoAtividadeParticipante> inscritos, String siglaSigaa) 
//	throws DAOException {
//
//		GenericDAO dao = getGenericDAO(mov);
//		try{
//			for (InscricaoAtividadeParticipante participante : inscritos) {
//				participante.getStatusInscricao().setId(StatusInscricaoParticipante.RECUSADO);
//				dao.updateNoFlush(participante);
//				// Enviar email para os inscrito informando da rejeição de sua participação
//				String motivo = ((InscricaoAtividade) mov.getObjMovimentado()).getMotivoCancelamento();
//				enviarMensagemInscrito(participante, "Recusada", RECUSADA_COORDENADOR, " O coordenador da ação " +
//						((motivo == null || motivo.isEmpty()) ? "não informou o motivo" : 
//							"informou o seguinte motivo: " + motivo) +
//							".\n\tVisite o portal público do "+siglaSigaa+" para consultar Cursos e Eventos de " +
//				"Extensão que possuam inscrições abertas.");
//			}
//		}finally{
//			dao.close();
//		}
//
//	}
//
//	/**
//	 * Método responsável pela construção e envio do email.
//	 * 
//	 * @param p
//	 * @param assunto
//	 * @param conteudo
//	 * @param complemento
//	 */
//	private void enviarMensagemInscrito(InscricaoAtividadeParticipante p, String assunto, String conteudo, String complemento) {
//
//		StringBuffer mensagem = new StringBuffer();
//		mensagem.append("Prezado(a) ");
//		mensagem.append(p.getNome());
//		mensagem.append(", \n\n");
//		
//		if(! ValidatorUtil.isEmpty(p.getInscricaoAtividade().getAtividade())){
//			mensagem.append("\tSua solicitação de inscrição para participação na Ação \"");
//			mensagem.append(p.getInscricaoAtividade().getAtividade().getTitulo().toUpperCase());
//		} else {
//			mensagem.append("\tSua solicitação de inscrição para participação na Mini Ação \"");
//			mensagem.append(p.getInscricaoAtividade().getSubAtividade().getTitulo().toUpperCase());
//		}
//		
//		
//		mensagem.append("\" foi ");
//		mensagem.append(conteudo);
//		mensagem.append(complemento);
//
//		MailBody mail = new MailBody();
//		mail.setContentType(MailBody.TEXT_PLAN);
//		mail.setAssunto("Inscrição " + assunto + " - MENSAGEM AUTOMÁTICA");
//		mail.setMensagem(mensagem.toString());
//		mail.setEmail(p.getEmail());
//		mail.setNome(p.getNome());
//		Mail.send(mail);
//	}
//
//	/**
//	 * Retorna uma instância de NegocioException contendo a mensagem de erro.
//	 * 
//	 * @param msg
//	 * @return
//	 */
//	private NegocioException getNegocioException(String msg) {
//		NegocioException e = new NegocioException();
//		e.addErro(msg);
//		return e;
//	}
//	
//	/**
//	 * 
//	 * A inscrição pode ser na atividade principal ou em uma subAtividadeExtensao
//	 * 
//	 * @param iap
//	 * @return
//	 */
//	private boolean isInscricaoEmAtividade(InscricaoAtividadeParticipante iap) {		
//		if( ! ValidatorUtil.isEmpty(iap.getInscricaoAtividade().getAtividade())) {
//			return true;
//		} else {
//			return false;
//		}				
//	}

}
