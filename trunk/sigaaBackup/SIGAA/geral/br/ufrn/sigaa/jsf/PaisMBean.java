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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Gerado pelo CrudBuilder
 */

@Component("pais")
@Scope("request")
public class PaisMBean extends
SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.Pais> {
	public PaisMBean() {
		obj = new Pais();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(Pais.class, "id", "nome");
	}
	
	@Override
	public Collection<Pais> getAllPaginado() throws ArqException {
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
		Collection<Pais> mesmoPais = dao.findByExactField(Pais.class, "nome", obj.getNome());
		for (Pais p : mesmoPais) {
			if (p.getId() == obj.getId()) {
				return super.cadastrar();
			} if(p.getNome().equals(obj.getNome())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Pais");
				return null;
			}
		}
		return super.cadastrar();
	}

}