package br.ufrn.sigaa.ensino_rede.jsf;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculoCoordenacaoGeralRede;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino_rede.dao.SelecionaCampusIesDao;

@SuppressWarnings("serial")
@Component("selecionaCampusRedeMBean") @Scope("session")
public class SelecionaCampusIesMBean extends EnsinoRedeAbstractController<CampusIes> {
	
	private static final String LISTA_CAMPUS = "/ensino_rede/busca_geral/campus_ies/lista_campus.jsp";
	
	private SelecionaCampus requisitor;
	
	private boolean buscaIes;
	
	public SelecionaCampusIesMBean() {
		obj = new CampusIes();
		obj.setInstituicao(new InstituicoesEnsino());
	}
	
	public String iniciar() {
		return forward(LISTA_CAMPUS);
	}

	public void buscar(ActionEvent evt) throws DAOException {
		SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
		
		TipoVinculoCoordenacaoGeralRede vinculo = (TipoVinculoCoordenacaoGeralRede) getUsuarioLogado().getVinculoAtivo().getTipoVinculo();
		
		setResultadosBusca(dao.buscar(vinculo.getCoordenacao().getProgramaRede(), obj.getInstituicao().getSigla()));
	}
	
	public void escolheCampus(ActionEvent evt) throws ArqException {

		Integer id = (Integer) evt.getComponent().getAttributes().get("idCampus");
		escolheCampus(id);
		
	}
	
	private void escolheCampus(Integer id) throws ArqException {
		SelecionaCampusIesDao dao = getDAO(SelecionaCampusIesDao.class);
		obj = dao.findByPrimaryKey(id, CampusIes.class);
		
		requisitor.setCampus(obj);
		requisitor.selecionaCampus();
		
	}

	public boolean isBuscaIes() {
		return buscaIes;
	}

	public void setBuscaIes(boolean buscaIes) {
		this.buscaIes = buscaIes;
	}

	public SelecionaCampus getRequisitor() {
		return requisitor;
	}

	public void setRequisitor(SelecionaCampus requisitor) {
		this.requisitor = requisitor;
	}

}
