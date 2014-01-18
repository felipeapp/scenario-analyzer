<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="tutoriaIMD"/>
<h2><ufrn:subSistema /> > Relatório de Discentes por Turma > Seleção da Turma do IMD</h2> 

	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<!-- DADOS GERAIS -->
			<!-- Cursos do IMD -->
			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td colspan="4">
					<h:selectOneMenu value="#{tutoriaIMD.curso.id}" valueChangeListener="#{tutoriaIMD.carregarModulos}" id="curso" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tutoriaIMD.cursosCombo}" />
				 		<a4j:support event="onchange" reRender="modulo" />  
					</h:selectOneMenu>
				</td>
			</tr>
			
			<!--Lista de Módulos -->
			<tr>
				<th>Módulo:</th>
				<td colspan="4">
					<h:selectOneMenu value="#{tutoriaIMD.modulo.id}" id="modulo" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tutoriaIMD.modulosCombo}" />   
					</h:selectOneMenu>
				</td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="5">
					<h:commandButton value="Buscar" action="#{tutoriaIMD.listarTurmas}" id="listar" />
					<h:commandButton value="Cancelar" action="#{tutoriaIMD.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
<c:if test="${not empty tutoriaIMD.listaTurmas}">
<div class="infoAltRem">
	<html:img page="/img/seta.gif" style="overflow: visible;"/>: Selecionar Turma
</div>
	
	<table class="listagem">
	  <caption>Turmas Encontradas (${fn:length(tutoriaIMD.listaTurmas)})</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Ano - Período</th>
				<th style="text-align: left;">Turma</th>
				<th style="text-align: left;">Local</th>
				<th style="text-align: left;">Horário</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{tutoriaIMD.listaTurmas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
					<td width="15%" style="text-align: left;">${linha.anoReferencia} - ${linha.periodoReferencia}</td>
					<td width="52%" style="text-align: left;">${linha.especializacao.descricao}</td>
					<td width="15%" style="text-align: left;">${linha.dadosTurmaIMD.local}</td>
					<td width="15%"style="text-align: left;">${linha.dadosTurmaIMD.horario}</td>
					
					<td width="10px;">
						<h:commandLink action="#{tutoriaIMD.gerarRelatorioListaDiscentes}"  >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" alt="Selecionar Turma" title="Selecionar Turma"/>
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					
				</tr>
		   </c:forEach>
		</tbody>
	</table>
</c:if>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

