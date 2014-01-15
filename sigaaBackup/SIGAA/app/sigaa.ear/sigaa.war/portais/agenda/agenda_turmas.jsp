<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<c:set var="hideSubsistema" value="true" />

<style>
.fc-header-title{
	margin-top:0;
	text-align:center;
	white-space:nowrap;
	width:250px;
}

</style>
<f:view>
	<p:resources/>
	<h:form id="form">
	<input type="hidden" name="aba" id="aba" />
	<h2><ufrn:subSistema /> > Agenda de Horário das Turmas no Semestre Atual</h2>
	<p:tabView>  
  
	    <p:tab title="Agenda de Horários">  
	        <p:schedule id="scheduleAgenda"
					value="#{agendaTurmasBean.turmasAgendaModel}"
					editable="false"
					draggable="false" 
					widgetVar="minhaAgenda" 
					minTime="7" 
					maxTime="23"
					view="agendaWeek" 
					locale="pt" 
					aspectRatio="2" />
	    </p:tab>  
	    <p:tab title="Lista de Turmas">  
	        <c:set var="turmas" value="#{ gradeDocente.turmas }"/>
			<c:set var="horariosTurma" value="#{ gradeDocente.horariosTurma }"/>
			<c:set var="nivelGrupo" value="" />
			<c:forEach var="item" items="#{ turmas }" varStatus="status">
			<c:if test="${nivelGrupo != item.disciplina.nivel}">
				<c:if test="${fechaTabela}">
					</tbody>
					</table>
					<br/>
				</c:if>
				<c:set var="count" value="0" />
				<c:forEach var="turmaCount" items="#{turmas}">
					<c:if test="${turmaCount.disciplina.nivelDesc == item.disciplina.nivelDesc}"><c:set var="count" value="${count + 1}" /></c:if>
				</c:forEach>
				<table id="matriculas" class="listagem">
				<caption>Turmas de ${ item.disciplina.nivelDesc } (${count})</caption>
				<thead>
					<tr>
						<th width="10%" align="center">Cód.</th>
						<th width="45%">Disciplinas/Docentes</th>
						<th width="5%" align="center">Turma</th>
						<th width="20%" align="center">Local</th>
						<th width="20%" align="center" class="direita">Horário</th>
					</tr>
				</thead>
				<tbody>
				<c:set var="nivelGrupo" value="${item.disciplina.nivel}" />
				<c:set var="fechaTabela" value="true" />
			</c:if>
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td class="codigo" valign="top">${ item.disciplina.codigo }<br/>
							  	(${ item.disciplina.tipoComponente.descricao })</td>
						<td valign="top">
							<span class="componente">
								${ item.disciplina.nome } 
								<span class="nivelComponente"> (${item.anoPeriodo}) </span>
							</span>
							<span class="docente">${ item.docentesNomes }</span>
						</td>
						<td class="turma" valign="top">${ item.codigo }</td>
						<td class="status" style="font-variant: small-caps;" valign="top">${ item.local }</td>
						<td class="horario" valign="top">${ item.descricaoHorario }</td>
					</tr>
		</c:forEach>
				</tbody>
				</table>
	    </p:tab>  
	</p:tabView>
	<br/>
	<div align="center">
		<a href="javascript:history.go(-1)">&lt;&lt; voltar</a>
	</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
