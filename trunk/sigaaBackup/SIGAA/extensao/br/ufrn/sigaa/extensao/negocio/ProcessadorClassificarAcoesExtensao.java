package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.extensao.AvaliacaoExtensaoDao;
import br.ufrn.sigaa.arq.dao.projetos.ProjetoDao;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.projetos.dominio.Projeto;

public class ProcessadorClassificarAcoesExtensao extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	validate(mov);	
	MovimentoCadastro cmov = (MovimentoCadastro) mov;
	AvaliacaoExtensaoDao dao = getDAO(AvaliacaoExtensaoDao.class, mov);
	try {
	    @SuppressWarnings("unchecked")
	    List<AtividadeExtensao> acoes = (List<AtividadeExtensao>) cmov.getColObjMovimentado();
	    for (AtividadeExtensao acao : acoes) {
		dao.updateField(Projeto.class, acao.getProjeto().getId(), "media", acao.getProjeto().getMedia());
	    }	    
	} finally {
	    dao.close();
	}
	
	return null;
    }

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
	checkRole(SigaaPapeis.GESTOR_EXTENSAO, mov);	
	ListaMensagens lista = new ListaMensagens();
	ProjetoDao dao = getDAO(ProjetoDao.class, mov);
	try {
	    @SuppressWarnings("unchecked")
	    List<AtividadeExtensao> acoes = (List<AtividadeExtensao>) ((MovimentoCadastro) mov).getColObjMovimentado();
	    ValidatorUtil.validateRequired(acoes,"Lista de ações para classificar", lista);
	    
	    EditalExtensao editalExtensao = (EditalExtensao) ((MovimentoCadastro) mov).getObjAuxiliar();
	    int total = dao.getTotalProjetosClassificadosParaEdital(editalExtensao.getEdital().getId());
	    if (total > 0) {
		lista.addErro("As ações de extensão vinculadas a este edital não podem ser re-classificadas.");
	    }
	}finally {
	    dao.close();
	}
	checkValidation(lista);

    }

}
