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
import br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora;

public class ClassificacaoFinanciadoraMBean
		extends
		AbstractControllerCadastro<br.ufrn.sigaa.projetos.dominio.ClassificacaoFinanciadora> {
	public ClassificacaoFinanciadoraMBean() {
		obj = new ClassificacaoFinanciadora();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(ClassificacaoFinanciadora.class, "id", "descricao");
	}
	
	@Override
	public Collection<ClassificacaoFinanciadora> getAllPaginado() throws ArqException {
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
		Collection<ClassificacaoFinanciadora> mesmaFinanciadora = dao.findByExactField(ClassificacaoFinanciadora.class, "descricao", obj.getDescricao());
		for (ClassificacaoFinanciadora as : mesmaFinanciadora) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Classificação Financiadora");
				return null;
			}
		}
		return super.cadastrar();
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, ClassificacaoFinanciadora.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
}