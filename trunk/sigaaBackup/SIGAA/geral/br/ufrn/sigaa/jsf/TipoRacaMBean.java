/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.TipoRaca;

/** Controller respons�vel por cadastro de tipo de ra�as.
 * Gerado pelo CrudBuilder
 */
public class TipoRacaMBean extends
SigaaAbstractController<br.ufrn.sigaa.pessoa.dominio.TipoRaca> {

	/** Cole��o de tipo de ra�as v�lidas. */
	private Collection<SelectItem> tiposValidos;
	
	/** Construtor padr�o. */
	public TipoRacaMBean() {
		obj = new TipoRaca();
	}

	/** Retorna uma cole��o de SelectItem de tipos de ra�as.
	 * @return
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoRaca.class, "id", "descricao");
	}
	
	/** Retorna uma cole��o de SelectItem de tipos de ra�as v�lidos.
	 * @return
	 */
	public Collection<SelectItem> getAllValidoCombo() throws DAOException {
		if (tiposValidos == null) {
			tiposValidos = new ArrayList<SelectItem>();
			for (TipoRaca tipo : getGenericDAO().findAll(TipoRaca.class, "descricao", "asc")) {
				if (tipo.getId() > 0)
					tiposValidos.add(new SelectItem(tipo.getId(), tipo.getDescricao()));
			}
		}
		return tiposValidos;
	}
	
	/** Retorna uma cole��o de tipos de ra�as, paginado.
	 * @return
	 * @throws ArqException
	 */
	@Override
	public Collection<TipoRaca> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	/** Retorna o atributo que determina a ordem dos elementos de uma lista de tipos de ra�a.
	 * @return
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	/**
	 * Cadastra um tipo de ra�a.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/cadastro/TipoRaca/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
		NegocioException {

		Collection<TipoRaca> raca = getGenericDAO().findByExactField(TipoRaca.class, "descricao", obj.getDescricao());
		
		if (!raca.isEmpty()) {
		addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO,"Tipo Ra�a");
		return null;
		}
		return super.cadastrar();
	}
	
	/** Remove um tipo de ra�a.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/cadastro/TipoRaca/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoRaca.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

}