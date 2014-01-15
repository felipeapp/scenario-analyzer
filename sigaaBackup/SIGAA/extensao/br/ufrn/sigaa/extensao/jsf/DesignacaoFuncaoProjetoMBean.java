package br.ufrn.sigaa.extensao.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.projetos.MembroProjetoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dao.DesignacaoFuncaoProjetoDao;
import br.ufrn.sigaa.extensao.dominio.DesignacaoFuncaoProjeto;
import br.ufrn.sigaa.extensao.dominio.TipoDesignacaoFuncaoProjeto;
import br.ufrn.sigaa.extensao.jsf.helper.DesignacaoFuncaoProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;

/**
 * Classe que responsável pelas atribuições das designações aos membros dos projetos de extensão. 
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class DesignacaoFuncaoProjetoMBean extends SigaaAbstractController<DesignacaoFuncaoProjeto> {

	/** Projeto no qual está sendo atribuida a designação */
	private Projeto projeto;
	
	public DesignacaoFuncaoProjetoMBean() {
		clear();
	}

	/** Método responsável por limpar o dados */
	private void clear() {
		obj = new DesignacaoFuncaoProjeto();
		obj.setProjeto(new Projeto());
		obj.setTipoDesignacao(new TipoDesignacaoFuncaoProjeto());
		obj.setMembroProjeto(new MembroProjeto());
		if ( projeto == null )
			projeto = new Projeto();
	}
	
	@Override
	public String getDirBase() {
		return "/extensao/designacaoFuncaoProjeto";
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		obj.setProjeto(projeto);
		erros.addAll(obj.validate());
		erros.addAll(validaDesignacaoDuplicada(obj));
		
		if ( hasErrors() ) {
			return null;
		}
		
		Comando comando;
		if ( obj.getId() > 0 )
			comando = ArqListaComando.ALTERAR;
		else 
			comando = ArqListaComando.CADASTRAR;
		
		if ( !isOperacaoAtiva(ArqListaComando.ALTERAR.getId(), ArqListaComando.CADASTRAR.getId()) ) {
			addMensagemErro("A operação já havia sido concluída. Por favor, reinicie os procedimentos.");
			return super.cancelar();
		}
		
		prepareMovimento(comando);
		MovimentoCadastro mov = new MovimentoCadastro(obj, comando);
		execute(mov);
		addMensagem(OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		
		return cancelar();
	}
	
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
	
	@Override
	public String inativar() throws ArqException, NegocioException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), DesignacaoFuncaoProjeto.class) );
		if (obj.getId() == 0 && !obj.isAtivo()) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA);
		} else {
			prepareMovimento(ArqListaComando.DESATIVAR);
			MovimentoCadastro mov = new MovimentoCadastro(obj, ArqListaComando.DESATIVAR);
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		}
		
		return forward(getListPage());
	}
	
	/** Responsável por validar a designação que está sendo atribuida, verificando se o membro já apresenta.  */
	private ListaMensagens validaDesignacaoDuplicada(DesignacaoFuncaoProjeto obj) throws DAOException {
		ListaMensagens lista = new ListaMensagens();
		DesignacaoFuncaoProjetoDao dao = getDAO(DesignacaoFuncaoProjetoDao.class);
		try {
			Collection<DesignacaoFuncaoProjeto> designacoes = 
					dao.findDesignacoesMembroProjeto(obj.getMembroProjeto().getId(), obj.getProjeto().getId());

			for (DesignacaoFuncaoProjeto designacaoFuncaoProjeto : designacoes) {
				if ( obj.getMembroProjeto().getId() == designacaoFuncaoProjeto.getMembroProjeto().getId() && 
						obj.getTipoDesignacao().getId() == designacaoFuncaoProjeto.getTipoDesignacao().getId()) {
					lista.addErro("O membro " + designacaoFuncaoProjeto.getMembroProjeto().getNomeMembroProjeto() + " já" +
							"possui designação " + designacaoFuncaoProjeto.getTipoDesignacao().getDenominacao() + " cadastrada.");
				}
			}
			
			if ( obj.getProjeto().getCoordenador().getId() == obj.getMembroProjeto().getId() )
				lista.addErro("Não é possível atribuir designação ao coordenador do Projeto.");
			
		} finally {
			dao.close();
		}
		
		return lista;
	}

	@Override
	public String listar() throws ArqException {
		DesignacaoFuncaoProjetoDao dao = getDAO(DesignacaoFuncaoProjetoDao.class);
		try {
			setProjeto( dao.findProjetoLeve(getParameterInt("id", projeto.getId())) );
			clear();
			
			if ( !DesignacaoFuncaoProjetoHelper.isCoordenadorProjeto(projeto.getId(), 
					getUsuarioLogado().getPessoa().getId()) ) {
				addMensagemErro("Apenas o Coordenador podem acessar essa operação.");
				return null;
			}
			
		} finally {
			dao.close();
		}
		return forward(getListPage());
	}
	
	@Override
	public String atualizar() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), DesignacaoFuncaoProjeto.class) );
		if (!obj.isAtivo()) {
			addMensagem(MensagensArquitetura.ACAO_JA_EXECUTADA, "Designação","removida");
			return forward(getListPage());
		} else {
			return super.atualizar();
		}
	}
	
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		clear();
		setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		setConfirmButton("Cadastrar");
		     return forward(getFormPage());
	}
	
	/** Retorna todos os membros que podem ter designação cadastrada */
	public Collection<SelectItem> getAllMembros() throws ArqException {
		MembroProjetoDao dao = getDAO(MembroProjetoDao.class);
		try {
			return toSelectItems(dao.findAtivosByProjeto(projeto.getId(), CategoriaMembro.DOCENTE, CategoriaMembro.SERVIDOR), "id", "nomeMembroProjeto");
		} finally {
			dao.close();
		}
	}
	
	@Override
	public Collection<DesignacaoFuncaoProjeto> getAllAtivos() throws ArqException {
		return getGenericDAO().findAtivosByExactField(DesignacaoFuncaoProjeto.class, 
				"projeto.id", projeto.getId(), "dataCadastro", "desc");
	}

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}
	
}