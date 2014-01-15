/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/02/2008
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.sigaa.SipacSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dao.ProcessoDAO;
import br.ufrn.comum.dao.ProcessoDAOImpl;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.integracao.dto.ProcessoDTO;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.caixa_postal.dominio.Mensagem;
import br.ufrn.sigaa.caixa_postal.dominio.MensagensHelper;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoOrientacaoAcademica;
import br.ufrn.sigaa.ensino.graduacao.negocio.ProcessadorOrientacaoAcademica;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.HomologacaoTrabalhoFinal;

/**
 * Processador para realiza��o de homologa��o do trabalho final de curso
 * de p�s-gradua��o stricto-sensu.
 *
 * @author David Pereira
 *
 */
public class ProcessadorHomologacaoTrabalhoFinal extends AbstractProcessador{

	/**
	 * M�todo invocado pela arquitetura
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);

		if (mov.getCodMovimento().equals(SigaaListaComando.HOMOLOGAR_TRABALHO_FINAL_STRICTO)) 
			criarSolicitacao(mov);

		return null;
	}

	/**
	 * Cria a solicita��o de homologa��o do diploma
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void criarSolicitacao(Movimento mov) throws DAOException, ArqException,	NegocioException {
		
		MovimentoHomologacao hMov = (MovimentoHomologacao) mov;
		OrientacaoAcademicaDao dao = getDAO(OrientacaoAcademicaDao.class, mov);
		
		try {
			// Salvar Arquivo
			HomologacaoTrabalhoFinal homologacao = hMov.getHomologacao();

			salvarArquivo(hMov, homologacao);

			DiscenteStricto discente = homologacao.getBanca().getDadosDefesa().getDiscente();
			discente.setBancaDefesa(homologacao.getBanca());
			discente.setDataDefesa(homologacao.getBanca().getData());
			dao.update(discente);
			
			// Registrar Homologa��o
			dao.update(homologacao.getBanca().getDadosDefesa());
			if( homologacao.getId() > 0 ){
				dao.update(homologacao);
			}else{
				dao.create(homologacao);

				// Alterar Status do Discente para Em Homologa��o
				DiscenteHelper.alterarStatusDiscente(homologacao.getBanca().getDadosDefesa().getDiscente(),
						StatusDiscente.EM_HOMOLOGACAO, mov, dao);

				// � necess�rio finalizar as orienta��es dos alunos
				finalizarOrientacaoAcademica(mov, dao, discente);
	
				// envia mensagem para os gestores
				enviarMensagemParaGestores(mov, homologacao, discente);
			}

		} catch (IOException e) {
			throw new ArqException(e);
		}
	}

	/**
	 * Envia mensagem para gestores stricto
	 * 
	 * @param mov
	 * @param homologacao
	 * @param discente
	 * @throws DAOException
	 */
	private void enviarMensagemParaGestores(Movimento mov, HomologacaoTrabalhoFinal homologacao,
			DiscenteStricto discente) throws DAOException {
		PermissaoDAO pdao = getDAO(PermissaoDAO.class, mov);
		Collection<UsuarioGeral> usuarios = pdao.findByPapel(new Papel(SigaaPapeis.PPG));
		if (usuarios != null) {
			for (UsuarioGeral user : usuarios) {

				Mensagem msg = new Mensagem();
				msg.setTitulo("Solicita��o de Conclus�o de Discente");
				msg.setMensagem("Solicitamos a conclus�o do discente " + discente.getNome()
						+ ", matr�cula " + discente.getMatricula() + ". Processo de Homologa��o: "
						+ homologacao.getNumProcesso() + "/" + homologacao.getAnoProcesso() + ".");
				msg.setTipo(br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM);
				msg.setDataCadastro(new Date());
				msg.setConfLeitura(false);
				msg.setUsuario(user);
				msg.setRemetente(mov.getUsuarioLogado());
				msg.setPapel(new Papel(SigaaPapeis.PPG));
				msg.setAutomatica(true);

				MensagemDAO msgDAO = new MensagemDAO();
				try{
					msgDAO.create(MensagensHelper.msgSigaaToMsgArq(msg));
				}finally{
					msgDAO.close();
				}
			}
		}
	}

