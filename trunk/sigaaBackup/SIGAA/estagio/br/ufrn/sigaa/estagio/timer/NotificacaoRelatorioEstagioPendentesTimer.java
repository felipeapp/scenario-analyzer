/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 08/12/2010
 *
 */	
package br.ufrn.sigaa.estagio.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.estagio.EstagiarioDao;
import br.ufrn.sigaa.estagio.dominio.ParametrosEstagio;
import br.ufrn.sigaa.mensagens.TemplatesDocumentos;

/*******************************************************************************
 * Thread que roda periodicamente enviando e-mail para os interessados dos est�gios, 
 * notificando que j� passou o prazo de preenchimento do relat�rio do est�gio. 
 * 
 * @author Arlindo Rodrigues
 * 
 ******************************************************************************/
public class NotificacaoRelatorioEstagioPendentesTimer extends TarefaTimer {
	
	/**
	 * Executa o Timer
	 */
	@Override
	public void run() {
		try {
			notificar();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeNegocioException(e);
		} 
	}
	
	/**
	 * Notifica os Interessados
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	private void notificar() throws HibernateException, DAOException{		
		EstagiarioDao dao = DAOFactory.getInstance().getDAO(EstagiarioDao.class);	
		try {
			List<Map<String, Object>> estagios = dao.findEstagiosComRelatoriosPendente(null);
			int quantMesesRelatorio = ParametroHelper.getInstance().getParametroInt(ParametrosEstagio.MESES_PARA_PREENCHIMENTO_RELATORIO_ESTAGIO);
			Date hoje = CalendarUtils.descartarHoras(new Date());
			for (Map<String, Object> e : estagios){
					Date prazoMaximo = CalendarUtils.adicionaMeses((Date) e.get("data_inicio"), ((Integer) e.get("qtd_esperada")) * quantMesesRelatorio); 
					Map<String, String> params = new HashMap<String, String>();
					params.put("ASSUNTO", RepositorioDadosInstitucionais.get("siglaSigaa" )+" - Notifica��o de Relat�rio Est�gio - MENSAGEM AUTOM�TICA ");	
					
					params.put("ALUNO", String.valueOf( e.get("discente") ));
					params.put("MATRICULA", String.valueOf( e.get("matricula") ));
					params.put("CURSO", String.valueOf( e.get("curso") ));
					params.put("QUANTMESES", "("+ 
							(CalendarUtils.calculoMeses((Date) e.get("data_inicio"), (Date) e.get("prazo_max_relatorio"))) +
							")");
					params.put("TIPOESTAGIO", String.valueOf( e.get("tipo_estagio") ));
					params.put("CONCEDENTE", String.valueOf( e.get("concedente") ));
					params.put("CNPJ", Formatador.getInstance().formatarCPF_CNPJ( (Long) e.get("cpf_cnpj") ) );
					if (prazoMaximo.before(hoje))
						params.put("DATAFINAL", " foi no dia " + Formatador.getInstance().formatarData(prazoMaximo));
					else
						params.put("DATAFINAL", " � no dia " + Formatador.getInstance().formatarData(prazoMaximo));
					params.put("CONVENIO", String.valueOf( e.get("numero_convenio") ));
					
					String nome = String.valueOf( e.get("nome") );
					String email = String.valueOf( e.get("email") );
					
					Mail.enviaComTemplate(nome, email, TemplatesDocumentos.EMAIL_ALERTA_PREENCHIMENTO_RELATORIO_ESTAGIO_PENDENTE, params);
					
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
	}
}
