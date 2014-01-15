<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${acesso.coordenadorPolo}">
	<%@include file="/portais/cpolo/menu_cpolo.jsp" %>
</c:if>
	<h2><ufrn:subSistema /> > Relatório de Alunos por Pólo</h2>
	<h:outputText value="#{relatorioAlunosPolo.create}" />
	<h:messages showDetail="true"/>
	<h:form id="busca">
		<table class="formulario" width="80%">
			<caption>Dados do Relatório</caption>
			<tbody>
				
				<c:if test="${acesso.coordenadorPolo == true}">
				<tr>
					<th class="required">Polo:</th>
					<td>
						<h:selectOneMenu disabled="true" id="poloSelecionado" value="#{relatorioHorario.polo.id}">
						<f:selectItems value="#{relatorioHorario.polos}" />
						<h:inputHidden id="poloSelecionado2" value="#{relatorioHorario.polo.id}"></h:inputHidden>
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<c:if test="${acesso.coordenadorPolo == false}">
				<tr>
					<td class="required">Polo: </td>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosPolo.polo.id}">
						<f:selectItem itemValue="0" itemLabel="Todos" />
						<f:selectItems value="#{tutorOrientador.polos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				</c:if>
				
				<tr>
					<td class="required">Curso: </td>
					<td>
						<h:selectOneMenu value="#{relatorioAlunosPolo.curso.id}">
						<f:selectItem itemValue="0" itemLabel="Todos" />
						<f:selectItems value="#{tutorOrientador.cursos}" />
						</h:selectOneMenu> 
					</td>
				</tr>
				<tr>
					<td>Ano-Período de Ingresso: </td>
					<td>
						<h:inputText value="#{relatorioAlunosPolo.ano}" maxlength="4" size="4" onkeyup="return formatarInteiro(this);"/> - <h:inputText value="#{relatorioAlunosPolo.periodo}" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/> 
					</td>
				</tr>
				<tr>
					<td class="required">Tipo do Relatório:</td>
					<td>
						<h:selectOneRadio value="#{relatorioAlunosPolo.analitico}" id="formato">
						<f:selectItem itemValue="true" itemLabel="Analítico" />
						<f:selectItem itemValue="false" itemLabel="Sintético" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<td>Apenas matriculados:</td>
					<td>
						<h:selectBooleanCheckbox value="#{ relatorioAlunosPolo.matriculados }"/>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{relatorioAlunosPolo.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioAlunosPolo.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br />
	<div align="center">
		<img src="/shared/img/required.gif" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</div>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>