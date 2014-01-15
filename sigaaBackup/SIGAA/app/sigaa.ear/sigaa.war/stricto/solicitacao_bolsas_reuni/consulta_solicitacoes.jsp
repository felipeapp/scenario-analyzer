<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Consulta Solicitações Bolsa Reuni </h2>
	
	<h:form prependId="false">
		
		<table class="formulario" width="45%">
			<caption class="formulario">Dados da Busca</caption>
			
			<tbody>			
					<tr>
						<td class="required" style="width: 50px;">Edital:</td>
						<td>
							<h:selectOneMenu value="#{solicitacaoBolsasReuniBean.edital}" style="width: 350px;" id="idEdital">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
								<f:selectItems value="#{editalBolsasReuniBean.allCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Consultar" action="#{solicitacaoBolsasReuniBean.buscar}" id="buscar" />
						<h:commandButton value="Cancelar"  action="#{solicitacaoBolsasReuniBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	<br />
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>