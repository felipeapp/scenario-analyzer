package br.ufrn.sigaa.apedagogica.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Controller que gerencia a operação de alteração da situação da inscrição.
 * @author Mário Rizzi
 *
 */
@Component("geracaoListaPresencaAP") @Scope("request")
public class GeracaoListaPresencaAPMBean  extends SigaaAbstractController<AtividadeAtualizacaoPedagogica> 
	implements OperadorAtividadeAP{

	/** Lista de participantes para impressão da lista de presença. */
	private Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes;
	
	public GeracaoListaPresencaAPMBean(){
		obj = new AtividadeAtualizacaoPedagogica();
		participantes = new ArrayList<ParticipanteAtividadeAtualizacaoPedagogica>();
	}
	
	/**
	 * Gera uma lista contendo todos os docentes inscritos para uma determinada atividade
 	 * <br /> 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
 	 * <ul>
 	 *    <li>sigaa.war/apedagogica/geral.jsp</li>
 	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws SegurancaException 
	 */
	public String consultar() throws DAOException, SegurancaException{
		
		checkChangeRole();
		
		ConsultaAtividadeAPMBean consultaMBean = (ConsultaAtividadeAPMBean) getMBean("consultaAtividadeAP");
		consultaMBean.setCodigoOperacao(OperacaoAtividadeAP.GERA_LISTA_PRESENCA);
		consultaMBean.setAll(null);
		consultaMBean.setResultadosBusca(null);
		
		return forward( consultaMBean.getFormPage() );
		
	}
	
	@Override
	public String selecionaAtividade() {
		
		try {
			participantes = getGenericDAO().findByExactField(ParticipanteAtividadeAtualizacaoPedagogica.class, "atividade.id", obj.getId(), "ASC", "docente.pessoa.nome");
			if( isEmpty(participantes) )
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS, "Participantes");
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}	
		
		if( hasErrors() )
			return null;
		
		return forward(getListPage());
		
	}
	
	@Override
	public String getDirBase() {
		// TODO Auto-generated method stub
		return "/apedagogica/GeracaoListaPresencaAP";
	}

	@Override
	public void setAtividade(AtividadeAtualizacaoPedagogica atividade) {
		obj = atividade;
	}

	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(
			Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes) {
		this.participantes = participantes;
	}

}
