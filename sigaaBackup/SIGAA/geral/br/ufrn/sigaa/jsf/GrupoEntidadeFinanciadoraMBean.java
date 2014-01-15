/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '04/01/2007'
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
import br.ufrn.sigaa.projetos.dominio.GrupoEntidadeFinanciadora;

/**
 * 
 * @author Victor Hugo
 *
 */
public class GrupoEntidadeFinanciadoraMBean extends
		AbstractControllerCadastro<GrupoEntidadeFinanciadora> {
	public GrupoEntidadeFinanciadoraMBean() {
		obj = new GrupoEntidadeFinanciadora();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(GrupoEntidadeFinanciadora.class, "id", "nome");
	}

	@Override
	public Collection<GrupoEntidadeFinanciadora> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "nome";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<GrupoEntidadeFinanciadora> mesmaEntidade = dao.findByExactField(GrupoEntidadeFinanciadora.class, "nome", obj.getNome());
		for (GrupoEntidadeFinanciadora as : mesmaEntidade) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getNome().equals(obj.getNome())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Grupo Entidade Financiadora");
				return null;
			}
		}
		return super.cadastrar();
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, GrupoEntidadeFinanciadora.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}