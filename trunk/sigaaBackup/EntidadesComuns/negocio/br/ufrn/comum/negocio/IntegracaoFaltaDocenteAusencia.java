package br.ufrn.comum.negocio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.dao.EmptyResultDataAccessException;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.comum.dominio.DadosTurmaDocenteDTO;
import br.ufrn.comum.dominio.Sistema;

/**
 * Classe utilitária que contém métodos para possibilitar a integração entre os casos de uso:
 * SIGAA: Plano de Reposição de Aula
 * SIGRH: Afastamento de Docente 
 * 
 * @author Henrique André
 *
 */
public class IntegracaoFaltaDocenteAusencia {
	
	
	/**
	 * Retorna uma lista de ausências (afastamentos) de um servidor para um determinado período
	 * que estejam pendentes de homologação pela chefia da unidade.
	 * 
	 * @param idServidor
	 * @param inicioPeriodo
	 * @param fimPeriodo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> findAfastamentosPendentesHomologacaoByServidorPeriodo(int idServidor){
		StringBuilder sql = new StringBuilder();
		try{
			sql	.append("SELECT id_ausencia, inicio, fim ")
				.append("FROM ")
				.append("funcional.ausencia ") 
				.append("WHERE ")
				.append("id_ausencia_afastamento IS NOT NULL ") 
				.append("AND homologado IS NULL ")
				.append("AND id_servidor = ? ");
			return getJdbcTemplate(Sistema.SIGRH).queryForList(sql.toString(), new Object[]{idServidor });
		}catch(EmptyResultDataAccessException erdae){
			return null;
		}
	}
	
	/**
	 * <h2>Deprecated</h2>
	 *  <p>
	 *  O serviço oferecido por este método é agora fornecido pela interface <code>AulasDocenteRemoteService</code>
	 *  através de chamada a um serviço remoto segundo o Spring Remoting Http Invoker, descrito em 
	 *  <a href="http://info.ufrn.br/wikisistemas/doku.php?id=desenvolvimento:especificacoes:arquitetura:servicos:integracao_entre_sistemas#como_acessar_serviços_remotos">
	 *  Como Acessar Serviços Remotos
	 *  </a> 
	 *  no wiki.
	 *  </p>
	 *  <p>
	 *  A implementação do serviços disponibilizados na interface estão implementados em <code>AulasDocenteRemoteServiceImpl</code>.  
	 *  </p>
	 * 
	 * <h2>Descrição</h2>
	 *  Método utilitário que verifica se o afastamento do servidor docente no período coincide com aulas que necessitem de reposição,
	 *  e retorna uma mensagem com as informações das turmas afetadas, em forma de lista não-ordenada.
	 * 
	 *  Utilizado no AusenciaMBean e ProcessadorAusencia do SIGPRH
	 * 
	 * @param idServidor
	 * @param inicioAfastamento
	 * @param fimAfastamento
	 * @return
	 * @throws DAOException 
	 */
	@Deprecated
	public static String getTurmasDocenteAsString(int idServidor, Date inicioAfastamento, Date fimAfastamento) throws DAOException{
		StringBuilder mensagemAulas = new StringBuilder();
		List<DadosTurmaDocenteDTO> aulasSigaa = findAulasByDocente(idServidor, inicioAfastamento, fimAfastamento);
		//se o servidor tem aulas no período
		if(!aulasSigaa.isEmpty()){
			mensagemAulas.append("<ul>");
			for(DadosTurmaDocenteDTO aula:aulasSigaa)
				mensagemAulas.append("<li>").append(aula.toString()).append("</li>");
			mensagemAulas.append("</ul>");
		}
		return mensagemAulas.toString();
	}

