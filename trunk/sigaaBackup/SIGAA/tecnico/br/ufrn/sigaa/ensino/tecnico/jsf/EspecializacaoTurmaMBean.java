/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Cadastro de especializações de turma de entrada do ensino técnico.
 *
 * O acesso a esse caso de uso é feito no menu de gestor do ensino técnico
 * @author Andre M Dantas
 *
 */

@Component("especializacaoTurma") @Scope("request")
public class EspecializacaoTurmaMBean extends SigaaAbstractController<EspecializacaoTurmaEntrada> {

	public EspecializacaoTurmaMBean() {
		obj = new EspecializacaoTurmaEntrada();
	}

	/**
	 * Retorna uma coleção de todas as Especializações.
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
	 * Retorna uma coleção de Especializações de uma determinada Unidade Gestora.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Verifica se as especializações de turma de entrada são utilizadas pela
	 * unidade gestora acadêmica atual. Ou seja, se no âmbito da unidade gestora
	 * acadêmica atualmente definida em sessão existe o conceito de
	 * especializações de turma de entrada.
	 * @return
	 * @throws DAOException
	 */
	public boolean isUtilizadoPelaGestora() throws DAOException {
		return getParametrosAcademicos().isEspecializacaoTurmaEntrada();
	}
	
	/** Pre remoção de uma turma de Especialização. */
	@Override
	public String preRemover() {
		setReadOnly(false);
		return super.preRemover();
	}
	
}
