/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.ChefiaDao;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Chefia;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoChefia;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Serve para cadastrar a chefia para um Docente, informando a data do documento, a data da publicação,
 * a data fim da validade da chefia designada ao docente, informando também se a chefia é remunerada ou não e qual o 
 * tipo de chefia que o Docente vai exercer.
 */
public class ChefiaMBean extends AbstractControllerAtividades<Chefia> {
	public ChefiaMBean() {
		obj = new Chefia();
		obj.setServidor(new Servidor());
		obj.setAutoridade(new Servidor()); //O autocompletation Ajax da JSP precisa que este campo esteja instanciado no construtor, para poder chamar o método setID do mesmo
		obj.setTipoChefia( new TipoChefia());
		setBuscaServidor(true);
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Chefia.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new Chefia();
	}

	/**
	 * Para verificar se o usuário tem a permissão necessária para acessar as funcionalidades.
	 * 
	 *  JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/abas/propesq.jsp
	 */
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO);
	}
	
	/**
	 * Para verificar se o usuário tem a permissão necessária para acessar as funcionalidades.
	 * 
	 *  JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/abas/propesq.jsp
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkListRole();
	}

	/**
	 * Para evitar que o não seja encontrada a página, foi colocado o endereço da mesma.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/atividades/Chefia/form.jsp
	 */
	@Override
	public String getListPage() {
		return "/prodocente/atividades/Chefia/lista.jsf";
	}
	
	/**
	 * Para quando o usuário cancelar a operação o mesmo seja direcionado para a tela da listagem.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/atividades/Chefia/form.jsp
	 */
	@Override
	public String cancelar() {
		return forward(getListPage());	
	}
	
	/**
	 * Serve para setar para nulo a autoridade, caso a mesma não tenho sido informada, assim evitando
	 * nullPointer na hora do cadastro.
	 * 
	 * Não invocada por JSP.
	 */
	@Override
	public void beforeCadastrarAfterValidate(){
		if (obj.getAutoridade() != null && obj.getAutoridade().getId()<=0)
			obj.setAutoridade(null);
	}

	/**
	 * Serve pra buscar todas as chefias cadastradas levando em conta a unidade do Docente logado e 
	 * a data final da validade da chefia exercida pelo docente.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<Chefia> getAll() {
		ChefiaDao dao = getDAO(ChefiaDao.class);
		try {
			return (Collection<Chefia>) dao.findAllUnidade(obj.getClass(), getUsuarioLogado().getVinculoAtivo().getUnidade());
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return atividades;
	}

	/**
	 * Serve para setar o campo ativo para false, evitando assim que o registro seja removido 
	 * completamente do banco de dados.
	 * 
	 * JSP: /SIGAA/app/sigaa.ear/sigaa.war/prodocente/abas/propesq.jsp
	 */
	@Override
	public String inativar() throws ArqException, NegocioException {
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		return super.inativar();
	}
	
}