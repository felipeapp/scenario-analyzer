/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.nee.negocio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrm.sigaa.nee.dao.NeeDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.nee.dominio.SolicitacaoApoioNee;
import br.ufrn.sigaa.nee.dominio.StatusAtendimento;
import br.ufrn.sigaa.nee.dominio.TipoNecessidadeSolicitacaoNee;
import br.ufrn.sigaa.negocio.ProcessadorPessoaNecessidadeEspecial;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador para as opera��es relacionadas ao {@link SolicitacaoApoioNee}.
 * @author Rafael Gomes
 *
 */
public class ProcessadorSolicitacaoApoioNee extends ProcessadorCadastro{
	
	public Object execute(Movimento mov) throws NegocioException, ArqException {
		SolicitacaoApoioNee solicitacaoNee = ((MovimentoCadastro)mov).getObjMovimentado();
		@SuppressWarnings("unchecked")
		List<Integer> tiposNEE = (List<Integer>) ((MovimentoCadastro)mov).getObjAuxiliar();
		DiscenteAdapter discenteAdapter = new Discente();
		NeeDao dao = getDAO(NeeDao.class, mov);
		
		try{
			if (solicitacaoNee.getParecerAtivo() != null && solicitacaoNee.getParecerAtivo() == false){
				solicitacaoNee.setStatusAtendimento(new StatusAtendimento(StatusAtendimento.CANCELADO));
			}
			
			validate(mov);
			
			solicitacaoNee.setCodigoCadastro(dao.nextValueCodigoDiscenteNee());
			
			discenteAdapter = solicitacaoNee.getDiscente();
			solicitacaoNee.setDiscente(solicitacaoNee.getDiscente().getDiscente());
		
			if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE) )
				solicitacaoNee.setLida(false);
			
			
			dao.create(solicitacaoNee);
			cadastrarNecessidadesEspeciaisAluno(solicitacaoNee, tiposNEE, mov);
			if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE)
					|| mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_PARECER_SOLICITACAO_NEE) 
					|| (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SOLICITACAO_NEE) 
							&& solicitacaoNee.getStatusAtendimento().getId() == StatusAtendimento.SUBMETIDO) ){
				removerNecessidadesEspeciaisAluno(solicitacaoNee, tiposNEE,dao );
			}
			
			/* Cadastrar ou remover necessidades especiais do aluno, relacionando na entidade PessoaNecessidadeEspecial.*/
			List<Object> neeSelecionadas = new ArrayList<Object>();
			for (Object tipo : tiposNEE) {
				neeSelecionadas.add(tipo.toString());
			}
			ProcessadorPessoaNecessidadeEspecial processadorPessoaNEE = new ProcessadorPessoaNecessidadeEspecial();
			processadorPessoaNEE.cadastrarNecessidadesEspeciaisAluno(solicitacaoNee.getDiscente(), neeSelecionadas, mov);
			processadorPessoaNEE.removerNecessidadesEspeciaisAluno(solicitacaoNee.getDiscente(), neeSelecionadas, dao);
			
			
			if (solicitacaoNee.getParecerAtivo() != null && solicitacaoNee.getParecerAtivo() == false){
				enviarEmailNotificacaoCoordenacao(mov);
			}
			solicitacaoNee.setDiscente(discenteAdapter);
		} finally{
			dao.close();
		}
		return solicitacaoNee;
	}
	
	/**
	 * M�todo utilizado por persistir as necessidades especiais do aluno.
	 * @param solicitacaoNee
	 * @param tiposNEE
	 * @param mov
	 * @throws DAOException
	 */
	public void cadastrarNecessidadesEspeciaisAluno(SolicitacaoApoioNee solicitacaoNee, List<Integer> tiposNEE, Movimento mov) throws DAOException{
		NeeDao dao = getDAO(NeeDao.class, mov);
		try {
			for (Object idTipo : tiposNEE) {
				TipoNecessidadeSolicitacaoNee tipoNeeDiscente = new TipoNecessidadeSolicitacaoNee();
				tipoNeeDiscente.setSolicitacaoApoioNEE(solicitacaoNee);
				tipoNeeDiscente.setTipoNecessidadeEspecial(dao.findByPrimaryKey(Integer.valueOf( (String) idTipo ), TipoNecessidadeEspecial.class));
				if ( !existeNecessidadeEspecial(solicitacaoNee, tipoNeeDiscente, mov) )
					dao.createOrUpdate(tipoNeeDiscente);
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * M�todo invocado para remover as necessidades especiais do aluno,
	 * para isso a necessidade se torna inativa.
	 * @param solicitacaoNee
	 * @param tiposNEE
	 * @param mov
	 * @throws DAOException
	 */
	public void removerNecessidadesEspeciaisAluno(SolicitacaoApoioNee solicitacaoNee, List<Integer> tiposNEE, NeeDao dao) throws DAOException{
	
		Collection<TipoNecessidadeSolicitacaoNee> colNecessidadesDiscente = dao.findNecessidadesEspeciaisBySolicitacaoNee(solicitacaoNee.getId());
		for (TipoNecessidadeSolicitacaoNee t : colNecessidadesDiscente) {
			if ( !tiposNEE.contains(new Integer(t.getTipoNecessidadeEspecial().getId()).toString() ) ){
				t.setAtivo(false);
				dao.createOrUpdate(t);
			}
		}
	
	}
	
	/**
	 * M�todo respons�vel por verificar se o aluno j� possui a necessidade Especial em sua 
	 * lista de necessidades especiais cadastradas em banco.
	 * @param solicitacaoNee
	 * @param tipoNeeNova
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	public boolean existeNecessidadeEspecial(SolicitacaoApoioNee solicitacaoNee, TipoNecessidadeSolicitacaoNee tipoNeeNova, Movimento mov) throws DAOException{
		NeeDao dao = getDAO(NeeDao.class, mov);
		boolean existeNecessidadeEspecial = false;
		try {
			Collection<TipoNecessidadeSolicitacaoNee> colTipos = dao.findNecessidadesEspeciaisBySolicitacaoNee(solicitacaoNee.getId());
			for (TipoNecessidadeSolicitacaoNee tipoNeeAtual : colTipos) {
				if ( tipoNeeAtual.getTipoNecessidadeEspecial().getId() == tipoNeeNova.getTipoNecessidadeEspecial().getId() )
					existeNecessidadeEspecial = true;
			}
		} finally {
			dao.close();
		}
		return existeNecessidadeEspecial;
	}

	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens mensagens = new ListaMensagens();
		NeeDao dao = getDAO(NeeDao.class, mov);
		SolicitacaoApoioNee solicitacaoNee = ((MovimentoCadastro)mov).getObjMovimentado();
		@SuppressWarnings("unchecked")
		List<Object> tiposNEE = (List<Object>) ((MovimentoCadastro)mov).getObjAuxiliar();
		 
		if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SOLICITACAO_NEE) &&
				dao.existeSolicitacaoAtivasByDiscente(solicitacaoNee.getDiscente().getId()) ){
			throw new NegocioException("O aluno j� possui solicita��o de apoio Submetida a CAENE ou Em Atendimento.");
		}
		
		if ( mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SOLICITACAO_NEE) &&
				solicitacaoNee.getStatusAtendimento().getId() != StatusAtendimento.SUBMETIDO ){
			throw new NegocioException("N�o � poss�vel alterar uma solicita��o, com situa��o diferente de Submetido a CAENE.");
		}
		
		if ( solicitacaoNee.getDiscente().getPessoa().getTipoNecessidadeEspecial() != null && !tiposNEE.contains( 
				new Integer( solicitacaoNee.getDiscente().getPessoa().getTipoNecessidadeEspecial().getId()).toString()) ){
			mensagens.addInformation("N�o foi poss�vel remover a Necessidade Especial definida durante a inscri��o do vestibular.");
			tiposNEE.add( 
					new Integer( solicitacaoNee.getDiscente().getPessoa().getTipoNecessidadeEspecial().getId()).toString() );
		}
		
		if ( solicitacaoNee.getStatusAtendimento().getId() == StatusAtendimento.EM_ATENDIMENTO && ValidatorUtil.isEmpty(solicitacaoNee.getParecerComissao()) ){
			throw new NegocioException("Imposs�vel Adicionar Situa��o de Em Atendimento para solicita��o sem antes informar o Parecer.");
		}
		
		if ( !ValidatorUtil.isEmpty(solicitacaoNee.getParecerComissao()) && (solicitacaoNee.getStatusAtendimento().getId() == StatusAtendimento.SUBMETIDO) ){
			mensagens.addInformation("A situa��o do atendimento encontra-se como SUBMETIDO, impossibilitanto a visualiza��o do parecer pelos Docentes do aluno.");
		}
		
		if ( mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PARECER_SOLICITACAO_NEE) && 
				ValidatorUtil.isEmpty(solicitacaoNee.getParecerAtivo()) && 
				solicitacaoNee.getStatusAtendimento().getId() != StatusAtendimento.SUBMETIDO ){
			throw new NegocioException("Favor informar o parecer da solicita��o.");
		}	
		
		if ( !ValidatorUtil.isEmpty(solicitacaoNee.getParecerAtivo()) && !solicitacaoNee.getParecerAtivo().booleanValue() )
			solicitacaoNee.setStatusAtendimento(new StatusAtendimento(StatusAtendimento.CANCELADO));
		
		((MovimentoCadastro)mov).setMensagens(mensagens);
		
	}
	
	/**
	 * Envia um email para o Coordenador do curso do discente, com as atualiza��es do parecer do mesmo.
	 * 
	 * @param movimento
	 * @throws DAOException
	 */
	private void enviarEmailNotificacaoCoordenacao( Movimento movimento ) throws DAOException {
		
		SolicitacaoApoioNee solicitacaoNee = ((MovimentoCadastro)movimento).getObjMovimentado();
		DiscenteAdapter discenteAdapter = new Discente();
		
		CoordenacaoCursoDao daoCoord = getDAO( CoordenacaoCursoDao.class, movimento );
		
		try{
			discenteAdapter = solicitacaoNee.getDiscente();
			solicitacaoNee.setDiscente(solicitacaoNee.getDiscente().getDiscente());
			
			Collection<CoordenacaoCurso> coordenadores; 
			if (discenteAdapter.isStricto())
				coordenadores = daoCoord.findCoordViceByCursoNivel(discenteAdapter.getGestoraAcademica().getId(), discenteAdapter.getCurso().getNivel() );
			else 
				coordenadores = daoCoord.findCoordViceByCursoNivel(discenteAdapter.getCurso().getId(), discenteAdapter.getCurso().getNivel());
			
			for( CoordenacaoCurso cc : coordenadores ){
				
				if (cc.getEmailContato() != null ) {
					MailBody mail = new MailBody();
					//destinat�rio: usu�rio que cadastrou a solicita��o
					//nome: nome do destinat�rio
					mail.setNome( cc.getServidor().getNome() );
					mail.setEmail(cc.getEmailContato());
					
					//assunto e mensagem
					mail.setAssunto("SIGAA - Solicita��o de Apoio a CAENE");
					mail.setMensagem("Caro(a) " + cc.getServidor().getNome() +", \n\n" +
							"A Solicita��o de Apoio a CAENE do discente "+ discenteAdapter.getDiscente().getMatriculaNome() +
							" foi INDEFERIDA pela Comiss�o Permanente de Apoio a Estudantes com Necessidades Educacionais Especiais, com o seguinte parecer:\n\n" +
							solicitacaoNee.getParecerComissao());
					
					//usu�rio que esta enviando e email para resposta.
					mail.setReplyTo( ParametroHelper.getInstance().getParametro(ParametrosGerais.EMAIL_CAENE) ); 
					mail.setContentType(MailBody.TEXT_PLAN);
					Mail.send(mail);
				}
			}
			
		}finally{
			daoCoord.close();
		}
	}
}