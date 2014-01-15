<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Alterar Dados de Ingresso</h2>

	<h:form id="formulario">
	
	<h:inputHidden value="#{alterarFormaIngresso.obj.id}" />
	
	<table class="visualizacao" >
		<tr>
			<th width="20%"> Matrícula: </th>
			<td> ${alterarFormaIngresso.obj.matricula } </td>
		</tr>
		<tr>
			<th> Discente: </th>
			<td> ${alterarFormaIngresso.obj.pessoa.nome } </td>
		</tr>
		<tr>
			<th> Curso: </th>
			<td> ${alterarFormaIngresso.obj.curso.descricao } </td>
		</tr>
		<tr>
			<th> Status: </th>
			<td> ${alterarFormaIngresso.obj.statusString } </td>
		</tr>
		<tr>
			<th> Forma de Ingresso Atual: </th>
			<td> ${alterarFormaIngresso.obj.formaIngresso.descricao} </td>
		</tr>
		<tr>
			<th> Período de Ingresso Atual: </th>
			<td> ${alterarFormaIngresso.obj.anoPeriodoIngresso} </td>
		</tr>
	</table>
	<br />	
	
	<table class="formulario" width="650px">
			<caption class="listagem">Selecione os Novos Dados de Ingresso Para Este Aluno</caption>
			<tr>
				<th class="obrigatorio">Forma de Ingresso:</th>
				<td><h:selectOneMenu id="formaIngresso" value="#{alterarFormaIngresso.formaIngresso.id}">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{alterarFormaIngresso.formasIngressoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="obrigatorio">Período de Ingresso:</th>
				<td><h:selectOneMenu id="anoPeriodoIngresso" value="#{alterarFormaIngresso.periodoIngresso}">
					<f:selectItem itemValue="1" itemLabel="1º Semestre" />
					<f:selectItem itemValue="2" itemLabel="2º Semestre" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="obrigatorio">Cola Grau:</th>
				<td>
					<h:selectBooleanCheckbox id="colaGrau" value="#{alterarFormaIngresso.obj.colaGrau}" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Alterar Dados de Ingresso" action="#{alterarFormaIngresso.chamaModelo}" id="btnChama"/> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alterarFormaIngresso.cancelar}" id="btnCancelar"/>
					</td>
				</tr>
			</tfoot>
	</table>
	</h:form>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
