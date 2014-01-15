package br.ufrn.sigaa.ensino.latosensu.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParametrosPropostaCursoLato;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * @author Jean Guerethes
 */
@Component @Scope("request")
public class ParametrosPropostaCursoLatoMBean extends AbstractControllerAtividades<ParametrosPropostaCursoLato> {

	public ParametrosPropostaCursoLatoMBean() {
		clear();
	}

	private void clear() {
		obj = new ParametrosPropostaCursoLato(); 		
	}
	
	@Override
	public String getDirBase() {
		return "/lato/parametros";
	}
	
	/**
	 * Carrega a última 
	 */
	@Override
	public String preCadastrar() {
		clear();
		try {
			setObj((ParametrosPropostaCursoLato) getGenericDAO().findLast(ParametrosPropostaCursoLato.class));
			if (obj == null)
				clear();
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return forward(getFormPage());
	}

	/**
	 * Cadastro dos parâmtros para a verificação da proposta dos Cursos lato Sensu.
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		if (obj.getId() != 0){ 
			setOperacaoAtiva(ArqListaComando.ALTERAR.getId());
			prepareMovimento(ArqListaComando.ALTERAR);
		} else {
			setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
			prepareMovimento(ArqListaComando.CADASTRAR);
		}
		super.cadastrar();
		return cancelar();
	}
	
}