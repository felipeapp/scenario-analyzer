<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<f:view>

<%@include file="/public/docente/cabecalho.jsp" %>

<div id="id-docente">
	<h3>${fn:toLowerCase(docente.nome)}</h3>
	<p class="departamento">${docente.unidade.siglaAcademica} - ${docente.unidade.nome}</p>
</div>

<div id="disciplinas-docente">
	<h4>Disciplinas Ministradas</h4>

	<c:set var="turmasGraduacao" value="#{portal.turmasGraduacao}" />
	<c:set var="turmasPos" value="#{portal.turmasPosGraduacao}" />
	<c:set var="turmasTecnico" value="#{portal.turmasTecnico}" />
	<c:set var="turmasMedio" value="#{portal.turmasMedio}" />
	<c:set var="turmasInfantil" value="#{portal.turmasInfantil}" />

	<div id="abas-turmas">
		<div id="turmas-graduacao" class="aba">
			<c:if test="${not empty turmasGraduacao}">
				<table class="listagem">
					<thead>
						<tr>
							<th colspan="2">Disciplina</th>
							<th>Carga Horária</th>
							<th>Horário</th>
						</tr>
					</thead>

					<tbody>
						<c:set var="anoPeriodo" value=""/>
						<c:forEach var="turma" items="#{turmasGraduacao}" varStatus="loop">

						<c:if test="${anoPeriodo != turma.anoPeriodo}">
							<c:set var="anoPeriodo" value="${turma.anoPeriodo}"/>

							<c:if test="${not loop.first}">
								<tr> <td class="spacer" colspan="5"> </td> </tr>
							</c:if>

							<tr><td class="anoPeriodo" colspan="5"> ${anoPeriodo}</td></tr>
						</c:if>
						<tr>
							<td class="codigo"> ${turma.disciplina.codigo} </td>
							<td> <a href="${ctx}/public/docente/turma.jsf?tid=${turma.id}">${turma.disciplina.nome}</a> </td>
							<td class="ch"> ${turma.disciplina.chTotal}h </td>
							<td class="horario"> ${empty turma.descricaoHorario ? '-' : turma.descricaoHorario }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${empty turmasGraduacao}">
				<p class="vazio">
					Nenhuma turma encontrada
				</p>
			</c:if>
		</div>

		<div id="turmas-pos" class="aba">
			<c:if test="${not empty turmasPos}">
				<table class="listagem">
					<thead>
						<tr>
							<th colspan="2">Disciplina</th>
							<th>Carga Horária</th>
							<th>Horário</th>
						</tr>
					</thead>

					<tbody>
						<c:set var="anoPeriodo" value=""/>
						<c:forEach var="turma" items="#{turmasPos}" varStatus="loop">

						<c:if test="${anoPeriodo != turma.anoPeriodo}">
							<c:set var="anoPeriodo" value="${turma.anoPeriodo}"/>

							<c:if test="${not loop.first}">
								<tr> <td class="spacer" colspan="5"> </td> </tr>
							</c:if>

							<tr><td class="anoPeriodo" colspan="5"> ${anoPeriodo}</td></tr>
						</c:if>
						<tr>
							<td class="codigo"> ${turma.disciplina.codigo} </td>
							<td> <a href="${ctx}/public/docente/turma.jsf?tid=${turma.id}">${turma.disciplina.nome}</a> </td>
							<td class="ch"> ${turma.disciplina.chTotal}h </td>
							<td class="horario"> ${empty turma.descricaoHorario ? '-' : turma.descricaoHorario }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${empty turmasPos}">
				<p class="vazio">
					Nenhuma turma encontrada
				</p>
			</c:if>
		</div>
		
		<div id="turmas-tecnico" class="aba">
			<c:if test="${not empty turmasTecnico}">
				<table class="listagem">
					<thead>
						<tr>
							<th colspan="2">Disciplina</th>
							<th>Carga Horária</th>
							<th>Horário</th>
						</tr>
					</thead>

					<tbody>
						<c:set var="anoPeriodo" value=""/>
						<c:forEach var="turma" items="#{turmasTecnico}" varStatus="loop">

						<c:if test="${anoPeriodo != turma.anoPeriodo}">
							<c:set var="anoPeriodo" value="${turma.anoPeriodo}"/>

							<c:if test="${not loop.first}">
								<tr> <td class="spacer" colspan="5"> </td> </tr>
							</c:if>

							<tr><td class="anoPeriodo" colspan="5"> ${anoPeriodo}</td></tr>
						</c:if>
						<tr>
							<td class="codigo"> ${turma.disciplina.codigo} </td>
							<td> <a href="${ctx}/public/docente/turma.jsf?tid=${turma.id}">${turma.disciplina.nome}</a> </td>
							<td class="ch"> ${turma.disciplina.chTotal}h </td>
							<td class="horario"> ${empty turma.descricaoHorario ? '-' : turma.descricaoHorario }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${empty turmasTecnico}">
				<p class="vazio">
					Nenhuma turma encontrada
				</p>
			</c:if>
		</div>
				
		<div id="turmas-medio" class="aba">
			<c:if test="${not empty turmasMedio}">
				<table class="listagem">
					<thead>
						<tr>
							<th colspan="2">Disciplina</th>
							<th>Carga Horária</th>
							<th>Horário</th>
						</tr>
					</thead>

					<tbody>
						<c:set var="anoPeriodo" value=""/>
						<c:forEach var="turma" items="#{turmasMedio}" varStatus="loop">

						<c:if test="${anoPeriodo != turma.anoPeriodo}">
							<c:set var="anoPeriodo" value="${turma.anoPeriodo}"/>

							<c:if test="${not loop.first}">
								<tr> <td class="spacer" colspan="5"> </td> </tr>
							</c:if>

							<tr><td class="anoPeriodo" colspan="5"> ${anoPeriodo}</td></tr>
						</c:if>
						<tr>
							<td class="codigo"> ${turma.disciplina.codigo} </td>
							<td> <a href="${ctx}/public/docente/turma.jsf?tid=${turma.id}">${turma.disciplina.nome}</a> </td>
							<td class="ch"> ${turma.disciplina.chTotal}h </td>
							<td class="horario"> ${empty turma.descricaoHorario ? '-' : turma.descricaoHorario }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${empty turmasMedio}">
				<p class="vazio">
					Nenhuma turma encontrada
				</p>
			</c:if>
		</div>		
		
		<div id="turmas-infantil" class="aba">
			<c:if test="${not empty turmasInfantil}">
				<table class="listagem">
					<thead>
						<tr>
							<th colspan="2">Disciplina</th>
							<th>Carga Horária</th>
							<th>Horário</th>
						</tr>
					</thead>

					<tbody>
						<c:set var="anoPeriodo" value=""/>
						<c:forEach var="turma" items="#{turmasInfantil}" varStatus="loop">

						<c:if test="${anoPeriodo != turma.anoPeriodo}">
							<c:set var="anoPeriodo" value="${turma.anoPeriodo}"/>

							<c:if test="${not loop.first}">
								<tr> <td class="spacer" colspan="5"> </td> </tr>
							</c:if>
 
							<tr><td class="anoPeriodo" colspan="5"> ${anoPeriodo}</td></tr>
						</c:if>
						<tr>
							<td class="codigo"> ${turma.disciplina.codigo} </td>
							<td> <a href="${ctx}/public/docente/turma.jsf?tid=${turma.id}">${turma.disciplina.nome}</a> </td>
							<td class="ch"> ${turma.disciplina.chTotal}h </td>
							<td class="horario"> ${empty turma.descricaoHorario ? '-' : turma.descricaoHorario }</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:if>

			<c:if test="${empty turmasInfantil}">
				<p class="vazio">
					Nenhuma turma encontrada
				</p>
			</c:if>
		</div>		
	</div>
</div>

<script>
Ext.onReady(function(){
    var tabs = new Ext.TabPanel({
        renderTo: 'abas-turmas',
        activeItem: 0,
        plain:true,
        defaults:{autoHeight: true},
        items:[
			{contentEl:'turmas-infantil', title: 'Infantil'},	   
			{contentEl:'turmas-medio', title: 'Médio'},	   
            {contentEl:'turmas-tecnico', title: 'Técnico'},
            {contentEl:'turmas-graduacao', title: 'Graduação'},
            {contentEl:'turmas-pos', title: 'Pós-Graduação'}
        ]
    });
    tabs.activate(0);
    tabs.activate(2);
    tabs.activate(1);
    tabs.activate(3);
    tabs.activate(4);
});
</script>
</f:view>
<%@include file="/public/include/rodape.jsp" %>
