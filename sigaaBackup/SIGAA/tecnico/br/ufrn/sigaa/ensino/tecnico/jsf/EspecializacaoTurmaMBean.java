/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/06/2007
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;

/**
 * Cadastro de especializa��es de turma de entrada do ensino t�cnico.
 *
 * O acesso a esse caso de uso � feito no menu de gestor do ensino t�cnico
 * @author Andre M Dantas
 *
 */

@Component("especializacaoTurma") @Scope("request")
public class EspecializacaoTurmaMBean extends SigaaAbstractController<EspecializacaoTurmaEntrada> {

	public EspecializacaoTurmaMBean() {
		obj = new EspecializacaoTurmaEntrada();
	}

	/**
	 * Retorna uma cole��o de todas as Especializa��es.
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(EspecializacaoTurmaEntrada.class, "id", "descricao");
	}
	
	@Override
	public Collection<EspecializacaoTurmaEntrada> getAllPaginado()
			throws ArqException {
		return getGenericDAO().findByExactField(EspecializacaoTurmaEntrada.class, "unidade.id", getUsuarioLogado().getUnidade().getId(), getPaginacao());
	}
	
	/**
	 * Retorna uma cole��o de Especializa��es de uma determinada Unidade Gestora.
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/ensino/tecnico/turma/dados_gerais.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public Collection<SelectItem> getEspecializacoesUnidadeGestoraCombo() throws DAOException, ArqException {
		GenericDAO dao = getGenericDAO();
		Collection<EspecializacaoTurmaEntrada> itens = dao.findByExactField(EspecializacaoTurmaEntrada.class, "unidade.id",getUnidadeGestora(), "asc", "descricao");
		return toSelectItems(itens,"id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws NegocioException, SegurancaException, DAOException {
		try {
			obj.setUnidade(new Unidade(getUnidadeGestora()));
		} catch (ArqException e) {
			throw new DAOException(e);
		}
		checkRole(SigaaPapeis.GESTOR_TECNICO,SigaaPapeis.GESTOR_MEDIO,SigaaPapeis.GESTOR_INFANTIL, SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR);
		super.beforeCadastrarAndValidate();
	}

	@Override
	public String getFormPage() {
		return "/administracao/cadastro/EspecializacaoTurmaEntrada/form.jsf";
	}

	@Override
	public String getListPage() {
		return "/administracao/cadastro/EspecializacaoTurmaEntrada/lista.jsf";
	}

	/**
	 * Verifica se as especializa��es de turma de entrada s�o utilizadas pela
	 * unidade gestora acad�mica atual. Ou seja, se no �mbito da unidade gestora
	 * acad�mica atualmente definida em sess�o existe o conceito de
	 * especializa��es de turma de entrada.
	 * @return
	 * @throws DAOException
	 */
	public boolean isUtilizadoPelaGestora() throws DAOException {
		return getParametrosAcademicos().isEspecializacaoTurmaEntrada();
	}
	
	/** Pre remo��o de uma turma de Especializa��o. */
	@Override
	public String preRemover() {
		setReadOnly(false);
		return super.preRemover();
	}
	
}
