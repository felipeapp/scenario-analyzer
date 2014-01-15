/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/10/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;
import java.util.Iterator;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.TipoCursoEventoExtensao;

/**
 * 
 * MBean para controle dos tipos de Cursos e Eventos cadastrados
 * 
 * @author Victor Hugo
 * @author Ricardo Wendell
 * @author Ilueny Santos
 * 
 */
@Component("tipoCursoEventoExtensao") @Scope("session")
public class TipoCursoEventoExtensaoMBean extends SigaaAbstractController<TipoCursoEventoExtensao> {

	public TipoCursoEventoExtensaoMBean() {
		obj = new TipoCursoEventoExtensao();
	}

	/**
	 * Utilizado para mostrar os tipos de curso/evento ativos
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(TipoCursoEventoExtensao.class, "id", "descricao");
	}

	/**
	 * 
	 * Utilizado para mostrar os tipos de curso ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoCombo() throws DAOException {
		Collection<TipoCursoEventoExtensao> tipos = getGenericDAO().findByExactField(TipoCursoEventoExtensao.class,
					"escopo", 'C',	"asc", "descricao");
		for (Iterator<TipoCursoEventoExtensao> iterator = tipos.iterator(); iterator.hasNext();) {
			if (!iterator.next().isAtivo()) {
				iterator.remove();
			}
		}

		return toSelectItems(tipos, "id", "descricao");
	}

	
	/**
	 * 
	 * Utilizado para mostrar os tipos de evento ativos
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllEventoCombo() throws DAOException {

		Collection<TipoCursoEventoExtensao> tipos = getGenericDAO().findByExactField(TipoCursoEventoExtensao.class,
			"escopo", 'E',	"asc", "descricao");
		for (Iterator<TipoCursoEventoExtensao> iterator = tipos.iterator(); iterator.hasNext();) {
			if (!iterator.next().isAtivo()) {
				iterator.remove();
			}
		}
		return toSelectItems(tipos, "id", "descricao");
	}

	
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			notifyError(e);
		}
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		super.beforeInativar();
	}

	@Override
	public java.util.Collection<TipoCursoEventoExtensao> getAllAtivos()throws ArqException {
		return getGenericDAO().findByExactField(TipoCursoEventoExtensao.class, "ativo", Boolean.TRUE);
	}

	/**
	 * Inicia o cadastro de tipos de curso e evento.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/TipoCursoEventoExtensao/form.jsp");
	}
}
