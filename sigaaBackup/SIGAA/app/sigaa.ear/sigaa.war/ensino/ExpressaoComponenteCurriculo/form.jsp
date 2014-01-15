<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>

<f:view>
	<h2><ufrn:subSistema /> &gt; Expressões Específicas de Currículo</h2>

	<h:form id="form">
	<a4j:keepAlive beanName="expressaoComponenteCurriculoBean" />
	
		<table class="visualizacao" style="width: 90%">
			<thead>
				<tr>
					<th colspan="2">Dados do Componente Curricular</th>
				</tr>
			</thead>
			<tr>
				<th width="20%" style="text-align: right;">Componente:</th>
				<td width="80%" style="text-align: left;">${expressaoComponenteCurriculoBean.obj.componente.descricao }</td>
			</tr>
			<tr>
				<th style="text-align: right;"> Unidade: </th>
				<td style="text-align: left;"> ${expressaoComponenteCurriculoBean.obj.componente.unidade.nome } </td>
			</tr>
			<tr>
				<th style="text-align: right;"> Equivalência: </th>
				<td style="text-align: left;"> <sigaa:expressao expr="${expressaoComponenteCurriculoBean.obj.componente.equivalencia}"/>  </td>
			</tr>
			<tr>
				<th style="text-align: right;"> Pré-requisitos: </th>
				<td style="text-align: left;"> <sigaa:expressao expr="${expressaoComponenteCurriculoBean.obj.componente.preRequisito}"/> </td>
			</tr>
			<tr>
				<th style="text-align: right;"> Co-requisitos: </th>
				<td style="text-align: left;"> <sigaa:expressao expr="${expressaoComponenteCurriculoBean.obj.componente.coRequisito}"/> </td>
			</tr>
			
			<c:if test="${not empty expressaoComponenteCurriculoBean.expressoes}">
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center">Expressões já cadastradas para este componente</td>
			</tr>
			
			<tr>
				<td colspan="2"> 
					<table width="100%">
						<thead >
							<tr >
								<th width="50%" style="text-align: center">Curriculo</th>
								<th width="20%" style="text-align: left">Co-requisitos</th>
								<th width="20%" style="text-align: left">Pre-requisitos</th>
							</tr>
						</thead>
						<tbody>
						<c:forEach var="exp" items="${expressaoComponenteCurriculoBean.expressoes}">
								<td>${exp.curriculo.descricaoCursoCurriculo}</td>
								<td> <sigaa:expressao expr="${exp.corequisito}"/></td>
								<td> <sigaa:expressao expr="${exp.prerequisito}"/></td>
						</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
			</c:if>

		</table>
		<br/>
	
	
	
		<table class="formulario">
			<caption>Cadastro de Expressões Específicas de um Currículo</caption>
			<tr>
				<th>Currículo: <span class="required">&nbsp;</span></th>
				<td>
					<a4j:region>
					<h:selectOneMenu id="curriculo" value="#{expressaoComponenteCurriculoBean.obj.curriculo.id }" 
					valueChangeListener="#{expressaoComponenteCurriculoBean.carregarExpComponenteCurriculoSelecionado}">
						<f:selectItems value="#{expressaoComponenteCurriculoBean.curriculos}" />
						<a4j:support event="onchange" reRender="ativo, expressaoprerequisito, expressaocorequisito,idcomponente, idobj, btnConfirmar"></a4j:support>
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/ajax-loader.gif"/>
						</f:facet>
					</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr>
				<th>Ativo:</th>
				<td>
					<h:selectOneRadio value="#{expressaoComponenteCurriculoBean.obj.ativo}" id="ativo">
						<f:selectItem itemValue="true" itemLabel="Sim" />
						<f:selectItem itemValue="false" itemLabel="Não" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th>Expressão de Pre-requisito:</th>
				<td><h:inputText id="expressaoprerequisito" value="#{ expressaoComponenteCurriculoBean.preRequisitoForm }" size="50"/></td>
			</tr>
			<tr>
				<th>Expressão de Co-requisito:</th>
				<td><h:inputText id="expressaocorequisito" value="#{ expressaoComponenteCurriculoBean.coRequisitoForm }" size="50"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden id="idobj" value="#{expressaoComponenteCurriculoBean.obj.id}" /> 
						<h:inputHidden id="idcomponente" value="#{expressaoComponenteCurriculoBean.obj.componente.id}" />
						<h:commandButton value="#{expressaoComponenteCurriculoBean.confirmButton}" action="#{expressaoComponenteCurriculoBean.cadastrar}" id="btnConfirmar"/>
						<h:commandButton  value="Voltar" action="#{componenteCurricular.listar}" id="btVoltar"/> 
						<h:commandButton value="Cancelar" action="#{expressaoComponenteCurriculoBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancel"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>