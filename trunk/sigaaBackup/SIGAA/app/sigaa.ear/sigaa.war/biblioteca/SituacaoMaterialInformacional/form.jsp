<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> > Situa��o do Material Informacional</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${situacaoMaterialInformacionalMBean.obj.id == 0}">
   		<p> Entre com as informa��es da nova Situa��o e se ele aceitar� que seus materiais sejam vis�veis para os usu�rios finais das bibliotecas.</p>
   	</c:if>
   	<c:if test="${situacaoMaterialInformacionalMBean.obj.id > 0}">
   		<p> Edite as informa��es da Situa��o selecionada.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form id="formCadatraEditaSituacao">
	
		<a4j:keepAlive  beanName="situacaoMaterialInformacionalMBean" />

		<table class="formulario" width="70%">
			<caption>Situa��o do Material</caption>
			
			<tr>
				<th class="obrigatorio">Descri��o:</th>
				<td><h:inputText value="#{situacaoMaterialInformacionalMBean.obj.descricao}"  maxlength="200" size="40"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Vis�vel ?</th>
				<td>
					<h:selectOneRadio  id="selectOneRadioStatusPermiteEmprestimo" value="#{situacaoMaterialInformacionalMBean.obj.visivelPeloUsuario}" >
							<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="N�O" itemValue="false" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{situacaoMaterialInformacionalMBean.confirmButton}"  action="#{situacaoMaterialInformacionalMBean.cadastrar}" id="acao"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{situacaoMaterialInformacionalMBean.listar}" immediate="true" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>