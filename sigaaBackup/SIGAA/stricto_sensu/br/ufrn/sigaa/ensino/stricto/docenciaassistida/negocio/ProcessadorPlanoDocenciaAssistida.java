/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/03/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.DtoUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.DestinatarioDTO;
import br.ufrn.integracao.dto.NotificacaoDTO;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.EnvioNotificacoesRemoteService;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.PlanoDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.AtividadeDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.HistoricoPlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.IndicacaoBolsistaReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.IndicacaoBolsistaReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PeriodoIndicacaoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.negocio.MovimentoIndicacaoBolsistaReuni;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.ModalidadeBolsaExterna;

/**
 * Processador responsável pelo cadastro de Indicação de bolsas REUNI
 * e de Plano de Docência Assistida de pós-graduação.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorPlanoDocenciaAssistida extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {
				
		if (SigaaListaComando.CADASTRAR_INDICACAO_BOLSAS_REUNI.equals(mov.getCodMovimento()) || 
				SigaaListaComando.ALTERAR_ALUNO_INDICACAO_BOLSAS_REUNI.equals(mov.getCodMovimento())){
			IndicacaoBolsistaReuni indicacao = ((MovimentoIndicacaoBolsistaReuni) mov).getIndicacao();
			DiscenteStricto discenteAtual = ((MovimentoIndicacaoBolsistaReuni) mov).getDiscenteAtual();
			IndicacaoBolsistaReuniDao daoIndicacao = getDAO(IndicacaoBolsistaReuniDao.class, mov);	
			
			try {				
				DiscenteStricto discenteAnterior = indicacao.getDiscente();
				indicacao.setDiscente(discenteAtual);					
				daoIndicacao.createOrUpdate(indicacao);					
				
				if (SigaaListaComando.ALTERAR_ALUNO_INDICACAO_BOLSAS_REUNI.equals(mov.getCodMovimento())){
					/* caso foi alterado o discente, inativa o plano de docência do discente anterior
					 *  e cria um novo plano para o atual. */ 
					daoIndicacao.inativarPlanoDocenciaAssistida(indicacao);	

					/* Remove as permissões da(s) turma(s) do discente anterior, referente aos períodos indicados */
					for (PeriodoIndicacaoReuni periodo : indicacao.getPeriodosIndicacao())
						daoIndicacao.removerPermissaoTurma(discenteAnterior,periodo.getAno(),periodo.getPeriodo(), true);			
					
					CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
					for (PeriodoIndicacaoReuni periodo : indicacao.getPeriodosIndicacao()){
						if (cal != null && (cal.getAno() <= periodo.getAno() && cal.getPeriodo() <= periodo.getPeriodo())){
							PlanoDocenciaAssistida plano = new PlanoDocenciaAssistida();
							plano.setDiscente(indicacao.getDiscente());
							plano.setStatus(PlanoDocenciaAssistida.CADASTRADO);
							plano.setPeriodoIndicacaoBolsa(periodo);
							plano.setAtivo(true);
							plano.setBolsista(true);
							plano.setModalidadeBolsa(new ModalidadeBolsaExterna(ModalidadeBolsaExterna.REUNI));
							daoIndicacao.createNoFlush(plano);						
						}					
					}	
				}				
			} finally {
				if (daoIndicacao != null)
					daoIndicacao.close();
			}
			
			return indicacao;
			
		} else if (SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) || SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) 
				|| SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento())){
			
			PlanoDocenciaAssistida plano = getPlanoDocenciaAssistida(mov);
			PlanoDocenciaAssistidaDao daoPlano = getPlanoDocenciaDao(mov);		
			IndicacaoBolsistaReuniDao daoIndicacao = getDAO(IndicacaoBolsistaReuniDao.class, mov);
			
			/* Remove as permissões da(s) turma(s) do discente */
			if (plano.isSubmetido() || plano.isConcluido() || plano.isCadastrado() || plano.isReprovado() || plano.isSolicitadoAlteracao())
				daoIndicacao.removerPermissaoTurma(plano.getDiscente(), plano.getAno(), plano.getPeriodo(), false);			
			
			try {
				if (SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) || SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento())){					
					validate(mov);
					// Salva arquivo enviado no relatório final
					salvarArquivo((MovimentoCadastro) mov);

					/* Salva as atividades */
					if (isNotEmpty(plano.getAtividadeDocenciaAssistida()))
						daoPlano.getHibernateTemplate().saveOrUpdateAll(plano.getAtividadeDocenciaAssistida());
													
					daoPlano.createOrUpdate(plano);
				} else if (SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento())) {				
					/* Atualiza a observação */
					daoPlano.updateField(PlanoDocenciaAssistida.class, plano.getId(), "observacao", plano.getObservacao());
					/* Atualiza o status */
					daoPlano.updateField(PlanoDocenciaAssistida.class, plano.getId(), "status", plano.getStatus());
					/* Inativa o plano caso cancele */
					if (plano.getStatus() == PlanoDocenciaAssistida.CANCELADO)
						daoPlano.updateField(PlanoDocenciaAssistida.class, plano.getId(), "ativo", false);
				}
				
				/* Cria a permissão para acesso a turma virtual */
				if ((plano.isAprovado() || plano.isAnaliseRelatorio() || plano.isSolicitadoAlteracaoRelatorio()) && !isEmpty(plano.getTurmaDocenciaAssistida()))
					criarPermissaoTurma(plano.getTurmaDocenciaAssistida(), mov);				
				
				/* Cria Histórico de Mudança de Status */
				if (SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) || 
						SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) || 
						plano.isSubmetido() || plano.isAnaliseRelatorio())		
					daoPlano.create(criarHistorico(mov));
				
				// Notifica os Docentes, Chefes, Orientadores.. 
				if (plano.isSubmetido())
					notificarResponsaveis(plano,mov);
				
			} finally {
				if (daoPlano != null)
					daoPlano.close();
				
				if (daoIndicacao != null)
					daoIndicacao.close();				
			}
			return plano;
		}
		return mov;			
	}
	
	/**
	 * Cria o Histórico de Mudança de Status do Plano de Docência Assistida
	 * @param mov
	 * @throws DAOException 
	 */
	private HistoricoPlanoDocenciaAssistida criarHistorico(Movimento mov) throws DAOException{
		PlanoDocenciaAssistida plano = getPlanoDocenciaAssistida(mov);		
		
		HistoricoPlanoDocenciaAssistida historico = new HistoricoPlanoDocenciaAssistida();
		historico.setPlanoDocenciaAssistida(plano);
		historico.setStatus(plano.getStatus());
		
		if (SigaaListaComando.ANALISAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()))
			historico.setObservacao(plano.getObservacao());
		else if (SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()))
			historico.setObservacao("PLANO CADASTRADO PELO DISCENTE");
		else if (plano.isSubmetido())
			historico.setObservacao("PLANO SUBMETIDO PELO DISCENTE");
		else if (plano.isAnaliseRelatorio())
			historico.setObservacao("RELATÓRIO SUBMETIDO PELO DISCENTE");		
					
		return historico;
	}
	
	/**
	 * Salva o arquivo anexado
	 * 
	 * @param hMov
	 * @param homologacao
	 * @throws IOException
	 * @throws DAOException 
	 */
	private void salvarArquivo(MovimentoCadastro mov) throws DAOException {
		try {
			UploadedFile arquivo = (UploadedFile) mov.getObjAuxiliar();
			if (arquivo != null){
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
				PlanoDocenciaAssistida plano = getPlanoDocenciaAssistida(mov);
				plano.setIdArquivo(idArquivo);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}	
	
	/**
	 * Retorna o plano movimentado
	 * @param mov
	 * @return
	 */
	private PlanoDocenciaAssistida getPlanoDocenciaAssistida(Movimento mov) {
		return ((MovimentoCadastro) mov).getObjMovimentado();
	}
	
	/**
	 * Retorna o dao do plano de docência assistida
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private PlanoDocenciaAssistidaDao getPlanoDocenciaDao(Movimento mov)	throws DAOException {
		return getDAO(PlanoDocenciaAssistidaDao.class, mov);
	}	
	
	/**
	 * Cria permissão para a Turma Virtual da turmas passada como parâmetro.
	 * @param turmas
	 * @param mov
	 * @throws DAOException
	 */
	private void criarPermissaoTurma(List<TurmaDocenciaAssistida> turmas, Movimento mov) throws DAOException{
		TurmaVirtualDao daoTurmaVirtual = getDAO(TurmaVirtualDao.class, mov);
		try {		
			// cria as permissões para utilizar as turmas virtuais.
			for (TurmaDocenciaAssistida turma : turmas){
				PermissaoAva permissao = daoTurmaVirtual.findPermissaoByPessoaTurma(getPlanoDocenciaAssistida(mov).getDiscente().getPessoa(), turma.getTurma());
				if (permissao == null){
					permissao = new PermissaoAva();
					permissao.setPessoa(getPlanoDocenciaAssistida(mov).getDiscente().getPessoa());							
					permissao.setTurma(turma.getTurma());
				}
				permissao.setDocente(false);
				permissao.setEnquete(true);
				permissao.setForum(true);
				permissao.setInserirArquivo(true);
				permissao.setTarefa(true);
				permissao.setCorrigirTarefa(true);							
				daoTurmaVirtual.createOrUpdate(permissao);									
			}
		} finally {
			if (daoTurmaVirtual != null)
				daoTurmaVirtual.close();							
		}			
	}	
	
	/**
	 * Notifica via email os responsáveis informados no Plano de Docência Assistida.
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void notificarResponsaveis(PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		CoordenacaoCursoDao dao =  getDAO(CoordenacaoCursoDao.class, mov);
		/* Corpo da Mensagem */
		StringBuilder msg = new StringBuilder();
		try{
			msg.append("<table style='width:100%'>");
			msg.append("  <tr>");
			msg.append("  	<td style='text-align:center;'><b><h2>MENSAGEM AUTOMÁTICA</h2></b></td>");
			msg.append("  </tr>");
			msg.append("  <tr>");
			msg.append("  	<td style='text-align:center;'>");
			msg.append("  	 Foi enviado à Coordenação de <b>"+plano.getDiscente().getGestoraAcademica().getNome() + "</b>, o ");
			msg.append(" Plano de Docência Assistida, conforme dados a seguir:");
			msg.append("  	</td>");		
			msg.append("  </tr>");		
			msg.append("  <tr><td><b>Nome:</b> "+plano.getDiscente().getNome()+"</td></tr>");
			msg.append("  <tr><td><b>Unidade:</b> "+plano.getDiscente().getGestoraAcademica().getNome()+"</td></tr>");
			
			OrientacaoAcademica orientador = DiscenteHelper.getUltimoOrientador(plano.getDiscente().getDiscente());		
			if (orientador != null)
				msg.append("  <tr><td><b>Orientador:</b> "+orientador.getDescricaoOrientador()+"</td></tr>");
			else
				msg.append("  <tr><td><b>Orientador:</b> SEM ORIENTADOR</td></tr>");
				
			msg.append("  <tr><td><b>Nível:</b> "+plano.getDiscente().getNivelDesc()+"</td></tr>");
			
			if (isEmpty(plano.getPeriodoIndicacaoBolsa()))		
				msg.append("  <tr><td><b>Ano/Período de Referência:</b> "+plano.getAno()+"."+plano.getPeriodo()+"</td></tr>");
			else
				msg.append("  <tr><td><b>Período da Indicação:</b> "+plano.getPeriodoIndicacaoBolsa().getAnoPeriodoFormatado()+"</td></tr>");
			if (plano.isBolsista() && (!isEmpty(plano.getModalidadeBolsa()) || !isEmpty(plano.getOutraModalidade()))){
				if (plano.getModalidadeBolsa() != null && plano.getModalidadeBolsa().getId() > 0){
					msg.append("  <tr><td><b>Bolsista:</b> Sim ("+plano.getModalidadeBolsa().getDescricao()+")</td></tr>");				
				} else {
					if (!plano.getOutraModalidade().trim().isEmpty())
						msg.append("  <tr><td><b>Bolsista:</b> Sim ("+plano.getOutraModalidade()+")</td></tr>");
				}
			} else {
				msg.append("  <tr><td><b>Bolsista:</b> Não</td></tr>");			
			}
			if (ValidatorUtil.isNotEmpty(plano.getComponenteCurricular())){
				
				ComponenteCurricular componente = dao.findByPrimaryKey(plano.getComponenteCurricular().getId(), ComponenteCurricular.class,
						"id","detalhes.nome","detalhes.chTotal", "codigo","unidade.nome", "tipoComponente.id");
				
				msg.append("  <tr><td><b>Componente Curricular:</b> "+componente.getDescricao()+"</td></tr>");
				
				msg.append("  <tr><td><b>Departamento:</b> "+componente.getUnidade().getNome()+"</td></tr>");
				
				if (componente.isAtividade() && ValidatorUtil.isNotEmpty(plano.getServidor()))
					msg.append("  <tr><td><b>Docente:</b> "+plano.getServidor().getNome()+"</td></tr>");
				
			} else {
				
				msg.append("  <tr><td><b>Componente Curricular: ** NÃO INFORMADO ** </b> ");
				
			}
			
			msg.append("  <tr><td><b>Curso:</b> "+plano.getCurso().getDescricao()+"</td></tr>");
			
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			if (!isEmpty(plano.getTurmaDocenciaAssistida())){
				plano.getTurmaDocenciaAssistida().iterator();
				msg.append("  <tr style='font-weight: bold;'><td>Turma(s):</td></tr>");
				msg.append("  <table style='width:80%; border:1px solid black;'><tr style='font-weight: bold;'><td>Turma</td><td>Docente(s)</td><td>Data Início</td><td>Data Fim</td></tr>");			
				for (TurmaDocenciaAssistida turma : plano.getTurmaDocenciaAssistida()){
					msg.append("  <tr>");
					msg.append("    <td>"+turma.getTurma().getCodigo() + " - " +turma.getTurma().getDisciplina().getNome() +"</td>");						
					msg.append("    <td>"+turma.getTurma().getDocentesNomes() +"</td>");
					msg.append("    <td>"+df.format(turma.getDataInicio())+"</td>");
					msg.append("    <td>"+df.format(turma.getDataFim())+"</td>");
					msg.append("  </tr>");						
				}
				msg.append("  </table>");
			}
			msg.append("  <tr><td></td></tr>");
			msg.append("  <tr><td><b>Justificativa:</b> "+plano.getJustificativa()+"</td></tr>");
			msg.append("  <tr><td><b>Objetivos:</b> "+plano.getObjetivos()+"</td></tr>");
			msg.append("  <tr><td></td></tr>");
			msg.append("  <tr style='font-weight: bold;'><td>Atividade(s):</td></tr>");
			msg.append("  <table style='width:80%; border:1px solid black;'>");
			for (AtividadeDocenciaAssistida atividade : plano.getAtividadeDocenciaAssistida()){
				msg.append("  <tr style='font-weight: bold;'><td>Atividade</td><td>Data Início</td><td>Data Fim</td><td>Frequência</td><td>Carga Horária</td></tr><tr>");
				if (atividade.getFormaAtuacao() != null && atividade.getFormaAtuacao().getId() > 0)
					msg.append("    <td>"+atividade.getFormaAtuacao().getDescricao() +"</td>");
				else
					msg.append("    <td>"+atividade.getOutraAtividade() +"</td>");				
				msg.append("    <td>"+df.format(atividade.getDataInicio())+"</td>");
				msg.append("    <td>"+df.format(atividade.getDataFim())+"</td>");
				if (ValidatorUtil.isNotEmpty(atividade.getFrequenciaAtividade()))
					msg.append("    <td>"+atividade.getFrequenciaAtividade().getDescricao()+"</td>");
				else
					msg.append("    <td>** Não Informado **</td>");
				msg.append("    <td>"+atividade.getCh() +"</td>");
				msg.append("  </tr>");
				msg.append("  <tr>");
				msg.append("    <td colspan='5'><b>Como Organizar:</b> "+atividade.getComoOrganizar()+"</td>");
				msg.append("  </tr>");
				msg.append("  <tr>");
				msg.append("    <td style='border-bottom: 1px solid black;' colspan='5'><b>Procedimentos:</b> "+atividade.getProcedimentos()+"</td>");
				msg.append("  </tr>");			
			}
			msg.append("  </table>");	

			CoordenacaoCurso coordenacao = dao.findUltimaByPrograma(plano.getDiscente().getGestoraAcademica());	
			msg.append("  <tr>");
			msg.append("  	<td style='text-align:center;'>");
			msg.append("<b>Qualquer discordância entrar em contato com o Coordenador do Programa");
			if (coordenacao != null)
				msg.append(", através do email: "+coordenacao.getServidor().getPessoa().getEmail()+" ou pelo telefone: "+coordenacao.getTelefoneContato1());
			msg.append(".</b></td></tr>");			
			
		} finally {
			if (dao != null)
				dao.close();
		}			
		
		msg.append("</table>");
		
		/* Envia email para o orientador do discente */
		enviarEmailOrientador(msg.toString(), plano, mov);
		/* Envia email para o Chefe do Departamento do componente curricular */
		enviarEmailChefeDepartemento(msg.toString(), plano, mov);
		/* Envia email para os Docentes das Turmas */
		enviarEmailDocentesTurmas(msg.toString(), plano, mov);
		/* Envia email para o coordenador do curso de graduação */
		enviarEmailCoordenadorCursoGraduacao(msg.toString(), plano, mov);
		/* Envia email para o coordenador do Programa da Pós */
		enviarEmailCoordenadorPrograma(msg.toString(), plano, mov);
	}	
	
	/**
	 * Cria a estrutura da mensagem de notificação
	 * @param titulo
	 * @param conteudo
	 * @return
	 */
	private Mensagem criarMensagem(String conteudo){
		Mensagem mensagem = new Mensagem();
		mensagem.setTitulo("Novo Plano de Docência Assistida foi Submetido");
		mensagem.setMensagem(conteudo);	
		return mensagem;
	}	
	
	/**
	 * Envia email para o Chefe do Departamento do componente curricular
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void enviarEmailChefeDepartemento(String msg, PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		ServidorDao dao = getDAO(ServidorDao.class, mov);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		try {
			Servidor servidor = dao.findChefeByDepartamento(plano.getComponenteCurricular().getUnidade().getId());
			if (servidor != null){
				
				List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
				Usuario usuario = uDao.findByPessoaParaEmail(servidor.getPessoa());
				if (usuario != null){
					
					UsuarioDTO uDTO = new UsuarioDTO();
					uDTO.setId(usuario.getId());
					uDTO.setLogin(usuario.getLogin());
					uDTO.setEmail(usuario.getEmail());
					uDTO.setIdPessoa(servidor.getPessoa().getId());
					uDTO.setNome(servidor.getPessoa().getNome());
					
					DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
					dDTO.setEmail(uDTO.getEmail());
					dDTO.setNome(uDTO.getNome());
					
					destinatarios.add(dDTO);
					enviarEmail(mov, criarMensagem(msg), destinatarios);
				}				
			}			
		} finally {
			if (dao != null)
				dao.close();
			if (uDao != null)
				uDao.close();
		}				
	}
	
	/**
	 *  Envia email para o orientador do discente 
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void enviarEmailOrientador(String msg, PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		OrientacaoAcademica orientador = DiscenteHelper.getUltimoOrientador(plano.getDiscente().getDiscente());		
		UsuarioDao dao = getDAO(UsuarioDao.class, mov);
		try {
			if (orientador != null){
				List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
				
				Usuario usuario = dao.findByPessoaParaEmail(orientador.getPessoa());
				if (usuario != null){
					
					UsuarioDTO uDTO = new UsuarioDTO();
					uDTO.setId(usuario.getId());
					uDTO.setLogin(usuario.getLogin());
					uDTO.setEmail(usuario.getEmail());
					uDTO.setIdPessoa(orientador.getPessoa().getId());
					uDTO.setNome(orientador.getPessoa().getNome());
					
					DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
					dDTO.setEmail(uDTO.getEmail());
					dDTO.setNome(uDTO.getNome());
					
					destinatarios.add(dDTO);
					enviarEmail(mov, criarMensagem(msg), destinatarios);
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Envia email para os Docentes das Turmas
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void enviarEmailDocentesTurmas(String msg, PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		try {
			
			List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
			if (!isEmpty(plano.getTurmaDocenciaAssistida())){
				for (TurmaDocenciaAssistida turma : plano.getTurmaDocenciaAssistida()){
					for (DocenteTurma docente : turma.getTurma().getDocentesTurmas()){						
						if (docente.getDocente() != null){
							Usuario usuario = uDao.findByPessoaParaEmail(docente.getDocente().getPessoa());							
							
							if( ValidatorUtil.isEmpty(usuario) ) {
								continue;
							}
							
							UsuarioDTO uDTO = new UsuarioDTO();
							uDTO.setId(usuario.getId());
							uDTO.setLogin(usuario.getLogin());
							uDTO.setEmail(usuario.getEmail());
							uDTO.setIdPessoa(docente.getDocente().getPessoa().getId());
							uDTO.setNome(docente.getDocente().getPessoa().getNome());							
							DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
							dDTO.setEmail(uDTO.getEmail());
							dDTO.setNome(uDTO.getNome());
							destinatarios.add(dDTO);
							
						}
					}
				}
			} else {
				if (plano.getComponenteCurricular().isAtividade() && !isEmpty(plano.getServidor())){
					Usuario usuario = uDao.findByPessoaParaEmail(plano.getServidor().getPessoa());
					if( ! ValidatorUtil.isEmpty(usuario) ) {
						UsuarioDTO uDTO = new UsuarioDTO();
						uDTO.setId(usuario.getId());
						uDTO.setLogin(usuario.getLogin());
						uDTO.setEmail(usuario.getEmail());
						uDTO.setIdPessoa(plano.getServidor().getPessoa().getId());
						uDTO.setNome(plano.getServidor().getPessoa().getNome());							
						DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
						dDTO.setEmail(uDTO.getEmail());
						dDTO.setNome(uDTO.getNome());
						destinatarios.add(dDTO);			
					}
				}
			}			
			
			enviarEmail(mov, criarMensagem(msg), destinatarios);
		} finally {
			if (uDao != null)
				uDao.close();
		}				
		
	}
	
	/**
	 * Envia email para o coordenador do curso de graduação
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void enviarEmailCoordenadorCursoGraduacao(String msg, PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, mov);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		try {
			CoordenacaoCurso coordenacao = dao.findAtivoByData(new Date(), plano.getCurso());	
			if (coordenacao != null){

				Usuario usuario = uDao.findByPessoaParaEmail(coordenacao.getServidor().getPessoa());
				if (usuario != null){
					List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
					UsuarioDTO uDTO = new UsuarioDTO();
					uDTO.setId(usuario.getId());
					uDTO.setLogin(usuario.getLogin());
					uDTO.setEmail(usuario.getEmail());
					uDTO.setIdPessoa(coordenacao.getServidor().getPessoa().getId());
					uDTO.setNome(coordenacao.getServidor().getPessoa().getNome());
					
					DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
					dDTO.setEmail(uDTO.getEmail());
					dDTO.setNome(uDTO.getNome());
					
					destinatarios.add(dDTO);
					enviarEmail(mov, criarMensagem(msg), destinatarios);
				}				
				
			}
		} finally {
			if (dao != null)
				dao.close();
			if (uDao != null)
				uDao.close();			
		}		
	}
	
	/**
	 * Envia email para o coordenador do Programa da Pós
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void enviarEmailCoordenadorPrograma(String msg, PlanoDocenciaAssistida plano, Movimento mov) throws ArqException, NegocioException, RemoteException{
		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, mov);
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		try {
			CoordenacaoCurso coordenacao = dao.findUltimaByPrograma(plano.getDiscente().getGestoraAcademica());	
			if (coordenacao != null){

				Usuario usuario = uDao.findByPessoaParaEmail(coordenacao.getServidor().getPessoa());
				if (usuario != null){
					List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
					UsuarioDTO uDTO = new UsuarioDTO();
					uDTO.setId(usuario.getId());
					uDTO.setLogin(usuario.getLogin());
					uDTO.setEmail(usuario.getEmail());
					uDTO.setIdPessoa(coordenacao.getServidor().getPessoa().getId());
					uDTO.setNome(coordenacao.getServidor().getPessoa().getNome());
					
					DestinatarioDTO dDTO = new DestinatarioDTO(uDTO);
					dDTO.setEmail(uDTO.getEmail());
					dDTO.setNome(uDTO.getNome());
					
					destinatarios.add(dDTO);
					enviarEmail(mov, criarMensagem(msg), destinatarios);
				}				
				
			}
		} finally {
			if (dao != null)
				dao.close();
			if (uDao != null)
				uDao.close();			
		}			
	}	
	
	/**
	 * Notifica os Usuários que foi criado um Plano de Docência Assistida.
	 * @param rpMov
	 * @param usuarios
	 * @throws RemoteException  
	 * @throws NegocioException 
	 */
	private void enviarEmail(Movimento mov, Mensagem mensagem, List<DestinatarioDTO> destinatarios) throws ArqException, NegocioException, RemoteException {				
		// enviando notificações de e-mail.		
		if (ValidatorUtil.isNotEmpty(destinatarios)){
			NotificacaoDTO notificacao = new NotificacaoDTO();
			notificacao.setDestinatarios(destinatarios);
			notificacao.setDestinatariosMsg(destinatarios);
			
			String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
			notificacao.setNomeRemetente(siglaSigaa+" - Docência Assistida");
			
			notificacao.setTitulo(mensagem.getTitulo());
			
			notificacao.setMensagem(mensagem.getMensagem());
			notificacao.setContentType(MailBody.HTML);
			notificacao.setEnviarEmail(true);
			notificacao.setEnviarMensagem(true);
			notificacao.setAutorizado(true);
			
			try {
				EnvioNotificacoesRemoteService enviador = getBean("envioNotificacoesInvoker", mov);
				enviador.enviar(DtoUtils.deUsuarioParaDTO(mov.getUsuarioLogado()), notificacao);
			} catch (NegocioRemotoException e) {
				throw new NegocioException(e.getMessage());
			}						
		}
	}	
	
	/**
	 * Valida os dados
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		if (SigaaListaComando.CADASTRAR_INDICACAO_BOLSAS_REUNI.equals(mov.getCodMovimento())){
			IndicacaoBolsistaReuni indicacao = ((MovimentoCadastro) mov).getObjMovimentado();
			checkValidation( indicacao.validate() );			
		} else if (SigaaListaComando.CADASTRAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento()) || SigaaListaComando.ALTERAR_PLANO_DOCENCIA_ASSISTIDA.equals(mov.getCodMovimento())){
			PlanoDocenciaAssistida plano = getPlanoDocenciaAssistida(mov);
			PlanoDocenciaAssistidaDao daoPlano = getPlanoDocenciaDao(mov);
			
			try{
				if (!plano.getComponenteCurricular().isAtividade())
					plano.setServidor(null);
				
				if (ValidatorUtil.isEmpty(plano.getTurmaDocenciaAssistida())) {
					plano.setTurmaDocenciaAssistida(null);
				}
				
				if (plano.isSubmetido())
					checkValidation( plano.validate() );			
				else {												
					if (ValidatorUtil.isEmpty(plano.getAtividadeDocenciaAssistida())){
						plano.setAtividadeDocenciaAssistida(null);
					}
					
					if (ValidatorUtil.isEmpty(plano.getComponenteCurricular())){
						plano.setComponenteCurricular(null);
					}
					
					if (ValidatorUtil.isEmpty(plano.getCurso())){
						plano.setCurso(null);
					} 					
				}	
				
				if (plano.getOutraModalidade() != null || plano.getModalidadeBolsa() != null){
					if (plano.getModalidadeBolsa() != null && plano.getModalidadeBolsa().getId() > 0){
						plano.setModalidadeBolsa(daoPlano.refresh(plano.getModalidadeBolsa()));
						plano.setOutraModalidade(null);				 
					} else
						plano.setModalidadeBolsa(null);
				} else {			
					plano.setModalidadeBolsa(null);
					plano.setOutraModalidade(null);
				}										
			} finally {
				if (daoPlano != null)
					daoPlano.close();
			}
		}		
	}

}
