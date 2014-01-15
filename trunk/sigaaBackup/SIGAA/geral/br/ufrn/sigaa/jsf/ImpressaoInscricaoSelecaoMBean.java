/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/03/2010
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.LoteInscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.dominio.StatusInscricaoSelecao;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;


/**
 * Classe Manged Bean responsável em exibir todos os dados dos inscritos que não 
 * possuem status CANCELADO de um processo seletivo.
 * @author sist-sigaa-12
 *
 */
@Component("impressaoInscricaoSelecao") @Scope("session") 
public class ImpressaoInscricaoSelecaoMBean extends AbstractController{
	
	private List<LoteInscricaoSelecao> loteInscricaoSelecao = new ArrayList<LoteInscricaoSelecao>();
	private ProcessoSeletivo processoSeletivo;
	
	public ImpressaoInscricaoSelecaoMBean(){
	}
	
	/**
	 * Imprime em lote os Inscritos no processo seletivo.
	 * <br /> 
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/administracao/cadastro/ProcessoSeletivo/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String imprimirInscritos() throws SegurancaException, ArqException,
		NegocioException {
		
		//Recebe o id do processo seletivo enviado por GET
		Integer idProcesso = getParameterInt("id");
		Collection<InscricaoSelecao> inscricoes = new ArrayList<InscricaoSelecao>();
		
		if(idProcesso > 0)
			setProcessoSeletivo(getGenericDAO().findByPrimaryKey(idProcesso,ProcessoSeletivo.class));
				
		if (processoSeletivo == null)
			addMensagemErro("Não já removido ou inexistente");
		else{
			//Recebe a coleção de inscritos do processo seletivo acima.
			inscricoes = 
				getGenericDAO().findByExactField(InscricaoSelecao.class, "processoSeletivo.id", 
						getProcessoSeletivo().getId());
		}
		
		if (inscricoes.isEmpty())
			addMensagemErro("Não foram encontradas inscrições para este processo seletivo");
		
		if(hasErrors())
			return null;
		
		//Popula o objeto que exibirá a lista de inscritos com seus DataModels
		populaLoteInscricaoSelecao(inscricoes);
				
		return forward("/administracao/cadastro/ProcessoSeletivo/view_inscrito_lote.jsf");
	}
	
	
	/**
	 * Popula o objeto para listagem na view.
	 * @throws DAOException 
	 */
	private void populaLoteInscricaoSelecao(Collection<InscricaoSelecao> inscricoes) 
		throws DAOException{
		
		loteInscricaoSelecao = new ArrayList<LoteInscricaoSelecao>();
		QuestionarioRespostas questionariosRespostas = null;
		LoteInscricaoSelecao loteIS = null;
		
		for (InscricaoSelecao i : inscricoes) {

			if(StatusInscricaoSelecao.CANCELADA != i.getStatus()){
		
				questionariosRespostas = 
					getDAO(QuestionarioRespostasDao.class).findByInscricaoSelecao(i);
				
				if(questionariosRespostas!=null){
					loteIS = new LoteInscricaoSelecao(i,
							new ListDataModel(CollectionUtils.toList(questionariosRespostas.getRespostas())));
				}else{
					loteIS = new LoteInscricaoSelecao(i, null);
				}

				loteInscricaoSelecao.add(loteIS);
				
			}
			
		}
		
	}

	public List<LoteInscricaoSelecao> getLoteInscricaoSelecao() {
		return loteInscricaoSelecao;
	}

	public ProcessoSeletivo getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivo processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}
		
}