	/**
	 * Todas as orientacoes do aluno devem ser canceladas
	 * 
	 * @param mov
	 * @param dao
	 * @param discente
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void finalizarOrientacaoAcademica(Movimento mov, OrientacaoAcademicaDao dao,
			DiscenteStricto discente) throws DAOException, NegocioException,
			ArqException {
		OrientacaoAcademica orientacao = dao.findOrientadorAtivoByDiscente(discente.getId());
		if (orientacao != null) {
			orientacao.setFim(new Date());
			MovimentoOrientacaoAcademica movOrientacoes = new MovimentoOrientacaoAcademica();
			orientacao.setTipoOrientacao(OrientacaoAcademica.ORIENTADOR);
			movOrientacoes.setCodMovimento(SigaaListaComando.FINALIZAR_ORIENTACOES_DISCENTE);
			movOrientacoes.setSistema(mov.getSistema());
			movOrientacoes.setOrientacao(orientacao);
			movOrientacoes.setSubsistema(mov.getSubsistema());
			movOrientacoes.setUsuarioLogado(mov.getUsuarioLogado());
			(new ProcessadorOrientacaoAcademica()).execute(movOrientacoes);
		}
	}

	/**
	 * Salva o trabalho do aluno
	 * 
	 * @param hMov
	 * @param homologacao
	 * @throws IOException
	 */
	private void salvarArquivo(MovimentoHomologacao hMov, HomologacaoTrabalhoFinal homologacao) throws IOException {
		UploadedFile arquivo = hMov.getArquivo();
		if (arquivo != null) {
			
			/** Se for altera��o tem que remover o arquivo antigo */
			if( homologacao.getId() > 0 )
				EnvioArquivoHelper.removeArquivo( homologacao.getBanca().getDadosDefesa().getIdArquivo() );
			
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
			homologacao.getBanca().getDadosDefesa().setIdArquivo(idArquivo);
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

		ProcessoDAO procDao = new ProcessoDAOImpl();		
		try {		
			MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);		
			BancaPosDao bancaDao = getDAO(BancaPosDao.class, mov);

			MovimentoHomologacao hMov = (MovimentoHomologacao) mov;
			HomologacaoTrabalhoFinal homologacao = hMov.getHomologacao();
			DiscenteStricto discente = homologacao.getBanca().getDadosDefesa().getDiscente();

			Collection<MatriculaComponente> matriculadas = dao.findAtividades(discente, new TipoAtividade(TipoAtividade.TESE), SituacaoMatricula.APROVADO);
			BancaPos banca = bancaDao.findMaisRecenteByTipo(discente, BancaPos.BANCA_DEFESA);

			// S� � poss�vel realizar a homologa��o se o aluno tiver uma defesa cadastrada
			if (isEmpty(matriculadas) && banca == null) {
				throw new NegocioException("S� � poss�vel realizar a homologa��o se o aluno tiver uma defesa cadastrada.");
			}

			// Validar se est� passando do per�odo de homologa��o (6 meses)
			int tempoMaximo = ParametroHelper.getInstance().getParametroInt(ConstantesParametro.TEMPO_MAXIMO_HOMOLOGACAO_STRICTO);
			if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.PPG) && discente.getAnoIngresso() >= 2005 && CalendarUtils.calculoMeses(banca.getData(), new Date()) > tempoMaximo) {
				throw new NegocioException("N�o � poss�vel realizar a homologa��o porque o prazo de " + tempoMaximo + " meses se esgotou.");
			}

			UploadedFile arquivo = hMov.getArquivo();
			if (homologacao.getId() == 0 && arquivo == null) {
				throw new NegocioException("� necess�rio preencher o campo Arquivo.");
			}

			if (homologacao.getNumProcesso() <= 0 || homologacao.getAnoProcesso() <= 0) {
				throw new NegocioException("O processo de homologa��o informado � inv�lido.");
			}
		
			if( homologacao.getId() > 0 && homologacao.getBanca().getDadosDefesa().getDiscente().isConcluido() ){
				throw new NegocioException("O processo de homologa��o j� foi finalizado e o discente conclu�do, portanto n�o � poss�vel alterar os dados da homologa��o.");
			}
		
			if (Sistema.isSipacAtivo() && isSubSistemaAtivo(SipacSubsistemas.PROTOCOLO)) {
				ProcessoDTO processoHomologacao = procDao.findByIdentificador(homologacao.getNumProcesso(), homologacao.getAnoProcesso(), -1);
				if (processoHomologacao == null) {
					throw new NegocioException("O processo de homologa��o informado � inv�lido.");
				}
			}
		}
		finally {
			procDao.close();
		}
	}
}
