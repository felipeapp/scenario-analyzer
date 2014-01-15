<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="disciplinaMedioMBean"></a4j:keepAlive>
<style>
<!--
table.visualizacao td {text-align: left;}
table.visualizacao th {font-weight: bold;}
-->
</style>
<f:view>
 <h2 class="title"><ufrn:subSistema/> &gt; Dados da Disciplina</h2>
<h:form id="form">
	<table class="visualizacao" style="width:100%">
		<caption>Dados Gerais da Disciplina</caption>
		<tr>
			<th>Tipo:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.tipoComponente.descricao}" />
		</tr>
		<tr>
			<th>Nível:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.descricaoNivelEnsino}" />
		</tr>		
		<tr>
			<th>Unidade:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.unidade.nome}" />
		</tr>		
		<tr>
			<th width="30%">Código:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.codigo}" /></td>
		</tr>
		<tr>
			<th>Nome:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.detalhes.nome}" /></td>
		</tr>
		<tr>
			<th>Carga Horária Teórica:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.detalhes.chAula}" /> h</td>
		</tr>
		<tr>
			<th>Carga Horária Prática:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.detalhes.chLaboratorio}" /> h</td>
		</tr>
		<tr>
			<th>Carga Horária Total:</th>
			<td>
			<h:outputText value="#{disciplinaMedioMBean.obj.chTotal}" /> h</td>
		</tr>
		<tr>
			<th>Carga Horária Dedicada do Docente:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.detalhes.chDedicadaDocente}" /> h</td>
		</tr>
		<tr>
			<th>Horário Flexível da Turma:</th>
			<td><ufrn:format type="bool_sn" valor="${disciplinaMedioMBean.obj.permiteHorarioFlexivel}" /></td>
		</tr>
		<tr>
			<th>Horário Flexível do Docente:</th>
			<td><ufrn:format type="bool_sn" valor="${disciplinaMedioMBean.obj.permiteHorarioDocenteFlexivel}" /></td>
		</tr>
		<tr>
			<th>Permite Ch Compartilhada:</th>
			<td><ufrn:format type="bool_sn" valor="${disciplinaMedioMBean.obj.detalhes.permiteChCompartilhada}" /></td>
		</tr>		
		<tr>
			<th valign="top">Ementa:</th>
			<td><h:outputText value="#{disciplinaMedioMBean.obj.detalhes.ementa}" /></td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center">
					<h:commandButton id="cadastrar" value="#{disciplinaMedioMBean.confirmButton}" 
						action="#{disciplinaMedioMBean.cadastrar}" rendered="#{disciplinaMedioMBean.exibeBotaoCadastrar}"/>

					<h:commandButton id="voltar" value="<< Voltar" action="#{disciplinaMedioMBean.listar}" 
						rendered="#{!disciplinaMedioMBean.exibeBotaoCadastrar}"/>

					<h:commandButton id="voltarCadastro" value="<< Dados Gerais" action="#{disciplinaMedioMBean.telaCadastro}" 
						rendered="#{disciplinaMedioMBean.exibeBotaoCadastrar}"/>						

					<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{disciplinaMedioMBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
