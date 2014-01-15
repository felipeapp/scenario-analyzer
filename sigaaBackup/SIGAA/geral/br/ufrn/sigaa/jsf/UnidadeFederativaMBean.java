/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

public class UnidadeFederativaMBean extends
SigaaAbstractController<br.ufrn.sigaa.dominio.UnidadeFederativa> {
	
	public UnidadeFederativaMBean() {
		obj = new UnidadeFederativa();
		obj.setCapital(new Municipio());
	}

	public Collection<SelectItem> getAllCombo() throws DAOException {
		return getAll(UnidadeFederativa.class, "id", "descricao");
	}
	
	@Override
	public Collection<UnidadeFederativa> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<UnidadeFederativa> mesmaUF = dao.findByExactField(UnidadeFederativa.class, "descricao", obj.getDescricao());
		for (UnidadeFederativa uf : mesmaUF) {
			if (uf.getId() == obj.getId()) {
				return super.cadastrar();
			} if(uf.getDescricao().equals(obj.getDescricao()) && uf.getPais().getId() == obj.getPais().getId() 
						&& uf.getSigla().equals(obj.getSigla())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Unidade Federativa");
				return null;
			}
		}
		return super.cadastrar();
	}

}