	/**
	 * Retorna uma lista contendo as turmas e uma lista com as datas das aulas
	 * TODO: Excluir feriados
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public static List<DadosTurmaDocenteDTO> findAulasByDocente(int idServidor, Date inicioAfastamento, Date fimAfastamento) throws DAOException {

		JdbcTemplate jt = getJdbcTemplate(Sistema.SIGAA);
		
		try{ 
			List lista = jt.queryForList("select ht.hora_inicio, ht.hora_fim, ht.id_turma, ht.dia, ht.data_inicio, ht.data_fim, detalhes.nome_ascii as disciplina_nome, cc.codigo as disciplina_codigo, t.codigo, t.descricao_horario from ensino.docente_turma dt " +
					" join ensino.turma t using (id_turma) " +
					" join ensino.componente_curricular cc using (id_disciplina) " +
					" join ensino.componente_curricular_detalhes detalhes on (cc.id_detalhe = detalhes.id_componente_detalhes) " +
					" join ensino.horario_turma ht using (id_turma) " +
					" join ensino.horario h using (id_horario) " +
					" where dt.id_docente = ? and (DATE (?), DATE (?) ) OVERLAPS ( t.data_inicio, t.data_fim ) " +
					" and t.id_situacao_turma = 1" + // Somente turmas abertas
					" order by t.id_turma asc, h.id_horario, ht.dia", new Object[] {idServidor, inicioAfastamento.toString(), fimAfastamento.toString()});
			// Mapa com o idTurma e uma lista contendo os dias da semana que possuem aula (Equivalente a Calendar.DAY_OF_WEEK)
			Map<Integer, List<Integer>> dias = new HashMap<Integer, List<Integer>>();
			
			// Mapa contendo cada linha do resultado do SQL executado.
			// Percorre o resultado e monta lista com os dias da semana que tem aula de cada turma
			for (Object object : lista) {
				Map<String, Object> linha = (Map<String, Object>) object;
	
				Integer dia = new Integer((String) linha.get("dia"));
				Integer idTurma = (Integer) linha.get("id_turma");
				
				if (dias.get(idTurma) == null)
					dias.put(idTurma, new ArrayList<Integer>());
				
				dias.get(idTurma).add(dia);
			}
			
			return calcularQuantidadeDeAulasDasTurmas(lista, dias, inicioAfastamento, fimAfastamento);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	/**
	 * Calcula a quantidade de aulas que cada turma do professor possui
	 * 
	 * @param lista
	 * @param diasTurmas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List<DadosTurmaDocenteDTO> calcularQuantidadeDeAulasDasTurmas(List lista, Map<Integer, List<Integer>> diasTurmas, Date inicioAfastamento, Date fimAfastamento) {
		
		List<DadosTurmaDocenteDTO> dadosTurmas = new ArrayList<DadosTurmaDocenteDTO>();
		
		for (Object object : lista) {
			// Mapa contendo cada linha do resultado do SQL executado.
			Map<String, Object> linha = (Map<String, Object>) object;
			
			Integer idTurma = (Integer) linha.get("id_turma");
			Date inicio = (Date) linha.get("data_inicio");
			Date fim = (Date) linha.get("data_fim");
			Date horaInicio = (Date) linha.get("hora_inicio");
			Date horaFim = (Date) linha.get("hora_fim");
			String descricaoHorario = (String) linha.get("descricao_horario");
			
			//if(CalendarUtils.isIntervalosDeDatasConflitantes(inicio, fim, inicio2, termino2))
			DadosTurmaDocenteDTO dtd = new DadosTurmaDocenteDTO();
			dtd.setIdTurma((Integer) linha.get("id_turma"));
			
			// Se a turma já estiver na lista
			if (dadosTurmas.contains(dtd))
				continue;
			
			// Lista com os dias da semana que tem aula
			List<Integer> dias = diasTurmas.get(idTurma);
			
			// Set que irá armazenar as datas da turma
			Set<Date> datas = new TreeSet<Date>();
			Map<Date, Date> horariosTurma = new HashMap<Date, Date>();
			Calendar cal = Calendar.getInstance();
			Date dataBase = inicio;
			
			// Comeca na data qua turma iniciou e vai incrementando até chegar na data final da turma
			while (dataBase.before(fim) || dataBase.equals(fim)) {
				cal.setTime(dataBase);
				if (dias.contains(cal.get(Calendar.DAY_OF_WEEK))) 
					if(CalendarUtils.isDentroPeriodo(inicioAfastamento, fimAfastamento, dataBase)){ //se não estiver dentro do período do afastamento não incluir
						datas.add(dataBase);
						if(!horariosTurma.containsKey(horaInicio))
							horariosTurma.put(horaInicio, horaFim);
					}
				cal.add(Calendar.DAY_OF_MONTH, 1);
				dataBase = cal.getTime();
			}
			
			dtd.setCodigoTurma((String) linha.get("codigo"));
			dtd.setDisciplina(linha.get("disciplina_codigo") + " - " + linha.get("disciplina_nome"));
			dtd.setAulas(datas);
			dtd.setHorariosTurma(horariosTurma);
			dtd.setDescricaohorario(descricaoHorario);
			
			if(!datas.isEmpty()){
				dadosTurmas.add(dtd);
			}
			
		}
		
		return dadosTurmas;
	}

	/**
	 * Verifica se o servidor elaborou um plano de aula para o dia em que faltou
	 * 
	 * @param dataFalta Data que o docente não deu aula
	 * @param idServidor ID do servidor
	 * @return
	 */
	public static boolean isDocenteSemPlanoAula(Date dataFalta, int idServidor) {
		JdbcTemplate jt = getJdbcTemplate(Sistema.SIGAA);
		
		int count = jt.queryForInt("select count(*) from ensino.falta_homologada fh " +
				" inner join ensino.docente_turma dt using (id_docente_turma) " +
				" inner join rh.servidor s on ( dt.id_docente = s.id_servidor) " +
				" left join ensino.plano_reposicao_aula pra on (fh.id_falta_homologada = pra.id_falta_homologada) " +
				" where fh.data_aula = ? and s.id_servidor = ? and pra.id_falta_homologada is null", new Object[] { dataFalta, idServidor });
		
		if (count > 0)
			return true;
		
		return false;
		
	}
	
	/**
	 * Retorna {@link JdbcTemplate} com uma conexão com o {@link Sistema} passado como argumento
	 * @param sistema
	 * @return
	 */
	private static JdbcTemplate getJdbcTemplate(int sistema) {
		GenericDAOImpl dao = new GenericDAOImpl(sistema);
		return dao.getJdbcTemplate();
	}
	
}
