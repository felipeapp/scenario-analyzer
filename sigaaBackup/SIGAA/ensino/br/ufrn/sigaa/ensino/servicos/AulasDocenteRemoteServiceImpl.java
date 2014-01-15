/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 'Jan 27, 2010'
 *
 */

package br.ufrn.sigaa.ensino.servicos;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.DadosTurmaDocenteDTO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.AulasDocenteDto;
import br.ufrn.integracao.interfaces.AulasDocenteRemoteService;

/**
 * Implementação dos serviços remotos definidos na interface <code>AulasDocenteRemoteService</code>
 * 
 * @author Rômulo Augusto
 */
@WebService
public class AulasDocenteRemoteServiceImpl implements AulasDocenteRemoteService {

	public String getTurmasDocenteAsString(AulasDocenteDto aulasDocenteDto) {
		
		StringBuilder mensagemAulas = new StringBuilder();
		
		try {
			List<DadosTurmaDocenteDTO> aulasSigaa = findAulasByDocente(aulasDocenteDto.getIdServidor(), aulasDocenteDto.getInicioAfastamento(), aulasDocenteDto.getFimAfastamento());
			
			//se o servidor tem aulas no período
			if (!aulasSigaa.isEmpty()) {
				
				mensagemAulas.append("<ul>");
				
				for (DadosTurmaDocenteDTO aula : aulasSigaa) {
					mensagemAulas.append("<li>").append(aula.toString()).append("</li>");
				}
				
				mensagemAulas.append("</ul>");
			}
			
			return mensagemAulas.toString();
		} catch(DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}
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
	private List<DadosTurmaDocenteDTO> findAulasByDocente(int idServidor, Date inicioAfastamento, Date fimAfastamento) throws DAOException {

		JdbcTemplate jt = getJdbcTemplate(Sistema.SIGAA);
		
		try {
			
			List lista = jt.queryForList("select ht.hora_inicio, ht.hora_fim, ht.id_turma, ht.dia, ht.data_inicio, ht.data_fim, detalhes.nome_ascii as disciplina_nome, cc.codigo as disciplina_codigo, t.codigo, t.descricao_horario " +
					" from ensino.docente_turma dt " +
					" join ensino.turma t using (id_turma) " +
					" join ensino.componente_curricular cc using (id_disciplina) " +
					" join ensino.componente_curricular_detalhes detalhes on (cc.id_detalhe = detalhes.id_componente_detalhes) " +
					" join ensino.horario_turma ht using (id_turma) " +
					" join ensino.horario h using (id_horario) " +
					" where dt.id_docente = ? and (DATE (?), DATE (?) ) OVERLAPS ( t.data_inicio, t.data_fim ) " +
					" order by t.id_turma asc, h.id_horario, ht.dia", 
					new Object[] {idServidor, inicioAfastamento.toString(), fimAfastamento.toString()});
			
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
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Calcula a quantidade de aulas que cada turma do professor possui.
	 * 
	 * @param lista
	 * @param diasTurmas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DadosTurmaDocenteDTO> calcularQuantidadeDeAulasDasTurmas(List lista, Map<Integer, List<Integer>> diasTurmas, Date inicioAfastamento, Date fimAfastamento) {
		
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
			
			descricaoHorario = (ValidatorUtil.isEmpty(descricaoHorario)) ? StringUtils.EMPTY : descricaoHorario;
			
			DadosTurmaDocenteDTO dtd = new DadosTurmaDocenteDTO();
			dtd.setIdTurma((Integer) linha.get("id_turma"));
			
			// Se a turma já estiver na lista
			if (dadosTurmas.contains(dtd)) {
				continue;
			}
			
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
				
				if (dias.contains(cal.get(Calendar.DAY_OF_WEEK))) {
					
					//se não estiver dentro do período do afastamento não incluir
					if (CalendarUtils.isDentroPeriodo(inicioAfastamento, fimAfastamento, dataBase)) {
						
						datas.add(dataBase);
						
						if (!horariosTurma.containsKey(horaInicio)) {
							horariosTurma.put(horaInicio, horaFim);
						}
					}
				}
				
				cal.add(Calendar.DAY_OF_MONTH, 1);
				dataBase = cal.getTime();
			}
			
			dtd.setCodigoTurma((String) linha.get("codigo"));
			dtd.setDisciplina(linha.get("disciplina_codigo") + " - " + linha.get("disciplina_nome"));
			dtd.setAulas(datas);
			dtd.setHorariosTurma(horariosTurma);
			dtd.setDescricaohorario(descricaoHorario);
			
			if (!datas.isEmpty()) {
				dadosTurmas.add(dtd);
			}
		}
		
		return dadosTurmas;
	}

	/**
	 * Retorna {@link JdbcTemplate} com uma conexão com o {@link Sistema} passado como argumento
	 * @param sistema
	 * @return
	 */
	private JdbcTemplate getJdbcTemplate(int sistema) {
		GenericDAOImpl dao = new GenericDAOImpl(sistema);
		return dao.getJdbcTemplate();
	}
		
}
