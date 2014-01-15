package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.rh.ServidorDao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.portal.jsf.PortalPublicoDocenteMBean;

@Component("buscaDocenteTouch") @Scope("request")
public class BuscaDocenteTouchMBean extends SigaaTouchAbstractController<Servidor> {
	
	private Collection<Servidor> docentes;
	
	private String nomeBusca;
	
	private Unidade unidadeBusca;
	
	public BuscaDocenteTouchMBean(){
		init();
	}
	
	private void init() {
		obj = new Servidor();
		
		unidadeBusca = new Unidade();
	}

	public String iniciarBusca() {
		return forward("/mobile/touch/public/busca_docente.jsf");
	}
	
	public String buscarDocentes() throws DAOException {
		if (StringUtils.isEmpty(nomeBusca) && isEmpty(unidadeBusca)) {
			addMensagemErro("Especifique um nome ou escolha uma unidade para realizar a busca");
			return null;
		}

		if ( !StringUtils.isEmpty(nomeBusca) ) {
			if (nomeBusca.length() < PortalPublicoDocenteMBean.MINIMO_NOME_DOCENTE) {
				addMensagemErro("É necessário informar pelo menos 4 caracteres para buscar pelo nome");
				return null;
			}
		}
		
		ServidorDao servidorDao = getDAO(ServidorDao.class);
		docentes = servidorDao.findDocenteByUnidadeNome(unidadeBusca.getId(), nomeBusca);

		if ( docentes.isEmpty() ) {
			addMensagemErro("Nenhum docente foi encontrado de acordo com os critérios de busca informados");
			return null;
		}

		return forward("/mobile/touch/public/lista_docentes.jsf");
	}

	public Collection<Servidor> getDocentes() {
		return docentes;
	}

	public void setDocentes(Collection<Servidor> docentes) {
		this.docentes = docentes;
	}

	public String getNomeBusca() {
		return nomeBusca;
	}

	public void setNomeBusca(String nomeBusca) {
		this.nomeBusca = nomeBusca;
	}

	public Unidade getUnidadeBusca() {
		return unidadeBusca;
	}

	public void setUnidadeBusca(Unidade unidadeBusca) {
		this.unidadeBusca = unidadeBusca;
	}

}
