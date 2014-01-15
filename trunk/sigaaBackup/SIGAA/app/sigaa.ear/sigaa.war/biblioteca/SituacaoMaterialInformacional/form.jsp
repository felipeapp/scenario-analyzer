<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>


<h2>  <ufrn:subSistema /> > Situação do Material Informacional</h2>

<div class="descricaoOperacao"> 
   	<c:if test="${situacaoMaterialInformacionalMBean.obj.id == 0}">
   		<p> Entre com as informações da nova Situação e se ele aceitará que seus materiais sejam visíveis para os usuários finais das bibliotecas.</p>
   	</c:if>
   	<c:if test="${situacaoMaterialInformacionalMBean.obj.id > 0}">
   		<p> Edite as informações da Situação selecionada.</p>
   	</c:if>
</div>	

<f:view>
	
	<br>
	<h:form id="formCadatraEditaSituacao">
	
		<a4j:keepAlive  beanName="situacaoMaterialInformacionalMBean" />

		<table class="formulario" width="70%">
			<caption>Situação do Material</caption>
			
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText value="#{situacaoMaterialInformacionalMBean.obj.descricao}"  maxlength="200" size="40"/></td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Visível ?</th>
				<td>
					<h:selectOneRadio  id="selectOneRadioStatusPermiteEmprestimo" value="#{situacaoMaterialInformacionalMBean.obj.visivelPeloUsuario}" >
							<f:selectItem itemLabel="SIM" itemValue="true" />
							<f:selectItem itemLabel="NÃO" itemValue="false" />
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