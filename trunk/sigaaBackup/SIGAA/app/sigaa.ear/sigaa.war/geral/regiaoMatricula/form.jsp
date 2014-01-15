<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>

<h2><ufrn:subSistema /> &gt; Cadastro de Regi�o de Campus para Matr�cula</h2>

<h:form id="form">	

	<div class="descricaoOperacao">
		<p>Esta opera��o tem como finalidade cadastrar regi�es de <i>Campus de Institui��o de Ensino Superior</i>,
		possibilitando aos discentes realizarem matr�culas em um campus diferente ao de origem, quando o Campus da 
		matr�cula pertencer a mesma regi�o de matr�cula do discente.</p>
	</div>

	<h:inputHidden value="#{regiaoMatriculaBean.confirmButton}"/>
	<h:inputHidden value="#{regiaoMatriculaBean.obj.id}"/>

	<table class="formulario" width="80%">
		<caption>Dados da Regi�o de Campus para Matr�cula</caption>

		<tr>
			<th class="required"> Nome: </th>
			<td> <h:inputText value="#{regiaoMatriculaBean.obj.nome}" id="nome" size="50" maxlength="190" readonly="#{regiaoMatriculaBean.readOnly}"/></td>
		</tr>
		
		<tr>
		<c:choose>
			<c:when test="${ regiaoMatriculaBean.obj.nivel != null }">
				<th><b>N�vel de Ensino:</b></th>
				<td>${ regiaoMatriculaBean.nivelDescricao }</td>
			</c:when>
			<c:otherwise>
				<th class="required"> N�vel de Ensino: </th>
				<td>
					<h:selectOneMenu id="nivel" value="#{regiaoMatriculaBean.obj.nivel}">
						<f:selectItems value="#{nivelEnsino.allCombo}" />
					</h:selectOneMenu> 
				</td>
			</c:otherwise>
		</c:choose>	
		</tr>
	
		<tr>
			<th valign="top" style="padding-top: 3px" class="required"> Campus: </th>
			<td> 
				<t:dataTable var="campusLoop" value="#{ regiaoMatriculaBean.campus }" width="100%" style="font-size: 0.9em;">				
					<t:column width="4%">
						<h:selectBooleanCheckbox id="selecionaCampus" value="#{ campusLoop.selecionado }"/>
					</t:column>
					
					<t:column width="96%">
						<h:outputLabel for="selecionaCampus" value="#{ campusLoop.nome }"/>
					</t:column>
				</t:dataTable>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="#{regiaoMatriculaBean.confirmButton}" action="#{regiaoMatriculaBean.cadastrar}"/>
					<c:choose>
						<c:when test="${regiaoMatriculaBean.obj.id > 0}">
							<h:commandButton value="Cancelar" action="#{regiaoMatriculaBean.listar}" immediate="true" id="btnCancelListar"/>
						</c:when>
						<c:otherwise>
							<h:commandButton value="Cancelar" action="#{regiaoMatriculaBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancel"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</tfoot>
	</table>	
</h:form>
<br>
<div align="center">
	<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
