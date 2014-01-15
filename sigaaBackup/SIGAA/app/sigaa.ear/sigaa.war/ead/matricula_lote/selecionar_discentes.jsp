<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Procurar Alunos</h2>

<f:view>
	<h:form id="busca">
		<table class="formulario" id="formulario" width="100%">
			<caption>Selecionar Alunos</caption>
			<tbody>
				<tr>
					<th class="required">Curso:</th>
					<td>
						<h:selectOneMenu id="curso" value="#{loteMatriculas.obj.curso.id}">
							<f:selectItems value="#{curso.allCursosGraduacaoEADCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Status:</th>
					<td>
						<h:selectOneMenu id="status" value="#{loteMatriculas.status.id}">
							<f:selectItems value="#{statusDiscente.allAtivosEad}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th class="required">Ano-Semestre (Entrada):</th>
					<td><h:inputText value="#{loteMatriculas.anoEntrada}" size="4" maxlength="4" id="anoEntrada" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/> - 
					<h:inputText value="#{loteMatriculas.periodoEntrada}" size="1" maxlength="1" id="periodoEntrada" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar" action="#{ loteMatriculas.buscarDiscentes }"/>
						<h:commandButton value="Cancelar" action="#{ loteMatriculas.cancelar }" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br/>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>