/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Processador para realizar o cadastro e gerenciamento das inscri��es realizadas pelo portal p�blico e 
 * o gerenciamento das inscri��es pelo coordenador atrav�s do portal docente.
 * 
 * @author Daniel Augusto
 *
 * 
 * @Deprecated c�digo imposs�vel de se manter. Esse c�digo � um bom exemplo de como n�o se deve programar!
 */
@Deprecated
public class ProcessadorInscricaoParticipante extends AbstractProcessador {

//	/** representa parte da mensagem de submiss�o com sucesso para o usu�rio. */
//	private static final String SUBMETIDA_SUCESSO = "submetida com sucesso!";
//	
//	/** representa parte da mensagem  enviadas para o usu�rio informando da aprova��o de sua participa��o. */
//	private static final String ACEITA_COORDENADOR = "aceita pelo coordenador da a��o.";
//	
//	/** representa parte da mensagem  enviadas para o usu�rio informando da rejei��o de sua participa��o*/
//	private static final String RECUSADA_COORDENADOR = "recusada pelo coordenador da a��o.";
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
//				mensagem.append(" Esta a��o inicia em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getAtividade().getDataInicio()));
//				mensagem.append(" e finaliza em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getAtividade().getDataFim()));
//			} else {
//				mensagem.append(" Esta mini a��o inicia em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getSubAtividade().getInicio()));
//				mensagem.append(" e finaliza em ");
//				mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade().getSubAtividade().getFim()));
//			}
//			
//			
//			
//			
//			if (StatusInscricaoParticipante.INSCRITO.equals(participante.getStatusInscricao().getId())) {
//			    mensagem.append(". Para confirmar sua solicita��o acesse o endere�o abaixo e informe a senha recebida.\n\n ");
//			    mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			    mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//			    mensagem.append(participante.getCodigoAcesso() + "/");
//			    mensagem.append(participante.getInscricaoAtividade().getId());
//			    mensagem.append("\n\nSenha: ");
//			    mensagem.append(participante.getSenha());
//			    mensagem.append("\n\nATEN��O: Sua inscri��o est� pendente de aprova��o pelo coordenador da a��o, ");
//			    mensagem.append("acompanhe o andamento atrav�s da �rea de inscritos pelo portal p�blico do "+siglaSigaa+".");
//			    
//			} else if (StatusInscricaoParticipante.APROVADO.equals(participante.getStatusInscricao().getId()) ||
//				StatusInscricaoParticipante.CONFIRMADO.equals(participante.getStatusInscricao().getId())) {
//			    mensagem.append("\n\nSua senha e c�digo de acesso foram alterados com sucesso.\n\n");
//			    mensagem.append("\n\nParticipe desta a��o atrav�s da �rea de inscritos pelo portal p�blico do "+siglaSigaa+" informando o e-mail e senha.\n\n");
//			    mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			    mensagem.append("/sigaa/link/public/extensao/acessarAreaInscrito \n\n");
//			    mensagem.append("\n\nE-mail: ");
//			    mensagem.append(participante.getEmail());
//			    mensagem.append("\n\nSenha: ");
//			    mensagem.append(participante.getSenha());
//			} else {
//			    mensagem.append("\n\nN�o foram encontradas inscri��es v�lidas para o usu�rio informado.\n\n");
//			    mensagem.append("\n\nPor favor, realize uma nova inscri��o atrav�s do portal p�blico do "+siglaSigaa+".\n\n");
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
//		    	 * @negocio: N�o h� subdivis�o de participantes por inscri��o on-line aberta.
//		    	 * Todos os participantes s�o ligados diretamente a uma a��o de extens�o.
//		    	 * Ou seja, um participante cadastrado atrav�s da inscri��o que ficou aberta de 01 a 05 de junho
//		    	 * est� vinculado a a��o de extens�o da mesma forma que um participante que fez a inscri��o atrav�s da inscri��o
//		    	 * que ficou aberta de 06 a 10 de junho.
//		    	 * A gest�o de mini-cursos ou sub-eventos ainda n�o est� implementada. 		
//		    	 */
//		    
//			validate(mov);
//			InscricaoAtividadeParticipante participante = mov.getObjMovimentado();
//			InscricaoAtividadeParticipante inscricaoAnterior = (InscricaoAtividadeParticipante) mov.getObjAuxiliar();
//			verificarEmailInformado(mov);
//			
//			//Cancelando inscri��o anterior
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
//			 * Significa que n�o foi pago ainda. Caso a atividade n�o possua taxa de matr�cula
//			 * Esse campo ficar� nulo no banco.
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
//			// Caso um question�rio personalizado para o processo seletivo 
//			// tenha sido respondido, persistir respostas informadas
//			if (participante.getQuestionarioRespostas() != null) {
//				ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
//				processadorQuestionarioRespostas.cadastrarRespostas(mov, participante.getQuestionarioRespostas());
//			}
//			
//			
//			StringBuffer mensagem = new StringBuffer();
//			mensagem.append(" Esta a��o inicia em ");
//			mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//					.getAtividade().getDataInicio()));
//			mensagem.append(" e finaliza em ");
//			mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//					.getAtividade().getDataFim()));
//			mensagem.append(". Para confirmar sua solicita��o acesse o endere�o abaixo e informe a senha recebida.\n\n ");
//			mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//			mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//			mensagem.append(participante.getCodigoAcesso() + "/");
//			mensagem.append(participante.getInscricaoAtividade().getId());
//			mensagem.append("\n\nSenha: ");
//			mensagem.append(participante.getSenha());
//			
//			mensagem.append("\n\nATEN��O: \n\tSua inscri��o est� pendente de aprova��o pelo coordenador da a��o, ");
//			mensagem.append("acompanhe o andamento atrav�s da �rea de inscritos pelo portal p�blico do "+siglaSigaa+".");
//
//			/*
//			 * Avisa ao usu�rio que o curso exige cobran�a de taxa e que ele deve emitir a GRU na �rea de acompanhamento de cursos e eventos extens�o 
//			 */
//		
//			if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//				mensagem.append("\n\n\tEssa atividade exige a cobran�a de uma taxa de matr�cula de "+cursoEEvento.getTaxaMatriculaFormatada()+".");
//				mensagem.append(" Na �rea de inscritos existe um link para a emiss�o da GRU para efitivar o pagamento. A aprova��o do coordenador ir� depender da confirma��o desse pagamento. ");
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
//			// Atualiza apenas os campos permitidos da inscri��o //
//			// O nome, endere�o, data de nascimento e email.
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
//		//Cancelando inscri��o anterior
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
//		mensagem.append(" Esta mini a��o inicia em ");
//		mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//				.getSubAtividade().getInicio()));
//		mensagem.append(" e finaliza em ");
//		mensagem.append(Formatador.getInstance().formatarData(participante.getInscricaoAtividade()
//				.getSubAtividade().getFim()));
//		mensagem.append(". Para confirmar sua solicita��o acesse o endere�o abaixo e informe a senha recebida.\n\n ");
//		mensagem.append(ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO));
//		mensagem.append("/sigaa/link/public/extensao/confirmarInscricao/");
//		mensagem.append(participante.getCodigoAcesso() + "/");
//		mensagem.append(participante.getInscricaoAtividade().getId());
//		mensagem.append("\n\nSenha: ");
//		mensagem.append(participante.getSenha());
//		
//		mensagem.append("\n\nATEN��O: \n\tSua inscri��o est� pendente de aprova��o pelo coordenador da a��o, ");
//		mensagem.append("acompanhe o andamento atrav�s da �rea de inscritos pelo portal p�blico do "+siglaSigaa+".");
//
//		/*
//		 * Avisa ao usu�rio que o curso exige cobran�a de taxa e que ele deve emitir a GRU na �rea de acompanhamento de cursos e eventos extens�o 
//		 */
//	
//		if(cursoEEvento != null && cursoEEvento.isCobrancaTaxaMatricula()){
//			mensagem.append("\n\n\tEssa atividade exige a cobran�a de uma taxa de matr�cula de "+cursoEEvento.getTaxaMatriculaFormatada()+".");
//			mensagem.append(" Na �rea de inscritos existe um link para a emiss�o da GRU para efitivar o pagamento. A aprova��o do coordenador ir� depender da confirma��o desse pagamento. ");
//		}
//		
//		
//		
//		
//		StringBuffer msg = new StringBuffer();
//		msg.append("Prezado(a) ");
//		msg.append(participante.getNome());
//		msg.append(", \n\n");
//		msg.append("\tSua solicita��o de inscri��o para participa��o na Mini A��o \"");
//		msg.append(participante.getInscricaoAtividade().getSubAtividade().getTitulo().toUpperCase());
//		msg.append("\" foi Submetida com sucesso!");		
//
//		MailBody mail = new MailBody();
//		mail.setContentType(MailBody.TEXT_PLAN);
//		mail.setAssunto("Inscri��o  Submetida - MENSAGEM AUTOM�TICA");
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
//			//Verifica-se existe um n�mero suficiente de vagas
//			if (vagasRestantes < selecionados.size()) {
//				throw getNegocioException("O n�mero de inscritos selecionados excede o n�mero de vagas dispon�veis");
//			}
//			if (selecionados.isEmpty()) {
//				throw getNegocioException("Nenhum inscrito foi selecionado");
//			}
//
//			/*
//			 * OS INSCRITOS APROVADOS NA A��O DE EXTENS�O ter�o as informa��es das suas inscri��es 
//			 * replicadas em participante externo de extens�o
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
//				 *  As inscri��es para sub atividade est�o vindo com a informa��o da atividade
//				 *  Ai todas inscri��es aprovadas das sub atividade est�o indo para a atividade pai.
//				 *  Aqui tem que testar primeiro se a inscri��o � para uma sub atividade 
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
//						throw new NegocioException(" A incria��o realizada n�o pertence nem a uma atividade nem mini atividade de extens�o.");
//					}	
//				}				
//				
//				participante.setAtivo(true);
//				
//				//verifica se participante tem cpf ou � estrangeiro com passaporte cadastrado. 
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
//				// SEPARA UF E MUNICIPIO DE ENDERE�O
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
//				// Preencher o tipo de participa��o
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
//			// TODO verificar solu��o mais confi�vel que um iterator
//			Iterator<ParticipanteAcaoExtensao> i = participantes.iterator();
//			for (InscricaoAtividadeParticipante inscricaoOnline : selecionados) {
//				inscricaoOnline.getStatusInscricao().setId(StatusInscricaoParticipante.APROVADO);
//				inscricaoOnline.setParticipanteExtensao(i.next());
//								
//				dao.updateNoFlush(inscricaoOnline);
//				// Enviar email para os inscrito informando da aprova��o de sua participa��o
//				enviarMensagemInscrito(inscricaoOnline, "Aceita", ACEITA_COORDENADOR, " Consulte a �rea de inscritos " +
//						"pelo Portal P�blico do "+siglaSigaa+" atrav�s do endere�o abaixo para maiores informa��es.\n\n" +
//						ParametroHelper.getInstance().getParametro(ParametrosGerais.ENDERECO_ACESSO) +
//						"/sigaa/link/public/extensao/acessarAreaInscrito");
//			}
//
//			//atualiza a listagem dos participantes na tela para o usu�rio.
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
//			//atualiza a listagem dos participantes na tela para o usu�rio.
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
//	/** Respons�vel por visualizar o email informado e alterar das demais inscri��es j� realizadas */
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
//	 * Faz a valida��o da inscri��o de extens�o a ser cadastrada/atualizada.
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
//				mensagens.addErro("N�o foram encontradas inscri��es v�lidas para o usu�rio informado.");
//			}
//		}else if (SigaaListaComando.ALTERAR_INSCRICAO_PARTICIPANTE.equals(mov.getCodMovimento())) {
//			
//			InscricaoAtividadeParticipante participante = ((MovimentoCadastro)mov).getObjMovimentado();
//			
//			ValidatorUtil.validateRequired(participante.getNome(), "Nome Completo", mensagens);
//			ValidatorUtil.validateRequired(participante.getLogradouro(), "Logradouro", mensagens);
//			ValidatorUtil.validateRequired(participante.getNumero(), "N�mero", mensagens);
//			ValidatorUtil.validateRequired(participante.getBairro(), "Bairro", mensagens);
//			ValidatorUtil.validateRequiredId(participante.getMunicipio().getId(), "Munic�pio", mensagens);
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
//	 * Atualiza a situa��o da inscri��o do participante.
//	 * A altera��o pode ser realizada 1 a 1 ou tamb�m pode ser realizada
//	 * selecionado as inscri��es que devem ser confirmadas. 
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
//	 * Realiza a mudan�a da situa��o da inscri��o para RECUSADA e envia um email notificando o candidato.
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
//				// Enviar email para os inscrito informando da rejei��o de sua participa��o
//				String motivo = ((InscricaoAtividade) mov.getObjMovimentado()).getMotivoCancelamento();
//				enviarMensagemInscrito(participante, "Recusada", RECUSADA_COORDENADOR, " O coordenador da a��o " +
//						((motivo == null || motivo.isEmpty()) ? "n�o informou o motivo" : 
//							"informou o seguinte motivo: " + motivo) +
//							".\n\tVisite o portal p�blico do "+siglaSigaa+" para consultar Cursos e Eventos de " +
//				"Extens�o que possuam inscri��es abertas.");
//			}
//		}finally{
//			dao.close();
//		}
//
//	}
//
//	/**
//	 * M�todo respons�vel pela constru��o e envio do email.
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
//			mensagem.append("\tSua solicita��o de inscri��o para participa��o na A��o \"");
//			mensagem.append(p.getInscricaoAtividade().getAtividade().getTitulo().toUpperCase());
//		} else {
//			mensagem.append("\tSua solicita��o de inscri��o para participa��o na Mini A��o \"");
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
//		mail.setAssunto("Inscri��o " + assunto + " - MENSAGEM AUTOM�TICA");
//		mail.setMensagem(mensagem.toString());
//		mail.setEmail(p.getEmail());
//		mail.setNome(p.getNome());
//		Mail.send(mail);
//	}
//
//	/**
//	 * Retorna uma inst�ncia de NegocioException contendo a mensagem de erro.
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
//	 * A inscri��o pode ser na atividade principal ou em uma subAtividadeExtensao
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
