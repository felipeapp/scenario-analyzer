/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/08/2009
 *
 */
package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.extensao.negocio.ProcessadorAutorizacaoDepartamento;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Processador para alterar dados do membro de um projeto.
 * 
 * @author Ilueny Santos
 *
 */
public class ProcessadorMembroProjeto extends AbstractProcessador {


    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	MovimentoCadastro movC = (MovimentoCadastro) mov;
	validate(movC);
	if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE)) {
	    salvar(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE)) {
	    remover(movC);
	} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_COORDENADOR_PROJETO_BASE)){
		alterarCoordenador(movC);
	}
	return movC.getObjMovimentado();
    }
    
    
    /**
     * Remove membro do projeto do banco definitivamente.
     * Utilizado durante o cadastro da proposta de projeto.
     * 
     * @param mov
     * @throws DAOException
     */
    private void salvar(MovimentoCadastro mov) throws DAOException {
	GenericDAO dao = getGenericDAO(mov);
	try {
	    MembroProjeto membro = mov.getObjMovimentado();
	    membro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
	    dao.createOrUpdate(membro);
	    if(membro.isCoordenadorAtivo()) {		
		dao.updateField(Projeto.class, membro.getProjeto().getId(), "coordenador.id", membro.getId());
	    }
	} finally {
	    dao.close();
	}
    }

    /**
     * Remove membro do projeto.
     * Utilizado durante o cadastro da proposta de projeto e na manutenção da equipe pelo coordenador
     * durante a execução do projeto.
     * 
     * @param mov
     * @throws RemoteException 
     * @throws ArqException 
     * @throws NegocioException 
     */
    private void remover(MovimentoCadastro mov) throws NegocioException, ArqException, RemoteException {
    	GenericDAO dao = getGenericDAO(mov);
    	try {
    		MembroProjeto membro = mov.getObjMovimentado();
    		membro = dao.refresh(membro);	    
    		// Retira o coordenador do projeto primeiro evitando o erro de violação de chave na remoção.
    		if (membro.getProjeto().getCoordenador() != null && 
    				(membro.getProjeto().getCoordenador().getId() == membro.getId() || membro.isCoordenadorAtivo())) {
    			dao.updateField(Projeto.class, membro.getProjeto().getId(), "coordenador.id", null);
    			membro.getProjeto().setCoordenador(null);
    		}
    		dao.updateField(MembroProjeto.class, membro.getId(), "ativo", false);
    		membro.getProjeto().getEquipe().remove(membro);
    		

    		// Caso a pessoa removida seja um servidor.
    		if (ValidatorUtil.isNotEmpty(membro.getServidor())){
    			boolean ultimoServidorDoDeptoNaEquipe = dao.count("from projetos.membro_projeto mp join rh.servidor s using(id_servidor) join comum.unidade u using(id_unidade) " +
    					" where mp.ativo = trueValue() and mp.id_projeto = " + membro.getProjeto().getId() + " and u.id_unidade = " + membro.getServidor().getId() ) == 0;

    			/** @negocio: Removendo autorizações de unidades que não possuem mais membros na equipe do projeto. */
    			if (ultimoServidorDoDeptoNaEquipe) {	
    				ProcessadorAutorizacaoDepartamento procAuto = new ProcessadorAutorizacaoDepartamento();
    				MovimentoCadastro movAD = new MovimentoCadastro();
    				movAD.setSistema(mov.getSistema());
    				movAD.setObjMovimentado(membro.getProjeto());
    				movAD.setUsuarioLogado(mov.getUsuarioLogado());
    				movAD.setCodMovimento(SigaaListaComando.INATIVAR_AUTORIZACOES_DEPARTAMENTOS);
    				procAuto.execute(movAD);	    
    			}
    		}

    	} finally {
    		dao.close();
    	}
    }

    /**
     * Altera o coordenador de uma ação de extensão do banco definitivamente.
     * Utilizado durante a alteração de um coordenador de uma ação de extensão ja existente. 
     * 
     * @param mov
     * @throws DAOException
     */
    public void alterarCoordenador(MovimentoCadastro mov) throws DAOException{
    	GenericDAO dao = getGenericDAO(mov);
    	try{
    	
    		MembroProjeto membro = mov.getObjMovimentado(); // Novo Coordenador
        	MembroProjeto membroAux = (MembroProjeto) mov.getObjAuxiliar(); // Coordenador Atual
        	membro.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
    		
    		// Finaliza o coordenador atual atualizando seu campo de data final
        	dao.createOrUpdate(membroAux);
    		dao.detach(membroAux);
    		
    		// Adiciona o novo coordenador
    		dao.create(membro);
    		
    		// Atualiza o coordenador do projeto
    		dao.updateField(Projeto.class, membroAux.getProjeto().getId(), "coordenador.id", membro.getId());
    		
    	} finally{
    		dao.close();
    	}
    }
       
    public void validate(Movimento mov) throws NegocioException, ArqException {
	MovimentoCadastro movC = (MovimentoCadastro) mov;
	MembroProjeto membro = movC.getObjMovimentado();
	ListaMensagens mensagens = new ListaMensagens();
	
	// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
	Usuario usuario = (Usuario) mov.getUsuarioLogado();
	if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
		mensagens.addErro("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
	}
	
	if (membro == null) {
	    mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Membro do Projeto");
	}
	else {
	    if (mov.getCodMovimento().equals(SigaaListaComando.SALVAR_MEMBRO_PROJETO_BASE)) {
			if (membro.isCategoriaDiscente()) {
			    MembroProjetoValidator.validaDiscenteSemValidacaoBolsista(membro.getProjeto().getEquipe(), membro, mensagens);
			}
			if (membro.isCategoriaDocente() || membro.isCategoriaServidor()) {
			    MembroProjetoValidator.validaServidor(membro.getProjeto().getEquipe(), membro, mensagens);
			    if (membro.isCoordenador()) {
				MembroProjetoValidator.validaCooordenacaoDupla(membro.getProjeto(), membro, mensagens);
				MembroProjetoValidator.validaCoordenacaoEnsinoPesquisa(membro, mensagens);
				ProjetoBaseValidator.validaRestricoesCoordenacaoEdital(membro.getProjeto(), membro.getServidor(), mensagens);
			    }
			}
			if (membro.getCategoriaMembro().isExterno()) {
			    MembroProjetoValidator.validaParticipanteExterno(membro.getProjeto().getEquipe(), membro, mensagens, false);
			}
			if (membro.getId() > 0) {
			    MembroProjetoValidator.validaAlteracaoMembroProjetoBase(membro, getDAO(MembroProjetoDao.class, mov), mensagens);
			}
			
			if ( membro.getProjeto().getTipoProjeto().isExtensao() ) {
				MembroProjetoValidator.verificaRelatorioPendenteDocente(membro, mensagens);
			}
			
	    } else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_MEMBRO_PROJETO_BASE)) {
	    	MembroProjetoValidator.validaRemoverMembroProjeto(membro, mensagens);
	    } else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_COORDENADOR_PROJETO_BASE)) {
	    	GenericDAO dao = getGenericDAO(movC);
	    	if ( membro.isCategoriaDocente() ){
	    		MembroProjetoHelper.adicionarDocenteSemCoordenacaoDupla(dao, membro, membro.getProjeto(), mensagens);
	    	}else if(membro.isCategoriaServidor()){
	    		MembroProjetoHelper.adicionarServidorSemCoordenacaoDupla(dao, membro, membro.getProjeto(), mensagens);
	    	}
	    }
	}
	checkValidation(mensagens);
    }

}
