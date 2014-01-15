<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="turmaEntradaTecnicoMBean"/>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>
<h2> <ufrn:subSistema /> > Listagem de Turmas de Entrada</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 60%">
	  <caption>Informe os critérios da busca</caption>
 		<tbody>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{turmaEntradaTecnicoMBean.filtroAno}" id="checkAno" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkAno" onclick="$('form:checkAno').checked = !$('form:checkAno').checked;">Ano:</label></td>
				<td><h:inputText size="10" maxlength="4" value="#{turmaEntradaTecnicoMBean.obj.anoReferencia}" onfocus="$('form:checkAno').checked = true;" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{turmaEntradaTecnicoMBean.filtroCurso}" id="checkCurso" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkCurso" onclick="$('form:checkCurso').checked = !$('form:checkCurso').checked;">Curso:</label></td>
				<td colspan="5">
					<h:selectOneMenu value="#{turmaEntradaTecnicoMBean.obj.estruturaCurricularTecnica.cursoTecnico.id}" id="curso" 
					onchange="$('form:checkCurso').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{curso.allCursoNivelAtualCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${not turmaEntradaTecnicoMBean.portalEscolasEspecializadas}">
			<tr>
				<td width="3%"><h:selectBooleanCheckbox value="#{turmaEntradaTecnicoMBean.filtroEspecializacao}" id="checkEspecializacao" styleClass="noborder"/> </td>
				<td width="20%"><label for="checkEspecializacao" onclick="$('form:checkEspecializacao').checked = !$('form:checkEspecializacao').checked;">Especialização:</label></td>
				<td colspan="5">
					<h:selectOneMenu value="#{turmaEntradaTecnicoMBean.obj.especializacao.id}" id="especializacao" 
					onchange="$('form:checkEspecializacao').checked = true;">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{turmaEntradaTecnicoMBean.allEspecializacao}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			</c:if>
	    </tbody>
	    <tfoot>
		    <tr>
				<td colspan="4">
					<h:commandButton value="Buscar" action="#{turmaEntradaTecnicoMBean.buscar}" id="buscar" />
					<h:commandButton value="Cancelar" action="#{turmaEntradaTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		    </tr>
		</tfoot>
	</table>
	
	<br /><br />

<div class="infoAltRem">
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>: Atualizar Turma de Entrada
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Turma de Entrada
</div>
	
	<table class="listagem">
	  <caption>Turmas de Entrada Encontradas (${ fn:length(turmaEntradaTecnicoMBean.turmaEntradaTecnico) })</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Descrição</th>
				<th style="text-align: left;">Especialização</th>
				<th style="text-align: center;">Ano</th>
				<th style="text-align: center;">Período</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{turmaEntradaTecnicoMBean.turmaEntradaTecnico}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="40%" style="text-align: left;">${linha.cursoTecnico.descricao}</td>
					<td style="text-align: left;">${linha.especializacao.descricao}</td>
					<td style="text-align: center;">${linha.anoReferencia}</td>
					<td style="text-align: center;">${linha.periodoReferencia}</td>
					<td width="3%" align="right">
						<h:commandLink action="#{turmaEntradaTecnicoMBean.atualizar}">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar Turma de Entrada" />  
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
					<td width="3%" align="right">
						<h:commandLink action="#{turmaEntradaTecnicoMBean.inativar}" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Turma de Entrada" />
							<f:param name="id" value="#{linha.id}"/>
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>