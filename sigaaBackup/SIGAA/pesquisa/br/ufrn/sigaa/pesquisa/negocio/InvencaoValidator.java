/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/07/2008
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pesquisa.dominio.FinanciamentoInvencao;
import br.ufrn.sigaa.pesquisa.dominio.Invencao;
import br.ufrn.sigaa.pesquisa.dominio.Inventor;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;

/**
 * Classe com m�todos de valida��o para os passos do
 * cadastro de uma inven��o.
 * 
 * @author Leonardo Campos
 *
 */
public class InvencaoValidator {

	/** 
	 * Valida os dados gerais da Inven��o
	 * 
	 * @param invencao
	 * @param lista
	 */
	public static void validaDadosGerais(Invencao invencao, ListaMensagens lista){
		ValidatorUtil.validateRequired(invencao.getCentro(), "Centro", lista);
		ValidatorUtil.validateRequired(invencao.getTipo(), "Tipo", lista);
		ValidatorUtil.validateRequired(invencao.getPalavrasChavePortugues(), "Palavas-chave em portugu�s", lista);
		ValidatorUtil.validateRequired(invencao.getPalavrasChaveIngles(), "Palavras-chave em ingl�s", lista);
		ValidatorUtil.validateRequired(invencao.getAreaConhecimentoCnpq(), "�rea de Conhecimento", lista);
	}
	
	/**
	 * Valida o Financiamento de uma Inven��o
	 * 
	 * @param financiamento
	 * @param lista
	 */
	public static void validaAdicionaFinanciamento(FinanciamentoInvencao financiamento, ListaMensagens lista){
		validateRequiredId(financiamento.getEntidadeFinanciadora().getId(), "Entidade Financiadora", lista);
	}
	
	/** 
	 * Valida o Inventor de uma Inven��o
	 * 
	 * @param inventor
	 * @param docente
	 * @param discente
	 * @param servidor
	 * @param lista
	 */
	public static void validaAdicionaInventorInterno(Inventor inventor, Servidor docente, Discente discente, Servidor servidor, ListaMensagens lista) {

		if ((inventor.getCategoriaMembro() != null) && (inventor.getCategoriaMembro().getId() > 0)){
			if(CategoriaMembro.SERVIDOR == inventor.getCategoriaMembro().getId()){
				ValidatorUtil.validateRequired(servidor, "Selecione um servidor", lista);
			}
	
			if(CategoriaMembro.DOCENTE == inventor.getCategoriaMembro().getId()){
				ValidatorUtil.validateRequired(docente, "Selecione um docente", lista);
			}
			
			if(CategoriaMembro.DISCENTE == inventor.getCategoriaMembro().getId()){
				ValidatorUtil.validateRequired(discente, "Selecione um discente", lista);
			}
		}else{
			lista.addErro("Selecione uma categoria de inventor: Docente, Discente, Servidor ou Externo.");
		}
		
	}
	
	/** 
	 * Valida os dados do Inventor Externo
	 * 
	 * @param membro
	 * @param docenteExterno
	 * @param lista
	 */
	public static void validaInventorDocenteExterno(Inventor membro, DocenteExterno docenteExterno, ListaMensagens lista) {
		
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getSexo(), "Sexo", lista);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getEndereco(), "Endere�o", lista);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getPais(), "Nacionalidade", lista);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getTelefone(), "Telefone", lista);
		ValidatorUtil.validateRequired(docenteExterno.getPessoa().getEmail(), "E-mail", lista);
		ValidatorUtil.validateRequired(docenteExterno.getFormacao(), "Forma��o", lista);
		ValidatorUtil.validateRequired(docenteExterno.getInstituicao(), "Institui��o de Ensino", lista);
			
	}
	
	/** 
	 * Valida os dados do Inventor, seja este Docente, Discente ou Servidor
	 * 
	 * @param membro
	 * @param lista
	 */
	public static void validaInventor(Inventor membro, ListaMensagens lista){
		
		ValidatorUtil.validateRequired(membro.getPessoa().getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(membro.getPessoa().getEndereco(), "Endere�o", lista);
		ValidatorUtil.validateRequired(membro.getPessoa().getPais(), "Nacionalidade", lista);
		ValidatorUtil.validateRequired(membro.getPessoa().getTelefone(), "Telefone", lista);
		ValidatorUtil.validateRequired(membro.getPessoa().getEmail(), "E-mail", lista);
		
	}
	
}
