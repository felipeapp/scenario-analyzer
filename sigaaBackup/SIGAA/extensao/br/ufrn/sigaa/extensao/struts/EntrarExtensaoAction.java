/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/10/2006
 *
 */
package br.ufrn.sigaa.extensao.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.bolsas.negocio.IntegracaoBolsas;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.extensao.DiscenteExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoSituacaoDiscenteExtensao;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.TipoVinculoDiscente;

/*******************************************************************************
 * Controla o acesso ao menu da pro-reitoria de extensão
 * Permite que técnicos administrativos submetam ações de extensão através de um
 * menu especifico.
 * 
 * @author Ilueny Santos
 *  
 ******************************************************************************/
public class EntrarExtensaoAction extends AbstractAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Verifica se o SIPAC esta ativo na IFES.
		//Caso esteja, verifica se existem discentes desligados com bolsa ativa no SIPAC.
		if(Sistema.isSipacAtivo() && !isUserInRole(request,
				SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO)) {
			discenteDesligadoComBolsaAtivaSIPAC(request);
		}		

		if (isUserInRole(request, SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
				SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO)) {

			setSubSistemaAtual(request, SigaaSubsistemas.EXTENSAO);
			request.getSession().setAttribute("tipoEdital", Edital.EXTENSAO);

			request.setAttribute("hideSubsistema", Boolean.TRUE);

			return mapping.findForward("sucesso");

			// permite que técnicos administrativos entrem no menu de extensão
		} else if (isUserInRole(request,
				SigaaPapeis.EXTENSAO_TECNICO_ADMINISTRATIVO)) {

			setSubSistemaAtual(request, SigaaSubsistemas.EXTENSAO);
			request.getSession().setAttribute("tipoEdital", Edital.EXTENSAO);

			request.setAttribute("hideSubsistema", Boolean.TRUE);

			return mapping.findForward("menu_ta");

		} else {
			throw new SegurancaException();
		}

	}
	
	/**
	 * Verifica total de discentes de extensão desligados com bolsas ativas no SIPAC.
	 * Caso encontre algum discente nesta situação uma mensagem de warning é emitida. 
	 */
	private void discenteDesligadoComBolsaAtivaSIPAC(HttpServletRequest request) throws Exception {
		//Verificando total de discentes de extensão desligados com bolsas ativas no SIPAC 
		DiscenteExtensaoDao dao = getDAO(DiscenteExtensaoDao.class, request);
		Collection<DiscenteExtensao> discentesExtensao = dao.findDiscenteExtensaoBySituacao(TipoSituacaoDiscenteExtensao.FINALIZADO, TipoVinculoDiscente.EXTENSAO_BOLSISTA_INTERNO);
		List<Long> matriculas = new ArrayList<Long>();
		for(DiscenteExtensao de: discentesExtensao) {
			matriculas.add(de.getDiscente().getMatricula());
		}
		//Coleção de matrículas é utilizada em gerarStringIn, evitando erro quando banco esta vazio. 
		if(matriculas != null && !matriculas.isEmpty()){			
			Collection<Long> alunosComBolsasAtivasSipac = IntegracaoBolsas.verificarCadastroBolsaSIPAC(matriculas,new Integer[] {IntegracaoBolsas.TIPO_BOLSA_EXTENSAO});
	
			Integer totalAlunosComBolsasExtensaoAtivasSipac = 0;
			if(alunosComBolsasAtivasSipac != null && !alunosComBolsasAtivasSipac.isEmpty())
				totalAlunosComBolsasExtensaoAtivasSipac = alunosComBolsasAtivasSipac.size();
				if(totalAlunosComBolsasExtensaoAtivasSipac > 0){
					addWarning("Existe(m) " + totalAlunosComBolsasExtensaoAtivasSipac + " Bolsista(s) Finalizado(s) mas com bolsa ativa no SIPAC.", request);
				}
		}
	}
}
