package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.bolsas.dominio.IntegracaoTipoBolsa;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Representação do tipo de bolsa no SIGAA para o SIPAC.
 *  
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class IntegracaoTipoBolsaMBean extends SigaaAbstractController<IntegracaoTipoBolsa> {

	public IntegracaoTipoBolsaMBean() {
		clear();
	}

	private void clear() {
		obj = new IntegracaoTipoBolsa();
		obj.setUf("");
		obj.setMunicipio("");
		all = null;
	}
	
	@Override
	public String listar() throws ArqException {
		clear();
		return super.listar();
	}
	
	@Override
	public String getDirBase() {
		return "/administracao/cadastro/IntegracaoTipoBolsa";
	}
	
	public Collection<SelectItem> getAllUfCombo() throws DAOException {
		return getAll(UnidadeFederativa.class, "sigla", "descricao");
	}
	
	@Override
	protected String forwardInativar() {
		clear();
		return "/administracao/cadastro/IntegracaoTipoBolsa/lista.jsp";
	}
	
	@Override
	public String inativar() throws ArqException, NegocioException {
		prepareMovimento(ArqListaComando.DESATIVAR);
		return super.inativar();
	}
	
	/**
	 * Retorna uma coleção de {@link SelectItem} referentes aos municípios cuja unidade federativa seja o RN.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllMunicipiosUf() throws DAOException {
		List<SelectItem> itens = new ArrayList<SelectItem>();
			if ( obj.getUf() != null && !obj.getUf().isEmpty() ) {
				MunicipioDao dao = getDAO(MunicipioDao.class);
				try {
					Collection<Municipio> municipios = dao.findByUf(obj.getUf());
					itens = toSelectItems(municipios, "nome", "nome");
				} finally {
					dao.close();
				}
			}
		return itens;
	}
	
}