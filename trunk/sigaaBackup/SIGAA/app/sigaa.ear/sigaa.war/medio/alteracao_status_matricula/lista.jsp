<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="alteracaoStatusMatriculaMedioMBean"/>
<h2> <ufrn:subSistema /> &gt; Busca de Turmas</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Critérios de busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{alteracaoStatusMatriculaMedioMBean.filtroAno}" id="checkAno" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkAno" onclick="$('form:checkNumero').checked = !$('form:checkAno').checked;">Ano:</label></td>
				<td><h:inputText value="#{alteracaoStatusMatriculaMedioMBean.ano}" onfocus="$('form:checkAno').checked = true;" size="4" maxlength="4" 
								id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/></td>
			</tr> 		
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{alteracaoStatusMatriculaMedioMBean.filtroNumero}" id="checkNumero" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkNumero" onclick="$('form:checkNumero').checked = !$('form:checkNumero').checked;">Série:</label></td>
				<td><h:inputText size="4" maxlength="4" value="#{alteracaoStatusMatriculaMedioMBean.serie.numero}" id="inputSerie"
							onfocus="$('form:checkNumero').checked = true;" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{alteracaoStatusMatriculaMedioMBean.filtroTurma}" id="checkTurma" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkTurma" onclick="$('form:checkTurma').checked = !$('form:checkTurma').checked;">Turma:</label></td>
				<td><h:inputText size="4" maxlength="4" value="#{alteracaoStatusMatriculaMedioMBean.turma}" onfocus="$('form:checkTurma').checked = true;"  id="inputTurma" onkeyup="CAPS(this)"/></td>
			</tr>			
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{alteracaoStatusMatriculaMedioMBean.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td>
					<h:selectOneMenu value="#{ alteracaoStatusMatriculaMedioMBean.serie.cursoMedio.id }" onchange="$('form:checkCurso').checked = true;" style="width: 95%;" id="selectCurso">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{ cursoMedio.allCombo }" />
					</h:selectOneMenu>
				</td>	
			</tr>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{alteracaoStatusMatriculaMedioMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{alteracaoStatusMatriculaMedioMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />
	
<c:if test="${not empty alteracaoStatusMatriculaMedioMBean.listaSeries}">	
	
	<div class="infoAltRem" style="width:70%">
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar Turma
	</div>
	
	<table class="listagem" style="width:70%">
	  <caption>Turmas Encontradas (${fn:length(alteracaoStatusMatriculaMedioMBean.listaSeries)})</caption>
		<thead>
			<tr>
				<th style="text-align: center;" width="30%">Ano</th>
				<th>Série</th>
				<th style="text-align: center;">Turma</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
		   <c:set var="idCurso" value="0"/>
		   <c:forEach var="linha" items="#{alteracaoStatusMatriculaMedioMBean.listaSeries}" varStatus="status">
		   		
		   		<c:if test="${idCurso != linha.serie.cursoMedio.id}">
		   			<tr style="background: #C8D5EC">
		   				<td colspan="4" style="font-weight: bold;">${linha.serie.cursoMedio.nomeCompleto}</td>
		   			</tr>		   			
		   		</c:if>
		   		
		   		<c:set var="idCurso" value="${linha.serie.cursoMedio.id}"/>
		   		
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: center;">${linha.ano}</td>
					<td>${linha.serie.descricaoCompleta}</td>
					<td style="text-align: center;">${linha.nome}</td>
					<td width="2%" align="right">
						<h:commandLink action="#{alteracaoStatusMatriculaMedioMBean.selecionarTurma}" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Selecionar Turma" />  
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