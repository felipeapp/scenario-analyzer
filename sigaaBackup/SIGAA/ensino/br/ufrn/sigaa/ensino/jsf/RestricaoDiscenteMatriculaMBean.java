package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.RestricaoDiscenteMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.tecnico.dao.MatriculaFormacaoComplementarDao;

@Component @Scope("session")
public class RestricaoDiscenteMatriculaMBean extends SigaaAbstractController<RestricaoDiscenteMatricula> {

	Collection<RestricaoDiscenteMatricula> restricoes;
	
	public RestricaoDiscenteMatriculaMBean() {
		obj = new RestricaoDiscenteMatricula();
		obj.setUnidade(new Unidade());
		obj.setSituacaoMatricula(new SituacaoMatricula());
	}

	@Override
	public String atualizar() throws ArqException {
		MatriculaFormacaoComplementarDao dao = getDAO(MatriculaFormacaoComplementarDao.class);
		try {
			restricoes = dao.findRestricoesDiscenteMatricula( getParametrosAcademicos().getUnidade(), getNivelEnsino());
			obj.popularCampos(restricoes);
		} finally {
			dao.close();
		}
		return forward( getFormPage() );
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {

		GenericDAOImpl dao = getDAO(GenericDAOImpl.class);		
		try {
			for (RestricaoDiscenteMatricula restricao : restricoes) {
				if (restricao.getHoraInicio() != null || restricao.getMinutoInicio() != null) {
					Date dateInicio = CalendarUtils.configuraTempoDaData(CalendarUtils.adicionaUmDia(restricao.getInicio()), 
							restricao.getHoraInicio() != null ? restricao.getHoraInicio() : 0, 
									restricao.getMinutoInicio()!= null ? restricao.getMinutoInicio() : 0, 0, 0);
					restricao.setInicio(dateInicio); 
				}
				if (restricao.getHoraFim() != null || restricao.getMinutoFim() != null) {
					Date dateFim = CalendarUtils.configuraTempoDaData(CalendarUtils.adicionaUmDia(restricao.getFim()), 
							restricao.getHoraFim() != null ? restricao.getHoraFim() : 0, 
									restricao.getMinutoFim() != null ? restricao.getMinutoFim() : 0, 0, 0);
					restricao.setFim(dateFim);
				}
				
				getGenericDAO().update(restricao);
			}

		} finally {
			dao.close();
		}
		
		addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Restrições Matrícula On-Line");
		return atualizar();
	}
	
	@Override
	public String getDirBase() {
		return "/ensino/formacao_complementar/restricaoMatricula";
	}
	
	public Collection<RestricaoDiscenteMatricula> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(Collection<RestricaoDiscenteMatricula> restricoes) {
		this.restricoes = restricoes;
	}
	
}