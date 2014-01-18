package br.ufrn.sigaa.ensino_rede.portal.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenadorUnidadeRede;
import br.ufrn.sigaa.ensino_rede.dao.TurmaRedeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;
import br.ufrn.sigaa.ensino_rede.jsf.EnsinoRedeAbstractController;

@SuppressWarnings("serial")
@Component("portalCoordenadorRedeBean") @Scope("session")
public class PortalCoordenadorRedeMBean extends EnsinoRedeAbstractController<Object> {

	private List<TurmaRede> turmasAbertas;
	
	/**
	 * Retorna a coordenação do curso em rede
	 * 
	 * @return
	 */
	public CoordenadorUnidade getCoordenacao() {
		TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		return vinculo.getCoordenacao();
	}
	
	public List<TurmaRede> getTurmasAbertas() throws DAOException {

		TurmaRedeDao dao = null;
		
		try {
		
			if (turmasAbertas == null) {
				dao = getDAO(TurmaRedeDao.class);
				
				TipoVinculoCoordenadorUnidadeRede vinculo = (TipoVinculoCoordenadorUnidadeRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
				DadosCursoRede dc = vinculo.getCoordenacao().getDadosCurso();
				
				turmasAbertas = (List<TurmaRede>) dao.findTurmasAbertasByCampusAnoPeriodo(dc.getCampus().getId(),dc.getProgramaRede().getId(),null,null);
			}
			
			return turmasAbertas;
		
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	public void setTurmasAbertas(List<TurmaRede> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}	
	
}
