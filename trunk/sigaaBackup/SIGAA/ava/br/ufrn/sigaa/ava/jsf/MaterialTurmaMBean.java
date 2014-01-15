package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.richfaces.event.DropEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;

/**
 * Utilizado para gestão de materiais na turma virtual.
 * Centraliza operações relacionadas a todos os tipos de material da turma.
 * 
 * @author Ilueny Santos
 *
 */
@Component("materialTurmaBean") 
@Scope("request")
public class MaterialTurmaMBean extends AbstractControllerCadastro<MaterialTurma> {

	/** Lista os movimentos de ordenação possíveis para os materiais. */
	private Map<String, Comando> movimentosPossiveis = new HashMap<String, Comando>();
	
	
	/**
	 * Construtor Padrão.
	 */
	public MaterialTurmaMBean() {
		obj = new MaterialTurma();		
		movimentosPossiveis.put("paraCima", SigaaListaComando.ORDENAR_MATERIAL_TURMA_CIMA);
		movimentosPossiveis.put("paraBaixo", SigaaListaComando.ORDENAR_MATERIAL_TURMA_BAIXO);
		movimentosPossiveis.put("paraEsquerda", SigaaListaComando.ORDENAR_MATERIAL_TURMA_ESQUERDA);
		movimentosPossiveis.put("paraDireita", SigaaListaComando.ORDENAR_MATERIAL_TURMA_DIREITA);
	}

	/**
	 * Altera a ordem de exibição dos materiais do tópico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/ava/topico_aula.jsp</li> 
	 * </ul>
	 * @param evt Valor recebido da jsp na geração do evento.
	 */
	public void moverMaterial(){
		try {			
			Integer id = getParameterInt("id");
			String movimento = getParameter("movimento");
			if (id != null){
				try {

					obj.setId(id);
					MovimentoCadastro mov = new MovimentoCadastro();
					mov.setObjMovimentado(obj);

					if (obj.getId() != 0) {
						prepareMovimento(movimentosPossiveis.get(movimento));
						mov.setCodMovimento(movimentosPossiveis.get(movimento));
						atualizarMateriaisView((ArrayList<MaterialTurma>) execute(mov));
					}

				} catch (NegocioException e) {
					addMensagensAjax(e.getListaMensagens());
				} catch (Exception e) {
					tratamentoErroPadrao(e);
				}
			}
		} catch (Exception e) {
			notifyError(e);
		}
	}

	
	/**
	 * Altera a ordem de exibição dos materiais do tópico.
	 * 
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/ava/topico_aula.jsp</li> 
	 * </ul>
	 * @param evt Valor recebido da jsp na geração do evento.
	 */
	public void moverMaterial(DropEvent evt){
		try {			
			
			Integer idMaterialOrigem = (Integer) evt.getDragValue();
			Integer idMaterialDestino = (Integer) evt.getDropValue();
			
			if (ValidatorUtil.isNotEmpty(idMaterialOrigem) && ValidatorUtil.isNotEmpty(idMaterialDestino)){
				try {
					
					MovimentoCadastro mov = new MovimentoCadastro();					
					mov.setObjMovimentado(new MaterialTurma(idMaterialOrigem));
					mov.setObjAuxiliar(new MaterialTurma(idMaterialDestino));

					prepareMovimento(SigaaListaComando.ORDENAR_MATERIAL_TURMA_TROCAR_POSICAO);
					mov.setCodMovimento(SigaaListaComando.ORDENAR_MATERIAL_TURMA_TROCAR_POSICAO);
					atualizarMateriaisView((ArrayList<MaterialTurma>) execute(mov));
					
				} catch (NegocioException e) {
					addMensagensAjax(e.getListaMensagens());
				} catch (Exception e) {
					tratamentoErroPadrao(e);
				}
			}
		} catch (Exception e) {
			notifyError(e);
		}
	}

	/**
	 * Atualizando ordem e nível dos materiais da view.
	 * 
	 * @param materiaisNovaOrdem
	 * @throws DAOException
	 */
	private void atualizarMateriaisView(ArrayList<MaterialTurma> materiaisNovaOrdem) throws DAOException {
		if (ValidatorUtil.isNotEmpty(materiaisNovaOrdem)) {
			List<TopicoAula> aulas = ((TopicoAulaMBean) getMBean("topicoAula")).getAulas();
			TopicoAula topicoAlterado = aulas.get(aulas.indexOf(materiaisNovaOrdem.get(0).getTopicoAula()));
			
			//Atualizando ordem e nível dos materiais da view
			for (AbstractMaterialTurma abstractMat : topicoAlterado.getMateriais()) {
				for(MaterialTurma matOrd : materiaisNovaOrdem) {
					if (matOrd.getId() == abstractMat.getMaterial().getId()) {
						abstractMat.getMaterial().setOrdem(matOrd.getOrdem());
						abstractMat.getMaterial().setNivel(matOrd.getNivel());
					}
				}
			}
			//Re-ordenando view.
			Collections.sort(topicoAlterado.getMateriais());
		}		
	}

	
}
