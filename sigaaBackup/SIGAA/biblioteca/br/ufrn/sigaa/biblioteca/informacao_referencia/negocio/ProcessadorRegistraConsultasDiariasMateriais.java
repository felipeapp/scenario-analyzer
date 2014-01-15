/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/06/2009
 * 
 */

package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.RegistroConsultasMateriaisDao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.ClasseMaterialConsultado;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.RegistroConsultasDiariasMateriais;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;

/**
 * Processador que registra uma movimenta��o di�ria de consulta a materiais catalogr�ficos na biblioteca.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorRegistraConsultasDiariasMateriais extends AbstractProcessador {

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		GenericDAO dao = null;
		
		MovimentoCadastro personalMov = (MovimentoCadastro) mov;
		RegistroConsultasDiariasMateriais registro = personalMov.getObjMovimentado();
		
		@SuppressWarnings("unchecked")
		Collection<ClasseMaterialConsultado> classesARegistrar =  (Collection<ClasseMaterialConsultado>) personalMov.getColObjMovimentado();
		
		try {
			dao = getGenericDAO(personalMov);

			registro.getClassesConsultadas().addAll(classesARegistrar); // adiciona apenas antes de salvar para caso der erro, n�o alterar ficar duplicado para o usu�rio.
			
			if (registro.getId() == 0){
				dao.create(registro);
			}else{
				dao.update(registro);
			}
			
			return null;
			
		} finally {
			if (dao != null) dao.close();
		}
	}

	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro personalMov = (MovimentoCadastro) mov;
		
		RegistroConsultasDiariasMateriais registro = personalMov.getObjMovimentado();
		
		ListaMensagens erros = new ListaMensagens();
		
		if (registro == null)
			throw new NegocioException ("Uma movimenta��o deve ser informada.");
		
		erros.addAll(registro.validate());
		
		ClassificacaoBibliografica classificacaoDaBiblioteca = (ClassificacaoBibliografica) personalMov.getObjAuxiliar();
		
		@SuppressWarnings("unchecked")
		Collection<ClasseMaterialConsultado> classesARegistrar =  (Collection<ClasseMaterialConsultado>) personalMov.getColObjMovimentado();
	
		
		for (ClasseMaterialConsultado classeConsulta : classesARegistrar) {
			if(   
				( classificacaoDaBiblioteca.isPrimeiraClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao2()) || StringUtils.notEmpty(classeConsulta.getClassificacao3()) ) )  
				|| (classificacaoDaBiblioteca.isSegundaClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao1()) || StringUtils.notEmpty(classeConsulta.getClassificacao3()) ) ) 
				|| (classificacaoDaBiblioteca.isTerceiraClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao1()) || StringUtils.notEmpty(classeConsulta.getClassificacao2()) ) )  ){
					
				erros.addErro("A biblioteca selecionada utiliza a classifica��o " +classificacaoDaBiblioteca.getDescricao()+", por isso somente � poss�vel registrar consultas para esssa classifica��o. ");
			}
		}
		
		for (ClasseMaterialConsultado classeConsulta : registro.getClassesConsultadas()) {
			
			if(   
				( classificacaoDaBiblioteca.isPrimeiraClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao2()) || StringUtils.notEmpty(classeConsulta.getClassificacao3()) ) )  
				|| (classificacaoDaBiblioteca.isSegundaClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao1()) || StringUtils.notEmpty(classeConsulta.getClassificacao3()) ) ) 
				|| (classificacaoDaBiblioteca.isTerceiraClassificacao()  && ( StringUtils.notEmpty(classeConsulta.getClassificacao1()) || StringUtils.notEmpty(classeConsulta.getClassificacao2()) ) )  ){
					
				erros.addErro("A biblioteca selecionada utiliza a classifica��o " +classificacaoDaBiblioteca.getDescricao()+", por isso somente � poss�vel registrar consultas para esssa classifica��o. ");
			}
			
		}
		
		RegistroConsultasMateriaisDao dao = null;
		
		try{
			dao = getDAO(RegistroConsultasMateriaisDao.class, personalMov);
			
			boolean existeRegistro = dao.existeRegistroConsultaSalvo(registro.getBiblioteca().getId(), registro.getTipoMaterial().getId(), registro.getColecao().getId(), registro.getTurno(), registro.getDataConsulta());
			
			if(existeRegistro && registro.getId() == 0 ){
				erros.addErro("J� existem uma consulta registrada para a mesma biblioteca na mesma data");
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		checkValidation(erros);
	}
}
