/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.ensino.PessoaInscricaoDao;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.EquipeProgramaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.AgendaProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.EditalProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.PessoaInscricao;
import br.ufrn.sigaa.ensino.negocio.dominio.MovimentoInscricaoSelecao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 * Processador responsável pelo controle
 * das inscrições em processos seletivos
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorInscricaoSelecao extends AbstractProcessador {

	/** Processa a inscrição do candidato.
	 * (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoInscricaoSelecao movInscricao = (MovimentoInscricaoSelecao) mov;
		InscricaoSelecao inscricao = movInscricao.getInscricaoSelecao();
		QuestionarioRespostas questionarioRespostas = movInscricao.getQuestionarioRespostas();
		
		ProcessoSeletivoDao selecaoDao = getDAO(ProcessoSeletivoDao.class, mov);
		PessoaInscricaoDao pessoaInscricaoDao = getDAO(PessoaInscricaoDao.class, mov);
		PessoaInscricao pessoaInscricao = null;
		
		// guardando os ids originais dos objetos para caso ocorra alguma exceção retornar-los aos que eram antes
		int idInscricaoInicial = inscricao.getId();
		int idPessoaInscricaoInicial = 0, idEnderecoInicial = 0;
		if( inscricao.getPessoaInscricao() != null )
			idPessoaInscricaoInicial = inscricao.getPessoaInscricao().getId();
		if( inscricao.getPessoaInscricao().getEnderecoResidencial() != null )
			idEnderecoInicial = inscricao.getPessoaInscricao().getEnderecoResidencial().getId();
		
		UsuarioDao uDao = getDAO(UsuarioDao.class, movInscricao);
		EquipeProgramaDao equipeDao = getDAO(EquipeProgramaDao.class, movInscricao);
		
		try {
			validate(mov);
			
			
			// Persistir arquivo com o edital
			if (movInscricao.getArquivoProjeto() != null ) {

				int idArquivoProjeto = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivoProjeto,
						movInscricao.getArquivoProjeto().getBytes(),
						movInscricao.getArquivoProjeto().getContentType(),
						movInscricao.getArquivoProjeto().getName());
				inscricao.setIdArquivoProjeto( idArquivoProjeto );
			}

			// Cadastrar dados pessoais da inscrição
			pessoaInscricao = inscricao.getPessoaInscricao();
			pessoaInscricao.formatarDados();
			anularTransientObjects(pessoaInscricao);
			pessoaInscricaoDao.createOrUpdate(pessoaInscricao);

			//Caso não seja processo de transferência voluntária
			if ( !inscricao.getProcessoSeletivo().isPossuiAgendamento() 
					|| (inscricao.getProcessoSeletivo().getCurso() != null && inscricao.getProcessoSeletivo().getCurso().getNivel()!=NivelEnsino.GRADUACAO)
					|| (inscricao.getProcessoSeletivo().getMatrizCurricular()!=null && inscricao.getProcessoSeletivo().getMatrizCurricular().getCurso().getNivel()!=NivelEnsino.GRADUACAO)){
				inscricao.setAgenda(null);
			}
			
			// Definir número de inscrição
			if ( inscricao.getId() == 0 ) 
				inscricao.setNumeroInscricao(selecaoDao.getNextSeq("ensino.processo_seletivo_id_seq"));
				
			if( inscricao.getProcessoSeletivo().isPossuiAgendamento() && inscricao.getAgenda() != null && inscricao.getAgenda().getId()>0 ){
				inscricao.setAgenda(getGenericDAO(movInscricao).
						findByPrimaryKey(inscricao.getAgenda().getId(), AgendaProcessoSeletivo.class));
			}
			
			// Caso o concurso tenha taxa de inscrição,
			if ( inscricao.getId() == 0 && movInscricao.getInscricaoSelecao().getProcessoSeletivo().getEditalProcessoSeletivo().getTaxaInscricao() > 0) {
				// cria uma GRU para pagamento da taxa de inscrição
				EditalProcessoSeletivo edital = inscricao.getProcessoSeletivo().getEditalProcessoSeletivo();
				String instrucoes = "Nº de Inscrição: " + inscricao.getNumeroInscricao()+"\n" +
						"Curso: " + inscricao.getProcessoSeletivo().getCurso().getDescricao();
				// instrução expecífica na GRU de formação complementar.
				if (!isEmpty(edital.getInstrucoesEspecificasGRU())) {
					instrucoes = edital.getInstrucoesEspecificasGRU() + "\n\n" +
								instrucoes;
				}
				SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
				String competencia = formato.format(edital.getDataVencimentoBoleto());
				GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.createGRU(
						edital.getIdConfiguracaoGRU(),
						inscricao.getPessoaInscricao().getCpf(),
						inscricao.getPessoaInscricao().getNome(),
						inscricao.getPessoaInscricao().getEnderecoResidencial().getDescricao(),
						instrucoes, 
						competencia,
						edital.getDataVencimentoBoleto(), 
						edital.getTaxaInscricao());
				inscricao.setIdGRU(gru.getId());
			}
			// Persistir inscrição
			inscricao.anularAtributosVazios();
			selecaoDao.createOrUpdate( inscricao );
			
			// Caso um questionário personalizado para o processo seletivo 
			// tenha sido respondido, persistir respostas informadas
			if (questionarioRespostas != null) {
				ProcessadorQuestionarioRespostas processadorQuestionarioRespostas = new ProcessadorQuestionarioRespostas();
				processadorQuestionarioRespostas.cadastrarRespostas(mov, questionarioRespostas);
			}
			
			
			// Notifica o orientador que foi escolhido
			if (inscricao.getProcessoSeletivo().getNotificarOrientador() != null
					&& inscricao.getProcessoSeletivo().getNotificarOrientador()
					&& inscricao.getOrientador() != null
				) {

				EquipePrograma equipePrograma = equipeDao.refresh(inscricao.getOrientador());
				
				//Verifica se existe um usuário para o envio da notificação do orientador
				List<Usuario> usuarios = uDao.findByPessoa(equipePrograma.getPessoa());
				if ( !usuarios.isEmpty() ){
					
					Usuario	usuario = new Usuario();
					usuario = usuarios.get(0);
				
					Mensagem msg = new Mensagem();
					msg.setTitulo("Escolhido como Orientador(a) em Processo Seletivo");
					msg.setMensagem("Prezado(a) Orientador(a), \n\t O aluno " + 
							inscricao.getPessoaInscricao().getNome() + " se inscreveu na seleção do Programa de " + 
							inscricao.getProcessoSeletivo().getCurso().getNomeCursoStricto() + " e o escolheu como Orientador(a). \n " +
									"Para maiores informações, procure a coordenação do programa. ");
					msg.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
					msg.setDataCadastro(new Date());
					msg.setConfLeitura(false);
					msg.setUsuario(usuario);
					msg.setRemetente(new Usuario(Usuario.USUARIO_SISTEMA));
					msg.setPapel(null);
					msg.setAutomatica(true);
	
					MensagemDAO msgDAO = new MensagemDAO();
					try{
						msgDAO.create(MensagensHelper.msgSigaaToMsgArq(msg));
					}finally{
						msgDAO.close();
					}				
	
					MailBody mail = new MailBody();
					mail.setContentType(MailBody.TEXT_PLAN);
					
					// Definir remetente
					mail.setFromName( RepositorioDadosInstitucionais.get("siglaSigaa") );
					
					mail.setEmail( usuario.getEmail() );
					mail.setAssunto( msg.getTitulo() );
					mail.setMensagem( msg.getMensagem() );
					Mail.send(mail);			
				
				}
				
			}
		
		}catch (ArqException e) {
			// retornando os ids dos objetos aos valores que eram antes caso ocorra alguma exceção 
			if( inscricao != null )
				inscricao.setId(idInscricaoInicial);
			if( pessoaInscricao != null )
				pessoaInscricao.setId(idPessoaInscricaoInicial);
			if( pessoaInscricao != null && pessoaInscricao.getEnderecoResidencial() != null )
				pessoaInscricao.getEnderecoResidencial().setId(idEnderecoInicial);
			throw e;
		}catch (NegocioException e) {
			// retornando os ids dos objetos aos valores que eram antes caso ocorra alguma exceção 
			if( inscricao != null )
				inscricao.setId(idInscricaoInicial);
			if( pessoaInscricao != null )
				pessoaInscricao.setId(idPessoaInscricaoInicial);
			if( pessoaInscricao != null && pessoaInscricao.getEnderecoResidencial() != null )
				pessoaInscricao.getEnderecoResidencial().setId(idEnderecoInicial);
			throw e;
		} catch (IOException e) {
			throw new ArqException(e);
		} finally {
			selecaoDao.close();
			pessoaInscricaoDao.close();
			uDao.close();
			equipeDao.close();
		}

		return inscricao;
	}

	/** Valida os dados do movimento.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoInscricaoSelecao movInscricao = (MovimentoInscricaoSelecao) mov;
		GenericDAO dao = getGenericDAO(movInscricao);
		InscricaoSelecaoDao inscricaoDao = getDAO(InscricaoSelecaoDao.class, mov);
		
		InscricaoSelecao inscricao = movInscricao.getInscricaoSelecao();
		inscricao.getProcessoSeletivo().setEditalProcessoSeletivo( 
				dao.refresh( inscricao.getProcessoSeletivo().getEditalProcessoSeletivo() ) );
		
		ListaMensagens erros = new ListaMensagens();
		erros.addAll( inscricao.validate() );

		// Validar se já existe uma inscrição para esta pessoa no mesmo curso
		// Esta validação é realizada apenas para Processos Seletivos que são gratuítos.
		// Processos Seletivos pagos, ficam valendo a última inscrição paga.
		if(inscricao.getProcessoSeletivo().getEditalProcessoSeletivo().getTaxaInscricao() == 0 ){
			inscricaoDao = getDAO(InscricaoSelecaoDao.class, mov);
			try {
				InscricaoSelecao outraInscricao = inscricaoDao.findByCpfAndProcessoEdital(
						inscricao.getPessoaInscricao().getCpf(),
						(inscricao.getPessoaInscricao().isEstrangeiro()?inscricao.getPessoaInscricao().getPassaporte():null),
						inscricao.getProcessoSeletivo(), inscricao.getProcessoSeletivo().getEditalProcessoSeletivo());
				
 				if ( !isEmpty(outraInscricao) && outraInscricao.getId() != inscricao.getId() ) {
					erros.addErro("Já existe uma inscrição realizada neste processo seletivo para este " + 
							( !isEmpty(inscricao.getPessoaInscricao().getPassaporte())?"passaporte.":"CPF." )		
					);
				}
			} finally {
				inscricaoDao.close();
			}
		}
		
		if( movInscricao.getCodMovimento().equals(SigaaListaComando.INSCREVER_PROCESSO_SELETIVO) ){
			InscricaoSelecaoValidator.validarGeral(inscricao, erros);
		}
		
		InscricaoSelecaoValidator.validarInscricaoProcessoSeletivo(inscricao, erros);
		
		checkValidation(erros);
	}
	
	/**
	 * Anula objetos transientes de pessoa! Para evitar transient object exception
	 * @param pessoa
	 */
	public static void anularTransientObjects(PessoaInscricao pessoa){
		if (pessoa.getTipoRaca() != null && pessoa.getTipoRaca().getId() == 0)
			pessoa.setTipoRaca(null);
		if (pessoa.getEstadoCivil() != null && pessoa.getEstadoCivil().getId() == 0)
			pessoa.setEstadoCivil(null);
		if (pessoa.getUnidadeFederativa() != null && pessoa.getUnidadeFederativa().getId() == 0) 
			pessoa.setUnidadeFederativa(null);
		if (pessoa.getMunicipio() != null && pessoa.getMunicipio().getId() == 0) 
			pessoa.setMunicipio(null);
		if (pessoa.getEnderecoResidencial() != null ){
			if(pessoa.getEnderecoResidencial().getMunicipio() != null && pessoa.getEnderecoResidencial().getMunicipio().getId() == 0)
				pessoa.getEnderecoResidencial().setMunicipio(null);
			if(pessoa.getEnderecoResidencial().getUnidadeFederativa() != null && pessoa.getEnderecoResidencial().getUnidadeFederativa().getId() == 0)
				pessoa.getEnderecoResidencial().setUnidadeFederativa(null);
		}
		
	}

}
