/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 24/10/2006
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.TipoCampusUnidade;

/**
 * Managed Bean do tipos de campus Unidade, responsável pelas seguintes operações 
 * cadastro, atualização, remoção de um discente.
 * 
 * @author gleydson
 */
@Component("tipoCampusUnidade")
@Scope("request")
public class TipoCampusUnidadeMBean extends 
						AbstractControllerCadastro<br.ufrn.sigaa.ensino.dominio.TipoCampusUnidade> {
	
	/** Construtor padrão. */
	public TipoCampusUnidadeMBean() {
		obj = new TipoCampusUnidade();
	}
	
	/** Retorna o descrição do combo */
	@Override
	public String getLabelCombo() {
		return "descricao";
	}
	
	/** Retorna todos os tipos de Campus da Unidade paginando-os em um limite máximo por página. */
	@Override
	public Collection<TipoCampusUnidade> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	/** Retorna o atributo que vai servir como ordenação para os tipos de Campus Unidade */
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	/**  
	 * Responsável pelo cadastro de um novo tipo de campus Unidade. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/cadastro/TipoCampusUnidade/form.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoCampusUnidade> mesmoCampus = dao.findByExactField(TipoCampusUnidade.class, "descricao", obj.getDescricao());
		for (TipoCampusUnidade as : mesmoCampus) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Campus Unidade");
				return null;
			}
		}
		return super.cadastrar();
	}

	/** 
	 * Responsável pela remoção de um Tipo Campus Unidade. <br>
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/cadastro/TipoCampusUnidade/lista.jsp</li>
	 *	</ul>
	 */
	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoCampusUnidade.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}

}