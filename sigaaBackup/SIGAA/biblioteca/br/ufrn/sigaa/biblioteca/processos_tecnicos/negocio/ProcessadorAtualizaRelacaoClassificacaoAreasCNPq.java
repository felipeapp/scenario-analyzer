/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/09/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RelacionaClassificacaoBibliograficaAreaCNPq;

/**
 *
 * <p> Processador que atualiza o relacionamento das grandes �reas do CNPq com as classifica��es utilizadas na biblioteca. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizaRelacaoClassificacaoAreasCNPq extends AbstractProcessador{

	/**
	 * Ver coment�rios na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		GenericDAO dao = null; 
		
		try{
			dao = getGenericDAO(mov);
			
			MovimentoAtualizaRelacaoClassificacaoAreasCNPq m = (MovimentoAtualizaRelacaoClassificacaoAreasCNPq) mov;
			
			// Atualiza os relacionamentos das classifica��es com as �reas do CNPq //
			for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : m.getRelacionamentos()) {
				
				if(relacionamento.getId() <= 0){
					// atribui manualmente o id ou vai ficar com 2 objetos iguais na sess�o e d� erro
					relacionamento.setId(dao.getNextSeq("biblioteca", "hibernate_sequence")); 
					dao.create(relacionamento);
				}else{
					dao.update(relacionamento);
				}
				
			}
			
		}finally{
			if (dao != null) dao.close();
		}
		
		return null;
	}

	
	
	/**
	 * Ver coment�rios na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		ListaMensagens erros = new ListaMensagens();
		
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
//		Pattern pClasseCDU = Pattern.compile("[^\\d\\s\\./-]");  // N�o � d�gito nem "-" nem espaca�o em banco ou "." ou "/"
//		
//		Pattern pClasseBlack = Pattern.compile("[^\\d\\s\\./D-]");  // N�o � d�gito nem "-" nem espaca�o em banco ou "." ou "/" nem "D"
//		
//		Pattern p2 = Pattern.compile("(?i)^d");  // come�a com "D" ou "d"
//		
//		MovimentoAtualizaRelacaoClassificacaoAreasCNPq movi = (MovimentoAtualizaRelacaoClassificacaoAreasCNPq) mov;
//		
//		for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : movi.getRelacionamentos()) {
//			
//			// verifica se tiver alguma coisa diferente de n�mero e "-"  //
//			
//			Matcher matcherClasseCDU = pClasseCDU.matcher(relacionamento.getClassesInclusao()));
//			
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes CDU da �rea "+area.getNome()+" possuem caracteres inv�lidos ");
//			}
//			
//			matcherClasseCDU = pClasseCDU.matcher(area.getClassesCDUExclusao());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes CDU de Exclus�o da �rea "+area.getNome()+" possuem caracteres inv�lidos ");
//			}
//			
//			matcherClasseCDU = pClasseBlack.matcher(area.getClassesBlack());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes Black da �rea "+area.getNome()+" possuem caracteres inv�lidos ");
//			}
//			
//			matcherClasseCDU = pClasseBlack.matcher(area.getClassesBlackExclusao());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes Black de Exclus�o da �rea "+area.getNome()+" possuem caracteres inv�lidos ");
//			}
//			
//			
//			
//			
//			// verifica se as classes black n�o come�am com "D" //
//			
//			if(StringUtils.notEmpty(area.getClassesBlack())){
//				String[] classesBlack = area.getClassesBlack().split("\\s");
//				
//				for (String string : classesBlack) {
//					Matcher m2 = p2.matcher(string);
//					if( ! m2.find() )
//						erros.addErro("As classes Black da �rea "+area.getNome()+" deve come�ar com o caracter \"D\" ");
//				} 
//			}
//			
//			if(StringUtils.notEmpty(area.getClassesBlackExclusao())){
//				String[] classesBlackExclusao = area.getClassesBlackExclusao().split("\\s");
//				
//				for (String string : classesBlackExclusao) {
//					Matcher m2 = p2.matcher(string);
//					if( ! m2.find() )
//						erros.addErro("As classes Black da �rea "+area.getNome()+" deve come�ar com o caracter \"D\" ");
//				} 
//			}
//			
//		}
//		
		
		
		checkValidation(erros);
	}

}
