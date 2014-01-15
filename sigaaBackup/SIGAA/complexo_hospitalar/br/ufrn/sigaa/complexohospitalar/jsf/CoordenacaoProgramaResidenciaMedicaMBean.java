package br.ufrn.sigaa.complexohospitalar.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.complexohospitalar.dominio.CoordenacaoProgramaResidenciaMedica;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;

/**
 * MBean para gerenciar CoordenaçãoProgramaResidencia.
 * 
 * @author Jean Guerethes
 */
@Component("coordenacaoResidenciaMedica")
@Scope("request")
public class CoordenacaoProgramaResidenciaMedicaMBean extends AbstractControllerCadastro<CoordenacaoProgramaResidenciaMedica> {

	public CoordenacaoProgramaResidenciaMedicaMBean() {
		clear();
	}
	
	private void clear() {
		this.obj = new CoordenacaoProgramaResidenciaMedica();
		this.obj.setProgramaResidenciaMedica( new ProgramaResidenciaMedica() );
		this.obj.setServidor(new Servidor());
	}
	
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_COMPLEXO_HOSPITALAR, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA);
	}
	
	public String direcionar() throws SegurancaException{
		checkListRole();
		clear();
		return forward("/complexo_hospitalar/CoordenacaoProgramaResidenciaMedica/form.jsf");
	}

	@Override
	public String getDirBase() {
		String dir = "/complexo_hospitalar/CoordenacaoProgramaResidenciaMedica";
		return dir;
	}
	
	@Override
	public String remover() throws ArqException {
	
		int id = getParameterInt("id", 0);
		obj = getDAO(GenericDAOImpl.class).findByPrimaryKey(id, CoordenacaoProgramaResidenciaMedica.class);
		
		if (obj == null) {
			clear();
		}
		return super.remover();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		if (!(obj.getFim() == null)) {
			if (obj.getInicio().compareTo(obj.getFim()) >= 0) {
				addMensagemErro("A data Inicial é maior do que a data Final ");
				return null;
			}
		}
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
}