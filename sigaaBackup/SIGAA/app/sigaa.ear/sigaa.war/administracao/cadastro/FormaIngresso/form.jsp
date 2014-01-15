<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<f:view>


<h2> <ufrn:subSistema /> > Forma de Ingresso</h2>




<h:form>
<div class="infoAltRem" style="text-align: center; width: 100%">
	<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<h:commandLink value="Listar" action="#{formaIngresso.listar}"/>
</div>
</h:form>

<h:form id="form">
<table class="formulario">
		<caption>Cadastro de Forma de Ingresso</caption>
		<h:inputHidden value="#{formaIngresso.confirmButton}" />
		<h:inputHidden value="#{formaIngresso.obj.id}" />
		<tbody>
			<tr>
				<th class="required">Descrição:</th>
				<td>
					<h:inputText readonly="#{formaIngresso.readOnly}" size="60" maxlength="80" value="#{formaIngresso.obj.descricao}" />
				</td>
			</tr>
			<tr>
				<th  class="required">Nível:</th>
				<td>
					<h:selectOneMenu value="#{formaIngresso.obj.nivel}" id="nivel" disabled="#{formaIngresso.readOnly}">
					<f:selectItem itemValue="0" itemLabel="TODOS"/>
					<f:selectItems value="#{nivelEnsino.allCombo}"/>
				</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th  class="required">Tipo:</th>
				<td>
					<a4j:region>
						<h:selectOneMenu value="#{formaIngresso.obj.tipo}" id="tipo" onchange="submit()">
							<f:selectItem itemValue="0" itemLabel="TODOS"/>
							<f:selectItem itemValue="E" itemLabel="Aluno Especial"/>
							<f:selectItem itemValue="R" itemLabel="Aluno Regular"/>
							<a4j:support event="onchange" reRender="categoria" />										
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start"><h:graphicImage value="/img/indicator.gif"/></f:facet>
						</a4j:status>
					</a4j:region>
				</td>
			</tr>
			<tr >
				<c:if test="${formaIngresso.especial }">
					<th  class="required">Categoria de Discente Especial:</th>
					<td>
						<h:selectOneMenu value="#{formaIngresso.obj.categoriaDiscenteEspecial.id }"  id="categoria" style="width: 200px">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />						
							<f:selectItems value="#{ categoriaDiscenteEspecial.allCombo}" />
						</h:selectOneMenu>
					</td>
				</c:if>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{formaIngresso.obj.diplomaConvenio}" disabled="#{formaIngresso.readOnly}"/></th>
				<td>Utiliza o modelo de diploma de Convênio</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{formaIngresso.obj.realizaProcessoSeletivo}" disabled="#{formaIngresso.readOnly}"/></th>
				<td>Realiza Processo Seletivo para a entrada dos discentes</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{formaIngresso.obj.tipoMobilidadeEstudantil}" disabled="#{formaIngresso.readOnly}" /></th>
				<td>Mobilidade Estudantil</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{formaIngresso.obj.ativo}" disabled="#{formaIngresso.readOnly}" /></th>
				<td>Ativo</td>
			</tr>
		</tbody>	
		<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{formaIngresso.confirmButton}" action="#{formaIngresso.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{formaIngresso.cancelar}" />
				</td>
			</tr>
		</tfoot>
</table>
</h:form>

<br />
<center><h:graphicImage url="/img/required.gif"/> 
	<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>