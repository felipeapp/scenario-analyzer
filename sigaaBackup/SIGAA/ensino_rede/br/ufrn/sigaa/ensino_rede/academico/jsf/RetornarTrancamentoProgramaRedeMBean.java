package br.ufrn.sigaa.ensino_rede.academico.jsf;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino_rede.academico.dao.MovimentacaoDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.dominio.TipoMovimentacao;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscenteMBean;

@SuppressWarnings("serial")
@Component @Scope("session")
public class RetornarTrancamentoProgramaRedeMBean extends EnsinoRedeAbstractController<MovimentacaoDiscenteAssociado> implements SelecionaDiscente {

	public void clear() {
		obj = new MovimentacaoDiscenteAssociado();
	}
	
	public String iniciar() throws DAOException, NegocioException {
		clear();
		
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.setRequisitor(this);
		
		mBean.filterByStatus(new ArrayList<StatusDiscenteAssociado>() {{
			add(new StatusDiscenteAssociado(StatusDiscenteAssociado.TRANCADO));
		}});
		
		if (isCoordenadorUnidadeRede())
			return mBean.executar(getCampusIes());
		else
			return mBean.executar();
	}

	@Override
	public void setDiscente(DiscenteAssociado discente) {
		obj.setDiscente(discente);
	}

	@Override
	public String selecionaDiscente() throws ArqException {
		
		MovimentacaoDiscenteAssociadoDao dao = getDAO(MovimentacaoDiscenteAssociadoDao.class);
		setResultadosBusca( dao.findTrancamentosNaoRetornados(obj.getDiscente()) );
		
		if( ValidatorUtil.isEmpty( getResultadosBusca() )  ){
			addMensagemErro("O Discente não possui Trancamento Ativo");
			return null;
		}

		
		prepareMovimento(SigaaListaComando.RETORNAR_TRANCAMENTO_PROGRAMA_ENSINO_REDE);
		
		return forward("/ensino_rede/retornar_trancamento/lista.jsp");
	}

	public String selecionarMovimentacao() throws DAOException {
		Integer id = getParameterInt("idMovimentacao");
		obj = getGenericDAO().findByPrimaryKey(id, MovimentacaoDiscenteAssociado.class);
		return forward("/ensino_rede/retornar_trancamento/confirmacao.jsp");
	}

	public String cadastrar() throws NegocioException, ArqException  {
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.RETORNAR_TRANCAMENTO_PROGRAMA_ENSINO_REDE);
		mov.setObjMovimentado(obj);
		mov.setObjAuxiliar(getPessoaLogada());
		
		try {
			execute(mov);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Retorno de Trancamento");
		} catch(NegocioException e) {
			addMensagemErro("Não foi possível retornar o discente");
			return null;
		}
		
		return cancelar();	
	}
	
}
