/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.projetos.dominio.GrupoPublicoAlvo;
import br.ufrn.sigaa.projetos.dominio.TipoPublicoAlvo;

/**
 * Managed-Bean para operações de tipo de público alvo
 *
 * @author Gleydson
 * @author ilueny santos
 *
 */
@Component(value = "tipoPublicoAlvo")
@Scope(value = "request")
public class TipoPublicoAlvoMBean extends SigaaAbstractController<TipoPublicoAlvo> {
	
	
	public TipoPublicoAlvoMBean() {
		obj = new TipoPublicoAlvo();
	}

	/**
	 * lista de grupos de tipos
	 * @return
	 * @throws DAOException
	 */
	public Collection<GrupoPublicoAlvo> getGrupos() throws DAOException {
		return getGenericDAO().findAllAtivos(GrupoPublicoAlvo.class, "descricao");
	}
	
	public Collection<SelectItem> getAllGrupoCombo() { 
		return getAllAtivo(GrupoPublicoAlvo.class, "id", "descricao");
	}

	
	/**
	 * Retorna todos os tipos de públicos alvo ordenados por grupo
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<TipoPublicoAlvo> getAllTipoPublicoAlvo() throws DAOException { 
		return getGenericDAO().findAllAtivos(TipoPublicoAlvo.class, "grupo");
	}
	
	/**
	 * Método chamado para entrar no modo de remoção
	 * 
	 * sigaa.ear/sigaa.war/extensao/TipoPublicoAlvo/lista.jsp
	 *
	 * @return
	 */
	public String preInativar() {

		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
			setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
			populateObj(true);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
		}

		setReadOnly(true);
		setConfirmButton("Remover");
		return forward(getFormPage());
	}
	
	@Override
	public Collection<TipoPublicoAlvo> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoPublicoAlvo.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		super.remover();

		if (hasErrors()) {
			return forward(getListPage());
		}
		return null;
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoPublicoAlvo> mesmoTipo = dao.findByExactField(TipoPublicoAlvo.class, "descricao", obj.getDescricao());
		for (TipoPublicoAlvo as : mesmoTipo) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao()) && as.getGrupo().getId() == obj.getGrupo().getId()){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Público Alvo");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	/**
	 * Inicia o cadastro de tipos de Público Alvo.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/TipoPublicoAlvo/form.jsp");
	}
	
}