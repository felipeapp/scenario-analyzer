/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/12/2007
 * 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoGenerico;

/**
 * ManagedBean respons�vel pelo tratamento e controle do caso de uso referente a Integraliza��o dos cr�ditos dos alunos
 * oriundos de migra��o do antigo sistema acad�mico.
 * @author leonardo
 *
 */
public class IntegralizacaoAlunoMigradoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	public IntegralizacaoAlunoMigradoMBean() {
		
	}
	
	/**
	 * Redirecionar para o Managed Bean para a busca de discentes de gradua��o
	 *
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul><li>
	 * 	/sigaa.war/graduacao/menus/administracao.jsp
	 * </li></ul>
	 * @return
	 */
	public String buscarDiscente() throws SegurancaException {
		checkRole(new int[]{SigaaPapeis.ADMINISTRADOR_DAE});
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.INTEGRALIZAR_ALUNO_MIGRADO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * M�todo respons�vel por popular o objeto {@link DiscenteAdapter} e a chamada ao processador com a fun��o de realizar 
	 * a valida��o e integraliza��o dos cr�ditos do aluno.
	 * <br>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *  <ul><li>
	 * 	/sigaa.war/graduacao/busca_discente.jsp
	 * </li></ul>
	 */
	public String selecionaDiscente() throws ArqException {
		checkRole(new int[]{SigaaPapeis.ADMINISTRADOR_DAE});
		try {
			prepareMovimento(SigaaListaComando.INTEGRALIZAR_ALUNO_MIGRADO);
			
			DiscenteDao dao = getDAO(DiscenteDao.class);
			if(!dao.isAlunoConcluidoCreditoPendente(obj)){
				addMensagemErro("O aluno selecionado n�o possui cr�ditos pendentes.");
				return null;
			}
			
			List<MatriculaComponente> disciplinas = dao.findDisciplinasConcluidasMatriculadas(obj.getId(), true);
			Collection<ComponenteCurricular> lista = dao.findByDisciplinasCurricularesPendentes(obj.getId(), disciplinas, new ArrayList<TipoGenerico>());
			if(lista != null && !lista.isEmpty()){
				addMensagemErro("O aluno selecionado ainda possui componentes obrigat�rios pendentes.");
				return null;
			}
			
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.INTEGRALIZAR_ALUNO_MIGRADO);
			mov.setObjMovimentado(obj);
			
			execute(mov, getCurrentRequest());
			
			addMessage("Os cr�ditos pendentes do aluno foram integralizados com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			return "menuGraduacao";
		} catch (NegocioException e) {
			addMensagens( e.getListaMensagens() );
			return null;
		}
	}

	/** 
	 * Seta o discente escolhido da lista de resultados da busca.
	 * 
	 * <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *   <li>N�o Invocado por JSP.</li>
	 * </ul>
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj = discente;		
	}

}
