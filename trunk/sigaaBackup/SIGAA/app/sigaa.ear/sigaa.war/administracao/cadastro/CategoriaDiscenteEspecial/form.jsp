<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> <ufrn:subSistema /> > Categoria de Discente Especial</h2>




<h:form>
<div class="infoAltRem" style="text-align: center; width: 100%">
	<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
			<h:commandLink value="Listar" action="#{categoriaDiscenteEspecial.listar}"/>
</div>
</h:form>

<h:form id="form">
<table class="formulario">
		<caption>Cadastro de Categoria de Discente Especial</caption>
		<h:inputHidden value="#{categoriaDiscenteEspecial.confirmButton}" />
		<h:inputHidden value="#{categoriaDiscenteEspecial.obj.id}" />
		<tr>
			<th class="required">Descrição:</th>
			<td>
				<h:inputText readonly="#{categoriaDiscenteEspecial.readOnly}" size="60" maxlength="100" value="#{categoriaDiscenteEspecial.obj.descricao}" />
			</td>
		</tr>
		<tr>
			<th class="required">Número Máximo de Disciplinas:</th>
			<td>
				<h:inputText readonly="#{categoriaDiscenteEspecial.readOnly}" size="5" maxlength="2" value="#{categoriaDiscenteEspecial.obj.maxDisciplinas}" 
				onkeyup="return formatarInteiro(this);"  id="max_disciplinas" title="Número Máximo de Disciplinas" />
			</td>
		</tr>
		<tr>
			<th  class="obrigatorio" >Número Máximo de Períodos:</th>
			<td>
				<h:inputText readonly="#{categoriaDiscenteEspecial.readOnly}" size="5" maxlength="2" value="#{categoriaDiscenteEspecial.obj.maxPeriodos}"
				onkeyup="return formatarInteiro(this);"  id="max_periodos" title="Número Máximo de Períodos"/>
			</td>
		</tr>
		<tr>
			<th><h:selectBooleanCheckbox value="#{categoriaDiscenteEspecial.obj.permiteRematricula}" disabled="#{categoriaDiscenteEspecial.readOnly}"/></th>
			<td>Permitido realizar solicitação de re-matrícula</td>
		</tr>
		<tr>
			<th><h:selectBooleanCheckbox value="#{categoriaDiscenteEspecial.obj.processaMatricula}" disabled="#{categoriaDiscenteEspecial.readOnly}"/></th>
			<td>Participa no processamento de matrícula</td>
		</tr>
		<tr>
			<th><h:selectBooleanCheckbox value="#{categoriaDiscenteEspecial.obj.processaRematricula}" disabled="#{categoriaDiscenteEspecial.readOnly}"/></th>
			<td>Participa no processamento de re-matrícula</td>
		</tr>
		<tr>
			<th><h:selectBooleanCheckbox value="#{categoriaDiscenteEspecial.obj.chefeAnalisaSolicitacao}" disabled="#{categoriaDiscenteEspecial.readOnly}"/></th>
			<td>Chefe de departamento poderá analisar as solicitações de matrícula</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan=2>
					<h:commandButton value="#{categoriaDiscenteEspecial.confirmButton}" action="#{categoriaDiscenteEspecial.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{categoriaDiscenteEspecial.cancelar}" />
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