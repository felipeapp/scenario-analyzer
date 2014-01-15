<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<h2>  <ufrn:subSistema /> > Confirma Remo��o Tipo do Material </h2>

<div class="descricaoOperacao"> 
    <p>Caro Usu�rio, confirme a remo��o do Tipo do Material, escolhendo o tipo para onde os materiais que possuem o tipo que est� sendo removido ser�o migrados. </p>
</div>

<f:view>

	<a4j:keepAlive beanName="tipoMaterialMBean" />
	
	<h:form id="formConfirmaRemovaoTipomaterial">
	
		<table class="formulario" width="70%">
			<caption>Confirme a Remo��o do Tipo do Material</caption>
		
			<tbody>
		
				<tr>
					<th>Descri��o:</th>
					<td>${tipoMaterialMBean.obj.descricao}</td>
		
				</tr>
				
				<tr>
					<th class="required">Novo Tipo para os Materiais:</th>
					<td colspan="5">
						<h:selectOneMenu id="comboTipoMaterial" value="#{tipoMaterialMBean.novoTipo.id}" >
							<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
							<f:selectItems value="#{tipoMaterialMBean.tiposAtivos}"/>
						</h:selectOneMenu>
					</td>
				</tr>

			</tbody>
			<tfoot>
			
				<tr>
					<td colspan="6">
			
						<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">
							<h:commandButton id="cmdBottonRemoveStatus"  value="Remover Tipo do Material" action="#{tipoMaterialMBean.remover}" />
						</ufrn:checkRole>
						
						<h:commandButton id="cmdBottonCancelar" value="Cancelar" action="#{tipoMaterialMBean.listar}" immediate="true"  onclick="#{confirm}"/>
			
					</td>
				</tr>
			
			</tfoot>
		</table>

	</h:form>


	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>