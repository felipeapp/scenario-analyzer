/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 23/10/2006
 * 
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoRegimeAluno;

/**
 * Classe responsável por gerenciar as ações de inclusão, alteração e remoção dos tipos de regime do aluno.
 * 
 * @author Gleydson
 *
 */
public class TipoRegimeAlunoMBean extends
		AbstractControllerCadastro<TipoRegimeAluno> {

	public TipoRegimeAlunoMBean() {
		clear();
	}

	/**
	 * Método que inicializa os objetos envolvidos na classe.
	 */
	private void clear() {
		obj = new TipoRegimeAluno();
	}
	
	/**
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
	 * <br/>
	 * Chamado pelas JSP(s):
	 * <ul> 
	 * 	<li> /sigaa.war/administracao/cadastro/TipoRegimeAluno/lista.jsp </li>
	 * </ul>
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, TipoRegimeAluno.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getAll(),"id","descricao");
	}
	
	/**
	 * Para que o usuário seja redirecionado para a tela da listagem logo após um novo cadastro.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	@Override
	public Collection<TipoRegimeAluno> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoRegimeAluno> mesmoRegime = dao.findByExactField(TipoRegimeAluno.class, "descricao", obj.getDescricao());
		for (TipoRegimeAluno as : mesmoRegime) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Regime Aluno");
				return null;
			}
		}
		return super.cadastrar();
	}
	
}