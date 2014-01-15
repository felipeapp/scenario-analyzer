/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.InstituicoesEnsinoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Managed-Bean para o cadastro de instituições de ensino
 *
 */
@Scope("request")
@Component("instituicoesEnsino")
public class InstituicoesEnsinoMBean extends
		SigaaAbstractController<br.ufrn.sigaa.dominio.InstituicoesEnsino> {

	public InstituicoesEnsinoMBean() {
		obj = new InstituicoesEnsino();
		// coloca o brasil como país padrão
		Pais p = new Pais();
		p.setId(Pais.BRASIL);
		obj.setPais(p);

	}

	public Collection<SelectItem> getAllCombo() throws DAOException {
		InstituicoesEnsinoDao dao = getDAO(InstituicoesEnsinoDao.class);
		List<SelectItem> items = new ArrayList<SelectItem>();
		Collection<InstituicoesEnsino> lista = dao.findAll();
		for (InstituicoesEnsino i : lista)
			items.add(new SelectItem(i.getId(), i.getNome()));
		return items;
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.GESTOR_LATO, SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.DAE, SigaaPapeis.GESTOR_PESQUISA });
	}


	/* (non-Javadoc)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getDirBase()
	 */
	@Override
	public String getDirBase() {
		return "/administracao/cadastro/InstituicoesEnsino";
	}

	@Override
	public Collection<InstituicoesEnsino> getAllPaginado() throws ArqException {
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
		Collection<InstituicoesEnsino> mesmaInstituicao = dao.findByExactField(InstituicoesEnsino.class, "nome", obj.getNome());
		for (InstituicoesEnsino as : mesmaInstituicao) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getNome().equals(obj.getNome())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Instituição de Ensino");
				return null;
			}
		}
		return super.cadastrar(); 
	}
	
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, InstituicoesEnsino.class);
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

}