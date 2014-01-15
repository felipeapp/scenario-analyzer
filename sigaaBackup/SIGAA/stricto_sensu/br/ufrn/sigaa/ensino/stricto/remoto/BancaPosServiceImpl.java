/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 18/02/2010
 *
 */
package br.ufrn.sigaa.ensino.stricto.remoto;

import java.util.Collection;
import java.util.Date;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.interfaces.BancaPosRemoteService;
import br.ufrn.sigaa.arq.dao.ensino.stricto.BancaPosDao;
import br.ufrn.sigaa.ensino.stricto.dominio.BancaPos;
import br.ufrn.sigaa.ensino.stricto.dominio.MembroBancaPos;

/**
 * Implementação local da interface BancaPosService.
 * O SIPAC irá se comunicar com essa classe através do Spring HTTP Invoker.
 * @author Arlindo Rodrigues
 */
@Component("bancaPosService")
@Scope("singleton")
@WebService
public class BancaPosServiceImpl implements BancaPosRemoteService {
	
	/**
	 * Verifica se o Membro passado por parâmetro está cadastrado em alguma banca no período informado.
	 */
	public boolean findBancabyMembro(final Long cpf, Date dataInicio, Date dataFim) {
		BancaPosDao dao = new BancaPosDao();
		dao.setSistema(Sistema.SIGAA);

		boolean possuiBanca = false;
		try{
			Collection<BancaPos> bancas = dao.findByPeriodo(dataInicio, dataFim);

			for (BancaPos banca : bancas){
				if (CollectionUtils.countMatches(banca.getMembrosBanca(), new Predicate() {
								public boolean evaluate(Object obj) {
									MembroBancaPos membro = (MembroBancaPos) obj;
									return membro.getPessoa().getCpf_cnpj().equals(cpf);
								}
							}) > 0){
					possuiBanca = true;
					break;
				}
			}		

		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}finally{
			dao.close();
		}
		
		return possuiBanca;
	}
}
