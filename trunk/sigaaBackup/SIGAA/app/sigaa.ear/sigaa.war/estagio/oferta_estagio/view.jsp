<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> &gt; Oferta de Estágio</h2>
<a4j:keepAlive beanName="ofertaEstagioMBean" />

<style>
	.alignLeft {
		text-align: left !important;
	} 
</style>

<c:set var="oferta" value="#{ofertaEstagioMBean.obj}"/>
<%@include file="include/_oferta.jsp"%>
<table class="visualizacao" style="width: 90%">
	<tfoot>
		<tr>
			<td colspan="4">
				<h:form id="form">												
					<h:commandButton value="Cadastrar" action="#{ofertaEstagioMBean.cadastrar}" id="btCadastrar" 
						rendered="#{ofertaEstagioMBean.obj.id == 0 && ofertaEstagioMBean.cadastro}" />
					
					<h:commandButton value="Alterar" action="#{ofertaEstagioMBean.cadastrar}" id="btAlterar" rendered="#{ofertaEstagioMBean.obj.id > 0 && ofertaEstagioMBean.cadastro}"/>
					
					<h:commandButton value="<< Voltar" action="#{ofertaEstagioMBean.telaForm}" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{ofertaEstagioMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
				</h:form>
			</td>
		</tr>
	</tfoot>
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>