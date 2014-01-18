<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="tutoriaIMD"/>
<h2> <ufrn:subSistema /> > Listagem de Turmas</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<!-- DADOS GERAIS -->
			<!-- Cursos do IMD -->
			<tr>
				<th width="25%" class="obrigatorio">Curso:</th>
				<td>
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
				<td>
					<h:selectOneMenu value="#{tutoriaIMD.modulo.id}" id="modulo" required="true">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tutoriaIMD.modulosCombo}" />   
					</h:selectOneMenu>
				</td>
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{tutoriaIMD.listarTurmas}" id="listar" />
					<h:commandButton value="Cancelar" action="#{tutoriaIMD.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
<c:if test="${not empty tutoriaIMD.listaTurmas}">
<div class="infoAltRem">
	<html:img page="/img/user.png" style="overflow: visible;"/>: Vincular/Alterar Tutor
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Alterar Turma
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Turma
</div>
	
	<table class="listagem">
	  <caption>Turmas Encontradas (${fn:length(tutoriaIMD.listaTurmas)})</caption>
		<thead>
			<tr>
				
				<th style="text-align: left;">Turma</th>
				<th style="text-align: left;">Módulo</th>
				<th style="text-align: left;">Pólo</th>
				<th style="text-align: left;">Local</th>
				<th style="text-align: left;">Horário</th>
				<th style="text-align: left;">Código Moodle</th>
				<th style="text-align: left;">Prefixo Moodle</th>
				
				<th colspan="3"></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{tutoriaIMD.listaTurmas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="10%" style="text-align: left;">${linha.especializacao.descricao} (${linha.anoReferencia}.${linha.periodoReferencia})</td>
					<td width="30%" style="text-align: left;">${linha.dadosTurmaIMD.cronograma.modulo.descricao}</td>
					<td width="15%" style="text-align: left;">${linha.opcaoPoloGrupo.polo.descricao}</td>
					<td width="9%" style="text-align: left;">${linha.dadosTurmaIMD.local}</td>
					<td width="6%"style="text-align: left;">${linha.dadosTurmaIMD.horario}</td>
					
					<td width="8%" style="text-align: left;">${linha.dadosTurmaIMD.codigoIntegracao}</td>
					
					<c:if test="${not empty linha.dadosTurmaIMD.codigoIntegracao}">
						<td width="8%" style="text-align: left;">T${linha.dadosTurmaIMD.cronograma.ano}${linha.dadosTurmaIMD.cronograma.periodo}C${linha.dadosTurmaIMD.cronograma.id}</td>
					</c:if>
					<c:if test="${empty linha.dadosTurmaIMD.codigoIntegracao}">
						<td width="8%" style="text-align: left;"></td>
					</c:if>
					
					<td width="3%" align="right">
						<h:commandLink action="#{tutoriaIMD.preVincularTutor}">
							<h:graphicImage value="/img/user.png" style="overflow: visible;" title="Vincular/Alterar Tutor" alt="Vincular/Alterar Tutor" />  
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					<td width="3%" align="right">
						<h:commandLink action="#{tutoriaIMD.preAlterar}">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Turma"  alt="Alterar Turma" />  
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					<td width="3%" align="right">
						<h:commandLink action="#{tutoriaIMD.remover}" id="remover" onclick="#{confirmDelete}">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Turma" alt="Remover Turma"  />
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