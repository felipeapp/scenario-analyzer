/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.pessoa.dominio.TipoProcedenciaAluno;

public class TipoProcedenciaAlunoMBean extends
		AbstractControllerCadastro<br.ufrn.sigaa.pessoa.dominio.TipoProcedenciaAluno> {

	public TipoProcedenciaAlunoMBean() {
		obj = new TipoProcedenciaAluno();
	}
	
	@Override
	public Collection<TipoProcedenciaAluno> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoProcedenciaAluno> mesmaProcedencia = dao.findByExactField(TipoProcedenciaAluno.class, "descricao", obj.getDescricao());
		for (TipoProcedenciaAluno as : mesmaProcedencia) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo de Procedência");
				return null;
			}
		}
		return super.cadastrar();
	}

	@Override
	public String remover() throws ArqException {
		Integer id = getParameterInt("id", 0);
		obj = getGenericDAO().findByPrimaryKey(id, TipoProcedenciaAluno.class);
		if (obj == null) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return null;
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}