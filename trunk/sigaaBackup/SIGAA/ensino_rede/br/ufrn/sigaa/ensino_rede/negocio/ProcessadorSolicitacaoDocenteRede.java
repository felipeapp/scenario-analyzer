/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 21/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.sigaa.arq.negocio.PessoaMov;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.dao.DocenteRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoSituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SituacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.SolicitacaoDocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.StatusSolicitacaoDocenteRede;
import br.ufrn.sigaa.negocio.PessoaValidator;
import br.ufrn.sigaa.negocio.ProcessadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador responsável pelo fluxo de solicitação de docente de ensino em rede
 *
 * @author Diego Jácome
 */
public class ProcessadorSolicitacaoDocenteRede extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {

		if (mov.getCodMovimento().equals(SigaaListaComando.SOLICITACAO_CADASTRO_DOCENTE_REDE)) 
			solicitacaoCadastrarDocente(mov);
		else if (mov.getCodMovimento().equals(SigaaListaComando.SOLICITACAO_ALTERACAO_DOCENTE_REDE))
			solicitacaoAlterarDocente(mov);
		else if (mov.getCodMovimento().equals(SigaaListaComando.HOMOLOGAR_SOLICITACAO_DOCENTES_REDE))
			homologarDocentes(mov);
		
		return null;
	}

	/**
	 * Método auxiliar do execute(), para a realizar cadastro de solicitação de docente.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	private void solicitacaoCadastrarDocente(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		SolicitacaoDocenteRede sol = cmov.getObjMovimentado();
		
		DocenteRedeDao dao = null;
		
		try {
			
			dao = getDAO(DocenteRedeDao.class,cmov);
			
			if (sol.getDocente().getPessoa().getId() == 0)
				criarPessoa(dao, cmov, sol.getDocente());
			
			sol.getDocente().setSituacao(new SituacaoDocenteRede(SituacaoDocenteRede.PENDENTE));
			sol.setSituacaoRequerida(new SituacaoDocenteRede(SituacaoDocenteRede.ATIVO));
			sol.setTipoRequerido(sol.getDocente().getTipo());
			
			dao.createOrUpdate(sol.getDocente());
			dao.create(sol);
			
			AlteracaoSituacaoDocenteRede alteracao = DocenteRedeHelper.createAlteracaoDocenteRede(sol.getDocente(), cmov, null, sol.getDocente().getSituacao());
			dao.create(alteracao);
			
		} finally {
			if (dao!=null)
				dao.close();
		}
		
	}

	/**
	 * Método auxiliar do execute(), para a realizar solicitação de alteração de docente.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private void solicitacaoAlterarDocente(Movimento mov) throws DAOException {

		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		SolicitacaoDocenteRede sol = cmov.getObjMovimentado();
		
		DocenteRedeDao dao = null;
		
		try {
			
			dao = getDAO(DocenteRedeDao.class,cmov);
			dao.create(sol);			
			
		} finally {
			if (dao!=null)
				dao.close();
		}
		
		
	}
	
	/**
	 * Método auxiliar do execute(), para a realizar homologação de solicitações de docente.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void homologarDocentes(Movimento mov) throws DAOException {

		MovimentoCadastro cmov = (MovimentoCadastro) mov;
		ArrayList<SolicitacaoDocenteRede> solicitacoes = (ArrayList<SolicitacaoDocenteRede>) cmov.getColObjMovimentado();
		
		DocenteRedeDao dao = null;

		try {
		
			dao = getDAO(DocenteRedeDao.class,cmov);
			
			for (SolicitacaoDocenteRede s : solicitacoes) {
				
				DocenteRede d = s.getDocente();
				
				if (s.getStatus().getId() == StatusSolicitacaoDocenteRede.DEFERIDA){
					SituacaoDocenteRede situacaoAntiga = new SituacaoDocenteRede(d.getSituacao().getId());
					
					if (situacaoAntiga.getId() != s.getSituacaoRequerida().getId()){					
						d.setSituacao(s.getSituacaoRequerida());
						d.setTipo(s.getTipoRequerido());
						dao.update(d);
						AlteracaoSituacaoDocenteRede alteracao = DocenteRedeHelper.createAlteracaoDocenteRede(s.getDocente(), cmov, situacaoAntiga, d.getSituacao());
						dao.create(alteracao);
					}	
				} 	
				
				Pessoa coord = s.getUsuario().getPessoa();
				notificarRespostaCoordenador(coord,s);
				dao.update(s);
			}
		
		} finally {
			if (dao!=null)
				dao.close();
		}
	}
	
    /**
     * Notifica o parecer aos responsáveis da unidade.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     */
    private void notificarRespostaCoordenador(Pessoa pessoa, SolicitacaoDocenteRede sol) {
    	MailBody mail = new MailBody();
		mail.setContentType(MailBody.HTML);
		mail.setAssunto("Resposta de Solicitação de Docente");
		mail.setMensagem(getMensagemHomologacaoSolicitacao(pessoa,sol));
		mail.setEmail(pessoa.getEmail());
		mail.setNome(pessoa.getNome());
		Mail.send(mail);
    }
	
    /**
     * Monta a mensagem a ser enviada ao responsável pela unidade que recebeu a resposta.
     * 
     * @param pessoa
     * @param manifestacao
     * @param historico
     * @return
     */
    private String getMensagemHomologacaoSolicitacao(Pessoa pessoa, SolicitacaoDocenteRede sol) {
    	
    	String mensagem = "Prezado(a) " + pessoa.getNome() + ", <br /><br />" + 
							"Informamos que a solicitação de alteração no docente foi: " +sol.getStatus().getDescricao();
							
		if (!isEmpty(sol.getAtendimento())) {
			mensagem += "<br/> Segue o parecer feito pelo gestor geral:" +
						"<br />" +
						sol.getAtendimento() +
						"<br />";
		}
		
    	mensagem += "<br /><br /> <i> Esta mensagem é automática e não deve ser respondida </i>";
		
		return mensagem;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Método auxiliar responsável pelo cadastro de pessoa.
	 * 
	 * @param mov
	 * @return
	 * @throws RemoteException
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	private void criarPessoa(GenericDAO dao, MovimentoCadastro movCad, DocenteRede docente) throws DAOException, NegocioException, ArqException, RemoteException {
		docente.getPessoa().setUltimaAtualizacao(new Date());
		
		ProcessadorPessoa.anularTransientObjects(docente.getPessoa());
		ProcessadorPessoa.persistirContaBancaria(docente.getPessoa(), dao);
		
		// chama o processador pessoa para o cadastro da pessoa
		ProcessadorPessoa pPessoa = new ProcessadorPessoa();
		PessoaMov movPessoa = new PessoaMov();
		movPessoa.setPessoa(docente.getPessoa());
		movPessoa.setUsuarioLogado(movCad.getUsuarioLogado());
		movPessoa.setSistema( movCad.getSistema() );
		movPessoa.setTipoValidacao(PessoaValidator.DOCENTE_EXTERNO);
		
		// se a pessoa já existir chama alteração
		if ( docente.getPessoa().getId() != 0 ) {
			movPessoa.setCodMovimento(SigaaListaComando.ALTERAR_PESSOA);
		} else {
			// Sincroniza para pegar o próximo idPessoa, para evitar assim que o Docente Externo seja cadastrado com o id = 0.
			movPessoa.setCodMovimento(SigaaListaComando.CADASTRAR_PESSOA);
			int idPessoa = SincronizadorPessoas.getNextIdPessoa();
			docente.getPessoa().setId(idPessoa);
		}
		Pessoa pessoa = (Pessoa) pPessoa.execute(movPessoa);
		if (pessoa != null)
			docente.setPessoa(pessoa);
	}
	

}
