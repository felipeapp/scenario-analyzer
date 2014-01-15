/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.ensino.dominio.Turno;

public class TurnoMBean extends AbstractControllerCadastro<Turno> {

	public TurnoMBean() {
		obj = new Turno();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Turno.class, "id", "descricao");
	}


	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(Turno.class, "id", "descricao");
	}


	public boolean isSubSistemaGraduacao() {
		return getSubSistema().equals(SigaaSubsistemas.GRADUACAO);
	}
	
	@Override
	public String getListPage() {
		return "/administracao/cadastro/Turno/lista.jsf";
	}
	
	@Override
	public String getFormPage() {
		return "/administracao/cadastro/Turno/form.jsf";
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<Turno> mesmaTurno = dao.findByExactField(Turno.class, "descricao", obj.getDescricao());
		for (Turno t : mesmaTurno) {
			if (t.getId() == obj.getId()) {
				return super.cadastrar();
			} if(t.getDescricao().equals(obj.getDescricao()) && t.getSigla().equals(obj.getSigla())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Atividade Complementar");
				return null;
			}
		}
		return super.cadastrar();
	}
	
	@Override
	public Collection<Turno> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

}