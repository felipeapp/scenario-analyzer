/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Processador que atualiza o relacionamento das grandes áreas do CNPq com as classificações utilizadas na biblioteca. </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizaRelacaoClassificacaoAreasCNPq extends AbstractProcessador{

	/**
	 * Ver comentários na classe pai.
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
			
			// Atualiza os relacionamentos das classificações com as áreas do CNPq //
			for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : m.getRelacionamentos()) {
				
				if(relacionamento.getId() <= 0){
					// atribui manualmente o id ou vai ficar com 2 objetos iguais na sessão e dá erro
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
	 * Ver comentários na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	
		ListaMensagens erros = new ListaMensagens();
		
		checkRole(new int[]{SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}, mov);
		
//		Pattern pClasseCDU = Pattern.compile("[^\\d\\s\\./-]");  // Não é dígito nem "-" nem espacaço em banco ou "." ou "/"
//		
//		Pattern pClasseBlack = Pattern.compile("[^\\d\\s\\./D-]");  // Não é dígito nem "-" nem espacaço em banco ou "." ou "/" nem "D"
//		
//		Pattern p2 = Pattern.compile("(?i)^d");  // começa com "D" ou "d"
//		
//		MovimentoAtualizaRelacaoClassificacaoAreasCNPq movi = (MovimentoAtualizaRelacaoClassificacaoAreasCNPq) mov;
//		
//		for (RelacionaClassificacaoBibliograficaAreaCNPq relacionamento : movi.getRelacionamentos()) {
//			
//			// verifica se tiver alguma coisa diferente de número e "-"  //
//			
//			Matcher matcherClasseCDU = pClasseCDU.matcher(relacionamento.getClassesInclusao()));
//			
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes CDU da área "+area.getNome()+" possuem caracteres inválidos ");
//			}
//			
//			matcherClasseCDU = pClasseCDU.matcher(area.getClassesCDUExclusao());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes CDU de Exclusão da área "+area.getNome()+" possuem caracteres inválidos ");
//			}
//			
//			matcherClasseCDU = pClasseBlack.matcher(area.getClassesBlack());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes Black da área "+area.getNome()+" possuem caracteres inválidos ");
//			}
//			
//			matcherClasseCDU = pClasseBlack.matcher(area.getClassesBlackExclusao());
//			if( matcherClasseCDU.find() ){
//				erros.addErro("As classes Black de Exclusão da área "+area.getNome()+" possuem caracteres inválidos ");
//			}
//			
//			
//			
//			
//			// verifica se as classes black não começam com "D" //
//			
//			if(StringUtils.notEmpty(area.getClassesBlack())){
//				String[] classesBlack = area.getClassesBlack().split("\\s");
//				
//				for (String string : classesBlack) {
//					Matcher m2 = p2.matcher(string);
//					if( ! m2.find() )
//						erros.addErro("As classes Black da área "+area.getNome()+" deve começar com o caracter \"D\" ");
//				} 
//			}
//			
//			if(StringUtils.notEmpty(area.getClassesBlackExclusao())){
//				String[] classesBlackExclusao = area.getClassesBlackExclusao().split("\\s");
//				
//				for (String string : classesBlackExclusao) {
//					Matcher m2 = p2.matcher(string);
//					if( ! m2.find() )
//						erros.addErro("As classes Black da área "+area.getNome()+" deve começar com o caracter \"D\" ");
//				} 
//			}
//			
//		}
//		
		
		
		checkValidation(erros);
	}

}
