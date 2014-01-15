package br.ufrn.sigaa.pesquisa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.jsf.CursoMBean;
import br.ufrn.sigaa.pesquisa.dominio.CursosTecnologicos;

/**
 * Controlador para as operações sobre as Cursos Tecnologicos. 
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class CursosTecnologicosMBean extends SigaaAbstractController<CursosTecnologicos> {

	/** Construtor Padrão */
	public CursosTecnologicosMBean(){
		obj = new CursosTecnologicos();
		obj.setCurso( new Curso() );
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		Collection<CursosTecnologicos> cursosCadastrados = getGenericDAO().findAll(CursosTecnologicos.class);
		for (CursosTecnologicos cursosTecnologicos : cursosCadastrados) {
			if (cursosTecnologicos.getCurso().equals(obj.getCurso())) {
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Curso");
				break;
			}
		}
		
		if (hasErrors())
			return null;
		else
			return super.cadastrar();
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), CursosTecnologicos.class) );
		if ( isEmpty( obj ) ) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return cancelar();
		} else {
			prepareMovimento(ArqListaComando.REMOVER);
			return super.remover();
		}
	}

	@Override
	public String atualizar() throws ArqException {
		setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
		prepareMovimento(ArqListaComando.ALTERAR);
		setId();
		
		obj = getGenericDAO().findAndFetch(obj.getId(), CursosTecnologicos.class, "curso");

		setConfirmButton("Alterar");
		afterAtualizar();

		return forward(getFormPage());
	}
	
	@Override
	public String getDirBase() {
		return "/pesquisa/cursosTecnologicos";
	}
	
	/**
	 * Método que possibilitar a criação de autocompletes de todos os Curso.
	 * <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/cursosTecnologicos/form.jsp</li>
	 * </ul>
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<Curso> autocompleteNomeGeralCursos(Object event) throws DAOException {
		CursoMBean mBean = getMBean("curso");
		return mBean.autocompleteNomeGeralCursos(event);  			
	}	
	
	
}