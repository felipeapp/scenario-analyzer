<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cursoTecnicoMBean"/>
<h2> <ufrn:subSistema /> &gt; Cadastro de Curso</h2>
	
<h:form id="form">
	<table class="formulario" style="width: 90%">
	  <caption>Dados do Curso</caption>
		<h:inputHidden value="#{cursoTecnicoMBean.obj.id}" />
		<tbody>
			
			<tr>
				<th class="obrigatorio">Código na ${ configSistema['siglaInstituicao'] }:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.codigo}" size="10" maxlength="20"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.nome}" size="70" maxlength="70"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Data de Início do Funcionamento: </th>
				<td><t:inputCalendar value="#{cursoTecnicoMBean.obj.dataInicioFuncionamento}"  
					title="Data de Inicio do Funcionamento" size="10" maxlength="10" 
					onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
					id="dataInicioFuncionamento" renderAsPopup="true" 
					renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" >
					<f:converter converterId="convertData"/>
				</t:inputCalendar></td>
			</tr>
			<tr>
				<th class="obrigatorio">Carga Horária Mínima:</th>
				<td><h:inputText value="#{cursoTecnicoMBean.obj.chMinima}" maxlength="5" 
					size="6" onkeyup="formatarInteiro(this);"/>(em horas)</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Turno: </th>
			 	<td>
					<h:selectOneMenu value="#{cursoTecnicoMBean.obj.turno.id}" id="turno">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
						<f:selectItems value="#{turno.allCombo}" />
					</h:selectOneMenu>
			 	</td>
			</tr>
			<tr>
				<th class="required">Forma de Participação do Aluno:</th>
				<td>
					<h:selectOneMenu id="modalidade" value="#{cursoTecnicoMBean.obj.modalidadeEducacao.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="#{cursoTecnicoMBean.confirmButton}" action="#{cursoTecnicoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{cursoTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>