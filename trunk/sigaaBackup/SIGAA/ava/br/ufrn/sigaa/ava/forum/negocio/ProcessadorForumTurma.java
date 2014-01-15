/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '25/04/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.forum.dao.ForumGeralMensagemDao;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.negocio.MaterialTurmaHelper;


/**
 * Processador para regras de negócio do cadastro de um fórum de turma.
 * 
 * @author Ilueny Santos
 * 
 */
public class ProcessadorForumTurma extends ProcessadorCadastro  {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_TURMA)) {
			obj = criar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_TURMA)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_TURMA)) {
			obj = remover(mov);
		}

		return obj;
	}
	
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumTurma forumTurma = mov.getObjMovimentado();
		
			if (!forumTurma.isVincularTopico()) {
				forumTurma.setTopicoAula(null);
			}
			
			MovimentoCadastro mov2 = new MovimentoCadastro();
			mov2.setSistema(mov.getSistema());
			mov2.setUsuarioLogado(mov.getUsuarioLogado());
			mov2.setObjMovimentado(forumTurma.getForum());
			mov2.setObjAuxiliar(forumTurma.getTurma());
			mov2.setCodMovimento(SigaaListaComando.CADASTRAR_FORUM_GERAL);
			new ProcessadorForumGeral().execute(mov2);			

			notificarTurma(mov);
			
			MaterialTurmaHelper.definirNovoMaterialParaTopico(forumTurma, forumTurma.getTopicoAula(), forumTurma.getTurma());
			forumTurma = (ForumTurma) super.criar(mov);
			MaterialTurmaHelper.atualizarMaterial(getGenericDAO(mov), forumTurma, true);
			
		
		return forumTurma;
	}
	
	/**
	 * Altera o fórum. 
	 */	
	@Override
	protected Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumTurma forumTurma = mov.getObjMovimentado();
		
			MovimentoCadastro mov2 = new MovimentoCadastro();
			mov2.setSistema(mov.getSistema());
			mov2.setUsuarioLogado(mov.getUsuarioLogado());
			mov2.setObjMovimentado(forumTurma.getForum());
			mov2.setCodMovimento(SigaaListaComando.ALTERAR_FORUM_GERAL);
			new ProcessadorForumGeral().execute(mov2);			

			MaterialTurmaHelper.definirNovoMaterialParaTopico(forumTurma, forumTurma.getTopicoAula(), forumTurma.getTurma());
			forumTurma = (ForumTurma) super.alterar(mov);
			MaterialTurmaHelper.atualizarMaterial(getGenericDAO(mov), forumTurma,false);
			
		
		return forumTurma;
		
	}

	
    /**
     * Envia e-mail para os participantes do fórum.
     * 
     */
    public void notificarTurma(MovimentoCadastro mov) throws NegocioException, ArqException {
    	ForumTurma forumTurma = mov.getObjMovimentado();
    	ForumGeralMensagemDao dao = getDAO(ForumGeralMensagemDao.class, mov);

    	List<String> emails = null;
    	try {
    		emails = dao.findEmailsByTurma(forumTurma.getTurma());
    		
    		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
    		String data = sdf.format(forumTurma.getForum().getDataCadastro());
    		
    		if (forumTurma.isVincularTopico()) {
    			forumTurma.setTopicoAula(dao.findByPrimaryKey(forumTurma.getTopicoAula().getId(), TopicoAula.class, "id","descricao"));
    		}

    		for (String email : emails) {
    			MailBody mail = new MailBody();
    			mail.setContentType(MailBody.HTML);
    			
    			if (forumTurma.isVincularTopico()) {
    				mail.setAssunto("Novo fórum adicionado ao tópico de aula: " + forumTurma.getTopicoAula().getDescricao() + " - " + data );
    			}else {
    				mail.setAssunto("Novo fórum adicionado à turma: " + forumTurma.getTurma().getDescricaoCodigo() + " - " + data );
    			}    			
    			mail.setFromName("Fórum SIGAA: " + mov.getUsuarioLogado().getNome());
    			mail.setEmail(email);
    			
    			StringBuffer msg = new StringBuffer();
    			msg.append("<b>Título do Fórum:</b> ");
    			msg.append(forumTurma.getForum().getTitulo());
    			msg.append("<br/>"+(char)13); // Enter(pula uma linha)
    			msg.append("<b>Descrição:</b> ");
    			msg.append("<br/>"+(char)13); // Enter(pula uma linha)
    			// Removendo comentários inseridos quando se copia e cola conteúdos de arquivos do Word.
    			msg.append(StringUtils.removerComentarios(forumTurma.getForum().getDescricao()));
    			
    			mail.setMensagem(msg.toString());
    			Mail.send(mail);
    		}
    	}finally{
    		dao.close();
    	}
    }

    
	/** 
	 * Remove um fórum de turma. 
	 * 
	 */
	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		ForumTurma forumTurma = mov.getObjMovimentado();
		GenericDAO dao = getGenericDAO(mov);
		
		try {
			MovimentoCadastro mov2 = new MovimentoCadastro();
			mov2.setSistema(mov.getSistema());
			mov2.setUsuarioLogado(mov.getUsuarioLogado());
			mov2.setObjMovimentado(forumTurma.getForum());
			mov2.setCodMovimento(SigaaListaComando.REMOVER_FORUM_GERAL);
			new ProcessadorForumGeral().execute(mov2);
			
			dao.updateField(ForumTurma.class, forumTurma.getId(), "ativo", false);
			dao.updateField(MaterialTurma.class, forumTurma.getMaterial().getId(), "ativo", false);
			MaterialTurmaHelper.reOrdenarMateriais(forumTurma.getAula());

		}finally{
			dao.close();
		}
		
		return forumTurma;
	}


	
    @Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
    	MovimentoCadastro movC = (MovimentoCadastro) mov;
    	ListaMensagens mensagens = new ListaMensagens();
    	ForumTurma forumTurma = movC.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_FORUM_TURMA)) {
			mensagens.addAll(forumTurma.validate());
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_FORUM_TURMA)) {
			mensagens.addAll(forumTurma.validate());
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_FORUM_TURMA)) {
			//TODO:
		}
		checkValidation(mensagens);
	}

	
}

