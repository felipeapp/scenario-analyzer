package br.ufrn.sigaa.ensino_rede.academico.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.ensino_rede.academico.dao.MovimentacaoDiscenteAssociadoDao;
import br.ufrn.sigaa.ensino_rede.academico.dominio.MovimentacaoDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.academico.jsf.ConsultarDiscenteMBean.DiscenteConsultado;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscente;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaDiscenteMBean;


@SuppressWarnings("serial")
@Component @Scope("session")
public class ConsultarDiscenteMBean extends EnsinoRedeAbstractController<DiscenteConsultado> implements SelecionaDiscente {

	public ConsultarDiscenteMBean() {
		clear();
	}
	
	private void clear() {
		obj = new DiscenteConsultado();
		obj.setDiscente(new DiscenteAssociado());
		obj.setMovimentacoes(new ArrayList<MovimentacaoDiscenteAssociado>());
	}

	public String iniciarConsultar() throws DAOException, NegocioException {
		
		clear();
		
		SelecionaDiscenteMBean mBean = getMBean("selecionaDiscenteMBean");
		mBean.setRequisitor(this);
		
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
		obj.setMovimentacoes( dao.findAllByDiscente(obj.getDiscente()) );
		
		return forward("/ensino_rede/discente/consulta/consulta.jsp");
	}
	
	public class DiscenteConsultado {
		
		private DiscenteAssociado discente;
		private List<MovimentacaoDiscenteAssociado> movimentacoes;

		public DiscenteAssociado getDiscente() {
			return discente;
		}

		public void setDiscente(DiscenteAssociado discente) {
			this.discente = discente;
		}

		public List<MovimentacaoDiscenteAssociado> getMovimentacoes() {
			return movimentacoes;
		}

		public void setMovimentacoes(
				List<MovimentacaoDiscenteAssociado> movimentacoes) {
			this.movimentacoes = movimentacoes;
		}
	}



	
}
