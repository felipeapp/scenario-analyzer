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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;

/**
 * MBean para entidade de Grau acadêmico.
 *
 * @author Rafael G. Rodrigues
 *
 */
public class GrauAcademicoMBean extends AbstractControllerCadastro<GrauAcademico> {
	
	public GrauAcademicoMBean() {
		obj = new GrauAcademico();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(GrauAcademico.class, "id", "descricao");
	}
	
	@Override
	public String getFormPage() {
		return "/administracao/cadastro/GrauAcademico/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/administracao/cadastro/GrauAcademico/lista.jsf";
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<GrauAcademico> mesmaGrau = dao.findByExactField(GrauAcademico.class, "descricao", obj.getDescricao());
		for (GrauAcademico as : mesmaGrau) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Grau Acadêmico");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), GrauAcademico.class);
		if (obj == null) {
			obj = new GrauAcademico();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return forward(getListPage());
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
	@Override
	public String atualizar() throws ArqException {
		setId();
		GrauAcademico grau = getGenericDAO().findByPrimaryKey(obj.getId(), GrauAcademico.class, "id");
		if (grau == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return forward(getListPage());
		}
		return super.atualizar();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	/** Retorna o link para a página ao qual o usuário será redirecionado após cadastrar o grau acadêmico.
	 * <br/>Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
}