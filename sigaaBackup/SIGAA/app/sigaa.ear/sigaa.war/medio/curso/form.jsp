<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoMedio"/>
<h2> <ufrn:subSistema /> &gt; Cadastro de Curso </h2>
	
<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
		<h:inputHidden value="#{cursoMedio.obj.id}" />
		<tbody>
			<tr>
				<th>Código no INEP:</th>
				<td><h:inputText value="#{cursoMedio.obj.codigoINEP}" size="10" maxlength="20" onkeyup="CAPS(this)"/>(Código do Curso cadastrado no INEP/SETEC)</td>
			</tr>
			<tr>
				<th class="obrigatorio">Código na ${ configSistema['siglaInstituicao'] }:</th>
				<td><h:inputText value="#{cursoMedio.obj.codigo}" size="10" maxlength="20" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText value="#{cursoMedio.obj.nome}" size="100" maxlength="180" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Data de Início do Funcionamento: </th>
				<td><t:inputCalendar value="#{cursoMedio.obj.dataInicio}"  
					title="Data de Inicio do Funcionamento" size="10" maxlength="10" 
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
					id="dataInicioFuncionamento" renderAsPopup="true" 
					renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
					<f:converter converterId="convertData"/>
				</t:inputCalendar></td>
			</tr>
			<tr>
				<th class="obrigatorio">Modalidade: </th>
				<td>
					<h:selectOneMenu value="#{cursoMedio.obj.modalidadeCursoMedio.id}" id="modalidade">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{modalidadeCursoMedio.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
			 	<th>Regime Letivo: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoMedio.obj.tipoRegimeLetivo.id}" id="tipoRegimeLetivo">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{tipoRegimeLetivo.allCombo}" /> 
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
			 	<th>Sistema Curricular: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoMedio.obj.tipoSistemaCurricular.id}" id="sistemaCurricular">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{tipoSistemaCurricular.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>

			<tr>
			 	<th>Situação do Curso: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoMedio.obj.situacaoCursoHabil.id}" id="situacaoCurso">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{situacaoCursoHabil.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
			 	<th>Situação do Diploma:</th>
			 	<td>
					<h:selectOneMenu value="#{cursoMedio.obj.situacaoDiploma.id}" id="situacaoDiploma">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{situacaoDiploma.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
				<th class="obrigatorio">Turno: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoMedio.obj.turno.id}" id="turno">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{turno.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
				<th class="required">Forma de Participação do Aluno:</th>
				<td>
					<h:selectOneMenu id="modalidadeEducacao" value="#{cursoMedio.obj.modalidadeEducacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th> Ativo: </th>
				<td>
					<h:selectBooleanCheckbox id="checkAtivo" value="#{cursoMedio.obj.ativo}" disabled="#{cursoMedio.obj.id == 0}" styleClass="noborder" /> 
				</td>
			<tr>
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="#{cursoMedio.confirmButton}" action="#{cursoMedio.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{cursoMedio.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>