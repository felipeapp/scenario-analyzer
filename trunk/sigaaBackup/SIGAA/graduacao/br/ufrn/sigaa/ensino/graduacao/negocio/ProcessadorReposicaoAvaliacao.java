/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/05/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.DtoUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.DestinatarioDTO;
import br.ufrn.integracao.dto.NotificacaoDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import br.ufrn.integracao.interfaces.EnvioNotificacoesRemoteService;
import br.ufrn.sigaa.arq.dao.ensino.SolicitacaoReposicaoAvaliacaoDao;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoReposicaoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.StatusReposicaoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Este processador é responsável por Gerenciar as Operações de Solicitação de Reposição de Prova.
 * 
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorReposicaoAvaliacao extends AbstractProcessador {

	/**
	 * Executa o processador
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoReposicaoProva rpMov = (MovimentoReposicaoProva) mov;
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class, rpMov);
		try{			
			if (rpMov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_REPOSICAO_PROVA)) {							
				cadastrar(rpMov);										
			} else if (rpMov.getCodMovimento().equals(SigaaListaComando.HOMOLOGAR_REPOSICAO_PROVA)) {	
				homologar(rpMov);				
			} else if (rpMov.getCodMovimento().equals(SigaaListaComando.PARECER_REPOSICAO_PROVA)) {				
				registrarParecer(rpMov);									
			} else if (rpMov.getCodMovimento().equals(SigaaListaComando.CANCELAR_REPOSICAO_PROVA)) {
				SolicitacaoReposicaoAvaliacao rp = rpMov.getSolicitacaoReposicaoProva();
				rp.setStatus(new StatusReposicaoAvaliacao(rpMov.getNovoStatus()));
				rp.setDataCancelamento(new Date());
				rp.setRegistroCancelamento(rpMov.getUsuarioLogado().getRegistroEntrada());
				dao.update(rp);
			}
		} catch(NegocioRemotoException e) {
			throw new NegocioException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}		
		return rpMov.getSolicitacaoReposicaoProva();
	}

	/**
	 * Registra o parecer do docente
	 * @param rpMov
	 * @param dao
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws NegocioRemotoException 
	 */
	private void registrarParecer(MovimentoReposicaoProva rpMov) throws ArqException, NegocioException, RemoteException, NegocioRemotoException {
		
		List<SolicitacaoReposicaoAvaliacao> solicitacoes = new ArrayList<SolicitacaoReposicaoAvaliacao>();
		List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
		List<DestinatarioDTO> destinatariosDiscentes = new ArrayList<DestinatarioDTO>();
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class, rpMov);
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		
		try {
			for (SolicitacaoReposicaoAvaliacao srp : rpMov.getSolicitacoes()){
				srp = dao.refresh(srp);
				srp.setObservacaoDocente(rpMov.getObservacao());			
				srp.setStatusParecer(new StatusReposicaoAvaliacao(rpMov.getNovoStatus()));
				srp.setLocalProva(rpMov.getSolicitacaoReposicaoProva().getLocalProva());
				srp.setDataProvaSugerida(rpMov.getSolicitacaoReposicaoProva().getDataProvaSugerida());
				dao.updateNoFlush(srp);
				
				solicitacoes.add(srp);
				
				//Notifica o Aluno			
				DestinatarioDTO d = new DestinatarioDTO(srp.getDiscente().getNome(), srp.getDiscente().getPessoa().getEmail() );
				destinatariosDiscentes.add(d);	
				
				//Adiciona os usuários dos chefes que serão notificados
				Pessoa pessoa = findChefeDepartamento(srp, rpMov);
				if (pessoa != null && !pessoas.contains(pessoa))
					pessoas.add(pessoa);			
			}
			
			if (!ValidatorUtil.isEmpty(pessoas)){
				for (Pessoa p : pessoas){
					DestinatarioDTO d = new DestinatarioDTO(p.getNome(), p.getEmail() );
					destinatarios.add(d);
				}				
				notificar(rpMov, criarMensagemParecerDocente(solicitacoes), destinatarios);				
			}
			
			if (!ValidatorUtil.isEmpty(destinatariosDiscentes))
				notificarHomologacaoDocenteDiscentes(rpMov, solicitacoes);
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Homologa as solicitações de reposição
	 * @param rpMov
	 * @param dao
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws NegocioRemotoException 
	 */
	private void homologar(MovimentoReposicaoProva rpMov) throws DAOException,
			ArqException, NegocioException, RemoteException, NegocioRemotoException {
		
		List<SolicitacaoReposicaoAvaliacao> solicitacoes = new ArrayList<SolicitacaoReposicaoAvaliacao>();
		List<DestinatarioDTO> destinatariosDiscentes = new ArrayList<DestinatarioDTO>();
		List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class, rpMov);
		
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		try {
			for (SolicitacaoReposicaoAvaliacao srp : rpMov.getSolicitacoes()){
				srp = dao.refresh(srp);
				srp.setObservacaoHomologacao(rpMov.getObservacao());			
				srp.setStatus(new StatusReposicaoAvaliacao(rpMov.getNovoStatus()));
				srp.setHomologado(true);
				dao.updateNoFlush(srp);
				
				//Notifica o Aluno			
				DestinatarioDTO d = new DestinatarioDTO(srp.getDiscente().getNome(), srp.getDiscente().getPessoa().getEmail() );
				destinatariosDiscentes.add(d);		
				
				//Adiciona os usuários do docentes
				List<Pessoa> p = findDocentesTurma(srp, rpMov);
				if (!ValidatorUtil.isEmpty(p))
					pessoas.addAll(p);
			
				solicitacoes.add(srp);
			}	
			
			if (!ValidatorUtil.isEmpty(destinatariosDiscentes))
				notificarDiscentes(rpMov, solicitacoes);		
			
			if (!ValidatorUtil.isEmpty(pessoas)){
				for (Pessoa p : pessoas){
					DestinatarioDTO d = new DestinatarioDTO(p.getNome(), p.getEmail() );
					destinatarios.add(d);
				}							
				notificar(rpMov, criarMensagemHomologacaoDocentes(solicitacoes), destinatarios);							
			}
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}

	/**
	 * Cadastra a solicitação de repodição de avaliação
	 * @param rpMov
	 * @param dao
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException
	 * @throws NegocioRemotoException 
	 */
	private void cadastrar(MovimentoReposicaoProva rpMov) throws DAOException, ArqException,
			NegocioException, RemoteException, NegocioRemotoException {
		
		SolicitacaoReposicaoAvaliacao rp = rpMov.getSolicitacaoReposicaoProva();
		SolicitacaoReposicaoAvaliacaoDao dao = getDAO(SolicitacaoReposicaoAvaliacaoDao.class, rpMov);
		try {
			if (rpMov.getArquivo() != null){
				// salva o arquivo anexado
				try {
					int idArquivo = EnvioArquivoHelper.getNextIdArquivo();			
					EnvioArquivoHelper.inserirArquivo(idArquivo, rpMov.getArquivo().getBytes(), rpMov.getArquivo().getContentType(), rpMov.getArquivo().getName());
					rp.setIdArquivo(idArquivo);									
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			rp.setStatus(new StatusReposicaoAvaliacao(rpMov.getNovoStatus()));
			
			dao.createOrUpdate(rp);
			
			//Notifica os docentes da criação da solicitação de repodição da avaliação
			List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
			List<Pessoa> pessoas = findDocentesTurma(rp, rpMov);
			if (!ValidatorUtil.isEmpty(pessoas)){
				for (Pessoa p : pessoas){
					DestinatarioDTO d = new DestinatarioDTO(p.getNome(), p.getEmail() );
					destinatarios.add(d);
				}					
				notificar(rpMov, criarMensagemSolicitacao(rp), destinatarios);							
			}
		} finally {
			if (dao != null)
				dao.close();
		}

	}

	/**
	 * Valida
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

	
	/**
	 * Cria a mensagem que será enviada aos Docentes
	 * @param rp
	 * @return
	 */
	private Mensagem criarMensagemSolicitacao(SolicitacaoReposicaoAvaliacao rp) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String discente = rp.getDiscente().getNome();
		long matricula = rp.getDiscente().getMatricula();
		String turma = rp.getTurma().getDisciplina().getNome();
		String avaliacao = rp.getDataAvaliacao().getDescricao();
		String curso = rp.getDiscente().getCurso().getDescricao();
		Date dataAvaliacao = rp.getDataAvaliacao().getData();
		String justificativa = rp.getJustificativa();
		String titulo = discente+" Solicitou Reposição de Prova";
				
		String conteudo = "O(a) aluno(a) "+discente+" de matrícula Nº "+matricula+", do curso de "+curso+
		", solicitou Reposição da "+avaliacao+" do Componente Curricular "+turma+", que foi realizada no dia "+sdf.format(dataAvaliacao)+".";
		
		conteudo += "<br/><br/> Justificativa da Solicitação: "+justificativa;
		
		if (rp.getIdArquivo() != null)
			conteudo += "<br/><br/> <b>Um arquivo foi anexado à solicitação.</b>";
		
		int dias = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QUANTIDADE_MAXIMA_DIAS_REPOSICAO_PROVA);
		if (dias > 0)
			conteudo += "<br/><b>ATENÇÃO!</b> Só será possível analisar as solicitações de reposição após "+
						dias+" dias úteis contados a partir da data de realização da avaliação.";
				
		return criarMensagem(titulo, conteudo);
	}	
	
	/**
	 * Cria a estrutura da mensagem de notificação
	 * @param titulo
	 * @param conteudo
	 * @return
	 */
	private Mensagem criarMensagem(String titulo, String conteudo){
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(conteudo);	
		return mensagem;
	}
	
	/**
	 * Cria a Mensagem que será enviada para o Discente ao ser Homologada a Reposição de Prova.
	 * @param rp
	 * @return
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws NegocioRemotoException 
	 */
	private void notificarDiscentes(Movimento mov, List<SolicitacaoReposicaoAvaliacao> solicitacoes) throws ArqException, NegocioException, RemoteException, NegocioRemotoException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String titulo = "Parecer da Solicitação de Reposição de Prova";
		
		for (SolicitacaoReposicaoAvaliacao rp : solicitacoes){
			String disciplina = rp.getTurma().getDisciplina().getNome();
			
			String conteudo;		
			conteudo = "Sua Solicitação de Reposição de Prova, que foi realizada no dia "+sdf.format(rp.getDataCadastro())+
			", referente o Componente Curricular <b>"+disciplina +"</b> e Avaliação <b>"+rp.getDataAvaliacao().getDescricao()+"</b>, foi";
			if (rp.isDeferido()){
				conteudo += " <b>Deferida</b>,";
				
				if (rp.getStatusParecer() != null && rp.getDataProvaSugerida() != null){
					conteudo += " e a prova foi marcada para o dia <b>"+sdf.format(rp.getDataProvaSugerida())+" às "+
					Formatador.getInstance().formatarHora(rp.getDataProvaSugerida())+"</b>, que será realizada no seguinte Local: <b>"+
					rp.getLocalProva()+"</b>.";				
				} else {
					conteudo += " entre em contato com o seu professor para marcar horário e local da avaliação. ";
				}
				
			}
			
			if (!ValidatorUtil.isEmpty(rp.getObservacaoHomologacao())){
				if (rp.isDeferido())
					conteudo += "<br/><br/>Observação: <br/>";	
				else {
					conteudo += " <b>Indeferida</b>.";
					conteudo += "<br/><br/>Motivo: <br/>";										
				}
				conteudo += "<p>"+rp.getObservacaoHomologacao()+"</p>";
			}
			
			List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
			destinatarios.add(new DestinatarioDTO(rp.getDiscente().getNome(), rp.getDiscente().getPessoa().getEmail() ));
			notificar(mov, criarMensagem(titulo, conteudo), destinatarios ); 
		}
		
	}
	
	/**
	 * Cria a Mensagem que será enviada para o Discente após análise do docente
	 * @param rp
	 * @return
	 * @throws RemoteException 
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws NegocioRemotoException 
	 */
	private void notificarHomologacaoDocenteDiscentes(Movimento mov, List<SolicitacaoReposicaoAvaliacao> solicitacoes) throws ArqException, NegocioException, RemoteException, NegocioRemotoException{
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String titulo = "Parecer da Solicitação de Reposição de Prova";
		
		for (SolicitacaoReposicaoAvaliacao rp : solicitacoes){
			String disciplina = rp.getTurma().getDisciplina().getNome();
			
			String conteudo;		
			conteudo = "Sua Solicitação de Reposição de Prova, que foi realizada no dia "+sdf.format(rp.getDataCadastro())+
			", referente o Componente Curricular <b>"+disciplina +"</b> e Avaliação <b>"+rp.getDataAvaliacao().getDescricao()+"</b>, foi";
			if (rp.getStatusParecer().isDeferido())
				conteudo += " <b>Deferida</b> pelo docente e aguarda análise da chefia do departamento.";
			
			if (!ValidatorUtil.isEmpty(rp.getObservacaoDocente())){
				if (rp.getStatusParecer().isDeferido())
					conteudo += "<br/><br/>Observação: <br/>";	
				else {
					conteudo += " <b>Indeferida</b>.";
					conteudo += "<br/><br/>Motivo: <br/>";										
				}
			}
			List<DestinatarioDTO> destinatarios = new ArrayList<DestinatarioDTO>();
			destinatarios.add(new DestinatarioDTO(rp.getDiscente().getNome(), rp.getDiscente().getPessoa().getEmail() ));
			notificar(mov, criarMensagem(titulo, conteudo), destinatarios ); 
		}
		
	}
	
	/**
	 * Cria a Mensagem que será enviada os chefes.
	 * @param rp
	 * @return
	 */
	private Mensagem criarMensagemParecerDocente(List<SolicitacaoReposicaoAvaliacao> srp){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String disciplina;
		String titulo = "Solicitação de Reposição de Prova";
		String conteudo = "A(s) seguinte(s) Solicitação(ões) de Reposição de Prova foram analisadas pelo docente e aguardam homologação da chefia: ";
		String lista = "<br/><br/><table border='1' width='90%'>";
		lista += "<tr>";
		lista += "  <td>";
		lista +=     "<b>Discente</b>";
		lista += "  </td>";		
		lista += "  <td>";
		lista +=     "<b>Curso</b>";
		lista += "  </td>";	
		lista += "  <td>";
		lista +=     "<b>Avaliação</b>";
		lista += "  </td>";	
		lista += "  <td>";
		lista +=     "<b>Data/Hora da Prova</b>";
		lista += "  </td>";		
		lista += "  <td>";
		lista +=     "<b>Local da Prova</b>";
		lista += "  </td>";			
		lista += "</tr>";		
		int idTurma = 0;
		for (SolicitacaoReposicaoAvaliacao rp : srp){
			if (idTurma != rp.getTurma().getId()){
				idTurma = rp.getTurma().getId();
				disciplina = rp.getTurma().getNome();			
				lista += "<tr>";
				lista += "  <td colspan='5'>";
				lista +=     "<b>"+disciplina+"</b>";
				lista += "  </td>";						
				lista += "</tr>";
			}			
			lista += "<tr>";
			lista += "  <td>";
			lista +=     rp.getDiscente().getMatriculaNome();
			lista += "  </td>";		
			lista += "  <td>";
			lista +=     rp.getDiscente().getCurso().getDescricao();
			lista += "  </td>";	
			lista += "  <td>";
			lista +=     rp.getDataAvaliacao().getDescricao();
			lista += "  </td>";	
			if (rp.getStatusParecer().isDeferido()){
				if (rp.getDataProvaSugerida() != null){
					lista += "  <td>";
					lista +=  sdf.format(rp.getDataProvaSugerida()) + " às "+ Formatador.getInstance().formatarHora(rp.getDataProvaSugerida());
					lista += "  </td>";
					lista += "  <td>";
					lista +=     rp.getLocalProva();
					lista += "  </td>";						
					lista += "</tr>";									
				} else {
					lista += "  <td> A ser definido </td>";
					lista += "  <td> A ser definido </td>";
					lista += "</tr>";							
				}
			}else{
				lista += "  <td>";
				lista +=  "<span style='color:red;'><b>INDEFERIDO</b></span>";
				lista += "  </td>";
				lista += "  <td>";
				lista +=  "<span style='color:red;'><b>-</b></span>";
				lista += "  </td>";					
				lista += "</tr>";							
				lista += "<tr>";
				lista += "  <td colspan='5'>";
				lista += "Motivo: <p>"+rp.getObservacaoDocente()+"</p>";
				lista += "  </td>";
				lista += "</tr>";				
			}							
		}
		lista += "</table>";
		
		conteudo += lista;
		
		return criarMensagem(titulo, conteudo); 
	}	
	
	/**
	 * Cria a Mensagem que será enviada para o Docente ao ser Homologada a Reposição de Prova.
	 * @param rp
	 * @return
	 */
	private Mensagem criarMensagemHomologacaoDocentes(List<SolicitacaoReposicaoAvaliacao> srp){
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		String disciplina;
		String titulo = "Parecer de Solicitação de Reposição de Prova";
		String conteudo;
		conteudo = "Foi(ram) analisada(s) a(s) seguinte(s) Solicitação(ões) de Reposição de Avaliação: ";
		String lista = "<br/><br/><table border='1' width='90%'>";
		lista += "<tr>";
		lista += "  <td>";
		lista +=     "<b>Discente</b>";
		lista += "  </td>";		
		lista += "  <td>";
		lista +=     "<b>Curso</b>";
		lista += "  </td>";	
		lista += "  <td>";
		lista +=     "<b>Avaliação</b>";
		lista += "  </td>";	
		lista += "  <td>";
		lista +=     "<b>Data/Hora da Prova</b>";
		lista += "  </td>";		
		lista += "  <td>";
		lista +=     "<b>Local da Prova</b>";
		lista += "  </td>";			
		lista += "</tr>";		
		int idTurma = 0;
		for (SolicitacaoReposicaoAvaliacao rp : srp){
			if (idTurma != rp.getTurma().getId()){
				idTurma = rp.getTurma().getId();
				disciplina = rp.getTurma().getNome();
				lista += "<tr>";
				lista += "  <td colspan='5'>";
				lista +=     "<b>"+disciplina+"</b>";
				lista += "  </td>";						
				lista += "</tr>";
			}			
			lista += "<tr>";
			lista += "  <td>";
			lista +=     rp.getDiscente().getMatriculaNome();
			lista += "  </td>";		
			lista += "  <td>";
			lista +=     rp.getDiscente().getCurso().getDescricao();
			lista += "  </td>";	
			lista += "  <td>";
			lista +=     rp.getDataAvaliacao().getDescricao();
			lista += "  </td>";	
			if (rp.isDeferido()){
				if (rp.getDataProvaSugerida() != null){
					lista += "  <td>";
					lista +=  sdf.format(rp.getDataProvaSugerida()) + " às "+ Formatador.getInstance().formatarHora(rp.getDataProvaSugerida());
					lista += "  </td>";
					lista += "  <td>";
					lista +=     rp.getLocalProva();
					lista += "  </td>";						
					lista += "</tr>";									
				} else {
					lista += "  <td> A ser definido </td>";
					lista += "  <td> A ser definido </td>";
					lista += "</tr>";							
				}
			}else{
				lista += "  <td>";
				lista +=  "<span style='color:red;'><b>INDEFERIDO</b></span>";
				lista += "  </td>";
				lista += "  <td>";
				lista +=  "<span style='color:red;'><b>-</b></span>";
				lista += "  </td>";					
				lista += "</tr>";							
				lista += "<tr>";
				lista += "  <td colspan='5'>";
				lista += "Motivo: <p>"+rp.getObservacaoHomologacao()+"</p>";
				lista += "  </td>";
				lista += "</tr>";				
			}							
		}
		lista += "</table>";
		
		conteudo += lista;
		
		return criarMensagem(titulo, conteudo); 
	}		
	
	/**
	 * Retorna o chefe de departamento ao qual a turma pertence
	 * @param rpMov
	 * @return
	 * @throws DAOException
	 */
	private Pessoa findChefeDepartamento(SolicitacaoReposicaoAvaliacao rp, MovimentoReposicaoProva rpMov) throws DAOException {			
		Unidade unidade = rp.getTurma().getDisciplina().getUnidade();
		
		ServidorDao dao = getDAO(ServidorDao.class, rpMov);
		Pessoa p = null;
		try {
			Servidor chefe = null;
			if (unidade.isUnidadeAcademicaEspecializada())
				chefe = dao.findDiretorByUnidade(unidade.getId());
			else
				chefe = dao.findChefeByDepartamento(unidade.getId());
				
			if (chefe != null) p = chefe.getPessoa();
		} finally {
			if (dao != null)
				dao.close();
		}
		return p;
	}	
	
	/**
	 * Retorna o chefe de departamento ao qual a turma pertence
	 * @param rpMov
	 * @return
	 * @throws DAOException
	 */
	private List<Pessoa> findDocentesTurma(SolicitacaoReposicaoAvaliacao rp, MovimentoReposicaoProva rpMov) throws DAOException {			
		Turma turma = getGenericDAO(rpMov).refresh(rp.getTurma());
		turma.getDocentesTurmas().iterator();
		
		List<Pessoa> destinatarios = new ArrayList<Pessoa>();
		for (DocenteTurma d : turma.getDocentesTurmas()){
			Pessoa p = null; 
			if (d.getDocente() == null || d.getDocente().getId() == 0)
				p = d.getDocenteExterno().getPessoa();
			else if (d.getDocenteExterno() == null || d.getDocenteExterno().getId() == 0)
				p = d.getDocente().getPessoa();
			
			if (p != null)
				destinatarios.add(p);					
		}
		return destinatarios;
	}		
	
	/**
	 * Notifica os Usuários que foi criado uma solicitação de reposição de prova.
	 * @param rpMov
	 * @param usuarios
	 * @throws RemoteException  
	 * @throws NegocioException 
	 * @throws NegocioRemotoException 
	 */
	private void notificar(Movimento mov, Mensagem mensagem, List<DestinatarioDTO> destinatarios) throws ArqException, NegocioException, RemoteException, NegocioRemotoException {				
		if (destinatarios == null) 
			throw new IllegalArgumentException();
			
		NotificacaoDTO not = new NotificacaoDTO();
		not.setEnviarEmail(true);
		not.setEnviarMensagem(true);
		not.setDestinatarios(destinatarios);
		not.setTitulo(mensagem.getTitulo());
		not.setMensagem(mensagem.getMensagem());
		not.setAutorizado(true);
		not.setContentType(MailBody.HTML);
		not.setNomeRemetente("Sistemas/"+RepositorioDadosInstitucionais.getSiglaInstituicao());
		
		// Preparar cadastro
		EnvioNotificacoesRemoteService enviador = getBean("envioNotificacoesInvoker", mov);
		enviador.enviar(DtoUtils.deUsuarioParaDTO(mov.getUsuarioLogado()), not);	
	}	
}
