package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dao.TipoRefeicaoRUDao;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;

/**
 * Reponspavel por realizar as modificações dos horários das refeições do Restaurante Universitário.
 * 
 * @author Jean Guerethes
 */
@Component @Scope("session")
public class TipoRefeicaoRUMBean extends SigaaAbstractController<TipoRefeicaoRU> {

	Collection<TipoRefeicaoRU> horarioRefeicaoRU;
	
	public TipoRefeicaoRUMBean() {
		obj = new TipoRefeicaoRU();
	}
	
	public String iniciarAlteracaoHorario() throws DAOException {
		horarioRefeicaoRU = getGenericDAO().findAll(TipoRefeicaoRU.class, "id", "asc");
		return forward(getFormPage());
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,NegocioException {
		TipoRefeicaoRU cafe = null;
		TipoRefeicaoRU almoco = null;
		TipoRefeicaoRU janta = null;

		for (TipoRefeicaoRU horarioRefeicao : horarioRefeicaoRU) {
			if ( horarioRefeicao.getId() == TipoRefeicaoRU.CAFE || horarioRefeicao.getId() == TipoRefeicaoRU.CAFE_FIM_SEMANA )
				cafe = horarioRefeicao;
			if ( horarioRefeicao.getId() == TipoRefeicaoRU.ALMOCO || horarioRefeicao.getId() == TipoRefeicaoRU.ALMOCO_FIM_SEMANA )
				almoco = horarioRefeicao;
			if ( horarioRefeicao.getId() == TipoRefeicaoRU.JANTA || horarioRefeicao.getId() == TipoRefeicaoRU.JANTA_FIM_SEMANA )
				janta = horarioRefeicao;
			
			if ( ValidatorUtil.isNotEmpty(cafe) && ValidatorUtil.isNotEmpty(almoco) && ValidatorUtil.isNotEmpty(janta) ) {
				validaHorario(cafe, almoco, janta);
				cafe = null;
				almoco = null;
				janta = null;
			}
		}
		
		if ( !erros.isEmpty() ) {
			addMensagens(erros);
			return null;
		} else
			getDAO(TipoRefeicaoRUDao.class).atualizarHorarios(horarioRefeicaoRU);
			addMensagem(OPERACAO_SUCESSO);	
			return redirect( getSubSistema().getLink() );
	}
	
	private void validaHorario( TipoRefeicaoRU cafe, TipoRefeicaoRU almoco, TipoRefeicaoRU janta){
		
		if (cafe.getHoraInicio() > cafe.getHoraFim() || 
				( cafe.getHoraInicio() == cafe.getHoraFim() && cafe.getMinutoInicio() > cafe.getMinutoFim() ) ) {
			
			String msg = cafe.isFimSemana() ? "(Final de Semana)" : "(Durante a Semana)";
			erros.addErro("O horário definido para o termino do café " + msg + " é superior ao horário de início do mesmo.");
		}
		
		if (almoco.getHoraInicio() > almoco.getHoraFim() || 
				( almoco.getHoraInicio() == almoco.getHoraFim() && almoco.getMinutoInicio() > almoco.getMinutoFim() ) ) {
			
			String msg = almoco.isFimSemana() ? "(Final de Semana)" : "(Durante a Semana)";
			erros.addErro("O horário definido para o termino do almoço " + msg + " é superior ao horário de início do mesmo.");
		}

		if (janta.getHoraInicio() > janta.getHoraFim() || 
				( janta.getHoraInicio() == janta.getHoraFim() && janta.getMinutoInicio() > janta.getMinutoFim() ) ) {
			
			String msg = janta.isFimSemana() ? "(Final de Semana)" : "(Durante a Semana)";
			erros.addErro("O horário definido para o termino do jantar " + msg + " é superior ao horário de início do mesmo.");
		}
	
		if ( erros.isEmpty() ) {
			
			if ( cafe.getHoraFim() > almoco.getHoraInicio() || cafe.getHoraFim() > janta.getHoraInicio() ||
					( cafe.getHoraFim() == almoco.getHoraInicio() && cafe.getMinutoFim() > almoco.getMinutoInicio() ) ||
					( cafe.getHoraFim() == janta.getHoraInicio() && cafe.getMinutoFim() > janta.getMinutoInicio() ) ) {
				
				String msg = cafe.isFimSemana() ? "(Final de Semana)" : "(Durante a Semana)";
				erros.addErro("O café " + msg + " deve iniciar é finalizar antes do almoço é do jantar.");
			}
			
			if ( almoco.getHoraFim() > janta.getHoraInicio() || almoco.getHoraInicio() < cafe.getHoraFim() ||
					( almoco.getHoraFim() == janta.getHoraInicio() && almoco.getMinutoFim() > janta.getMinutoInicio() ) ||
					( almoco.getHoraInicio() == cafe.getHoraFim() && almoco.getMinutoInicio() < cafe.getMinutoFim() ) ) {
			
				String msg = almoco.isFimSemana() ? "(Final de Semana)" : "(Durante a Semana)";
				erros.addErro("O almoço " + msg + " deve iniciar é finalizar antes do jantar é deve iniciar depois do café.");
			}

			if ( almoco.getHoraFim() > janta.getHoraInicio() ||
					( almoco.getHoraFim() == janta.getHoraInicio() && almoco.getMinutoFim() > janta.getMinutoInicio() ) )
				
				erros.addErro("O jantar deve finalizar depois do almoço.");
		}
	}
	
	@Override
	public String getDirBase() {
		return "/sae/tipoRefeicaoRU";
	}
	
	public Collection<TipoRefeicaoRU> getHorarioRefeicaoRU() {
		return horarioRefeicaoRU;
	}

	public void setHorarioRefeicaoRU(Collection<TipoRefeicaoRU> horarioRefeicaoRU) {
		this.horarioRefeicaoRU = horarioRefeicaoRU;
	}
	
}