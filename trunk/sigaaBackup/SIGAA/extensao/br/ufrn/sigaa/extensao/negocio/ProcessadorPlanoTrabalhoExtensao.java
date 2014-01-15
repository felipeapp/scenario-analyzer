/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/12/2007
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Destinatario;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoSelecaoExtensaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoSelecaoExtensao;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.projetos.negocio.CronogramaProjetoHelper;

/*******************************************************************************
 * Processador responsável por regras de negócio dos planos de trabalhados dos
 * discentes de extensão.
 * 
 * O coordenador da ação de extensão deverá apresentar um plano de trabalho para cada
 * bolsa concedida, devendo ficar definido, neste plano, os locais de trabalho
 * em que o bolsista pleiteado desenvolverá suas atividades.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ProcessadorPlanoTrabalhoExtensao extends AbstractProcessador {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		InscricaoSelecaoExtensaoDao dao = getDAO(InscricaoSelecaoExtensaoDao.class, mov);
		validate(mov);
		PlanoTrabalhoExtensao pt = (PlanoTrabalhoExtensao) mov.getObjMovimentado();

		try {
			
			if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_PLANO_TRABALHO_EXTENSAO)) {
				pt.setDataEnvio(new Date());
				pt.getDiscenteExtensao().setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.ATIVO));
				salvarPlano(mov, dao, pt);
				
				// Selecionou um discente da lista de interessados.
				// Atualizando dados da inscrição
				if (pt.getDiscenteExtensao() != null && ValidatorUtil.isNotEmpty(pt.getInscricaoSelecaoExtensao())){
					dao.updateFields(InscricaoSelecaoExtensao.class, pt.getInscricaoSelecaoExtensao().getId(), 
							new String[] {"tipoVinculo.id", "discenteExtensao.id", "situacaoDiscenteExtensao.id"}, 
							new Object[] {pt.getDiscenteExtensao().getTipoVinculo().getId(), pt.getDiscenteExtensao().getId(), 
											TipoSituacaoDiscenteExtensao.SELECIONADO});
				}
	
			}else if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_PLANO_TRABALHO_EXTENSAO)) {
				salvarPlano(mov, dao, pt);
	
			}else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO_EXTENSAO)) {
	
				//Excluindo todos os discentes do histórico do plano
				for (DiscenteExtensao de : pt.getHistoricoDiscentesPlano()) {
					de.setAtivo(false);
					de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.EXCLUIDO));		    
					dao.update(de);
					
					// gravando histórico da situação de todos os discente que já passaram por este plano de trabalho 	    
					DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, mov.getUsuarioLogado().getRegistroEntrada());
				}
				
				//Se o plano tiver um discente ativo. Pode ser que o plano tenha sido criado e removido logo em seguida.
				if (pt.getDiscenteExtensao() != null) {
					DiscenteExtensao de = pt.getDiscenteExtensao(); 
					de.setAtivo(false);
					de.setSituacaoDiscenteExtensao(new TipoSituacaoDiscenteExtensao(TipoSituacaoDiscenteExtensao.EXCLUIDO));		    
					dao.update(de);
	
					// Gravando histórico da situação do discente	    
					DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, de, mov.getUsuarioLogado().getRegistroEntrada());
					
					// Depois de excluído do plano, o discente volta para lista de interessados, permitindo que  seja selecionado novamente para o projeto.
					InscricaoSelecaoExtensao inscricao = dao.findByDiscenteAtividade(de.getDiscente().getId(), de.getAtividade().getId());
					if (ValidatorUtil.isNotEmpty(inscricao)){
						dao.updateFields(InscricaoSelecaoExtensao.class, inscricao.getId(), 
								new String[] {"tipoVinculo", "discenteExtensao", "situacaoDiscenteExtensao.id"}, 
								new Object[] {null, null, TipoSituacaoDiscenteExtensao.INSCRITO_PROCESSO_SELETIVO});
					}
				}
	
				pt.setAtivo(false);
				pt.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
				dao.update(pt);
			
			}else if (mov.getCodMovimento().equals(SigaaListaComando.NOTIFICACAO_DISCENTE_MENSAGEM_EMAIL)) {
				InscricaoSelecaoExtensao inscricao = (InscricaoSelecaoExtensao) mov.getObjAuxiliar();
				
				if (inscricao.getNotificacao().isEnviarEmail()) 
					notificarPorEmail(inscricao, mov.getUsuarioLogado(), dao);
	
				if (inscricao.getNotificacao().isEnviarMensagem()) 
					notificarPorMensagem(inscricao.getNotificacao(), mov.getUsuarioLogado(), dao);
			}
		}finally {
			dao.close();
		}

		return null;
	}


	/**
	 * Salva o plano de trabalho. Utilizado para salvar temporariamente o plano
	 * e para enviá-lo.
	 * 
	 * @param mov
	 * @param dao
	 * @param pt
	 * @throws DAOException
	 */
	private void salvarPlano(MovimentoCadastro mov, GenericDAO dao,	PlanoTrabalhoExtensao pt) throws DAOException {
		
		// Evitar erro de objeto transient...
		if(pt.getDiscenteExtensao() != null){
			if(ValidatorUtil.isEmpty(pt.getDiscenteExtensao().getBanco())) pt.getDiscenteExtensao().setBanco(null);
			if(ValidatorUtil.isEmpty(pt.getDiscenteExtensao().getTipoVinculo())) pt.getDiscenteExtensao().setTipoVinculo(null);
			if(ValidatorUtil.isEmpty(pt.getDiscenteExtensao().getDiscente())) pt.setDiscenteExtensao(null);
		}
		
		// Salvando o plano de trabalho
		if (pt.isNovoPlano()) {
			dao.create(pt);			
		}else {
			pt.setCronogramas(CronogramaProjetoHelper.submeterCronogramaPlanoTrabalhoExtensao( dao, pt, pt.getCronogramas() ) );
			dao.update(pt);
		}
		
		if( pt.getDiscenteExtensao() != null && ValidatorUtil.isNotEmpty(pt.getDiscenteExtensao().getDiscente())) {
			pt.getDiscenteExtensao().setPlanoTrabalhoExtensao(pt);
			pt.getDiscenteExtensao().setAtividade(pt.getAtividade());
			pt.getDiscenteExtensao().setDataFim(pt.getDataFim()); //definindo uma data fim padrão para o discente (data fim do plano de trabalho)
			dao.update(pt.getDiscenteExtensao());
			// gravando histórico da situação do discente	    
			DiscenteExtensaoHelper.gravarHistoricoSituacao(dao, pt.getDiscenteExtensao(), mov.getUsuarioLogado().getRegistroEntrada());
		}

	}
	
    /**
     * Utilizado na validação do cadastro do plano de trabalho.
     *  
     */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro aMov = (MovimentoCadastro) mov;
		
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
		
		ListaMensagens lista = new ListaMensagens();
		PlanoTrabalhoExtensao plano = aMov.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.ENVIAR_PLANO_TRABALHO_EXTENSAO)) {
			PlanoTrabalhoValidator.validaDadosGerais(plano, lista);
			PlanoTrabalhoValidator.validarCronogramaExtensao(plano, lista);
		}else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PLANO_TRABALHO_EXTENSAO)) {

		}

		checkValidation(lista);
	}

    /**
     * Notifica todos os destinatários selecionados por email.
     *
     * @param notificacao
     * @param remetente
     * @throws DAOException 
     */
    public static void notificarPorEmail(InscricaoSelecaoExtensao inscricao, UsuarioGeral remetente, InscricaoSelecaoExtensaoDao dao) throws DAOException {
    	InscricaoSelecaoExtensao inscricaoOrig = dao.findByPrimaryKey(inscricao.getId(), InscricaoSelecaoExtensao.class);
    	
    	for (Destinatario destinatario : inscricao.getNotificacao().getDestinatariosEmail()) {
		    MailBody mail = new MailBody();
			mail.setContentType(MailBody.HTML);
			mail.setReplyTo("noReply@ufrn.br");
		    mail.setEmail(destinatario.getEmail()) ;
		    
		    mail.setAssunto( inscricao.getNotificacao().getTitulo() );

		    String tituloAtividade = inscricaoOrig != null ? inscricaoOrig.getAtividade().getProjeto().getTitulo() : inscricao.getAtividade().getProjeto().getTitulo();
		    String coordenadorAcao = inscricaoOrig != null ? inscricaoOrig.getAtividade().getCoordenacao().getServidor().getPessoa().getNome() : 
		    		inscricao.getAtividade().getCoordenacao().getServidor().getPessoa().getNome(); 
		    
		    String mensagem = "Título da Ação: " + tituloAtividade + " <br /><br />";
		    mensagem += "Coordenador(a): " + coordenadorAcao + " <br /><br />";
		    mensagem += "Mensagem: " + inscricao.getNotificacao().getMensagem() + " <br />";
		    mensagem += "<br /><br /><br />ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA " +
	    		RepositorioDadosInstitucionais.get("siglaSigaa")+".<br />POR FAVOR, NÃO RESPONDÊ-LO. <br/><br/><br/>"; 
		    
		    mail.setMensagem( mensagem );
		    Mail.send(mail);
		}
    }
	
    /**
     * Notifica todos os destinatários selecionados por mensagens
     * no sistema
     * @param remetente
     *
     * @param destinatario
     */
    public static void notificarPorMensagem(Notificacao notificacao, UsuarioGeral remetente, InscricaoSelecaoExtensaoDao dao) {

		Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
			for (Destinatario destinatario : notificacao.getDestinatariosMensagem()) {
				usuarios.add(destinatario.getUsuario());
			}
	
		Mensagem mensagem = new Mensagem();
		mensagem.setTitulo(notificacao.getTitulo());
		mensagem.setMensagem(StringUtils.stripHtmlTags(notificacao.getMensagem()));
		mensagem.setRemetente(remetente);
		mensagem.setAutomatica(true);
		mensagem.setLeituraObrigatoria(true);
		mensagem.setConfLeitura(true);
		mensagem.setEnviarEmail(true);
	
		ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(mensagem, remetente, usuarios);
    }

}